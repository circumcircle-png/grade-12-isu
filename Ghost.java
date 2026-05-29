import javax.swing.*;
import java.awt.*;
import java.awt.image.ImageObserver;

public class Ghost {
    private final Image image;
    private int x, y; // these represent center coordinates of ghost
    private int targetX, targetY; // these represent the coordinates of the adjacent cell it is going towards

    public Ghost() {
        image = new ImageIcon("teleport gifs/teleport down.gif").getImage();
        x = 8*22+4;
        y = 8*24+4;
        targetX = x;
        targetY = y;
    }

    public void draw(Graphics2D g, ImageObserver observer) {
        // ImageObserver is somehow needed to not have the gif frozen at one frame
        g.drawImage(image, x-8, y-8, observer);
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

            // TODO: currently only targets square and row 24 and column 2
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