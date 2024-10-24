package uk.ac.soton.comp1206.scene;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.Multimedia;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The main menu of the game. Provides a gateway to the rest of the game.
 */
public class MenuScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    private Multimedia multimedia;

    /**
     * Create a new menu scene
     * @param gameWindow the Game Window this will be displayed in
     */
    public MenuScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Menu Scene");
    }

    /**
     * Build the menu layout
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var menuPane = new StackPane();
        menuPane.setMaxWidth(gameWindow.getWidth());
        menuPane.setMaxHeight(gameWindow.getHeight());
        menuPane.getStyleClass().add("menu-background");

        // Fade-in animation for the menu items
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), menuPane);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();

        root.getChildren().add(menuPane);

        var mainPane = new BorderPane();
        menuPane.getChildren().add(mainPane);

        //Awful title
        var title = new Text("TetrECS");
        title.getStyleClass().add("title");
        mainPane.setTop(title);
        // Center align the title
        BorderPane.setAlignment(title, Pos.CENTER);

        // TranslateTransition animation for the title
        TranslateTransition titleTransition = new TranslateTransition(Duration.seconds(1), title);
        titleTransition.setFromY(50); // Start the title 50 pixels above its original position
        titleTransition.setToY(0); // Move the title to its original position
        titleTransition.setInterpolator(Interpolator.EASE_OUT); // Apply easing function
        titleTransition.setCycleCount(Animation.INDEFINITE); // Loop indefinitely
        titleTransition.setAutoReverse(true); // Reverse the animation

        titleTransition.play(); // Start the animation

        //For now, let us just add a button that starts the game. I'm sure you'll do something way better.
        var singlePlayerButton = new Button("Single Player");
        var multiPlayerButton = new Button("Multi Player");
        var instructionsButton = new Button("How to Play");
        var extButton = new Button(("Exit"));
        VBox menu = new VBox();
        menu.setAlignment(Pos.CENTER);
        singlePlayerButton.getStyleClass().add("menuItem");
        multiPlayerButton.getStyleClass().add("menuItem");
        instructionsButton.getStyleClass().add("menuItem");
        extButton.getStyleClass().add("menuItem");

        // Hover effects for buttons
        singlePlayerButton.setOnMouseEntered(e -> singlePlayerButton.setEffect(new DropShadow()));
        singlePlayerButton.setOnMouseExited(e -> singlePlayerButton.setEffect(null));
        multiPlayerButton.setOnMouseEntered(e -> multiPlayerButton.setEffect(new DropShadow()));
        multiPlayerButton.setOnMouseExited(e -> multiPlayerButton.setEffect(null));
        instructionsButton.setOnMouseEntered(e -> instructionsButton.setEffect(new DropShadow()));
        instructionsButton.setOnMouseExited(e -> instructionsButton.setEffect(null));
        extButton.setOnMouseEntered(e -> extButton.setEffect(new DropShadow()));
        extButton.setOnMouseExited(e -> extButton.setEffect(null));

        menu.getChildren().addAll(singlePlayerButton,multiPlayerButton,instructionsButton,extButton);
        menu.getStyleClass().add("menu");

        mainPane.setCenter(menu);
        multimedia = new Multimedia();
        multimedia.playMusic("menu.mp3");


        //Bind the button action to the startGame method in the menu
        singlePlayerButton.setOnAction(this::startGame);
        instructionsButton.setOnAction(this::openInstructions);
        multiPlayerButton.setOnAction(this::openLobby);
        extButton.setOnAction((event -> {gameWindow.closeGame();}));
    }

    /**
     * Initialise the menu
     */
    @Override
    public void initialise() {


    }

    /**
     * Handle when the Start Game button is pressed
     * @param event event
     */
    private void startGame(ActionEvent event) {
        gameWindow.startChallenge();
        multimedia.stopMusic();
    }

    /**
     * Handle when the How to play button is pressed
     * @param event event
     */
    private void openInstructions(ActionEvent event){
        logger.info("Attempting to open the instructions page");
        multimedia.stopMusic();
        gameWindow.startInstruction();
    }

    /**
     * Handle when the Multiplayer button is pressed
     * @param event multiplayer button pressed
     */
    private void openLobby(ActionEvent event){
        logger.info("Attempting to open the multiplayer Lobby");
        multimedia.stopMusic();
        gameWindow.startMultiplayerLobby();
    }

}
