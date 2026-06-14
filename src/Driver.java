package src;

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import javax.imageio.ImageIO;

import src.ghost.*;

public class Driver extends JPanel implements Runnable, MouseListener, KeyListener, MouseMotionListener {
    private Thread thread;

    private Point mousePos = new Point();

    private Player player;
    private Maze maze;
    private ArrayList<Ghost> ghosts = new ArrayList<>();
    private int timer;
    private ArrayList<Leaderboard> leaderboard = new ArrayList<>();
    private ArrayList<Leaderboard> searchedBoard = new ArrayList<>();
    private enum Screen {
        MAIN_MENU, GAME, VICTORY, GAME_OVER, TUTORIAL, CREDITS, LEADERBOARD, SETTINGS
    }
    private Screen currentScreen = Screen.MAIN_MENU;
    private Map<Rectangle, String> buttons = new HashMap<>();
    private Map<Screen, Image> screenImages = new HashMap<>();
    private int starti, endi;
    private boolean searched;
    private String prev;
    private Font pacmanFont;

    private JTextField gameName;
    private JSlider volumeSlider;

    private final int WINDOW_WIDTH = 600;
    private final int WINDOW_HEIGHT = 700;

    private int mazeTopLeftX, mazeTopLeftY, mazeBottomRightX, mazeBottomRightY;

    public Driver() {
        setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        addMouseListener(this);
        addMouseMotionListener(this);
        setVisible(true);    
        initialize();
        thread = new Thread(this);
        thread.start();
    }

