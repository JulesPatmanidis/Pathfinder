import javax.swing.*;
import java.awt.*;

public class Block implements Comparable<Block>{

    private JButton button;
    private Node node;
    private transient Block parentBlock;

    public Block(int x, int y) {
        button = new JButton();
        node = new Node(x, y);
    }


    public Block(Block block) {
        button = block.getButton();
        node = new Node(block.getNode());

    }


    public void setButton(JButton button) {
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
        if (this.getNode().getTotalScore() > o.getNode().getTotalScore()) {
            return 1;
        } else if (this.getNode().getTotalScore() == o.getNode().getTotalScore()) {
            return 0;
        } else return -1;
    }
}
