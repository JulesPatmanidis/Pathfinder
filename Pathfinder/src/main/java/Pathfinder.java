import java.util.List;

/**
 * Pathfinder abstract class
 * Sets the model for all Pathfinder classes
 */
public abstract class Pathfinder implements Runnable{

    /** CONSTANTS **/
    public static final List<List<Integer>> DISPLACEMENT_LIST = List.of(
            List.of(-1,0),
            List.of(0,-1),
            List.of(1,0),
            List.of(0,1),
            List.of(-1,-1),
            List.of(1,1),
            List.of(-1,1),
            List.of(1,-1)
    );

    public static final List<List<Integer>> DISPLACEMENT_LIST_2 = List.of(
            List.of(-1,0),
            List.of(0,-1),
            List.of(1,0),
            List.of(0,1)
    );

    /** INSTANCE VARIABLES **/

    public Gui gui;


    /**
     * Method containing the main algorithm of each pathfinder
     * @return A list of blocks that represents the path found
     */
     public abstract List<Block> findRoute();

    public abstract List<Block> findRouteTest();

     public abstract List<Block> getNeighbours(Block parentBlock);

    @Override
    public void run() {

        //System.out.println("Run");
        System.out.println(Thread.currentThread().getName());
        Gui.paintRoute(findRouteTest());    //findRouteTest for testing

    }
}
