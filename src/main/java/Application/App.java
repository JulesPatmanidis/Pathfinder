package Application;

import Pathfinders.*;
import Utilities.Utils;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class App {
    /** CONSTANTS **/
    private static final String FRAME_TITLE = "Pathfinder";
    public static final int BLOCK_NUMBER = 80; //60 // Keep below 150
    public static final int ROUTE_PAINT_DELAY = 8; // ms
    private static final Border MAIN_BORDER = BorderFactory.createEmptyBorder(20,20,20,20);
    private static final Border TOP_BORDER = BorderFactory.createEmptyBorder(10,23,10,23);

    public static final String A_STAR = "A-Star";
    public static final String DIJKSTRA = "Dijkstra";
    public static final String BEST_FIRST_SEARCH = "BestFirstSearch";
    public static final String BREADTH_FIRST_SEARCH = "BreadthFirstSearch";
    public static final String DEPTH_FIRST_SEARCH = "DepthFirstSearch";
    public static final String WORST_FIRST_SEARCH = "WorstFirstSearch";
    public static final String RANDOMISED_PRIMS = "Randomised DFS";
    public static final String RANDOMIZED_DFS = "Randomised Prim's";

    private static final String[] ALGORITHMS =
            {A_STAR, DIJKSTRA, BEST_FIRST_SEARCH, BREADTH_FIRST_SEARCH, DEPTH_FIRST_SEARCH, WORST_FIRST_SEARCH,};
    private static final String[] MAZES =
            {RANDOMIZED_DFS, RANDOMISED_PRIMS};

    public static final Color BACKGROUND = new Color(0, 0, 0);
    public static final Color BLOCK_COLOR = new Color(139, 139, 144);
    public static final Color BLOCK_BORDER_COLOR = new Color(40, 40, 40);
    public static final Color TEXT_COLOR = new Color(211, 211, 211);
    public static final Color PRESSED_COLOR = new Color(139, 0, 0);
    public static final Color OBSTACLE_COLOR = new Color(40, 40, 40);
    public static final Color PATH_COLOR = new Color(229, 61, 61);
    public static final Color ACCENT_COLOR = new Color(2, 76, 71);
    public static final Color PANEL_COLOUR = new Color(39, 39, 39);
    public static final Color CHECKED_COLOR = ACCENT_COLOR;
    //public static final Color RESET_COLOR = ACCENT_COLOR;
    public static final Font GLOBAL_FONT = new Font("Times New Roman", Font.PLAIN, 20);

    enum State {
        PRE_START,
        INITIALISE,
        EDIT_MAP,
        RUN
    }
    private static final String[] algorithmInfo = new String[ALGORITHMS.length];
    private JPanel globalPanel;
    private JPanel bottomPanel;
    private JPanel leftPanel;
    private JPanel centrePanel;
    private JButton resetButton;
    private JButton runButton;
    private JList<String> algorithmList;
    private JLabel instructionsLabel;
    private JCheckBox allowDiagonalCheckBox;
    private JCheckBox fadeCheckBox;
    private JSlider delaySlider;
    private JLabel sliderLabel;
    private JLabel sliderValueLabel;
    private JTextArea infoTextArea;
    private JTextArea infoTitleTextArea;
    private JList<String> mazeList;
    private JTextArea mazeTitleTextArea;
    private JTextArea mazeInfoTextArea;

    private int clickCount = 0;
    private Pathfinder pathfinder;
    private State state;
    public static boolean isFadeChecked = true;
    public static int paintDelay = 50;
    public static boolean allowDiagonal = true;

    public App() {
        System.out.println("App: " + Thread.currentThread());
        Utils.parseAlgorithmInfo(algorithmInfo);
        long startTime = System.nanoTime();
        createUIComponents();
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);  //divide by 1000000 to get milliseconds.
        System.out.println("createUIComponents(): " + Math.floor(duration / 1000000f));

        startTime = System.nanoTime();
        initPathfinder();
        endTime = System.nanoTime();
        duration = (endTime - startTime);  //divide by 1000000 to get milliseconds.
        System.out.println("initPathfinder(): " + Math.floor(duration / 1000000f));
    }


    private void createUIComponents() {
        // GLOBAL PANEL --------------------------------------------------
        globalPanel = new JPanel();
        globalPanel.setLayout(new BorderLayout(0,0));

        // TOP PANEL

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.setBackground(PANEL_COLOUR);

        JLabel topLabel = new JLabel("PATHFINDER");
        topLabel.setFont(new Font("Times New Roman", Font.BOLD, 40));
        topLabel.setForeground(TEXT_COLOR);
        topLabel.setBorder(BorderFactory.createEmptyBorder(0,10,0,0));
        topPanel.add(topLabel, BorderLayout.CENTER);

        globalPanel.add(topPanel, BorderLayout.NORTH);
        // BOTTOM PANEL -----------------------------------------------------

        bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        bottomPanel.setAlignmentX(1f);
        bottomPanel.setBorder(TOP_BORDER);
        bottomPanel.setBackground(PANEL_COLOUR);
        globalPanel.add(bottomPanel, BorderLayout.SOUTH);

        instructionsLabel = new JLabel();
        instructionsLabel.setText("Select starting and ending block...");
        instructionsLabel.setOpaque(true);
        instructionsLabel.setBackground(PANEL_COLOUR);
        instructionsLabel.setForeground(TEXT_COLOR);
        instructionsLabel.setFont(GLOBAL_FONT);
        bottomPanel.add(instructionsLabel);

        bottomPanel.add(Box.createHorizontalGlue());
        //bottomPanel.add(Box.createRigidArea(new Dimension(10,0)));

        sliderLabel = new JLabel("Delay (ms):");
        sliderLabel.setFont(GLOBAL_FONT);
        sliderLabel.setBackground(PANEL_COLOUR);
        sliderLabel.setForeground(TEXT_COLOR);
        bottomPanel.add(sliderLabel);
        bottomPanel.add(Box.createRigidArea(new Dimension(10,0)));

        sliderValueLabel = new JLabel();
        sliderValueLabel.setFont(GLOBAL_FONT);
        sliderValueLabel.setBackground(PANEL_COLOUR);
        sliderValueLabel.setForeground(TEXT_COLOR);
        sliderValueLabel.setPreferredSize(new Dimension(70,40));
        sliderValueLabel.setMinimumSize(new Dimension(70,40));

        bottomPanel.add(sliderValueLabel);

        //bottomPanel.add(Box.createRigidArea(new Dimension(10,0)));

        delaySlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
        delaySlider.setSnapToTicks(false);
        delaySlider.setFont(GLOBAL_FONT);
        delaySlider.setBackground(BLOCK_BORDER_COLOR);
        delaySlider.setForeground(TEXT_COLOR);
        delaySlider.setMaximumSize(new Dimension(150,35));
        delaySlider.setToolTipText("Delay");

        delaySlider.addChangeListener(changeEvent -> {
            paintDelay = delaySlider.getValue();
            sliderValueLabel.setText(String.valueOf(delaySlider.getValue()));
        });
        sliderValueLabel.setText(String.valueOf(delaySlider.getValue()));
        bottomPanel.add(delaySlider);
        bottomPanel.add(Box.createRigidArea(new Dimension(10,0)));

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
        bottomPanel.add(allowDiagonalCheckBox);

        bottomPanel.add(Box.createRigidArea(new Dimension(10,0)));

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
        bottomPanel.add(fadeCheckBox);

        bottomPanel.add(Box.createRigidArea(new Dimension(10,0)));

        resetButton = new JButton();
        resetButton.setText("Reset");
        resetButton.setBackground(ACCENT_COLOR);
        resetButton.setForeground(TEXT_COLOR);
        resetButton.setFocusPainted(false);
        resetButton.addActionListener(x -> resetApp());
        resetButton.setFont(GLOBAL_FONT);
        allowDiagonalCheckBox.setBorder(resetButton.getBorder());

        bottomPanel.add(resetButton);

        bottomPanel.add(Box.createRigidArea(new Dimension(10,0)));

        runButton = new JButton();
        runButton.setText("Run");
        runButton.setBackground(ACCENT_COLOR);
        runButton.setForeground(TEXT_COLOR);
        runButton.setFocusPainted(false);
        runButton.addActionListener(x -> runButtonClicked());
        runButton.setFont(GLOBAL_FONT);
        bottomPanel.add(runButton);


        // CENTRE PANEL -----------------------------------------------------
        centrePanel = new JPanel();
        centrePanel.setLayout(new GridLayout(BLOCK_NUMBER, (int) (BLOCK_NUMBER * Utils.getAspectRatio()), 0 ,0));
        System.out.println(BLOCK_NUMBER * Utils.getAspectRatio());
        centrePanel.setBorder(MAIN_BORDER);
        centrePanel.setBackground(BACKGROUND);
        globalPanel.add(centrePanel, BorderLayout.CENTER);

        // LEFT PANEL -----------------------------------------------------
        int leftPanelWidth = 300;
        Border emptyBorder10 = BorderFactory.createEmptyBorder(10,10,10,10);
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
            int selected  = list.getSelectedIndex();
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
        mazeList.setListData(MAZES);
        DefaultListCellRenderer mazeListRenderer = (DefaultListCellRenderer) mazeList.getCellRenderer();
        mazeListRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        //mazeList.setMaximumSize(mazeList.getSize());

        mazeList.setSelectedIndex(0);
        mazeList.setForeground(new Color(184, 184, 184));
        mazeList.setBackground(PANEL_COLOUR);
        mazeList.setSelectionBackground(PATH_COLOR);
        mazeList.setFont(GLOBAL_FONT);
        mazeList.addListSelectionListener(e -> {
            JList<String> list = (JList<String>) e.getSource();
            int selected  = list.getSelectedIndex();
            mazeInfoTextArea.setText(algorithmInfo[selected]);
        });

        leftPanel.add(mazeList);

        mazeInfoTextArea = new JTextArea();
        mazeInfoTextArea.setAlignmentX(0f);
        mazeInfoTextArea.setPreferredSize(new Dimension(leftPanelWidth, 260));
        mazeInfoTextArea.setMaximumSize(mazeInfoTextArea.getPreferredSize());
        mazeInfoTextArea.setBorder(emptyBorder10);
        mazeInfoTextArea.setBackground(PANEL_COLOUR);
        mazeInfoTextArea.setForeground(TEXT_COLOR);
        mazeInfoTextArea.setLineWrap(true);
        mazeInfoTextArea.setWrapStyleWord(true);
        mazeInfoTextArea.setEditable(false);
        mazeInfoTextArea.setText(algorithmInfo[0]);
        mazeInfoTextArea.setFont(GLOBAL_FONT);
        //leftPanel.add(mazeInfoTextArea);

        // GENERAL ------------------------------------------------------------------

        // Disable button-press with space-bar
        InputMap im = (InputMap)UIManager.get("Button.focusInputMap");
        im.put(KeyStroke.getKeyStroke("pressed SPACE"), "none");
        im.put(KeyStroke.getKeyStroke("released SPACE"), "none");
        state = State.INITIALISE;

    }

    /*
        INITIALISATION-------------------------------------------------------------------------------------------------
     */

    // CHANGE LATER
    private void initPathfinder() {
        this.pathfinder = new AStarPathfinder();
        pathfinder.setBlocksList(createBlockGrid());
        pathfinder.setMoveDiagonally(allowDiagonalCheckBox.isSelected());
        pathfinder.setVariableDelay(delaySlider.getValue());

        globalPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke('m'), "mAction");
        globalPanel.getActionMap().put("mAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("actionPerformed: " + Thread.currentThread());
                selectMaze();
            }
        });
    }

    /**
     * Adds JButtons to the mainPanel
     */
    private List<List<Block>>  createBlockGrid() {
        double panelAspectRatio = Utils.getAspectRatio();
        List<List<Block>> tempList = new ArrayList<>();
        for (int i = 0; i < BLOCK_NUMBER ; i++) {
            tempList.add(i, new ArrayList<>());
            for (int j = 0; j < (int) (BLOCK_NUMBER * panelAspectRatio); j++) {
                Block current = new Block(i, j);
                initializeBlockButton(current);
                tempList.get(i).add(j, current);
                centrePanel.add(current.getButton());
                //System.out.printf("Added %d, %d\n", i, j);
            }
        }
        return tempList;
        //pathfinder.setBlocksList(tempList);
    }

    /**
     * Initializes the JButton on the blocks array.
     * @param block block
     */
    public void initializeBlockButton(Block block) {
        block.getButton().setBackground(BLOCK_COLOR);

        int offset = Toolkit.getDefaultToolkit().getScreenSize().height - 200;
        int buttonSize = (int) Math.floor(offset / (double) BLOCK_NUMBER);
        block.getButton().setPreferredSize(new Dimension(buttonSize,buttonSize));

        block.getButton().setBorder(BorderFactory.createMatteBorder(1,0,0,1, BLOCK_BORDER_COLOR));
        String text = "" + block.getRow() + ", " + block.getColumn();
        //block.getButton().setText(text);
        block.getButton().addActionListener(e -> {
            block.getButton().setBackground(PRESSED_COLOR);

            clickCount++;
            if (clickCount == 1) {
                pathfinder.setStart(block);
            }
            if (clickCount == 2) {
                pathfinder.setEnd(block);
                updateState();
            }
        });
    }

    /*
        STATE --------------------------------------------------------------------------------------------------------
     */

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
                System.out.println("State cannot be updated from here (RUN)");
                break;
            default:
                System.out.println("State is invalid");
                break;
        }
    }

    private void goToMapEditState() {
        instructionsLabel.setText("Draw obstacles and then click 'Run' or press 'r'  to run the selected Pathfinder.");
        centrePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('r'), "EnterAction");
        centrePanel.getActionMap().put("EnterAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateState();
            }
        });
        pathfinder.getBlocks().stream().flatMap(List::stream).forEach(this::updateBlockClick);
    }

    private void goToRunState() {
        instructionsLabel.setText("Pathfinder is running");
        centrePanel.resetKeyboardActions();
        pathfinder.getBlocks().stream().flatMap(List::stream).forEach(this::updateBlockFinal);
        selectPathfinder();
        pathfinder.setMoveDiagonally(allowDiagonalCheckBox.isSelected());

        Thread algorithmThread = new Thread(pathfinder);
        algorithmThread.setName("Algorithm Thread");
        algorithmThread.start();

    }

    private void updateBlockFinal(Block block) {
        clearListeners(block.getButton());
    }

    private void goToHoverState() {
        pathfinder.getBlocks().stream().flatMap(List::stream).forEach(this::updateBlockHover);
    }

    private void updateBlockClick(Block block) {
        clearListeners(block.getButton());
        block.getButton().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {        //Application.Block set to be obstacle
                if (e.getSource() instanceof JButton) {
                    block.getButton().setBackground(OBSTACLE_COLOR);
                    block.setWalkable(false);
                    goToHoverState();
                }
            }
        });
    }

    private void updateBlockHover(Block block) {
        clearListeners(block.getButton());
        block.getButton().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (e.getSource() instanceof JButton) {
                    block.getButton().setBackground(OBSTACLE_COLOR);
                    block.setWalkable(false);
                }
            }
        });
        block.getButton().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                goToMapEditState();
            }
        });
    }

    /*
        LOGIC + UTILS -------------------------------------------------------------------------------------------------
     */

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

    private void selectMaze() {
        if (state.equals(State.EDIT_MAP) || clickCount > 0) {
            return;
        }
        switch (mazeList.getSelectedValue()) {
            case RANDOMIZED_DFS:
                pathfinder.createDFSMaze();
                break;
            case RANDOMISED_PRIMS:
                pathfinder.createPrimsMaze();
                break;
            default:
                break;
        }
    }

    private void clearListeners(JButton block) {
        Arrays.stream(block.getActionListeners()).forEach(block::removeActionListener);
        Arrays.stream(block.getMouseListeners()).forEach(block::removeMouseListener);
    }

    public static void paintRoute(List<Block> blocks) {
        //System.out.println(Thread.currentThread().getName() + " paintRoute");
        for (Block block : blocks) {
            block.getButton().paintPath();
        }

        try {
            //TimeUnit.MILLISECONDS.sleep(ROUTE_PAINT_DELAY);
            SwingUtilities.invokeAndWait(() -> {
                blocks.get(0).getButton().setBackground(PRESSED_COLOR);
                blocks.get(blocks.size() - 1).getButton().setBackground(PRESSED_COLOR);
            });
        } catch (InterruptedException e) {
            System.err.println("Error: Thread was interrupted");
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void resetApp() {
        this.centrePanel.removeAll();
        initPathfinder();
        state = State.PRE_START;

        updateState();

        centrePanel.revalidate();
        centrePanel.repaint();
    }

    private void runButtonClicked() {
        if (state == State.EDIT_MAP) {
            updateState();
        }
    }

    public static void main(String[] args) {

        JFrame frame = new JFrame(FRAME_TITLE);
        frame.setResizable(false);
        frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        frame.setContentPane(new App().globalPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        long start = System.nanoTime();
        frame.pack();
        long end = System.nanoTime();
        frame.setVisible(true);
        long end2 = System.nanoTime();
        System.out.println("pack(): " + (end - start) / 1000000f + "ms");
        System.out.println("setVisible(): " + (end2 - start - (end - start)) / 1000000f + "ms");
        System.out.println(Thread.currentThread());
    }
}
