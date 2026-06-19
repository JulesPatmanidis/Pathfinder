package Mazes;

import Model.Grid;
import Model.GridChangeListener;

public interface MazeGenerator {
    String getName();

    void generate(Grid grid, GridChangeListener listener);
}
