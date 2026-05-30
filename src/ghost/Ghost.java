package src.ghost;

import java.awt.*;
import java.io.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import javax.imageio.ImageIO;
import java.util.*;

import src.Maze;
import src.Constants;
import src.Direction;

public abstract class Ghost {
    protected int x, y; // these represent center coordinates of ghost
    protected int targetX, targetY; // these represent the coordinates of the adjacent cell it is going towards

    private final Map<Direction, Image[]> ghostSprites;
    private Direction facing;
    private int frameIndex = 0;
    private int frameCounter = 0;

    public Ghost(String name, int startR, int startC) {
        ghostSprites = new HashMap<>();
        try {
            BufferedImage sheet = ImageIO.read(new File("images/ghost/" + name + ".png"));
            for (int i = 0; i <= 3; i++) {
                ghostSprites.put(
                    Constants.DIRECTIONS[i],
                    new Image[] {sheet.getSubimage(0, 16*i, 16, 16), sheet.getSubimage(16, 16*i, 16, 16)}
                );
            }
        }
        catch (IOException e) {
            // no mercy
            System.exit(0);
        }

        x = 8*startC+4;
        targetX = 8*startC+4;
        y = 8*startR+4;
        targetY = 8*startR+4;

        facing = Direction.DOWN;
    }

    public void draw(Graphics2D g, ImageObserver observer) {
        // ImageObserver is somehow needed to not have the gif frozen at one frame
        g.drawImage(ghostSprites.get(facing)[frameIndex], x-8, y-8, observer);

        frameCounter++;
        if (frameCounter >= 5) {
            frameCounter = 0;
            frameIndex = (frameIndex + 1) % 2;
        }
    }

    public void updatePosition(Maze maze) {
        if (x < targetX) {
            x++;
            facing = Direction.RIGHT;
        }
        else if (x > targetX) {
            x--;
            facing = Direction.LEFT;
        }
        else if (y < targetY) {
            y++;
            facing = Direction.DOWN;
        }
        else if (y > targetY) {
            y--;
            facing = Direction.UP;
        }
        
        // if reached target coordinate, update to next target
        if (x == targetX && y == targetY) {
            // convert x, y coordinates to row and column value of maze
            int r = (y+3)/8;
            int c = (x+3)/8;

            // TODO: currently only targets square at row 24 and column 2
            Direction direction = maze.shortestPath[r][c][24][2];
            if (direction == Direction.UP)
                targetY -= 8;
            else if (direction == Direction.DOWN)
                targetY += 8;
            else if (direction == Direction.LEFT)
                targetX -= 8;
            else if (direction == Direction.RIGHT)
                targetX += 8;
        }
    }
}