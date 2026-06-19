package Model;

import java.util.List;

public class Grid {

    private Block[][] blockList;
    private int columns;
    private int rows;

    public Grid(Block[][] blockList) {
        this.blockList = blockList;
        this.rows = blockList.length;
        this.columns = blockList[0].length;
    }

    public Block[][] getBlockList() {
        return blockList;
    }

    public void setBlockList(Block[][] blockList) {
        this.blockList = blockList;
    }

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    public Block getBlock(int row, int col) {
        if (row >= blockList.length || col >= blockList[row].length) {
            return null;
        }

        return blockList[row][col];
    }
}
