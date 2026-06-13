package src.ghost;

import src.*;

public class BullGhost extends Ghost {
    public BullGhost(int startR, int startC) {
        super("bull", startR, startC);
    }

    public void initSpeeds() {
        // Description: This method puts speeds into the speed map.
        // Parameters: None
        // Return: void

        super.initSpeeds();
        speeds.put(State.BULL_STRAIGHT, 120);
        speeds.put(State.BULL_TURN, 20);
    }

    protected boolean chooseTarget(Maze maze, Player player) {
        // Description: This method chooses the next target coordinate for the ghost.
        // Parameters: Maze and player
        // Return: Boolean representing a target coordinate is selected, or the ghost will remain still
        
        if (state == State.BULL_TURN || state == State.BULL_STRAIGHT)
            return chooseTargetWhenNormal(maze, player);
        return super.chooseTarget(maze, player);
    }

    public void nextFrame(Maze maze, Player player) {
        // Description: This method simulates one additional frame for the Ghost, and it always updates the state (even if the state remains the same).
        // Parameters: Maze and player
        // Return: void

        super.nextFrame(maze, player);

        if (state == State.SCARY||state==State.DEAD) return;

        Direction first = Direction.STILL;
        if (targetR < previousR) first = Direction.UP;
        else if (targetR > previousR) first = Direction.DOWN;
        else if (targetC < previousC) first = Direction.LEFT;
        else if (targetC > previousC) first = Direction.RIGHT;

        int playerR = player.getCurrentPosition()[0];
        int playerC = player.getCurrentPosition()[1];

        Direction second = maze.getShortestPath()[targetR][targetC][playerR][playerC];

        if (first == Direction.STILL || second == Direction.STILL)
            state = State.BULL_STRAIGHT;
        else if (first == second)
            state = State.BULL_STRAIGHT;
        else
            state = State.BULL_TURN;
    }
}
