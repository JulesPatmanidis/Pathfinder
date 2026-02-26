package Pathfinders;

import Application.Block;

public class DijkstraPathfinder extends AStarPathfinder {

    public DijkstraPathfinder(Pathfinder previous) {
        super(previous);
    }

    @Override
    protected double getPriority(Block block) {
        return block.getScoreFromStart();
    }
}
