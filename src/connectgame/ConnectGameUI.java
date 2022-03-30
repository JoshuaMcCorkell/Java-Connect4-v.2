package connectgame;

import java.awt.event.MouseEvent;

import connectgame.engine.Connect4;
import connectgame.engine.ConnectGame;
import connectgame.engine.GameBoard;

/**
 * <h4>ConnectGameUI</h4>
 * This class acts as a back end/dataclass for handling a single ConnectGame
 * for the {@link GUI} class.
 */
public class ConnectGameUI {

    /**
     * All the different Game Modes that are currently supported by 
     * this class and the GUI class.
     */
    public enum GameMode {
        PLAYER_V_PLAYER,
        PLAYER_V_RANDOM,
        PLAYER_V_COMPUTER
    }

    private int defaultSpaceSize = 50;
    
    private ConnectGame game;
    private int playerDisk;
    private GameMode currentMode;

    private boolean isDone = false;
    
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
     * @param playerDisk  The disk for the player to start with. If the mode is 
     * player v player, this paramter is ignored, so use the constructor ConnectGameUI()
     * which has the default mode as player v player.
     */
    public ConnectGameUI(GameMode mode, int playerDisk) {
        this.game = new Connect4();
        this.currentMode = mode;
        this.playerDisk = playerDisk;
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
     * Returns the winner of the current game. (RED or YELLOW (1 or 2), 3 if 
     * the game ended in a draw, 0 if game is ongoing.)
     */
    public int getWinner() {
        return game.getWinner();
    }

    /**
     * Returns the mode of this ConnectGameUI.
     */
    public GameMode getMode() {
        return currentMode;
    }
    
    /**
     * Sets the isDone boolean to true.
     */
    public void finish() {
        isDone = true;
    }

    /**
     * Returns true if finish() has been called on this ConnectGameUI
     */
    public boolean isDone() {
        return isDone;
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
     * Undoes the last move of the game.
     */
    public void undoLast() {
        game.undoLast();
    }

    /**
     * Plays a random or computer move based on the current mode. 
     * @throws InterruptedException
     */
    public void playAuto() {
        if (game.getWinner() == 3) {
            return;
        }
        if (currentMode == GameMode.PLAYER_V_RANDOM) {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            game.playRandom();
        } else if (currentMode == GameMode.PLAYER_V_COMPUTER) {
            game.playComputer();
        }
    }
    
    /**
     * This is an event handler for a MouseEvent from a GUI using this ConnectGameUI.
     * All it does is play a move based on the current click, if legal and it is the player's turn. 
     * @param mouseEvent  The mouse event. The only properties used are the X and Y values.
     * @return  Whether a move was played.
     */
    public boolean playerMousePressed(MouseEvent mouseEvent) {
        if (isPlayersTurn() && game.getWinner() == 0) { // If it is the player's turn, play their move.
            final int playColumn = (mouseEvent.getX() - 18) / defaultSpaceSize; // 18 just works.
            if (playColumn < gameColumns()) {
                boolean played = game.safePlay(playColumn);
                if (played) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Plays the computer's move IF legal andg it is the computer's turn.
     * Should be run in a separate thread.
     * @return  Whether a move was played.
     */
    public boolean computerTurn() {
        if (currentMode != GameMode.PLAYER_V_PLAYER && !isPlayersTurn() && game.getWinner() == 0) { 
            // If it is now the computer's turn and the game is not over, play their move. 
            playAuto();
            return true;
        }
        return false;
    }
}