package connectgame;

import connectgame.engine.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

public class GUI extends MouseAdapter {

    private static final int DISK_SIZE = 50;

    private ConnectGameUI ui;
    private Click currentClickThread;

    //Components
    private JFrame frame;
    private JPanel mainPanel;
    
    public GUI() {
        frame = new JFrame();
    }

    /**
     * Initializes the GUI with all settings default:
     * <ul>
     * <li>ConnectGame: Connect4
     * <li>Game Mode: Player v Player
     * <li>
     */
    public void init() {
        ui = new ConnectGameUI(ConnectGameUI.GameMode.PLAYER_V_PLAYER);
        frame.setSize(ui.gameColumns() * DISK_SIZE + 200, ui.gameRows() * DISK_SIZE + 100);
        frame.setTitle("Connect 4");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.addMouseListener(this);
        frame.setVisible(true); //TODO working on this currently
    }

    public void updateGUI() {
        // TODO: will implement this when I've created the GUI!!
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
