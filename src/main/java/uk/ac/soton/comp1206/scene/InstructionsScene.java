package uk.ac.soton.comp1206.scene;

import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
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
    instructionsPane.getStyleClass().add("instructions-background");

    // Adding Background image
    Image image =
        new Image(
            Objects.requireNonNull(InstructionsScene.class.getResource("/images/Instructions.png"))
                .toExternalForm());
    ImageView imageView = new ImageView(image);
    imageView.setFitHeight(gameWindow.getHeight() / 2);
    imageView.setPreserveRatio(true); // maintain aspect ratio
    // Create a StackPane to center the ImageView'
    Text Title = new Text("Instructions");
    Title.getStyleClass().add("title");
    Text instructions = new Text("TertECS is a fast-paced gravity- free block placement game, where you must survive by clearing rows through careful placement of the \nupcoming  blocks before the time runs out. Loose all three lives and you're destroyes!");
    instructions.setWrappingWidth(gameWindow.getWidth()/2);
    instructions.getStyleClass().add("instructions");
    StackPane imagePane = new StackPane(imageView);
    VBox display = new VBox(Title,instructions,imagePane);
    display.setAlignment(Pos.CENTER);
    BorderPane.setAlignment(display, Pos.TOP_CENTER); // Align the StackPane to the top center
    var gamePieces = createBlocks();
    instructionsPane.setTop(display);
    instructionsPane.setBottom(gamePieces);
    BorderPane.setAlignment(gamePieces, Pos.CENTER);

    root.getChildren().add(instructionsPane);
  }

  public VBox createBlocks() {
    Text text = new Text("Game Pieces");
    text.getStyleClass().add("title");
    GridPane gridPane = new GridPane();
    VBox display = new VBox();
    display.setAlignment(Pos.CENTER);


    for (var x = 0; x < 5; x++) {
      Grid grid = new Grid(3, 3);
      PieceBoard pieceBoard =
          new PieceBoard(grid, gameWindow.getWidth() / 12, gameWindow.getHeight() / 12);
      pieceBoard.settingPieceToDisplay(GamePiece.createPiece(x));

      gridPane.add(pieceBoard, x, 0);
    }
    for (var x = 5; x < 10; x++) {
      Grid grid = new Grid(3, 3);
      PieceBoard pieceBoard =
          new PieceBoard(grid, gameWindow.getWidth() / 12, gameWindow.getHeight() / 12);
      pieceBoard.settingPieceToDisplay(GamePiece.createPiece(x));
      gridPane.add(pieceBoard, x - 5, 1);
    }
    for (var x = 10; x < 15; x++) {
      Grid grid = new Grid(3, 3);
      PieceBoard pieceBoard =
          new PieceBoard(grid, gameWindow.getWidth() / 12, gameWindow.getHeight() / 12);
      pieceBoard.settingPieceToDisplay(GamePiece.createPiece(x));
        gridPane.add(pieceBoard, x - 10, 2);
    }

    // Set horizontal and vertical gap
    gridPane.setHgap(10); // horizontal gap in pixels
    gridPane.setVgap(10); // vertical gap in pixels
    gridPane.setAlignment(Pos.CENTER);
    display.getChildren().addAll(text,gridPane);
    return display;
  }
}
