package src.ghost;

import src.*;

public class BullGhost extends Ghost {
    public final static double STRAIGHT_SPEED = (double) 120 / Constants.FPS;
    public final static double TURN_SPEED = (double) 6 / Constants.FPS ;

    public BullGhost(int startR, int startC) {
        super("bull", startR, startC);
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
            speed = STRAIGHT_SPEED;
        else if (first == second)
            speed = STRAIGHT_SPEED;
        else
            speed = TURN_SPEED;
    }
}
