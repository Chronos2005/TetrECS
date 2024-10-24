package uk.ac.soton.comp1206.game;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.media.Media;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.Multimedia;

/**
 * The Grid is a model which holds the state of a game board. It is made up of a set of Integer
 * values arranged in a 2D arrow, with rows and columns.
 *
 * <p>Each value inside the Grid is an IntegerProperty can be bound to enable modification and
 * display of the contents of the grid.
 *
 * <p>The Grid contains functions related to modifying the model, for example, placing a piece
 * inside the grid.
 *
 * <p>The Grid should be linked to a GameBoard for it's display.
 */
public class Grid {

  private static final Logger logger = LogManager.getLogger(Grid.class);

  /** The number of columns in this grid */
  private final int cols;

  /** The number of rows in this grid */
  private final int rows;

  /** The grid is a 2D arrow with rows and columns of SimpleIntegerProperties. */
  private final SimpleIntegerProperty[][] grid;
  private Multimedia multimedia;

  /**
   * Create a new Grid with the specified number of columns and rows and initialise them
   *
   * @param cols number of columns
   * @param rows number of rows
   */
  public Grid(int cols, int rows) {
    this.cols = cols;
    this.rows = rows;

    // Create the grid itself
    grid = new SimpleIntegerProperty[cols][rows];

    // Add a SimpleIntegerProperty to every block in the grid
    for (var y = 0; y < rows; y++) {
      for (var x = 0; x < cols; x++) {
        grid[x][y] = new SimpleIntegerProperty(0);
      }
    }
  }

  /**
   * Get the Integer property contained inside the grid at a given row and column index. Can be used
   * for binding.
   *
   * @param x column
   * @param y row
   * @return the IntegerProperty at the given x and y in this grid
   */
  public IntegerProperty getGridProperty(int x, int y) {
    return grid[x][y];
  }

  /**
   * Update the value at the given x and y index within the grid
   *
   * @param x column
   * @param y row
   * @param value the new value
   */
  public void set(int x, int y, int value) {
    grid[x][y].set(value);
  }

  /**
   * Get the value represented at the given x and y index within the grid
   *
   * @param x column
   * @param y row
   * @return the value
   */
  public int get(int x, int y) {
    try {
      // Get the value held in the property at the x and y index provided
      return grid[x][y].get();
    } catch (ArrayIndexOutOfBoundsException e) {
      // No such index
      return -1;
    }
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

  /**
   * Check Whether a piece can be played un the gird at the given x,y position
   *
   * @param piece the piece to play
   * @param positionX position X
   * @param positionY position Y
   * @return whether the piece can be played or not
   */
  public boolean canPlayPiece(GamePiece piece, int positionX, int positionY) {
    logger.info("Checking if the piece can be played");
    int[][] blocks = piece.getBlocks();
    for (var x = 0; x < blocks.length; x++) {
      for (var y = 0; y < blocks.length; y++) {
        var blockValue = blocks[x][y];
        if (blockValue > 0) {
          var gridValue = get(positionX + x - 1, positionY + y - 1);
          if (gridValue != 0) return false;
        }
      }
    }
    return true;
  }

  /**
   * Play a piece by updating the grid by adding the pice to the grid
   *
   * @param piece the piece being played
   * @param positionX postion X
   * @param positionY position Y
   */
  public void playPiece(GamePiece piece, int positionX, int positionY) {
    logger.info("Playing the piece");
    int value = piece.getValue();
    int[][] blocks = piece.getBlocks();

    for (var x = 0; x < blocks.length; x++) {
      for (var y = 0; y < blocks.length; y++) {
        var blockValue = blocks[x][y];
        if (blockValue > 0) {
          set(positionX + x - 1, positionY + y - 1, value);
        }
      }
    }
    multimedia = new Multimedia();
    multimedia.playAudio("place.wav");
  }

  /** X coordinate of the aim */
  private final IntegerProperty aimX = new SimpleIntegerProperty(2);
  /** Y coordinate of the aim */
  private final IntegerProperty aimY = new SimpleIntegerProperty(2);

  /**
   * Get the X coordinate of the aim
   *
   * @return the X coordinate of the aim
   */
  public int getAimX() {
      return aimX.get();
  }
  /**
   * Get the Y coordinate of the aim
   *
   * @return the Y coordinate of the aim
   */
  public int getAimY() {
      return aimY.get();
  }

  /**
   * Set the X coordinate of the aim
   *
   * @param x the X coordinate of the aim
   */
  public void setAimX(int x) {
      aimX.set(x);
  }

    /**
     * Set the Y coordinate of the aim
     *
     * @param y the Y coordinate of the aim
     */
  public void setAimY(int y) {
      aimY.set(y);
  }


}
