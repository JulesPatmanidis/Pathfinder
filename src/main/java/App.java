import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class App {
    /** CONSTANTS **/
    private static final String FRAME_TITLE = "Pathfinder";
    public static final int BLOCK_NUMBER = 50; // Keep below 150
    private static final int BUTTON_DIM = 3;    //15

    //private static final Dimension DIMENSION = new Dimension(BLOCK_NUMBER * BUTTON_DIM,BLOCK_NUMBER * BUTTON_DIM);
    private static final int ROUTE_PAINT_DELAY = 8;
    private static final Border MAIN_BORDER = BorderFactory.createEmptyBorder(20,20,20,20);
    private static final Border TOP_BORDER = BorderFactory.createEmptyBorder(10,23,10,23);
    private static final String[] ALGORITHMS = {"A-Star","Dijkstra", "BestFirstSearch", "DepthFirstSearch", "BreadthFirstSearch"};

    public static final Color BACKGROUND = Color.black;
    public static final Color BLOCK_COLOR = new Color(105,105,105);
    public static final Color BLOCK_BORDER_COLOR = Color.BLACK;
    public static final Color TEXT_COLOR = new Color(211, 211, 211);
    public static final Color PRESSED_COLOR = new Color(255, 0, 0);
    public static final Color OBSTACLE_COLOR = new Color(255, 255, 255);
    public static final Color PATH_COLOR = new Color(229, 61, 61);
    public static final Color ACCENT_COLOR = new Color(2, 76, 71);
    public static final Color PANEL_COLOUR = new Color(39, 39, 39);
    public static final Color CHECKED_COLOR = ACCENT_COLOR;
    public static final Color RESET_COLOR = ACCENT_COLOR;

    enum State {
        PRE_START,
        INITIALISE,
        EDIT_MAP,
        RUN
    }

    private JPanel panel1;
    private JPanel topPanel;
    private JPanel leftPanel;
    private JPanel centrePanel;
    private JButton resetButton;
    private JButton runButton;
    private JList<String> algorithmList;
    private JLabel topPanelLabel;
    private JCheckBox allowDiagonalCheckBox;

    private int clickCount = 0;
    private Pathfinder pathfinder;
    private State state;

    public App() {
        createUIComponents();
        initPathfinder();
    }
    private void createUIComponents() {


        BorderLayout topLayout = new BorderLayout();
        topLayout.setVgap(5);
        topPanel.setLayout(topLayout);
        topPanel.setBorder(TOP_BORDER);
        topPanel.setBackground(PANEL_COLOUR);

        topPanelLabel.setText("Select starting and ending block...");
        topPanelLabel.setOpaque(true);
        topPanelLabel.setBackground(PANEL_COLOUR);
        topPanelLabel.setForeground(TEXT_COLOR);
        topPanel.add(topPanelLabel, BorderLayout.WEST);

        runButton.setText("Run");
        runButton.setBackground(RESET_COLOR);
        runButton.setForeground(TEXT_COLOR);
        runButton.addActionListener(x -> runButtonClicked());

        topPanel.add(resetButton, BorderLayout.EAST);
        topPanel.setBackground(PANEL_COLOUR);

        resetButton.setText("Reset");
        resetButton.setBackground(RESET_COLOR);
        resetButton.setForeground(TEXT_COLOR);
        resetButton.addActionListener(x -> resetApp());
        topPanel.add(resetButton, BorderLayout.EAST);

        algorithmList.setBorder(BorderFactory.createLineBorder(new Color(184, 184, 184), 2));
        algorithmList.setListData(ALGORITHMS);
        DefaultListCellRenderer renderer = (DefaultListCellRenderer) algorithmList.getCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);

        algorithmList.setSelectedIndex(0);
        algorithmList.setForeground(new Color(184, 184, 184));
        algorithmList.setBackground(PANEL_COLOUR);
        algorithmList.setSelectionBackground(PATH_COLOR);

        allowDiagonalCheckBox.setText("Allow Diagonal movement");
        allowDiagonalCheckBox.setBackground(RESET_COLOR);
        allowDiagonalCheckBox.setForeground(TEXT_COLOR);
        allowDiagonalCheckBox.setSelected(true);

        centrePanel.setLayout(new GridLayout(BLOCK_NUMBER, BLOCK_NUMBER, 0 ,0));
        centrePanel.setBorder(MAIN_BORDER);
        centrePanel.setBackground(BACKGROUND);


        // Disable button-press with spacebar
        InputMap im = (InputMap)UIManager.get("Button.focusInputMap");
        im.put(KeyStroke.getKeyStroke("pressed SPACE"), "none");
        im.put(KeyStroke.getKeyStroke("released SPACE"), "none");

        state = State.INITIALISE;

    }



    // CHANGE LATER
    private void initPathfinder() {
        this.pathfinder = new AStarPathfinder();
        pathfinder.setBlocksList(createBlockGrid());
        pathfinder.setMoveDiagonally(allowDiagonalCheckBox.isSelected());
        //System.out.println("SelectPathfinder");
        //System.out.println(pathfinder.toString());
    }

    /**
     * Adds JButtons to the mainPanel
     */
    private List<List<Block>>  createBlockGrid() {
        int panelAspectRatio = centrePanel.getMaximumSize().width / centrePanel.getMaximumSize().height;
        List<List<Block>> tempList = new ArrayList<>();
        for (int i = 0; i < BLOCK_NUMBER; i++) {
            tempList.add(i, new ArrayList<>());
            for (int j = 0; j < BLOCK_NUMBER * panelAspectRatio; j++) {
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

        Dimension buttonDimension = new Dimension(
                centrePanel.getMaximumSize().width / BLOCK_NUMBER,
                centrePanel.getMaximumSize().height / BLOCK_NUMBER
        );
        block.getButton().setSize(buttonDimension);

        //block.getButton().setPreferredSize(buttonDimension);
        block.getButton().setBorder(BorderFactory.createLineBorder(BLOCK_BORDER_COLOR));
        String text = "" + block.getNode().getRow() + ", " + block.getNode().getColumn();
//        block.getButton().setText(text);
//        block.getButton().setForeground(Color.WHITE);
        block.getButton().addActionListener(e -> {
            block.getButton().setBackground(PRESSED_COLOR);

            clickCount++;
            if (clickCount == 1) {
                pathfinder.setStart(block);
                System.out.println("Start is set");
                System.out.println(pathfinder.getStart().toString());
            }
            if (clickCount == 2) {
                pathfinder.setEnd(block);
                updateState();
                //goToClickState();
                System.out.println("End is set");
            }
        });
    }

    private void updateState() {
        switch (state) {
            case PRE_START:
                clickCount = 0;
                topPanelLabel.setText("Select starting and ending block...");
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
        topPanelLabel.setText("Draw obstacles and then click 'Run' or press 'r'  to run the selected Pathfinder.");
        centrePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('r'), "EnterAction");
        centrePanel.getActionMap().put("EnterAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateState();
            }
        });


        // Buttons become paint-able by click-dragging over them
        pathfinder.getBlocks().stream().flatMap(List::stream).forEach(this::updateBlockClick);
    }

    private void goToRunState() {

        topPanelLabel.setText("Pathfinder is running");
        centrePanel.resetKeyboardActions();
        pathfinder.getBlocks().stream().flatMap(List::stream).forEach(this::updateBlockFinal);
        selectPathfinder();
        pathfinder.setMoveDiagonally(allowDiagonalCheckBox.isSelected());

        Thread algorithmThread = new Thread(pathfinder);
        algorithmThread.setName("Algorithm Thread");
        algorithmThread.start();

    }

    private void selectPathfinder() {
        switch (algorithmList.getSelectedValue()) {
            case "A-Star":
                System.out.println("A-star");
                // Default case do nothing
                break;
            case "Dijkstra":
                pathfinder = new DijkstraPathfinder(pathfinder);
                //System.out.println("Dijkstra");
                break;
            case "DepthFirstSearch":
                pathfinder = new DepthFirstSearchPathfinder(pathfinder);
                //System.out.println("DFS");
                break;
            case "BreadthFirstSearch":
                pathfinder = new BreadthFirstSearchPathfinder(pathfinder);
                //System.out.println("BreadthFS");
                break;
            case "BestFirstSearch":
                pathfinder = new BestFirstSearchPathfinder(pathfinder);
                //System.out.println("BestFS");
                break;
            default:
                break;
        }
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
            public void mousePressed(MouseEvent e) {        //Block set to be obstacle
                if (e.getSource() instanceof JButton) {
                    block.getButton().setBackground(OBSTACLE_COLOR);
                    block.getNode().setWalkable(false);
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
                    block.getNode().setWalkable(false);
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

    private void clearListeners(JButton block) {
        for (ActionListener a : block.getActionListeners()) {
            block.removeActionListener(a);
        }
        for (MouseListener a : block.getMouseListeners()) {
            block.removeMouseListener(a);
        }
    }

    public double getAspectRatio() {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        //System.out.println(dim.getWidth());
        return dim.getWidth()/dim.getHeight();
    }

    public static void paintRoute(List<Block> blocks) {
        //System.out.println(Thread.currentThread().getName() + " paintRoute");
        for (Block block : blocks) {

            try {
                TimeUnit.MILLISECONDS.sleep(ROUTE_PAINT_DELAY);
                SwingUtilities.invokeAndWait(() -> block.getButton().setBackground(PATH_COLOR));
            } catch (InterruptedException e) {
                System.err.println("Error: Thread was interrupted");
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                System.err.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }

        try {
            TimeUnit.MILLISECONDS.sleep(ROUTE_PAINT_DELAY);
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

    public static void paintBlock(Block block, Color color) {
        block.getButton().setBackground(color);
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
        frame.setMaximumSize(Toolkit.getDefaultToolkit().getScreenSize());
        frame.setContentPane(new App().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
