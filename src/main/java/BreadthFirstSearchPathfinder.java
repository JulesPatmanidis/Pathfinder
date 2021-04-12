import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

public class BreadthFirstSearchPathfinder extends Pathfinder{

    private final LinkedList<Block> blockQueue;
    private final boolean[][] visited;

    public BreadthFirstSearchPathfinder(Pathfinder previous) {
        blockQueue = new LinkedList<>();
        setBlocksList(previous.getBlocks());
        setStart(previous.getStart());
        setEnd(previous.getEnd());
        visited = initializeVisited();
    }
    /**
     * Method containing the main algorithm of each pathfinder
     *
     * @return A list of blocks that represents the path found
     */
    @Override
    public List<Block> findRoute() {
        if (getStart() == null) {
            throw new NullPointerException("Start was null");
        }

        blockQueue.add(getStart());
        visited[getStart().getNode().getRow()][getStart().getNode().getColumn()] = true;
        Block currentBlock;
        while (!blockQueue.isEmpty()) {
            currentBlock = blockQueue.poll();
            for (Block neighbourBlock : getNeighbours(currentBlock)) {
                Node neighbourNode = neighbourBlock.getNode();

                if (currentBlock.equals(getEnd())) {
                    return reconstructPath(currentBlock);
                }
                if (visited[neighbourNode.getRow()][neighbourNode.getColumn()]) {
                    continue;
                }
                try {
                    SwingUtilities.invokeAndWait(() -> App.paintBlock(neighbourBlock, App.CHECKED_COLOR));
                } catch (InterruptedException e) {
                    System.err.println("Error: Thread was interrupted");
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    System.err.println("Error: " + e.getMessage());
                    e.printStackTrace();
                }
                visited[neighbourNode.getRow()][neighbourNode.getColumn()] = true;
                neighbourBlock.setParentBlock(currentBlock);
                blockQueue.add(neighbourBlock);
            }
        }
        return List.of(getEnd());
    }
}
