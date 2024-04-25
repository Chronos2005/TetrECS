package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.component.GameBlockCoordinate;

import java.util.Set;

/**
 * The Line Cleared Listener is used to handle the event when a line is cleared in the game.
 */
public interface LineClearedListener {
    /**
     * Handle the line cleared event
     * @param clearedCoordinates the coordinates of the blocks that were cleared
     */
    public void lineCleared(Set<GameBlockCoordinate> clearedCoordinates);
}
