package src.ghost;

public class TeleportGhost extends Ghost {
    public TeleportGhost() {
        super("teleport");
        x = 8*22+4;
        y = 8*24+4;
        targetX = x;
        targetY = y;
    }
}
