package uk.ac.soton.comp1206.scene;

import javafx.animation.*;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.Multimedia;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.event.GameLoopListener;
import uk.ac.soton.comp1206.event.LineClearedListener;
import uk.ac.soton.comp1206.event.NextPieceListener;
import uk.ac.soton.comp1206.event.SwapPieceListener;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class ChallengeScene extends BaseScene {

  private static final Logger logger = LogManager.getLogger(ChallengeScene.class);
  /**
   * The game object
   */
  protected Game game;
  /**
   * The multimedia object
   */
  private Multimedia multimedia;
  /**
   * The timer progress bar to represent the time left
   */
  private ProgressBar timer;
  /**
   * The current piece board
   */
  private PieceBoard currentPieceBoard;

  /**
   * Create a new Single Player challenge scene
   *
   * @param gameWindow the Game Window
   */
  public ChallengeScene(GameWindow gameWindow) {
    super(gameWindow);
    logger.info("Creating Challenge Scene");
  }

  /** Build the Challenge window */
  @Override
  public void build() {
    logger.info("Building " + this.getClass().getName());
    setupGame();
    // initilising the game pane
    root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

    // initialising the stack pane whcih contains
    var challengePane = new StackPane();
    challengePane.setMaxWidth(gameWindow.getWidth());
    challengePane.setMaxHeight(gameWindow.getHeight());
    challengePane.getStyleClass().add("menu-background");
    root.getChildren().add(challengePane);

    var mainPane = new BorderPane();
    challengePane.getChildren().add(mainPane);
    Grid grid = new Grid(3, 3);
    Grid grid2 = new Grid(3, 3);

    var board = new GameBoard(game.getGrid(), gameWindow.getWidth() / 2, gameWindow.getWidth() / 2);
    currentPieceBoard = new PieceBoard(grid, gameWindow.getWidth() / 5, gameWindow.getHeight() / 5);
    PieceBoard followingPieceBoard =
        new PieceBoard(grid2, gameWindow.getWidth() / 5, gameWindow.getHeight() / 5);
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
    game.setNextPieceListener(
        new NextPieceListener() {
          @Override
          public void nextPiece(GamePiece currentPiece, GamePiece nextPiece) {
            currentPieceBoard.settingPieceToDisplay(currentPiece);
            followingPieceBoard.settingPieceToDisplay(nextPiece);
          }
        });

    scoreLabel
        .textProperty()
        .bind(Bindings.concat("Score: ").concat(game.scoreProperty().asString()));
    livesLabel
        .textProperty()
        .bind(Bindings.concat("Lives: ").concat(game.livesProperty().asString()));
    levelLabel
        .textProperty()
        .bind(Bindings.concat("Level: ").concat(game.levelProperty().asString()));
    multiplier
        .textProperty()
        .bind(Bindings.concat("Multiplier: ").concat(game.multiplierProperty().asString()));

    menuPane
        .getChildren()
        .addAll(
            scoreLabel, livesLabel, multiplier, levelLabel, currentPieceBoard, followingPieceBoard);
    mainPane.setRight(menuPane);
    timer = new ProgressBar(1.0);
    mainPane.setBottom(timer);
    startAnimation(game.getTimerDelay(), timer);

    Label highScoreLabel = new Label("High Score: " + getHighScore());
    highScoreLabel.getStyleClass().add("heading");
    mainPane.setTop(highScoreLabel);



    game.setSwapPieceListener(
        (currentPiece, followingPiece) -> {
          currentPieceBoard.settingPieceToDisplay(followingPiece);
          followingPieceBoard.settingPieceToDisplay(currentPiece);
        });

    game.setOnGameLoop(
        new GameLoopListener() {
          @Override
          public void onGameLoop() {
            startAnimation(game.getTimerDelay(), timer);
            if(game.getLives()<0){
              openScoreScene();
            }
          }
        });
    game.setOnLineCleared(new LineClearedListener() {
      @Override
      public void lineCleared(Set<GameBlockCoordinate> clearedCoordinates) {
        board.fadeOut(clearedCoordinates);

      }



    });



    multimedia = new Multimedia();
    multimedia.playMusic("game.wav");

    // Handle block on gameboard grid being clicked
    board.setOnBlockClick(this::blockClicked);
  }

  /**
   * Handle when a block is clicked
   *
   * @param gameBlock the Game Block that was clocked
   */
  private void blockClicked(GameBlock gameBlock) {
    game.blockClicked(gameBlock);
  }

  /** Setup the game object and model */
  public void setupGame() {
    logger.info("Starting a new challenge");

    // Start new game
    game = new Game(5, 5);
  }



  /** Initialise the scene and start the game */
  @Override
  public void initialise() {
    logger.info("Initialising Challenge");


    game.start();
    scene.setOnKeyPressed(keyEvent -> {
      if (keyEvent.getCode() == KeyCode.E) {
        logger.info("E button pressed");
        game.rotateCurrentPiece(game.getCurrentPiece());
        currentPieceBoard.settingPieceToDisplay(game.getCurrentPiece());
      } else if (keyEvent.getCode() == KeyCode.SPACE) {
        logger.info("Space bar pressed");
        game.swapCurrentPiece();
      }
    });

  }

  /**
   * Start the timer animation
   *
   * @param time the time to animate
   * @param timeBar the progress bar to animate
   */
  public void startAnimation(int time, ProgressBar timeBar) {
    logger.info("Starting timer animation");
    final double MAX_PROGRESS = 1.0;
    final Duration ANIMATION_DURATION = Duration.millis(time);

    // Set the preferred width of the progress bar
    timeBar.setPrefWidth(gameWindow.getWidth());
    // Initially set the progress bar style to green
    timeBar.setStyle("-fx-accent: green;");

    Timeline timeline = new Timeline();

    // Decrease progress from max to 0
    KeyFrame decreaseFrame =
        new KeyFrame(Duration.ZERO, new KeyValue(timeBar.progressProperty(), MAX_PROGRESS));
    KeyFrame zeroFrame =
        new KeyFrame(ANIMATION_DURATION, new KeyValue(timeBar.progressProperty(), 0));

    // Add the keyframes to the timeline
    timeline.getKeyFrames().addAll(decreaseFrame, zeroFrame);

    // Add listener to change color when progress is below threshold
    timeBar
        .progressProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue.doubleValue() < 0.4) {
                timeBar.setStyle("-fx-accent: red;");
              } else {
                timeBar.setStyle("-fx-accent: green;");
              }
            });

    // Start the animation
    timeline.play();
  }

  /**
   * Open the score scene
   */
  public void openScoreScene(){
    gameWindow.loadScene(new ScoresScene(gameWindow,game));
  }

  /**
   * Get the high score from the scores.txt file
   * @return the high score
   */
  public String getHighScore(){
    String highscore = "n/a";
    try {
      BufferedReader reader = new BufferedReader(new FileReader("scores.txt"));
      String firstLine = reader.readLine();
      String parts[] = firstLine.split(":");
      highscore= parts[1];

      reader.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return highscore;
  }
}

