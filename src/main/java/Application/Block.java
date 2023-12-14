package Application;

import javax.swing.*;

public class Block implements Comparable<Block>{

    private final mapButton button;
    private final int row;
    private final int column;
    private transient Block parentBlock;
    private boolean walkable;
    private double totalScore;
    private double distanceScore;
    private double scoreFromStart = Double.MAX_VALUE;

    public Block(int row, int column) {
        walkable = true;
        this.row = row;
        this.column = column;
        button = new mapButton();
    }

    public mapButton getButton() {
        return button;
    }

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
        this.getButton().setBackground(App.OBSTACLE_COLOR);
        this.getButton().setEnabled(false);
        this.setWalkable(false);
    }

    public void makePath() {
        this.getButton().setBackground(App.BLOCK_COLOR);
        this.getButton().setEnabled(true);
        this.setWalkable(true);
    }

    @Override
    public int compareTo(Block o) {
        return Double.compare(this.totalScore, o.totalScore);
    }


}
