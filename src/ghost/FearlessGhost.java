package src.ghost;

import src.*;

public class FearlessGhost extends Ghost {
    public FearlessGhost(int startR, int startC) {
        super("fearless", startR, startC);
    }

    public void nextFrame(Maze maze, Player player) {
        super.nextFrame(maze, player);
        if (state != State.DEAD)
            state = State.NORMAL;
    }

    public void initSpeeds() {
        super.initSpeeds();
        speeds.put(State.NORMAL, 50);
    }
}
