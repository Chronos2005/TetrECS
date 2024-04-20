package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.event.CommunicationsListener;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

public class LobbyScene extends BaseScene{

    /**
     * Logger to help with debugging
     */
    private static final Logger logger = LogManager.getLogger(LobbyScene.class);
    /**
     * Timer to request current channels at fixed rates
     */
    private Timer timer;

    /**
     * VBox to hold the channels
     */
    private VBox channelBox = new VBox();
    /**
     * BorderPane to hold the lobby
     */
    private BorderPane borderPane = new BorderPane();
    private TextFlow textFlow;
    private ScrollPane scrollPane;
    private boolean scrollToBottom;


    private TextField messageToSend;

    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public LobbyScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Lobby Scene");
    }

    /**
    * Initialises the LobbyScene
    */
    @Override
    public void initialise() {
        logger.info("Initialising the lobby scene");

    }

    /**
    * Builds the lobby window
    */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());
        requestingChannelsTimer();
        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());
        StackPane lobbyPane = new StackPane();
        lobbyPane.getStyleClass().add("menu-background");
        Label multiplayerLabel = new Label("Multiplayer");
        Label currentGamesLabel = new Label("Current Games");
        Button hostGamesLabel = new Button("Host new Game");
        multiplayerLabel.getStyleClass().add("bigtitle");
        currentGamesLabel.getStyleClass().add("title");
        hostGamesLabel.getStyleClass().add("title");

        VBox vBox = new VBox();
        vBox.getChildren().addAll(currentGamesLabel,hostGamesLabel,channelBox);


        borderPane.setTop(multiplayerLabel);
        borderPane.setLeft(vBox);
        BorderPane.setAlignment(multiplayerLabel,Pos.CENTER);
        lobbyPane.getChildren().add(borderPane);
        root.getChildren().add(lobbyPane);

        gameWindow.getCommunicator().addListener(new CommunicationsListener() {

            @Override
            public void receiveCommunication(String communication) {
                Platform.runLater(() -> {
                    addCurrentChannels(communication);
                    joinChannel(communication);
                    receiveMessage(communication);
                });



            }
        });
        // Add action to the hostGamesLabel
        hostGamesLabel.setOnAction(e -> startNewChannel());


    }

    /**
     * Schedules a timer to request current channels at fixed rates
     */
    public void requestingChannelsTimer(){
        logger.info("Scheduling timer to request current channels at fixed rates");
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                gameWindow.getCommunicator().send("LIST");

            }
        };
        timer.scheduleAtFixedRate(task,0,5000);
    }

    /**
     * Adds the current channels to the channelBox
     * @param communication the communication received
     */
    public void addCurrentChannels(String communication){
        if (communication.startsWith("CHANNELS")){
            channelBox.getChildren().clear();
            if(communication.trim().substring(communication.indexOf(" ")+1).length()>0){
                communication = communication.trim().substring(communication.indexOf(" ")+1);
                logger.info("Current channels: "+communication);
                String[] channels = communication.split("\n");
                for (String channel: channels){
                    logger.info("Channel: "+channel);
                    Button button = new Button(channel);
                    button.setOnAction(e -> joinChannel("JOIN "+channel));
                    channelBox.getChildren().add(button);
                }
            }

        }

    }

    /**
     * Starts a new channel
     */

    private void startNewChannel(){

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Channel");
        dialog.setHeaderText("Enter a name for the new channel:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(channelName -> {
            // Send the channel name to the server
            gameWindow.getCommunicator().send("CREATE " + channelName);
            HBox.setHgrow(messageToSend, Priority.ALWAYS);
        });

    }

    /**
     * Joins a channel
     * @param communication the communication received
     */
    private void joinChannel(String communication){
        if(communication.startsWith("JOIN")){
            String channelName = communication.trim().substring(communication.indexOf(" ")+1);
            gameWindow.getCommunicator().send("JOIN " + channelName);
            borderPane.setRight(createChannelUI(channelName));
            logger.info("Joining channel: "+channelName);
        }

    }

    /**
     * Creates the channel UI
     * @param channelName the name of the channel
     */
    private BorderPane createChannelUI(String channelName){
        /*
        Label channelLabel = new Label(channelName);
        Label welcomeMessage = new Label("Welcome to the Lobby");
        Label nickNameLabel = new Label("Type /nick NewNAme to change your name");
        channelLabel.getStyleClass().add("title"); // Add a style class to the label
        VBox vBox = new VBox();


        // Create the HBox for the bottom region
        HBox hBox = new HBox();
        //Text field is the message which get sent
        messageToSend = new TextField();
        Button button = new Button("Send");
        hBox.getChildren().addAll(messageToSend, button);
        channelBox.getChildren().addAll(channelLabel, hBox);

        return channelBox;

         */
        //Setup scene with a border pane
        var pane = new BorderPane();

        scrollPane = new ScrollPane();


        //Text flow holds the messages which ave already been sent
         textFlow = new TextFlow();
        scrollPane.setContent(textFlow);
        scrollPane.setFitToWidth(true);
        pane.setStyle("-fx-background-color: transparent;");


        // Create the HBox for the bottom region
        HBox hBox = new HBox();
        //Text field is the message which get sent
        messageToSend = new TextField();
        Button button = new Button("Send");
        button.setOnAction((event)-> sendCurrentMessage(messageToSend.getText()));
        hBox.getChildren().addAll(messageToSend, button);
        pane.setBottom(hBox);
        messageToSend.setOnKeyPressed((event) -> {
            if (event.getCode() != KeyCode.ENTER) return;
            sendCurrentMessage(messageToSend.getText());
        });


        //TODO: Set up the GUI to be more useful than this...
        pane.setCenter(scrollPane);
        HBox.setHgrow(messageToSend,Priority.ALWAYS);
        // Create the buttons
        Button startGameButton = new Button("Start Game");
        Button leaveChannelButton = new Button("Leave Channel");

// Add action listeners
        startGameButton.setOnAction(e -> gameWindow.getCommunicator().send("START"));
        leaveChannelButton.setOnAction(e -> gameWindow.getCommunicator().send("PART"));

// Add the buttons to the UI
        HBox buttonBox = new HBox(startGameButton, leaveChannelButton);
        pane.setBottom(buttonBox);
        return pane;


    }

    /**
     * Sends the current message
     * @param text the text to send
     */
    private void sendCurrentMessage(String text) {
        gameWindow.getCommunicator().send("MSG " + text);
        messageToSend.clear();
    }
    /**
     * Receives the message
     * @param message the message to receive
     */
    public void receiveMessage(String message) {
        if (message.startsWith("MSG")) {
            message = message.trim().substring(message.indexOf(" ")+1);
            // Create a new Text object with the received message
            Text receivedMessageText = new Text(message+ "\n");



            // Add the new Text object to the TextFlow
            textFlow.getChildren().add(receivedMessageText);
            if(scrollPane.getVvalue()==0.0f|| scrollPane.getVvalue()>0.9f){
                scrollToBottom=true;
            }
        }

    }
}
