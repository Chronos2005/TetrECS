package uk.ac.soton.comp1206.scene;

import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.util.Objects;

public class InstructionsScene extends BaseScene {
    private static final Logger logger = LogManager.getLogger(InstructionsScene.class);
    public InstructionsScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Instructions Scene");
    }

    @Override
    public void initialise() {

    }

    public void build() {
        logger.info("Building " + this.getClass().getName());
        var instructionsPane = new StackPane();
        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        instructionsPane.setMaxWidth(gameWindow.getWidth());
        instructionsPane.setMaxHeight(gameWindow.getHeight());
        instructionsPane.getStyleClass().add("menu-background");
        Image image = new Image(Objects.requireNonNull(InstructionsScene.class.getResource("/images/Instructions.png")).toExternalForm());
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(gameWindow.getHeight());
        imageView.setFitWidth(gameWindow.getWidth());
        instructionsPane.getChildren().add(imageView);
        root.getChildren().add(instructionsPane);








    }

}
