package connectgame;

import connectgame.ConnectGameUI.GameMode;
import connectgame.engine.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.*;

public class GUI extends MouseAdapter { //TODO make the thing switch screens properly!!!

    public enum Screen {
        GAME_SCREEN(0),
        START_SCREEN(1),
        NEW_GAME_SCREEN(2);

        private final int panelArrayPosition;

        private Screen(int panelArrayPosition) {
            this.panelArrayPosition = panelArrayPosition;
        }

        public int panelArrayPosition() {
            return this.panelArrayPosition;
        }
    }

    private static final int DISK_SIZE = 50;
    private static final ImageIcon[] DISK_ICONS = {
        new ImageIcon("images/Blank.png"),
        new ImageIcon("images/Red.png"),
        new ImageIcon("images/Yellow.png")
    };

    private static final String[] PLAYER = {"Error", "Red", "Yellow"};

    private ConnectGameUI ui;
    private Screen currentScreen;
    private ComputerMove computerMoveThread;
    private int currentWidth;
    private int currentHeight;

    //Components
    private JFrame frame;
    private JPanel[] panels;
    //Game Screen
    private JLabel gameTitle;
    private JLabel[][] board;
    //Start Screen
    private JLabel startTitle;
    private JButton startButton;
    //New Game Screen
    private JLabel newGameTitle;
    
    /**
     * Creates a ConnectGameGUI with all settings default:
     * <ul>
     * <li>ConnectGame: Connect4
     * <li>Game Mode: Player v Player
     */
    public GUI() {
        try {
            UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName()
            );
        } catch (Exception ignore) {
            // Just keeps the default Look and Feel
        }

