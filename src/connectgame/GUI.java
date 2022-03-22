package connectgame;

import connectgame.ConnectGameUI.GameMode;
import connectgame.engine.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.*;


//TODO make the game stop when someone's won.
public class GUI extends MouseAdapter {

    private static final int DISK_SIZE = 50;
    private static final ImageIcon[] DISK_ICONS = {
        new ImageIcon("images/Blank.png"),
        new ImageIcon("images/Red.png"),
        new ImageIcon("images/Yellow.png")
    };

    private static final String[] PLAYER = {"Error", "Red", "Yellow"};

    private ConnectGameUI ui;
    private Click currentClickThread;

    //Components
    private JFrame frame;
    private JPanel mainPanel;
    private JLabel title;
    private JLabel[][] board;
    
    /**
     * Creates a ConnectGameGUI with all settings default:
     * <ul>
     * <li>ConnectGame: Connect4
     * <li>Game Mode: Player v Player
     */
    public GUI() {
        frame = new JFrame();
        ui = new ConnectGameUI();
        init();
    }

    /**
     * Initializes the GUI.
     */
    private void init() {
        final int width = (ui.gameColumns() + 6) * DISK_SIZE;
        final int height = (ui.gameColumns() + 3) * DISK_SIZE;

        mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(width, height, width, height));
        mainPanel.setLayout(null);

        title = new JLabel();
        title.setText("Java Connect " + ui.getGame().toWin());
        title.setFont(new Font("Arial Bold", Font.PLAIN, 40));
        title.setBounds(10,15,300,30);

        mainPanel.add(title);

        initBoard();
        updateBoard();

        frame.setSize(width, height);
        frame.setTitle("Java Connect " + ui.getGame().toWin());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(mainPanel, BorderLayout.CENTER);
    }

    /**
     * Initialize the board as an array of JLabels with ImageIcons, and add them to the mainPanel.
     */
    private void initBoard() {
        final int columns = ui.gameColumns();
        final int rows = ui.gameRows();
        board = new JLabel[ui.gameColumns()][ui.gameRows()];
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                board[i][j] = new JLabel();
                board[i][j].setBounds(i * DISK_SIZE,((rows) * DISK_SIZE - (j) * DISK_SIZE) + 15, DISK_SIZE, DISK_SIZE);
                mainPanel.add(board[i][j]);
            }
        }
    }

    /**
     * Updates the board to reflect the current GameBoard.
     */
    private void updateBoard() {
        for (int i = 0; i < ui.gameColumns(); i++) {
            for (int j = 0; j < ui.gameRows(); j++) {
                board[i][j].setIcon(DISK_ICONS[ui.getGameBoard().get(i, j)]);
            }
        }
    }

    /**
     * Show and start the GUI.
     */
    public void start() {
        frame.addMouseListener(this);
        frame.setVisible(true);
    }

    /**
     * Resets ConnectGameGUI with all settings default:
     * <ul>
     * <li>ConnectGame: Connect4
     * <li>Game Mode: Player v Player
     */
    public void newGame() {
        ui = new ConnectGameUI();
        init();
    }

    /**
     * Updates the GUI.
     */
    public void updateGUI() {
        updateBoard();
    }

    /**
     * This returns a string that can be used anywhere that notifies the user that the game has ended. 
     * @return  A string notifying the user that the game has ended, and any relevant information.
     */
    private String getEndGameText() {
        switch (ui.getMode()) {
            case PLAYER_V_PLAYER:
                if (ui.getWinner() != 3) {
                    return ui.getGame().toWin() + " in a row! " + PLAYER[ui.getWinner()] + " has won!";
                } else {
                    return "The game ended in a draw. So-so...";
                }

            case PLAYER_V_RANDOM: case PLAYER_V_COMPUTER:
                if (ui.getWinner() == ui.getPlayerDisk()) {
                    return ui.getGame().toWin() + " in a row! You win!";
                } else {
                    return "Better luck next time! The almighty computer got " + ui.getGame().toWin() + " in a row. You lost...";
                }
            default:
                return "An Error ocurred and no message could be displayed. Method: getEndGameText()";
        }
    }

    /**
     * This method does any necessary checks and actions that need to be completed after a move is played.
     */
    private void movePlayed() {
        if (ui.getWinner() != 0) {
            final String[] options = {"OK", "New Game", "Exit"};
            getEndGameText();
            int input = JOptionPane.showOptionDialog(
                frame,
                getEndGameText(),
                "Game Over",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                DISK_ICONS[ui.getWinner()],
                options,
                options[1]
            );
            switch (input) {
                case 0: break;
                case 1: newGame(); break;
                case 2: System.exit(0); break;
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        // This will send a new mouseEvent to the UI, unless it is already handling one. 
        if (currentClickThread != null && currentClickThread.isAlive()) {
            return;
        }
        currentClickThread = new Click(mouseEvent);
        currentClickThread.start();
    }

    /**
     * The purpose of this private class is to be able to have the UI handling click events in a separate thread 
     * to avoid unresponsiveness when calculating the computer move. 
     */
    private class Click extends Thread {

        private MouseEvent mouseEvent;

        /**
         * Creates a new Click thread with a MouseEvent object.
         * @param mouseEvent
         */
        public Click(MouseEvent mouseEvent) {
            this.mouseEvent = mouseEvent;
        }

        @Override 
        public void run() {
            ui.playerMousePressed(mouseEvent);
            updateGUI();
            movePlayed(); // TODO: make this break or return if something happens during the movePlayed... may need to refactor.
            ui.computerTurn(); // This method will only play the computer's move if it is it's turn, so we can call it here safely.
            updateGUI();
            movePlayed();
        }
    }
}
