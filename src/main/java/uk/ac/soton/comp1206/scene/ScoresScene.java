package uk.ac.soton.comp1206.scene;

import javafx.beans.property.SimpleListProperty;
import javafx.scene.layout.StackPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

public class ScoresScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(ScoresScene.class);
  private SimpleListProperty localScores = new SimpleListProperty();

    public ScoresScene(GameWindow gameWindow, Game game) {
        super(gameWindow);
        logger.info("Creating the scores scene");

    }


    @Override
    public void initialise() {

    }

    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());
        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());
        var scoresPane = new StackPane();
        scoresPane.getStyleClass().add("menu-background");
        root.getChildren().add(scoresPane);

    }
}
