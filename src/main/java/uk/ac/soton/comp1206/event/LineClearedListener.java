package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.component.GameBlockCoordinate;

import java.util.Set;

public interface LineClearedListener {
    public void lineCleared(Set<GameBlockCoordinate> clearedCoordinates);
}
