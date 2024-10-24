package uk.ac.soton.comp1206.game;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.scene.InstructionsScene;


/**
 * Multiplayer game class to play games with multiple players
 */
public class MultiplayerGame extends Game{
    /**
     * The communicator object
     */
    private Communicator communicator = new Communicator("ws://ofb-labs.soton.ac.uk:9700");
    private static final Logger logger = LogManager.getLogger(InstructionsScene.class);
    /**
     * The new piece
     */
    private int newPiece=0;

    /**
     * Create a multiplayer game with a specified number of rows and columns for the gameboard
     * @param cols number of columns
     * @param rows  number of rows
     */
    public MultiplayerGame(int cols, int rows) {
        super(cols, rows);
    }





    /**
     * Returns the number of the piece being created
     * @param communication message received from the server
     */
    public void pieceQueue(String communication){
        if(communication.startsWith("PIECE")){
            communication = communication.trim().substring(communication.indexOf(" ")+1);
             newPiece = Integer.parseInt(communication);
        }

    }

    /*
    /**
     * created a piece based on the new piece number
     * @return
     *
    public GamePiece spawnPiece() {
        logger.info("Spawning new piece in multiplayer");
        communicator.send("PIECE");
        // Create a new piece
        var piece = GamePiece.createPiece(newPiece);

        return piece;
    }
    *
     */





}
