package uk.ac.soton.comp1206.component;

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
                var value = piece.getValue();
                if(blockValue>0){
                    grid.set(x,y,value);
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
                grid.set(x,y,0);

            }
        }
    }
}
