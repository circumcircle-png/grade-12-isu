package src.ghost;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import src.*;

public abstract class Ghost extends Movable {
    private final static int FRAMES_SCARED = 5 * Constants.FPS;
    private final static double NORMAL_SPEED = (double) 45 / Constants.FPS;
    private final static double SCARED_SPEED = (double) 12 / Constants.FPS;

    // BASIC ASSUMPTION: A GHOST MUST REACH (TARGET_R, TARGET_C) BEFORE IT SWITCHES TO A NEW TARGET

    public int scaredFrameTimer = 0;

    protected enum State {
        NORMAL, // normal chasing
        SCARED, // scared, running away from player
    }
    protected State state;

    protected final Map<Direction, Image[]> scaredDirectionalSprites = new HashMap<>();

    public Ghost(String name, int startR, int startC) {
        super(startR, startC);
        DEBUG = false;
        state = State.NORMAL;

        try {
            BufferedImage sheet = ImageIO.read(new File("images/ghost/" + name + ".png"));
            for (int i = 0; i <= 3; i++) {
                directionalSprites.put(
                    Constants.DIRECTIONS[i],
                    new Image[] {sheet.getSubimage(0, 16*i, 16, 16), sheet.getSubimage(16, 16*i, 16, 16)}
                    // ghosts are always 16 by 16
                );
            }

            BufferedImage scaredSheet = ImageIO.read(new File("images/ghost/scared " + name + ".png"));
            for (int i = 0; i <= 3; i++) {
                scaredDirectionalSprites.put(
                    Constants.DIRECTIONS[i],
                    new Image[] {scaredSheet.getSubimage(0, 16*i, 16, 16), scaredSheet.getSubimage(16, 16*i, 16, 16)}
                );
            }
        }
        catch (IOException e) {
            // no mercy
            System.exit(0);
        }
    }

    public void nextFrame(Maze maze, Player player) {
        super.nextFrame(maze);
        scaredFrameTimer = Math.max(scaredFrameTimer - 1, 0);
        if (state == State.NORMAL)
            speed = NORMAL_SPEED;
        else if (state == State.SCARED) {
            speed = SCARED_SPEED;
            if (scaredFrameTimer == 0)
                state = State.NORMAL;
        }
    }

    protected Map<Direction, Image[]> getDirectionalSpriteMap() {
        if (state == State.SCARED)
            return scaredDirectionalSprites;
        return super.getDirectionalSpriteMap();
    }

    public void setScared() {
        state = State.SCARED;
        scaredFrameTimer = FRAMES_SCARED;
    }


    public void updatePosition(Maze maze, Player player) {
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
                previousR = targetR;
                previousC = targetC;
                if (!chooseTarget(maze, player))
                    break;
            }
        }
    }

    // this function can be overrided by ghost subclasses
    // returning true means target is chosen, false means we choose not to select a target to break
    // assumption for final target chosen is it must be on an "available" square, so that the ghost can resume normal pathing reaching it
    protected boolean chooseTarget(Maze maze, Player player) {
        int[] target = player.getCurrentPosition();
        int playerR = target[0];
        int playerC = target[1];
        if (state == State.NORMAL) {
            Direction direction = maze.shortestPath[previousR][previousC][playerR][playerC];
            if (direction == Direction.UP) targetR--;
            else if (direction == Direction.DOWN) targetR++;
            else if (direction == Direction.LEFT) targetC--;
            else if (direction == Direction.RIGHT) targetC++;
            else if (direction == Direction.STILL) return false;
        }
        else if (state == State.SCARED) {
            // moves away from player based on position
            if (targetR < playerR && maze.isAccessible(targetR-1, targetC))
                targetR--;
            else if (playerR < targetR && maze.isAccessible(targetR+1, targetC))
                targetR++;
            else if (targetC < playerC && maze.isAccessible(targetR, targetC-1))
                targetC--;
            else if (playerC < targetC && maze.isAccessible(targetR, targetC+1))
                targetC++;
            else
                return false;
        }
        return true;
    }
}