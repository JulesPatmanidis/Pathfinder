import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Gui extends JFrame {

    /** CONSTANTS **/
    private static final String FRAME_TITLE = "Pathfinder";
    private static final int BLOCK_NUMBER = 50; //50
    private static final int BUTTON_DIM = 18;    //15
    //private static final Dimension DIMENSION = new Dimension(BLOCK_NUMBER * BUTTON_DIM,BLOCK_NUMBER * BUTTON_DIM);
    private static final int ROUTE_PAINT_DELAY = 8;
    private static final Border MAIN_BORDER = BorderFactory.createEmptyBorder(0,20,20,20);
    private static final Border TOP_BORDER = BorderFactory.createEmptyBorder(10,23,0,23);

    public static final Color BACKGROUND = Color.black;
    public static final Color BLOCK_COLOR = new Color(105,105,105);
    public static final Color BLOCK_BORDER_COLOR = Color.BLACK;
    public static final Color TEXT_COLOR = new Color(211, 211, 211);
    public static final Color PRESSED_COLOR = new Color(83, 0, 2);
    public static final Color OBSTACLE_COLOR = new Color(255, 255, 255);
    public static final Color PATH_COLOR = new Color(255, 97, 48);
    public static final Color ACCENT_COLOR = new Color(0, 83, 78);
    public static final Color CHECKED_COLOR = ACCENT_COLOR;
    public static final Color RESET_COLOR = ACCENT_COLOR;


    /** INSTANCE VARIABLES **/
    private JLabel text;
    private JPanel mainPanel;
    private JPanel topPanel;
    private int clickCount = 0;
    private JButton resetButton;
    private Dimension dimension = new Dimension(BLOCK_NUMBER * BUTTON_DIM,(int) (BLOCK_NUMBER * BUTTON_DIM * getAspectRatio()));

    private Pathfinder pathfinder;
    //private boolean analyticView;


    /**
     * Constructor
     */
    public Gui(){
        super(FRAME_TITLE);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBounds(new Rectangle(dimension));
        setLayout(new BorderLayout());
        setBackground(BACKGROUND);
        setResizable(true);

//        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//        setSize(screenSize.width, screenSize.height);
        setExtendedState(MAXIMIZED_BOTH);


        topPanel = new JPanel();
        BorderLayout topLayout = new BorderLayout();
        topLayout.setVgap(5);
        topPanel.setLayout(topLayout);
        topPanel.setBorder(TOP_BORDER);
        topPanel.setBackground(BACKGROUND);

        add(topPanel, BorderLayout.NORTH);

        text = new JLabel("Select starting and ending block...");
        text.setOpaque(true);
        text.setBackground(BACKGROUND);
        text.setForeground(TEXT_COLOR);
        topPanel.add(text, BorderLayout.WEST);

        JPanel linePanel = new JPanel();
        linePanel.setBackground(ACCENT_COLOR);
        //topPanel.add(linePanel, BorderLayout.SOUTH);

        resetButton = new JButton("Reset");
        resetButton.setBackground(RESET_COLOR);
        resetButton.setForeground(TEXT_COLOR);
        resetButton.addActionListener(e -> resetGUI());
        topPanel.add(resetButton, BorderLayout.EAST);


        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(BLOCK_NUMBER, (int) Math.round(BLOCK_NUMBER * getAspectRatio()), 0 ,0));
        mainPanel.setBorder(MAIN_BORDER);
        mainPanel.setBackground(BACKGROUND);
        add(mainPanel, BorderLayout.CENTER);

        selectPathfinder();

    }

    /**
     * Adds JButtons to the mainPanel
     */
    private void addBlocks() {
        List<List<Block>> tempList = new ArrayList<>();
        for (int i = 0; i < BLOCK_NUMBER; i++) {
            tempList.add(i, new ArrayList<>());
            for (int j = 0; j < BLOCK_NUMBER * getAspectRatio(); j++) {
                Block current = new Block(i, j);
                initializeBlockButton(current);
                tempList.get(i).add(j, current);
                mainPanel.add(current.getButton());
            }
        }
        pathfinder.setBlocksList(tempList);
    }

    // CHANGE LATER
    private void selectPathfinder() {
        this.pathfinder = new AStarPathfinder();
        System.out.println("SelectPathfinder");
        System.out.println(pathfinder.toString());
        addBlocks();
    }

    public Pathfinder getPathfinder() {
        return pathfinder;
    }

    public void setPathfinder(Pathfinder pathfinder) {
        this.pathfinder = pathfinder;
    }


//    public Block getEnd() {
//        return end;
//    }


    public Dimension getDimension() {
        return dimension;
    }
    /**
     * Initializes the JButton on the blocks array.
     * @param block
     */
    public void initializeBlockButton(Block block) {
        block.getButton().setBackground(BLOCK_COLOR);
        block.getButton().setPreferredSize(new Dimension(BUTTON_DIM, BUTTON_DIM));
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
                goToClickState();
                System.out.println("End is set");
            }
        });
    }


    public <T extends JComponent> void addToMainPanel(T t) {
        mainPanel.add(t);
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


    private void goToClickState() {
        text.setText("Draw obstacles and then press 'Enter' for instant view or 'Space' for analytic view.");
        mainPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke('\n'), "EnterAction");
        mainPanel.getActionMap().put("EnterAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goToFinalState();
            }
        });

        mainPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(' '), "SpaceAction");
        mainPanel.getActionMap().put("SpaceAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goToFinalState();
            }
        });

        pathfinder.getBlocks().stream().flatMap(List::stream).forEach(this::updateBlockClick);
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
                goToClickState();
            }
        });
    }

    private void goToHoverState() {
        pathfinder.getBlocks().stream().flatMap(List::stream).forEach(this::updateBlockHover);
    }

    private void updateBlockFinal(Block block) {
        clearListeners(block.getButton());
    }

    private void goToFinalState() {

        text.setText("Pathfinder is running");
        mainPanel.resetKeyboardActions();
        pathfinder.getBlocks().stream().flatMap(List::stream).forEach(this::updateBlockFinal);

        Thread algorithmThread = new Thread(pathfinder);
        algorithmThread.setName("Algorithm Thread");
        algorithmThread.start();

    }

    private void clearListeners(JButton block) {
        for (ActionListener a : block.getActionListeners()) {
            block.removeActionListener(a);
        }
        for (MouseListener a : block.getMouseListeners()) {
            block.removeMouseListener(a);
        }
    }

    public int calcBlockDim(int num) {
        return 1080/num;
    }

    public static void paintRoute(List<Block> blocks) {
        System.out.println(Thread.currentThread().getName() + " paintRoute");
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


    public void resetGUI() {
        Main.run(this);
    }

    public double getAspectRatio() {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        //System.out.println(dim.getWidth());
        return dim.getWidth()/dim.getHeight();
    }

    public static void paintBlock(Block block, Color color) {
        block.getButton().setBackground(color);
    }


}
