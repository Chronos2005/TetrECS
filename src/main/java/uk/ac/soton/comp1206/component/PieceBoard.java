package uk.ac.soton.comp1206.component;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;

public class PieceBoard extends GameBoard {
    public PieceBoard(Grid grid, double width, double height) {
        super(grid, width, height);

    }

    public void settingPieceToDisplay(GamePiece piece){
        clear();
        int[][] blocks = piece.getBlocks();
        for (var x=0;x<blocks.length;x++){
            for (var y=0;y<blocks.length;y++){
                var blockValue = blocks[x][y];
                if(blockValue>0){
                    Rectangle block = new Rectangle();
                    block.setFill(Color.RED); // You can set any color you want here
                    block.setWidth(getWidth() / 3);
                    block.setHeight(getHeight() / 3);
                    add(block, x, y); // Add the block to the PieceBoard
                }
            }
        }



    }
    /**
     * Clear the PieceBoard by removing all blocks from it.
     */
    public void clear() {
        for (var x=0;x<blocks.length;x++){
            for (var y=0;y<blocks.length;y++){
                Rectangle block = new Rectangle();
                block.setFill(Color.WHITE); // You can set any color you want here
                block.setWidth(getWidth() / 3);
                block.setHeight(getHeight() / 3);
                add(block, x, y); // Add the block to the PieceBoard

            }
        }
    }
}
