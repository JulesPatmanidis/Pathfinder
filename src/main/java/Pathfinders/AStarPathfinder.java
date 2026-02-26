package Pathfinders;

import java.util.*;
import java.util.List;
import Application.Block;


public class AStarPathfinder extends Pathfinder {

    private static final int OPEN_QUEUE_SIZE = 50;


    public AStarPathfinder() {
    }
    public AStarPathfinder(Pathfinder previous) {
        setStart(previous.getStart());
        setEnd(previous.getEnd());
        setBlocksList(previous.getBlocks());
    }

    protected double getPriority(Block block) {
        return block.getTotalScore();
    }

    private static final class QueueEntry {
        private final Block block;
        private final double priority;
        private final long sequence;

        private QueueEntry(Block block, double priority, long sequence) {
            this.block = block;
            this.priority = priority;
            this.sequence = sequence;
        }
    }

    @Override
    public List<Block> findRoute() {
        PriorityQueue<QueueEntry> openQueue = new PriorityQueue<>(
                OPEN_QUEUE_SIZE,
                Comparator.comparingDouble((QueueEntry entry) -> entry.priority)
                        .thenComparingLong(entry -> entry.sequence)
        );
        Set<Block> openSet = new HashSet<>();
        Set<Block> closedSet = new HashSet<>();
        long sequence = 0L;
        getStart().setScoreFromStart(0);
        openQueue.add(new QueueEntry(getStart(), getPriority(getStart()), sequence++));
        openSet.add(getStart());
        Block currentBlock;

        while (!openQueue.isEmpty()) {
            QueueEntry entry = openQueue.poll();
            currentBlock = entry.block;
            if (closedSet.contains(currentBlock)) {
                continue;
            }
            if (Double.compare(entry.priority, getPriority(currentBlock)) != 0) {
                continue; // stale queue entry after a better path was enqueued later
            }

            openSet.remove(currentBlock);
            currentBlock.makeWalked();
            /* if current node is the destination, generate route and return it */
            if (currentBlock.equals(getEnd())) {
                return reconstructPath(currentBlock);
            }

            /* add currentBlock to the closed set and consider its neighbours */
            closedSet.add(currentBlock);
            List<Block> neighbours = getNeighbours(currentBlock);

            /* for every neighbour */
            for (Block neighbourBlock : neighbours) {
                double newDistFromParent = neighbourBlock.calcEuclidDistanceTo(currentBlock);
                double tentativeScore = currentBlock.getScoreFromStart() + newDistFromParent;
                if (tentativeScore >= neighbourBlock.getScoreFromStart()) {
                    continue;
                }

                boolean wasUndiscovered = !openSet.contains(neighbourBlock) && !closedSet.contains(neighbourBlock);
                closedSet.remove(neighbourBlock); // allow reopen if a better path was found

                /* init neighbour scores*/
                neighbourBlock.setParentBlock(currentBlock);
                neighbourBlock.calcDistanceScore(getEnd()); // h(n), the heuristic
                neighbourBlock.calcScoreFromStart(currentBlock); // g(n)
                neighbourBlock.calcTotalScoreAStar(); // h(n) + g(n)

                if (wasUndiscovered) {
                    neighbourBlock.makeNeighbour();
                }
                openQueue.add(new QueueEntry(neighbourBlock, getPriority(neighbourBlock), sequence++));
                openSet.add(neighbourBlock);
            }

        }
        return List.of();
    }

}
