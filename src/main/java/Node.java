public class Node implements Comparable<Node> {
    private boolean walkable;
    //private Block block;
    private int totalScore;
    private int distanceScore;
    private int scoreFromStart;
    private int row;
    private int column;
    private String id;

    public Node(int row, int column) {
        walkable = true;
        this.row = row;
        this.column = column;
        id = generateID(row, column);
    }

    public Node(Node node) {
        walkable = node.isWalkable();
        row = node.getRow();
        column = node.getColumn();
        id = node.getId();
        distanceScore = node.getDistanceScore();
    }

    public int getRow() {
        return row;
    }


    public int getColumn() {
        return column;
    }


    public String getId() {
        return id;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public int getDistanceScore() {
        return distanceScore;
    }

    public int getScoreFromStart() {
        return scoreFromStart;
    }

    public void setScoreFromStart(int scoreFromStart) {
        this.scoreFromStart = scoreFromStart;
    }

    public void calcScoreFromStart(Node parent) {
        if (parent == null) {
            scoreFromStart = 0;
        } else {
            this.scoreFromStart = parent.getTotalScore() + 1;
        }
    }

    public boolean isWalkable() {
        return walkable;
    }


    public void setWalkable(boolean walkable) {
        this.walkable = walkable;
    }


    public void calcDistanceScore(Node destination) {
        distanceScore = (int) (Math.pow((destination.getRow() - this.row), 2) +
                Math.pow((destination.getColumn() - this.column), 2));
    }

    // Works better if scoreFromStart is not taken into account
    public void calcTotalScore() {
        totalScore = distanceScore + scoreFromStart;
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
