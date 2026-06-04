package src;

public class Player extends Movable {
    public Player(int startR, int startC) {
        super(startR, startC);
        // TODO: Jonathan, write code
        // no
    }

    public void updatePosition() {
        // TODO: Jonathan, look at Ghost.updatePosition for inspiration
    }

    public int[] getCurrentPosition() {
        return new int[] {targetR, targetC};
    }
}
