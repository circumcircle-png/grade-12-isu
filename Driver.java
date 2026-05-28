import javax.swing.*;
import java.awt.*;
import java.io.*;

public class Driver extends JPanel {
    private Maze maze;

    public Driver() throws IOException {
        maze = new Maze("maze.txt");
        maze.createTileSetComponent();
        setPreferredSize(new Dimension(600, 600));
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // scale up
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        double scale = 2;
        g2.scale(scale, scale);

        maze.drawToScreen(g2);
    }

    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame("Pac-Man");
        frame.add(new Driver());
        frame.pack();
        frame.setVisible(true);
    }
}
