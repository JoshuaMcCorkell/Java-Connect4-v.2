package connectgame;

public class Connect4 implements ConnectGame {
    //Constants
    private static final int ROWS = 6;
    private static final int COLUMNS = 7;
    private static final int TOWIN = 4;
    private static final int BLANK = GameBoard.BLANK;
    private static final int RED = GameBoard.RED;
    private static final int YELLOW = GameBoard.YELLOW;

    private GameBoard current;
    private int currentTurn;
    private int winner;

    public Connect4() {
        current = new GameBoard(COLUMNS, ROWS);
        currentTurn = RED;
        winner = 0;
    }

    @Override
    public void play(int column) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean safePlay(int column) {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public boolean undoLast() {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public int currentTurn() {
        return currentTurn;
    }

    @Override
    public int getWinner() {
        return winner;
    }

    @Override
    public GameBoard getGameBoard() {
        return current;
    }

    @Override
    public int columns() {
        return COLUMNS;
    }

    @Override
    public int rows() {
        return ROWS;
    }

    @Override
    public int toWin() {
        return TOWIN;
    }

    @Override
    public void playRandom() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void playComputer() {
        // TODO Auto-generated method stub
        
    }

    
}
