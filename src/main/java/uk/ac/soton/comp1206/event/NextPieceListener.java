package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.game.GamePiece;
/**
 * The Next Piece Listener is used to handle the event when the next piece is generated.
 */
public interface NextPieceListener {
    /**
     * Handle the next piece event
     * @param currentPiece the current piece
     * @param nextPiece the next piece
     */
    public  void nextPiece(GamePiece currentPiece, GamePiece nextPiece);
}
