package src;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.*;

import src.*;
import src.ghost.Ghost;

public class Player extends Movable implements KeyListener{
    protected enum State {
        NORMAL, // normal chasing
        SCARY, // scary, run away from player
        INVINCIBLE,// invincibility frames
    }
    protected State state;
    protected Direction nextFacing;
    private final static int FRAMES_SCARY = 5* Constants.FPS;
    public int scaryFrameTimer = 0;
    private final static int FRAMES_INVICIBLE = 3*Constants.FPS;
    private int invicibleTimer = 0;
    protected final Map<Direction, Image[]> scaryDirectionalSprites = new HashMap<>();
    private int heartCount=3;
    public Player(int startR, int startC) {
        super(startR, startC);
        DEBUG = false;
        state = State.NORMAL;

        try {
            BufferedImage sheet = ImageIO.read(new File("images/pacman/pacman.png"));
            for (int i = 0; i <= 3; i++) {
                directionalSprites.put(
                    Constants.DIRECTIONS[i],
                    new Image[] {sheet.getSubimage(0, 16*i, 16, 16), sheet.getSubimage(16, 16*i, 16, 16)}
                );
            }
         BufferedImage scarySheet = ImageIO.read(new File("images/pacman/scary pacman.png"));
            for (int i = 0; i <= 3; i++) {
                scaryDirectionalSprites.put(
                    Constants.DIRECTIONS[i],
                    new Image[] {scarySheet.getSubimage(0, 16*i, 16, 16), scarySheet.getSubimage(16, 16*i, 16, 16)}
                );
            }
        }
        catch (IOException e) {
            // no mercy
            System.exit(0);
        }
    }

    public void nextFrame(Maze maze){
        super.nextFrame(maze);
        scaryFrameTimer = Math.max(scaryFrameTimer - 1, 0);
        invicibleTimer = Math.max(invicibleTimer-1, 0);
        String tile = maze.getTileType(previousR, previousC);
        if(tile.equals("dot")){
            maze.remove(previousR,previousC);
        }
        if(tile.equals("bigDot")){
            maze.remove(previousR, previousC);
            state = State.SCARY;
            scaryFrameTimer = FRAMES_SCARY;
        }
        if(scaryFrameTimer == 0){
            state = State.NORMAL;
        }
    }
    protected Map<Direction, Image[]> getDirectionalSpriteMap() {
        if (state == State.SCARY)
            return scaryDirectionalSprites;
        return super.getDirectionalSpriteMap();
    }
    public void updatePosition(Maze maze) {
        if(nextFacing != facing){
            if(nextFacing == Direction.UP && maze.isAccessible(targetR-1, targetC)){
                if(facing == Direction.DOWN){
                    int tempC = previousC;
                    int tempR = previousR;
                    previousC = targetC;
                    previousR= targetR;
                    targetC = tempC;
                    targetR = tempR;
                    
                }
                facing = nextFacing;
            }
            else if(nextFacing == Direction.LEFT && maze.isAccessible(targetR, targetC-1)){
                if(facing == Direction.RIGHT){
                    int tempC = previousC;
                    int tempR = previousR;
                    previousC = targetC;
                    previousR= targetR;
                    targetC = tempC;
                    targetR = tempR;
                }
                facing = nextFacing;
            }
            else if(nextFacing == Direction.RIGHT && maze.isAccessible(targetR, targetC+1)){
                if(facing == Direction.LEFT){
                    int tempC = previousC;
                    int tempR = previousR;
                    previousC = targetC;
                    previousR= targetR;
                    targetC = tempC;
                    targetR = tempR;
                }
                facing = nextFacing;
            }
            else if(nextFacing == Direction.DOWN && maze.isAccessible(targetR+1, targetC)){
                if(facing == Direction.UP){
                    int tempC = previousC;
                    int tempR = previousR;
                    previousC = targetC;
                    previousR= targetR;
                    targetC = tempC;
                    targetR = tempR;
                }
                facing = nextFacing;
            }
        }
        double remainingDistanceToTravel = speed;
        while (MathUtils.greater(remainingDistanceToTravel, 0)) {
            double oldX = x;
            double oldY = y;
            if (previousC < targetC) {
                x = Math.min(x+remainingDistanceToTravel, 8*targetC);
               // facing = Direction.RIGHT;
            }
            else if (targetC < previousC) {
                x = Math.max(x-remainingDistanceToTravel, 8*targetC);
              //  facing = Direction.LEFT;
            }
            else if (previousR < targetR) {
                y = Math.min(y+remainingDistanceToTravel, 8*targetR);
              //  facing = Direction.DOWN;
            }
            else if (targetR < previousR) {
                y = Math.max(y-remainingDistanceToTravel, 8*targetR);
               // facing = Direction.UP;
            }

            remainingDistanceToTravel -= Math.abs(oldX - x) + Math.abs(oldY - y);

            // change target
            if (MathUtils.nearlyEqual(x, 8*targetC) && MathUtils.nearlyEqual(y, 8*targetR)) {
                previousR = targetR;
                previousC = targetC;
                if(facing == Direction.UP && maze.isAccessible(targetR-1, targetC)){
                    targetR -= 1;
                }
                else if(facing == Direction.LEFT && maze.isAccessible(targetR, targetC-1)){
                    targetC -= 1;
                }
                else if(facing == Direction.RIGHT && maze.isAccessible(targetR, targetC+1)){
                    targetC +=1;
                }
                else if(facing == Direction.DOWN && maze.isAccessible(targetR+1, targetC)){
                    targetR += 1;
                }
                else {
                    break;
                }
            }
        }
    }
    public int[] getCurrentPosition() {
        return new int[] {targetR, targetC};
    }
    public int getHearts(){
        return heartCount;
    }
    public void loseHeart(){
        if(invicibleTimer == 0){
            heartCount-=1;
            System.out.println(heartCount);
            invicibleTimer = FRAMES_INVICIBLE;
            
        }
    }
    public void gainHeart(){
        heartCount++;
    }
    @Override
    public void keyTyped(KeyEvent e) {
        //throw new UnsupportedOperationException("Unimplemented method 'keyTyped'");
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int input = e.getKeyCode();
        if(input == KeyEvent.VK_W||input==KeyEvent.VK_UP){
            nextFacing = Direction.UP;
            //targetR -=1;
        }
        
        if(input == KeyEvent.VK_A||input==KeyEvent.VK_LEFT){
            nextFacing = Direction.LEFT;
            //targetC -= 1;
        }
        if(input == KeyEvent.VK_D||input==KeyEvent.VK_RIGHT){
            nextFacing = Direction.RIGHT;
            //targetC += 1;
        }
        if(input == KeyEvent.VK_S||input==KeyEvent.VK_DOWN){
            nextFacing = Direction.DOWN;
            //targetR += 1;
        }

        
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //throw new UnsupportedOperationException("Unimplemented method 'keyReleased'");
    }
    public boolean getScary(){
        if(state == State.SCARY)
            return true;
        return false;
    }
}
