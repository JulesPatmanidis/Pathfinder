package Utilities;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ipat
 */
public class testGui {

    int BLOCKS = 200;
    int RATIO = 16/9;
    JPanel mainPanel;
    List<List<JButton>> buttonList;
    /**
     * Creates new form Utilities.testGui
     */
    public testGui() {
        initComponents();
    }


    private void initComponents() {
        mainPanel = new JPanel(new GridLayout(BLOCKS, BLOCKS * RATIO));
        addBlocks();
    }

    public void addBlocks() {
        buttonList = new ArrayList<>();
        for (int i = 0; i < BLOCKS; i++) {
            buttonList.add(new ArrayList<>());
            for (int j = 0; j < BLOCKS * RATIO; j++) {
                JButton button = new JButton();
                button.setBackground(Color.CYAN);
                button.setPreferredSize(new Dimension(5,5));
                buttonList.get(i).add(button);
                mainPanel.add(button);
            }
        }

    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("FRAME_TITLE");
        frame.setResizable(true);
        frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        //frame.setMaximumSize(Toolkit.getDefaultToolkit().getScreenSize());
        frame.setContentPane(new testGui().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }
}
