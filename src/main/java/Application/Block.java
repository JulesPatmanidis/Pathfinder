package Application;

import java.awt.*;

enum BlockState {
    WALKABLE,
    NON_WALKABLE,
    WALKED,
    NEIGHBOUR,
    START_END,
    PATH
}

public class Block implements Comparable<Block> {

    //private final mapButton button;
    private final Rectangle rect;
    private final int row;
    private final int column;
    private transient Block parentBlock;
    private boolean walkable;
    private double totalScore;
    private double distanceScore;
    private double scoreFromStart = Double.MAX_VALUE;



    private BlockState state;

    public Block(int row, int column) {
        walkable = true;
        this.row = row;
        this.column = column;
        rect = new Rectangle(column * 18, row * 18, 18, 18);
        //button = new mapButton();
        state = BlockState.WALKABLE;
    }

//    public mapButton getButton() {
//        return button;
//    }

    public Block getParentBlock() {
        return parentBlock;
    }

    public void setParentBlock(Block block) {
        this.parentBlock = block;
    }

    public boolean isWalkable() {
        return walkable;
    }

    public void setWalkable(boolean walkable) {
        this.walkable = walkable;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public double getTotalScore() {
        return totalScore;
    }

    public double getDistanceScore() {
        return distanceScore;
    }

    public double getScoreFromStart() {
        return scoreFromStart;
    }

    public void setScoreFromStart(double scoreFromStart) {
        this.scoreFromStart = scoreFromStart;
    }

    public BlockState getState() {
        return state;
    }

    public void calcScoreFromStart(Block parent) {
        if (parent == null) {
            scoreFromStart = 0;
            throw new NullPointerException("Error: Score for node without parent was attempted to be calculated.");
        } else {
            scoreFromStart = parent.scoreFromStart + calcEuclidDistanceTo(parent);
        }
    }

    public void calcDistanceScore(Block destination) {
        distanceScore = calcEuclidDistanceTo(destination);
    }

    public double calcEuclidDistanceTo(Block destination) {
        return Math.sqrt(
                Math.pow((destination.getRow() - this.row), 2) + Math.pow((destination.getColumn() - this.column), 2)
        );
    }

    public void calcTotalScoreAStar() {
        totalScore = scoreFromStart + distanceScore;
    }

    public void makeWall() {
//        this.getButton().setBackground(App.OBSTACLE_COLOR);
//        this.getButton().setEnabled(false);
        this.state = BlockState.NON_WALKABLE;
        this.setWalkable(false);
    }

    public void makeWalkable() {
//        this.getButton().setBackground(App.BLOCK_COLOR);
//        this.getButton().setEnabled(true);
        this.state = BlockState.WALKABLE;
        this.setWalkable(true);
    }

    public void makeWalked() {
        this.state = BlockState.WALKED;
        this.setWalkable(true);
    }

    public void makeNeighbour() {
        this.state = BlockState.NEIGHBOUR;
    }

    public void makeStartEnd() {
        this.state = BlockState.START_END;
        this.setWalkable(true);
    }

    public void makePath() {
        this.state = BlockState.PATH;
    }

    @Override
    public int compareTo(Block o) {
        return Double.compare(this.totalScore, o.totalScore);
    }


}
