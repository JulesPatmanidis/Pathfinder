package Application;

import Model.Block;
import Model.Grid;
import Model.GridEvent;
import Mazes.AldousBroderMazeGenerator;
import Mazes.MazeGenerator;
import Mazes.RandomizedPrimsMazeGenerator;
import Mazes.RecursiveBacktrackingMazeGenerator;
import Pathfinders.*;
import Utilities.Utils;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class App {
    private static final int DEFAULT_CELL_SIZE = 10;
    private static final int MIN_GRID_ROWS = 1;
    private static final int MIN_GRID_COLUMNS = 5;
    public static final String A_STAR = "A-Star";
    public static final String DIJKSTRA = "Dijkstra";
    public static final String BEST_FIRST_SEARCH = "BestFirstSearch";
    public static final String BREADTH_FIRST_SEARCH = "BreadthFirstSearch";
    public static final String DEPTH_FIRST_SEARCH = "DepthFirstSearch";
    public static final String WORST_FIRST_SEARCH = "WorstFirstSearch";
    public static final Color DEFAULT_BACKGROUND = new Color(18, 18, 18);
    public static final Color DEFAULT_BLOCK_COLOR = new Color(139, 139, 144);
    public static final Color DEFAULT_BLOCK_BORDER_COLOR = new Color(55, 55, 55);
    public static final Color TEXT_COLOR = new Color(211, 211, 211);
    public static final Color DEFAULT_PRESSED_COLOR = new Color(139, 0, 0);
    public static final Color DEFAULT_OBSTACLE_COLOR = new Color(40, 40, 40);
    public static final Color DEFAULT_PATH_COLOR = new Color(229, 61, 61);
    public static final Color DEFAULT_ACCENT_COLOR = new Color(2, 76, 71);
    public static final Color DEFAULT_NEIGHBOUR_COLOR = new Color(255, 165, 0);
    public static final Color DEFAULT_VISITED_COLOR = new Color(2, 76, 71);
    public static Color BACKGROUND = DEFAULT_BACKGROUND;
    public static Color BLOCK_COLOR = DEFAULT_BLOCK_COLOR;
    public static Color BLOCK_BORDER_COLOR = DEFAULT_BLOCK_BORDER_COLOR;
    public static Color PRESSED_COLOR = DEFAULT_PRESSED_COLOR;
    public static Color OBSTACLE_COLOR = DEFAULT_OBSTACLE_COLOR;
    public static Color PATH_COLOR = DEFAULT_PATH_COLOR;
    public static Color ACCENT_COLOR = DEFAULT_ACCENT_COLOR;
    public static Color NEIGHBOUR_COLOR = DEFAULT_NEIGHBOUR_COLOR;
    public static Color VISITED_COLOR = DEFAULT_VISITED_COLOR;
    public static final Color PANEL_COLOUR = new Color(39, 39, 39);
    public static final Font GLOBAL_FONT = new Font("Sans Serif", Font.PLAIN, 20);
    private static final String FRAME_TITLE = "Pathfinder";
    private static final Border MAIN_BORDER = BorderFactory.createEmptyBorder(20, 20, 20, 20);
    private static final Border TOP_BORDER = BorderFactory.createEmptyBorder(10, 23, 10, 23);
    private static final String[] ALGORITHMS =
            {A_STAR, DIJKSTRA, BEST_FIRST_SEARCH, BREADTH_FIRST_SEARCH, DEPTH_FIRST_SEARCH, WORST_FIRST_SEARCH,};
    private static final String[] algorithmInfo = new String[ALGORITHMS.length];
    private static final MazeGenerator[] MAZE_GENERATORS = {
            new RecursiveBacktrackingMazeGenerator(),
            new RandomizedPrimsMazeGenerator(),
            new AldousBroderMazeGenerator()
    };

    public static boolean isFadeChecked = true;
    public static boolean allowDiagonal = true;
    private JPanel globalPanel;
    private JPanel bottomPanel;
    private JPanel leftPanel;
    private GridView centrePanel;
    private JLabel fpsLabel;
    private Timer fpsLabelTimer;
    private JButton resetButton;
    private JButton runButton;
    private JList<String> algorithmList;
    private JLabel instructionsLabel;
    private JCheckBox allowDiagonalCheckBox;
    private JCheckBox fadeCheckBox;
    private JSpinner cellSizeSpinner;
    private JButton applyGridButton;
    private JComboBox<String> paletteComboBox;
    private JSlider playbackSpeedSlider;
    private JLabel playbackSpeedLabel;
    private JLabel playbackSpeedValueLabel;
    private JTextArea infoTextArea;
    private JList<String> mazeList;
    private JTextArea mazeTitleTextArea;
    private int clickCount = 0;
    private Pathfinder pathfinder;
    private Grid grid;
    private Thread algorithmThread;
    private volatile int gridVersion = 0;
    private State state;
    private boolean isDrawingWall = false;
    private GridConfig gridConfig;


    private final java.util.Queue<QueuedGridEvent> eventQueue = new ConcurrentLinkedQueue<>();
    private Timer playbackTimer;
    
    private record AppTheme(String name, Color appBackground, Color panelBackground, Color controlBackground,
                            Color controlText, Color mutedText, Color listSelection, Color gridBackground,
                            Color blockColor, Color blockBorderColor, Color startEndColor, Color obstacleColor,
                            Color pathColor, Color accentColor, Color neighbourColor, Color visitedColor) {
    }

    private static final AppTheme[] APP_THEMES = {
            classicTheme(),
            craitTheme(),
            slateTheme(),
            futureTheme(),
            aztecTheme()
    };

    private static AppTheme classicTheme() {
        Color appBackground = DEFAULT_BACKGROUND;
        Color panelBackground = PANEL_COLOUR;
        Color controlBackground = DEFAULT_ACCENT_COLOR;
        Color controlText = TEXT_COLOR;
        Color mutedText = new Color(184, 184, 184);
        Color listSelection = DEFAULT_PATH_COLOR;

        Color gridBackground = DEFAULT_BACKGROUND;
        Color blockColor = new Color(139, 139, 144);
        Color blockBorderColor = new Color(40, 40, 40);
        Color startEndColor = new Color(139, 0, 0);
        Color obstacleColor = new Color(40, 40, 40);
        Color pathColor = new Color(229, 61, 61);
        Color accentColor = new Color(2, 76, 71);
        Color neighbourColor = new Color(255, 165, 0);
        Color visitedColor = new Color(2, 76, 71);

        return new AppTheme(
                "Classic",
                appBackground, panelBackground, controlBackground, controlText, mutedText, listSelection,
                gridBackground, blockColor, blockBorderColor, startEndColor, obstacleColor, pathColor,
                accentColor, neighbourColor, visitedColor
        );
    }

    private static AppTheme craitTheme() {
        Color appBackground = new Color(16, 17, 19);
        Color panelBackground = new Color(31, 33, 38);
        Color controlBackground = new Color(70, 28, 34);
        Color controlText = new Color(245, 242, 235);
        Color mutedText = new Color(190, 194, 198);
        Color listSelection = new Color(181, 24, 39);

        Color gridBackground = new Color(16, 17, 19);
        Color blockColor = new Color(228, 230, 238);
        Color blockBorderColor = new Color(52, 54, 58);
        Color startEndColor = new Color(92, 13, 24);
        Color obstacleColor = new Color(29, 31, 35);
        Color pathColor = new Color(204, 25, 38);
        Color accentColor = new Color(70, 28, 34);
        Color neighbourColor = new Color(204, 25, 38);
        Color visitedColor = new Color(186, 193, 207);

        return new AppTheme(
                "Crait",
                appBackground, panelBackground, controlBackground, controlText, mutedText, listSelection,
                gridBackground, blockColor, blockBorderColor, startEndColor, obstacleColor, pathColor,
                accentColor, neighbourColor, visitedColor
        );
    }

    private static AppTheme slateTheme() {
        Color appBackground = DEFAULT_BACKGROUND;
        Color panelBackground = new Color(27, 42, 46);
        Color controlBackground = new Color(24, 58, 55);
        Color controlText = new Color(239, 214, 172);
        Color mutedText = new Color(198, 177, 142);
        Color listSelection = new Color(196, 73, 0);

        Color gridBackground = DEFAULT_BACKGROUND;
        Color blockColor = new Color(239, 214, 172);
        Color blockBorderColor = DEFAULT_BLOCK_BORDER_COLOR;
        Color startEndColor = DEFAULT_PRESSED_COLOR;
        Color obstacleColor = new Color(4, 21, 31);
        Color pathColor = new Color(238, 137, 53);
        Color accentColor = DEFAULT_ACCENT_COLOR;
        Color neighbourColor = new Color(196, 73, 0);
        Color visitedColor = new Color(24, 58, 55);

        return new AppTheme(
                "Slate",
                appBackground, panelBackground, controlBackground, controlText, mutedText, listSelection,
                gridBackground, blockColor, blockBorderColor, startEndColor, obstacleColor, pathColor,
                accentColor, neighbourColor, visitedColor
        );
    }

    private static AppTheme futureTheme() {
        Color appBackground = DEFAULT_BACKGROUND;
        Color panelBackground = new Color(28, 36, 39);
        Color controlBackground = new Color(8, 126, 139);
        Color controlText = new Color(245, 245, 245);
        Color mutedText = new Color(196, 207, 209);
        Color listSelection = new Color(255, 90, 95);

        Color gridBackground = DEFAULT_BACKGROUND;
        Color blockColor = new Color(245, 245, 245);
        Color blockBorderColor = DEFAULT_BLOCK_BORDER_COLOR;
        Color startEndColor = DEFAULT_PRESSED_COLOR;
        Color obstacleColor = new Color(60, 60, 60);
        Color pathColor = new Color(255, 214, 10);
        Color accentColor = DEFAULT_ACCENT_COLOR;
        Color neighbourColor = new Color(255, 90, 95);
        Color visitedColor = new Color(8, 126, 139);

        return new AppTheme(
                "Future",
                appBackground, panelBackground, controlBackground, controlText, mutedText, listSelection,
                gridBackground, blockColor, blockBorderColor, startEndColor, obstacleColor, pathColor,
                accentColor, neighbourColor, visitedColor
        );
    }

    private static AppTheme aztecTheme() {
        Color appBackground = DEFAULT_BACKGROUND;
        Color panelBackground = new Color(31, 41, 24);
        Color controlBackground = new Color(96, 108, 56);
        Color controlText = new Color(254, 250, 224);
        Color mutedText = new Color(216, 207, 171);
        Color listSelection = new Color(221, 161, 94);

        Color gridBackground = DEFAULT_BACKGROUND;
        Color blockColor = new Color(158, 155, 134);
        Color blockBorderColor = DEFAULT_BLOCK_BORDER_COLOR;
        Color startEndColor = DEFAULT_PRESSED_COLOR;
        Color obstacleColor = new Color(40, 54, 24);
        Color pathColor = new Color(255, 198, 55);
        Color accentColor = DEFAULT_ACCENT_COLOR;
        Color neighbourColor = new Color(221, 161, 94);
        Color visitedColor = new Color(96, 108, 56);

        return new AppTheme(
                "Aztec",
                appBackground, panelBackground, controlBackground, controlText, mutedText, listSelection,
                gridBackground, blockColor, blockBorderColor, startEndColor, obstacleColor, pathColor,
                accentColor, neighbourColor, visitedColor
        );
    }

    private static final class GridConfig {
        private final int rows;
        private final int cols;
        private final int cellSize;

        private GridConfig(int rows, int cols, int cellSize) {
            this.rows = rows;
            this.cols = cols;
            this.cellSize = cellSize;
        }
    }

    private record QueuedGridEvent(GridEvent event, int gridVersion) {
    }

    public App() {
        try {
            List<String> algorithmInfoLines = Utils.parseAlgorithmInfo();
            for (int i = 0; i < algorithmInfo.length && i < algorithmInfoLines.size(); i++) {
                algorithmInfo[i] = algorithmInfoLines.get(i);
            }
        } catch (IOException e) {
            System.err.println("Algorithm info parsing failed");
        }
        gridConfig = createDefaultGridConfig();
        createUIComponents();
        initPathfinder();
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            App app = new App();
            JFrame frame = new JFrame(FRAME_TITLE);
            frame.setResizable(true);
            frame.setContentPane(app.globalPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setBounds(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds());
            frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
            frame.setVisible(true);
        });
    }

    /*
        INITIALISATION-------------------------------------------------------------------------------------------------
     */

    private void createUIComponents() {
        // GLOBAL PANEL --------------------------------------------------
        globalPanel = new JPanel();
        globalPanel.setLayout(new BorderLayout(0, 0));

        // TOP PANEL
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.setBackground(PANEL_COLOUR);

        JLabel topLabel = new JLabel("PATHFINDER");
        topLabel.setFont(new Font("Sans Serif", Font.BOLD, 40));
        topLabel.setForeground(TEXT_COLOR);
        topLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        topPanel.add(topLabel, BorderLayout.CENTER);

        fpsLabel = new JLabel("FPS: --");
        fpsLabel.setFont(new Font("Sans Serif", Font.PLAIN, 18));
        fpsLabel.setForeground(TEXT_COLOR);
        fpsLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        topPanel.add(fpsLabel, BorderLayout.EAST);

        globalPanel.add(topPanel, BorderLayout.NORTH);

        // BOTTOM PANEL -----------------------------------------------------

        bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout(0, 8));
        bottomPanel.setAlignmentX(1f);
        bottomPanel.setBorder(TOP_BORDER);
        bottomPanel.setBackground(PANEL_COLOUR);
        globalPanel.add(bottomPanel, BorderLayout.SOUTH);

        JPanel bottomInfoRow = new JPanel();
        bottomInfoRow.setLayout(new BoxLayout(bottomInfoRow, BoxLayout.X_AXIS));
        bottomInfoRow.setBackground(PANEL_COLOUR);
        bottomInfoRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel bottomControlsRow = new JPanel();
        bottomControlsRow.setLayout(new BoxLayout(bottomControlsRow, BoxLayout.X_AXIS));
        bottomControlsRow.setBackground(PANEL_COLOUR);
        bottomControlsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        instructionsLabel = new JLabel();
        instructionsLabel.setText("Select starting and ending block...");
        instructionsLabel.setOpaque(true);
        instructionsLabel.setBackground(PANEL_COLOUR);
        instructionsLabel.setForeground(TEXT_COLOR);
        instructionsLabel.setFont(GLOBAL_FONT);
        bottomInfoRow.add(instructionsLabel);

        bottomInfoRow.add(Box.createHorizontalGlue());

        playbackSpeedLabel = new JLabel("Playback:");
        playbackSpeedLabel.setFont(GLOBAL_FONT);
        playbackSpeedLabel.setBackground(PANEL_COLOUR);
        playbackSpeedLabel.setForeground(TEXT_COLOR);
        bottomControlsRow.add(playbackSpeedLabel);
        bottomControlsRow.add(Box.createRigidArea(new Dimension(10, 0)));

        playbackSpeedValueLabel = new JLabel();
        playbackSpeedValueLabel.setFont(GLOBAL_FONT);
        playbackSpeedValueLabel.setBackground(PANEL_COLOUR);
        playbackSpeedValueLabel.setForeground(TEXT_COLOR);
        playbackSpeedValueLabel.setPreferredSize(new Dimension(120, 40));
        playbackSpeedValueLabel.setMinimumSize(new Dimension(120, 40));

        bottomControlsRow.add(playbackSpeedValueLabel);

        //bottomPanel.add(Box.createRigidArea(new Dimension(10,0)));

        playbackSpeedSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
        playbackSpeedSlider.setSnapToTicks(false);
        playbackSpeedSlider.setFont(GLOBAL_FONT);
        playbackSpeedSlider.setBackground(BLOCK_BORDER_COLOR);
        playbackSpeedSlider.setForeground(TEXT_COLOR);
        playbackSpeedSlider.setMaximumSize(new Dimension(150, 35));
        playbackSpeedSlider.setToolTipText("Playback speed");

        playbackSpeedSlider.addChangeListener(changeEvent -> {
            updatePlaybackSpeedLabel();
        });

        updatePlaybackSpeedLabel();
        bottomControlsRow.add(playbackSpeedSlider);
        bottomControlsRow.add(Box.createRigidArea(new Dimension(10, 0)));

        allowDiagonalCheckBox = new JCheckBox();
        allowDiagonalCheckBox.setAlignmentX(0f);
        allowDiagonalCheckBox.setText("Allow Diagonal movement");
        allowDiagonalCheckBox.setBackground(ACCENT_COLOR);
        allowDiagonalCheckBox.setForeground(TEXT_COLOR);
        allowDiagonalCheckBox.setFont(GLOBAL_FONT);
        allowDiagonalCheckBox.setFocusPainted(false);
        allowDiagonalCheckBox.setSelected(true);
        allowDiagonalCheckBox.setBorder(new JButton().getBorder());
        allowDiagonalCheckBox.setBorderPainted(true);
        allowDiagonalCheckBox.addActionListener(x -> allowDiagonal = allowDiagonalCheckBox.isSelected());
        bottomControlsRow.add(allowDiagonalCheckBox);

        bottomControlsRow.add(Box.createRigidArea(new Dimension(10, 0)));

        fadeCheckBox = new JCheckBox();
        fadeCheckBox.setAlignmentX(0f);
        fadeCheckBox.setText("Fade");
        fadeCheckBox.setBackground(ACCENT_COLOR);
        fadeCheckBox.setForeground(TEXT_COLOR);
        fadeCheckBox.setFont(GLOBAL_FONT);
        fadeCheckBox.setFocusPainted(false);
        fadeCheckBox.setSelected(true);
        fadeCheckBox.setBorder(new JButton().getBorder());
        fadeCheckBox.setBorderPainted(true);
        fadeCheckBox.addActionListener(x -> isFadeChecked = fadeCheckBox.isSelected());
        bottomControlsRow.add(fadeCheckBox);

        bottomControlsRow.add(Box.createRigidArea(new Dimension(10, 0)));

        JLabel paletteLabel = new JLabel("Palette:");
        paletteLabel.setFont(GLOBAL_FONT);
        paletteLabel.setForeground(TEXT_COLOR);
        bottomControlsRow.add(paletteLabel);
        bottomControlsRow.add(Box.createRigidArea(new Dimension(6, 0)));

        String[] paletteNames = Arrays.stream(APP_THEMES).map(theme -> theme.name).toArray(String[]::new);
        paletteComboBox = new JComboBox<>(paletteNames);
        paletteComboBox.setMaximumSize(new Dimension(150, 35));
        paletteComboBox.setFont(new Font("Sans Serif", Font.PLAIN, 16));
        paletteComboBox.addActionListener(event -> applySelectedTheme());
        bottomControlsRow.add(paletteComboBox);

        bottomControlsRow.add(Box.createRigidArea(new Dimension(10, 0)));

        JLabel cellSizeLabel = new JLabel("Cell Size:");
        cellSizeLabel.setFont(GLOBAL_FONT);
        cellSizeLabel.setForeground(TEXT_COLOR);
        bottomControlsRow.add(cellSizeLabel);
        bottomControlsRow.add(Box.createRigidArea(new Dimension(4, 0)));

        cellSizeSpinner = new JSpinner(new SpinnerNumberModel(gridConfig.cellSize, 2, 100, 1));
        cellSizeSpinner.setMaximumSize(new Dimension(60, 35));
        bottomControlsRow.add(cellSizeSpinner);
        bottomControlsRow.add(Box.createRigidArea(new Dimension(8, 0)));

        applyGridButton = new JButton("Apply Grid");
        applyGridButton.setBackground(ACCENT_COLOR);
        applyGridButton.setForeground(TEXT_COLOR);
        applyGridButton.setFocusPainted(false);
        applyGridButton.setFont(GLOBAL_FONT);
        applyGridButton.addActionListener(event -> applyGridSettings());
        bottomControlsRow.add(applyGridButton);

        bottomControlsRow.add(Box.createRigidArea(new Dimension(10, 0)));

        resetButton = new JButton();
        resetButton.setText("Reset");
        resetButton.setBackground(ACCENT_COLOR);
        resetButton.setForeground(TEXT_COLOR);
        resetButton.setFocusPainted(false);
        resetButton.addActionListener(event -> resetApp());
        resetButton.setFont(GLOBAL_FONT);
        allowDiagonalCheckBox.setBorder(resetButton.getBorder());

        bottomControlsRow.add(resetButton);

        bottomControlsRow.add(Box.createRigidArea(new Dimension(10, 0)));

        runButton = new JButton();
        runButton.setText("Run");
        runButton.setBackground(ACCENT_COLOR);
        runButton.setForeground(TEXT_COLOR);
        runButton.setFocusPainted(false);
        runButton.addActionListener(event -> runButtonClicked());
        runButton.setFont(GLOBAL_FONT);
        bottomControlsRow.add(runButton);

        bottomControlsRow.add(Box.createHorizontalGlue());
        bottomPanel.add(bottomInfoRow, BorderLayout.NORTH);
        bottomPanel.add(bottomControlsRow, BorderLayout.SOUTH);


        // CENTRE PANEL -----------------------------------------------------
        centrePanel = new GridView();//JPanel();
        centrePanel.setBorder(MAIN_BORDER);
        centrePanel.setBackground(BACKGROUND);
        centrePanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent event) {
                applyGridSettingsFromResize();
            }
        });
        globalPanel.add(centrePanel, BorderLayout.CENTER);
        fpsLabelTimer = new Timer(250, event -> fpsLabel.setText("FPS: " + centrePanel.getCurrentFps()));
        fpsLabelTimer.start();
        playbackTimer = new Timer(16, event -> playGridEvents());
        playbackTimer.start();

        // LEFT PANEL -----------------------------------------------------
        int leftPanelWidth = 300;
        Border emptyBorder10 = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        Border listBorderInner = BorderFactory.createLineBorder(TEXT_COLOR, 4);
        Border listBorder = BorderFactory.createCompoundBorder(emptyBorder10, listBorderInner);

        leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(PANEL_COLOUR);
        leftPanel.setBorder(BorderFactory.createLineBorder(BACKGROUND, 4));
        globalPanel.add(leftPanel, BorderLayout.WEST);

        algorithmList = new JList<>();
        algorithmList.setAlignmentX(0f);

        algorithmList.setBorder(listBorder);
        algorithmList.setListData(ALGORITHMS);
        DefaultListCellRenderer renderer = (DefaultListCellRenderer) algorithmList.getCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        algorithmList.setSelectedIndex(0);
        algorithmList.setForeground(new Color(184, 184, 184));
        algorithmList.setBackground(PANEL_COLOUR);
        algorithmList.setSelectionBackground(PATH_COLOR);
        algorithmList.setFont(GLOBAL_FONT);
        algorithmList.addListSelectionListener(e -> {
            JList<String> list = (JList<String>) e.getSource();
            int selected = list.getSelectedIndex();
            infoTextArea.setText(algorithmInfo[selected]);
        });
        leftPanel.add(algorithmList);

        infoTextArea = new JTextArea();
        infoTextArea.setAlignmentX(0f);
        infoTextArea.setPreferredSize(new Dimension(leftPanelWidth, 260));
        infoTextArea.setMaximumSize(infoTextArea.getPreferredSize());
        infoTextArea.setBorder(emptyBorder10);
        infoTextArea.setBackground(PANEL_COLOUR);
        infoTextArea.setForeground(TEXT_COLOR);
        infoTextArea.setLineWrap(true);
        infoTextArea.setWrapStyleWord(true);
        infoTextArea.setEditable(false);
        infoTextArea.setText(algorithmInfo[0]);
        infoTextArea.setFont(GLOBAL_FONT);
        leftPanel.add(infoTextArea);

        leftPanel.add(Box.createVerticalGlue());

        mazeTitleTextArea = new JTextArea();
        mazeTitleTextArea.setAlignmentX(0f);
        mazeTitleTextArea.setPreferredSize(new Dimension(leftPanelWidth, 100));
        mazeTitleTextArea.setMaximumSize(mazeTitleTextArea.getPreferredSize());
        mazeTitleTextArea.setBorder(emptyBorder10);
        mazeTitleTextArea.setBackground(PANEL_COLOUR);
        mazeTitleTextArea.setForeground(TEXT_COLOR);
        mazeTitleTextArea.setLineWrap(true);
        mazeTitleTextArea.setWrapStyleWord(true);
        mazeTitleTextArea.setEditable(false);
        mazeTitleTextArea.setText("Select maze generation algorithm " +
                "(press 'm' to create maze before setting start/end)");
        mazeTitleTextArea.setFont(GLOBAL_FONT);
        leftPanel.add(mazeTitleTextArea);

        mazeList = new JList<>();
        mazeList.setAlignmentX(0f);

        mazeList.setBorder(listBorder);
        mazeList.setListData(Arrays.stream(MAZE_GENERATORS).map(MazeGenerator::getName).toArray(String[]::new));
        DefaultListCellRenderer mazeListRenderer = (DefaultListCellRenderer) mazeList.getCellRenderer();
        mazeListRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        //mazeList.setMaximumSize(mazeList.getSize());

        mazeList.setSelectedIndex(0);
        mazeList.setForeground(new Color(184, 184, 184));
        mazeList.setBackground(PANEL_COLOUR);
        mazeList.setSelectionBackground(PATH_COLOR);
        mazeList.setFont(GLOBAL_FONT);
        leftPanel.add(mazeList);

        // GENERAL ------------------------------------------------------------------

        // Disable button-press with space-bar
        InputMap im = (InputMap) UIManager.get("Button.focusInputMap");
        im.put(KeyStroke.getKeyStroke("pressed SPACE"), "none");
        im.put(KeyStroke.getKeyStroke("released SPACE"), "none");
        state = State.INITIALISE;
        applySelectedTheme();

    }

    /**
     * Initializes the pathfinder and sets up the block grid and mouse listeners.
     */
    private void initPathfinder() {
        gridVersion++;

        this.pathfinder = new AStarPathfinder();
        Arrays.stream(centrePanel.getMouseListeners()).forEach(centrePanel::removeMouseListener);
        Arrays.stream(centrePanel.getMouseMotionListeners()).forEach(centrePanel::removeMouseMotionListener);
        centrePanel.resetKeyboardActions();
        List<List<Block>> blockList = createBlockGrid();
        grid = new Grid(blockList);

        centrePanel.setGrid(grid, gridConfig.cellSize);
        pathfinder.setBlocksList(blockList);

        pathfinder.setMoveDiagonally(allowDiagonalCheckBox.isSelected());

        globalPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke('m'), "mAction");
        globalPanel.getActionMap().put("mAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectMaze();
            }
        });

        configurePathfinderCallbacks();

    }

    private void playGridEvents() {
        int eventsThisFrame = getEventsPerFrame();

        for (int i = 0; i < eventsThisFrame; i++) {
            QueuedGridEvent event = eventQueue.poll();
            if (event == null) {
                break;
            }

            if (event.gridVersion() != gridVersion) {
                continue;
            }

            GridEvent gridEvent = event.event();
            centrePanel.applyBlockChange(
                    gridEvent.row(),
                    gridEvent.column(),
                    gridEvent.state(),
                    gridEvent.animate()
            );
        }
    }

    private int getEventsPerFrame() {
        int value = playbackSpeedSlider.getValue();
        double t = value / 100.0;
        double maxEventsPerFrame = 10_000.0;
        double minEventsPerFrame = 1.0;
        return Math.max(1, (int) Math.round(maxEventsPerFrame * Math.pow(minEventsPerFrame / maxEventsPerFrame, t)));
    }

    private void updatePlaybackSpeedLabel() {
        playbackSpeedValueLabel.setText(getEventsPerFrame() + "/frame");
    }


    /*
        STATE --------------------------------------------------------------------------------------------------------
     */

    /**
     * Creates a 2D grid of Block objects and adds mouse listeners to the centrePanel to handle block selection.
     */
    private List<List<Block>> createBlockGrid() {
        List<List<Block>> tempList = new ArrayList<>();
        for (int i = 0; i < gridConfig.rows; i++) {
            tempList.add(i, new ArrayList<>());
            for (int j = 0; j < gridConfig.cols; j++) {
                Block current = new Block(i, j);
                tempList.get(i).add(j, current);
            }
        }

        centrePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = centrePanel.getColumnAtX(e.getX());
                int row = centrePanel.getRowAtY(e.getY());

                List<List<Block>> blocks = pathfinder.getBlocks();
                if (row < 0 || row >= blocks.size() || col < 0 || col >= blocks.get(row).size()) {
                    return;
                }

                Block clickedBlock = blocks.get(row).get(col);
                clickCount++;
                if (clickCount == 1) {
                    pathfinder.setStart(clickedBlock);
                    clickedBlock.makeStartEnd();
                    centrePanel.applyBlockChange(row, col, clickedBlock.getState(), false);
                }
                if (clickCount == 2) {
                    pathfinder.setEnd(clickedBlock);
                    clickedBlock.makeStartEnd();
                    centrePanel.applyBlockChange(row, col, clickedBlock.getState(), false);
                    updateState();
                }
            }
        });

        return tempList;
    }

    private void updateState() {
        switch (state) {
            case PRE_START:
                clickCount = 0;
                instructionsLabel.setText("Select starting and ending block...");
                state = State.INITIALISE;
                break;
            case INITIALISE:

                goToMapEditState();
                state = State.EDIT_MAP;
                break;
            case EDIT_MAP:
                if (clickCount != 2) {
                    break;
                }
                goToRunState();
                state = State.RUN;
                break;
            case RUN:
                break;
            default:
                break;
        }
    }

    private void goToMapEditState() {
        instructionsLabel.setText("Draw obstacles and then click 'Run' or press 'r'  to run the selected Pathfinder.");
        Arrays.stream(centrePanel.getMouseListeners()).forEach(centrePanel::removeMouseListener);
        Arrays.stream(centrePanel.getMouseMotionListeners()).forEach(centrePanel::removeMouseMotionListener);
        centrePanel.resetKeyboardActions();
        centrePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('r'), "EnterAction");
        centrePanel.getActionMap().put("EnterAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateState();
            }
        });

        centrePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                isDrawingWall = true;
                setBlockAsWall(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isDrawingWall = false;
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                setBlockAsWall(e);
            }
        });

        centrePanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isDrawingWall) {
                    setBlockAsWall(e);
                }
            }
        });
    }

    private void setBlockAsWall(MouseEvent e) {
        if (state != State.EDIT_MAP) {
            return;
        }
        int col = centrePanel.getColumnAtX(e.getX());
        int row = centrePanel.getRowAtY(e.getY());
        List<List<Block>> blocks = pathfinder.getBlocks();
        if (row >= 0 && row < blocks.size() && col >= 0 && col < blocks.get(row).size()) {
            Block block = blocks.get(row).get(col);
            if (block == pathfinder.getStart() || block == pathfinder.getEnd()) {
                return;
            }
            block.setWalkable(false);
            block.makeWall();
            centrePanel.applyBlockChange(row, col, block.getState(), false);
        }
    }

    /*
        LOGIC + UTILS -------------------------------------------------------------------------------------------------
     */

    private void goToRunState() {
        instructionsLabel.setText("Pathfinder is running");
        centrePanel.resetKeyboardActions();
        Arrays.stream(centrePanel.getMouseListeners()).forEach(centrePanel::removeMouseListener);
        Arrays.stream(centrePanel.getMouseMotionListeners()).forEach(centrePanel::removeMouseMotionListener);
        selectPathfinder();
        configurePathfinderCallbacks();
        pathfinder.setMoveDiagonally(allowDiagonalCheckBox.isSelected());

        algorithmThread = new Thread(pathfinder);
        algorithmThread.setName("Algorithm Thread");
        algorithmThread.start();

    }

    private void selectPathfinder() {
        switch (algorithmList.getSelectedValue()) {
            case A_STAR:
                // Default case, do nothing
                break;
            case DIJKSTRA:
                pathfinder = new DijkstraPathfinder(pathfinder);
                break;
            case DEPTH_FIRST_SEARCH:
                pathfinder = new DepthFirstSearchPathfinder(pathfinder);
                break;
            case BREADTH_FIRST_SEARCH:
                pathfinder = new BreadthFirstSearchPathfinder(pathfinder);
                break;
            case BEST_FIRST_SEARCH:
                pathfinder = new BestFirstSearchPathfinder(pathfinder);
                break;
            case WORST_FIRST_SEARCH:
                pathfinder = new WorstFirstSearchPathfinder(pathfinder);
                break;
            default:
                break;
        }
    }

    private void configurePathfinderCallbacks() {
        int callbackGridVersion = gridVersion;
        pathfinder.setGridChangeListener(event -> eventQueue.add(new QueuedGridEvent(event, callbackGridVersion)));
    }

    private void selectMaze() {
        if (state.equals(State.EDIT_MAP) || clickCount > 0) {
            return;
        }
        eventQueue.clear();
        int selectedIndex = mazeList.getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= MAZE_GENERATORS.length || grid == null) {
            return;
        }

        MAZE_GENERATORS[selectedIndex].generate(grid, event -> {});
        centrePanel.refreshColors();
    }

    /**
     * Resets the application to its initial state, clearing the grid and reinitializing the pathfinder.
     */
    public void resetApp() {
        if (pathfinder != null) {
            pathfinder.cancel();
        }
        if (algorithmThread != null && algorithmThread.isAlive()) {
            algorithmThread.interrupt();
        }
        algorithmThread = null;

        eventQueue.clear();

        Arrays.stream(centrePanel.getMouseListeners()).forEach(centrePanel::removeMouseListener);
        Arrays.stream(centrePanel.getMouseMotionListeners()).forEach(centrePanel::removeMouseMotionListener);
        centrePanel.resetKeyboardActions();
        isDrawingWall = false;
        this.centrePanel.removeAll();
        initPathfinder();
        state = State.PRE_START;
        clickCount = 0;

        updateState();

        centrePanel.revalidate();
    }

    private void runButtonClicked() {
        if (state == State.EDIT_MAP) {
            updateState();
        }
    }

    private GridConfig createDefaultGridConfig() {
        return new GridConfig(MIN_GRID_ROWS, MIN_GRID_COLUMNS, DEFAULT_CELL_SIZE);
    }

    private void applyGridSettings() {
        GridConfig newGridConfig = createGridConfigForPane((Integer) cellSizeSpinner.getValue());
        if (newGridConfig.rows == gridConfig.rows
                && newGridConfig.cols == gridConfig.cols
                && newGridConfig.cellSize == gridConfig.cellSize) {
            return;
        }
        gridConfig = newGridConfig;
        resetApp();
    }

    private void applyGridSettingsFromResize() {
        if (cellSizeSpinner == null || clickCount > 0 || state == State.RUN) {
            return;
        }
        applyGridSettings();
    }

    private void applySelectedTheme() {
        if (paletteComboBox == null) {
            return;
        }
        int selectedIndex = paletteComboBox.getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= APP_THEMES.length) {
            return;
        }
        applyTheme(APP_THEMES[selectedIndex]);
    }

    private void applyTheme(AppTheme theme) {
        BACKGROUND = theme.gridBackground;
        BLOCK_COLOR = theme.blockColor;
        BLOCK_BORDER_COLOR = theme.blockBorderColor;
        PRESSED_COLOR = theme.startEndColor;
        OBSTACLE_COLOR = theme.obstacleColor;
        PATH_COLOR = theme.pathColor;
        ACCENT_COLOR = theme.accentColor;
        NEIGHBOUR_COLOR = theme.neighbourColor;
        VISITED_COLOR = theme.visitedColor;

        if (globalPanel != null) {
            styleComponent(globalPanel, theme);
            if (leftPanel != null) {
                leftPanel.setBorder(BorderFactory.createLineBorder(theme.gridBackground, 4));
            }
            globalPanel.repaint();
        }

        if (centrePanel != null) {
            centrePanel.refreshColors();
        }
    }

    private void styleComponent(Component component, AppTheme theme) {
        if (component == globalPanel) {
            component.setBackground(theme.appBackground);
        } else if (component instanceof GridView) {
            component.setBackground(theme.gridBackground);
        } else if (component instanceof JButton button) {
            styleButton(button, theme);
        } else if (component instanceof JCheckBox checkBox) {
            styleCheckBox(checkBox, theme);
        } else if (component instanceof JList<?> list) {
            styleList(list, theme);
        } else if (component instanceof JTextArea textArea) {
            styleTextArea(textArea, theme);
        } else if (component instanceof javax.swing.text.JTextComponent textComponent) {
            textComponent.setBackground(theme.controlBackground);
            textComponent.setForeground(theme.controlText);
            textComponent.setCaretColor(theme.controlText);
        } else if (component instanceof JLabel label) {
            label.setForeground(theme.controlText);
            label.setBackground(theme.panelBackground);
        } else if (component instanceof JSlider slider) {
            slider.setBackground(theme.panelBackground);
            slider.setForeground(theme.controlText);
        } else if (component instanceof JComboBox<?> comboBox) {
            comboBox.setBackground(theme.controlBackground);
            comboBox.setForeground(theme.controlText);
        } else if (component instanceof JSpinner spinner) {
            spinner.setBackground(theme.controlBackground);
            spinner.setForeground(theme.controlText);
            JComponent editor = spinner.getEditor();
            editor.setBackground(theme.controlBackground);
            editor.setForeground(theme.controlText);
            styleComponent(editor, theme);
        } else if (component instanceof JPanel panel) {
            panel.setBackground(theme.panelBackground);
        }

        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                styleComponent(child, theme);
            }
        }
    }

    private void styleButton(JButton button, AppTheme theme) {
        button.setBackground(theme.controlBackground);
        button.setForeground(theme.controlText);
        button.setFocusPainted(false);
    }

    private void styleCheckBox(JCheckBox checkBox, AppTheme theme) {
        checkBox.setBackground(theme.controlBackground);
        checkBox.setForeground(theme.controlText);
        checkBox.setFocusPainted(false);
    }

    private void styleList(JList<?> list, AppTheme theme) {
        Border emptyBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        Border innerBorder = BorderFactory.createLineBorder(theme.controlText, 4);
        list.setBorder(BorderFactory.createCompoundBorder(emptyBorder, innerBorder));
        list.setBackground(theme.panelBackground);
        list.setForeground(theme.mutedText);
        list.setSelectionBackground(theme.listSelection);
        list.setSelectionForeground(theme.controlText);
    }

    private void styleTextArea(JTextArea textArea, AppTheme theme) {
        textArea.setBackground(theme.panelBackground);
        textArea.setForeground(theme.controlText);
    }

    private GridConfig createGridConfigForPane(int cellSize) {
        if (centrePanel == null) {
            return new GridConfig(MIN_GRID_ROWS, MIN_GRID_COLUMNS, cellSize);
        }

        Insets insets = centrePanel.getInsets();
        int usableWidth = centrePanel.getWidth() - insets.left - insets.right;
        int usableHeight = centrePanel.getHeight() - insets.top - insets.bottom;

        if (usableWidth <= 0 || usableHeight <= 0) {
            return new GridConfig(gridConfig.rows, gridConfig.cols, cellSize);
        }

        int rows = Math.max(MIN_GRID_ROWS, usableHeight / cellSize);
        int cols = Math.max(MIN_GRID_COLUMNS, usableWidth / cellSize);
        return new GridConfig(rows, cols, cellSize);
    }

    enum State {
        PRE_START,
        INITIALISE,
        EDIT_MAP,
        RUN
    }
}
