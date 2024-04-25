package uk.ac.soton.comp1206.component;

import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;

/**
 * The PieceBoard is used to display the current piece that is being controlled by the player.
 */
public class PieceBoard extends GameBoard {
    /**
     * Create a new PieceBoard with the given grid and dimensions.
     * @param grid grid to use
     * @param width width of the board
     * @param height height of the board
     */
    public PieceBoard(Grid grid, double width, double height) {
        super(grid, width, height);

    }

    /**
     * Set the piece to display on the PieceBoard
     * @param piece the piece to display
     */
    public void settingPieceToDisplay(GamePiece piece){
        clear();

        int[][] blocks = piece.getBlocks();
        for (var x=0;x<blocks.length;x++){
            for (var y=0;y<blocks.length;y++){
                var blockValue = blocks[x][y];
                var value = piece.getValue();
                if(blockValue>0){
                    grid.set(x,y,value);
                }
            }
        }



        // Get the middle block
        int middleX = (int) Math.floor(blocks.length / 2.0);
        int middleY = (int) Math.floor(blocks[0].length / 2.0);
        GameBlock middleBlock = getBlock(middleX, middleY);

        // Draw the circle on the middle block
        middleBlock.drawCircle();





    }
    /**
     * Clear the PieceBoard by removing all blocks from it.
     */
    public void clear() {
        for (var x=0;x<blocks.length;x++){
            for (var y=0;y<blocks.length;y++){
                grid.set(x,y,0);

            }
        }
    }
}
