package uk.ac.soton.comp1206;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.game.Game;

public class Multimedia {
    private static final Logger logger = LogManager.getLogger(Multimedia.class);
    private MediaPlayer audioPlayer;
    private MediaPlayer musicPlayer;
    private static Media media;

  private static boolean soundeEnabled =true;

    /**
     *
     * @param filename
     */
  public void playAudio(String filename){
      try {
          logger.info("Trying to play Audio");
          var toPlay = Multimedia.class.getResource("/sounds/" + filename).toExternalForm();
          media = new Media(toPlay);
          audioPlayer = new MediaPlayer(media);
          audioPlayer.play();
      }
      catch (Exception e){
          e.printStackTrace();
          logger.error("Unable to play Audio" + filename );
          logger.error("Disabling Audio");
          soundeEnabled = false;
      }
  }

    /**
     *
      * @param filename
     */
  public void playMusic(String filename){
        try {
            logger.info("Trying to play Music"+ filename);
            var toPlay = Multimedia.class.getResource("/music/" + filename).toExternalForm();
            media = new Media(toPlay);
            musicPlayer = new MediaPlayer(media);
            musicPlayer.play();
            musicPlayer.setOnEndOfMedia(() -> musicPlayer.seek(Duration.ZERO));
        }
        catch (Exception e){
            e.printStackTrace();
            logger.error("Unable to play Music" + filename );
            logger.error("Disabling Music");
            soundeEnabled = false;
        }
    }



}

