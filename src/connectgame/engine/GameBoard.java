package connectgame.engine;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * <h4>GameBoard</h4>
 * <p>This class essentially contains a ConnectGame GameBoard, which is a two dimensional array containing BLANK, RED, 
 * and YELLOW values (which are 0, 1, and 2 respectively), and the methods associated with core game logic and rules.</p>
 */
public class GameBoard {
    // Disk Constants
    public static final int BLANK = 0;
    public static final int RED = 1;
    public static final int YELLOW = 2;

    private int[][] data;
    private int[] nextDisk;
    private int rows;
    private int columns;
    private int toWin;

    private int middleColumn;
    private int[] sortedLegalOrder;

    /**
     * Initializes an empty ConnectGame GameBoard with the dimensions given and the default winning amount given.
     * @param columns any non-zero integer.
     * @param rows  any non-zero integer.
     * @param toWin  any non-zero integer lower than the amount of columns <strong>and</strong> the amount of rows.
     */
    public GameBoard(int columns, int rows, int toWin) {
        this.columns = columns;
        this.rows = rows;
        this.toWin = toWin;

        this.middleColumn = columns / 2;
        
        data = new int[columns][rows];
        nextDisk = new int[columns];
        for (int i = 0; i < columns; i++) {
            nextDisk[i] = 0;
            for (int j = 0; j < rows; j++) {
                data[i][j] = BLANK;
            }
        }

        setSortedLegalOrder();
    }

    /**
     * This sets the order that the legal moves appear in in {@code getLegal()}.
     * Sorted by distance from the center.
     */
    private void setSortedLegalOrder() {
            int[] allColumns = new int[columns];
            for (int i = 0; i < columns; i++) {
                allColumns[i] = i;
            }
            sortedLegalOrder = Arrays.stream(allColumns)
                .boxed()
                .sorted((a, b) -> (Math.abs(middleColumn - a) - Math.abs(middleColumn - b)))
                .mapToInt(i -> i)
                .toArray();
    }

    /**
     * Returns the disk (int) in the given column and row.
     * @param column  must be >= 0 && <= columns.
     * @param row  must be >= 0 && <= rows.
     * @return  BLANK, RED or YELLOW (0, 1 or 2)
     */
    public int get(int column, int row) {
        return data[column][row];
    }

    /**
     * Returns the array with the current GameBoard data. 
     * @return  A 2-dimensional array containing BLANK, RED, and YELLOW values (0, 1, and 2).
     */
    public int[][] getData() {
        return data;
    }

    /**
     * Returns the array containing the heights of each column in <@code data>.
     * In other words, the array containing the indices for each column that the next disk will occupy.
     */
    public int[] getNextDiskIndices() {
        return nextDisk;
    }

    /**
     * Returns the number of rows in the GameBoard.
     */
    public int rows() {
        return rows;
    }

    /**
     * Returns the number of columns in the GameBoard.
     */
    public int columns() {
        return columns;
    }

    /**
     * Returns the number of consecutive disks required to win in this GameBoard.
     */
    public int toWin() {
        return toWin;
    }
    
    /**
     * Sets the number of consecutive disks required to win the game in this GameBoard. 
     * <p><strong>Warning:</strong> The behaviour of the checkWin and (internal) checkLines methods could be unexpected 
     * if this property is reduced part way through a game. This is because there might be winning lines already in place with the new toWin length
     * that have not been checked for.
     * @param toWin  The new length of a winning line required to win the game. 
     */
    public void setToWin(int toWin) {
        this.toWin = toWin;
    }

    /**
     * Adds the disk (int) to the top of the given column.
     * @param disk  must be RED or YELLOW (1 or 2)
     * @param column  must be an column in the current GameBoard (>= 0 && <= columns).
     */
    public void putDisk(int disk, int column) {
        data[column][nextDisk[column]] = disk;
        nextDisk[column]++;
    }

    /**
     * Adds a disk (int) to the top of a column based on a Play object.
     * @param play  The play object with the disk and column properties.
     */
    public void putDisk(Play play) {
        data[play.column][nextDisk[play.column]] = play.disk;
        nextDisk[play.column]++;
    }

    /**
     * Removes and returns the disk (int) at the top of the given column.
     * @param column  must be an column in the current GameBoard (>= 0 && <= columns).
     * @return  RED or YELLOW (1 or 2)
     * @throws ArrayIndexOutOfBoundsException  if column is empty
     */
    public int popDisk(int column) {
        final int disk = data[column][nextDisk[column]-1];
        data[column][nextDisk[column]-1] = BLANK;
        nextDisk[column]--;
        return disk;
    }

    /**
     * Removes and returns the disk (int) at the top of a column based on a Play object.
     * @param play  The play object with the column property.
     * @return  RED or YELLOW (1 or 2)
     * @throws ArrayIndexOutOfBoundsException  if column is empty
     */
    public int popDisk(Play play) {
        final int disk = data[play.column][nextDisk[play.column]-1];
        data[play.column][nextDisk[play.column]-1] = BLANK;
        nextDisk[play.column]--;
        return disk;
    }
    
    /**
     * Whether the current GameBoard is 'full' or not. This means whether every space is occupied by a disk (1 or 2)
     * @return  {@code true} if full, otherwise {@code false}.
     */
    public boolean isFull() {
        for (int i : nextDisk) {
            if (i != rows) {
                return false;
            }
        }
        return true;
    }

    /**
     * Resets the board to all BLANK (0).
     */
    public void clearBoard() {
        data = new int[columns][rows];
    }
    
