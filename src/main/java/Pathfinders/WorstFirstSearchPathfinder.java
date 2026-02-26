package Pathfinders;

import Application.Block;

public class WorstFirstSearchPathfinder extends AStarPathfinder {

    public WorstFirstSearchPathfinder(Pathfinder previous) {
        super(previous);
    }

    @Override
    protected double getPriority(Block block) {
        // Negate to preserve "worst-first" behavior while the queue remains ascending.
        return -block.getTotalScore();
    }

}
