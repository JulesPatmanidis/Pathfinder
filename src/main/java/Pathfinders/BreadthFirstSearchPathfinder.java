package Pathfinders;

import javax.swing.*;
import java.util.LinkedList;
import java.util.List;

import Application.Block;

public class BreadthFirstSearchPathfinder extends Pathfinder {

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
        visited[getStart().getRow()][getStart().getColumn()] = true;
        Block currentBlock;
        while (!blockQueue.isEmpty()) {
            currentBlock = blockQueue.poll();

            if (currentBlock.equals(getEnd())) {
                return reconstructPath(currentBlock);
            }

            for (Block neighbourBlock : getNeighbours(currentBlock)) {
                //Node neighbourNode = neighbourBlock.getNode();

                if (visited[neighbourBlock.getRow()][neighbourBlock.getColumn()]) {
                    continue;
                }

                //neighbourBlock.getButton().paintNeighbour();
                neighbourBlock.makeNeighbour();
                visited[neighbourBlock.getRow()][neighbourBlock.getColumn()] = true;
                neighbourBlock.setParentBlock(currentBlock);
                blockQueue.add(neighbourBlock);

            }
        }
        return List.of();
    }
}
