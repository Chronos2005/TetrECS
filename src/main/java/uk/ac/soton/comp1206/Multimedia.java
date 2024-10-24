package uk.ac.soton.comp1206;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.game.Game;

/**
 * Handles the audio being played from files
 */
public class Multimedia {
    private static final Logger logger = LogManager.getLogger(Multimedia.class);
    /**
     * The audio player
     */
    private MediaPlayer audioPlayer;
    /**
     * The music player
     */
    private MediaPlayer musicPlayer;
    /**
     * The media object
     */
    private static Media media;
    /**
     * Whether sound is enabled
     */
  private static boolean soundeEnabled =true;

    /**
     * Plays the audio file
     * @param filename the file to play
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
     *plays the music file
     * @param filename the file to play
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

    /**
     * Stops the music
     */
    public void stopMusic() {
    if (musicPlayer != null) {
        musicPlayer.stop();
    }
}



}

