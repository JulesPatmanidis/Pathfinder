import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class Block implements Comparable<Block>{

    private mapButton button;
    private Node node;
    private transient Block parentBlock;

    public Block(int row, int column) {
        node = new Node(row, column);
        button = new mapButton(row, column);
    }

    public void setButton(mapButton button) {
        this.button = button;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public Node getNode() {
        return node;
    }

    public JButton getButton() {
        return button;
    }

    public Block getParentBlock() {
        return parentBlock;
    }

    public void setParentBlock(Block block) {
        this.parentBlock = block;
    }


    @Override
    public int compareTo(Block o) {
        return this.getNode().compareTo(o.getNode());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Block)) return false;
        Block block = (Block) o;
        return getNode().getId().equals(block.getNode().getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNode());
    }
}
