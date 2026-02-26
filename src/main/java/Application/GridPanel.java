package Application;

import Utilities.Utils;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GridPanel extends JPanel {

    private final Timer repaintTimer = new Timer(16, _ -> {
        // Update fade ratios for animating rectangles
        for (FadeRect rect : FadeRect.getAnimatingRects()) {
            rect.incrementFadeRatio();
        }
        repaint();
    });

    private List<List<Block>> blockList;

    {
        repaintTimer.start();
    }

    public GridPanel() {
        super();
    }

    public void setBlockList(List<List<Block>> blockList) {
        this.blockList = blockList;
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
                        case WALKED -> {
                            if (block.getRect().isInAnimation() && App.isFadeChecked) {
                                double fadeRatio = block.getRect().getFadeRatio();
                                g.setColor(Utils.fadeColor(Color.ORANGE, App.ACCENT_COLOR, fadeRatio));
                            } else {
                                g.setColor(App.ACCENT_COLOR);
                            }
                        }
                        case NEIGHBOUR -> {
                            if (block.getRect().isInAnimation() && App.isFadeChecked) {
                                double fadeRatio = block.getRect().getFadeRatio();
                                g.setColor(Utils.fadeColor(App.BLOCK_COLOR, Color.ORANGE, fadeRatio));
                            } else {
                                g.setColor(Color.ORANGE);
                            }
                        }
                        case WALKABLE -> g.setColor(App.BLOCK_COLOR);
                        case NON_WALKABLE -> g.setColor(App.OBSTACLE_COLOR);
                        case PATH -> {
                            if (block.getRect().isInAnimation() && App.isFadeChecked) {
                                double fadeRatio = block.getRect().getFadeRatio();
                                g.setColor(Utils.fadeColor(App.ACCENT_COLOR, App.PATH_COLOR, fadeRatio));
                            } else {
                                g.setColor(App.PATH_COLOR);
                            }
                        }
                    }

                    g.fillRect(
                            block.getRect().x,
                            block.getRect().y,
                            block.getRect().width,
                            block.getRect().height
                    );
                }
            }

            // Draw the borders of the blocks
            g.setColor(App.BLOCK_BORDER_COLOR);
            for (List<Block> row : blockList) {
                for (Block block : row) {
                    g.drawRect(
                            block.getRect().x,
                            block.getRect().y,
                            block.getRect().width,
                            block.getRect().height);
                }
            }
        }
    }
}
