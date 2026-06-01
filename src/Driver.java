package src;

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;

import src.ghost.*;

@SuppressWarnings("serial")
public class Driver extends JPanel implements Runnable {
    // TODO: Jonathan, I think pickups can be implemented directly on the Maze class, if you decide to do so, make sure you update the Maze.isAccessible function

    final int FPS = 60;
    Thread thread;

    private Maze maze;
    private ArrayList<Ghost> ghosts = new ArrayList<>();

    public Driver() {

        setPreferredSize(new Dimension(600, 600));
        setVisible(true);

        thread = new Thread(this);
        thread.start();
    }

    public void run() {
        initialize();
        while (true) {
            update();
            this.repaint();
            try {
                Thread.sleep(1000/FPS);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void initialize() {
        try {
            maze = new Maze("maze.txt");
            ghosts.add(new BullGhost(24, 22));
            maze.createTileSetComponent();
            maze.generateShortestPathMatrix();
        } catch (Exception e) {};
    }

    public void update() {
        ghosts.get(0).nextFrame(maze);
        ghosts.get(0).updatePosition(maze);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // scale up
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        double scale = 2;
        g2.scale(scale, scale);

        maze.draw(g2);

        // draw ghosts
        ghosts.get(0).draw(g2);
    }

    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame("Pac-Man");
        JPanel panel = new Driver();
        frame.add(panel);
        // frame.addKeyListener(panel);
        // frame.addMouseListener(panel);
        frame.setVisible(true);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
    }
}
