import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Maze {
    private BufferedImage tileset;
    private Map<String, BufferedImage> tilesetComponents = new HashMap<>();

    private char[][] maze;
    private int numRows, numColumns;

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
    }

    private void createTile(String name, int x, int y) {
        tilesetComponents.put(name, tileset.getSubimage(x, y, 8, 8));
    }

    public void createTileSetComponent() throws IOException {
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

    private boolean isAccessible(int r, int c) {
        return maze[r][c] == '.' || maze[r][c] == '*' || maze[r][c] == 'a';
    }

    private boolean isWall(int r, int c) {
        return maze[r][c] == '1' || maze[r][c] == '2' || maze[r][c] == '3' || maze[r][c] == '4';
    }

    private String getTileType(int r, int c) {
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

    public void drawToScreen(Graphics g) {
        for (int r = 1; r < numRows-1; r++) {
            for (int c = 1; c < numColumns-1; c++) {
                String nameToDraw = getTileType(r, c);
                if (nameToDraw.equals("UNKNOWN")) {
                    g.setColor(Color.RED);
                    g.fillRect(8*c, 8*r, 8*c+8, 8*r+8);
                    continue;
                }
                if (maze[r][c] == '2')
                    nameToDraw += "2";
                g.drawImage(tilesetComponents.get(nameToDraw), 8*c, 8*r, null);
            }
        }
    }
}