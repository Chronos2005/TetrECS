package uk.ac.soton.comp1206.game;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.Multimedia;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.event.GameLoopListener;
import uk.ac.soton.comp1206.event.LineClearedListener;
import uk.ac.soton.comp1206.event.NextPieceListener;
import uk.ac.soton.comp1206.event.SwapPieceListener;

/**
 * The Game class handles the main logic, state and properties of the TetrECS game. Methods to
 * manipulate the game state and to handle actions made by the player should take place inside this
 * class.
 */
public class Game {

  /** The score of the game */
  private final IntegerProperty score;
  /** The level of the game */
  private final IntegerProperty level;
  /** The number of lives the player has */
  private final IntegerProperty lives;
  /** The multiplier of the game */
  private final IntegerProperty multiplier;
  /** The random number generator */
  private final Random random = new Random();
  private static final Logger logger = LogManager.getLogger(Game.class);
  /** The listener for when the next piece is spawned */
  private NextPieceListener nextPieceListener;
  /** The piece after the current piece*/
  private GamePiece followingPiece;
  /** The listener for the game loop */
  private GameLoopListener gameLoopListener;
  /** The listener for when the pieces are swapped */
  private SwapPieceListener swapPieceListener;
  /** The listener for when the lines are cleared */
  private  LineClearedListener lineClearedListener;
  /** The  media object to play sounds and music*/
  private Multimedia media;

  /** Number of rows */
  protected final int rows;

  /** Number of columns */
  protected final int cols;

  /** The grid model linked to the game */
  protected final Grid grid;

  /** The current piece being played */
  private GamePiece curentPiece;
  /** The number of lines to clear */
  private int linesToClear;
  /** The timer for the game loop */
  private Timer timer;

  private  ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
  private Boolean piecePlayed =false;



  HashSet<GameBlockCoordinate> blocksToClear = new HashSet<>();

  /**
   * Create a new game with the specified rows and columns. Creates a corresponding grid model.
   *
   * @param cols number of columns
   * @param rows number of rows
   */
  public Game(int cols, int rows) {
    this.cols = cols;
    this.rows = rows;

    // Create a new grid model to represent the game state
    this.grid = new Grid(cols, rows);
    score = new SimpleIntegerProperty(0);
    level = new SimpleIntegerProperty(0);
    lives = new SimpleIntegerProperty(3);
    multiplier = new SimpleIntegerProperty(1);

  }

  /** Start the game */
  public void start() {
    logger.info("Starting game");
    initialiseGame();
  }

  /** Initialise a new game and set up anything that needs to be done at the start */
  public void initialiseGame() {
    logger.info("Initialising game");
    followingPiece = spawnPiece();
    nextPiece();
    timer = new Timer();
    startGameLoopTimer();

    logger.info("starting the timer");

  }

  /**
   * Handle what should happen when a particular block is clicked
   *
   * @param gameBlock the block that was clicked
   */
  public void blockClicked(GameBlock gameBlock) {
    // Get the position of this block
    int x = gameBlock.getX();
    int y = gameBlock.getY();

    if (grid.canPlayPiece(curentPiece, x, y)) {
      grid.playPiece(curentPiece, x, y);
      afterPiece();
      nextPiece();
    }
  }

  /**
   * Get the grid model inside this game representing the game state of the board
   *
   * @return game grid model
   */
  public Grid getGrid() {
    return grid;
  }

  /**
   * Get the number of columns in this game
   *
   * @return number of columns
   */
  public int getCols() {
    return cols;
  }

  /**
   * Get the number of rows in this game
   *
   * @return number of rows
   */
  public int getRows() {
    return rows;
  }

  public GamePiece spawnPiece() {
    var maxPieces = GamePiece.PIECES;
    logger.info("A Random Piece is being picked");
    var piece = GamePiece.createPiece(random.nextInt(maxPieces));
    logger.info("The Random peiece {} has been picked", random);
    return piece;
  }

