package connectgame;

public class Position {
    // Disk Constants
    public static final int BLANK = 0;
    public static final int RED = 1;
    public static final int YELLOW = 2;

    private int[][] data;
    private int[] nextDisk;
    private int rows;
    private int columns;

    /**
     * Initializes an empty ConnectGame Position with the dimensions given.
     * @param columns
     * @param rows
     */
    public Position(int columns, int rows) {
        this.columns = columns;
        this.rows = rows;
        
        data = new int[columns][rows];
        for (int i = 0; i < columns; i++) {
            nextDisk[i] = 0;
            for (int j = 0; j < rows; j++) {
                data[i][j] = BLANK;
            }
        }
    }

    /**
     * Returns the disk (int) in the given column and row.
     * @param column
     * @param row
     * @return  RED or YELLOW (1 or 2)
     */
    public int get(int column, int row) {
        return data[column][row];
    }

    /**
     * Returns the array with the current position data. 
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
     * Returns the number of rows in the position.
     */
    public int rowCount() {
        return rows;
    }

    /**
     * Returns the number of columns in the position.
     */
    public int columnCount() {
        return columns;
    }

    /**
     * Adds the disk (int) to the top of the given column.
     * @param column
     * @param disk  must be RED or YELLOW (1 or 2)
     */
    public void push(int column, int disk) {
        data[column][nextDisk[column]] = disk;
        nextDisk[column]++;
    }

    /**
     * Removes and returns the disk (int) at the top of the given column.
     * @param column
     * @return  RED or YELLOW (1 or 2)
     * @throws ArrayIndexOutOfBoundsException  if column is empty
     */
    public int pop(int column) {
        int disk = data[column][nextDisk[column]-1];
        data[column][nextDisk[column]-1] = BLANK;
        nextDisk[column]--;
        return disk;
    }

    /**
     * Whether the current position is 'full' or not. This means whether every space is occupied by a disk (1 or 2)
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
}