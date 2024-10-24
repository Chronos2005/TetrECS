package uk.ac.soton.comp1206.scene;

import javafx.beans.property.SimpleListProperty;
import javafx.geometry.Pos;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.LeaderBoard;
import uk.ac.soton.comp1206.event.CommunicationsListener;
import uk.ac.soton.comp1206.game.MultiplayerGame;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The Multiplayer Scene is a scene that displays the multiplayer game
 */
public class MultiplayerScene extends ChallengeScene {
    /**
     * The game object
     */
    protected MultiplayerGame multiplayergame;
    private static final Logger logger = LogManager.getLogger(MultiplayerScene.class);
    /**
     * Create a new Multiplayer Player challenge scene
     *
     * @param gameWindow the Game Window
     */
    public MultiplayerScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Multiplayer Scene");
    }

    private LeaderBoard leaderBoard;

    @Override
    public void build() {
        super.build();
        logger.info("Building Multiplayer Scene");
        Text Title = new Text("Multiplayer Match");
        Title.getStyleClass().add("title");
        mainPane.setTop(Title);
        BorderPane.setAlignment(Title, Pos.CENTER);



    }

    private void addLeaderBoard(String Communication) {
        gameWindow.getCommunicator().send("SCORES"+ game.getScore());
        leaderBoard = new LeaderBoard();
        mainPane.setLeft(leaderBoard);
        BorderPane.setAlignment(leaderBoard, Pos.CENTER);
    }





}
