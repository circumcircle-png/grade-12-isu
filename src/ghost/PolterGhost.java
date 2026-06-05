package src.ghost;

import src.*;

public class PolterGhost extends Ghost {
    public PolterGhost(int startR, int startC) {
        super("polter", startR, startC);
    }

    protected boolean chooseTarget(Maze maze, Player player) {
        if (state == State.SCARED)
            return super.chooseTarget(maze, player);
        int playerR = player.getCurrentPosition()[0];
        int playerC = player.getCurrentPosition()[1];
        int DELTA_R = 0;
        int DELTA_C = 0;
        if (targetR < playerR)
            DELTA_R = -1;
        else if (playerR < targetR)
            DELTA_R = 1;
        else if (targetC < playerC)
            DELTA_C = 1;
        else if (playerC < targetC)
            DELTA_C = -1;

        if (DELTA_R == 0 && DELTA_C == 0)
            return false;

        int r = targetR + DELTA_R;
        int c = targetC + DELTA_C;
        while (0 <= r && r < maze.numRows && 0 <= c && c < maze.numColumns) {
            if (maze.isAccessible(r, c)) {
                targetR = r;
                targetC = c;
                return true;
            }
            r += DELTA_R;
            c += DELTA_C;
        }
        return false;

        // return super.chooseTarget(maze, player);
    }
}
