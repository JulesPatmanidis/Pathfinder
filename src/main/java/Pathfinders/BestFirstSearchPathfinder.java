package Pathfinders;

import Application.Block;

public class BestFirstSearchPathfinder extends AStarPathfinder {
    public BestFirstSearchPathfinder(Pathfinder previous) {
        super(previous);
    }

    @Override
    protected double getPriority(Block block) {
        return block.getDistanceScore();
    }
}
