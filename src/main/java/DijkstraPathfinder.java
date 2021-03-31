import java.util.Comparator;

public class DijkstraPathfinder extends AStarPathfinder {

    public DijkstraPathfinder(Pathfinder previous) {
        super(previous);
        // Compare blocks based on distance from start
        Comparator<Block> blockByDistanceFromStart = Comparator.comparingDouble(o -> o.getNode().getScoreFromStart());
        setBlockComparator(blockByDistanceFromStart);

    }
}
