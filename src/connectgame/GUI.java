package connectgame;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import connectgame.ConnectGameUI.GameMode;
import connectgame.engine.GameBoard;

/**
 * <h4>GUI<h4>
 * This is a class that creates and runs a Connect 4 GUI. It is possible to adapt this,
 * however, to another ConnectGame, although there are some things that would need to 
 * be altered.
 */
public class GUI extends MouseAdapter {
    
    /**
     * This is an enum for all the different possible screens for the GUI
     * to be currently on. Each enum value has a number associated with it 
     * that is its position in the panel array. it can be accessed by 
     * ENUM_VALUE.panelArrayPosition()
     * @implNote  For future implementations, implement each enum value with
     * an actual panel, instead of just an array position. This would be a 
     * LOT easier to work with.
     */
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

    // Important constants for rendering, describing disks etc.
    private static final int DISK_SIZE = 50;
    private static final ImageIcon[] DISK_ICONS = {
        new ImageIcon("images/Blank.png"), // Blank spot (0)
        new ImageIcon("images/Red.png"), // 'Red' (1)
        new ImageIcon("images/Yellow.png"), // 'Yellow' (2)
        new ImageIcon("images/Blank.png") // This is the icon to display in case of a draw
    };
    private static final String[] PLAYER = {"Error", "Red", "Yellow"};
    private static final Color[] PLAYER_COLORS = {Color.DARK_GRAY, Color.RED, Color.ORANGE};

    // Fonts
    private static final String FONT_NAME = "Arial Bold";
    private static final Font TITLE_FONT = new Font(FONT_NAME, Font.PLAIN, 40);
    private static final Font LABEL_FONT = new Font(FONT_NAME, Font.PLAIN, 20);
    private static final Font SUBTITLE_FONT = new Font(FONT_NAME, Font.PLAIN, 30);

    // Sounds
    public static final File CLICK_SOUND_1 = new File("sounds/Click Sound 1.wav"); 
    public static final File MOVE_PLAYED_SOUND = new File("sounds/Move Played Sound.wav");

    // GUI level fields.
    private ConnectGameUI ui;
    private Screen currentScreen;
    private ComputerMove computerMoveThread;
    private int currentWidth;
    private int currentHeight;
    private boolean soundFXToggle = true;
    private JFrame frame;
    private JPanel[] panels;
    
    //Components that need to be accessed by multiple methods:
    //Game Screen
    private JLabel[][] board;  
    private JLabel turnLabel;
    //New Game Screen
    private ButtonGroup gameModeSelect; 
    private ButtonGroup startPlayerSelect;
    private JLabel startSelectTitle;
    private JRadioButton randomStartSelect;
    private JRadioButton redStartSelect;
    private JRadioButton yellowStartSelect;
    private JCheckBox allowUndoCheckBox;
    private Random rn = new Random();
    
