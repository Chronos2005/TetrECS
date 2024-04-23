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


/*
public class ScoresList extends VBox {
  private SimpleListProperty<Pair<String,Integer>> scoresList;
  public ScoresList() {




  }

  public ObservableList<Pair<String, Integer>> getScoresList() {
    return scoresList.get();
  }
}

 */
public class ScoresList extends VBox {

  private final SimpleListProperty<Pair<String, Integer>> scores = new SimpleListProperty<>();

  public ScoresList() {
    getStyleClass().add("scorelist");
    scoresProperty().addListener((observable, oldValue, newValue) -> updateScores(newValue));
  }

  public SimpleListProperty<Pair<String, Integer>> scoresProperty() {
    return scores;
  }

  private void updateScores(ObservableList<Pair<String, Integer>> scoreList) {
    getChildren().clear();
    for (Pair<String, Integer> score : scoreList) {
      Text scoreLabel = new Text(score.getKey() + ": " + score.getValue());
        scoreLabel.setTextAlignment(TextAlignment.CENTER);
        scoreLabel.getStyleClass().add("Hiscore");
        getChildren().add(scoreLabel);
    }
  }

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