    /**
     * <p>This method checks all the designated potential winning lines, and returns RED or YELLOW (1 or 2) if any of them are winning (consecutive disks of either colour.)
     * <p>This method loops through the columns and rows designated by the parameters. Each pair of (column, row) will be a space on the GameBoard. 
     * This space will be the <em>start</em> of a potential winning line. Each potential winning line will have a length of {@code winAmount} 
     * and will be going in the direction designated by the {@code columnStep} and {@code rowStep} parameters. The return value is the Disk (int) of the winner, 
     * 0 if none. 
     * <p>For example: 
     * {@code checkLines(0, 3, 0, 2, 1, 1, 4)} will check all upwards diagonals ({@code columnStep = 1, rowStep = 1}) starting in the columns 0 - 2 (3 columns) 
     * and rows 0 - 1 (2 rows).
     * @param startColumn  The first column to check. Must be a column in the current GameBoard.
     * @param columnAmount  The amount of columns to check. This number, in combination with the {@code startColumn}, {@code columnStep} and {@code winAmount} parameters, 
     * must not cause the algorithm to check outside the bounds of the GameBoard. <p>For example, using {@code startColumn = 0, columnAmount = 5, columnStep = 1} and
     * {@code winAmount = 4} will raise an error if the GameBoard has less than 7 columns.
     * @param startRow  The first row to check. Must be a row in the current GameBoard.
     * @param rowAmount  The amount of rows to check. This number, in combination with the {@code startRow}, {@code rowStep} and {@code winAmount} parameters, 
     * must not cause the algorithm to check outside the bounds of the GameBoard. <p>For example, using {@code startRow = 0, rowAmount = 4, rowStep = 1} and
     * {@code winAmount = 4} will raise an error if the GameBoard has less than 6 rows.
     * @param columnStep  The amount of columns to advance for each space in the potential winning line. 
     * @param rowStep  The amount of rows to advance (in an upwards direction) for each space in the potential winning line.
     * <p>For example if {@code columnStep = 1} and {@code rowStep = 1} the potential winning lines will be in an upwards diagonal direction. 
     * Similarly, if {@code columnStep = 0} and {@code rowStep = 1} the potential winning lines will be vertical.
     * @param winAmount  The amount of spaces to check for each of the potential winning lines. All of the spaces in the winning line must be either RED or YELLOW (1 or 2) 
     * to return a non-zero value.
     * @return  0 if no winning lines are found, RED (1) if red was found to be the winner, YELLOW (2) if yellow was found to be the winner.
     * @throws ArrayIndexOutOfBoundsException  if the parameters given cause the algorithm to check a space on the GameBoard that is out of bounds.
     * @implNote  This method assumes there is at most 1 winning line. It returns as soon as a winning line is found, and returns 0 if all potential winning lines 
     * have been checked and none of them are winning for either player.
     */
    private int checkLines(int startColumn, int columnAmount, int startRow, int rowAmount, int columnStep, int rowStep, int winAmount) {
        int n; // This variable basically serves as a 'flag' for which disks are in the potential winning lines.
        final int redWin = 1;
        final int yellowWin = (int) Math.ceil(Math.pow(YELLOW, winAmount) - 0.1); // subtract 0.1 to make sure float inaccuracy isn't a problem.
        
        for (int i = startColumn; i < startColumn + columnAmount; i++) { // Loops through the columns.
            for (int j = startRow; j < startRow + rowAmount; j++) {  // Loops through the rows
                // Each pair of (column, row) will be a space on the GameBoard designating the start of a potential win position. 
                n = 1;
                for (int k = 0; k < winAmount; k++) { // Loops through the spaces in the potential win position, multiplying n by the value in each one. 
                    n *= data[i + (k * columnStep)][j + (k * rowStep)];
                }
                if (n == 0) { // n is 0, which means at least 1 of the spaces was blank.
                    continue;
                }
                if (n == redWin) {
                    return RED;
                }
                if (n == yellowWin) {
                    return YELLOW;
                }
            }
        }
        // If no winning lines have been found:
        return 0;
    }

    /**
     * Checks if either player has won in the current position. <p>This means they have a line of consecutive Disks with length {@code toWin} specified in the constructor.
     * @return  RED or YELLOW (1 or 2) if they have won, 3 if the position has ended in a draw, otherwise 0.
     */
    public int checkWin() {
        int winPlayer;

        // Horizontal Check
        winPlayer = checkLines(0, columns - toWin + 1, 0, rows, 1, 0, toWin);
        if (winPlayer != 0) {
            return winPlayer;
        }

        // Vertical Check
        winPlayer = checkLines(0, columns, 0, rows - toWin + 1, 0, 1, toWin);
        if (winPlayer != 0) {
            return winPlayer;
        }

        // Upward Diagonals Check
        winPlayer = checkLines(0, columns - toWin + 1, 0, rows - toWin + 1, 1, 1, toWin);
        if (winPlayer != 0) {
            return winPlayer;
        }

        // Downward Diagonals Check
        winPlayer = checkLines(0, columns - toWin + 1, toWin - 1, rows - toWin + 1, 1, -1, toWin);
        if (winPlayer != 0) {
            return winPlayer;
        }

        // If no one has won:
        if (isFull()) {
            return 3;
        }
        return 0;
    }

    /**
     * Returns an int[] will all the legal moves in the current position, sorted by their distance from the center.
     * 
     * Note: This could probably be made more efficient...
     */
    public int[] getLegal() {
        LinkedList<Integer> legalPlays = new LinkedList<>();
        
        for (int i = 0; i < sortedLegalOrder.length; i++) {
            if (nextDisk[i] < rows) {
                legalPlays.add(i);
            }
        }
        return legalPlays.stream().mapToInt(i -> i).toArray();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof GameBoard)) {
            return false;
        }
        GameBoard other = (GameBoard) o;
        return Arrays.deepEquals(data, other.getData());
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(data);
    }
}