package Application;

import java.awt.Color;
import java.util.concurrent.CopyOnWriteArrayList;

public class CellAnimation {

    private enum AnimationType {
        FADE,
        PATH_SCALE
    }

    private static final long FADE_ANIMATION_DURATION_NANOS = 320_000_000L;
    private static final long PATH_ANIMATION_DURATION_NANOS = 520_000_000L;
    private static final double PATH_START_SCALE = 1.75;
    private static final java.util.List<CellAnimation> animatingCells = new CopyOnWriteArrayList<>();

    private final int row;
    private final int column;
    private volatile boolean inAnimation = false;
    private volatile double animationRatio = 0.0;
    private volatile long animationStartNanos = 0L;
    private AnimationType animationType = AnimationType.FADE;
    private Color animationStartColor = App.BLOCK_COLOR;
    private Color animationEndColor = App.BLOCK_COLOR;
    private Color currentColor = App.BLOCK_COLOR;
    private double scale = 1.0;


    public CellAnimation(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public static java.util.List<CellAnimation> getAnimatingCells() {
        return animatingCells;
    }

    public static void clearAnimatingCells() {
        animatingCells.clear();
    }

    public static double getPathStartScale() {
        return PATH_START_SCALE;
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

    public synchronized void step() {
        if (!inAnimation) {
            animatingCells.remove(this);
            return;
        }
        long elapsedNanos = Math.max(0L, System.nanoTime() - animationStartNanos);
        long durationNanos = animationType == AnimationType.PATH_SCALE
                ? PATH_ANIMATION_DURATION_NANOS
                : FADE_ANIMATION_DURATION_NANOS;
        animationRatio = Math.min(1.0, elapsedNanos / (double) durationNanos);

        if (animationType == AnimationType.FADE) {
            currentColor = interpolate(animationStartColor, animationEndColor, animationRatio);
        } else {
            currentColor = animationEndColor;
            scale = PATH_START_SCALE - ((PATH_START_SCALE - 1.0) * animationRatio);
        }

        if (animationRatio >= 1.0) {
            animationRatio = 1.0;
            currentColor = animationEndColor;
            scale = 1.0;
            inAnimation = false;
            animatingCells.remove(this);
        }
    }

    public synchronized Color getCurrentColor() {
        return currentColor;
    }

    public synchronized void setCurrentColor(Color currentColor) {
        this.currentColor = currentColor;
    }

    public synchronized double getScale() {
        return scale;
    }

    public synchronized void startFadeAnimation(Color targetColor) {
        animationType = AnimationType.FADE;
        animationStartColor = currentColor;
        animationEndColor = targetColor;
        animationStartNanos = System.nanoTime();
        animationRatio = 0.0;
        scale = 1.0;
        inAnimation = true;
        if (!animatingCells.contains(this)) {
            animatingCells.add(this);
        }
    }

    public synchronized void startPathAnimation(Color targetColor) {
        animationType = AnimationType.PATH_SCALE;
        animationStartColor = targetColor;
        animationEndColor = targetColor;
        currentColor = targetColor;
        animationStartNanos = System.nanoTime();
        animationRatio = 0.0;
        scale = PATH_START_SCALE;
        inAnimation = true;
        if (!animatingCells.contains(this)) {
            animatingCells.add(this);
        }
    }

    private Color interpolate(Color startColor, Color endColor, double ratio) {
        int red = (int) ((1 - ratio) * startColor.getRed() + ratio * endColor.getRed());
        int green = (int) ((1 - ratio) * startColor.getGreen() + ratio * endColor.getGreen());
        int blue = (int) ((1 - ratio) * startColor.getBlue() + ratio * endColor.getBlue());
        return new Color(red, green, blue);
    }
}
