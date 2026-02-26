package Application;

import java.awt.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class FadeRect extends Rectangle {

    private static final long ANIMATION_DURATION_NANOS = 320_000_000L;
    private static final java.util.List<FadeRect> animatingRects = new CopyOnWriteArrayList<>();

    private volatile boolean inAnimation = false;
    private volatile double fadeRatio = 0.0;
    private volatile long animationStartNanos = 0L;


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

    public synchronized void incrementFadeRatio() {
        if (!inAnimation) {
            animatingRects.remove(this);
            return;
        }
        long elapsedNanos = Math.max(0L, System.nanoTime() - animationStartNanos);
        fadeRatio = Math.min(1.0, elapsedNanos / (double) ANIMATION_DURATION_NANOS);
        if (fadeRatio >= 1.0) {
            fadeRatio = 1.0;
            inAnimation = false;
            animatingRects.remove(this);
        }
    }

    public synchronized void startAnimation() {
        animationStartNanos = System.nanoTime();
        fadeRatio = 0.0;
        inAnimation = true;
        if (!animatingRects.contains(this)) {
            animatingRects.add(this);
        }
    }
}
