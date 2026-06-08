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
        SCARY, // scary, run away from player
    }

    protected State state;
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
        }
        catch (IOException e) {
            // no mercy
            System.exit(0);
        }
    }


    public void updatePosition(Maze maze) {
        System.out.println(facing + " " + targetR + " " + targetC);
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

    @Override
    public void keyTyped(KeyEvent e) {
        //throw new UnsupportedOperationException("Unimplemented method 'keyTyped'");
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int input = e.getKeyCode();
        if(input == KeyEvent.VK_W){
            facing = Direction.UP;
            //targetR -=1;
        }
        if(input == KeyEvent.VK_A){
            facing = Direction.LEFT;
            //targetC -= 1;
        }
        if(input == KeyEvent.VK_D){
            facing = Direction.RIGHT;
            //targetC += 1;
        }
        if(input == KeyEvent.VK_S){
            facing = Direction.DOWN;
            //targetR += 1;
        }

        
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //throw new UnsupportedOperationException("Unimplemented method 'keyReleased'");
    }
}
