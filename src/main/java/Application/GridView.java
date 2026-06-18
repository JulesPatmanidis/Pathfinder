package Application;

import Model.Block;
import Model.Grid;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GridView extends JPanel {

    private final Timer repaintTimer = new Timer(16, event -> {
        updateFpsCounter();
        for (FadeRect rect : FadeRect.getAnimatingRects()) {
            rect.incrementFadeRatio();
            repaintCell(rect.getRow(), rect.getColumn());
        }
    });

    private Grid grid;
    private List<List<FadeRect>> fadeRects;
    private int cellSize;
    private long fpsWindowStartNanos = System.nanoTime();
    private int timerTicksInWindow = 0;
    private int currentFps = 0;

    public GridView() {
        super();
        repaintTimer.start();
    }

    public void setGrid(Grid grid, int cellSize) {
        this.grid = grid;
        this.cellSize = cellSize;
        this.fadeRects = createFadeRects(grid);
        FadeRect.clearAnimatingRects();
        revalidate();
        repaint();
    }

    private List<List<FadeRect>> createFadeRects(Grid grid) {
        List<List<FadeRect>> fadeRects = new java.util.ArrayList<>();
        for (int row = 0; row < grid.getRows(); row++) {
            fadeRects.add(row, new java.util.ArrayList<>());
            for (int col = 0; col < grid.getColumns(); col++) {
                fadeRects.get(row).add(col, new FadeRect(row, col));
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
        repaintCell(block.getRow(), block.getColumn());
    }

    public int getCurrentFps() {
        return currentFps;
    }

    @Override
    public Dimension getPreferredSize() {
        Insets insets = getInsets();
        if (grid == null) {
            return new Dimension(insets.left + insets.right, insets.top + insets.bottom);
        }

        return new Dimension(
                insets.left + getGridPixelWidth() + insets.right,
                insets.top + getGridPixelHeight() + insets.bottom
        );
    }

    public int getColumnAtX(int x) {
        if (grid == null) {
            return -1;
        }

        int relativeX = x - getGridOriginX();
        if (relativeX < 0 || relativeX >= getGridPixelWidth()) {
            return -1;
        }
        return relativeX / cellSize;
    }

    public int getRowAtY(int y) {
        if (grid == null) {
            return -1;
        }

        int relativeY = y - getGridOriginY();
        if (relativeY < 0 || relativeY >= getGridPixelHeight()) {
            return -1;
        }
        return relativeY / cellSize;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (grid == null || fadeRects == null) {
            return;
        }

        for (int row = 0; row < grid.getRows(); row++) {
            for (int col = 0; col < grid.getColumns(); col++) {
                FadeRect fadeRect = fadeRects.get(row).get(col);
                Block block = grid.getBlock(row, col);
                Rectangle bounds = getCellBounds(row, col);

                if (fadeRect.isInAnimation() && App.isFadeChecked) {
                    g.setColor(fadeRect.getCurrentColor());
                } else {
                    Color targetColor = getTargetColor(block);
                    fadeRect.setCurrentColor(targetColor);
                    g.setColor(targetColor);
                }

                g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
            }
        }

        if (cellSize > 8) {
            g.setColor(App.BLOCK_BORDER_COLOR);
            for (int row = 0; row < grid.getRows(); row++) {
                for (int col = 0; col < grid.getColumns(); col++) {
                    Rectangle bounds = getCellBounds(row, col);
                    g.drawRect(bounds.x, bounds.y, bounds.width - 1, bounds.height - 1);
                }
            }
        }
    }

    private void repaintCell(int row, int column) {
        if (grid == null || row < 0 || row >= grid.getRows() || column < 0 || column >= grid.getColumns()) {
            return;
        }

        Rectangle bounds = getCellBounds(row, column);
        repaint(bounds.x, bounds.y, bounds.width + 1, bounds.height + 1);
    }

    private Rectangle getCellBounds(int row, int column) {
        return new Rectangle(
                getGridOriginX() + column * cellSize,
                getGridOriginY() + row * cellSize,
                cellSize,
                cellSize
        );
    }

    private int getGridOriginX() {
        Insets insets = getInsets();
        int usableWidth = getWidth() - insets.left - insets.right;
        return insets.left + Math.max(0, (usableWidth - getGridPixelWidth()) / 2);
    }

    private int getGridOriginY() {
        Insets insets = getInsets();
        int usableHeight = getHeight() - insets.top - insets.bottom;
        return insets.top + Math.max(0, (usableHeight - getGridPixelHeight()) / 2);
    }

    private int getGridPixelWidth() {
        return grid.getColumns() * cellSize;
    }

    private int getGridPixelHeight() {
        return grid.getRows() * cellSize;
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
