import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.Timer;
import javax.swing.*;

class Shape {
    Tetrominoe pieceShape;
    int coords[][];
    int[][][] coordsTable;

    public Shape() {
        initShape();
    }
   
    void initShape() {
        coords = new int[4][2];
        setShape(Tetrominoe.NoShape);
    }

    protected void setShape(Tetrominoe shape) {
        coordsTable = new int[][][] {
            { { 0, 0 },   { 0, 0 },   { 0, 0 },   { 0, 0 } },
            { { 0, -1 },  { 0, 0 },   { -1, 0 },  { -1, 1 } },
            { { 0, -1 },  { 0, 0 },   { 1, 0 },   { 1, 1 } },
            { { 0, -1 },  { 0, 0 },   { 0, 1 },   { 0, 2 } },
            { { -1, 0 },  { 0, 0 },   { 1, 0 },   { 0, 1 } },
            { { 0, 0 },   { 1, 0 },   { 0, 1 },   { 1, 1 } },
            { { -1, -1 }, { 0, -1 },  { 0, 0 },   { 0, 1 } },
            { { 1, -1 },  { 0, -1 },  { 0, 0 },   { 0, 1 } }
        };

        for (int i = 0; i < 4 ; i++) {
            for (int j = 0; j < 2; ++j) {
                coords[i][j] = coordsTable[shape.ordinal()][i][j];
            }
        }
       
        pieceShape = shape;
    }

    public void setRandomShape() {
        Random r = new Random();
        int x = Math.abs(r.nextInt()) % 7 + 1;
        Tetrominoe[] values = Tetrominoe.values();
        setShape(values[x]);
    }

    public Tetrominoe getShape() {
        return pieceShape;
    }
}

enum Tetrominoe { NoShape, ZShape, SShape, LineShape, TShape, SquareShape, LShape, MirroredLShape }

class Board extends JPanel {
    final int BOARD_WIDTH = 10;
    final int BOARD_HEIGHT = 22;
    final int INITIAL_DELAY = 100;
    final int PERIOD_INTERVAL = 300;

    Timer timer;
    boolean isFallingFinished = false;
    boolean isStarted = false;
    boolean isPaused = false;
    int numLinesRemoved = 0;
    int curX = 0;
    int curY = 0;
    JLabel statusbar;
    Shape curPiece;
    Tetrominoe[] board;

    public Board(Tetris parent) {
        setFocusable(true);
        setBorder(BorderFactory.createLineBorder(Color.pink, 4));
        timer = new Timer();
        timer.scheduleAtFixedRate(new ScheduleTask(), INITIAL_DELAY, PERIOD_INTERVAL);

        curPiece = new Shape();
        statusbar = parent.getStatusBar();  // ✅ Fix 1: getStatusBar() method is now included in Tetris class
        board = new Tetrominoe[BOARD_WIDTH * BOARD_HEIGHT];
        addKeyListener(new TAdapter());
        clearBoard();
    }

    public void start() {
        isStarted = true;
        clearBoard();
        newPiece();
    }

    void pause() {
        if (!isStarted) {
            return;
        }

        isPaused = !isPaused;
        statusbar.setText(isPaused ? "Paused" : "Points: " + numLinesRemoved);
    }

    void dropDown() {
        int newY = curY;
        while (newY > 0) {
            if (!tryMove(curPiece, curX, newY - 1)) {
                break;
            }
            --newY;
        }
        pieceDropped();
    }

    void oneLineDown() {
        if (!tryMove(curPiece, curX, curY - 1)) {
            pieceDropped();
        }
    }

    void clearBoard() {
        Arrays.fill(board, Tetrominoe.NoShape);
    }

    void pieceDropped() {
        for (int i = 0; i < 4; ++i) {
            int x = curX + curPiece.coords[i][0];
            int y = curY - curPiece.coords[i][1];
            board[(y * BOARD_WIDTH) + x] = curPiece.getShape();
        }
        removeFullLines(); // ✅ Fix 2: removeFullLines() method is implemented below
        if (!isFallingFinished) {
            newPiece();
        }
    }

    void newPiece() {
        curPiece.setRandomShape();
        curX = BOARD_WIDTH / 2 + 1;
        curY = BOARD_HEIGHT - 1;
        if (!tryMove(curPiece, curX, curY)) {
            curPiece.setShape(Tetrominoe.NoShape);
            timer.cancel();
            isStarted = false;
            statusbar.setText("GAME OVER!");
        }
    }

    void removeFullLines() {  // ✅ Fix 2: Implemented removeFullLines() method
        int numFullLines = 0;
        for (int i = BOARD_HEIGHT - 1; i >= 0; --i) {
            boolean lineIsFull = true;
            for (int j = 0; j < BOARD_WIDTH; ++j) {
                if (board[(i * BOARD_WIDTH) + j] == Tetrominoe.NoShape) {
                    lineIsFull = false;
                    break;
                }
            }
            if (lineIsFull) {
                ++numFullLines;
            }
        }
        if (numFullLines > 0) {
            numLinesRemoved += numFullLines;
            statusbar.setText("Points: " + numLinesRemoved);
            isFallingFinished = true;
            repaint();
        }
    }

    class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (!isStarted || curPiece.getShape() == Tetrominoe.NoShape) {
                return;
            }
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                pause();
            }
        }
    }

    class ScheduleTask extends TimerTask {
        @Override
        public void run() {
            if (!isPaused) {
                if (isFallingFinished) {
                    isFallingFinished = false;
                    newPiece();
                } else {
                    oneLineDown();
                }
            }
            repaint();
        }
    }
}

public class Tetris extends JFrame {
    JLabel statusbar;

    public Tetris() {
        initUI();
    }

    void initUI() {
        statusbar = new JLabel("Points: 0");
        add(statusbar, BorderLayout.NORTH);
        Board board = new Board(this);
        add(board);
        setTitle("Tetris");
        setSize(400, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        board.start();
    }

    public JLabel getStatusBar() {  // ✅ Fix 1: Added getStatusBar() method
        return statusbar;
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            Tetris game = new Tetris();
            game.setVisible(true);
        });
    }
}
