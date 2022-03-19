package connectgame;

import connectgame.engine.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.BorderLayout;

import javax.swing.*;


//TODO make the game stop when someone's won.
public class GUI extends MouseAdapter {

    private static final int DISK_SIZE = 50;
    private static final ImageIcon[] DISK_ICONS = {
        new ImageIcon("images/Blank.png"),
        new ImageIcon("images/Red.png"),
        new ImageIcon("images/Yellow.png")
    };

    private ConnectGameUI ui;
    private Click currentClickThread;

    //Components
    private JFrame frame;
    private JPanel mainPanel;
    private JLabel title;
    private JLabel[][] board;
    
    /**
     * Creates a ConnectGame GUI with all settings default:
     * <ul>
     * <li>ConnectGame: Connect4
     * <li>Game Mode: Player v Player
     */
    public GUI() {
        frame = new JFrame();
        ui = new ConnectGameUI(ConnectGameUI.GameMode.PLAYER_V_RANDOM, GameBoard.YELLOW);
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
        title.setText("<html><h1>Java Connect 4");
        title.setBounds(10,5,200,30);

        mainPanel.add(title);
        
        initBoard();
        updateBoard();

        frame.setSize(width, height);
        frame.setTitle("Java Connect 4");
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
                board[i][j].setBounds(i * DISK_SIZE, (rows - 1) * DISK_SIZE - (j - 1) * DISK_SIZE, DISK_SIZE, DISK_SIZE);
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
     * Updates the GUI.
     */
    public void updateGUI() {
        updateBoard();
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
            ui.mousePressed(mouseEvent);
            updateGUI();
        }
    }
}
