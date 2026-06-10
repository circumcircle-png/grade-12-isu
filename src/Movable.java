package src;

import java.util.*;
import java.awt.*;

public abstract class Movable {
    protected boolean DEBUG = false;
    private final static int FRAMES_PER_DRAWING = Constants.FPS / 12;

    public String name = "NO NAME";

    public enum State {
        NORMAL,
        SCARY,
        EGG,
        BULL_STRAIGHT,
        BULL_TURN,
    }
    public State state;

    // map from state to speed (frames per second)
    protected Map<State, Integer> speeds = new HashMap<>();

    // these represent the top left coordinate of the CENTER 8x8 of the movable
    // therefore, get the top left coordinate of the 16x16 square the Movable seems to be centered in, do (x-4, y-4)
    public double x, y; 

    // these store the corodinates of the 8x8 cell the movable just left
    protected int previousR, previousC;

    // these store the coordinates of the 8x8 cell the movable is going towards
    protected int targetR, targetC;

    // BASIC ASSUMPTION: previousR, previousC, targetR, targtC must ALWAYS point to an accessible square

    protected final Map<Direction, Image[]> directionalSprites = new HashMap<>();
    protected Direction facing = Direction.DOWN;
    private int drawingFrameCounter = 0;

    public Movable(String name, int startR, int startC) {
        this.name = name;
        x = 8*startC;
        y = 8*startR;
        previousR = targetR = startR;
        previousC = targetC = startC;
        initSpeeds();
    }

    // this method adds to the speeds map
    public abstract void initSpeeds();

    public void nextFrame() {
        // Description: This method updates the drawing frame counter to display the GIF.
        // Parameters: None
        // Return: void

        drawingFrameCounter = (drawingFrameCounter + 1) % (2 * FRAMES_PER_DRAWING);
    }

    public void draw(Graphics2D g) {
        // Description: This method draws the Movable to the screen.
        // Parameters: The Graphics2D associated with the screen
        // Return: void

        g.drawImage(getCurrentSprite(), (int)x-4, (int)y-4, null);

        if (DEBUG) {
            g.setColor(Color.YELLOW);
            g.drawRect(8*previousC, 8*previousR, 8, 8);

            g.setColor(Color.BLUE);
            g.fillOval((int)x-2, (int)y-2, 4, 4);

            g.setColor(Color.GREEN);
            g.drawRect(8*targetC, 8*targetR, 8, 8);
        }
    }

    protected Image getCurrentSprite() {
        // Description: This method gets the sprite to be displayed based on the direction and the frame counter.
        // Parameters: None
        // Return: The sprite to de displayed

        return getDirectionalSpriteMap().get(facing)[drawingFrameCounter / FRAMES_PER_DRAWING];
    }

    protected Map<Direction, Image[]> getDirectionalSpriteMap() {
        // Description: This method returns the sprite map to be used by getCurrentSprite.
        // Parameters: None
        // Return: The sprite map to be used

        return directionalSprites;
    }
}
