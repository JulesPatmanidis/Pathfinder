package Pathfinders;

import java.util.Comparator;

import Application.Block;

public class WorstFirstSearchPathfinder extends AStarPathfinder {

    public WorstFirstSearchPathfinder(Pathfinder previous) {
        super(previous);
        // Compare blocks based on distance from start (try the worst option first)
        Comparator<Block> blockByDistanceScore = (Block a, Block b) -> Double.compare(b.getTotalScore(), a.getTotalScore());
        setBlockComparator(blockByDistanceScore);
    }

}
