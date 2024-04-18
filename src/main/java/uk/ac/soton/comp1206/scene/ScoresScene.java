package uk.ac.soton.comp1206.scene;

import javafx.beans.Observable;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.ScoresList;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/*
public class ScoresScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(ScoresScene.class);
    private ScoresList scoresList ;

    private SimpleListProperty localScores;


    public ScoresScene(GameWindow gameWindow, Game game) {
        super(gameWindow);
        logger.info("Creating the scores scene");
        // Set up the observable list of scores
        ArrayList<Pair<String, Integer>> Scores = new ArrayList<>();

        ObservableList<Pair<String, Integer>> observableList = FXCollections.observableArrayList(Scores);
        localScores = new SimpleListProperty<Pair<String, Integer>>(observableList);





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
        Label localScore = new Label("Local Scores");


    }

}

 */
public class ScoresScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(ScoresScene.class);
    private ScoresList scoresList;

    private SimpleListProperty<Pair<String, Integer>> localScores;

    public ScoresScene(GameWindow gameWindow, Game game) {
        super(gameWindow);
        logger.info("Creating the scores scene");
        // Set up the observable list of scores
        ArrayList<Pair<String, Integer>> scores = new ArrayList<>();

        ObservableList<Pair<String, Integer>> observableList = FXCollections.observableArrayList(scores);
        localScores = new SimpleListProperty<>(observableList);

        loadScores();
        checkNewHighScore(game.getScore());
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

        Label localScoreLabel = new Label("Local Scores");
        VBox vBox = new VBox();
        BorderPane borderPane = new BorderPane();
        vBox.getChildren().add(localScoreLabel);



        scoresList = new ScoresList();
        scoresList.scoresProperty().bindBidirectional(localScores);
        vBox.setMaxWidth(gameWindow.getWidth()/2);
        vBox.getChildren().add(scoresList);

        borderPane.setCenter(vBox);
        scoresPane.getChildren().add(borderPane);
        scoresList.showAnimation();

        scoresList.reveal();


    }

    private void loadScores() {
        try {
            File scoresFile = new File("scores.txt");
            if (scoresFile.exists()) {
                List<String> lines = Files.readAllLines(scoresFile.toPath());
                for (String line : lines) {
                    String[] parts = line.split(":");
                    if (parts.length == 2) {
                        String name = parts[0];
                        int score = Integer.parseInt(parts[1]);
                        localScores.add(new Pair<>(name, score));
                    }
                }
            } else {
                // Create a default list of scores if the file doesn't exist
                localScores.addAll(
                        Arrays.asList(
                                new Pair<>("Player1", 100),
                                new Pair<>("Player2", 80),
                                new Pair<>("Player3", 60)
                        )
                );
                writeScores();
            }
        } catch (IOException e) {
            logger.error("Failed to load scores from file", e);
        }
    }

    private void writeScores() {
        try {
            File scoresFile = new File("scores.txt");
            List<String> lines = new ArrayList<>();
            for (Pair<String, Integer> score : localScores) {
                lines.add(score.getKey() + ":" + score.getValue());
            }
            Files.write(scoresFile.toPath(), lines);
        } catch (IOException e) {
            logger.error("Failed to write scores to file", e);
        }
    }

    private void checkNewHighScore(int playerScore) {
        if (!localScores.isEmpty()) {
            int lowestScore = localScores.get(localScores.size() - 1).getValue();
            if (playerScore > lowestScore) {
                String name = promptForName();
                int insertIndex = localScores.size();
                for (int i = 0; i < localScores.size(); i++) {
                    if (playerScore > localScores.get(i).getValue()) {
                        insertIndex = i;
                        break;
                    }
                }
                localScores.add(insertIndex, new Pair<>(name, playerScore));
                writeScores();
            }
        } else {
            String name = promptForName();
            localScores.add(new Pair<>(name, playerScore));
            writeScores();
        }
    }

    private String promptForName() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New High Score");
        dialog.setHeaderText("Congratulations! You achieved a new high score.");
        dialog.setContentText("Please enter your name:");

        Optional<String> result = dialog.showAndWait();
        return result.orElse("Anonymous");
    }
}
