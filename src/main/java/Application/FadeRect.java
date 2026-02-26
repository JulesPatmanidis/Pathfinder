package Application;

import java.awt.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class FadeRect extends Rectangle {

    private static final float INCREMENT = 0.05f;
    private static final java.util.List<FadeRect> animatingRects = new CopyOnWriteArrayList<>();

    private boolean inAnimation = false;
    private double fadeRatio = 0.0;


    public FadeRect(int row, int column, int width, int height) {
        super(column * width, row * height, width, height);
    }

    public static java.util.List<FadeRect> getAnimatingRects() {
        return animatingRects;
    }

    public static void clearAnimatingRects() {
        animatingRects.clear();
    }

    public boolean isInAnimation() {
        return inAnimation;
    }

    public double getFadeRatio() {
        return fadeRatio;
    }

    public void incrementFadeRatio() {
        fadeRatio += INCREMENT;
        if (fadeRatio >= 1.0) {
            fadeRatio = 1.0;
            inAnimation = false;
            synchronized (animatingRects) {
                animatingRects.remove(this);
            }
        }
    }

    public void startAnimation() {
        if (inAnimation) return;
        inAnimation = true;
        fadeRatio = 0.0;
        animatingRects.add(this);
    }
}
