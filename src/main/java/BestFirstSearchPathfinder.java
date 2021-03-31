import java.util.Comparator;

public class BestFirstSearchPathfinder extends AStarPathfinder{
    public BestFirstSearchPathfinder(Pathfinder previous) {
        super(previous);
        // Compare blocks based on distance from start
        Comparator<Block> blockByDistanceScore = Comparator.comparingDouble(o -> o.getNode().getDistanceScore());
        setBlockComparator(blockByDistanceScore);

    }
}
