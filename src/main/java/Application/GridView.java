package Application;

import Model.Block;
import Model.Grid;
import javax.swing.*;
import java.awt.Color;
import java.util.List;

public class GridView extends JPanel {

    private final Timer repaintTimer = new Timer(16, e -> {
        updateFpsCounter();
        // Update fade ratios for animating rectangles
        for (FadeRect rect : FadeRect.getAnimatingRects()) {
            rect.incrementFadeRatio();
            repaint(rect.x, rect.y, rect.width + 1, rect.height + 1);
        }

    });

    private Grid grid;
    private List<List<FadeRect>> fadeRects;
    private long fpsWindowStartNanos = System.nanoTime();
    private int timerTicksInWindow = 0;
    private int currentFps = 0;

    public GridView() {
        super();
        repaintTimer.start();
    }

    public void setFadeRects(List<List<FadeRect>> fadeRects) {
        this.fadeRects = fadeRects;
        FadeRect.clearAnimatingRects();
        revalidate();
        repaint();
    }

    public void setGrid(Grid grid, int cellSize) {
        this.grid = grid;
        this.fadeRects = createFadeRects(grid, cellSize);
        FadeRect.clearAnimatingRects();
        revalidate();
        repaint();
    }

    private List<List<FadeRect>> createFadeRects(Grid grid, int cellSize) {
        List<List<FadeRect>> fadeRects = new java.util.ArrayList<>();
        for (int row = 0; row < grid.getRows(); row++) {
            fadeRects.add(row, new java.util.ArrayList<>());
            for (int col = 0; col < grid.getColumns(); col++) {
                fadeRects.get(row).add(col, new FadeRect(row, col, cellSize, cellSize));
            }
        }
        return fadeRects;
    }

    public void startAnimation(Block block) {
        fadeRects
                .get(block.getRow())
                .get(block.getColumn())
                .startAnimation(getTargetColor(block));
    }

    public void repaintBlock(Block block) {
        FadeRect fadeRect = fadeRects
                .get(block.getRow())
                .get(block.getColumn());

        repaint(fadeRect.x, fadeRect.y, fadeRect.width + 1, fadeRect.height + 1);
    }

    public int getCurrentFps() {
        return currentFps;
    }

    @Override
    protected void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);

        // Draw the insides of the blocks
        if (grid != null) {
            int cellSize = 0;
            for (int row = 0; row < grid.getRows(); row++) {
                for (int col = 0; col < grid.getColumns(); col++) {
                    FadeRect fadeRect = fadeRects.get(row).get(col);
                    Block block = grid.getBlock(row, col);

                    cellSize = fadeRect.width;
                    Color targetColor = getTargetColor(block);
                    if (fadeRect.isInAnimation() && App.isFadeChecked) {
                        g.setColor(fadeRect.getCurrentColor());
                    } else {
                        fadeRect.setCurrentColor(targetColor);
                        g.setColor(targetColor);
                    }

                    g.fillRect(
                            fadeRect.x,
                            fadeRect.y,
                            fadeRect.width,
                            fadeRect.height
                    );
                }
            }

            // Draw the borders of the blocks
            if (cellSize > 8) {
                g.setColor(App.BLOCK_BORDER_COLOR);
                for (List<FadeRect> row : fadeRects) {
                    for (FadeRect fadeRect : row) {
                        g.drawRect(
                                fadeRect.x,
                                fadeRect.y,
                                fadeRect.width,
                                fadeRect.height);
                    }
                }
            }
        }
    }

    private Color getTargetColor(Block block) {
        return switch (block.getState()) {
            case START_END -> App.PRESSED_COLOR;
            case WALKED -> App.VISITED_COLOR;
            case NEIGHBOUR -> App.NEIGHBOUR_COLOR;
            case WALKABLE -> App.BLOCK_COLOR;
            case NON_WALKABLE -> App.OBSTACLE_COLOR;
            case PATH -> App.PATH_COLOR;
        };
    }

    private void updateFpsCounter() {
        timerTicksInWindow++;
        long now = System.nanoTime();
        long elapsed = now - fpsWindowStartNanos;
        if (elapsed >= 1_000_000_000L) {
            currentFps = (int) Math.round((timerTicksInWindow * 1_000_000_000.0) / elapsed);
            timerTicksInWindow = 0;
            fpsWindowStartNanos = now;
        }
    }
}
