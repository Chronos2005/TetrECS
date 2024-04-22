package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
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
public class ScoresScene extends BaseScene  {

    private static final Logger logger = LogManager.getLogger(ScoresScene.class);
    private ScoresList scoresList;
    private ScoresList remoteScoresList;

    private SimpleListProperty<Pair<String, Integer>> localScores;
    private SimpleListProperty<Pair<String, Integer>> remoteScores;
    private String recievedMessage;

    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     * @param game the game
     * @param gameWindow the game window
     */
    public ScoresScene(GameWindow gameWindow, Game game) {
        super(gameWindow);
        logger.info("Creating the scores scene");
        // Set up the observable list of scores
        ArrayList<Pair<String, Integer>> scores = new ArrayList<>();

        ObservableList<Pair<String, Integer>> observableList = FXCollections.observableArrayList(scores);
        ObservableList<Pair<String, Integer>> remoteobservableList = FXCollections.observableArrayList(scores);
        localScores = new SimpleListProperty<>(observableList);
        remoteScores = new SimpleListProperty<>(remoteobservableList);
        loadScores();


        checkNewHighScore(game.getScore());

    }


    @Override
    public void initialise() {
        logger.info("Initialising the Score screen");


    }

    @Override
    public void build() {


        logger.info("Building " + this.getClass().getName());
        logger.info("string message {}",recievedMessage);
        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());
        var scoresPane = new StackPane();
        scoresPane.getStyleClass().add("scores-background");
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
        gameWindow.getCommunicator().send("HISCORES DEFAULT");
        writeOnlineScore("Ross",10);


        gameWindow.getCommunicator().addListener(new CommunicationsListener() {

            @Override
            public void receiveCommunication(String communication) {
                logger.info("Listener called");
                loadOnlineScores(communication);


            }
        });


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

    /**
     * Load the scores from the scores file
     */
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

    /**
     * Write the scores to the scores file
     */
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

    /**
     * Prompt the user for their name
     * @return the name entered by the user
     */
    private String promptForName() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New High Score");
        dialog.setHeaderText("Congratulations! You achieved a new high score.");
        dialog.setContentText("Please enter your name:");

        Optional<String> result = dialog.showAndWait();
        return result.orElse("Anonymous");
    }


    /**
     * Load the online scores from the server
     * @param communication the communication received
     */
    private void loadOnlineScores(String communication){

        Platform.runLater(()->
        {String[] scores = communication.split("\n");
            remoteScores.clear();
            for (String line : scores) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String name = parts[0];
                    int score = Integer.parseInt(parts[1]);
                    logger.info("name:{} score{}",name,score);
                    remoteScores.add(new Pair<>(name, score));
                }
            }

        });

    }

    private void writeOnlineScore(String name, int score) {
        logger.info("writeOnlineScore  method called");
        String message = "HISCORE " + name + ":" + score;
        gameWindow.getCommunicator().send(message);
    }


}
