package Mazes;

import Model.Block;
import Model.Grid;
import Model.GridChangeListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class RandomizedPrimsMazeGenerator extends AbstractMazeGenerator {

    @Override
    public String getName() {
        return "Randomised Prim's";
    }

    @Override
    public void generate(Grid grid, GridChangeListener listener) {
        Random random = new Random();
        fillWithWalls(grid, listener);

        Block first = grid.getBlock(
                random.nextInt(grid.getRows()),
                random.nextInt(grid.getColumns())
        );

        markWalkable(first, listener);
        Map<Block, Block> frontierMap = getMazeNeighbours(grid, first).entrySet().stream()
                .filter(entry -> !entry.getKey().isWalkable())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        HashMap::new
                ));

        while (!frontierMap.isEmpty()) {
            int randomIndex = random.nextInt(frontierMap.size());
            Block randomBlock = frontierMap.keySet().stream().skip(randomIndex).findFirst().orElseThrow();

            Map<Block, Block> neighbours = getMazeNeighbours(grid, randomBlock).entrySet().stream()
                    .filter(entry -> entry.getKey().isWalkable())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (a, b) -> a,
                            HashMap::new
                    ));

            if (!neighbours.isEmpty()) {
                int neighbourIndex = random.nextInt(neighbours.size());
                Block randomNeighbour = neighbours.keySet().stream().skip(neighbourIndex).findFirst().orElseThrow();
                markWalkable(neighbours.get(randomNeighbour), listener);
                markWalkable(randomBlock, listener);

                Map<Block, Block> newFrontierBlocks = getMazeNeighbours(grid, randomBlock).entrySet().stream()
                        .filter(entry -> !entry.getKey().isWalkable())
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (a, b) -> a,
                                HashMap::new
                        ));
                frontierMap.putAll(newFrontierBlocks);
            }
            frontierMap.remove(randomBlock);
        }
    }
}
