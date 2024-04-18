package uk.ac.soton.comp1206.scene;

import javafx.beans.Observable;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.ScoresList;
import uk.ac.soton.comp1206.event.CommunicationsListener;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.network.Communicator;
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
public class ScoresScene extends BaseScene implements CommunicationsListener {

    private static final Logger logger = LogManager.getLogger(ScoresScene.class);
    private ScoresList scoresList;
    private ScoresList remoteScoresList;

    private SimpleListProperty<Pair<String, Integer>> localScores;
    private SimpleListProperty<Pair<String, Integer>> remoteScores;

    public ScoresScene(GameWindow gameWindow, Game game) {
        super(gameWindow);
        logger.info("Creating the scores scene");
        // Set up the observable list of scores
        ArrayList<Pair<String, Integer>> scores = new ArrayList<>();

        ObservableList<Pair<String, Integer>> observableList = FXCollections.observableArrayList(scores);
        localScores = new SimpleListProperty<>(observableList);
        remoteScores = new SimpleListProperty<>();
        loadScores();

        loadOnlineScores();
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

        // Create labels for local and remote scores
        Label localScoreLabel = new Label("Local Scores");
        Label remoteScoreLabel = new Label("Online Scores");

        // Create VBox to hold the labels
        VBox labelsBox = new VBox();
        labelsBox.getChildren().addAll(localScoreLabel, remoteScoreLabel);
        labelsBox.setSpacing(10);
        labelsBox.setAlignment(Pos.CENTER);

        // Create ScoresList for local score
        scoresList= new ScoresList();
        scoresList.scoresProperty().bindBidirectional(localScores);

        // Create ScoresList for remote scores
        remoteScoresList = new ScoresList();
        remoteScoresList.scoresProperty().bindBidirectional(remoteScores);

        // Create HBox to hold the local and remote scores side by side
        HBox scoresBox = new HBox();
        scoresBox.getChildren().addAll(scoresList, remoteScoresList);
        scoresBox.setSpacing(20);
        scoresBox.setAlignment(Pos.CENTER);

        // Create VBox to hold the labels and scores
        VBox mainBox = new VBox();
        mainBox.getChildren().addAll(labelsBox, scoresBox);
        mainBox.setSpacing(20);
        mainBox.setAlignment(Pos.CENTER);

        // Add the main VBox to the scores pane
        scoresPane.getChildren().add(mainBox);

        // Hide the scores initially
        scoresList.hide();
        remoteScoresList.hide();

        // Reveal the scores with animation
        scoresList.reveal();
        remoteScoresList.reveal();
        scoresList.showAnimation();
        remoteScoresList.showAnimation();

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


    private void loadOnlineScores(){
        gameWindow.getCommunicator().send("HISCORES DEFAULT");
    }

    @Override
    public void receiveCommunication(String communication) {
        if (communication.startsWith("HISCORES")) {
            String[] scores = communication.split("\\n");
            remoteScores.clear();
            for (int i = 1; i < scores.length; i++) {
                String score = scores[i].trim();
                if (!score.isEmpty()) {
                    String[] data = score.split(":");
                    if (data.length == 2) {
                        String name = data[0];
                        int scoreValue = Integer.parseInt(data[1]);
                        remoteScores.add(new Pair<>(name, scoreValue));
                    }
                }
            }
            remoteScores.sort((p1, p2) -> p2.getValue().compareTo(p1.getValue()));
        } else if (communication.startsWith("NEWSCORE")) {
            String[] data = communication.split("\\s+")[1].split(":");
            if (data.length == 2) {
                String name = data[0];
                int score = Integer.parseInt(data[1]);
                remoteScores.add(new Pair<>(name, score));
                remoteScores.sort((p1, p2) -> p2.getValue().compareTo(p1.getValue()));
            }
        }


    }
}
