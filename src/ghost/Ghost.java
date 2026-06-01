package src.ghost;

import java.awt.*;
import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import src.Maze;
import src.Constants;
import src.Direction;
import src.MathUtils;
import src.Movable;

public abstract class Ghost extends Movable {
    // BASIC ASSUMPTION: A GHOST MUST REACH (TARGET_R, TARGET_C) BEFORE IT SWITCHES TO A NEW TARGET

    protected enum State {
        NORMAL, // normal chasing
        SCARED, // scared, running away from player
        WALL, // going through wall (teleport ghost)
        EGG, // in egg (phoenix ghost)
    }
    protected State state = State.NORMAL;

    public Ghost(String name, int startR, int startC) {
        super(startR, startC);
        DEBUG = true;

        try {
            BufferedImage sheet = ImageIO.read(new File("images/ghost/" + name + ".png"));
            for (int i = 0; i <= 3; i++) {
                directionalSprites.put(
                    Constants.DIRECTIONS[i],
                    new Image[] {sheet.getSubimage(0, 16*i, 16, 16), sheet.getSubimage(16, 16*i, 16, 16)}
                );
            }
        }
        catch (IOException e) {
            // no mercy
            System.exit(0);
        }
    }


    public void updatePosition(Maze maze) {
        double remainingDistanceToTravel = speed;
        
        while (MathUtils.greater(remainingDistanceToTravel, 0)) {
            double oldX = x;
            double oldY = y;

            if (previousC < targetC) {
                x = Math.min(x+remainingDistanceToTravel, 8*targetC);
                facing = Direction.RIGHT;
            }
            else if (targetC < previousC) {
                x = Math.max(x-remainingDistanceToTravel, 8*targetC);
                facing = Direction.LEFT;
            }
            else if (previousR < targetR) {
                y = Math.min(y+remainingDistanceToTravel, 8*targetR);
                facing = Direction.DOWN;
            }
            else if (targetR < previousR) {
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