    public void run() {
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
        addKeyListener(this);
        setFocusable(true);
        setLayout(null);
        gameName = new JTextField();

        volumeSlider = new JSlider(0, 100, 20);
        volumeSlider.setBounds(150, 260, 300, 50);
        volumeSlider.addChangeListener(e -> {
            Audio.changeVolume(volumeSlider.getValue());
        });
        volumeSlider.setVisible(false);
        volumeSlider.setOpaque(true);
        add(volumeSlider);

        Audio.initialize();
        Audio.playMainMenuMusic();

        try {
            pacmanFont = Font.createFont(Font.TRUETYPE_FONT, new File("images/font.ttf"));
            screenImages.put(Screen.TUTORIAL, ImageIO.read(new File("images/screen/tutorial.png")));
            screenImages.put(Screen.MAIN_MENU, ImageIO.read(new File("images/screen/main-menu.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        gameName.setFont(pacmanFont.deriveFont(50f));
        add(gameName);
    }

    public void startNewGame() throws IOException {
        timer = 0;
        maze = new Maze("maze.txt");
        ghosts.clear();
        mazeTopLeftX = (WINDOW_WIDTH - 16 * maze.getNumColumns()) / 2;
        mazeTopLeftY = (WINDOW_HEIGHT - 16 * maze.getNumRows()) / 2;
        mazeBottomRightX = mazeTopLeftX + 16 * maze.getNumColumns();
        mazeBottomRightY = mazeTopLeftY + 16 * maze.getNumRows();

        player = new Player(24, 14);
        ghosts.add(new FearlessGhost(20, 22));
        ghosts.add(new SlowGhost(21, 22));
        ghosts.add(new BullGhost(22, 22));
        ghosts.add(new TeleportGhost(23, 22));
        ghosts.add(new PolterGhost(24, 22));
        ghosts.add(new PhoenixGhost(25, 22));

        for (Ghost ghost: ghosts)
            ghost.respawn(player);

        maze.createTileSetComponent();
        maze.generateShortestPathMatrix();
    }

    public void update() {
        if (currentScreen == Screen.GAME) {
            timer += 1;
            player.nextFrame(maze, timer);
            player.updatePosition(maze);
            maze.generatePickUp();
            
            for (int i = ghosts.size()-1; i >= 0; i--) {
                Ghost ghost = ghosts.get(i);
                if (timer == ghost.getRespawnTimer())
                    ghost.respawn(player);
                ghost.nextFrame(maze, player);
                ghost.updatePosition(maze, player);
                if (ghost.checkCollision(player)) {
                    if (ghost.isScared() && !ghost.getDead()) {
                        player.updateScore(200);
                        if (ghost.getName().equals("phoenix")) {
                            PhoenixGhost phoenix = (PhoenixGhost) ghost;
                            phoenix.die(timer);
                        }
                        else
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

        Rectangle box = new Rectangle(boxX, boxY, boxW, boxH);

        if (box.contains(mousePos))
            g2.setColor(Color.YELLOW);

        // Draw rounded box
        g2.drawRoundRect(boxX, boxY, boxW, boxH, 12, 12);

        // Draw text
        createString(g2, fontSize, text, x, y);

        buttons.put(new Rectangle(boxX, boxY, boxW, boxH), text);

        g2.setColor(Color.WHITE);
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // clear buttons from previous screen
        buttons = new HashMap<>();

        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setColor(Color.WHITE);

        g.drawImage(screenImages.get(currentScreen), 0, 0, null);

        volumeSlider.setVisible(currentScreen == Screen.SETTINGS);

        if (currentScreen == Screen.MAIN_MENU) {
            createCenteredString(g2, 24, "pac-man", 100);

            int top = 300;
            int increment = 60;

            createCenteredButton(g2, 24, "play", top);
            createCenteredButton(g2, 24, "tutorial", top + increment);
            createCenteredButton(g2, 24, "leaderboard", top + increment * 2);
            createCenteredButton(g2, 24, "settings", top + increment * 3);
            createCenteredButton(g2, 24, "credits", top + increment * 4);
            gameName.setText("");
            gameName.setVisible(false);
            gameName.setEnabled(false);
        }
        else if (currentScreen == Screen.GAME_OVER) {
            createCenteredString(g2, 30, "defeat", 100);
            createCenteredButton(g2, 24, "home", 300);
        }
        else if (currentScreen == Screen.VICTORY) {
            createCenteredString(g2, 30, "victory", 100);
            createCenteredString(g2, 30, "Enter a Name", 200);
            createCenteredButton(g2, 24, "home", 600);
            createCenteredButton(g2,24,"Save Name", 400);
            gameName.setBounds((WINDOW_WIDTH-500) / 2,250,500,75);
            gameName.setVisible(true);
            gameName.setEnabled(true);
        }
        else if (currentScreen == Screen.GAME) {
            // draw hud at the top
            try {
                for (int i = 0; i < player.getHearts(); i++) {
                    g2.drawImage(ImageIO.read(new File("images/heart.png")), 46 * i + 30 + mazeTopLeftX, mazeBottomRightY + 10, null);
                }

                createString(g2, 24, "Score: "+player.getScore(), mazeTopLeftX + 30, mazeTopLeftY - 40);
                createString(g2, 24, "Time: "+timer/Constants.FPS, mazeTopLeftX + 30, mazeTopLeftY - 5);
                createButton(g2, 24, "quit", mazeBottomRightX - 120, mazeTopLeftY - 30);
            }
            catch (Exception e) {
                e.printStackTrace();
                return;
            }

            drawGame(g2);
        }
        else if (currentScreen == Screen.TUTORIAL) {
            createCenteredString(g2, 30, "Tutorial", 75);
            createButton(g2, 16, "home", 500, 70);

            String[] text = {
                "You are Pac-Man.",
                "Use WASD or arrow keys to move.",
                "Collect all dots to win.",
                "Run away from ghosts.",
                "Collect big dots to scare and eat ghosts.",
                "Other power ups: hearts and speed.",
                "Slow ghost is slow.",
                "Fearless ghost is never scared.",
                "Bull ghost runs straight fast, turns slow.",
                "Phoenix ghost becomes egg after dying.",
                "Teleport ghost periodically teleports.",
                "Polter ghost goes through walls.",
            };
            int currentY = 125;
            int incrementY = 40;

            for (String s: text) {
                createString(g2, 12, s, 80, currentY);
                currentY += incrementY;
            }
        }
        else if (currentScreen == Screen.CREDITS) {
            createCenteredString(g2, 30, "Credits", 75);
            createButton(g2, 16, "home", 500, 70);
            createCenteredString(g2, 12, "Created by Jonathan Zhou and Christopher Li", 125);
            createCenteredString(g2, 12, "June 13, 2026", 175);
        }
        else if(currentScreen == Screen.LEADERBOARD){
            displayLeaderboard(g2);
            createCenteredString(g2, 30, "Leaderboard", 75);
            createButton(g2, 16, "home", 500, 70);
            createButton(g2, 16, "Name", 150, 125);
            createButton(g2, 16, "Score", 300, 125);
            createButton(g2, 16, "Time", 450, 125);
            createCenteredString(g2, 16, "Search Name", 675);
            gameName.setBounds(25,600,550,50);
            gameName.setFont(pacmanFont.deriveFont(32f));
            gameName.setVisible(true);
            gameName.setEnabled(true);
            if(!gameName.getText().equals("")){
                searched=true;
                if(!gameName.getText().equalsIgnoreCase(prev)){
                    searchName();
                prev = gameName.getText();
                }
            }else{
                searched=false;
            }
        }
        else if (currentScreen == Screen.SETTINGS) {
            createCenteredString(g2, 30, "Settings", 75);
            createButton(g2, 16, "home", 500, 70);
            createCenteredString(g2, 24, "Volume: " + Audio.getVolume(), 190);
        }
    }

    public void mousePressed(MouseEvent e) {
        for (Rectangle area: buttons.keySet()) {
            String name = buttons.get(area);
            if (!area.contains(e.getPoint()))
                continue;

            if (name.equals("play"))
                Audio.stopMainMenuMusic();
            else
                Audio.playMainMenuMusic();

            if (name.equals("play")) {
                try {
                    startNewGame();
                } catch (Exception ex) {
                    ex.printStackTrace();
                };
                currentScreen = Screen.GAME;
            }
            else if (name.equals("quit"))
                currentScreen = Screen.MAIN_MENU;
            else if (name.equals("home"))
                currentScreen = Screen.MAIN_MENU;
            else if (name.equals("credits"))
                currentScreen = Screen.CREDITS;
            else if (name.equals("tutorial"))
                currentScreen = Screen.TUTORIAL;
            else if (name.equals("leaderboard")){
                readLeaderboard();
                Collections.sort(leaderboard);
                currentScreen = Screen.LEADERBOARD;
            }else if (name.equals("settings"))
                currentScreen = Screen.SETTINGS;
            else if(name.equals("Save Name")){
                String userName = gameName.getText().trim();
                writeLeaderboard(userName);
                currentScreen = Screen.MAIN_MENU;
            } else if(name.equals("Name")){
                if(searched){
                    Collections.sort(searchedBoard,new SortByName());
                }else{
                    Collections.sort(leaderboard,new SortByName());
                }
            }else if(name.equals("Score")){
                if(searched){
                    Collections.sort(searchedBoard);
                }else{
                    Collections.sort(leaderboard);
                }
            }else if(name.equals("Time")){
                if(searched){
                    Collections.sort(searchedBoard, new SortByTime());
                }else{
                    Collections.sort(leaderboard, new SortByTime());
                }
            }
            break;
        }
    }
    public void searchName(){
        String userName = gameName.getText().trim();
        Collections.sort(leaderboard, new SortByName());
        int i = Collections.binarySearch(leaderboard, new Leaderboard(userName, 0, 0), new SortByName());
        if(i<0){
            starti = (i*-1)-1;
            endi = (i*-1)-1;
        } else{
            starti = i;
            endi = i;
        }
        while(starti-1>=0&&userName.length()<=leaderboard.get(starti-1).getName().length()&&leaderboard.get(starti-1).getName().substring(0,userName.length()).equalsIgnoreCase(userName)){
            starti -=1;
        }
        while(endi+1<leaderboard.size()&&userName.length()<=leaderboard.get(endi+1).getName().length()&&leaderboard.get(endi+1).getName().substring(0,userName.length()).equalsIgnoreCase(userName)){
            endi +=1;
        }
        searchedBoard.clear();
        if(i>=0||starti!=endi||(endi<leaderboard.size()&&userName.length()<=leaderboard.get(endi).getName().length()&&leaderboard.get(endi).getName().substring(0,userName.length()).equalsIgnoreCase(userName))){
            for(i = starti;i<=endi;i++){
            searchedBoard.add(leaderboard.get(i));
            }
        }
    }
    public void displayLeaderboard(Graphics2D g2){
        int fontSize = 24;
        if(searched){
            if(searchedBoard.size()<10){
                 for(int i = 0; i <searchedBoard.size();i++){
                    createCenteredString(g2, fontSize, (i+1)+") "+searchedBoard.get(i).toString(), 200+40*i);
                }
            }else{
                for (int i = 0; i<10;i++){
                    createCenteredString(g2, fontSize, (i+1)+") "+searchedBoard.get(i).toString(), 200+40*i);
                }
            }
        } else {
            if(leaderboard.size()<10){
                 for(int i = 0; i <leaderboard.size();i++){
                    createCenteredString(g2, fontSize, (i+1)+") "+leaderboard.get(i).toString(), 200+40*i);
                }
            }else{
                for (int i = 0; i<10;i++){
                    createCenteredString(g2, fontSize, (i+1)+") "+leaderboard.get(i).toString(), 200+40*i);
                }
            }
        }
    }
     
    public void writeLeaderboard(String name){
        try {
            Leaderboard lb = new Leaderboard(name, player.getScore(), timer/Constants.FPS);
            PrintWriter leaderBoardFile = new PrintWriter(new FileWriter ("leaderboard.txt",true));
            leaderBoardFile.println(lb.getName());
            leaderBoardFile.println(lb.getScore() +" "+lb.getTime());
            leaderBoardFile.close();
        } catch (IOException e) {
            System.out.println("Writing error!");
        }
    }
     
    public void readLeaderboard(){
        leaderboard.clear();
        try{
            Scanner leaderboardFile = new Scanner (new File("leaderboard.txt"));
            while(leaderboardFile.hasNextLine()){
                String name = leaderboardFile.nextLine();
                StringTokenizer scoreTimer = new StringTokenizer(leaderboardFile.nextLine());
                leaderboard.add(new Leaderboard(name, Integer.parseInt(scoreTimer.nextToken()), Integer.parseInt(scoreTimer.nextToken())));
            }
            leaderboardFile.close();
        } catch(FileNotFoundException e){
            System.out.println("File not found");
        }

    }
    public void mouseClicked(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}

    public void mouseMoved(MouseEvent e) {
        mousePos = e.getPoint();
    }

    public void mouseDragged(MouseEvent e) {
        mousePos = e.getPoint();
    }

    public void keyTyped(KeyEvent e) {
        // throw new UnsupportedOperationException("Unimplemented method 'keyTyped'");
    }

    public void keyPressed(KeyEvent e) {
        // Description: turns the keypressed into user inputs
        // Parameter keyevent
        // return
        if (currentScreen == Screen.GAME) {
            int input = e.getKeyCode();
            if (input == KeyEvent.VK_W || input == KeyEvent.VK_UP)
                player.nextFacing = Direction.UP;
            else if (input == KeyEvent.VK_A || input == KeyEvent.VK_LEFT)
                player.nextFacing = Direction.LEFT;
            else if (input == KeyEvent.VK_D || input == KeyEvent.VK_RIGHT)
                player.nextFacing = Direction.RIGHT;
            else if (input == KeyEvent.VK_S || input == KeyEvent.VK_DOWN)
                player.nextFacing = Direction.DOWN;
            else if(input == KeyEvent.VK_BACK_SLASH){
                player.gainHeart();
            }
        }
    }

    public void keyReleased(KeyEvent e) {
        // throw new UnsupportedOperationException("Unimplemented method
        // 'keyReleased'");
    }

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
