package src;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.*;

import src.*;

public class Player extends Movable implements KeyListener{
    protected enum State {
        NORMAL, // normal chasing
        SCARED, // scared, running away from player
    }

    protected State state;
    public Player(int startR, int startC) {
        super(startR, startC);
        DEBUG = false;
        state = State.NORMAL;

        try {
            BufferedImage sheet = ImageIO.read(new File("images/pacman.png"));
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
        // TODO: Jonathan, write code
        // no
    }


    public void updatePosition(Maze maze) {
        // TODO: Jonathan, look at Ghost.updatePosition for inspiration
        double remainingDistanceToTravel = speed;
        if(facing == Direction.UP){
            targetR -= 1;
        }
        if(facing == Direction.LEFT){
            targetC -= 1;
        }
        if(facing == Direction.RIGHT){
            targetC +=1;
        }
        if(facing == Direction.DOWN){
            targetR += 1;
        }
        if(!maze.isAccessible(targetR, targetC)){
            targetC = previousC;
            targetR = previousR;
        }
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
        }
    }
    public int[] getCurrentPosition() {
        return new int[] {targetR, targetC};
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'keyTyped'");
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int input = e.getKeyCode();
        if(input == KeyEvent.VK_W){
            facing = Direction.UP;
        }
        if(input == KeyEvent.VK_A){
            facing = Direction.LEFT;
        }
        if(input == KeyEvent.VK_S){
            facing = Direction.RIGHT;
        }
        if(input == KeyEvent.VK_D){
            facing = Direction.DOWN;
        }

        
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'keyReleased'");
    }
}
