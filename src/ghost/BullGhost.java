package src.ghost;

import src.Maze;
import src.Direction;

public class BullGhost extends Ghost {
    public BullGhost() {
        super("bull", 24, 22);
    }

    public void nextFrame(Maze maze) {
        super.nextFrame(maze);

        Direction first = Direction.STILL;
        if (targetR < previousR) first = Direction.UP;
        else if (targetR > previousR) first = Direction.DOWN;
        else if (targetC < previousC) first = Direction.LEFT;
        else if (targetC > previousC) first = Direction.RIGHT;

        Direction second = maze.shortestPath[targetR][targetC][24][2];

        if (first == Direction.STILL || second == Direction.STILL)
            velocity = 2;
        else if (first == second)
            velocity = 2;
        else
            velocity = 0.1;
    }
}
