import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;


public class AStarPathfinder extends Pathfinder {

    private static final int OPEN_QUEUE_SIZE = 50;
    private static final int CLOSED_QUEUE_SIZE = 50;


    /** INSTANCE VARIABLES **/
    private List<Block> openList;
    private List<Block> closedList;
    private PriorityQueue<Block> openQueue;
    private PriorityQueue<Block> closedQueue;
    private Hashtable<String, Integer> costSoFar;


    public AStarPathfinder(Gui gui) {
        this.gui = gui;
        openList = new ArrayList<>();
        closedList = new ArrayList<>();
        openQueue = new PriorityQueue<>(OPEN_QUEUE_SIZE);
        closedQueue = new PriorityQueue<>(CLOSED_QUEUE_SIZE);
        costSoFar = new Hashtable<>();
    }

    public List<Block> findRouteTest() {
        openQueue.add(gui.getStart());
        Block currentBlock;
        Node currentNode;

        while (!openQueue.isEmpty()) {
            currentBlock = openQueue.poll();  //Get the head of the queue and remove it from the list (poll = pop)
            currentNode = currentBlock.getNode();
            //System.out.println(currentBlock.getNode().getTotalScore());


            /* if current node is the destination, generate route and return it */
            if (currentNode.getId().equals(gui.getEnd().getNode().getId())) {
                System.out.println("Path found");
                List<Block> route = new ArrayList<>();
                Block current = currentBlock;
                while (current != null) {
                    route.add(current);
                    current = current.getParentBlock();
                }
                return route;
            }

            /* add currentBlock to the closedList and consider its neighbours */
            closedList.add(currentBlock);
            List<Block> neighbours = getNeighbours(currentBlock);

            /* for every neighbour,  */
            for (Block neighbourBlock : neighbours) {
                Node neighbourNode = neighbourBlock.getNode();
                /* init neighbour scores*/
                neighbourNode.calcDistanceScore(gui.getEnd().getNode());
                neighbourNode.calcScoreFromStart(currentBlock.getNode());
                neighbourNode.calcTotalScore();

                /* skip neighbour if not walkable */
                if (!neighbourNode.isWalkable()){
                    continue;
                }

                /* if the neighbour is already in open list or closed list through a shorter path, skip it */
                // IMPLEMENT OWN VERSION OF QUEUE TO HAVE CUSTOM CONTAINS METHOD, THIS DOES NOT WORK
                if (queueContains(openQueue, neighbourBlock) &&
                        costSoFar.get(neighbourNode.getId()) < neighbourNode.getScoreFromStart())
                {
                    continue;
                }
                if (listContainsBlock(closedList, neighbourBlock) &&
                        getBlockScoreFromList(closedList, neighbourBlock) < neighbourNode.getScoreFromStart())
                {
                    continue;
                }

                openQueue.add(neighbourBlock);
                costSoFar.put(neighbourNode.getId(),neighbourNode.getScoreFromStart());
                System.out.printf("node added, row: %d, column: %d\n",neighbourBlock.getNode().getRow(), neighbourBlock.getNode().getColumn());
            }
        }
        //System.out.println("Path not found");
        return List.of(gui.getEnd());
    }

    public List<Block> findRoute() {
        openList.add(gui.getStart());
        Block currentBlock;
        Node currentNode;

        while (!openList.isEmpty()) {
            currentBlock = openList.stream().min(Block::compareTo).get();  //Get minimum score node
            currentNode = currentBlock.getNode();
            //System.out.println(currentBlock.getNode().getTotalScore());

            openList.remove(currentBlock);

            /* if current node is the destination, generate route and return it */
            if (currentNode.getId().equals(gui.getEnd().getNode().getId())) {
                System.out.println("Path found");
                List<Block> route = new ArrayList<>();
                Block current = currentBlock;
                while (current != null) {
                    route.add(current);
                    current = current.getParentBlock();
                }
                return route;
            }

            /* add currentBlock to the closedList and consider its neighbours */
            closedList.add(currentBlock);
            List<Block> neighbours = getNeighbours(currentBlock);

            /* for every neighbour,  */
            for (Block neighbourBlock : neighbours) {
                Node neighbourNode = neighbourBlock.getNode();
                /* init neighbour scores*/

                neighbourNode.calcDistanceScore(gui.getEnd().getNode());
                neighbourNode.calcScoreFromStart(currentBlock.getNode());
                neighbourNode.calcTotalScore();

                /* skip neighbour if not walkable */
                if (!neighbourNode.isWalkable()){
                    continue;
                }

                /* if the neighbour is already in open list or closed list through a shorter path, skip it */
                if (listContainsBlock(openList, neighbourBlock) &&
                        getBlockScoreFromList(openList, neighbourBlock) < neighbourNode.getScoreFromStart())
                {
                    continue;
                }
                if (listContainsBlock(closedList, neighbourBlock) &&
                        getBlockScoreFromList(closedList, neighbourBlock) < neighbourNode.getScoreFromStart())
                {
                    continue;
                }
                // Maybe remove block already in openList but with greater score
                openList.add(neighbourBlock);
                costSoFar.put(neighbourNode.getId(),neighbourNode.getScoreFromStart());
                System.out.printf("node added, x: %d, y: %d\n",neighbourBlock.getNode().getColumn(), neighbourBlock.getNode().getRow());
            }
        }
        //System.out.println("Path not found");
        return List.of(gui.getEnd());
    }

    public boolean queueContains(PriorityQueue<Block> queue, Block block) {
        Stream<String> streamId = queue.stream().map(Block::getNode).map(Node::getId);
        return streamId.anyMatch(x->x.equals(block.getNode().getId()));
    }

    /**
     * Returns the score of the given block in a given list, -1 if the block was not found
     */
    private int getBlockScoreFromList(List<Block> list, Block block) {
        String id = block.getNode().getId();
        for (Block blockInList : list) {
            if (blockInList.getNode().getId().equals(id)) {
                return  blockInList.getNode().getScoreFromStart();
            }
        }
        return -1;
    }
    private boolean listContainsBlock(List<Block> list, Block block) {
        return getBlockScoreFromList(list, block) != -1;
    }

    private boolean isValid(int x, int y) {
        return ((x >= 0 && x < gui.getBlocks().size()) && (y >= 0 && y < gui.getBlocks().get(0).size()));
    }


    public List<Block> getNeighbours (Block parentBlock)  {
        try {
            TimeUnit.NANOSECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<Block> neighbours = new ArrayList<>();
        int x = parentBlock.getNode().getRow();
        int y = parentBlock.getNode().getColumn();

        int dim = gui.getBlocks().size();

        for (List<Integer> vector : DISPLACEMENT_LIST) {
            int tempX = vector.get(0);
            int tempY = vector.get(1);
            if (isValid(x + tempX, y + tempY)) {

                Block tempBlock = gui.getBlocks().get(x + tempX).get(y +tempY);
                if (tempBlock.getNode().isWalkable()){
                    Block newBlock = new Block(tempBlock);
                   // Colour
                    try {
                        SwingUtilities.invokeAndWait(() -> newBlock.getButton().setBackground(Gui.CHECKED_COLOR));
                    } catch (InterruptedException e) {
                        System.err.println("Error: Thread was interrupted");
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        System.err.println("Error: " + e.getMessage());
                        e.printStackTrace();
                    }
                    // Score
                    newBlock.getNode().setScoreFromStart(parentBlock.getNode().getScoreFromStart() + 1);
                    // Parent
                    newBlock.setParentBlock(parentBlock);
                    // Add to neighbours
                    neighbours.add(newBlock);
                }
            }
        }
        return neighbours;
    }



}
