package uk.ac.soton.comp1206.component;

import javafx.animation.TranslateTransition;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Cell;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
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
      Label scoreLabel = new Label(score.getKey() + ": " + score.getValue());
      getChildren().add(scoreLabel);
    }
  }

  public void reveal() {
    TranslateTransition transition = new TranslateTransition(Duration.millis(500), this);
    transition.setFromY(-getHeight());
    transition.setToY(0);
    transition.play();
  }

  public void hide() {
    TranslateTransition transition = new TranslateTransition(Duration.millis(500), this);
    transition.setFromY(0);
    transition.setToY(-getHeight());
    transition.play();
  }

  public void showAnimation(Node... nodes) {
    for (Node node : nodes) {
      node.setOpacity(0);
      TranslateTransition transition = new TranslateTransition(Duration.millis(500), node);
      transition.setFromY(-node.getLayoutY());
      transition.setToY(0);
      transition.setOnFinished(event -> node.setOpacity(1));
      transition.play();
    }
  }
}
