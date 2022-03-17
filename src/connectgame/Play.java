package connectgame;

/**
 * A Wrapper class for ConnectGame 'Plays'.
 */
class Play {
    int disk;
    int column;

    /**
     * Constructs a new Play object.
     * @param disk  The disk (int) played.
     * @param column  The column the disk is played in.
     */
    public Play(int disk, int column) {
        this.disk = disk;
        this.column = column;
    }
}