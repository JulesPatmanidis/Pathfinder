import javax.swing.*;

/**
 * Main class of the program. Responsible for running the program.
 */
public class Main {

    public static void main(String[] args) {
        Gui mainGui = null;
        run(mainGui);
    }

    public static void run(Gui gui) {
        if (gui != null) {
            gui.dispose();
        }

        gui = new Gui();
        gui.setVisible(true);
    }
}
