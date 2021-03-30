import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Pathfinder abstract class
 * Sets the model for all Pathfinder classes
 */
public abstract class Pathfinder implements Runnable {

    /**
     * CONSTANTS
     **/
    private static final int BLOCK_NUMBER = 50; //50
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
        int row = parentBlock.getNode().getRow();
        int column = parentBlock.getNode().getColumn();

        List<List<Integer>> displacement_matrix = (canMoveDiagonally()) ? DISPLACEMENT_MATRIX_1 : DISPLACEMENT_MATRIX_2;
        for (List<Integer> vector : displacement_matrix) {
            int tempRow = vector.get(0);
            int tempColumn = vector.get(1);

            if (isValidNeighbour(row + tempRow, column + tempColumn)) {
                Block currentBlock = getBlocks().get(row + tempRow).get(column + tempColumn);
                if (currentBlock.getNode().isWalkable()) {
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

    public double getAspectRatio() {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        //System.out.println(dim.getWidth());
        return dim.getWidth()/dim.getHeight();
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

    @Override
    public void run() {
        App.paintRoute(findRoute());    // findRouteTest -> Testing // findRoute -> Normal

    }
}
