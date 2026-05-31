package src.ghost;

import java.awt.*;
import java.io.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import javax.imageio.ImageIO;
import java.util.*;

import src.Maze;
import src.Constants;
import src.Direction;
import src.MathUtils;

public abstract class Ghost {
    private final boolean DEBUG = true;

    // BASIC ASSUMPTION: A GHOST MUST REACH (TARGETR, TARGETC) BEFORE IT SWITCHES TO A NEW TARGET
    protected double x, y; // these represent top left coordinates of ghost
    protected int previousR, previousC; // these store the coordinate of cell the ghost left
    protected int targetR, targetC; // these store the coordinates of the cell it is going towards
    protected double velocity = 1;

    protected enum State {
        NORMAL, // normal chasing
        SCARED, // scared, running away from player
        WALL, // going through wall (teleport ghost)
        EGG, // in egg (phoenix ghost)
    }
    protected State state = State.NORMAL;

    private final Map<Direction, Image[]> ghostDirectionalSprites;
    private Direction facing;
    private int drawingFrameIndex = 0;
    private int drawingFrameCounter = 0;

    public Ghost(String name, int startR, int startC) {
        ghostDirectionalSprites = new HashMap<>();
        try {
            BufferedImage sheet = ImageIO.read(new File("images/ghost/" + name + ".png"));
            for (int i = 0; i <= 3; i++) {
                ghostDirectionalSprites.put(
                    Constants.DIRECTIONS[i],
                    new Image[] {sheet.getSubimage(0, 16*i, 16, 16), sheet.getSubimage(16, 16*i, 16, 16)}
                );
            }
        }
        catch (IOException e) {
            // no mercy
            System.exit(0);
        }

        x = 8*startC;
        y = 8*startR;
        previousR = targetR = startR;
        previousC = targetC = startC;

        facing = Direction.DOWN;
    }

    // this function is run every frame
    public void nextFrame(Maze maze) {
        drawingFrameCounter++;
        if (drawingFrameCounter >= 5) {
            drawingFrameCounter = 0;
            drawingFrameIndex = (drawingFrameIndex + 1) % 2;
        }
    }

    public void draw(Graphics2D g, ImageObserver observer) {
        // ImageObserver is somehow needed to not have the gif frozen at one frame
        g.drawImage(getCurrentSprite(), (int)x-4, (int)y-4, observer);

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
        return ghostDirectionalSprites.get(facing)[drawingFrameIndex];
    }

    public void updatePosition(Maze maze) {
        double remainingDistanceToTravel = velocity;
        
        while (MathUtils.greater(remainingDistanceToTravel, 0)) {
            double oldX = x;
            double oldY = y;

            if (x < 8*targetC) {
                x = Math.min(x+remainingDistanceToTravel, 8*targetC);
                facing = Direction.RIGHT;
            }
            else if (x > 8*targetC) {
                x = Math.max(x-remainingDistanceToTravel, 8*targetC);
                facing = Direction.LEFT;
            }
            else if (y < 8*targetR) {
                y = Math.min(y+remainingDistanceToTravel, 8*targetR);
                facing = Direction.DOWN;
            }
            else if (y > 8*targetR) {
                y = Math.max(y-remainingDistanceToTravel, 8*targetR);
                facing = Direction.UP;
            }

            remainingDistanceToTravel -= Math.abs(oldX - x) + Math.abs(oldY - y);
            
            // if reached target coordinate, update to next target
            if (MathUtils.nearlyEqual(x, 8*targetC) && MathUtils.nearlyEqual(y, 8*targetR)) {
                if (chooseTarget(maze))
                    break;
            }
        }
    }

    // this function can be overrided by ghost subclasses
    // returning true means stop break out of loop
    // assumption for final target chosen is it must be on an "available" square, so that the ghost can resume normal pathing reaching it
    protected boolean chooseTarget(Maze maze) {
        previousR = targetR;
        previousC = targetC;

        if (state == State.NORMAL) {
            // TODO: Jonathan to implement player
            Direction direction = maze.shortestPath[previousR][previousC][24][2];
            if (direction == Direction.UP) targetR--;
            else if (direction == Direction.DOWN) targetR++;
            else if (direction == Direction.LEFT) targetC--;
            else if (direction == Direction.RIGHT) targetC++;
            else if (direction == Direction.STILL) return true;
        }
        else if (state == State.SCARED) {
            // TODO: Jonathan to implement player
        }
        return false;
    }
}