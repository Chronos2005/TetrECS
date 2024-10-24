package uk.ac.soton.comp1206.component;

/**
 * Leaderboard to store the scores of all the players in a multiplayer game
 */
public class LeaderBoard extends ScoresList{
        /**
         * Create a new leaderboard
         */
        public LeaderBoard() {
            super();
            getStyleClass().add("leaderboard");
        }
        /**
         * Reveal the leaderboard
         */
        @Override
        public void reveal() {
            super.reveal();
        }
}