  /**
   * Spawns the next piece and sets the current piece to the following piece
   */
  public void nextPiece() {

    curentPiece = followingPiece;
    followingPiece = spawnPiece();
    if (nextPieceListener != null) {
      nextPieceListener.nextPiece(curentPiece, followingPiece);
    }

    logger.info("The current piece is: {}", curentPiece);
    logger.info("The following piece is: {}", followingPiece);
  }

  /** Handles everything that happens after a piece gets placed */
  public void afterPiece() {
    logger.info("AfterPiece being called");
    linesToClear=0;
    checkingVerticalLines();
    checkingHorizontalLines();
    score(linesToClear,blocksToClear.size());
    multiplier(linesToClear);
    level();

    executorService.shutdown();

    timer.cancel();
    timer = new Timer();
    startGameLoopTimer();
    if (gameLoopListener != null) {
      gameLoopListener.onGameLoop();
    }

    if(lineClearedListener!=null){
      lineClearedListener.lineCleared(blocksToClear);
    }
    // Clear blocks
    for (GameBlockCoordinate point : blocksToClear) {
      grid.set(point.getX(), point.getY(), 0);
    }
    blocksToClear.clear();

  }

  /**
   * Checks if any columns need to be
   */
  public void checkingVerticalLines(){
    logger.info("Checkig vertical line");
    ArrayList<GameBlockCoordinate> myList = new ArrayList<>();

    for(int x=0;x<cols;x++){
      int blockCount=0;

      for (int y=0;y<rows;y++){
        if (grid.get(x,y)==0)break;
        blockCount++;
        myList.add(new GameBlockCoordinate(x,y));
      }
      if (blockCount==cols){
        linesToClear++;
        blocksToClear.addAll(myList);
        myList.clear();

      }
      else {
        myList.clear();
      }
    }

  }

  /**
   * Checks if any rows need to be cleared
   */
  public void checkingHorizontalLines(){
    logger.info("Checking Horizontal lines");
    ArrayList<GameBlockCoordinate> myList = new ArrayList<>();
    for (int y =0; y<rows;y++){
      int blockCount=0;
      for (int x=0;x<cols;x++){
        if(grid.get(x,y)==0)break;
        blockCount++;
        myList.add(new GameBlockCoordinate(x,y));
      }
      if (blockCount==rows){
        linesToClear++;
        blocksToClear.addAll(myList);
        myList.clear();

      }
      else {
        myList.clear();
      }
    }

  }

  /**
   * Get the score of the game
   * @return the score
   */
  public int getScore() {
    return score.get();
  }

  /**
   * Get the level of the game
   * @return the level
   */
  public IntegerProperty scoreProperty() {
    return score;
  }

  /**
   * Set the score of the game
   * @param score the score
   */
  public void setScore(int score) {
    this.score.set(score);
  }

  /**
   * Get the level of the game
   * @return the level
   */
  public int getLevel() {
    return level.get();
  }

