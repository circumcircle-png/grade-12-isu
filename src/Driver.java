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
    private int timer;
    private enum Screen {
        MAIN_MENU,
        GAME,
        VICTORY,
        GAME_OVER,
    }
    private Screen currentScreen = Screen.MAIN_MENU;
    private Map<Rectangle, String> buttons = new HashMap<Rectangle, String>();

    private Font pacmanFont;

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
            maze = new Maze("maze.txt");

            mazeTopLeftX = (WINDOW_WIDTH - 16 * maze.numColumns) / 2;
            mazeTopLeftY = (WINDOW_HEIGHT - 16 * maze.numRows) / 2;
            mazeBottomRightX = mazeTopLeftX + 16 * maze.numColumns;
            mazeBottomRightY = mazeTopLeftY + 16 * maze.numRows;

            pacmanFont = Font.createFont(Font.TRUETYPE_FONT, new File("images/font.ttf"));

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
        timer += 1;
        if (currentScreen == Screen.GAME) {
            player.nextFrame(maze, timer);
            player.updatePosition(maze);
            maze.generatePickUp();
            
            for (int i = ghosts.size()-1; i >= 0; i--) {
                Ghost ghost = ghosts.get(i);
                ghost.respawn(timer, player);
                ghost.nextFrame(maze, player);
                ghost.updatePosition(maze, player);
                if (ghost.checkCollision(player)) {
                    if (player.getScary()&&!ghost.getDead()) {
                        if (ghost.getName().equals("phoenix")) {
                            PhoenixGhost phoenix = (PhoenixGhost) ghost;
                            phoenix.die(timer);
                        }
                       else
                            System.out.println(ghost.getName());
                           ghost.die(timer);
                    }
                    else if(!ghost.getDead()){
                        player.loseHeart(timer);
                        if (player.getHearts() == 0) {
                            currentScreen = Screen.GAME_OVER;
                        }
                    }
                }
            }
            if(maze.tilesLeft('.')==0){
                currentScreen = Screen.VICTORY;
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

    public void createCenteredString(Graphics2D g2, float fontSize, String text, int y) {
        Font font = pacmanFont.deriveFont(fontSize);
        g2.setFont(font);

        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int x = (WINDOW_WIDTH - textWidth) / 2;

        createString(g2, fontSize, text, x, y);
    }

    public void createString(Graphics2D g2, float fontSize, String text, int x, int y) {
        Font font = pacmanFont.deriveFont(fontSize);
        g2.setFont(font);
        g2.drawString(text, x, y);
    }

    public void createCenteredButton(Graphics2D g2, float fontSize, String text, int y) {
        Font font = pacmanFont.deriveFont(fontSize);
        g2.setFont(font);

        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int x = (WINDOW_WIDTH - textWidth) / 2;

        createButton(g2, fontSize, text, x, y);
    }

    public void createButton(Graphics2D g2, float fontSize, String text, int x, int y) {
        Font font = pacmanFont.deriveFont(fontSize);
        g2.setFont(font);

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
        createString(g2, fontSize, text, x, y);

        buttons.put(new Rectangle(boxX, boxY, boxW, boxH), text);
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setColor(Color.WHITE);

        if (currentScreen == Screen.MAIN_MENU) {
            createCenteredString(g2, 24f, "pac-man", 100);
            createCenteredButton(g2, 24f, "play", 300);
            createCenteredButton(g2, 24f, "leaderboard", 400);
            createCenteredButton(g2, 24f, "settings", 500);
            createCenteredButton(g2, 24f, "credits", 600);
        }
        else if (currentScreen == Screen.GAME_OVER) {
            createCenteredString(g2, 30f, "defeat", 100);
            createCenteredButton(g2, 24f, "home", 300);
        }
        else if (currentScreen == Screen.VICTORY) {
            createCenteredString(g2, 30f, "victory", 100);
            createCenteredButton(g2, 24f, "home", 300);
        }
        else if (currentScreen == Screen.GAME) {
            // draw hud at the top
            try {
                for (int i = 0; i < player.getHearts(); i++) {
                    g2.drawImage(ImageIO.read(new File("images/heart.png")), 46 * i + 30 + mazeTopLeftX, mazeBottomRightY + 10, null);
                }

                createString(g2, 24, "Score: "+player.getScore(), mazeTopLeftX + 30, mazeTopLeftY - 40);
                createString(g2, 24, "Time: "+timer/Constants.FPS, mazeTopLeftX + 30, mazeTopLeftY - 5);
                createButton(g2, 24f, "quit", mazeBottomRightX - 120, mazeTopLeftY - 30);
            }
            catch (Exception e) {
                e.printStackTrace();
                return;
            }

            drawGame(g2);
        }
    }

    public void mousePressed(MouseEvent e) {
        for (Rectangle area: buttons.keySet()) {
            String name = buttons.get(area);
            if (!area.contains(e.getPoint()))
                continue;

            if (currentScreen == Screen.MAIN_MENU) {
                if (name.equals("play")) {
                    timer = 0;
                    currentScreen = Screen.GAME;

                }
            }
            else if (currentScreen == Screen.GAME) {
                if (name.equals("quit")) {
                    currentScreen = Screen.MAIN_MENU;
                }
            }
            if (name.equals("home")) {
                currentScreen = Screen.MAIN_MENU;
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