    /**
     * Creates a Connect Game GUI with a Connect4 Game.
     * @throws FileNotFoundException
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
     * Plays the sound in the designated file if possible.
     * @param soundFile  The audio file to play.
     */
    public void playSound(File soundFile) {
        if (soundFXToggle) {
            try {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile.toURI().toURL());
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Initializes the Game Screen.
     * @param allowUndo  true if undoing moves is allowed, false if not. 
     * This basically toggles the undo button.
     */
    private synchronized void initGameScreen(boolean allowUndo) {
        final int panelNo = Screen.GAME_SCREEN.panelArrayPosition();
        panels[panelNo] = new JPanel();
        panels[panelNo].setBorder(BorderFactory.createEmptyBorder(currentWidth, currentHeight, currentWidth, currentHeight));
        panels[panelNo].setLayout(null);

        JLabel gameTitle = new JLabel("Connect 4");
        gameTitle.setFont(TITLE_FONT);
        gameTitle.setBounds(10,15,300,30);

        JButton newGameButton = new JButton("New Game");
        newGameButton.setBounds(ui.gameColumns() * DISK_SIZE + 30, ui.gameRows() * DISK_SIZE + 25, 150, 40);
        newGameButton.setFont(LABEL_FONT);
        newGameButton.addActionListener(new GameScreenNewGameButtonListener());

        turnLabel = new JLabel();
        turnLabel.setBounds(ui.gameColumns() * DISK_SIZE + 30, 65, 350, 40);
        turnLabel.setFont(SUBTITLE_FONT);

        if (allowUndo) {
            JButton undoButton = new JButton("Undo");
            undoButton.setBounds(ui.gameColumns() * 50 + 30, 115, 150, 40);
            undoButton.setFont(LABEL_FONT);
            undoButton.addActionListener(new UndoMoveButtonListener());
            panels[panelNo].add(undoButton);
        }

        JButton helpButton = new JButton("Help");
        helpButton.addActionListener(new HelpButtonListener());
        helpButton.setBounds(currentWidth - 100, 5, 80, 20);
        JButton toggleSoundFXButton = new JButton("FX: On");
        toggleSoundFXButton.addActionListener(new ToggleSoundFXListener());
        toggleSoundFXButton.setBounds(currentWidth - 100, 25, 80, 20);

        panels[panelNo].add(toggleSoundFXButton);
        panels[panelNo].add(helpButton);
        panels[panelNo].add(gameTitle);
        panels[panelNo].add(newGameButton);
        panels[panelNo].add(turnLabel);

        initBoard();
        updateGameScreen();
    }

    /**
     * Initializes the Start Screen.
     */
    private synchronized void initStartScreen() {
        final int panelNo = Screen.START_SCREEN.panelArrayPosition();
        panels[panelNo] = new JPanel();
        panels[panelNo].setBorder(BorderFactory.createEmptyBorder(currentWidth, currentHeight, currentWidth, currentHeight));
        panels[panelNo].setLayout(null);

        JLabel startTitle = new JLabel();
        startTitle.setText("Connect 4");
        startTitle.setFont(new Font(FONT_NAME, Font.PLAIN, 60));
        startTitle.setBounds((currentWidth/2) - 160,(currentHeight/2) - 80,300,50);

        JButton startButton = new JButton("Play");
        startButton.setFont(new Font(FONT_NAME, Font.PLAIN, 30));
        startButton.setBounds((currentWidth/2) - 110,(currentHeight/2) + 10,200,40);
        startButton.addActionListener(new StartGameButtonListener());

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
        newGameTitle.setFont(TITLE_FONT);
        newGameTitle.setBounds(10,15,600,30);

        int y = 90;
        JLabel gameModeSelectTitle = new JLabel("Game Mode:");
        gameModeSelectTitle.setBounds(10, y, 200, 40);
        gameModeSelectTitle.setFont(SUBTITLE_FONT);

        JRadioButton pvpSelect = new JRadioButton("Player vs Player", true); // Default
        pvpSelect.setBounds(10, y + 40, 250, 40);
        pvpSelect.setFont(LABEL_FONT);
        pvpSelect.setActionCommand("pvp");
        pvpSelect.addActionListener(new HideStartPlayerSelect());
        JRadioButton pvrSelect = new JRadioButton("Player vs Random"); 
        pvrSelect.setBounds(10, y + 80, 250, 40);
        pvrSelect.setFont(LABEL_FONT);
        pvrSelect.setActionCommand("pvr");
        pvrSelect.addActionListener(new ShowStartPlayerSelect());
        JRadioButton pvcSelect = new JRadioButton("Player vs Computer"); 
        pvcSelect.setBounds(10, y + 120, 250, 40);
        pvcSelect.setFont(LABEL_FONT);
        pvcSelect.setActionCommand("pvc");
        pvcSelect.addActionListener(new ShowStartPlayerSelect());

        gameModeSelect = new ButtonGroup();
        gameModeSelect.add(pvpSelect);
        gameModeSelect.add(pvrSelect);
        gameModeSelect.add(pvcSelect);

        startSelectTitle = new JLabel("Your Colour:");
        startSelectTitle.setBounds(265, y, 350, 40);
        startSelectTitle.setFont(SUBTITLE_FONT);
        
        randomStartSelect = new JRadioButton("Random", true); // Default
        randomStartSelect.setBounds(265, y + 40, 250, 40);
        randomStartSelect.setFont(LABEL_FONT);
        randomStartSelect.setActionCommand("random");
        redStartSelect = new JRadioButton("Red (You Start)");
        redStartSelect.setFont(LABEL_FONT);
        redStartSelect.setBounds(265, y + 80, 250, 40);
        redStartSelect.setActionCommand("red");
        yellowStartSelect = new JRadioButton("Yellow (Computer Starts)");
        yellowStartSelect.setFont(LABEL_FONT);
        yellowStartSelect.setBounds(265, y + 120, 300, 40);
        yellowStartSelect.setActionCommand("yellow");

        startPlayerSelect = new ButtonGroup();
        startPlayerSelect.add(randomStartSelect);
        startPlayerSelect.add(redStartSelect);
        startPlayerSelect.add(yellowStartSelect);

        allowUndoCheckBox = new JCheckBox("Allow Undo?", true);
        allowUndoCheckBox.setFont(LABEL_FONT);
        allowUndoCheckBox.setBounds(50, y + 175, 150, 40);

        JButton startGameButton = new JButton("Start Game!");
        startGameButton.setBounds(120, y + 240, 200, 40);
        startGameButton.setFont(SUBTITLE_FONT);
        startGameButton.addActionListener(new NewGameButtonListener());

        JButton helpButton = new JButton("Help");
        helpButton.addActionListener(new HelpButtonListener());
        helpButton.setBounds(currentWidth - 100, 5, 80, 20);
        JButton toggleSoundFXButton = new JButton("FX: On");
        toggleSoundFXButton.addActionListener(new ToggleSoundFXListener());
        toggleSoundFXButton.setBounds(currentWidth - 100, 25, 80, 20);

        panels[panelNo].add(toggleSoundFXButton);
        panels[panelNo].add(helpButton);
        panels[panelNo].add(newGameTitle);
        panels[panelNo].add(gameModeSelectTitle);
        panels[panelNo].add(pvpSelect);
        panels[panelNo].add(pvrSelect);
        panels[panelNo].add(pvcSelect);
        panels[panelNo].add(allowUndoCheckBox);
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
        playSound(CLICK_SOUND_1);
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
                board[i][j].setBounds(i * DISK_SIZE + 10,((rows) * DISK_SIZE - (j) * DISK_SIZE) + 15, DISK_SIZE, DISK_SIZE);
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
     * Updates the fields, assuming the game in progress.
     */
    private synchronized void updateFieldsMidGame() {
        if (ui.getMode() == GameMode.PLAYER_V_PLAYER) {
            if (ui.getGame().currentTurn() == 1) {
                turnLabel.setText("Red's Turn");
                turnLabel.setForeground(PLAYER_COLORS[GameBoard.RED]);
            } else {
                turnLabel.setText("Yellow's Turn");
                turnLabel.setForeground(PLAYER_COLORS[GameBoard.YELLOW]);
            }
        } else {
            if (ui.isPlayersTurn()) {
                turnLabel.setText("Your Turn...");
                turnLabel.setForeground(PLAYER_COLORS[ui.getPlayerDisk()]);
            } else {
                turnLabel.setText("Thinking...");
                turnLabel.setForeground(PLAYER_COLORS[3 - ui.getPlayerDisk()]);
            }
        }
    }

    /**
     * Updates the fields, assuming the game has ended.
     */
    private synchronized void updateFieldsPostGame() {
        if (ui.getMode() == GameMode.PLAYER_V_PLAYER) {
            if (ui.getWinner() == 1) {
                turnLabel.setText("Red Wins!");
                turnLabel.setForeground(PLAYER_COLORS[GameBoard.RED]);
            } else if (ui.getWinner() == 2) {
                turnLabel.setText("Yellow Wins!");
                turnLabel.setForeground(PLAYER_COLORS[GameBoard.YELLOW]);
            } else {
                turnLabel.setText("Draw!");
                turnLabel.setForeground(PLAYER_COLORS[GameBoard.BLANK]);
            }
        } else {
            if (ui.getPlayerDisk() == ui.getWinner()) {
                turnLabel.setText("You Win!!");
                turnLabel.setForeground(PLAYER_COLORS[ui.getPlayerDisk()]);
            } else if (ui.getWinner() == 3) {
                turnLabel.setText("Draw!");
                turnLabel.setForeground(PLAYER_COLORS[GameBoard.BLANK]);
            } else {
                turnLabel.setText("Computer Wins!!");
                turnLabel.setForeground(PLAYER_COLORS[3 - ui.getPlayerDisk()]);
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
     * Updates the Game Screen.
     */
    public synchronized void updateGameScreen() {
        updateBoard();
        if (ui.getWinner() == 0) {
            updateFieldsMidGame();
        } else {
            updateFieldsPostGame();
        }
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
                    return "The game ended in a draw.";
                }

            case PLAYER_V_RANDOM: case PLAYER_V_COMPUTER:
                if (ui.getWinner() == ui.getPlayerDisk()) {
                    return ui.getGame().toWin() + " in a row! You win!";
                } else if (ui.getWinner() == 3) {
                    return "The game ended in a draw.";
                } else {
                    return "Better luck next time! The computer got " + ui.getGame().toWin() + " in a row. You lost...";
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
                case 1: newGame(); return 2;
                case 2: System.exit(0); break;
                case 0: default: return 1;
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
            if (ui.playerMousePressed(mouseEvent)) {
                playSound(MOVE_PLAYED_SOUND);
            }
            updateGameScreen();
            int action = movePlayed();
            if (action == 0) {
                computerMoveThread = new ComputerMove();
                computerMoveThread.start();
            }
        }
    }

    /**
     * The purpose of this private class is to be able to have the UI handling 
     * click events in a separate thread to avoid unresponsiveness 
     * when calculating the computer move. 
     */
    private class ComputerMove extends Thread {
        @Override 
        public void run() {
            boolean isMovePlayed = ui.computerTurn();
            if (isMovePlayed && !Thread.interrupted()) { // This method will only play the computer's move if it is it's turn, so we can call it here safely.
                playSound(MOVE_PLAYED_SOUND);
                updateGameScreen();
                movePlayed();
            }     
        }
    }

    /**
     * Game Screen:
     * <p>ActionListener for the Undo Button. Undoes the last move/s.
     * If the mode is player v player, undoes the last move.
     * If the mode is player v random or computer, undoes until the time it was the player's turn and 
     * interrupts the computerMoveThread if active.
     */
    private class UndoMoveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (ui.isDone()) {
                return;
            }
            if (ui.getMode() == GameMode.PLAYER_V_PLAYER) {
                ui.undoLast();
            } else {
                if (ui.isPlayersTurn()) {
                    if (ui.getGame().getPlayStack().size() > 1) {
                    ui.undoLast();
                    ui.undoLast();
                    }
                } else {
                    computerMoveThread.interrupt();
                    ui.undoLast();
                    try {
                        computerMoveThread.join();
                    } catch (InterruptedException e1) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
            playSound(CLICK_SOUND_1);
            updateGameScreen(); 
        }
    }

    /**
     * Game Screen:
     * <p>Action Listener for the New Game Button. Asks the user if they 
     * want to start a new game, and does so if OK.
     */
    private class GameScreenNewGameButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int newGameConfirmation = JOptionPane.showConfirmDialog(
                frame,
                "Are you sure? The contents the the current game will be discarded!",
                "Confirm",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            if (newGameConfirmation == JOptionPane.OK_OPTION) {
                if (computerMoveThread != null) {
                    computerMoveThread.interrupt();
                }
                newGame();
            }
        }
    }

    /**
     * Start Screen:
     * <p>Takes the user to the New Game Screen. There is no way to return to the start screen.
     */
    private class StartGameButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            playSound(CLICK_SOUND_1); // This is so the sound gets loaded without lagging as much as possible.
            setCurrentScreen(Screen.NEW_GAME_SCREEN);
        }
    }

    /**
     * New Game Screen:
     * <p>Starts a new game (ui) and initializes the game screen based on 
     * the current selections in the gameModeSelect and startPlayerSelect button groups.
     */
    private class NewGameButtonListener implements ActionListener {
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
                case "random": ngStartPlayer = rn.nextInt(1, 3); break;
                case "red": ngStartPlayer = GameBoard.RED; break;
                case "yellow": ngStartPlayer = GameBoard.YELLOW; break;
                default: ngStartPlayer = 1; // Should never happen.
            }
            ui = new ConnectGameUI(ngGameMode, ngStartPlayer);
            initGameScreen(allowUndoCheckBox.isSelected());
            if (!ui.isPlayersTurn()) {
                Thread compMove = new ComputerMove();
                compMove.start();
            }
            setCurrentScreen(Screen.GAME_SCREEN);
        }
    }

