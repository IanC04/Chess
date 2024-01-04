/*
    Written by Ian Chen on 11/22/2023
    GitHub: https://github.com/IanC04
 */

package ui;

import java.awt.*;
import java.awt.event.*;
import java.util.Objects;
import java.util.Set;
import javax.swing.*;

import logic.Board;
import logic.Move;
import logic.Notation;
import logic.Piece;

public class Game extends JFrame {
    private final UIBoard uiBoard;
    private final UIToolBar uiToolBar;

    private final UIStatusBar uiStatusBar;

    private Game() {
        // Initialize final variables
        uiStatusBar = new UIStatusBar();
        uiBoard = new UIBoard(uiStatusBar, new GridLayout(0, 9));
        uiToolBar = new UIToolBar(uiBoard);
        initializeUI();
    }

    /**
     * Initializes the UI
     */
    private void initializeUI() {
        if (uiBoard == null || uiToolBar == null || uiStatusBar == null) {
            throw new IllegalStateException("Initialize uiBoard, uiToolBar, and uiStatusBar");
        }

        setTitle("Chess");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // setResizable(false);
        setLayout(new BorderLayout());

        add(uiStatusBar, BorderLayout.SOUTH);
        add(uiToolBar, BorderLayout.NORTH);
        add(uiBoard, BorderLayout.CENTER);

        pack();
        setLocationByPlatform(true);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Game::new);
    }
}

class UIBoard extends JPanel {
    private final UIStatusBar uiStatusBar;

    private final Board logicBoard;

    private final JButton[][] squares;

    private Notation squareSelected;

    private Set<Move> currentGreenSquares;

    private static final class AIStatus {
        boolean aiPlayer = false;
        boolean aiPlayerTurn = false;
    }

    private final AIStatus ai = new AIStatus();

    private static final Color LIGHT_SQUARE = new Color(240, 217, 181);
    private static final Color DARK_SQUARE = new Color(181, 136, 99);

    UIBoard(UIStatusBar uiStatusBar, GridLayout gridLayout) {
        super(gridLayout);
        this.uiStatusBar = uiStatusBar;
        logicBoard = new Board();
        squares = new JButton[8][8];
        currentGreenSquares = null;
        initializeBoard();
    }

    private void initializeBoard() {
        squareSelected = null;

        for (int i = 7; i >= 0; --i) {
            for (int j = -1; j < 8; ++j) {
                if (j == -1) {
                    add(new JLabel(Integer.toString(i + 1),
                            SwingConstants.CENTER));
                    continue;
                }
                JButton square = new JButton();
                square.setPreferredSize(new Dimension(100, 100));
                square.setFont(new Font("", Font.PLAIN, 40));
                square.setText(Objects.requireNonNullElse(logicBoard.getPiece(i, j), "").toString());
                square.setBackground((i + j) % 2 == 0 ? LIGHT_SQUARE : DARK_SQUARE);
                int finalI = i;
                int finalJ = j;
                square.addActionListener(e -> manageClick(Notation.get(finalI, finalJ)));
                squares[i][j] = square;
                add(square);
            }
        }
        add(new JLabel(""), BorderLayout.NORTH);
        for (int i = 0; i < 8; ++i) {
            add(new JLabel(Character.toString((char) ('A' + i)),
                    SwingConstants.CENTER));
        }

        uiStatusBar.setStatus("Initialized");
    }

    void resetGame() {
        System.out.println("New Game");
        uiStatusBar.setStatus("New Game");
        logicBoard.resetBoard();
        updateBoard();
    }

    void resignGame() {
        System.out.println("Resign");
    }

    void drawGame() {
        System.out.println("Draw");
    }

    void exitGame() {
        System.out.println("Exit");
        uiStatusBar.setStatus("Exiting...");
        System.exit(0);
    }

    void toggleAI() {
        ai.aiPlayer = !ai.aiPlayer;
        System.out.println("AI Player " + (ai.aiPlayer ? "enabled" : "disabled") + ".");
    }

    /**
     * Updates the graphical board
     */
    private void updateBoard() {
        System.out.println("Update");
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                squares[i][j].setText(Objects.requireNonNullElse(logicBoard.getPiece(i, j), "").toString());
            }
        }
        uiStatusBar.setStatus(logicBoard.whosTurn() + "'s turn");

        if (ai.aiPlayer && ai.aiPlayerTurn) {
            logicBoard.aiMove();
            ai.aiPlayerTurn = false;
            updateBoard();
        }
    }

    private void manageClick(Notation notation) {
        if (squareSelected == null && logicBoard.getPiece(notation) != null) {
            squareSelected = notation;
            currentGreenSquares =
                    logicBoard.getPiecePossibleMoves(notation);
            for (Move m : currentGreenSquares) {
                byte[] pos = m.end().getPosition();
                squares[pos[0]][pos[1]].setBackground(Color.GREEN);
            }
        } else {
            if (currentGreenSquares != null) {
                for (Move m : currentGreenSquares) {
                    byte[] pos = m.end().getPosition();
                    squares[pos[0]][pos[1]].setBackground((pos[0] + pos[1]) % 2 == 0 ?
                            LIGHT_SQUARE : DARK_SQUARE);
                }
                Move selected = currentGreenSquares.stream().filter(m -> m.end().equals(notation)).findFirst().orElse(null);
                if (selected != null) {
                    System.out.println("Move " + selected);
                    try {
                        Piece captured = logicBoard.movePiece(selected);
                        if (ai.aiPlayer) {
                            ai.aiPlayerTurn = true;
                        }
                        boolean gameOver = switch (logicBoard.gameStatus()) {
                            case 2, 3:
                                yield true;
                            default:
                                yield false;
                        };
                        System.out.println("Captured: " + captured);
                        if (gameOver) {
                            uiStatusBar.setStatus("Game Over");
                            // Causes IllegalStateException for thread not owner
                            wait(1_000);
                            resetGame();
                        }
                    } catch (IllegalArgumentException e) {
                        System.err.println("Bad argument: " + e.getMessage());
                    } catch (InterruptedException e) {
                        System.err.println("Interrupt: " + e.getMessage());
                    }
                }
            }
            squareSelected = null;
            currentGreenSquares = null;
        }

        updateBoard();
    }

    @Override
    public String toString() {
        return logicBoard.toString();
    }
}

class UIToolBar extends JToolBar {

    UIBoard uiBoard;

    UIToolBar(UIBoard uiBoard) {
        if (uiBoard == null) {
            throw new IllegalStateException("Initialize uiBoard");
        }

        this.uiBoard = uiBoard;
        setFloatable(false);
        initializeToolbar();
    }

    private void initializeToolbar() {
        setFloatable(false);
        addButtonToToolbar(this, "New Game", e -> uiBoard.resetGame());
        addSeparator();
        addButtonToToolbar(this, "Resign", e -> uiBoard.resignGame());
        addSeparator();
        addButtonToToolbar(this, "Draw", e -> uiBoard.drawGame());
        addSeparator();
        addButtonToToolbar(this, "Exit", e -> uiBoard.exitGame());
        addSeparator();
        addButtonToToolbar(this, "AI", e -> uiBoard.toggleAI());
    }

    private void addButtonToToolbar(final JToolBar toolBar, final String buttonText,
                                    final ActionListener actionListener) {
        final JButton button = new JButton(buttonText);
        button.addActionListener(actionListener);
        toolBar.add(button);
    }
}

class UIStatusBar extends JLabel {

    UIStatusBar() {
        initializeStatusBar();
    }

    private void initializeStatusBar() {
        setText("Initializing...");
    }

    void setStatus(String status) {
        setText(status);
    }
}