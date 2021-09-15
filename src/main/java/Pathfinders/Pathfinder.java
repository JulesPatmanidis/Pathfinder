package Pathfinders;

import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import Application.*;
import Utilities.Utils;

/**
 * Pathfinders.Pathfinder abstract class
 * Sets the model for all Pathfinders.Pathfinder classes
 */
public abstract class Pathfinder implements Runnable {

    /**
     * CONSTANTS
     **/
    public static final int DELAY = 1;

    public static final List<List<Integer>> DISPLACEMENT_MATRIX_1 = List.of(
            List.of(-1, 0),
            List.of(0, -1),
            List.of(1, 0),
            List.of(0, 1),
            List.of(-1, -1),
            List.of(1, 1),
            List.of(-1, 1),
            List.of(1, -1)
    );

    public static final List<List<Integer>> DISPLACEMENT_MATRIX_2 = List.of(
            List.of(-1, 0),
            List.of(0, -1),
            List.of(1, 0),
            List.of(0, 1)
    );

    public static final List<List<Integer>> MAZE_DISPLACEMENT_MATRIX = List.of(
            List.of(-2, 0),
            List.of(0, -2),
            List.of(2, 0),
            List.of(0, 2)
    );

    /**
     * INSTANCE VARIABLES
     **/
    private Block start;
    private Block end;
    private List<List<Block>> blocksList;
    private boolean moveDiagonally;


    /**
     * Method containing the main algorithm of each pathfinder
     *
     * @return A list of blocks that represents the path found
     */
    public abstract List<Block> findRoute();

    public List<Block> getNeighbours(Block parentBlock) {
        // Add delay
        try {
            TimeUnit.NANOSECONDS.sleep(DELAY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<Block> neighbours = new ArrayList<>();
        int row = parentBlock.getRow();
        int column = parentBlock.getColumn();

        List<List<Integer>> displacement_matrix = (canMoveDiagonally()) ? DISPLACEMENT_MATRIX_1 : DISPLACEMENT_MATRIX_2;
        for (List<Integer> vector : displacement_matrix) {
            int tempRow = vector.get(0);
            int tempColumn = vector.get(1);

            if (isValidNeighbour(row + tempRow, column + tempColumn)) {
                Block currentBlock = getBlocks().get(row + tempRow).get(column + tempColumn);
                if (currentBlock.isWalkable()) {
                    neighbours.add(currentBlock);
                }
            }
        }
        return neighbours;
    }

    public void setBlocksList(List<List<Block>> blocksList) {
        this.blocksList = blocksList;
    }

    public List<List<Block>> getBlocks() {return blocksList;}

    public Block getStart() {
        return start;
    }

    public void setStart(Block start) {
        this.start = start;
    }

    public void setEnd(Block end) {
        this.end = end;
    }

    public Block getEnd() {
        return end;
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
                System.out.println("Found duplicate while reconstructing path (infinite loop)");
                return path;
            }
            path.add(current);
            current = current.getParentBlock();
        }
        return path;
    }

    private boolean isValidNeighbour(int row, int column) {
        return ((row >= 0 && row < getBlocks().size()) && (column >= 0 && column < getBlocks().get(0).size()));
    }

    public boolean[][] initializeVisited() {
        int width = (int) (App.BLOCK_NUMBER * Utils.getAspectRatio());
        boolean[][] array = new boolean[App.BLOCK_NUMBER][width];
        Arrays.stream(array).forEach(x -> Arrays.fill(x, false));
        return array;
    }

    private void allBlocksAreWalls () {
        blocksList.stream()
                .flatMap(Collection::stream)
                .forEach(Block::makeWall);
    }

    // MAZE Generation ---------------------------------------------------------------------------------------------

    public HashMap<Block, Block> getMazeNeighbours(Block parentBlock) {
        HashMap<Block, Block> neighbours = new HashMap<>();
        int row = parentBlock.getRow();
        int column = parentBlock.getColumn();

        for (List<Integer> vector : MAZE_DISPLACEMENT_MATRIX) {
            int tempRow = vector.get(0) + row;
            int tempColumn = vector.get(1) + column;
            int pathRow = (vector.get(0) / 2) + row;
            int pathColumn = (vector.get(1) / 2) + column;
            if (isValidNeighbour(tempRow, tempColumn)) {
                neighbours.put(this.getBlocks().get(tempRow).get(tempColumn)
                        ,this.getBlocks().get(pathRow).get(pathColumn)
                );
            }
        }
        return neighbours;
    }

    public void createDFSMaze() {
        allBlocksAreWalls();

        int sizeX = blocksList.get(0).size();
        int sizeY = blocksList.size();
        boolean[][] visited = initializeVisited();

        Stack<Block> stack = new Stack<>();
        int randomX = (int) (Math.random() * sizeY);
        int randomY = (int) (Math.random() * sizeX);
        stack.push(blocksList.get(randomX).get(randomY));
        while (!stack.isEmpty()) {
            Block current = stack.pop();

            HashMap<Block, Block> neighboursAndPaths = (HashMap<Block, Block>) getMazeNeighbours(current).entrySet().stream()
                    .filter(map -> !visited[map.getKey().getRow()][map.getKey().getColumn()])
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            if (!neighboursAndPaths.isEmpty()) {
                int randomIndex = new Random().nextInt(neighboursAndPaths.size());
                List<Block> neighbours = new ArrayList<>(neighboursAndPaths.keySet());
                Block randNeighbour = neighbours.get(randomIndex);
                neighboursAndPaths.forEach((neighbour, path) -> {
                    neighbour.makePath();
                    visited[neighbour.getRow()][neighbour.getColumn()] = true;

                    path.makePath();
                    visited[path.getRow()][path.getColumn()] = true;
                    if (!neighbour.equals(randNeighbour)) stack.push(neighbour);
                });
                stack.push(randNeighbour);
            }
        }

    }

    public void createPrimsMaze() {
        Random random = new Random();
        allBlocksAreWalls();
        int randRow = new Random().nextInt(blocksList.size());
        int randCol = new Random().nextInt(blocksList.get(0).size());
        Block first = blocksList.get(randRow).get(randCol);

        first.makePath();
        HashMap<Block, Block> frontierMap = (HashMap<Block, Block>) getMazeNeighbours(first).entrySet().stream()
                .filter(map -> !map.getKey().isWalkable())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // Choose random frontier (neighbour block) AND a random block already in the maze to connect them, currently
        // each frontier block can only have one block in the maze (because of hashmap)
        while (!frontierMap.isEmpty()) {
            int randIndex = random.nextInt(frontierMap.size());
            Block randBlock = (Block) frontierMap.keySet().toArray()[randIndex];

            HashMap<Block, Block> neighbours = (HashMap<Block, Block>) getMazeNeighbours(randBlock)
                    .entrySet().stream()
                    .filter(map -> map.getKey().isWalkable())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            if (!neighbours.isEmpty()) {
                int randIndex2 = random.nextInt(neighbours.size());
                Block randNeighbour = (Block) neighbours.keySet().toArray()[randIndex2];
                neighbours.get(randNeighbour).makePath();
                randBlock.makePath();
                HashMap<Block, Block> newFrontierBlocks = (HashMap<Block, Block>) getMazeNeighbours(randBlock).entrySet().stream()
                        .filter(map -> !map.getKey().isWalkable())
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                frontierMap.putAll(newFrontierBlocks);
            }
            frontierMap.remove(randBlock);
        }
    }


    @Override
    public void run() {
        App.paintRoute(findRoute());
    }
}
