package Mazes;

import Model.Block;
import Model.Grid;
import Model.GridChangeListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class AldousBroderMazeGenerator extends AbstractMazeGenerator {

    @Override
    public String getName() {
        return "Aldous-Broder";
    }

    @Override
    public void generate(Grid grid, GridChangeListener listener) {
        fillWithWalls(grid, listener);

        boolean[][] visited = initializeVisited(grid);
        Block current = grid.getBlock(
                ThreadLocalRandom.current().nextInt(grid.getRows()),
                ThreadLocalRandom.current().nextInt(grid.getColumns())
        );

        int cellsToVisit = countReachableMazeCells(grid, current);
        int visitedCount = 1;
        visited[current.getRow()][current.getColumn()] = true;
        markWalkable(current, listener);

        while (visitedCount < cellsToVisit) {
            Map<Block, Block> neighboursAndPaths = getMazeNeighbours(grid, current);
            List<Block> neighbours = new ArrayList<>(neighboursAndPaths.keySet());
            Block next = neighbours.get(ThreadLocalRandom.current().nextInt(neighbours.size()));

            if (!visited[next.getRow()][next.getColumn()]) {
                markWalkable(neighboursAndPaths.get(next), listener);
                markWalkable(next, listener);
                visited[next.getRow()][next.getColumn()] = true;
                visitedCount++;
            }

            current = next;
        }
    }

    private int countReachableMazeCells(Grid grid, Block start) {
        int count = 0;
        for (int row = start.getRow() % 2; row < grid.getRows(); row += 2) {
            for (int column = start.getColumn() % 2; column < grid.getColumns(); column += 2) {
                count++;
            }
        }
        return count;
    }
}
