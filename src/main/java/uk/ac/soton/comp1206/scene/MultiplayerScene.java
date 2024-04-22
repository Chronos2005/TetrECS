package uk.ac.soton.comp1206.scene;

import javafx.scene.layout.BorderPane;
import uk.ac.soton.comp1206.ui.GameWindow;

public class MultiplayerScene extends ChallengeScene {
    /**
     * Create a new Multiplayer Player challenge scene
     *
     * @param gameWindow the Game Window
     */
    public MultiplayerScene(GameWindow gameWindow) {
        super(gameWindow);
    }

    @Override
    public void build() {
        super.build();
        BorderPane borderPane = new BorderPane();
        borderPane.getStyleClass().add("multiplayer-background");
    }
}
