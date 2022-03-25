package connectgame;

import connectgame.ConnectGameUI.GameMode;
import connectgame.engine.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.*;

public class GUI extends MouseAdapter { //TODO make newgame buttons and maybe combine settext and component declaration.

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
        new ImageIcon("images/Blank.png"), // Blank spot (0)
        new ImageIcon("images/Red.png"), // 'Red' (1)
        new ImageIcon("images/Yellow.png"), // 'Yellow' (2)
        new ImageIcon("images/Blank.png") // This is the icon to display in case of a draw
    };

    private static final String[] PLAYER = {"Error", "Red", "Yellow"};

    private ConnectGameUI ui;
    private Screen currentScreen;
    private ComputerMove computerMoveThread;
    private int currentWidth;
    private int currentHeight;

    final String fontName = "Arial Bold";
    Font titleFont = new Font(fontName, Font.PLAIN, 40);
    Font selectorFont = new Font(fontName, Font.PLAIN, 20);
    Font subtitleFont = new Font(fontName, Font.PLAIN, 30);

    private JFrame frame;
    private JPanel[] panels;
    
    //Game Screen
    private JLabel gameTitle;  // Move this
    private JLabel[][] board;  
    //Start Screen
    private JLabel startTitle; // Move these
    private JButton startButton;
    
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
        init();
    }

    /**
     * Initializes the GUI.
     */
    private synchronized void init() {
        currentWidth = 13 * DISK_SIZE;
        currentHeight = 9 * DISK_SIZE;

        panels = new JPanel[3];
        initStartScreen();
        initNewGameScreen();
        
        frame.setSize(currentWidth, currentHeight);
        frame.setTitle("Java Connect 4");
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
        gameTitle.setText("Connect 4");
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
        startTitle.setText("Connect 4");
        startTitle.setFont(new Font("Arial Bold", Font.PLAIN, 60));
        startTitle.setBounds((currentWidth/2) - 160,(currentHeight/2) - 80,300,50);

        startButton = new JButton("Play");
        startButton.setFont(new Font("Arial Bold", Font.PLAIN, 30));
        startButton.setBounds((currentWidth/2) - 110,(currentHeight/2) + 10,200,40);
        startButton.addActionListener(new ActionListener() {
            @Override 
            public void actionPerformed(ActionEvent e) {
                setCurrentScreen(Screen.NEW_GAME_SCREEN);
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

        JLabel newGameTitle = new JLabel("New Game");
        newGameTitle.setFont(titleFont);
        newGameTitle.setBounds(10,15,600,30);

        JLabel gameModeSelectTitle = new JLabel("Game Mode:");
        gameModeSelectTitle.setBounds(10, 60, 200, 40);
        gameModeSelectTitle.setFont(subtitleFont);

        JRadioButton pvpSelect = new JRadioButton("Player vs Player"); 
        pvpSelect.setBounds(10, 100, 250, 40);
        pvpSelect.setFont(selectorFont);
        pvpSelect.setActionCommand("pvp");
        JRadioButton pvrSelect = new JRadioButton("Player vs Random"); 
        pvrSelect.setBounds(10, 140, 250, 40);
        pvrSelect.setFont(selectorFont);
        pvrSelect.setActionCommand("pvr");
        JRadioButton pvcSelect = new JRadioButton("Player vs Computer"); 
        pvcSelect.setBounds(10, 180, 250, 40);
        pvcSelect.setFont(selectorFont);
        pvcSelect.setActionCommand("pvc");

        ButtonGroup gameModeSelect = new ButtonGroup();
        gameModeSelect.add(pvpSelect);
        gameModeSelect.add(pvrSelect);
        gameModeSelect.add(pvcSelect);
        pvpSelect.setSelected(true);

        JLabel startSelectTitle = new JLabel("Starting Player:");
        startSelectTitle.setBounds(265, 60, 350, 40);
        startSelectTitle.setFont(subtitleFont);
        
        JRadioButton randomStartSelect = new JRadioButton("Random");
        randomStartSelect.setBounds(265, 100, 250, 40);
        randomStartSelect.setFont(selectorFont);
        randomStartSelect.setActionCommand("random");
        JRadioButton redStartSelect = new JRadioButton("Red (You Start)");
        redStartSelect.setFont(selectorFont);
        redStartSelect.setBounds(265, 140, 250, 40);
        redStartSelect.setActionCommand("red");
        JRadioButton yellowStartSelect = new JRadioButton("Yellow (Computer Starts)");
        yellowStartSelect.setFont(selectorFont);
        yellowStartSelect.setBounds(265, 180, 300, 40);
        yellowStartSelect.setActionCommand("yellow");

        ButtonGroup startPlayerSelect = new ButtonGroup();
        startPlayerSelect.add(randomStartSelect);
        startPlayerSelect.add(redStartSelect);
        startPlayerSelect.add(yellowStartSelect);
        randomStartSelect.setSelected(true);

        JButton startGameButton = new JButton("Start Game!");
        startGameButton.setBounds(120, 250, 200, 40);
        startGameButton.setFont(subtitleFont);
        startGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ConnectGameUI.GameMode ngGameMode;
                int ngStartPlayer;

                switch (gameModeSelect.getSelection().getActionCommand()) {
                    case "pvp": ngGameMode = ConnectGameUI.GameMode.PLAYER_V_PLAYER; break;
                    case "pvr": ngGameMode = ConnectGameUI.GameMode.PLAYER_V_RANDOM; break;
                    case "pvc": ngGameMode = ConnectGameUI.GameMode.PLAYER_V_COMPUTER; break;
                    default: ngGameMode = ConnectGameUI.GameMode.PLAYER_V_PLAYER; // Should never happen.
                }
                switch (startPlayerSelect.getSelection().getActionCommand()) {
                    case "random": ngStartPlayer = new Random().nextInt(1, 3); break;
                    case "red": ngStartPlayer = GameBoard.RED; break;
                    case "yellow": ngStartPlayer = GameBoard.YELLOW; break;
                    default: ngStartPlayer = 1; // Should never happen.
                }
                ui = new ConnectGameUI(ngGameMode, ngStartPlayer);
                initGameScreen();
                if (!ui.isPlayersTurn()) {
                    Thread compMove = new ComputerMove();
                    compMove.start();
                }
                setCurrentScreen(Screen.GAME_SCREEN);
            }
        });

        panels[panelNo].add(newGameTitle);
        panels[panelNo].add(gameModeSelectTitle);
        panels[panelNo].add(pvpSelect);
        panels[panelNo].add(pvrSelect);
        panels[panelNo].add(pvcSelect);
        panels[panelNo].add(startSelectTitle);
        panels[panelNo].add(randomStartSelect);
        panels[panelNo].add(redStartSelect);
        panels[panelNo].add(yellowStartSelect);
        panels[panelNo].add(startGameButton);

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
    public synchronized void updateGameScreen() {
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
            updateGameScreen();
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
                updateGameScreen();
            }
        }
    }
}