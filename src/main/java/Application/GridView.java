package Application;

import Model.Block;
import Model.BlockState;
import Model.Grid;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GridView extends JPanel {

    private final Timer repaintTimer = new Timer(16, event -> {
        updateFpsCounter();
        for (CellAnimation cellAnimation : CellAnimation.getAnimatingCells()) {
            cellAnimation.step();
            repaintCell(cellAnimation.getRow(), cellAnimation.getColumn());
        }
    });

    private Grid grid;
    private List<List<CellAnimation>> cellAnimations;
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
        this.cellAnimations = createCellAnimations(grid);
        CellAnimation.clearAnimatingCells();
        revalidate();
        repaint();
    }

    private List<List<CellAnimation>> createCellAnimations(Grid grid) {
        List<List<CellAnimation>> cellAnimations = new java.util.ArrayList<>();
        for (int row = 0; row < grid.getRows(); row++) {
            cellAnimations.add(row, new java.util.ArrayList<>());
            for (int col = 0; col < grid.getColumns(); col++) {
                cellAnimations.get(row).add(col, new CellAnimation(row, col));
            }
        }
        return cellAnimations;
    }

    public void applyBlockChange(int row, int col, BlockState state, boolean animate) {
        CellAnimation cellAnimation = cellAnimations.get(row).get(col);
        Color targetColor = getTargetColor(state);

        if (animate && App.isFadeChecked && state == BlockState.PATH) {
            cellAnimation.startPathAnimation(targetColor);
        } else if (animate && App.isFadeChecked) {
            cellAnimation.startFadeAnimation(targetColor);
        } else {
            cellAnimation.setCurrentColor(targetColor);
        }

        repaintCell(row, col);
    }

    public void refreshColors() {
        if (grid == null || cellAnimations == null) {
            return;
        }

        CellAnimation.clearAnimatingCells();
        for (int row = 0; row < grid.getRows(); row++) {
            for (int col = 0; col < grid.getColumns(); col++) {
                BlockState state = grid.getBlock(row, col).getState();
                cellAnimations.get(row).get(col).setCurrentColor(getTargetColor(state));
            }
        }
        repaint();
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

        if (grid == null || cellAnimations == null) {
            return;
        }

        for (int row = 0; row < grid.getRows(); row++) {
            for (int col = 0; col < grid.getColumns(); col++) {
                CellAnimation cellAnimation = cellAnimations.get(row).get(col);
                Rectangle bounds = getCellBounds(row, col);

                g.setColor(cellAnimation.getCurrentColor());
                fillCell(g, bounds, 1.0);
            }
        }

        boolean drawBorders = cellSize > 8;

        if (drawBorders) {
            g.setColor(App.BLOCK_BORDER_COLOR);
            for (int row = 0; row < grid.getRows(); row++) {
                for (int col = 0; col < grid.getColumns(); col++) {
                    Rectangle bounds = getCellBounds(row, col);
                    g.drawRect(bounds.x, bounds.y, bounds.width - 1, bounds.height - 1);
                }
            }
        }

        if (App.isFadeChecked) {
            for (CellAnimation cellAnimation : CellAnimation.getAnimatingCells()) {
                if (cellAnimation.getScale() <= 1.0) {
                    continue;
                }
                Rectangle bounds = getCellBounds(cellAnimation.getRow(), cellAnimation.getColumn());
                g.setColor(cellAnimation.getCurrentColor());
                fillCell(g, bounds, cellAnimation.getScale());
                g.setColor(App.BLOCK_BORDER_COLOR);
                if (drawBorders) drawCellBorder(g, bounds, cellAnimation.getScale());
            }
        }
    }

    private void repaintCell(int row, int column) {
        if (grid == null || row < 0 || row >= grid.getRows() || column < 0 || column >= grid.getColumns()) {
            return;
        }

        Rectangle bounds = getCellBounds(row, column);
        int overflow = (int) Math.ceil(cellSize * ((CellAnimation.getPathStartScale() - 1.0) / 2.0));
        repaint(
                bounds.x - overflow,
                bounds.y - overflow,
                bounds.width + (2 * overflow) + 1,
                bounds.height + (2 * overflow) + 1
        );
    }

    private void fillCell(Graphics g, Rectangle bounds, double scale) {
        int scaledWidth = (int) Math.round(bounds.width * scale);
        int scaledHeight = (int) Math.round(bounds.height * scale);
        int x = bounds.x - ((scaledWidth - bounds.width) / 2);
        int y = bounds.y - ((scaledHeight - bounds.height) / 2);
        g.fillRect(x, y, scaledWidth, scaledHeight);
    }

    private void drawCellBorder(Graphics g, Rectangle bounds, double scale) {
        int scaledWidth = (int) Math.round(bounds.width * scale);
        int scaledHeight = (int) Math.round(bounds.height * scale);
        int x = bounds.x - ((scaledWidth - bounds.width) / 2);
        int y = bounds.y - ((scaledHeight - bounds.height) / 2);
        g.drawRect(x, y, scaledWidth - 1, scaledHeight - 1);
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

    private Color getTargetColor(BlockState state) {
        return switch (state) {
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
