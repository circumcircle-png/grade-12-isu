package src.ghost;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import src.*;

public abstract class Ghost extends Movable {
    // BASIC ASSUMPTION: A GHOST MUST REACH (TARGET_R, TARGET_C) BEFORE IT SWITCHES TO A NEW TARGET

    protected State state;
    protected int respawnTimer;
    protected Map <Direction, Image[]> blankArray = new HashMap<>();
    protected final Map<Direction, Image[]> scaredDirectionalSprites = new HashMap<>();
    protected final int RESPAWN_FRAMES = 5*Constants.FPS;
    public Ghost(String name, int startR, int startC) {
        super(name, startR, startC);
        DEBUG = true;
        this.name = name;
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
            for(int i = 0; i<=3;i++){
                BufferedImage blank = new BufferedImage(16,16,BufferedImage.TYPE_INT_ARGB);
                blankArray.put(
                    Constants.DIRECTIONS[i],
                    new Image[] {blank, blank}
                );
            }
        }
        catch (IOException e) {
            // no mercy
            System.exit(0);
        }
    }

    public void initSpeeds() {
        // Description: This method puts speeds into the speed map.
        // Parameters: None
        // Return: void

        speeds.put(State.NORMAL, 70);
        speeds.put(State.SCARY, 40);
        speeds.put(State.DEAD, 0);
    }

    public void nextFrame(Maze maze, Player player) {
        // Description: This method simulates one additional frame for the Ghost, and it always updates the state (even if the state remains the same).
        // Parameters: Maze and player
        // Return: void
        super.nextFrame();
        if (player.state == State.SCARY&&state!=State.DEAD)
            state = State.SCARY;
        else if(state!=State.DEAD)
            state = State.NORMAL;
    }

    protected Map<Direction, Image[]> getDirectionalSpriteMap() {
        // Description: This method returns the directional sprite map used to draw the ghost (purpose is to handle scary ghost graphics).
        // Parameters: None
        // Return: Map from Direction to an Image array (Image array represents gif)
        if(state == State.DEAD)
            return blankArray;
        if (state == State.SCARY)
            return scaredDirectionalSprites;
        return super.getDirectionalSpriteMap();
    }
    
    public boolean checkCollision(Player player){
        // Description: This method checks if the ghost collides with the player.
        // Parameters: Player
        // Return: Boolean representing whether the ghost collides with the player

        int left = (int)player.x-2;
        int right = (int)player.x+10;
        int top = (int)player.y-2;
        int bottom = (int)player.y+10;
        int leftG = (int) x-2;
        int rightG = (int)x+10;
        int topG = (int)y-2;
        int bottomG = (int)y+10;
        if(((left<=rightG&&left>=leftG)||(right>=leftG&&right<=rightG))&&((top<=bottomG&&top>=topG)||(bottom>=topG&&bottom<=bottomG))){
            return true;
        }
        return false;

    }
    public void die(int timer){
        respawnTimer = timer+RESPAWN_FRAMES;
        state = State.DEAD;

    }
    public void updatePosition(Maze maze, Player player) {
        // Description: This method updates the position of the ghost based on its state.
        // Parameters: Maze and player
        // Return: void

        double remainingDistanceToTravel = (double) speeds.get(state) / 60;
        
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

    protected boolean chooseTargetWhenNormal(Maze maze, Player player) {
        // Description: This method updates the target coordinate (targetR and targetC) to get the next step towards the player.
        // Parameters: Maze and player
        // Return: Boolean representing a target coordinate is selected, or the ghost will remain still

        int[] target = player.getCurrentPosition();
        int playerR = target[0];
        int playerC = target[1];

        Direction direction = maze.shortestPath[previousR][previousC][playerR][playerC];
        if (direction == Direction.UP) targetR--;
        else if (direction == Direction.DOWN) targetR++;
        else if (direction == Direction.LEFT) targetC--;
        else if (direction == Direction.RIGHT) targetC++;
        else if (direction == Direction.STILL) return false;

        return true;
    }

    protected boolean chooseTargetWhenScary(Maze maze, Player player) {
        // Description: This method updates the target coordinate (targetR and targetC) to get the next step away from the player.
        // Parameters: Maze and player
        // Return: Boolean representing a target coordinate is selected, or the ghost will remain still

        int[] target = player.getCurrentPosition();
        int playerR = target[0];
        int playerC = target[1];

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

        return true;
    }
    public void respawn(int timer, Player player){
        if(timer == respawnTimer){
            if(player.getScary())
                state = State.SCARY;
            else
                state=State.NORMAL;
            previousC = 22;
            previousR = 22;
            x = 8*previousR;
            y = 8*previousC;
            targetC=22;
            targetR=22;
        }
    }
    public boolean getDead(){
        if(state==State.DEAD)
            return true;
        return false;
    }
    protected boolean chooseTarget(Maze maze, Player player) throws UnsupportedOperationException {
        // Description: This method chooses the next target coordinate for the ghost.
        // Parameters: Maze and player
        // Return: Boolean representing a target coordinate is selected, or the ghost will remain still

        if (state == State.NORMAL)
            return chooseTargetWhenNormal(maze, player);
        else if (state == State.SCARY)
            return chooseTargetWhenScary(maze, player);

        String message = String.format(
            "Ghost subclass '%s' forgot to override the state '%s'",
            name, state
        );
        throw new UnsupportedOperationException(message);
    }
}