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

public abstract class Ghost {
    protected double x, y; // these represent center coordinates of ghost
    protected int targetX, targetY; // these represent the coordinates of the adjacent cell it is going towards
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

        x = 8*startC+4;
        targetX = 8*startC+4;
        y = 8*startR+4;
        targetY = 8*startR+4;

        facing = Direction.DOWN;
    }

    // this function is run every frame
    public void nextFrame() {
        drawingFrameCounter++;
        if (drawingFrameCounter >= 5) {
            drawingFrameCounter = 0;
            drawingFrameIndex = (drawingFrameIndex + 1) % 2;
        }
    }

    public void draw(Graphics2D g, ImageObserver observer) {
        // ImageObserver is somehow needed to not have the gif frozen at one frame
        g.drawImage(getCurrentSprite(), (int)x-8, (int)y-8, observer);
    }

    protected Image getCurrentSprite() {
        return ghostDirectionalSprites.get(facing)[drawingFrameIndex];
    }

    public void updatePosition(Maze maze) {
        double remainingDistanceToTravel = velocity;
        
        while (remainingDistanceToTravel > 0) {
            double oldX = x;
            double oldY = y;

            if (x < targetX) {
                x = Math.min(x+velocity, targetX);
                facing = Direction.RIGHT;
            }
            else if (x > targetX) {
                x = Math.max(x-velocity, targetX);
                facing = Direction.LEFT;
            }
            else if (y < targetY) {
                y = Math.min(y+velocity, targetY);
                facing = Direction.DOWN;
            }
            else if (y > targetY) {
                y = Math.max(y-velocity, targetY);
                facing = Direction.UP;
            }

            remainingDistanceToTravel -= Math.abs(oldX - x) + Math.abs(oldY - y);
            
            // if reached target coordinate, update to next target
            if (x == targetX && y == targetY) {
                if (chooseTarget(maze))
                    break;
            }
        }
    }

    // this function can be overrided by ghost subclasses
    // returning true means stop break out of loop
    // assumption for final target chosen is it must be on an "available" square, so that the ghost can resume normal pathing reaching it
    protected boolean chooseTarget(Maze maze) {
        if (state == State.NORMAL) {
            // convert x, y coordinates to row and column value of maze
            int r = ((int)y+3)/8;
            int c = ((int)x+3)/8;

            // TODO: Jonathan to implement player
            Direction direction = maze.shortestPath[r][c][24][2];
            if (direction == Direction.UP)
                targetY -= 8;
            else if (direction == Direction.DOWN)
                targetY += 8;
            else if (direction == Direction.LEFT)
                targetX -= 8;
            else if (direction == Direction.RIGHT)
                targetX += 8;
            else if (direction == Direction.STILL)
                return true;
        }
        else if (state == State.SCARED) {
            // TODO: Jonathan to implement player
        }
        return false;
    }
}