package uk.ac.soton.comp1206.game;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.event.NextPieceListener;

import java.util.Random;

/**
 * The Game class handles the main logic, state and properties of the TetrECS game. Methods to manipulate the game state
 * and to handle actions made by the player should take place inside this class.
 */
public class Game {

    private IntegerProperty score;
    private IntegerProperty level;
    private IntegerProperty lives;
    private IntegerProperty multiplier;
    private Random random = new  Random();
    private static final Logger logger = LogManager.getLogger(Game.class);

    private NextPieceListener nextPieceListener;

    /**
     * Number of rows
     */
    protected final int rows;

    /**
     * Number of columns
     */
    protected final int cols;

    /**
     * The grid model linked to the game
     */
    protected final Grid grid;

  private GamePiece curentPiece;

    /**
     * Create a new game with the specified rows and columns. Creates a corresponding grid model.
     * @param cols number of columns
     * @param rows number of rows
     */
    public Game(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        //Create a new grid model to represent the game state
        this.grid = new Grid(cols,rows);
        score = new SimpleIntegerProperty(0);
        level = new SimpleIntegerProperty(0);
        lives = new SimpleIntegerProperty(3);
        multiplier = new SimpleIntegerProperty(1);
    }

    /**
     * Start the game
     */
    public void start() {
        logger.info("Starting game");
        initialiseGame();
    }

    /**
     * Initialise a new game and set up anything that needs to be done at the start
     */
    public void initialiseGame() {
        nextPiece();
        logger.info("Initialising game");
    }

  /**
   * Handle what should happen when a particular block is clicked
   * @param gameBlock the block that was clicked
   */
  public void blockClicked(GameBlock gameBlock) {
    // Get the position of this block
    int x = gameBlock.getX();
    int y = gameBlock.getY();

    if (grid.canPlayPiece(curentPiece, x, y)) {
      grid.playPiece(curentPiece, x, y);
      nextPiece();
    }
    }

    /**
     * Get the grid model inside this game representing the game state of the board
     * @return game grid model
     */
    public Grid getGrid() {
        return grid;
    }

    /**
     * Get the number of columns in this game
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Get the number of rows in this game
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }

     public GamePiece spawmPiece(){
        var maxPieces = GamePiece.PIECES;
        logger.info("A Random Piece is being picked");
        var piece = GamePiece.createPiece(random.nextInt(maxPieces));
        logger.info("The Random peiece {} has been picked", random);
        return piece;

     }

     public GamePiece nextPiece(){
        curentPiece = spawmPiece();
        logger.info("The next piece is: {}", curentPiece);
        if(nextPieceListener!=null){
            nextPieceListener.nextPiece(curentPiece);
        }
        nextPieceListener.nextPiece(curentPiece);
        return curentPiece;
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

    public void setLives(int lives) {
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

    public void setNextPieceListener(NextPieceListener nextPieceListener){
        this.nextPieceListener= nextPieceListener;
    }

    public void rotateCurrentPiece(GamePiece piece){
        piece.rotate();
    }
}