    /**
     * New Game Screen:
     * <p>An Action Listener for hiding the options to choose the starting player 
     * when plaver v player is selected.
     */
    private class HideStartPlayerSelect implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int panelNo = Screen.NEW_GAME_SCREEN.panelArrayPosition();
            panels[panelNo].remove(startSelectTitle);
            panels[panelNo].remove(randomStartSelect);
            panels[panelNo].remove(redStartSelect);
            panels[panelNo].remove(yellowStartSelect);
            panels[panelNo].revalidate();
            panels[panelNo].repaint();
        }
    }

    /**
     * An Action Listener for showing the options to choose the 
     * starting player when player v player is deselected.
     */
    private class ShowStartPlayerSelect implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int panelNo = Screen.NEW_GAME_SCREEN.panelArrayPosition();
            panels[panelNo].add(startSelectTitle);
            panels[panelNo].add(randomStartSelect);
            panels[panelNo].add(redStartSelect);
            panels[panelNo].add(yellowStartSelect);
            panels[panelNo].revalidate();
            panels[panelNo].repaint();
        }
    }

    /**
     * An Action Listener for the help button. 
     * Brings up a JOptionPane that tells you how to play connect 4.
     */
    private class HelpButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            playSound(CLICK_SOUND_1);
            JOptionPane.showMessageDialog(
                frame,
                """
                    The aim of the game is to get 4 of your tokens in a row, 
                    while stopping your opponent from doing the same. When you
                    place a token in a column, it falls down to the next available
                    space in that column. 
                """,
                "About",
                JOptionPane.INFORMATION_MESSAGE
            );
            playSound(CLICK_SOUND_1);
        }
    }

    /**
     * An Action Listener for the sound FX toggle button.
     */
    private class ToggleSoundFXListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            soundFXToggle = !soundFXToggle;
            AbstractButton thisComponent = (AbstractButton) e.getSource();
            if (soundFXToggle) {
                thisComponent.setText("FX: On");
            } else {
                thisComponent.setText("FX: Off");
            }
        }
    }

}