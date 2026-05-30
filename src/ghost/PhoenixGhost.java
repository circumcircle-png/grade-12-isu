package src.ghost;

import java.awt.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import src.Maze;

public class PhoenixGhost extends Ghost {
    private int eggFrameCounter = 0;
    private final Image[] eggSprites = new Image[3];

    public PhoenixGhost() {
        super("phoenix", 24, 22);
        try {
            BufferedImage sheet = ImageIO.read(new File("images/ghost/egg.png"));
            for (int i = 0; i <= 2; i++)
                eggSprites[i] = sheet.getSubimage(0, 16*i, 16, 16);
        }
        catch (IOException e) {
            System.exit(0);
        }

        // to test dead
        state = State.EGG;
    }

    public void nextFrame() {
        super.nextFrame();
        if (state == State.EGG) {
            eggFrameCounter++; 
            if (eggFrameCounter == 180) {
                state = State.NORMAL;
            }
        }
    }

    protected Image getCurrentSprite() {
        if (state == State.EGG)
            return eggSprites[eggFrameCounter / 60];
        return super.getCurrentSprite();
    }

    protected boolean chooseTarget(Maze maze) {
        if (state == State.EGG)
            return true;
        return super.chooseTarget(maze);
    }
}
