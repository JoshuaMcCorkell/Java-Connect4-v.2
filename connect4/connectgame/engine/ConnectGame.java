package connectgame.engine;

import java.util.LinkedList;

public interface ConnectGame {
    /**
     * Plays a disk (based on whose turn it is) into the column specified.
     * If an error occurs due to an illegal move, this method will
     * <strong>not</strong> block the error.
     * <p>
     * This method will switch the turn to the other player if successful.
     * <p>
     * This method will update the winner if needed.
     * 
     * @param column must be an column in the current GameBoard (>= 0 && <=
     *               columns).
     */
    public void play(int column);

    /**
     * Plays a disk (based on whose turn it is) into the column specified.
     * If an error occurs due to an illegal move, this method ignores the error and
     * returns {@code false}.
     * <p>
     * This method will switch the turn to the other player if successful.
     * <p>
     * This method will update the winner if needed.
     * 
     * @param column must be an column in the current GameBoard (>= 0 && <=
     *               columns).
     * @return {@code true} if successful, otherwise {@code false}.
     */
    public boolean safePlay(int column);

    /**
     * Undoes the most recent play. Will be able to undo all the way
     * until the start of the game, unless the game was initialized from a custom
     * position,
     * in which case it can stop at that position.
     * 
     * @return {@code true} if successful, {@code false} if there are no more plays
     *         to undo.
     */
    public boolean undoLast();

    /**
     * Returns the current play stack.
     */
    public LinkedList<Play> getPlayStack();

    /**
     * Returns the most recent Play, null if none.
     */
    public Play getLast();

    /**
     * Returns the disk (int) whose turn it currently is.
     * 
     * @return RED or YELLOW (1 or 2)
     */
    public int currentTurn();

    /**
     * Returns the disk (int) who has won the game.
     * 
     * @return RED or YELLOW (1 or 2) if applicable, 0 if game is not over,
     *         3 if game is over and ended in a draw.
     */
    public int getWinner();

    /**
     * Returns the current GameBoard object.
     */
    public GameBoard getGameBoard();

    /**
     * Returns the amount of columns in the GameBoard.
     * Should be constant for any 1 implementation.
     */
    public int columns();

    /**
     * Returns the amount of rows in the GameBoard.
     * Should be constant for any 1 implementation.
     */
    public int rows();

    /**
     * Returns the amount of disks required to win the game.
     * Should be constant for any 1 implementation.
     */
    public int toWin();

    /**
     * Plays the disk whose turn it currently is to a random (legal) column.
     */
    public void playRandom();

    /**
     * Plays the disk whose turn it currently is to a column as decided by an
     * algorithm.
     */
    public void playComputer();
}