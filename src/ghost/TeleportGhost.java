package src.ghost;

import src.*;
import java.util.*;

public class TeleportGhost extends Ghost {
    public final static int FRAMES_PER_TELEPORT = 3 * Constants.FPS;

    private int teleportFrameCounter = 0;

    public TeleportGhost(int startR, int startC) {
        super("teleport", startR, startC);
    }

    public void nextFrame(Maze maze, Player player) {
        super.nextFrame(maze, player);
        teleportFrameCounter = (teleportFrameCounter + 1) % FRAMES_PER_TELEPORT;
        if (teleportFrameCounter == FRAMES_PER_TELEPORT-1 && state != State.SCARY) {
            // teleport to a random available square
            ArrayList<int[]> available = new ArrayList<>();
            for (int r = 0; r < maze.numRows; r++) {
                for (int c = 0; c < maze.numColumns; c++) {
                    if (maze.isAccessible(r, c))
                        available.add(new int[] {r, c});
                }
            }

            int randomIndex = (int) (available.size() * Math.random());
            int[] randomPosition = available.get(randomIndex);

            previousR = targetR = randomPosition[0];
            previousC = targetC = randomPosition[1];
            x = 8*targetC;
            y = 8*targetR;
            state = State.NORMAL;
        }
    }
}
