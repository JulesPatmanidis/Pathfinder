package Application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

public class mapButton extends JButton {

    private static final int BUTTON_DIM = 18;
    public static final Color BLOCK_COLOR = new Color(105,105,105);
    public static final Color BLOCK_BORDER_COLOR = Color.BLACK;

    private final Color fadeFromColor = Color.white;
    //private final Color fadeToColor = App.ACCENT_COLOR;
    private final int fadeStepsNeighbour = 20;
    private final int fadeStepsPath = 10;

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

    public void paintNeighbour() {
        try {
            if (App.isFadeChecked) {
                //rectFade(App.ACCENT_COLOR);
                //SwingUtilities.invokeAndWait(() -> rectFade(App.ACCENT_COLOR, fadeStepsNeighbour));
                SwingUtilities.invokeAndWait(() -> this.setBackground(App.ACCENT_COLOR));
            } else {
                SwingUtilities.invokeAndWait(() -> this.setBackground(App.ACCENT_COLOR));
            }

            // Add delay
            try {
                TimeUnit.MILLISECONDS.sleep(App.paintDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (InterruptedException e) {
            System.err.println("Error: Thread was interrupted");
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void paintPath() {
        try {
            if (App.isFadeChecked) {
                //rectFade(App.ACCENT_COLOR);
                SwingUtilities.invokeAndWait(() -> rectFade(App.PATH_COLOR, fadeStepsPath));
            } else {
                SwingUtilities.invokeAndWait(() -> this.setBackground(App.PATH_COLOR));
            }

            // Add delay
            try {
                TimeUnit.MILLISECONDS.sleep(App.ROUTE_PAINT_DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (InterruptedException e) {
            System.err.println("Error: Thread was interrupted");
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void paintCurrent() {
        try {
            SwingUtilities.invokeAndWait(() -> rectFade(Color.ORANGE, 10));
        } catch (InterruptedException e) {
            System.err.println("Error: Thread was interrupted");
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void rectFade(Color fadeTo, int fadeSteps) {
        new FadeRunnable(fadeTo, fadeSteps);
    }

    private Color lerp(Color start, Color end, double interpolationValue) {
        int r = (int) (start.getRed() + interpolationValue * (end.getRed() - start.getRed()));
        int g = (int) (start.getGreen() + interpolationValue * (end.getGreen() - start.getGreen()));
        int b = (int) (start.getBlue() + interpolationValue * (end.getBlue() - start.getBlue()));
        return new Color(r, g, b);
    }

    class FadeRunnable implements Runnable {
        private final Color fadeToColor;
        private final int fadeSteps;
        private final Timer timer;
        private double interpolationValue = 0;

        public FadeRunnable(Color fadeTo, int fadeSteps) {
            this.fadeToColor = fadeTo;
            this.fadeSteps = fadeSteps;
            this.timer = new Timer(15, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    interpolationValue += 1f / fadeSteps; // Adjust the step size as needed for the desired speed
                    Color newColor = lerp(fadeFromColor, fadeTo, interpolationValue);

                    setBackground(newColor);

                    if (interpolationValue >= 1.0f) {
                        timer.stop();
                        interpolationValue = 0.0f;
                    }
                }
            });

            Thread runner = new Thread(this);
            //System.out.println("fadeRunnable");
            runner.start();
        }

        @Override
        public void run() {
            Color oldColor = fadeFromColor;
            int dRed = fadeToColor.getRed() - oldColor.getRed();
            int dGreen = fadeToColor.getGreen() - oldColor.getGreen();
            int dBlue = fadeToColor.getBlue() - oldColor.getBlue();

            for (int i = 0; i <= fadeSteps; i++) {
                final Color c = new Color(
                        oldColor.getRed() + ((dRed * i) / fadeSteps),
                        oldColor.getGreen() + ((dGreen * i) / fadeSteps),
                        oldColor.getBlue() + ((dBlue * i) / fadeSteps));
                setBackground(c);

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //timer.start(); //Alternative "more correct" implementation of the animation, but performs worse
        }
    }
}
