package src;

import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Player extends Movable {
    private final static int FRAMES_SCARY = 5 * Constants.FPS;
    private final static int FRAMES_INVICIBLE = 5 * Constants.FPS;
    private final static int FRAMES_SPEEDY = 5 * Constants.FPS;
    private final int TEMP_SPEED = 80;

    protected Direction nextFacing;
    private int scaryFrameTimer = 0;
    private int invincibleTimer = 0;
    private int speedTimer = 0;
    private int speed;
    private int score;
    protected final Map<Direction, Image[]> scaryDirectionalSprites = new HashMap<>();
    private int heartCount;

    public Player(int startR, int startC) {
        super("player", startR, startC);
        DEBUG = false;
        state = State.NORMAL;
        score = 0;
        heartCount = 5;

        // read spritesheets
        try {
            BufferedImage sheet = ImageIO.read(new File("images/pacman/pacman.png"));
            for (int i = 0; i <= 3; i++) {
                directionalSprites.put(
                        Constants.DIRECTIONS[i],
                        new Image[] { sheet.getSubimage(0, 16 * i, 16, 16), sheet.getSubimage(16, 16 * i, 16, 16) });
            }
            BufferedImage scarySheet = ImageIO.read(new File("images/pacman/scary pacman.png"));
            for (int i = 0; i <= 3; i++) {
                scaryDirectionalSprites.put(
                        Constants.DIRECTIONS[i],
                        new Image[] { scarySheet.getSubimage(0, 16 * i, 16, 16),
                                scarySheet.getSubimage(16, 16 * i, 16, 16) });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initSpeeds() {
        // Description: This method puts speeds into the speed map.
        // Parameters: None
        // Return: void

        speeds.put(State.NORMAL, 80);
        speeds.put(State.SCARY, 80);
    }

    public void nextFrame(Maze maze, int timer) {
        // Description: This method simulates one extra frame for the player.
        // Parameters: Maze and timer
        // Return: void

        super.nextFrame();
        String tile = maze.getTileType(previousR, previousC);

        // handle collision with pickups
        if (tile.equals("dot")) {
            maze.remove(previousR, previousC);
            score += 10;
            Audio.playPing();
        }
        if (tile.equals("bigDot")) {
            maze.remove(previousR, previousC);
            state = State.SCARY;
            scaryFrameTimer = FRAMES_SCARY + timer;
            score += 50;
            Audio.playPowerUp();
        }
        if (tile.equals("heart")) {
            maze.remove(previousR, previousC);
            gainHeart();
            score += 50;
            Audio.playPowerUp();
        }
        if (tile.equals("speed")) {
            maze.remove(previousR, previousC);
            speedTimer = timer + FRAMES_SPEEDY;
            score += 50;
            Audio.playPowerUp();
        }

        if (scaryFrameTimer == timer) {// if the scary timer has expired then turn the state to normal
            state = State.NORMAL;
        }
        if (speedTimer >= timer) {// speed powerups, temporary speed is added
            speed = speeds.get(state) + TEMP_SPEED;
        } else {
            speed = speeds.get(state);// use normal speed
        }
    }

    // getter for score
    public int getScore() {
        return score;
    }

    public void updateScore(int points){
        // Description: This method increases the score by some number of points.
        // Parameters: The number of points
        // Return: void

        score += points;
    }

    protected Map<Direction, Image[]> getDirectionalSpriteMap() {
        // Description: This method returns the sprite map to be used by getCurrentSprite.
        // Parameters: None
        // Return: The sprite map to be used

        if (state == State.SCARY)
            return scaryDirectionalSprites;
        return super.getDirectionalSpriteMap();
    }


    public void updatePosition(Maze maze) {
        // Description: This method updates the position of the ghost based on its state.
        // Parameters: Maze
        // Return: void
        if (nextFacing != facing) {// fix if you spam opposite directions so it automatically updates your next position
            if (nextFacing == Direction.UP && maze.isAccessible(targetR - 1, targetC)) {
                if (facing == Direction.DOWN) {
                    int tempC = previousC;
                    int tempR = previousR;
                    previousC = targetC;
                    previousR = targetR;
                    targetC = tempC;
                    targetR = tempR;
                }
                facing = nextFacing;
            } else if (nextFacing == Direction.LEFT && maze.isAccessible(targetR, targetC - 1)) {
                if (facing == Direction.RIGHT) {
                    int tempC = previousC;
                    int tempR = previousR;
                    previousC = targetC;
                    previousR = targetR;
                    targetC = tempC;
                    targetR = tempR;
                }
                facing = nextFacing;
            } else if (nextFacing == Direction.RIGHT && maze.isAccessible(targetR, targetC + 1)) {
                if (facing == Direction.LEFT) {
                    int tempC = previousC;
                    int tempR = previousR;
                    previousC = targetC;
                    previousR = targetR;
                    targetC = tempC;
                    targetR = tempR;
                }
                facing = nextFacing;
            } else if (nextFacing == Direction.DOWN && maze.isAccessible(targetR + 1, targetC)) {
                if (facing == Direction.UP) {
                    int tempC = previousC;
                    int tempR = previousR;
                    previousC = targetC;
                    previousR = targetR;
                    targetC = tempC;
                    targetR = tempR;
                }
                facing = nextFacing;
            }
        }

        double remainingDistanceToTravel = (double) speed / 60;
        while (MathUtils.greater(remainingDistanceToTravel, 0)) {// move the ghost x,y to next position
            double oldX = x;
            double oldY = y;
            if (previousC < targetC)
                x = Math.min(x + remainingDistanceToTravel, 8 * targetC);
            else if (targetC < previousC)
                x = Math.max(x - remainingDistanceToTravel, 8 * targetC);
            else if (previousR < targetR)
                y = Math.min(y + remainingDistanceToTravel, 8 * targetR);
            else if (targetR < previousR)
                y = Math.max(y - remainingDistanceToTravel, 8 * targetR);

            remainingDistanceToTravel -= Math.abs(oldX - x) + Math.abs(oldY - y);

            // change target
            if (MathUtils.nearlyEqual(x, 8 * targetC) && MathUtils.nearlyEqual(y, 8 * targetR)) {
                previousR = targetR;
                previousC = targetC;
                if (facing == Direction.UP && maze.isAccessible(targetR - 1, targetC))
                    targetR--;
                else if (facing == Direction.LEFT && maze.isAccessible(targetR, targetC - 1))
                    targetC--;
                else if (facing == Direction.RIGHT && maze.isAccessible(targetR, targetC + 1))
                    targetC++;
                else if (facing == Direction.DOWN && maze.isAccessible(targetR + 1, targetC))
                    targetR++;
                else
                    break;
            }
        }
    }

    // getter for current position
    public int[] getCurrentPosition() {
        return new int[] { targetR, targetC };
    }

    
    public void loseHeart(int timer) {
        //Description: when the player gets hit by a ghost, lose a heart
        //parameters time
        // return void
        if (invincibleTimer <= timer) {
            Audio.playDamage();
            heartCount--;
            invincibleTimer = FRAMES_INVICIBLE + timer;// invincible frames
        }
    }

    // getter for number of hearts
    public int getHearts() {
        return heartCount;
    }

    public void gainHeart() {
        // Description: This method gives the player an extra heart.
        // Parameters: None
        // Return: void

        heartCount++;
    }

    // getter for X coordinate
    public double getX(){
        return x;
    }

    // getter for Y coordinate
    public double getY() {
        return y;
    }

    // getter for state
    public State getState() {
        return state;
    }

    // getter for invincibility time 
    public int getInvincibilityTimer() {
        return invincibleTimer;
    }
}
