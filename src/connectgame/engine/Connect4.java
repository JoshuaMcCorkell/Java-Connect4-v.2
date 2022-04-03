package connectgame.engine;

import java.util.LinkedList;
import java.util.HashMap;
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
    private LinkedList<Play> playStack; // This is so moves can be undone
    private HashMap<GameBoard, Integer> transpositionTable; // Tansposition table for minimax
    private Random rn = new Random();
    private int depth = 10; // The initial depth to search when playing a computer move.
    private LinkedList<Integer> depthStack;  
    private LinkedList<Play> compPlayStack;
        /* Whenever a computer move is played, the depth that was used is added
        to the depthStack, and the move is added to the compPlayStack. when 
        a move is undone, it checks if that move is the same as the last move
        the computer played. If so, the depth is set to the depth of the computer
        move played just before, and both the depth and compPlay stacks are 'popped' */ 

    /**
     * Constructs an empty Connect4 game object with RED (1) to start, and an empty playStack.
     */
    public Connect4() {
        current = new GameBoard(COLUMNS, ROWS, TOWIN);
        currentTurn = RED;
        winner = 0;
        playStack = new LinkedList<>();
        depthStack = new LinkedList<>();
        compPlayStack = new LinkedList<>();
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
        if (!playStack.isEmpty()) {
            Play undoneMove = playStack.pop();
            current.popDisk(undoneMove);
            if (undoneMove.equals(compPlayStack.peek())) { 
                // Backtrack the depth. For example if the last move was played using 11 depth, 
                // and that move is undone, the next move needs to be done with 11 depth.
                compPlayStack.pop();
                depth = depthStack.pop();
            }
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

    /**
     * The minimax algorithm does a recursive search of the current position, and tries to find 
     * the absolute best move (in the given depth) based on the expected best move of both players.
     * The position to run the minimax is the position in the {@code current} instance field.
     * @param depth  The depth to search. 
     * @param alpha  Parameter used for alpha-beta pruning. Generally set to -Infinity (-1000 is fine)
     * to start off with.
     * @param beta  Parameter used for alpha-beta pruning. Generally set to Infinity (1000 is fine) to
     * start off with.
     * @param maximizing  Whether or not to search for the maximizing player (which is the player with the
     * disk {@code computerDisk}).
     * @param computerDisk  The maximizing disk.
     * @return  The score of the {@code current} position.
     */
    private int minimax(int depth, int alpha, int beta, boolean maximizing, int computerDisk) {
        // Look up position in the transposition table.
        if (transpositionTable.containsKey(current)) {
            return transpositionTable.get(current);
        }
        // Return if the game has ended, or if the depth is at the maximum.
        int eval = current.checkWin();
        if (depth == 0 || eval != 0) {
            if (eval == 0 || eval == 3) {  // Game ends in a draw, or not over yet
                return 0;
            } else if (eval == computerDisk) { 
                return 100 + depth; // Computer wins. Adds depth to prioritize quick wins.
            } else {
                return -100 - depth; // Computer loses. Subtracts depth to prioritize slow losses.
            }
        }
        boolean broke = false;
        // Maximizing: finds the best way forward for the computer.
        if (maximizing) {
            int maxEval = -1000;
            for (int columnMove : current.getLegal()) {
                // Try all moves and recursively call the minimax algorithm.
                current.putDisk(computerDisk, columnMove);
                eval = minimax(depth - 1, alpha, beta, false, computerDisk);
                maxEval = (eval > maxEval)? eval : maxEval;
                alpha = (alpha > eval)? alpha : eval;
                current.popDisk(columnMove);
                if (beta <= alpha) { // Alpha-beta pruning
                    broke = true;
                    break;
                }
            }
            if (!broke) {
                transpositionTable.put(current, maxEval);
            }
            return maxEval;
        } else { // Minimizing: finds the best way forward for the opposing player.
            int minEval = 1000;
            int playerDisk = 3 - computerDisk;
            for (int columnMove : current.getLegal()) {
                current.putDisk(playerDisk, columnMove);
                eval = minimax(depth - 1, alpha, beta, true, computerDisk);
                minEval = (eval < minEval)? eval : minEval;
                beta = (beta < eval)? beta : eval;
                current.popDisk(columnMove);
                if (beta <= alpha) {
                    broke = true;
                    break;
                }
            }
            if (!broke) {
                transpositionTable.put(current, minEval);
            }
            return minEval;
        }
    }

    public void playComputer() {
        long startTime = System.nanoTime();
        transpositionTable = new HashMap<>();
        int maxEval = -1000;
        int bestPlay = -2;
        for (int columnMove : current.getLegal()) {
            if (Thread.interrupted()) {
                Thread.currentThread().interrupt();
                return; // Check the thread is still meant to be active
            }
            current.putDisk(currentTurn, columnMove);
            int eval = minimax(depth, -1000, 1000, false, currentTurn);
            if (eval > maxEval) {
                bestPlay = columnMove;
                maxEval = eval;
            }
            current.popDisk(columnMove);
        }
        long endTime = System.nanoTime();
        if (!Thread.interrupted()) { // Make sure the thread is still meant to be active before playing...
            compPlayStack.push(new Play(currentTurn, bestPlay)); // This is so the depth can be backtracked
            depthStack.push(depth);                              // after a move is undone.
            play(bestPlay);
            // Adjust the depth for next time so the computer does basically the maximum
            // depth it can without overloading the computer.
            if (depth < (42 - playStack.size())) { // If the depth isn't already maxed
                long timeElapsedms = (endTime - startTime) / 1000000;  // Elapsed time in ms
                if (timeElapsedms < 1500) { // Less than 1.5 seconds
                    depth += 1;
                    if (timeElapsedms < 200) {  // Less than 0.2 seconds
                        depth += 1;
                    }
                } else if (timeElapsedms > 3000) {  // More than 3 seconds
                    depth -= 1;
                    if (timeElapsedms > 7500) {  // More than 7.5 seconds
                        depth -= 1;
                    }
                }
            }
        }
    } 
}