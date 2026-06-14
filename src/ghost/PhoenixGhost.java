package src.ghost;

import java.awt.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import src.*;

public class PhoenixGhost extends Ghost {
    public final static int FRAMES_PER_PHASE = 1 * Constants.FPS;

    private final Image[] eggSprites = new Image[3];

    public PhoenixGhost(int startR, int startC) {
        super("phoenix", startR, startC);
        try {
            BufferedImage sheet = ImageIO.read(new File("images/ghost/egg.png"));
            for (int i = 0; i <= 2; i++)
                eggSprites[i] = sheet.getSubimage(0, 16*i, 16, 16);
        }
        catch (IOException e) {
            System.exit(0);
        }
    }

    public void initSpeeds() {
        // Description: This method puts speeds into the speed map.
        // Parameters: None
        // Return: void

        super.initSpeeds();
        speeds.put(State.DEAD, 0);
    }

    public void nextFrame(Maze maze, Player player) {
        // Description: This method simulates one additional frame for the Ghost, and it always updates the state (even if the state remains the same).
        // Parameters: Maze and player
        // Return: void

        if (Driver.gameTime() < respawnTimer)
            state = State.DEAD;
        else
            super.nextFrame(maze, player);
    }

    protected Image getCurrentSprite() {
        // Description: This method gets the sprite to be displayed based on the whether the ghost is an egg, the direction, and the frame counter.
        // Parameters: None
        // Return: The sprite to be displayed

        if (state == State.DEAD) {
            int index = 2 - (respawnTimer - Driver.gameTime()) / FRAMES_PER_PHASE;
            return eggSprites[index];
        }
        return super.getCurrentSprite();
    }

    protected boolean chooseTarget(Maze maze, Player player) {
        // Description: This method chooses the next target coordinate for the ghost.
        // Parameters: Maze and player
        // Return: Boolean representing a target coordinate is selected, or the ghost will remain still
        
        if (state == State.DEAD)
            return false;
        return super.chooseTarget(maze, player);
    }

    public void die() {
        // Description: This method kills the phoenix ghost and starts the timer for the egg to respawn.
        // Parameters: The current timer
        // Return: void

        respawnTimer = Driver.gameTime() + 3 * FRAMES_PER_PHASE - 1;
    }

    public void respawn() {
        // Description: This method overloads the respawn method so that nothing happens. The PhoenixGhost will just come back to life from where it died.
        // Parameters: Player
        // Return: Boolean representing if the ghost is dead

        return;
    }
}
