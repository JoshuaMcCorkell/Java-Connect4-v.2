package connectgame;

import connectgame.engine.*;

import java.awt.event.MouseEvent;

public class ConnectGameUI {

    public enum GameMode {
        PLAYER_V_PLAYER,
        PLAYER_V_RANDOM,
        PLAYER_V_COMPUTER
    }

    private static final int BLANK = GameBoard.BLANK;
    private static final int RED = GameBoard.RED;
    private static final int YELLOW = GameBoard.YELLOW;
    
    private ConnectGame game;
    private int playerDisk;
    private GameMode currentMode;
    
    /**
     * Constructs a new ConnectGameUI object with a new Connect4 game.
     */
    public ConnectGameUI(GameMode mode) {
        this.game = new Connect4();
        this.currentMode = mode;
    }

    /**
     * Constructs a new ConnectGameUI object with the given ConnectGame and mode.
     * @param game  The game to use.
     */
    public ConnectGameUI(ConnectGame game, GameMode mode) {
        this.game = game;
        this.currentMode = mode;
    }

    /**
     * Returns whether it is the player's turn in the current position. 
     */
    public boolean isPlayersTurn() {
        if (currentMode == GameMode.PLAYER_V_PLAYER) {
            return true;
        } else {
            return playerDisk == game.currentTurn();
        }
    }  

    /**
     * Returns the players disk in the current Game.
     */
    public int getPlayerDisk() {
        return playerDisk;
    }

    /**
     * Returns the current game (ConnectGame Object).
     * Note that this might not necessarily be thread safe. 
     */
    public ConnectGame getGame() {
        return game;
    }

    /**
     * Returns the GameBoard of the current ConnectGame.
     * Note that this might not necessarily be thread safe. 
     */
    public GameBoard getGameBoard() {
        return game.getGameBoard();
    }

    /**
     * Returns the number of rows in the current ConnectGame.
     */
    public int gameRows() {
        return game.rows();
    }

    /**
     * Returns the number of columns in the current ConnectGame.
     */
    public int gameColumns() {
        return game.columns();
    }

    /**
     * This is an event handler for a MouseEvent from a GUI using this ConnectGameUI. 
     * It should be run in a separate thread. 
     * @param mouseEvent
     */
    public void mousePressed(MouseEvent mouseEvent) {
        //TODO implement
    }
}
