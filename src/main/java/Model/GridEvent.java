package Model;

public record GridEvent(int row, int column, BlockState state, boolean animate) {
}
