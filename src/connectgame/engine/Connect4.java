package connectgame.engine;

import java.util.LinkedList;
import java.util.Random;

public class Connect4 implements ConnectGame {
    
    private static final int ROWS = 6;
    private static final int COLUMNS = 7;
    private static final int TOWIN = 4;

    private static final int BLANK = GameBoard.BLANK;
    private static final int RED = GameBoard.RED;
    private static final int YELLOW = GameBoard.YELLOW;

    private GameBoard current;
    private int currentTurn;
    private int winner;
    private LinkedList<Play> playStack;
    private Random rn = new Random();

    /**
     * Constructs an empty Connect4 game object with RED (1) to start, and an empty playStack.
     */
    public Connect4() {
        current = new GameBoard(COLUMNS, ROWS, TOWIN);
        currentTurn = RED;
        winner = 0;
        playStack = new LinkedList<>();
    }

    public void play(int column) {
        if (column >= 0 && column < COLUMNS && current.getNextDiskIndices()[column] < ROWS) {
            current.putDisk(currentTurn, column);
            playStack.push(new Play(currentTurn, column));
            winner = current.checkWin();
            currentTurn = (currentTurn == RED)? YELLOW : RED;
        } else {
            throw new IndexOutOfBoundsException("An Illegal Move was played. Column given: " + column + "  Total columns: " + COLUMNS);
        }
    }

    public boolean safePlay(int column) {
        if (column >= 0 && column < COLUMNS && current.getNextDiskIndices()[column] < ROWS) {
            current.putDisk(currentTurn, column);
            playStack.push(new Play(currentTurn, column));
            winner = current.checkWin();
            currentTurn = (currentTurn == RED)? YELLOW : RED;
            return true;
        } else {
            return false;
        }
    }
    
    public boolean undoLast() {
        if (playStack.size() > 0) {
            current.popDisk(playStack.pop());
            currentTurn = (currentTurn == RED)? YELLOW : RED;
            return true;
        } else {
            return false;
        }
    }

    public LinkedList<Play> getPlayStack() {
        return playStack;
    }

    public Play getLast() {
        return playStack.peek();
    }
    
    public int currentTurn() {
        return currentTurn;
    }

    public int getWinner() {
        return winner;
    }

    public GameBoard getGameBoard() {
        return current;
    }

    public int columns() {
        return COLUMNS;
    }

    public int rows() {
        return ROWS;
    }

    public int toWin() {
        return TOWIN;
    }

    public void playRandom() {
        int[] legalPlays = current.getLegal();
        play(legalPlays[rn.nextInt(legalPlays.length)]);
    }

    public void playComputer() {
        // TODO Auto-generated method stub
        // MAKE SURE this thing is thread safe and able to be interrupted by the undo button etc.
        
    } 
}