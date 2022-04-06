package connectgame.engine;

/**
 * A Wrapper class for ConnectGame 'Plays'.
 */
public class Play {
    int disk;
    int column;

    /**
     * Constructs a new Play object.
     * 
     * @param disk   The disk (int) played.
     * @param column The column the disk is played in.
     */
    public Play(int disk, int column) {
        this.disk = disk;
        this.column = column;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Play)) {
            return false;
        }
        Play other = (Play) o;
        return (other.disk == this.disk) && (other.column == this.column);
    }

    @Override
    public int hashCode() {
        return (10 * disk) + column;
    }

    @Override
    public String toString() {
        String str = disk == 1 ? "R " : "Y ";
        return str + (column + 1);
    }
}