  /**
   * Get the level of the game
   * @return the level
   */
  public IntegerProperty levelProperty() {
    return level;
  }
  /**
   * Set the level of the game
   * @param level the level
   */
  public void setLevel(int level) {
    this.level.set(level);
  }
  /**
   * Get the number of lives the player has
   * @return the number of lives
   */
  public int getLives() {
    return lives.get();
  }
  /**
   * Get the number of lives the player has
   * @return the number of lives
   */
  public IntegerProperty livesProperty() {
    return lives;
  }
  /**
   * Set the number of lives the player has
   * @param lives the number of lives
   */
  public synchronized void setLives(int lives) {
    this.lives.set(lives);
  }
  /**
   * Get the multiplier of the game
   * @return the multiplier
   */
  public int getMultiplier() {
    return multiplier.get();
  }
  /**
   * Get the multiplier of the game
   * @return the multiplier
   */
  public IntegerProperty multiplierProperty() {
    return multiplier;
  }
  /**
   * Set the multiplier of the game
   * @param multiplier the multiplier
   */
  public void setMultiplier(int multiplier) {
    this.multiplier.set(multiplier);
  }
  /**
   * Set the next piece listener
   * @param nextPieceListener the listener
   */
  public void setNextPieceListener(NextPieceListener nextPieceListener) {
    this.nextPieceListener = nextPieceListener;
  }
  /**
   * Changes the score of the game
   */
  public void score(int lines, int blocks) {
    var scoreIncrease = lines * blocks * getMultiplier()*10;
    setScore(getScore()+scoreIncrease);
  }
  /**
   * Changes the level of the game
   */
  public void level() {
    if (score.get() / 1000 >= 1) {
      level.set((int) Math.floor((double) score.get() / 1000));
    }
  }
  /**
   * Changes the multiplier of the game
   */
  public void multiplier(int linesCleared) {
    if (linesCleared > 0) {
      multiplier.set(multiplier.get()+1);
    } else {
      multiplier.set(1);
    }
  }

  /**
   * Rotates the current piece
   *
   * @param piece current piece
   */
  public void rotateCurrentPiece(GamePiece piece) {
    media = new Multimedia();
    media.playAudio("rotate.wav");
    piece.rotate();
  }

  /**
   * Swaps the current piece with the following piece
   */
  public void swapCurrentPiece() {
    logger.info("The pieces are being swaped");
    if (swapPieceListener != null) {
      swapPieceListener.swapPiece(curentPiece, followingPiece);
    }
    var tempPiece1 = curentPiece;
    var tempPiece2 = followingPiece;
    followingPiece = tempPiece1;
    curentPiece = tempPiece2;
    media = new Multimedia();
    media.playAudio("pling.wav");
  }


  /**
   * Get the delay for the timer unit gamelooop is called
   * @return the delay
   */
  public int getTimerDelay() {
    int maxDelay = 12000;
    int minDelay = 2500;
    int delayDecrement = 500;
    int delay = maxDelay - (level.get() * delayDecrement
    return Math.max(delay, minDelay);
  }
  /**
   * The game loop that runs every time the timer runs out
   */
  private void gameLoop() {
    Platform.runLater(()->{
        setLives(getLives() - 1);
        logger.info("The current number of lives {} ", lives.get());
        nextPiece();
        setMultiplier(1);
        // Notify the listener
        startGameLoopTimer();
        if (gameLoopListener != null) {
          gameLoopListener.onGameLoop();
        }
        if (lives.get()<0){
          timer.cancel();
        }



    });

  }
  /**
   * Set the game loop listener
   * @param listener the listener
   */
  public void setOnGameLoop(GameLoopListener listener) {
    this.gameLoopListener = listener;
  }
  /**
   * Set the swap piece listener
   * @param listener the listener
   */
  public void setSwapPieceListener(SwapPieceListener listener) {
    this.swapPieceListener = listener;
  }
  /**
   * Get the current piece
   * @return the current piece
   */
  public GamePiece getCurrentPiece(){
      return curentPiece;
  }
  /**
   * Get the following piece
   * @return the following piece
   */
  public GamePiece getFollowingPiece(){
      return followingPiece;
  }
  /**
   * Set the line cleared listener
   * @param listener the listener
   */
  public void setOnLineCleared(LineClearedListener listener){
    this.lineClearedListener=listener;
  }
  /**
   * Start the game loop timer
   */
  public void startGameLoopTimer(){
    TimerTask task = new TimerTask() {
      @Override
      public void run() {
        gameLoop();

      }
    };
    timer.schedule(task,getTimerDelay());
  }
  /**
   * Stop the game loop timer
   */
  public void stopGameLoopTimer(){
    timer.cancel();
  }
  /**
   * Set the following piece
   * @param piece the piece
   */
  public void setFollowingPiece(GamePiece piece){
    followingPiece = piece;
  }
}
