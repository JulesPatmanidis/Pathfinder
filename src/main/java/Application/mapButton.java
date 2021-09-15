package Application;

import javax.swing.*;
import java.awt.*;

public class mapButton extends JButton {

    private static final int BUTTON_DIM = 18;
    public static final Color BLOCK_COLOR = new Color(105,105,105);
    public static final Color BLOCK_BORDER_COLOR = Color.BLACK;



    public mapButton() {
        setBackground(BLOCK_COLOR);
        setPreferredSize(new Dimension(BUTTON_DIM, BUTTON_DIM));
        setBorder(BorderFactory.createLineBorder(BLOCK_BORDER_COLOR));
        setForeground(Color.WHITE);
        setFont(new Font("Times New Roman", Font.PLAIN, 10));
    }

    public void setButtonText(String text) {
        setText(text);
    }

}
