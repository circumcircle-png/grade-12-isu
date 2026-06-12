package src.ghost;

public class SlowGhost extends Ghost {
    public SlowGhost(int startR, int startC) {
        super("slow", startR, startC);
    }

    public void initSpeeds() {
        super.initSpeeds();
        speeds.put(State.NORMAL, 50);
    }
}
