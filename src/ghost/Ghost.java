package src.ghost;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import src.*;

public abstract class Ghost extends Movable {
    // BASIC ASSUMPTION: A GHOST MUST REACH (TARGET_R, TARGET_C) BEFORE IT SWITCHES TO A NEW TARGET

    protected final int RESPAWN_FRAMES = 5 * Constants.FPS;

    protected State state;
    protected int respawnTimer;
    protected Map <Direction, Image[]> blankArray = new HashMap<>();
    protected final Map<Direction, Image[]> scaredDirectionalSprites = new HashMap<>();

    private boolean targetingRandomSquare = false;
    protected int longRangeTargetR, longRangeTargetC;

    public Ghost(String name, int startR, int startC) {
        super(name, startR, startC);
        DEBUG = false;
        this.name = name;

        // import ghost spritesheets
        try {
            BufferedImage sheet = ImageIO.read(new File("images/ghost/" + name + ".png"));
            for (int i = 0; i <= 3; i++) {
                directionalSprites.put(
                    Constants.DIRECTIONS[i],
                    new Image[] {sheet.getSubimage(0, 16*i, 16, 16), sheet.getSubimage(16, 16*i, 16, 16)}
                    // ghosts are always 16 by 16
                );
            }

            // scared spritesheet
            BufferedImage scaredSheet = ImageIO.read(new File("images/ghost/scared " + name + ".png"));
            for (int i = 0; i <= 3; i++) {
                scaredDirectionalSprites.put(
                    Constants.DIRECTIONS[i],
                    new Image[] {scaredSheet.getSubimage(0, 16*i, 16, 16), scaredSheet.getSubimage(16, 16*i, 16, 16)}
                );
            }

            // blank spritesheet for when the ghost is dead
            for(int i = 0; i<=3;i++){
                BufferedImage blank = new BufferedImage(16,16,BufferedImage.TYPE_INT_ARGB);
                blankArray.put(
                    Constants.DIRECTIONS[i],
                    new Image[] {blank, blank}
                );
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isScared() {
        // Description: This method returns whether the ghost is scared or not.
        // Parameters: None
        // Return: Boolean representing if the ghost is scared

        return state == State.SCARY;
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
        // Description: This method sets the state of the ghost to either DEAD, NORMAL, or SCARY. This method can then be overloaded for more specific states.
        // Parameters: Maze and player
        // Return: void

        // through code, state should only be updated in nextFrame

        if (Driver.gameTime() < respawnTimer)
            state = State.DEAD;
        else
            state = player.getState();
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

        int left = (int)player.getX()-2;
        int right = (int)player.getX()+10;
        int top = (int)player.getY()-2;
        int bottom = (int)player.getY()+10;
        int leftG = (int) x-2;
        int rightG = (int)x+10;
        int topG = (int)y-2;
        int bottomG = (int)y+10;

        if(((left<=rightG&&left>=leftG)||(right>=leftG&&right<=rightG))&&((top<=bottomG&&top>=topG)||(bottom>=topG&&bottom<=bottomG))){
            return true;
        }
        return false;
    }

    public void die() {
        // Description: This method kills the ghost and starts the timer for the ghost to respawn.
        // Parameters: The current timer
        // Return: void

        respawnTimer = Driver.gameTime() + RESPAWN_FRAMES;
        state = State.DEAD;
        Audio.playGhostKill();
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

        // if you reached the long range target,
        if (previousR == longRangeTargetR && targetR == longRangeTargetR && previousC == longRangeTargetC && targetC == longRangeTargetC) {
            targetingRandomSquare = false;
        }

        if (targetingRandomSquare) {
            // do nothing, keep on targeting that square
        }
        else if (Math.random() < 0.02) {
            // start targeting random square
            java.util.List<int[]> accessible = maze.getAllAccessibleCells();
            int randomIndex = (int) (Math.random() * accessible.size());
            int[] randomCell = accessible.get(randomIndex);
            longRangeTargetR = randomCell[0];
            longRangeTargetC = randomCell[1];
            targetingRandomSquare = true;
        }
        else {
            // target player
            int[] target = player.getCurrentPosition();
            longRangeTargetR = target[0];
            longRangeTargetC = target[1];
        }


        Direction direction = maze.getShortestPath()[previousR][previousC][longRangeTargetR][longRangeTargetC];
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

        // moves away from player to the square that maximizes distance
        int furthestR = -1;
        int furthestC = -1;
        int furthestDistance = -1;

        for (int i = 0; i <= 3; i++) {
            int testR = targetR + Constants.DELTA_R[i];
            int testC = targetC + Constants.DELTA_C[i];

            if (!maze.isAccessible(testR, testC))
                continue;

            int testDistance = Math.abs(playerR - testR) + Math.abs(playerC - testC);
            if (testDistance > furthestDistance) {
                furthestR = testR;
                furthestC = testC;
                furthestDistance = testDistance;
            }
        }

        if (furthestDistance == -1)
            return false;
            
        targetR = furthestR;
        targetC = furthestC;

        return true;
    }

    public void sendToMiddle() {
        // Description: This method sends the ghost back to the middle.
        // Parameters: None
        // Return: void

        previousR = 15;
        previousC = 14;
        x = 8*previousC;
        y = 8*previousR;
        targetR = 12;
        targetC = 14;
    }

    public void respawn() {
        // Description: This method respawns the ghost back to the middle.
        // Parameters: None
        // Return: void

        // trust ms. wong, this might seem like a useless method, but this can be overrided by ghost subclasses for custom respawn logic (for phoenix ghost, it respawns where it died)
        sendToMiddle();
    }

    public boolean getDead(){
        // Description: This method returns whether the ghost is dead or not.
        // Parameters: None
        // Return: Boolean representing if the ghost is dead

        return state == State.DEAD;
    }

    // getter for respawn timer
    public int getRespawnTimer() {
        return respawnTimer;
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