package uk.ac.soton.comp1206.game;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.event.GameLoopListener;
import uk.ac.soton.comp1206.event.NextPieceListener;
import uk.ac.soton.comp1206.event.SwapPieceListener;

/**
 * The Game class handles the main logic, state and properties of the TetrECS game. Methods to manipulate the game state
 * and to handle actions made by the player should take place inside this class.
 */
public class Game {

    private final IntegerProperty score;
    private final IntegerProperty level;
    private final IntegerProperty lives;
    private final IntegerProperty multiplier;
    private final Random random = new  Random();
    private static final Logger logger = LogManager.getLogger(Game.class);

    private NextPieceListener nextPieceListener;
    private GamePiece followingPiece;
    private GameLoopListener gameLoopListener;
    private SwapPieceListener swapPieceListener;

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

    private Timer timer;
    private  TimerTask task;

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
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                gameLoop();

            }
        };
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
        logger.info("Initialising game");
        followingPiece=spawmPiece();
        nextPiece();

        logger.info("starting the timer");
        timer.schedule(task,getTimerDelay());

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
      afterPiece();
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

     public void nextPiece(){

         curentPiece = followingPiece;
         followingPiece = spawmPiece();
         if(nextPieceListener!=null){
             nextPieceListener.nextPiece(curentPiece,followingPiece);
         }



        logger.info("The current piece is: {}", curentPiece);
        logger.info("The following piece is: {}", followingPiece);


     }

    /**
     * Handles everything that happens after
     */
    public void afterPiece(){
        var linesCleared =0;
        int counterx =0;
        for(var x=0; x<cols;x++){
            for (var y =0 ; y<rows;y++){
                if(grid.get(x,y)==0)break;
                counterx++;
            }
            if (counterx ==rows){
                clearColumn(x);
                linesCleared++;
            }

        }

        int countery = 0;
        for(var y =0;y<rows;y++){
            for(var x =0; x<cols;x++){
                if(grid.get(x,y)==0)break;
                countery++;
            }
            if(countery==cols){
                clearRow(y);
                linesCleared++;
            }

        }
        score(linesCleared,linesCleared*5);
        level();
        multiplier(linesCleared);
        timer.cancel();
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                logger.info("gameloop is being called");
                gameLoop();

            }

        };
        timer.schedule(task,getTimerDelay());
        // Notify the listener
        if (gameLoopListener != null) {
            gameLoopListener.onGameLoop();
        }






    }

    public void clearColumn(int x){
        logger.info("Clearing a column");
        for (var y=0;y<cols;y++){
            grid.set(x,y,0);
        }
    }

    public void clearRow(int y){
        logger.info("Clearing a Row");
        for (var x=0;x<rows;x++){
            grid.set(x,y,0);
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

    public void setNextPieceListener(NextPieceListener nextPieceListener){
        this.nextPieceListener= nextPieceListener;
    }

    public void score(int lines , int blocks){
        var score = lines*blocks*getMultiplier();
        setScore(score);
    }

    public void level(){
        if(score.get()/1000>=1){
            level.set( (int)Math.floor((double) score.get() /1000));
        }

    }

    public void multiplier(int linesCleared){
        if (linesCleared>0){
            multiplier.set(multiplier.get()+linesCleared);
        }
        else {
            multiplier.set(1);
        }

    }

    /**
     * Rotates the current piece
     * @param piece current piece
     */
    public void rotateCurrentPiece(GamePiece piece){
        piece.rotate();
    }

    public void swapCurrentPiece(){
        logger.info("The pieces are being swaped");
        if(swapPieceListener!=null){
            swapPieceListener.swapPiece(curentPiece,followingPiece);
        }
        var tempPiece1 = curentPiece;
        var tempPiece2 = followingPiece;
        followingPiece = tempPiece1;
        curentPiece = tempPiece2;

    }

    public int getTimerDelay() {
        int MAX_DELAY = 12000;
        int MIN_DELAY = 2500;
        int DELAY_DECREMENT = 500;
        int delay = MAX_DELAY - (level.get() * DELAY_DECREMENT);
        return Math.max(delay, MIN_DELAY);
    }




    private void gameLoop() {
        Platform.runLater(()->{
            setLives(getLives()-1);
            logger.info("The current number of lives {} ",lives.get());
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

    public void setSwapPieceListener(SwapPieceListener listener){
        this.swapPieceListener = listener;
    }




}
