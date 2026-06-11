package src;

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
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
        GAME_OVER,
    }
    private Screen currentScreen = Screen.MAIN_MENU;
    private Map<Screen, Image> screens = new HashMap<Screen, Image>();
    private Map<Rectangle, String> buttons = new HashMap<Rectangle, String>();

    private final int WINDOW_WIDTH = 600;
    private final int WINDOW_HEIGHT = 700;

    private int mazeTopLeftX, mazeTopLeftY, mazeBottomRightX, mazeBottomRightY;

    public Driver() {
        setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
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
            screens.put(Screen.GAME_OVER, ImageIO.read(new File("images/screen/game-over.png")));

            maze = new Maze("maze.txt");

            mazeTopLeftX = (WINDOW_WIDTH - 16 * maze.numColumns) / 2;
            mazeTopLeftY = (WINDOW_HEIGHT - 16 * maze.numRows) / 2;
            mazeBottomRightX = mazeTopLeftX + 16 * maze.numColumns;
            mazeBottomRightY = mazeTopLeftY + 16 * maze.numRows;

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
            maze.generatePickUp();
            for (int i = ghosts.size()-1; i >= 0; i--) {
                Ghost ghost = ghosts.get(i);
                ghost.nextFrame(maze, player);
                ghost.updatePosition(maze, player);
                if (ghost.checkCollision(player)) {//what
                    if (player.getScary()) {
                        if (ghost.getName().equals("phoenix")) {
                            PhoenixGhost phoenix = (PhoenixGhost) ghost;
                            phoenix.die();
                        }
                        else
                            ghosts.remove(i);
                    }
                    else {
                        player.loseHeart();
                        if (player.getHearts() == 0) {
                            currentScreen = Screen.GAME_OVER;
                        }
                    }
                }
            }
            if(maze.tilesLeft('.')==0){
                currentScreen = Screen.GAME_OVER;
            }
        }
    }

    public void drawGame(Graphics2D g2) {
        AffineTransform t = g2.getTransform();

        // shift to the middle of the screen
        g2.translate(mazeTopLeftX, mazeTopLeftY);

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

        g2.setTransform(t);
    }

    public void createCenteredButton(Graphics2D g2, String text, int y) {
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int x = (WINDOW_WIDTH - textWidth) / 2;
        createButton(g2, text, x, y);
    }

    public void createButton(Graphics2D g2, String text, int x, int y) {
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();

        int padding = 24;

        int boxX = x - padding;
        int boxY = y - textHeight - padding / 2 + 1;
        int boxW = textWidth + padding * 2;
        int boxH = textHeight + padding;

        // Draw rounded box
        g2.drawRoundRect(boxX, boxY, boxW, boxH, 12, 12);

        // Draw text
        g2.drawString(text, x, y);

        buttons.put(new Rectangle(boxX, boxY, boxW, boxH), text);
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        Font font; 
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, new File("images/font.ttf"));
        }
        catch (Exception e) {
            e.printStackTrace();
            return;
        }
        
        font = font.deriveFont(24f);
        g2.setFont(font);

        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(Color.WHITE);

        if (currentScreen == Screen.MAIN_MENU) {
            createCenteredButton(g2, "play", 300);
        }
        else if (currentScreen == Screen.GAME_OVER) {
            g2.drawImage(screens.get(Screen.GAME_OVER), 0, 0, null);
        }
        else if (currentScreen == Screen.GAME) {
            // draw hud at the top
            try {
                for (int i = 0; i < player.getHearts(); i++) {
                    g2.drawImage(ImageIO.read(new File("images/heart.png")), 46 * i + 30 + mazeTopLeftX, mazeBottomRightY + 10, null);
                }

                g2.drawString("Score: 20", mazeTopLeftX + 30, mazeTopLeftY - 40);
                g2.drawString("Time: 20", mazeTopLeftX + 30, mazeTopLeftY - 5);
                createButton(g2, "quit", mazeBottomRightX - 120, mazeTopLeftY - 30);
            }
            catch (Exception e) {
                e.printStackTrace();
                return;
            }

            drawGame(g2);
        }
    }

    private boolean rectangleClicked(MouseEvent e, int topLeftX, int topLeftY, int bottomRightX, int bottomRightY) {
        return (topLeftX <= e.getX() && e.getX() <= bottomRightX && topLeftY <= e.getY() && e.getY() <= bottomRightY);
    }

    public void mousePressed(MouseEvent e) {
        for (Rectangle area: buttons.keySet()) {
            String name = buttons.get(area);
            if (!area.contains(e.getPoint()))
                continue;

            if (currentScreen == Screen.MAIN_MENU) {
                if (name.equals("play")) {
                    currentScreen = Screen.GAME;
                }
            }
            else if (currentScreen == Screen.GAME) {
                if (name.equals("quit")) {
                        currentScreen = Screen.MAIN_MENU;
                }
            }

            break;
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
        frame.setVisible(true);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
    }
}
