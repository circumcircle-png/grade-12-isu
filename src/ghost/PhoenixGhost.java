package src.ghost;

import java.awt.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import src.*;

public class PhoenixGhost extends Ghost {
    public final static int FRAMES_PER_PHASE = 60;

    private int eggFrameCounter = 0;
    private final Image[] eggSprites = new Image[3];

    private boolean isEgg = false;

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

        isEgg = true;
    }

    public void nextFrame(Maze maze) {
        super.nextFrame(maze);

        if (isEgg) {
            eggFrameCounter++; 
            if (eggFrameCounter == FRAMES_PER_PHASE * 3)
                isEgg = false;
        }
    }

    protected Image getCurrentSprite() {
        if (isEgg)
            return eggSprites[eggFrameCounter / FRAMES_PER_PHASE];
        return super.getCurrentSprite();
    }

    protected boolean chooseTarget(Maze maze, Player player) {
        if (isEgg)
            return false;
        return super.chooseTarget(maze, player);
    }
}
