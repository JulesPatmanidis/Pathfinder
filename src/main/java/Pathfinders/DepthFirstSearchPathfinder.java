package Pathfinders;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Stack;

import Application.App;
import Application.Block;

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
        visited[getStart().getRow()][getStart().getColumn()] = true;
        Block currentBlock;
        while (!blockStack.isEmpty()) {
            currentBlock = blockStack.pop();

            if (currentBlock.equals(getEnd())) {
                return reconstructPath(currentBlock);
            }

            for (Block neighbourBlock : getNeighbours(currentBlock)) {
                double newDistFromParent = neighbourBlock.calcEuclidDistanceTo(currentBlock);
                if (visited[neighbourBlock.getRow()][neighbourBlock.getColumn()]) {
                    if (!(neighbourBlock.getScoreFromStart() < currentBlock.getScoreFromStart() + newDistFromParent)) {
                        neighbourBlock.setParentBlock(currentBlock);
                    }
                    continue;
                }

                neighbourBlock.getButton().paintNeighbour();
                visited[neighbourBlock.getRow()][neighbourBlock.getColumn()] = true;
                neighbourBlock.setParentBlock(currentBlock);
                neighbourBlock.calcScoreFromStart(currentBlock);

                blockStack.add(neighbourBlock);
            }
        }
        return List.of(getEnd());
    }
}
