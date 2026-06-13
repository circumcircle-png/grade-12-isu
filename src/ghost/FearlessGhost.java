package src.ghost;

import src.*;

public class FearlessGhost extends Ghost {
    public FearlessGhost(int startR, int startC) {
        super("fearless", startR, startC);
    }

    public void nextFrame(Maze maze, Player player) {
        // Description: This method simulates one additional frame for the Ghost, and it always updates the state (even if the state remains the same).
        // Parameters: Maze and player
        // Return: void

        super.nextFrame(maze, player);
        if (state != State.DEAD)
            state = State.NORMAL;
    }

    public void initSpeeds() {
        // Description: This method puts speeds into the speed map.
        // Parameters: None
        // Return: void

        super.initSpeeds();
        speeds.put(State.NORMAL, 50);
    }
}
