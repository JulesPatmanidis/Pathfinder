package Mazes;

import Model.Block;
import Model.Grid;
import Model.GridChangeListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class RecursiveBacktrackingMazeGenerator extends AbstractMazeGenerator {

    @Override
    public String getName() {
        return "Recursive Backtracking";
    }

    @Override
    public void generate(Grid grid, GridChangeListener listener) {
        fillWithWalls(grid, listener);

        boolean[][] visited = initializeVisited(grid);
        Stack<Block> stack = new Stack<>();
        Block startBlock = grid.getBlock(
                ThreadLocalRandom.current().nextInt(grid.getRows()),
                ThreadLocalRandom.current().nextInt(grid.getColumns())
        );

        markWalkable(startBlock, listener);
        visited[startBlock.getRow()][startBlock.getColumn()] = true;
        stack.push(startBlock);

        while (!stack.isEmpty()) {
            Block current = stack.peek();
            Map<Block, Block> neighboursAndPaths =
                    getMazeNeighbours(grid, current).entrySet().stream()
                            .filter(entry -> !visited[entry.getKey().getRow()][entry.getKey().getColumn()])
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    Map.Entry::getValue
                            ));

            if (!neighboursAndPaths.isEmpty()) {
                List<Block> neighbours = new ArrayList<>(neighboursAndPaths.keySet());
                Block neighbour = neighbours.get(ThreadLocalRandom.current().nextInt(neighbours.size()));
                Block path = neighboursAndPaths.get(neighbour);

                markWalkable(path, listener);
                markWalkable(neighbour, listener);
                visited[neighbour.getRow()][neighbour.getColumn()] = true;
                stack.push(neighbour);
            } else {
                stack.pop();
            }
        }
    }
}
