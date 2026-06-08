package src;

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import javax.imageio.ImageIO;

import src.ghost.*;

@SuppressWarnings("serial")
public class Driver extends JPanel implements Runnable{
    // TODO: Jonathan, I think pickups can be implemented directly on the Maze class, if you decide to do so, make sure you update the Maze.isAccessible function

    private Thread thread;

    private Player player;
    private Maze maze;
    private ArrayList<Ghost> ghosts = new ArrayList<>();

    private enum Screen {
        MAIN_MENU,
        GAME,
    }
    private Screen currentScreen = Screen.GAME;
    private Map<Screen, Image> screens = new HashMap<Screen, Image>();

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
                Thread.sleep(1000 / Constants.FPS);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void initialize() {
        try {
            screens.put(Screen.MAIN_MENU, ImageIO.read(new File("images/screen/main-menu.png")));

            maze = new Maze("maze.txt");
            player = new Player(24, 2);
            // ghosts.add(new FearlessGhost(20, 22));
            // ghosts.add(new SlowGhost(21, 22));
            ghosts.add(new BullGhost(22, 22));
            ghosts.add(new TeleportGhost(23, 22));
            ghosts.add(new TeleportGhost(23, 22));
            ghosts.add(new TeleportGhost(23, 22));
            ghosts.add(new TeleportGhost(23, 22));
            ghosts.add(new PolterGhost(24, 22));
            ghosts.add(new PhoenixGhost(25, 22));
            addKeyListener(player);
            setFocusable(true);
            maze.createTileSetComponent();
            maze.generateShortestPathMatrix();
            ghosts.get(3).setScared();
        } catch (Exception e) {
            e.printStackTrace();
        };
    }

    public void update() {
        player.nextFrame(maze);
        player.updatePosition(maze);
         for (Ghost ghost: ghosts) {
            ghost.nextFrame(maze, player);
            ghost.updatePosition(maze, player);
         }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        
        if (currentScreen == Screen.MAIN_MENU) {
            g2.drawImage(screens.get(Screen.MAIN_MENU), 0, 0, null);

            // scale up
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            double scale = 2;
            g2.scale(scale, scale);

            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
            maze.draw(g2);
            g2.setComposite(AlphaComposite.SrcOver);
        }
        else if (currentScreen == Screen.GAME) {
            // scale up
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            double scale = 2;
            g2.scale(scale, scale);

            maze.draw(g2);

            // draw ghosts
            for (Ghost ghost: ghosts) {
                ghost.draw(g2);
            }

            // draw player
            player.draw(g2);
        }
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
