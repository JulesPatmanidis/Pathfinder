package Application;

import java.awt.Color;
import java.util.concurrent.CopyOnWriteArrayList;

public class FadeRect {

    private static final long ANIMATION_DURATION_NANOS = 320_000_000L;
    private static final java.util.List<FadeRect> animatingRects = new CopyOnWriteArrayList<>();

    private final int row;
    private final int column;
    private volatile boolean inAnimation = false;
    private volatile double fadeRatio = 0.0;
    private volatile long animationStartNanos = 0L;
    private Color animationStartColor = App.BLOCK_COLOR;
    private Color animationEndColor = App.BLOCK_COLOR;
    private Color currentColor = App.BLOCK_COLOR;


    public FadeRect(int row, int column) {
        this.row = row;
        this.column = column;
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

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
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
        currentColor = interpolate(animationStartColor, animationEndColor, fadeRatio);
        if (fadeRatio >= 1.0) {
            fadeRatio = 1.0;
            currentColor = animationEndColor;
            inAnimation = false;
            animatingRects.remove(this);
        }
    }

    public synchronized Color getCurrentColor() {
        return currentColor;
    }

    public synchronized void setCurrentColor(Color currentColor) {
        this.currentColor = currentColor;
    }

    public synchronized void startAnimation(Color targetColor) {
        animationStartColor = currentColor;
        animationEndColor = targetColor;
        animationStartNanos = System.nanoTime();
        fadeRatio = 0.0;
        inAnimation = true;
        if (!animatingRects.contains(this)) {
            animatingRects.add(this);
        }
    }

    private Color interpolate(Color startColor, Color endColor, double ratio) {
        int red = (int) ((1 - ratio) * startColor.getRed() + ratio * endColor.getRed());
        int green = (int) ((1 - ratio) * startColor.getGreen() + ratio * endColor.getGreen());
        int blue = (int) ((1 - ratio) * startColor.getBlue() + ratio * endColor.getBlue());
        return new Color(red, green, blue);
    }
}
