package uk.ac.soton.comp1206.game;

import java.util.*;

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

  private final IntegerProperty score;
  private final IntegerProperty level;
  private final IntegerProperty lives;
  private final IntegerProperty multiplier;
  private final Random random = new Random();
  private static final Logger logger = LogManager.getLogger(Game.class);

  private NextPieceListener nextPieceListener;
  private GamePiece followingPiece;
  private GameLoopListener gameLoopListener;
  private SwapPieceListener swapPieceListener;
  private  LineClearedListener lineClearedListener;
  private Multimedia media;

  /** Number of rows */
  protected final int rows;

  /** Number of columns */
  protected final int cols;

  /** The grid model linked to the game */
  protected final Grid grid;

  private GamePiece curentPiece;
  private int linesToClear;



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
    followingPiece = spawmPiece();
    nextPiece();

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

  public GamePiece spawmPiece() {
    var maxPieces = GamePiece.PIECES;
    logger.info("A Random Piece is being picked");
    var piece = GamePiece.createPiece(random.nextInt(maxPieces));
    logger.info("The Random peiece {} has been picked", random);
    return piece;
  }

  public void nextPiece() {

    curentPiece = followingPiece;
    followingPiece = spawmPiece();
    if (nextPieceListener != null) {
      nextPieceListener.nextPiece(curentPiece, followingPiece);
    }

    logger.info("The current piece is: {}", curentPiece);
    logger.info("The following piece is: {}", followingPiece);
  }

  /** Handles everything that happens after */
  public void afterPiece() {
    logger.info("AfterPiece being called");
    linesToClear=0;
    checkingVerticalLines();
    checkingHorizontalLines();
    score(linesToClear,blocksToClear.size());
    multiplier(linesToClear);
    level();

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


  public int getScore() {
    return score.get();
  }

  public IntegerProperty scoreProperty() {
    return score;
  }

  public void setScore(int score) {
    this.score.set(score);
  }

  public int getLevel() {
    return level.get();
  }

  public IntegerProperty levelProperty() {
    return level;
  }

  public void setLevel(int level) {
    this.level.set(level);
  }

  public int getLives() {
    return lives.get();
  }

  public IntegerProperty livesProperty() {
    return lives;
  }

  public synchronized void setLives(int lives) {
    this.lives.set(lives);
  }

  public int getMultiplier() {
    return multiplier.get();
  }

  public IntegerProperty multiplierProperty() {
    return multiplier;
  }

  public void setMultiplier(int multiplier) {
    this.multiplier.set(multiplier);
  }

  public void setNextPieceListener(NextPieceListener nextPieceListener) {
    this.nextPieceListener = nextPieceListener;
  }

  public void score(int lines, int blocks) {
    var scoreIncrease = lines * blocks * getMultiplier()*10;
    setScore(getScore()+scoreIncrease);
  }

  public void level() {
    if (score.get() / 1000 >= 1) {
      level.set((int) Math.floor((double) score.get() / 1000));
    }
  }

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
    media.playAudio("/sounds/rotate.wav");
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
    media.playAudio("/sounds/pling.wav");
  }



  public int getTimerDelay() {
    int MAX_DELAY = 12000;
    int MIN_DELAY = 2500;
    int DELAY_DECREMENT = 500;
    int delay = MAX_DELAY - (level.get() * DELAY_DECREMENT);
    return Math.max(delay, MIN_DELAY);
  }

  private void gameLoop() {
    Platform.runLater(
        () -> {
          setLives(getLives() - 1);
          logger.info("The current number of lives {} ", lives.get());
          nextPiece();
          setMultiplier(1);
          // Notify the listener
          if (gameLoopListener != null) {
            gameLoopListener.onGameLoop();
          }
        });
  }

  public void setOnGameLoop(GameLoopListener listener) {
    this.gameLoopListener = listener;
  }

  public void setSwapPieceListener(SwapPieceListener listener) {
    this.swapPieceListener = listener;
  }

  public GamePiece getCurrentPiece(){
      return curentPiece;
  }

  public GamePiece getFollowingPiece(){
      return followingPiece;
  }

  public void setOnLineCleared(LineClearedListener listener){
    this.lineClearedListener=listener;
  }
}
