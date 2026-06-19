package Application;

import Model.BlockState;
import Model.Grid;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GridView extends JPanel {

    private final List<CellAnimation> animatingCells = new ArrayList<>();

    private Grid grid;
    private CellAnimation[][] cellAnimations;
    private BufferedImage gridImage;
    private Graphics2D gridGraphics;
    private int cellSize;
    private long fpsWindowStartNanos = System.nanoTime();
    private int timerTicksInWindow = 0;
    private int currentFps = 0;
    private Color borderColor = new Color(
            App.BLOCK_BORDER_COLOR.getRed(),
            App.BLOCK_BORDER_COLOR.getGreen(),
            App.BLOCK_BORDER_COLOR.getBlue(),
            160);

    private final Timer repaintTimer = new Timer(16, event -> {
        updateFpsCounter();
        Insets insets = getInsets();
        int originX = getGridOriginX(insets);
        int originY = getGridOriginY(insets);

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int pathOverflow = getAnimationOverflow();

        Iterator<CellAnimation> iterator = animatingCells.iterator();
        while (iterator.hasNext()) {
            CellAnimation cellAnimation = iterator.next();

            boolean stillAnimating = cellAnimation.step();
            paintCellToBuffer(cellAnimation.getRow(), cellAnimation.getColumn());

            int x = originX + cellAnimation.getColumn() * cellSize;
            int y = originY + cellAnimation.getRow() * cellSize;
            int overflow = cellAnimation.getScale() > 1.0 ? pathOverflow : 0;

            minX = Math.min(minX, x - overflow);
            minY = Math.min(minY, y - overflow);
            maxX = Math.max(maxX, x + cellSize + overflow);
            maxY = Math.max(maxY, y + cellSize + overflow);

            if (!stillAnimating) {
                cellAnimation.setQueuedForAnimation(false);
                iterator.remove();
            }
        }
        // If anything was painted
        if (minX != Integer.MAX_VALUE) {
            repaint(minX, minY, maxX - minX + 1, maxY - minY + 1);
        }
    });

    public GridView() {
        super();
        repaintTimer.start();
    }

    public void setGrid(Grid grid, int cellSize) {
        this.grid = grid;
        this.cellSize = cellSize;
        this.cellAnimations = createCellAnimations(grid);
        refreshBorderColor();
        for (CellAnimation cellAnimation : animatingCells) {
            cellAnimation.setQueuedForAnimation(false);
        }
        animatingCells.clear();
        if (gridGraphics != null) {
            gridGraphics.dispose();
        }

        gridImage = new BufferedImage(getGridPixelWidth(), getGridPixelHeight(), BufferedImage.TYPE_INT_RGB);
        gridGraphics = gridImage.createGraphics();

        paintBuffer();
        revalidate();
        repaint();
    }

    private CellAnimation[][] createCellAnimations(Grid grid) {
        CellAnimation[][] cellAnimations = new CellAnimation[grid.getRows()][grid.getColumns()];

        for (int row = 0; row < cellAnimations.length; row++) {
            for (int col = 0; col < cellAnimations[0].length; col++) {
                cellAnimations[row][col] = new CellAnimation(row, col);
            }
        }
        return cellAnimations;
    }

    public void applyBlockChange(int row, int col, BlockState state, boolean animate) {
        CellAnimation cellAnimation = cellAnimations[row][col];
        Color targetColor = getTargetColor(state);

        if (animate && App.isFadeChecked && state == BlockState.PATH) {
            cellAnimation.startPathAnimation(targetColor);
            queueAnimation(cellAnimation);
        } else if (animate && App.isFadeChecked) {
            cellAnimation.startFadeAnimation(targetColor);
            queueAnimation(cellAnimation);
        } else {
            cellAnimation.setCurrentColor(targetColor);
            if (cellAnimation.isQueuedForAnimation()) {
                cellAnimation.setQueuedForAnimation(false);
                animatingCells.remove(cellAnimation);
            }
        }

        paintCellToBuffer(row, col);
        repaintCell(row, col);
    }

    private void queueAnimation(CellAnimation cellAnimation) {
        if (!cellAnimation.isQueuedForAnimation()) {
            cellAnimation.setQueuedForAnimation(true);
            animatingCells.add(cellAnimation);
        }
    }

    public void refreshColors() {
        if (grid == null || cellAnimations == null) {
            return;
        }

        refreshBorderColor();
        for (CellAnimation cellAnimation : animatingCells) {
            cellAnimation.setQueuedForAnimation(false);
        }
        animatingCells.clear();
        for (int row = 0; row < grid.getRows(); row++) {
            for (int col = 0; col < grid.getColumns(); col++) {
                CellAnimation cellAnimation = cellAnimations[row][col];
                BlockState state = grid.getBlock(row, col).getState();
                cellAnimation.setCurrentColor(getTargetColor(state));
            }
        }
        paintBuffer();
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

        Insets insets = getInsets();

        int relativeX = x - getGridOriginX(insets);
        if (relativeX < 0 || relativeX >= getGridPixelWidth()) {
            return -1;
        }
        return relativeX / cellSize;
    }

    public int getRowAtY(int y) {
        if (grid == null) {
            return -1;
        }

        Insets insets = getInsets();

        int relativeY = y - getGridOriginY(insets);
        if (relativeY < 0 || relativeY >= getGridPixelHeight()) {
            return -1;
        }
        return relativeY / cellSize;
    }

    private void paintBuffer() {
        if (gridImage == null || gridGraphics == null || grid == null || cellAnimations == null) {
            return;
        }

        int originX = 0;
        int originY = 0;
        int startRow = 0;
        int endRow = grid.getRows() - 1;
        int startCol = 0;
        int endCol = grid.getColumns() - 1;

        for (int row = startRow; row <= endRow; row++) {
            int y = originY + row * cellSize;
            for (int col = startCol; col <= endCol; col++) {
                CellAnimation cellAnimation = cellAnimations[row][col];
                int x = originX + col * cellSize;

                gridGraphics.setColor(cellAnimation.getCurrentColor());
                gridGraphics.fillRect(x, y, cellSize, cellSize);
            }
        }

        if (shouldDrawBorders()) {
            drawGridLines(gridGraphics, getBorderColor(), originX, originY, startRow, endRow, startCol, endCol);
        }
    }

    private void paintCellToBuffer(int row, int col) {
        if (gridImage == null || gridGraphics == null || cellAnimations == null
                || row < 0 || row >= cellAnimations.length
                || col < 0 || col >= cellAnimations[row].length) {
            return;
        }

        Graphics g = gridGraphics;
        int x = col * cellSize;
        int y = row * cellSize;

        g.setColor(cellAnimations[row][col].getCurrentColor());
        g.fillRect(x, y, cellSize, cellSize);

        if (shouldDrawBorders()) {
            g.setColor(getBorderColor());
            g.drawLine(x, y, x + cellSize, y);
            g.drawLine(x, y + cellSize, x + cellSize, y + cellSize);
            g.drawLine(x, y, x, y + cellSize);
            g.drawLine(x + cellSize, y, x + cellSize, y + cellSize);
        }
    }

    @Override
    public void removeNotify() {
        if (gridGraphics != null) {
            gridGraphics.dispose();
            gridGraphics = null;
        }
        super.removeNotify();
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (gridImage == null) {
            return;
        }

        Insets insets = getInsets();
        int originX = getGridOriginX(insets);
        int originY = getGridOriginY(insets);

        g.drawImage(gridImage, originX, originY, null);
        paintScaleAnimations(g, originX, originY);
    }

    private void paintScaleAnimations(Graphics g, int originX, int originY) {
        if (!App.isFadeChecked) {
            return;
        }

        boolean drawBorders = shouldDrawBorders();
        Color borderColor = getBorderColor();

        for (CellAnimation cellAnimation : animatingCells) {
            if (cellAnimation.getScale() <= 1.0) {
                continue;
            }
            int x = originX + cellAnimation.getColumn() * cellSize;
            int y = originY + cellAnimation.getRow() * cellSize;
            g.setColor(cellAnimation.getCurrentColor());
            fillCell(g, x, y, cellAnimation.getScale());

            if (drawBorders) {
                g.setColor(borderColor);
                drawCellBorder(g, x, y, cellAnimation.getScale());
            }
        }
    }

    private void drawGridLines(
            Graphics g,
            Color borderColor,
            int originX,
            int originY,
            int startRow,
            int endRow,
            int startCol,
            int endCol
    ) {
        g.setColor(borderColor);

        int startX = originX + startCol * cellSize;
        int endX = originX + (endCol + 1) * cellSize;
        int startY = originY + startRow * cellSize;
        int endY = originY + (endRow + 1) * cellSize;

        for (int col = startCol; col <= endCol + 1; col++) {
            int x = originX + col * cellSize;
            g.drawLine(x, startY, x, endY);
        }

        for (int row = startRow; row <= endRow + 1; row++) {
            int y = originY + row * cellSize;
            g.drawLine(startX, y, endX, y);
        }
    }

    private boolean shouldDrawBorders() {
        return cellSize > 6;
    }

    private Color getBorderColor() {
        return borderColor;
    }

    private void refreshBorderColor() {
        borderColor = new Color(
                App.BLOCK_BORDER_COLOR.getRed(),
                App.BLOCK_BORDER_COLOR.getGreen(),
                App.BLOCK_BORDER_COLOR.getBlue(),
                160
        );
    }

    private void repaintCell(int row, int column) {
        if (grid == null || row < 0 || row >= grid.getRows() || column < 0 || column >= grid.getColumns()) {
            return;
        }

        Insets insets = getInsets();
        int x = getGridOriginX(insets) + column * cellSize;
        int y = getGridOriginY(insets) + row * cellSize;
        int overflow = cellAnimations[row][column].getScale() > 1.0 ? getAnimationOverflow() : 0;
        repaint(
                x - overflow,
                y - overflow,
                cellSize + (2 * overflow) + 1,
                cellSize + (2 * overflow) + 1
        );
    }

    private int getAnimationOverflow() {
        return (int) Math.ceil(cellSize * ((CellAnimation.getPathStartScale() - 1.0) / 2.0));
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

    private int getGridOriginX(Insets insets) {
        int usableWidth = getWidth() - insets.left - insets.right;
        return insets.left + Math.max(0, (usableWidth - getGridPixelWidth()) / 2);
    }

    private int getGridOriginY(Insets insets) {
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
