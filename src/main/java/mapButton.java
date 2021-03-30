import javax.swing.*;
import java.awt.*;

public class mapButton extends JButton {

    private static final int BUTTON_DIM = 18;    //15
    public static final Color BLOCK_COLOR = new Color(105,105,105);
    public static final Color BLOCK_BORDER_COLOR = Color.BLACK;
    public static final Color PRESSED_COLOR = new Color(83, 0, 2);


    public mapButton(int row, int column) {
        setBackground(BLOCK_COLOR);
        setPreferredSize(new Dimension(BUTTON_DIM, BUTTON_DIM));
        setBorder(BorderFactory.createLineBorder(BLOCK_BORDER_COLOR));
        String text = "" + row + ", " + column;
        //setText(text);
        setForeground(Color.WHITE);
        setFont(new Font("Times New Roman", Font.PLAIN, 10));
    }

    public void setButtonText(String text) {
        setText(text);
    }

}
