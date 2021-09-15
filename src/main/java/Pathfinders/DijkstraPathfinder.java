package Pathfinders;

import java.util.Comparator;

import Application.Block;

public class DijkstraPathfinder extends AStarPathfinder {

    public DijkstraPathfinder(Pathfinder previous) {
        super(previous);
        // Compare blocks based on distance from start
        Comparator<Block> blockByDistanceFromStart = Comparator.comparingDouble(Block::getScoreFromStart);
        setBlockComparator(blockByDistanceFromStart);

    }
}
