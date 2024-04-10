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
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.Grid;
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
        Grid grid = new Grid(3,3);
        PieceBoard pieceBoard1= new PieceBoard(grid,gameWindow.getWidth()/12,gameWindow.getHeight()/12);
        PieceBoard pieceBoard2= new PieceBoard(grid,gameWindow.getWidth()/12,gameWindow.getHeight()/12);
        PieceBoard pieceBoard3= new PieceBoard(grid,gameWindow.getWidth()/12,gameWindow.getHeight()/12);
        PieceBoard pieceBoard4= new PieceBoard(grid,gameWindow.getWidth()/12,gameWindow.getHeight()/12);

        Image image = new Image(Objects.requireNonNull(InstructionsScene.class.getResource("/images/Instructions.png")).toExternalForm());
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(gameWindow.getHeight());
        imageView.setFitWidth(gameWindow.getWidth());
        instructionsPane.getChildren().addAll(imageView,pieceBoard1);
        root.getChildren().add(instructionsPane);








    }

}
