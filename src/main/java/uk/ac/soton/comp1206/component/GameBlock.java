package uk.ac.soton.comp1206.component;

import javafx.animation.AnimationTimer;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Visual User Interface component representing a single block in the grid.
 *
 * Extends Canvas and is responsible for drawing itself.
 *
 * Displays an empty square (when the value is 0) or a coloured square depending on value.
 *
 * The GameBlock value should be bound to a corresponding block in the Grid model.
 */
public class GameBlock extends Canvas {

    private static final Logger logger = LogManager.getLogger(GameBlock.class);

    /**
     * The set of colours for different pieces
     */
    public static final Color[] COLOURS = {
            Color.TRANSPARENT,
            Color.DEEPPINK,
            Color.RED,
            Color.ORANGE,
            Color.YELLOW,
            Color.YELLOWGREEN,
            Color.LIME,
            Color.GREEN,
            Color.DARKGREEN,
            Color.DARKTURQUOISE,
            Color.DEEPSKYBLUE,
            Color.AQUA,
            Color.AQUAMARINE,
            Color.BLUE,
            Color.MEDIUMPURPLE,
            Color.PURPLE
    };

    private final GameBoard gameBoard;
    private AnimationTimer fadeOutTimer;  // To handle the fade out animation

    private final double width;
    private final double height;
    private boolean isHovered = false;

    /**
     * The column this block exists as in the grid
     */
    private final int x;

    /**
     * The row this block exists as in the grid
     */
    private final int y;

    /**
     * The value of this block (0 = empty, otherwise specifies the colour to render as)
     */
    private final IntegerProperty value = new SimpleIntegerProperty(0);
    private static final int FADE_DURATION_MILLIS = 500; // Duration of the fade-out animation in milliseconds
    private static final int FADE_STEPS = 20; // Number of steps in the animation

    /**
     * Create a new single Game Block
     * @param gameBoard the board this block belongs to
     * @param x the column the block exists in
     * @param y the row the block exists in
     * @param width the width of the canvas to render
     * @param height the height of the canvas to render
     */
    public GameBlock(GameBoard gameBoard, int x, int y, double width, double height) {
        this.gameBoard = gameBoard;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;

        //A canvas needs a fixed width and height
        setWidth(width);
        setHeight(height);

        //Do an initial paint
        paint();

        //When the value property is updated, call the internal updateValue method
        value.addListener(this::updateValue);
        // Add mouse enter and exit handlers
        setOnMouseEntered(event -> {
            isHovered = true;
            paint(); // Repaint when mouse hovers over
        });

        setOnMouseExited(event -> {
            isHovered = false;
            paint(); // Repaint when mouse leaves
        });
    }

    /**
     * When the value of this block is updated,
     * @param observable what was updated
     * @param oldValue the old value
     * @param newValue the new value
     */
    private void updateValue(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        paint();
    }

    /**
     * Handle painting of the block canvas
     */
    public void paint() {
        if (isHovered&&value.get()==0) {
            paintHovered();
        } else {
            // Call either paintEmpty or paintColor as before
            if (value.get() == 0) {
                paintEmpty();
            } else {
                paintColor(COLOURS[value.get()]);
            }
        }

    }

    /**
     * Paint this canvas empty
     */
    private void paintEmpty() {
        var gc = getGraphicsContext2D();
        gc.clearRect(0, 0, width, height);

        // Draw rounded rectangle for empty tile
        gc.setFill(Color.rgb(225, 225, 225,0.4)); // Light gray color
        gc.fillRoundRect(2, 2, width - 4, height - 4, 8, 8); // Rounded rectangle with radius 8

        // Draw border
        gc.setStroke(Color.rgb(200, 200, 200)); // Slightly darker gray for border
        gc.strokeRoundRect(2, 2, width - 4, height - 4, 8, 8);


    }

    /**
     * Paint this canvas with the given colour
     * @param colour the colour to paint
     */
    private void paintColor(Paint colour) {
        var gc = getGraphicsContext2D();
        // Clear
        gc.clearRect(0, 0, width, height);

        // Draw filled rounded rectangle
        gc.setFill(colour);
        gc.fillRoundRect(2, 2, width - 4, height - 4, 8, 8); // Rounded rectangle with radius 8

        // Draw border
        gc.setStroke(Color.rgb(150, 150, 150)); // Slightly darker gray for border
        gc.strokeRoundRect(2, 2, width - 4, height - 4, 8, 8);

    }

    /**
     * Get the column of this block
     * @return column number
     */
    public int getX() {
        return x;
    }

    /**
     * Get the row of this block
     * @return row number
     */
    public int getY() {
        return y;
    }

    /**
     * Get the current value held by this block, representing it's colour
     * @return value
     */
    public int getValue() {
        return this.value.get();
    }

    /**
     * Bind the value of this block to another property. Used to link the visual block to a corresponding block in the Grid.
     * @param input property to bind the value to
     */
    public void bind(ObservableValue<? extends Number> input) {
        value.bind(input);
    }

    public void fadeOut() {
    // Stop a fade-out animation in progress (if any)
    if (fadeOutTimer != null) {
        fadeOutTimer.stop();
    }

    // Store original color
    final Color originalColor = Color.rgb(225, 225, 225,0.4);

    // Create a new AnimationTimer
    fadeOutTimer = new AnimationTimer() {
        private long startTime = System.currentTimeMillis();
        private double opacity = 1.0;

        @Override
        public void handle(long currentNanoTime) {
            double elapsedMillis = System.currentTimeMillis() - startTime;
            double progress = Math.min(elapsedMillis / FADE_DURATION_MILLIS, 1.0);

            // Update opacity based on progress
            opacity = 1.0 - progress;

            // Paint with updated opacity
            if (opacity > 0.5) {
                // For the first half of the animation, show green color
                paintColor(Color.rgb(0, 255, 0, opacity));
            } else {
                // For the second half of the animation, fade out to the original color
                paintColor(new Color(originalColor.getRed(), originalColor.getGreen(), originalColor.getBlue(), opacity * 2));
            }

            // Stop the animation when it completes
            if (progress >= 1.0) {
                stop();
                fadeOutTimer = null;
                paintEmpty(); // Paint the block as empty after the animation is complete
            }
        }
    };

    fadeOutTimer.start();
}

    private void paintHovered() {
        var gc = getGraphicsContext2D();
        gc.clearRect(0, 0, width, height);

        // ... Fill with regular block color ...
        // ... Example: (Call paintEmpty or paintColor here) ...

        // Draw a highlight border
        gc.setStroke(Color.YELLOW);

        gc.strokeRoundRect(2, 2, width - 4, height - 4, 8, 8);
    }





}


