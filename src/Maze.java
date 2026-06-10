package src;

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Maze {
    private final BufferedImage tileset;
    private Map<String, BufferedImage> tilesetComponents = new HashMap<>();
    public char[] pickups;
    public char[][] maze;
    public Direction[][][][] shortestPath; // shortestPath[a][b][c][d] stores the first direction path from (a,b) to (c,d)
    public int numRows, numColumns;

    // turning on DEBUG adds gridlines of 8x8 pixels
    private final boolean DEBUG = false;

    public Maze(String fileName) throws IOException {
        // read tileset
        tileset = ImageIO.read(new File("images/maze-tiles.png"));

        // read maze
        Scanner toFile = new Scanner(new File("maze.txt"));
        numRows = Integer.parseInt(toFile.nextLine());
        numColumns = Integer.parseInt(toFile.nextLine());
        maze = new char[numRows][numColumns];
        for (int i = 0; i < numRows; i++) {
            String row = toFile.nextLine();
            for (int j = 0; j < numColumns; j++)
                maze[i][j] = row.charAt(j);
        }
        toFile.close();
        pickups = new char[1];
        pickups[0]='.';
    }

    public void remove(int i, int j) {
        maze[i][j] = 'a';
    }

    public void generatePickUp() {
        boolean validSpot = true;
        do {
            int randRow = (int) (Math.random()*numRows);
            int randCol = (int) (Math.random()*numColumns);
            if (maze[randRow][randCol] == 'a') {//can spawn on player idk
                validSpot = false;
                int power = (int) (Math.random()*pickups.length);
                maze[randRow][randCol] = pickups[power];
            }
        } while (validSpot);
    }

    public void generateShortestPathMatrix() {
        // Description: This method uses BFS to find the shortest path from one cell to another.
        // Parameters: None
        // Return: void

        shortestPath = new Direction[numRows][numColumns][numRows][numColumns];

        // loop through every starting point
        for (int startR = 0; startR < numRows; startR++) {
            for (int startC = 0; startC < numColumns; startC++) {
                if (!isAccessible(startR, startC))
                    continue;

                Queue<int[]> queue = new LinkedList<>();

                shortestPath[startR][startC][startR][startC] = Direction.STILL;

                // add all 4 directions
                for (int i = 0; i < 4; i++)
                    queue.add(new int[] {startR + Constants.DELTA_R[i], startC + Constants.DELTA_C[i], i}); // third element represents first direction needed

                // run BFS
                while (!queue.isEmpty()) {
                    int[] current = queue.remove();
                    int r = current[0];
                    int c = current[1];

                    // check if it is accessible
                    if (!(0 <= r && r < numRows && 0 <= c && c < numColumns))
                        continue;

                    if (!isAccessible(r, c))
                        continue;

                    // check if it has been visited previously
                    if (shortestPath[startR][startC][r][c] != null)
                        continue;

                    shortestPath[startR][startC][r][c] = Constants.DIRECTIONS[current[2]];

                    for (int i = 0; i < 4; i++)
                        queue.add(new int[] {r + Constants.DELTA_R[i], c + Constants.DELTA_C[i], current[2]});
                }
            }
        }
    }

    private void createTile(String name, int x, int y) {
        // Description: This method inserts a sub image of the tileset into the map.
        // Parameters: Name of the tile, top left coordinate of 8x8 subimage
        // Return: void

        tilesetComponents.put(name, tileset.getSubimage(x, y, 8, 8));
    }

    public void createTileSetComponent() throws IOException {
        // Description: This method fills the tile map.
        // Parameters: None
        // Return: void

        createTile("blank", 333, 45);

        createTile("dot", 342, 45);
        createTile("bigDot", 360, 45);

        // directions are taken from the perspective of the hallway
        // i.e. pacman can move at a square, so that leftWall is to its left

        createTile("topRightCorner", 279, 36);
        createTile("topLeftCorner", 288, 36);
        createTile("bottomRightCorner", 315, 36);
        createTile("bottomLeftCorner", 324, 36);

        createTile("topRightCornerFake", 252, 45);
        createTile("topLeftCornerFake", 243, 45);
        createTile("bottomRightCornerFake", 270, 45);
        createTile("bottomLeftCornerFake", 261, 45);

        createTile("leftWall", 297, 36);
        createTile("rightWall", 306, 36);
        createTile("topWall", 261, 36);
        createTile("bottomWall", 351, 27);

        createTile("topRightCorner2", 225, 27);
        createTile("topLeftCorner2", 234, 27);
        createTile("bottomRightCorner2", 261, 27);
        createTile("bottomLeftCorner2", 270, 27);

        createTile("topRightCornerFake2", 225, 27);
        createTile("topLeftCornerFake2", 234, 27);
        createTile("bottomRightCornerFake2", 261, 27);
        createTile("bottomLeftCornerFake2", 270, 27);

        createTile("topWall2", 315, 27);
        createTile("bottomWall2", 333, 27);
        createTile("rightWall2", 243, 27);
        createTile("leftWall2", 252, 27);

        createTile("topRightCornerSharp", 333, 36);
        createTile("topLeftCornerSharp", 342, 36);
        createTile("bottomRightCornerSharp", 351, 36);
        createTile("bottomLeftCornerSharp", 360, 36);

        createTile("topToLeftTurn2", 279, 27);
        createTile("topToRightTurn2", 288, 27);
        createTile("bottomToLeftTurn2", 297, 27);
        createTile("bottomToRightTurn2", 306, 27);
        createTile("rightToBottomTurn2", 315, 45);
        createTile("leftToBottomTurn2", 324, 45);
    }

    public boolean isAccessible(int r, int c) {
        // Description: This method checks whether row r and column c is accessible.
        // Parameters: Row and column
        // Return: Whether the cell is accessible

        return maze[r][c] == '.' || maze[r][c] == '*' || maze[r][c] == 'a';
    }

    public boolean isWall(int r, int c) {
        // Description: This method checks whether row r and column c is a wall.
        // Parameters: Row and column
        // Return: Whether the cell is a wall

        return maze[r][c] == '1' || maze[r][c] == '2' || maze[r][c] == '3' || maze[r][c] == '4';
    }
    public void summonPickUp(){
        
    }
    public String getTileType(int r, int c) {
        // Description: This method returns the name of the tile to be displayed for the specific cell.
        // Parameters: Row and column of target cell
        // Return: Name

        if (maze[r][c] == '.')
            return "dot";
        if (maze[r][c] == '*')
            return "bigDot";
        if (!isWall(r, c))
            return "blank";

        // special wall
        if (maze[r][c] == '3') {
            if (maze[r][c-1] == '2' && maze[r][c+1] == '3') return "leftToBottomTurn2";
            if (maze[r][c+1] == '2' && maze[r][c-1] == '3') return "rightToBottomTurn2";
            if (maze[r-1][c] == '2' && maze[r+1][c] == '3' && maze[r][c-1] == '1') return "topToLeftTurn2";
            if (maze[r+1][c] == '2' && maze[r-1][c] == '3' && maze[r][c-1] == '1') return "bottomToLeftTurn2";
            if (maze[r-1][c] == '2' && maze[r+1][c] == '3' && maze[r][c+1] == '1') return "topToRightTurn2";
            if (maze[r+1][c] == '2' && maze[r-1][c] == '3' && maze[r][c+1] == '1') return "bottomToRightTurn2";
        }

        boolean n = !isAccessible(r-1, c);
        boolean s = !isAccessible(r+1, c);
        boolean w = !isAccessible(r, c-1);
        boolean e = !isAccessible(r, c+1);
        boolean ne = !isAccessible(r-1, c+1);
        boolean se = !isAccessible(r+1, c+1);
        boolean nw = !isAccessible(r-1, c-1);
        boolean sw = !isAccessible(r+1, c-1);
        // boolean n = isWall(r-1, c);
        // boolean s = isWall(r+1, c);
        // boolean w = isWall(r, c-1);
        // boolean e = isWall(r, c+1);
        // boolean ne = isWall(r-1, c+1);
        // boolean se = isWall(r+1, c+1);
        // boolean nw = isWall(r-1, c-1);
        // boolean sw = isWall(r+1, c-1);

        // sharp turn
        if (maze[r][c] == '4') {
            if (maze[r-1][c] == '2' && maze[r][c+1] == '2') return "bottomLeftCornerSharp";
            if (maze[r][c-1] == '2' && maze[r-1][c] == '2') return "bottomRightCornerSharp";
            if (maze[r][c-1] == '2' && maze[r][c-1] == '2') return "topRightCornerSharp";
            if (maze[r+1][c] == '2' && maze[r][c+1] == '2') return "topLeftCornerSharp";
        }

        // weird corners
        // 1 1 1
        // 1 1 1
        // . 1 1
        if (!sw && s && w) return "topRightCornerFake";
        if (!nw && n && w) return "bottomRightCornerFake";
        if (!ne && n && e) return "bottomLeftCornerFake";
        if (!se && s && e) return "topLeftCornerFake";

        // vertical straight
        if (n && s) {
            if (isAccessible(r, c-1))
                return "rightWall";
            return "leftWall";
        }

        // horizontal straight
        if (w && e) {
            if (isAccessible(r+1, c))
                return "topWall";
            return "bottomWall";
        }

        // real corners
        // . . .
        // 1 1 .
        // 1 1 .
        if (!n && !w && e && s) return "topLeftCorner";
        if (!n && !e && w && s) return "topRightCorner";
        if (!s && !w && n && e) return "bottomLeftCorner";
        if (!s && !e && n && w) return "bottomRightCorner";

        return "UNKNOWN";
    }

    public void draw(Graphics2D g) {
        // Description: This method draws the entire maze onto the screen.
        // Parameters: The Graphics2D associated with the screen
        // Return: void

        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numColumns; c++) {
                String nameToDraw = "";
                if (r == 0 && c == 0 && r == numRows-1 && c == numColumns-1) {
                    nameToDraw = "blank";
                }
                else {
                    nameToDraw = getTileType(r, c);
                    if (nameToDraw.equals("UNKNOWN")) {
                        g.setColor(Color.RED);
                        g.fillRect(8*c, 8*r, 8*c+8, 8*r+8);
                        continue;
                    }
                    if (maze[r][c] == '2')
                        nameToDraw += "2";
                }
                g.drawImage(tilesetComponents.get(nameToDraw), 8*c, 8*r, null);
            }
        }

        if (DEBUG) {
            for (int i = 0; i < 50; i++) {
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
                g.setColor(Color.RED);
                g.drawLine(8*i, 0, 8*i, 400);
                g.drawLine(0, 8*i, 400, 8*i);
                g.setComposite(AlphaComposite.SrcOver);
            }
        }
    }
}