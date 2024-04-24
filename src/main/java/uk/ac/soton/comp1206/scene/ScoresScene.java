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
import javafx.scene.text.Text;
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


public class ScoresScene extends BaseScene  {

    private static final Logger logger = LogManager.getLogger(ScoresScene.class);
    private ScoresList scoresList;
    private ScoresList remoteScoresList;

    private SimpleListProperty<Pair<String, Integer>> localScores;
    private SimpleListProperty<Pair<String, Integer>> remoteScores;
    private String recievedMessage;
    private String name;
    private Game game;

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
        this.game = game;




    }


    @Override
    public void initialise() {
        logger.info("Initialising the Score screen");
        checkNewHighScore(game.getScore());


    }

    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());
        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());
        BorderPane scoresPane = new BorderPane();
        scoresPane.getStyleClass().add("scores-background");
        Text Title = new Text("High Scores");
        Title.getStyleClass().add("title");
        scoresPane.setTop(Title);
        BorderPane.setAlignment(Title, Pos.TOP_CENTER);
        VBox localScoresBox = new VBox();
        Text localScoreLabel = new Text("Local Scores");
        localScoreLabel.getStyleClass().add("scores-label");

        // Create ScoresList for local score
        scoresList= new ScoresList();
        scoresList.scoresProperty().bindBidirectional(localScores);

        localScoresBox.getChildren().addAll(localScoreLabel, scoresList);
        // Create ScoresList for remote scores
        remoteScoresList = new ScoresList();
        remoteScoresList.scoresProperty().bindBidirectional(remoteScores);
        VBox remoteScoresBox = new VBox();
        Text remoteScoreLabel = new Text("Online Scores");
        remoteScoreLabel.getStyleClass().add("scores-label");
        remoteScoresBox.getChildren().addAll(remoteScoreLabel, remoteScoresList);
        HBox scoresBox = new HBox();
        scoresBox.getChildren().addAll(localScoresBox, remoteScoresBox);
        scoresBox.setSpacing(20);
        scoresBox.setAlignment(Pos.CENTER);
        scoresPane.setCenter(scoresBox);
        root.getChildren().add(scoresPane);
        gameWindow.getCommunicator().send("HISCORES DEFAULT");

        gameWindow.getCommunicator().addListener(new CommunicationsListener() {

            @Override
            public void receiveCommunication(String communication) {
                logger.info("Listener called");
                Platform.runLater(() -> {
                    recievedMessage = communication;
                    logger.info("recievedMessage:{}",recievedMessage);
                    loadOnlineScores(communication);
                    writeOnlineScore(communication);
                });



            }
        });
        scoresList.reveal();
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
                 name = promptForName();
                int insertIndex = localScores.size();
                for (int i = 0; i < localScores.size(); i++) {
                    if (playerScore > localScores.get(i).getValue()) {
                        insertIndex = i;
                        break;
                    }
                }

                localScores.add(insertIndex, new Pair<>(name, playerScore));
                while (localScores.size() > 10) {
                    localScores.remove(localScores.size() - 1);
                }
                writeScores();
            }
        } else {
            name = promptForName();
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
        name = result.orElse("Anonymous");
        gameWindow.getCommunicator().send("HISCORE " + name + ":" + game.getScore());
        return name;
    }


    /**
     * Load the online scores from the server
     * @param communication the communication received
     */
    private void loadOnlineScores(String communication){
        if (communication.startsWith("HISCORES")){
            communication = communication.trim().substring(communication.indexOf(" ")+1);
            String[] scores = communication.split("\n");
            remoteScores.clear();
            for (String line : scores) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String name = parts[0];
                    int score = Integer.parseInt(parts[1]);
                    remoteScores.add(new Pair<>(name, score));
                }
            }
            remoteScoresList.reveal();
        }

    }

    private void writeOnlineScore(String communication) {
        logger.info("writeOnlineScore  method called");
        if (communication.startsWith("NEWSCORE")) {
            communication = communication.trim().substring(communication.indexOf(" ")+1);
            String[] parts = communication.split(":");
            String name = parts[0];
            int score = Integer.parseInt(parts[1]);
            int insertIndex = remoteScores.size();
            for (int i = 0; i < remoteScores.size(); i++) {
                if (score > remoteScores.get(i).getValue()) {
                    insertIndex = i;
                    break;
                }
            }
            Pair<String, Integer> newPair = new Pair<>(name, score);

            remoteScores.add(insertIndex, newPair);

            logger.info("name:{} score:{}",name,score);
            remoteScores.remove(remoteScores.size()-1);
            remoteScoresList.reveal();




        }

    }


}
