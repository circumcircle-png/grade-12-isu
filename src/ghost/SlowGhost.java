package src.ghost;

public class SlowGhost extends Ghost {
    public SlowGhost(int startR, int startC) {
        super("slow", startR, startC);
    }

    public void initSpeeds() {
        // Description: This method puts speeds into the speed map.
        // Parameters: None
        // Return: void

        super.initSpeeds();
        speeds.put(State.NORMAL, 50);
    }
}
