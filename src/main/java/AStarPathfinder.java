import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;


public class AStarPathfinder extends Pathfinder {

    private static final int OPEN_QUEUE_SIZE = 50;


    /**
     * INSTANCE VARIABLES
     **/
    private Comparator<Block> blockComparator;


    public AStarPathfinder() {
        // Blocks get compared based on totalScore
        blockComparator = Comparator.comparingDouble(o -> o.getNode().getTotalScore());
    }
    public AStarPathfinder(Pathfinder previous) {
        blockComparator = Comparator.comparingDouble(o -> o.getNode().getTotalScore());
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
        getStart().getNode().setScoreFromStart(0);
        openQueue.add(getStart());
        Block currentBlock;
        Node currentNode;

        while (!openQueue.isEmpty()) {


            currentBlock = openQueue.poll();  //Get the head of the queue and remove it from the list

            if (currentBlock != null) {
                currentNode = currentBlock.getNode();
            } else throw new NullPointerException("Block popped from queue was null");

            //System.out.printf(" Current: %d, %d----------------\n", currentNode.getRow(), currentNode.getColumn());

            /* if current node is the destination, generate route and return it */
            if (currentBlock.equals(getEnd())) {
                //System.out.println("Path found");
                return reconstructPath(currentBlock);
            }

            /* add currentBlock to the closedList and consider its neighbours */
            closedList.add(currentBlock);
            List<Block> neighbours = getNeighbours(currentBlock);

            /* for every neighbour,  */
            for (Block neighbourBlock : neighbours) {
                Node neighbourNode = neighbourBlock.getNode();
                /* if the neighbour is already in open list or closed list through a shorter path, skip it */
                double newDistFromParent = neighbourNode.calcEuclidDistanceTo(currentNode);
                if (closedList.contains(neighbourBlock) &&
                        neighbourNode.getScoreFromStart() < currentNode.getScoreFromStart() + newDistFromParent) {
                    //System.out.printf("Skip: %f > %f , (%d %d)\n", neighbourNode.getScoreFromStart(), currentNode.getScoreFromStart() + newDistFromParent, neighbourNode.getRow(), neighbourNode.getColumn() );
                    continue;
                }
                if (queueContains(openQueue, neighbourBlock) &&
                        neighbourNode.getScoreFromStart() < currentNode.getScoreFromStart() + newDistFromParent) {
                    //System.out.printf("Skip: %f > %f , (%d %d)\n", neighbourNode.getScoreFromStart(), currentNode.getScoreFromStart() + newDistFromParent, neighbourNode.getRow(), neighbourNode.getColumn() );
                    continue;
                }

                /* init neighbour scores*/
                neighbourBlock.setParentBlock(currentBlock);
                neighbourNode.calcDistanceScore(getEnd().getNode()); // h(n), the heuristic
                neighbourNode.calcScoreFromStart(currentBlock.getNode()); // g(n)
                neighbourNode.calcTotalScoreAStar(); // h(n) + g(n)

                // If block has not been visited before, add it to the open queue
                if (!queueContains(openQueue, neighbourBlock) && !closedList.contains(neighbourBlock)) {
//                    String text = String.valueOf(neighbourNode.getScoreFromStart());
//                    neighbourBlock.getButton().setText(text);
                    try {
                        SwingUtilities.invokeAndWait(() -> App.paintBlock(neighbourBlock, App.CHECKED_COLOR));
                    } catch (InterruptedException e) {
                        System.err.println("Error: Thread was interrupted");
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        System.err.println("Error: " + e.getMessage());
                        e.printStackTrace();
                    }
                    openQueue.add(neighbourBlock);

                    // Debugging
//                  String text = String.valueOf(neighbourNode.getScoreFromStart());
//                  neighbourBlock.getButton().setText(text);
//                    System.out.printf("node added: (%d, %d) from parent: (%d, %d) with name: %s\n",
//                            neighbourNode.getRow(), neighbourNode.getColumn(), currentNode.getRow(), currentNode.getColumn(), neighbourNode);
                }
            }

        }
        //System.out.println("Path not found");
        return List.of(getEnd());
    }

    public boolean queueContains(PriorityQueue<Block> queue, Block block) {
        Stream<Block> blockStream = queue.stream();
        return blockStream.anyMatch(x -> x.equals(block));

    }

}
