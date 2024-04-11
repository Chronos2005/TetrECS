package uk.ac.soton.comp1206.scene;

import javafx.beans.binding.Bindings;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.Multimedia;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.event.NextPieceListener;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class ChallengeScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    protected Game game;
    private Multimedia multimedia;

    /**
     * Create a new Single Player challenge scene
     * @param gameWindow the Game Window
     */
    public ChallengeScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Challenge Scene");
    }

    /**
     * Build the Challenge window
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        setupGame();

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());


        var challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("menu-background");
        root.getChildren().add(challengePane);

        var mainPane = new BorderPane();
        challengePane.getChildren().add(mainPane);
        Grid grid = new Grid(3,3);
        Grid grid2 = new Grid(3,3);

        var board = new GameBoard(game.getGrid(),gameWindow.getWidth()/2,gameWindow.getWidth()/2);
        PieceBoard currentPieceBoard= new PieceBoard(grid,gameWindow.getWidth()/5,gameWindow.getHeight()/5);
        PieceBoard followingPieceBoard= new PieceBoard(grid2,gameWindow.getWidth()/5,gameWindow.getHeight()/5);
        mainPane.setCenter(board);
        VBox menuPane = new VBox();

        Label scoreLabel = new Label();
        Label livesLabel = new Label();
        Label multiplier = new Label();
        Label levelLabel = new Label();
        scoreLabel.getStyleClass().add("score");
        livesLabel.getStyleClass().add("lives");
        levelLabel.getStyleClass().add("level");
        multiplier.getStyleClass().add("level");
        // Create a NextPieceListener inside ChallengeScene
        game.setNextPieceListener(new NextPieceListener() {
            @Override
            public void nextPiece(GamePiece currentPiece, GamePiece nextPiece) {
                currentPieceBoard.settingPieceToDisplay(currentPiece);
                followingPieceBoard.settingPieceToDisplay(nextPiece);

            }

        });

        scoreLabel.textProperty().bind(Bindings.concat("Score: ").concat(game.scoreProperty().asString()));
        livesLabel.textProperty().bind(Bindings.concat("Lives: ").concat(game.livesProperty().asString()));
        levelLabel.textProperty().bind(Bindings.concat("Level: ").concat(game.levelProperty().asString()));
        multiplier.textProperty().bind(Bindings.concat("Multiplier: ").concat(game.multiplierProperty().asString()));

        menuPane.getChildren().addAll(scoreLabel, livesLabel, multiplier, levelLabel,currentPieceBoard,followingPieceBoard);
        mainPane.setRight(menuPane);

        multimedia = new Multimedia();
        multimedia.playMusic("game.wav");




        //Handle block on gameboard grid being clicked
        board.setOnBlockClick(this::blockClicked);
        gameWindow.getScene().setOnKeyPressed((event)->{});
    }

    /**
     * Handle when a block is clicked
     * @param gameBlock the Game Block that was clocked
     */
    private void blockClicked(GameBlock gameBlock) {
        game.blockClicked(gameBlock);
    }

    /**
     * Setup the game object and model
     */
    public void setupGame() {
        logger.info("Starting a new challenge");

        //Start new game
        game = new Game(5, 5);
    }

    /**
     * Initialise the scene and start the game
     */
    @Override
    public void initialise() {
        logger.info("Initialising Challenge");
        game.start();
    }

}
