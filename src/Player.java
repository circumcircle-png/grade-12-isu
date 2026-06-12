package src;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Player extends Movable implements KeyListener {
    private final static int FRAMES_SCARY = 5 * Constants.FPS;
    private final static int FRAMES_INVICIBLE = 3 * Constants.FPS;
    private final static int FRAMES_SPEEDY = 5 * Constants.FPS;
    private final int TEMP_SPEED = 80;
    protected Direction nextFacing;
    public int scaryFrameTimer = 0;
    private int invicibleTimer = 0;
    private int speedTimer = 0;
    private int speed;
    private int score;
    protected final Map<Direction, Image[]> scaryDirectionalSprites = new HashMap<>();
    private int heartCount = 3;

    public Player(int startR, int startC) {
        super("player", startR, startC);
        DEBUG = false;
        state = State.NORMAL;
        score = 0;
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
            // no mercy
            System.exit(0);
        }
    }

    public void initSpeeds() {
        speeds.put(State.NORMAL, 80);
        speeds.put(State.SCARY, 80);
    }

    public void nextFrame(Maze maze,int timer) {
        super.nextFrame();
        String tile = maze.getTileType(previousR, previousC);
        if (tile.equals("dot")) {
            maze.remove(previousR, previousC);
            score+=10;
        }
        if (tile.equals("bigDot")) {
            maze.remove(previousR, previousC);
            state = State.SCARY;
            scaryFrameTimer = FRAMES_SCARY+timer;
            score+=50;
        }
        if(tile.equals("heart")){
            maze.remove(previousR,previousC);
            gainHeart();
            score+=50;
        }
        if(tile.equals("speed")){
            maze.remove(previousR,previousC);//add the speed
            speedTimer = timer+speedTimer;
            score+=50;
        }
        if (scaryFrameTimer == timer) {
            state = State.NORMAL;
        }
        if(speedTimer>=timer){
            speed =speeds.get(state)+TEMP_SPEED;
        }else{
            speed = speeds.get(state);
        }
    }
    public int getScore(){
        return score;
    }
    protected Map<Direction, Image[]> getDirectionalSpriteMap() {
        if (state == State.SCARY)
            return scaryDirectionalSprites;
        return super.getDirectionalSpriteMap();
    }

    public void updatePosition(Maze maze) {
        if (nextFacing != facing) {
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
        while (MathUtils.greater(remainingDistanceToTravel, 0)) {
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

    public int[] getCurrentPosition() {
        return new int[] { targetR, targetC };
    }

    public int getHearts() {
        return heartCount;
    }
    
    public void loseHeart(int timer) {
        if (invicibleTimer <= timer) {
            heartCount--;
            invicibleTimer = FRAMES_INVICIBLE+timer;
        }
    }

    public void gainHeart() {
        heartCount++;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // throw new UnsupportedOperationException("Unimplemented method 'keyTyped'");
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int input = e.getKeyCode();
        if (input == KeyEvent.VK_W || input == KeyEvent.VK_UP)
            nextFacing = Direction.UP;
        else if (input == KeyEvent.VK_A || input == KeyEvent.VK_LEFT)
            nextFacing = Direction.LEFT;
        else if (input == KeyEvent.VK_D || input == KeyEvent.VK_RIGHT)
            nextFacing = Direction.RIGHT;
        else if (input == KeyEvent.VK_S || input == KeyEvent.VK_DOWN)
            nextFacing = Direction.DOWN;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // throw new UnsupportedOperationException("Unimplemented method
        // 'keyReleased'");
    }

    public boolean getScary() {
        if (state == State.SCARY)
            return true;
        return false;
    }
}