        frame = new JFrame();
        ui = new ConnectGameUI();
        init();
    }

    /**
     * Initializes the GUI.
     */
    private synchronized void init() {
        currentWidth = (ui.gameColumns() + 6) * DISK_SIZE;
        currentHeight = (ui.gameColumns() + 3) * DISK_SIZE;

        panels = new JPanel[3];
        initGameScreen();
        initStartScreen();
        initNewGameScreen();
        
        frame.setSize(currentWidth, currentHeight);
        frame.setTitle("Java Connect " + ui.getGame().toWin());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setCurrentScreen(Screen.START_SCREEN);
    }

    /**
     * Initializes the Game Screen.
     */
    private synchronized void initGameScreen() {
        final int panelNo = Screen.GAME_SCREEN.panelArrayPosition();
        panels[panelNo] = new JPanel();
        panels[panelNo].setBorder(BorderFactory.createEmptyBorder(currentWidth, currentHeight, currentWidth, currentHeight));
        panels[panelNo].setLayout(null);

        gameTitle = new JLabel();
        gameTitle.setText("Java Connect " + ui.getGame().toWin());
        gameTitle.setFont(new Font("Arial Bold", Font.PLAIN, 40));
        gameTitle.setBounds(10,15,300,30);
        panels[panelNo].add(gameTitle);

        initBoard();
        updateBoard();
    }

    /**
     * Initializes the Start Screen.
     */
    private synchronized void initStartScreen() {
        final int panelNo = Screen.START_SCREEN.panelArrayPosition();
        panels[panelNo] = new JPanel();
        panels[panelNo].setBorder(BorderFactory.createEmptyBorder(currentWidth, currentHeight, currentWidth, currentHeight));
        panels[panelNo].setLayout(null);

        startTitle = new JLabel();
        startTitle.setText("Connect " + ui.getGame().toWin());
        startTitle.setFont(new Font("Arial Bold", Font.PLAIN, 60));
        startTitle.setBounds((currentWidth/2) - 160,(currentHeight/2) - 80,300,50);

        startButton = new JButton();
        startButton.setText("Play");
        startButton.setFont(new Font("Arial Bold", Font.PLAIN, 30));
        startButton.setBounds((currentWidth/2) - 110,(currentHeight/2) + 10,200,40);
        startButton.addActionListener(new ActionListener() {
            @Override 
            public void actionPerformed(ActionEvent e) {
                setCurrentScreen(Screen.GAME_SCREEN);
            }
        });

        panels[panelNo].add(startTitle);
        panels[panelNo].add(startButton);
    }

    /**
     * Initializes the New Game options Screen.
     */
    private synchronized void initNewGameScreen() {
        final int panelNo = Screen.NEW_GAME_SCREEN.panelArrayPosition();
        panels[panelNo] = new JPanel();
        panels[panelNo].setBorder(BorderFactory.createEmptyBorder(currentWidth, currentHeight, currentWidth, currentHeight));
        panels[panelNo].setLayout(null);
        newGameTitle = new JLabel();
        newGameTitle.setText("Java Connect " + ui.getGame().toWin() + " New Game");
        newGameTitle.setFont(new Font("Arial Bold", Font.PLAIN, 37));
        newGameTitle.setBounds(10,15,600,30);

        panels[panelNo].add(newGameTitle);
    }

    /**
     * Sets the given screen to the current screen, removing other screens(panels) from the frame.
     * Note: This kills the current ComputerMove Thread.
     * @param screen  The screen to set.
     */
    private synchronized void setCurrentScreen(Screen newScreen) {
        if (currentScreen != null) {
            frame.remove(panels[currentScreen.panelArrayPosition()]);
        }
        frame.add(panels[newScreen.panelArrayPosition()]);
        currentScreen = newScreen;
        
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Initialize the board as an array of JLabels with ImageIcons, and add them to the mainPanel.
     */
    private synchronized void initBoard() {
        final int columns = ui.gameColumns();
        final int rows = ui.gameRows();
        board = new JLabel[ui.gameColumns()][ui.gameRows()];
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                board[i][j] = new JLabel();
                board[i][j].setBounds(i * DISK_SIZE,((rows) * DISK_SIZE - (j) * DISK_SIZE) + 15, DISK_SIZE, DISK_SIZE);
                panels[0].add(board[i][j]);
            }
        }
    }

    /**
     * Updates the board to reflect the current GameBoard.
     */
    private synchronized void updateBoard() {
        for (int i = 0; i < ui.gameColumns(); i++) {
            for (int j = 0; j < ui.gameRows(); j++) {
                board[i][j].setIcon(DISK_ICONS[ui.getGameBoard().get(i, j)]);
            }
        }
    }

    /**
     * Show and start the GUI.
     */
    public synchronized void start() {
        frame.addMouseListener(this);
        frame.setVisible(true);
    }

    /**
     * Resets ConnectGameGUI with all settings default:
     * <ul>
     * <li>ConnectGame: Connect4
     * <li>Game Mode: Player v Player
     */
    public synchronized void newGame() {
        setCurrentScreen(Screen.NEW_GAME_SCREEN);
    }

    /**
     * Updates the GUI.
     */
    public synchronized void updateGUI() {
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
     * @return 1 if the game has ended and no action necessary,
     *         2 if a new game has started
     *         0 if no action was taken. 
     */
    private synchronized int movePlayed() {
        if (ui.getWinner() != 0 && !ui.isDone()) {
            ui.finish();
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
                case 0: return 1;
                case 1: newGame(); return 2;
                case 2: System.exit(0);
            }
        }
        return 0;
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        if (currentScreen == Screen.GAME_SCREEN) {
            // This will send a new mouseEvent to the UI, unless it is already handling one. 
            if (computerMoveThread != null && computerMoveThread.isAlive()) {
                return;
            }
            ui.playerMousePressed(mouseEvent);
            updateGUI();
            int action = movePlayed();
            if (action == 0) {
                computerMoveThread = new ComputerMove();
                computerMoveThread.start();
            }
            movePlayed();
        }
    }

    /**
     * The purpose of this private class is to be able to have the UI handling click events in a separate thread 
     * to avoid unresponsiveness when calculating the computer move. 
     */
    private class ComputerMove extends Thread {

        private boolean killed = false;

        @Override 
        public void run() {
            ui.computerTurn(); // This method will only play the computer's move if it is it's turn, so we can call it here safely.
            if (!killed) {
                updateGUI();
            }
        }
    }
}