package Model;

import java.util.List;

public class Grid {

    private List<List<Block>> blockList;
    private int columns;
    private int rows;

    public Grid(List<List<Block>> blockList) {
        this.blockList = blockList;
        this.rows = blockList.size();
        this.columns = blockList.getFirst().size();
    }

    public List<List<Block>> getBlockList() {
        return blockList;
    }

    public void setBlockList(List<List<Block>> blockList) {
        this.blockList = blockList;
    }

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    public Block getBlock(int row, int col) {
        if (row >= blockList.size() || col >= blockList.get(row).size()) {
            return null;
        }

        return blockList.get(row).get(col);
    }
}
