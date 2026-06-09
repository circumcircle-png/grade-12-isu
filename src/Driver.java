package src;

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.imageio.ImageIO;

import src.ghost.*;

public class Driver extends JPanel implements Runnable, MouseListener {
    private Thread thread;

    private Player player;
    private Maze maze;
    private ArrayList<Ghost> ghosts = new ArrayList<>();

    private enum Screen {
        MAIN_MENU,
        GAME,
    }
    private Screen currentScreen = Screen.MAIN_MENU;
    private Map<Screen, Image> screens = new HashMap<Screen, Image>();

    public Driver() {
        setPreferredSize(new Dimension(16 * 30, 16 * 33));
        addMouseListener(this);
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
            ghosts.add(new PolterGhost(24, 22));
            ghosts.add(new PhoenixGhost(25, 22));
            addKeyListener(player);
            setFocusable(true);
            maze.createTileSetComponent();
            maze.generateShortestPathMatrix();
        } catch (Exception e) {
            e.printStackTrace();
        };
    }

    public void update() {
        if (currentScreen == Screen.GAME) {
            player.nextFrame(maze);
            player.updatePosition(maze);
            boolean scare = player.getScary();
            for (int i = ghosts.size()-1; i>=0;i--) {
                Ghost ghost = ghosts.get(i);
                if(scare&&ghost.scaredFrameTimer == 0)
                    ghost.setScared();
                ghost.nextFrame(maze, player);
                ghost.updatePosition(maze, player);
                if(scare&&ghost.checkCollision(player)){
                    if (ghost.getName().equals("phoenix")) {
                        PhoenixGhost phoenix = (PhoenixGhost) ghost;
                        phoenix.die();
                    }
                    else
                        ghosts.remove(i);
                }
            }
            
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

    private boolean rectangleClicked(MouseEvent e, int topLeftX, int topLeftY, int bottomRightX, int bottomRightY) {
        return (topLeftX <= e.getX() && e.getX() <= bottomRightX && topLeftY <= e.getY() && e.getY() <= bottomRightY);
    }

    public void mousePressed(MouseEvent e) {
        if (currentScreen == Screen.MAIN_MENU) {
            if (rectangleClicked(e, 101, 155, 379, 272)) {
                currentScreen = Screen.GAME;
            }
            else if (rectangleClicked(e, 101, 299, 379, 341)) {
                // clicked leaderboard
            }
            else if (rectangleClicked(e, 101, 356, 379, 398)) {
                // clicked settings
            }
            else if (rectangleClicked(e, 15, 456, 174, 498)) {
                // clicked help
            }
            else if (rectangleClicked(e, 305, 456, 464, 498)) {
                // clicked credit
            }
        }
    }
    public void mouseClicked(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}

    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame("Pac-Man");
        JPanel panel = new Driver();
        frame.add(panel);
        // frame.addKeyListener(panel);
        frame.setVisible(true);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
    }
}
