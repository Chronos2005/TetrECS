package uk.ac.soton.comp1206.scene;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.event.CommunicationsListener;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.util.Timer;
import java.util.TimerTask;

public class LobbyScene extends BaseScene{

    private static final Logger logger = LogManager.getLogger(LobbyScene.class);
    private Timer timer;

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
        vBox.getChildren().addAll(currentGamesLabel,hostGamesLabel);

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(multiplayerLabel);
        borderPane.setLeft(vBox);
        BorderPane.setAlignment(multiplayerLabel,Pos.CENTER);
        lobbyPane.getChildren().add(borderPane);
        root.getChildren().add(lobbyPane);

        gameWindow.getCommunicator().addListener(new CommunicationsListener() {

            @Override
            public void receiveCommunication(String communication) {


            }
        });


    }

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
}
