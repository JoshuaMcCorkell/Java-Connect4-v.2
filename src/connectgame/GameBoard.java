package connectgame;

import java.util.Arrays;

/**
 * <h4>GameBoard</h4>
 * <p>This class essentially contains a ConnectGame GameBoard, which is a two dimensional array containing BLANK, RED, 
 * and YELLOW values (which are 0, 1, and 2 respectively).</p>
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

    /**
     * Initializes an empty ConnectGame GameBoard with the dimensions given.
     * @param columns any non-zero integer.
     * @param rows  any non-zero integer.
     */
    public GameBoard(int columns, int rows) {
        this.columns = columns;
        this.rows = rows;
        
        data = new int[columns][rows];
        nextDisk = new int[columns];
        for (int i = 0; i < columns; i++) {
            nextDisk[i] = 0;
            for (int j = 0; j < rows; j++) {
                data[i][j] = BLANK;
            }
        }
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
     * Returns the array containing the heights of each column in <code>data</code>.
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
     * Adds the disk (int) to the top of the given column.
     * @param disk  must be RED or YELLOW (1 or 2)
     * @param column  must be an column in the current GameBoard (>= 0 && <= columns).
     */
    public void putDisk(int disk, int column) {
        data[column][nextDisk[column]] = disk;
        nextDisk[column]++;
    }

    /**
     * Removes and returns the disk (int) at the top of the given column.
     * @param column  must be an column in the current GameBoard (>= 0 && <= columns).
     * @return  RED or YELLOW (1 or 2)
     * @throws ArrayIndexOutOfBoundsException  if column is empty
     */
    public int popDisk(int column) {
        int disk = data[column][nextDisk[column]-1];
        data[column][nextDisk[column]-1] = BLANK;
        nextDisk[column]--;
        return disk;
    }

    /**
     * Whether the current GameBoard is 'full' or not. This means whether every space is occupied by a disk (1 or 2)
     * @return  <code>true</code> if full, otherwise <code>false</code>.
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
    
    private int checkLines(int startColumn, int columnAmount, int startRow, int rowAmount, int columnStep, int rowStep, int winAmount) {
        //TODO Add comments to this and a javadoc comment as well.
        int n; // This variable basically serves as a flag for which disks are in the potential winning lines.
        int redWin = 1;
        int yellowWin = (int) Math.ceil(Math.pow(YELLOW, winAmount) - 0.1); // subtract 0.1 to make sure float inaccuracy isn't a problem.
        for (int i = startColumn; i < startColumn + columnAmount; i++) {
            for (int j = startRow; j < startRow + rowAmount; j++) {
                n = 1;
                for (int k = 0; k < winAmount; k++) {
                    n *= data[i + (k * columnStep)][j + (k * rowStep)];
                }
                if (n == 0) {
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

    public int checkWin() {
        //TODO: Implement using checkLines
        return 0;
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