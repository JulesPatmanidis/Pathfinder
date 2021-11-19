package Pathfinders;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

import Application.Block;
import Application.App;


public class AStarPathfinder extends Pathfinder {

    private static final int OPEN_QUEUE_SIZE = 50;


    /**
     * INSTANCE VARIABLES
     **/
    private Comparator<Block> blockComparator;


    public AStarPathfinder() {
        // Blocks get compared based on totalScore
        blockComparator = Comparator.comparingDouble(Block::getTotalScore);
    }
    public AStarPathfinder(Pathfinder previous) {
        blockComparator = Comparator.comparingDouble(Block::getTotalScore);
        setStart(previous.getStart());
        setEnd(previous.getEnd());
        setBlocksList(previous.getBlocks());
    }

    public void setBlockComparator(Comparator<Block> blockComparator) {
        this.blockComparator = blockComparator;
    }

    @Override
    public List<Block> findRoute() {
        PriorityQueue<Block> openQueue = new PriorityQueue<>(OPEN_QUEUE_SIZE, blockComparator);
        List<Block> closedList = new ArrayList<>();
        getStart().setScoreFromStart(0);
        openQueue.add(getStart());
        Block currentBlock;

        while (!openQueue.isEmpty()) {
            currentBlock = openQueue.poll();  //Get the head of the queue and remove it from the list

            /* if current node is the destination, generate route and return it */
            if (currentBlock.equals(getEnd())) {
                return reconstructPath(currentBlock);
            }

            /* add currentBlock to the closedList and consider its neighbours */
            closedList.add(currentBlock);
            List<Block> neighbours = getNeighbours(currentBlock);

            /* for every neighbour,  */
            for (Block neighbourBlock : neighbours) {
                /* if the neighbour is already in open list or closed list through a shorter path, skip it */
                double newDistFromParent = neighbourBlock.calcEuclidDistanceTo(currentBlock);
                if (closedList.contains(neighbourBlock) &&
                        neighbourBlock.getScoreFromStart() < currentBlock.getScoreFromStart() + newDistFromParent) {
                    continue;
                }
                if (queueContains(openQueue, neighbourBlock) &&
                        neighbourBlock.getScoreFromStart() < currentBlock.getScoreFromStart() + newDistFromParent) {
                    continue;
                }

                /* init neighbour scores*/
                neighbourBlock.setParentBlock(currentBlock);
                neighbourBlock.calcDistanceScore(getEnd()); // h(n), the heuristic
                neighbourBlock.calcScoreFromStart(currentBlock); // g(n)
                neighbourBlock.calcTotalScoreAStar(); // h(n) + g(n)

                // If block has not been visited before, add it to the open queue
                if (!queueContains(openQueue, neighbourBlock) && !closedList.contains(neighbourBlock)) {
                    try {
                        SwingUtilities.invokeAndWait(() -> neighbourBlock.getButton().setBackground(App.CHECKED_COLOR));
                    } catch (InterruptedException e) {
                        System.err.println("Error: Thread was interrupted");
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        System.err.println("Error: " + e.getMessage());
                        e.printStackTrace();
                    }
                    openQueue.add(neighbourBlock);
                }
            }

        }
        return List.of(getEnd());
    }

    public boolean queueContains(PriorityQueue<Block> queue, Block block) {
        Stream<Block> blockStream = queue.stream();
        return blockStream.anyMatch(x -> x.equals(block));
    }

}
