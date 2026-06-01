package src;

import java.util.*;
import java.awt.*;

public abstract class Movable {
    protected boolean DEBUG = false;

    protected double x, y; // these represent top left coordinates of ghost
    protected int previousR, previousC; // these store the coordinate of cell the ghost left
    protected int targetR, targetC; // these store the coordinates of the cell it is going towards
    protected double speed = 1;

    protected final Map<Direction, Image[]> directionalSprites = new HashMap<>();
    protected Direction facing = Direction.DOWN;
    private int drawingFrameIndex = 0;
    private int drawingFrameCounter = 0;

    public Movable(int startR, int startC) {
        x = 8*startC;
        y = 8*startR;
        previousR = targetR = startR;
        previousC = targetC = startC;
    }

    // this function is run every frame
    public void nextFrame(Maze maze) {
        drawingFrameCounter++;
        if (drawingFrameCounter >= 5) {
            drawingFrameCounter = 0;
            drawingFrameIndex = (drawingFrameIndex + 1) % 2;
        }
    }

    public void draw(Graphics2D g) {
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
        return directionalSprites.get(facing)[drawingFrameIndex];
    }
}
