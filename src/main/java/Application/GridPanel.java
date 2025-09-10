package Application;

import Utilities.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class GridPanel extends JPanel {



    private List<List<Block>> blockList;

    public GridPanel(List<List<Block>> blockList) {
        super();
        this.blockList = blockList;

    }

    public GridPanel() {
        super();
    }

    public void setBlockList(List<List<Block>> blockList) {
        this.blockList = blockList;
    }

    private final Timer repaintTimer = new Timer(15, e -> repaint());

    {
        repaintTimer.start();
    }


    @Override
    protected void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);

        // Draw the insides of the blocks
        if (blockList != null) {
            for (List<Block> row : blockList) {
                for (Block block : row) {

                    switch (block.getState()) {
                        case START_END -> g.setColor(App.PRESSED_COLOR);
                        case WALKED -> g.setColor(App.ACCENT_COLOR);
                        case NEIGHBOUR -> g.setColor(Color.BLUE);
                        case WALKABLE -> g.setColor(App.BLOCK_COLOR);
                        case NON_WALKABLE -> g.setColor(App.OBSTACLE_COLOR);
                        case PATH -> g.setColor(Color.YELLOW);
                    }

                    g.fillRect(block.getColumn() * 18, block.getRow() * 18, 18, 18);
                }
            }

            // Draw the borders of the blocks
            g.setColor(App.BLOCK_BORDER_COLOR);
            for (List<Block> row : blockList) {
                for (Block block : row) {
                    g.drawRect(block.getColumn() * 18, block.getRow() * 18, 18, 18);
                }
            }
        }
    }
}
