package uk.ac.soton.comp1206.component;

import javafx.animation.*;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Cell;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.Pair;


/**
 * The ScoresList class is used to display a list of scores in the game. It is a VBox that displays a list of scores
 * in the game.
 */
public class ScoresList extends VBox {

    /**
     * The list of scores
     */
  private final SimpleListProperty<Pair<String, Integer>> scores = new SimpleListProperty<>();

  /**
   * Create a new ScoresList
   */
  public ScoresList() {
    getStyleClass().add("scorelist");
    scoresProperty().addListener((observable, oldValue, newValue) -> updateScores(newValue));
  }

  /**
   * Get the scores property
   * @return the scores property
   */
  public SimpleListProperty<Pair<String, Integer>> scoresProperty() {
    return scores;
  }

    /**
     * Update the scores in the list
     * @param scoreList
     */
  private void updateScores(ObservableList<Pair<String, Integer>> scoreList) {
    getChildren().clear();
    for (Pair<String, Integer> score : scoreList) {
      Text scoreLabel = new Text(score.getKey() + ": " + score.getValue());
        scoreLabel.setTextAlignment(TextAlignment.CENTER);
        scoreLabel.getStyleClass().add("Hiscore");
        getChildren().add(scoreLabel);
    }
  }

  /**
   * Reveal the scores in the list with a fade in animation
   */
  public void reveal() {
    double delay = 0.0;
    for (Node child : getChildren()) {
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(2), child);
        fadeTransition.setFromValue(0.0); // Starting opacity
        fadeTransition.setToValue(1.0);   // Ending opacity
        fadeTransition.setDelay(Duration.seconds(delay));
        fadeTransition.playFromStart();
        delay += 2.0;
    }
}


}
