public class Node implements Comparable<Node> {
    private boolean walkable;
    //private Block block;
    private double totalScore;
    private double distanceScore;
    private double scoreFromStart = Double.MAX_VALUE;
    private int row;
    private int column;
    private String id;

    public Node(int row, int column) {
        walkable = true;
        this.row = row;
        this.column = column;
        id = generateID(row, column);
    }

//    public Node(Node node) {
//        walkable = node.isWalkable();
//        row = node.getRow();
//        column = node.getColumn();
//        id = node.getId();
//        distanceScore = node.getDistanceScore();
//    }

    public int getRow() {
        return row;
    }


    public int getColumn() {
        return column;
    }


    public String getId() {
        return id;
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

    public void calcScoreFromStart(Node parent) {
        if (parent == null) {
            scoreFromStart = 0;
            throw new NullPointerException("Error: Score for node without parent was attempted to be calculated.");
        } else {
            scoreFromStart = parent.scoreFromStart + calcEuclidDistanceTo(parent);
        }
    }

    public boolean isWalkable() {
        return walkable;
    }


    public void setWalkable(boolean walkable) {
        this.walkable = walkable;
    }


    public void calcDistanceScore(Node destination) {
        distanceScore = calcEuclidDistanceTo(destination);
    }

    public double calcEuclidDistanceTo(Node destination) {
        return Math.sqrt(
                Math.pow((destination.getRow() - this.row), 2) + Math.pow((destination.getColumn() - this.column), 2)
        );
    }

    public void calcTotalScoreAStar() {
        totalScore = scoreFromStart + distanceScore;
        //totalScore = scoreFromStart;
        //totalScore = distanceScore;
    }

    public void calcTotalScoreDfs() {
        totalScore = scoreFromStart;
    }


    private String generateID(int row, int column) {
        return String.valueOf(0.5 * (row + column) * (row + column + 1) + column);
    }

    @Override
    public int compareTo(Node o) {
        if (this.totalScore > o.totalScore) {
            return 1;
        } else if (this.totalScore == o.totalScore) {
            return 0;
        } else return -1;
    }
}
