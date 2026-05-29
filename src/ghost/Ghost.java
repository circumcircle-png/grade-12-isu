package src.ghost;

import javax.swing.*;
import java.awt.*;
import java.awt.image.ImageObserver;

import src.Maze;
import src.Direction;

public abstract class Ghost {
    protected int x, y; // these represent center coordinates of ghost
    protected int targetX, targetY; // these represent the coordinates of the adjacent cell it is going towards

    private final Image upImage, downImage, leftImage, rightImage;

    public Ghost(String name) {
        this.upImage = new ImageIcon("images/ghost/" + name + "/" + name + " up.gif").getImage();
        this.downImage = new ImageIcon("images/ghost/" + name + "/" + name + " down.gif").getImage();
        this.leftImage = new ImageIcon("images/ghost/" + name + "/" + name + " left.gif").getImage();
        this.rightImage = new ImageIcon("images/ghost/" + name + "/" + name + " right.gif").getImage();
    }

    public void draw(Graphics2D g, ImageObserver observer) {
        // ImageObserver is somehow needed to not have the gif frozen at one frame
        g.drawImage(downImage, x-8, y-8, observer);
    }

    public void updatePosition(Maze maze) {
        if (x < targetX)
            x++;
        else if (x > targetX)
            x--;
        else if (y < targetY)
            y++;
        else if (y > targetY)
            y--;
        
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