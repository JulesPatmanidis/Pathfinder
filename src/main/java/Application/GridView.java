package Application;

import Model.Block;
import Model.BlockState;
import Model.Grid;

import javax.swing.*;
import java.awt.*;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class GridView extends JPanel {

    private final Set<CellAnimation> animatingCells = new LinkedHashSet<>();
    private final Timer repaintTimer = new Timer(16, event -> {
        updateFpsCounter();
        Iterator<CellAnimation> iterator = animatingCells.iterator();
        while (iterator.hasNext()) {
            CellAnimation cellAnimation = iterator.next();

            boolean stillAnimating = cellAnimation.step();
            repaintCell(cellAnimation.getRow(), cellAnimation.getColumn());
            if (!stillAnimating) {
                iterator.remove();
            }
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
        animatingCells.clear();
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
            animatingCells.add(cellAnimation);
        } else if (animate && App.isFadeChecked) {
            cellAnimation.startFadeAnimation(targetColor);
            animatingCells.add(cellAnimation);
        } else {
            cellAnimation.setCurrentColor(targetColor);
            animatingCells.remove(cellAnimation);
        }

        repaintCell(row, col);
    }

    public void refreshColors() {
        if (grid == null || cellAnimations == null) {
            return;
        }

        animatingCells.clear();
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

        // Only repaint "dirty" rectangles (bounding box)
        Rectangle clip = g.getClipBounds();
        int originX = getGridOriginX();
        int originY = getGridOriginY();
        int gridPixelWidth = getGridPixelWidth();
        int gridPixelHeight = getGridPixelHeight();

        int startCol = Math.max(0, (clip.x - originX) / cellSize);
        int endCol = Math.min(grid.getColumns() - 1, (clip.x + clip.width - originX - 1) / cellSize);
        int startRow = Math.max(0, (clip.y - originY) / cellSize);
        int endRow = Math.min(grid.getRows() - 1, (clip.y + clip.height - originY - 1) / cellSize);

        if (clip.x + clip.width <= originX
                || clip.y + clip.height <= originY
                || clip.x >= originX + gridPixelWidth
                || clip.y >= originY + gridPixelHeight) {
            return;
        }

        for (int row = startRow; row <= endRow; row++) {
            int y = originY + row * cellSize;
            for (int col = startCol; col <= endCol; col++) {
                CellAnimation cellAnimation = cellAnimations.get(row).get(col);
                int x = originX + col * cellSize;

                g.setColor(cellAnimation.getCurrentColor());
                g.fillRect(x, y, cellSize, cellSize);
            }
        }

        boolean drawBorders = cellSize > 8;

        if (drawBorders) {
            g.setColor(App.BLOCK_BORDER_COLOR);
            for (int row = startRow; row <= endRow; row++) {
                int y = originY + row * cellSize;
                for (int col = startCol; col <= endCol; col++) {
                    int x = originX + col * cellSize;
                    g.drawRect(x, y, cellSize - 1, cellSize - 1);
                }
            }
        }

        if (App.isFadeChecked) {
            for (CellAnimation cellAnimation : animatingCells) {
                if (cellAnimation.getScale() <= 1.0) {
                    continue;
                }
                int x = originX + cellAnimation.getColumn() * cellSize;
                int y = originY + cellAnimation.getRow() * cellSize;
                g.setColor(cellAnimation.getCurrentColor());
                fillCell(g, x, y, cellAnimation.getScale());
                g.setColor(App.BLOCK_BORDER_COLOR);
                if (drawBorders) {
                    drawCellBorder(g, x, y, cellAnimation.getScale());
                }
            }
        }
    }

    private void repaintCell(int row, int column) {
        if (grid == null || row < 0 || row >= grid.getRows() || column < 0 || column >= grid.getColumns()) {
            return;
        }

        int x = getGridOriginX() + column * cellSize;
        int y = getGridOriginY() + row * cellSize;
        int overflow = (int) Math.ceil(cellSize * ((CellAnimation.getPathStartScale() - 1.0) / 2.0));
        repaint(
                x - overflow,
                y - overflow,
                cellSize + (2 * overflow) + 1,
                cellSize + (2 * overflow) + 1
        );
    }

    private void fillCell(Graphics g, int x, int y, double scale) {
        int scaledWidth = (int) Math.round(cellSize * scale);
        int scaledHeight = (int) Math.round(cellSize * scale);
        int scaledX = x - ((scaledWidth - cellSize) / 2);
        int scaledY = y - ((scaledHeight - cellSize) / 2);
        g.fillRect(scaledX, scaledY, scaledWidth, scaledHeight);
    }

    private void drawCellBorder(Graphics g, int x, int y, double scale) {
        int scaledWidth = (int) Math.round(cellSize * scale);
        int scaledHeight = (int) Math.round(cellSize * scale);
        int scaledX = x - ((scaledWidth - cellSize) / 2);
        int scaledY = y - ((scaledHeight - cellSize) / 2);
        g.drawRect(scaledX, scaledY, scaledWidth - 1, scaledHeight - 1);
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
