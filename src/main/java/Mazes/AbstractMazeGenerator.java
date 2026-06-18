package Mazes;

import Model.Block;
import Model.Grid;
import Model.GridChangeListener;
import Model.GridEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract class AbstractMazeGenerator implements MazeGenerator {

    private static final List<List<Integer>> MAZE_DISPLACEMENT_MATRIX = List.of(
            List.of(-2, 0),
            List.of(0, -2),
            List.of(2, 0),
            List.of(0, 2)
    );

    protected void fillWithWalls(Grid grid, GridChangeListener listener) {
        for (List<Block> row : grid.getBlockList()) {
            for (Block block : row) {
                markWall(block, listener);
            }
        }
    }

    protected boolean[][] initializeVisited(Grid grid) {
        return new boolean[grid.getRows()][grid.getColumns()];
    }

    protected Map<Block, Block> getMazeNeighbours(Grid grid, Block block) {
        Map<Block, Block> neighbours = new HashMap<>();
        int row = block.getRow();
        int column = block.getColumn();

        for (List<Integer> vector : MAZE_DISPLACEMENT_MATRIX) {
            int neighbourRow = row + vector.get(0);
            int neighbourColumn = column + vector.get(1);
            int pathRow = row + (vector.get(0) / 2);
            int pathColumn = column + (vector.get(1) / 2);

            if (isValidNeighbour(grid, neighbourRow, neighbourColumn)) {
                neighbours.put(
                        grid.getBlock(neighbourRow, neighbourColumn),
                        grid.getBlock(pathRow, pathColumn)
                );
            }
        }
        return neighbours;
    }

    protected void markWall(Block block, GridChangeListener listener) {
        block.makeWall();
        listener.blockChanged(new GridEvent(block.getRow(), block.getColumn(), block.getState(), false));
    }

    protected void markWalkable(Block block, GridChangeListener listener) {
        block.makeWalkable();
        listener.blockChanged(new GridEvent(block.getRow(), block.getColumn(), block.getState(), false));
    }

    private boolean isValidNeighbour(Grid grid, int row, int column) {
        return row >= 0 && row < grid.getRows() && column >= 0 && column < grid.getColumns();
    }
}
