public class Node implements Comparable<Node> {
    private boolean walkable;
    //private Block block;
    private int totalScore;
    private int distanceScore;
    private int scoreFromStart;
    private int xPos;
    private int yPos;
    private String id;

    public Node(int x, int y) {
        walkable = true;
        xPos = x;
        yPos = y;
        id = generateID(x, y);
    }

    public Node(Node node) {
        walkable = node.isWalkable();
        xPos = node.getxPos();
        yPos = node.getyPos();
        id = node.getId();
        distanceScore = node.getDistanceScore();
    }

    public int getxPos() {
        return xPos;
    }


    public int getyPos() {
        return yPos;
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


    public void calcDistanceScore (Node destination){
        distanceScore =  (int) (Math.pow((destination.getxPos() - this.xPos), 2) + Math.pow((destination.getyPos() - this.yPos), 2));
    }

    // Works better if scoreFromStart is not taken into account
    public void calcTotalScore() {
        totalScore = distanceScore + scoreFromStart;
    }

    private String generateID(int x, int y) {
        return String.valueOf(0.5*(x + y)*(x + y + 1) + y);
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
