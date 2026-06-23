package Pathfinders;

import Model.Block;
import Model.GridChangeListener;
import Model.GridEvent;

import java.util.*;
import java.util.concurrent.CancellationException;

/**
 * Pathfinders.Pathfinder abstract class
 * Sets the model for all Pathfinders.Pathfinder classes
 */
public abstract class Pathfinder implements Runnable {

    /**
     * CONSTANTS
     **/

    public static final int[][] DISPLACEMENT_MATRIX_1 = {
            {-1,0}, {0, -1}, {1,0}, {0,1}, {-1, -1}, {1, 1}, {-1, 1}, {1, -1}
    };

    public static final int[][] DISPLACEMENT_MATRIX_2 = {{-1,0}, {0, -1}, {1,0}, {0,1}};

    /**
     * INSTANCE VARIABLES
     **/

    private Block start;
    private Block end;
    private Block[][] blocksList;
    private boolean moveDiagonally;
    private GridChangeListener listener = gridEvent -> {};
    private volatile boolean cancelled = false;

    /**
     * Method containing the main algorithm of each pathfinder
     *
     * @return A list of blocks that represents the path found
     */
    public abstract List<Block> findRoute();

    public List<Block> getNeighbours(Block parentBlock) {


        List<Block> neighbours = new ArrayList<>();
        int row = parentBlock.getRow();
        int column = parentBlock.getColumn();

        int[][] displacement_matrix = (canMoveDiagonally()) ? DISPLACEMENT_MATRIX_1 : DISPLACEMENT_MATRIX_2;
        for (int[] vector : displacement_matrix) {
            int tempRow = vector[0];
            int tempColumn = vector[1];

            if (isValidNeighbour(row + tempRow, column + tempColumn)) {
                Block currentBlock = getBlocks()[row + tempRow][column + tempColumn];
                if (currentBlock.isWalkable()) {
                    neighbours.add(currentBlock);
                }
            }
        }
        return neighbours;
    }

    public void setBlocksList(Block[][] blocksList) {
        this.blocksList = blocksList;
    }

    public Block[][] getBlocks() {
        return blocksList;
    }

    public Block getStart() {
        return start;
    }

    public void setStart(Block start) {
        this.start = start;
    }

    public Block getEnd() {
        return end;
    }

    public void setEnd(Block end) {
        this.end = end;
    }

    public boolean canMoveDiagonally() {
        return moveDiagonally;
    }

    public void setMoveDiagonally(boolean moveDiagonally) {
        this.moveDiagonally = moveDiagonally;
    }

    public List<Block> reconstructPath(Block block) {
        List<Block> path = new ArrayList<>();
        Block current = block;
        while (current != null) {
            if (path.contains(current)) {
                return path;
            }
            path.add(current);
            current = current.getParentBlock();
        }
        return path;
    }

    private boolean isValidNeighbour(int row, int column) {
        return ((row >= 0 && row < getBlocks().length) && (column >= 0 && column < getBlocks()[0].length));
    }

    public boolean[][] initializeVisited() {
        if (blocksList == null || blocksList.length == 0) {
            throw new IllegalStateException("Blocks list must be initialized before creating visited array.");
        }
        int height = blocksList.length;
        int width = blocksList[0].length;
        boolean[][] array = new boolean[height][width];
        Arrays.stream(array).forEach(x -> Arrays.fill(x, false));
        return array;
    }

    public void setGridChangeListener(GridChangeListener listener) {
        this.listener = listener;
    }

    public void cancel() {
        cancelled = true;
    }

    protected void markWalked(Block block) {
        if (cancelled || Thread.currentThread().isInterrupted()) {
            throw new CancellationException();
        }
        if (block == getStart() || block == getEnd()) {
            return;
        }
        block.makeWalked();
        listener.blockChanged(new GridEvent(block.getRow(), block.getColumn(), block.getState(), true));
    }

    protected void markNeighbour(Block block) {
        if (cancelled || Thread.currentThread().isInterrupted()) {
            throw new CancellationException();
        }
        if (block == getStart() || block == getEnd()) {
            return;
        }
        block.makeNeighbour();
        listener.blockChanged(new GridEvent(block.getRow(), block.getColumn(), block.getState(), false));
    }

    protected void markPath(Block block) {
        if (cancelled || Thread.currentThread().isInterrupted()) {
            throw new CancellationException();
        }
        block.makePath();
        listener.blockChanged(new GridEvent(block.getRow(), block.getColumn(), block.getState(), true));
    }

    @Override
    public void run() {
        try {
            List<Block> route = findRoute();
            for (Block block : route) {
                markPath(block);
            }
        } catch (CancellationException ignored) {
        }
    }
}
