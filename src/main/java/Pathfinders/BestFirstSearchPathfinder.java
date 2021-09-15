package Pathfinders;

import java.util.Comparator;

import Application.Block;

public class BestFirstSearchPathfinder extends AStarPathfinder {
    public BestFirstSearchPathfinder(Pathfinder previous) {
        super(previous);
        // Compare blocks based on distance from start
        Comparator<Block> blockByDistanceScore = Comparator.comparingDouble(Block::getDistanceScore);
        setBlockComparator(blockByDistanceScore);

    }
}
