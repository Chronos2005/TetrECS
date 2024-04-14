package uk.ac.soton.comp1206.scene;

import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.util.Objects;

public class InstructionsScene extends BaseScene {
  private static final Logger logger = LogManager.getLogger(InstructionsScene.class);
  private GamePiece gamePiece;

  public InstructionsScene(GameWindow gameWindow) {
    super(gameWindow);
    logger.info("Creating Instructions Scene");
  }

  @Override
  public void initialise() {}

  public void build() {
    logger.info("Building " + this.getClass().getName());
    var instructionsPane = new BorderPane();
    root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

    instructionsPane.setMaxWidth(gameWindow.getWidth());
    instructionsPane.setMaxHeight(gameWindow.getHeight());
    instructionsPane.getStyleClass().add("menu-background");

    // Adding Background image
    Image image =
        new Image(
            Objects.requireNonNull(InstructionsScene.class.getResource("/images/Instructions.png"))
                .toExternalForm());
    ImageView imageView = new ImageView(image);
    imageView.setFitHeight(gameWindow.getHeight() / 2);
    imageView.setPreserveRatio(true); // maintain aspect ratio
    // Create a StackPane to center the ImageView
    StackPane imagePane = new StackPane(imageView);
    BorderPane.setAlignment(imagePane, Pos.TOP_CENTER); // Align the StackPane to the top center

    instructionsPane.setTop(imagePane);
    createBlocks().setAlignment(Pos.CENTER);
    instructionsPane.setBottom(createBlocks());
    root.getChildren().add(instructionsPane);
  }

  public VBox createBlocks() {
    HBox hBox1 = new HBox();
    HBox hBox2 = new HBox();
    HBox hBox3 = new HBox();
    VBox display = new VBox();
    for (var x = 0; x < 5; x++) {
      Grid grid = new Grid(3, 3);
      PieceBoard pieceBoard =
          new PieceBoard(grid, gameWindow.getWidth() / 12, gameWindow.getHeight() / 12);
      pieceBoard.settingPieceToDisplay(GamePiece.createPiece(x));
      hBox1.getChildren().add(pieceBoard);
    }
    for (var x = 5; x < 10; x++) {
      Grid grid = new Grid(3, 3);
      PieceBoard pieceBoard =
          new PieceBoard(grid, gameWindow.getWidth() / 12, gameWindow.getHeight() / 12);
      pieceBoard.settingPieceToDisplay(GamePiece.createPiece(x));
      hBox2.getChildren().add(pieceBoard);
    }
    for (var x = 10; x < 15; x++) {
      Grid grid = new Grid(3, 3);
      PieceBoard pieceBoard =
          new PieceBoard(grid, gameWindow.getWidth() / 12, gameWindow.getHeight() / 12);
      pieceBoard.settingPieceToDisplay(GamePiece.createPiece(x));
      hBox3.getChildren().add(pieceBoard);
    }
    display.getChildren().addAll(hBox1, hBox2, hBox3);
    return display;
  }
}
