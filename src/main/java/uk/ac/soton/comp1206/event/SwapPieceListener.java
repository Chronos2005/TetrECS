package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.game.GamePiece;
/**
 * The Swap Piece Listener is used to handle the event when the current piece ande the following piece are being swapped.
 */
public interface SwapPieceListener {
    /**
     * Handle the swap piece event
     * @param currentPiece the current piece
     * @param followingPiece the following piece
     */
    public void swapPiece(GamePiece currentPiece, GamePiece followingPiece);
}
