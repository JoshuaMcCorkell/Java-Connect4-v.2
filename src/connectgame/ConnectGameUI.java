package connectgame;

import connectgame.engine.*;

import java.awt.event.MouseEvent;

import javax.naming.directory.InvalidAttributeValueException;
import javax.naming.directory.InvalidAttributesException;

public class ConnectGameUI {

    public enum GameMode {
        PLAYER_V_PLAYER,
        PLAYER_V_RANDOM,
        PLAYER_V_COMPUTER
    }

    private static final int BLANK = GameBoard.BLANK;
    private static final int RED = GameBoard.RED;
    private static final int YELLOW = GameBoard.YELLOW;

    private int defaultSpaceSize = 50;
    
    private ConnectGame game;
    private int playerDisk;
    private GameMode currentMode;
    
    /**
     * Constructs a new ConnectGameUI object with a new Connect4 game, and the mode player v player.
     */
    public ConnectGameUI() {
        this.game = new Connect4();
        this.currentMode = GameMode.PLAYER_V_PLAYER;
    }

    /**
     * Constructs a new ConnectGameUI object with a new Connect4 game.
     * @param mode  The game mode for this ui
     * @param playerDisk  The disk for the player to start with. If the mode is player v player, this paramter
     * is ignored, so use the constructor ConnectGameUI().
     */
    public ConnectGameUI(GameMode mode, int playerDisk) {
        this.game = new Connect4();
        this.currentMode = mode;
        this.playerDisk = playerDisk;
        if (!isPlayersTurn()) {
            Thread computerMove = new ComputerMove();
            computerMove.start();
        }
    }

    /**
     * Constructs a new ConnectGameUI object with the given ConnectGame and mode.
     * @param game  The game to use.
     * @param mode  The game mode for this ui.
     * @param playerDisk  The disk that the player will use. 
     */
    public ConnectGameUI(ConnectGame game, GameMode mode, int playerDisk) {
        this.game = game;
        this.currentMode = mode;
        this.playerDisk = playerDisk;
        if (!isPlayersTurn()) {
            Thread computerMove = new ComputerMove();
            computerMove.start();
        }
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
     * Note that mutating this might not necessarily be thread safe. 
     */
    public ConnectGame getGame() {
        return game;
    }

    /**
     * Returns the GameBoard of the current ConnectGame.
     * Note that mutating this might not necessarily be thread safe. 
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
     * Use this method to set the size of the disks (Spaces) on the gameboard. 
     * This property is by default set to 50.
     * @param spaceSize  The new space size. Must be higher than 0.
     * @throws IllegalArgumentException  if spaceSize <= 0.
     */
    public void setDefaultSpaceSize(int spaceSize) {
        if (spaceSize > 0) {
            defaultSpaceSize = spaceSize;
        } else {
            throw new IllegalArgumentException("The space size property of a ConnectGameUI must be greater than 0.");
        }        
    }
    
    /**
     * Plays a random or computer move based on the current mode. 
     */
    public void playAuto() {
        if (game.getWinner() == 3) {
            return;
        }
        if (currentMode == GameMode.PLAYER_V_RANDOM) {
            game.playRandom();
        } else if (currentMode == GameMode.PLAYER_V_COMPUTER) {
            game.playComputer();
        }
    }
    
    /**
     * This is an event handler for a MouseEvent from a GUI using this ConnectGameUI. 
     * It should preferably be run in a separate thread. 
     * @param mouseEvent
     */
    public void mousePressed(MouseEvent mouseEvent) {
        if (isPlayersTurn()) { // If it is the player's turn, play their move.
            final int playColumn = mouseEvent.getX() / defaultSpaceSize;
            if (playColumn < gameColumns()) {
                game.safePlay(playColumn);
            }
        }
        if (currentMode != GameMode.PLAYER_V_PLAYER && !isPlayersTurn()) { // If it is now the computer's turn, play their move. 
            playAuto();
        }
    }

    /**
     * This is just so the computer move can be played at the start of the game in another thread. 
     */
    private class ComputerMove extends Thread {
        @Override 
        public void run() {
            playAuto();
        }
    }
}