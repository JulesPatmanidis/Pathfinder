package Application;

import java.awt.*;

public class mapRect extends Rectangle {


    private static final int BUTTON_DIM = 18;
    public static final Color BLOCK_COLOR = new Color(105,105,105);
    public static final Color BLOCK_BORDER_COLOR = Color.BLACK;

    private final Color fadeFromColor = Color.white;
    //private final Color fadeToColor = App.ACCENT_COLOR;
    private final int fadeStepsNeighbour = 20;
    private final int fadeStepsPath = 10;

    public mapRect(int row, int column) {
        super(column * BUTTON_DIM, row * BUTTON_DIM, BUTTON_DIM, BUTTON_DIM);
    }

    public void paintAsNeighbour() {

    }

}
