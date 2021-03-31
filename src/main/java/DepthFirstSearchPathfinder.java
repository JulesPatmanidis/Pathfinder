import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Stack;

public class DepthFirstSearchPathfinder extends Pathfinder {

    private final Stack<Block> blockStack;
    private final boolean[][] visited;
    /**
     * Method containing the main algorithm of each pathfinder
     */
    public DepthFirstSearchPathfinder() {
        blockStack = new Stack<>();
        visited = initializeVisited();
    }

    public DepthFirstSearchPathfinder(Pathfinder previous) {
        blockStack = new Stack<>();
        visited = initializeVisited();
        setBlocksList(previous.getBlocks());
        setStart(previous.getStart());
        setEnd(previous.getEnd());
    }

    @Override
    public List<Block> findRoute() {
        if (getStart() == null) {
            throw new NullPointerException("Start was null");
        }

        blockStack.push(getStart());
        visited[getStart().getNode().getRow()][getStart().getNode().getColumn()] = true;
        Block currentBlock;
        Node currentNode;
        while (!blockStack.isEmpty()) {
            currentBlock = blockStack.pop();

            for (Block neighbourBlock : getNeighbours(currentBlock)) {
                Node neighbourNode = neighbourBlock.getNode();

                if (currentBlock.equals(getEnd())) {
                    System.out.println("Path found");
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
                blockStack.add(neighbourBlock);
//                System.out.printf("node added: (%d, %d) from parent: (%d, %d) with name: %s\n",
//                            neighbourNode.getRow(), neighbourNode.getColumn(), currentNode.getRow(), currentNode.getColumn(), neighbourNode);
            }
        }
        System.out.println("Ended");
        return List.of(getEnd());
    }


}
