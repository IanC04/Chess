/*
    Written by Ian Chen on 11/22/2023
    GitHub: https://github.com/IanC04
 */

package ui;

import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;
import javax.swing.*;

import logic.Board;
import logic.Move;
import logic.Notation;
import logic.Piece;

public class Game extends JFrame {
    private final UIStatusBar uiStatusBar;
    private final UIPopup uiPopup;
    private final UIBoard uiBoard;
    private final UIToolBar uiToolBar;

    private Game() {
        // Initialize final variables
        uiStatusBar = new UIStatusBar();
        uiPopup = new UIPopup();
        uiBoard = new UIBoard(uiStatusBar, uiPopup, new GridLayout(0, 9));
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

        java.io.File icon = new java.io.File("src/ui/icon.png");
        if (icon.isFile()) {
            ImageIcon imageIcon = new ImageIcon(icon.getAbsolutePath());
            setIconImage(imageIcon.getImage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Game::new);
    }
}

class UIBoard extends JPanel {
    private final UIStatusBar uiStatusBar;
    private final UIPopup uiPopup;
    private final Board logicBoard;
    private final JButton[][] squares;
    private Notation squareSelected;
    private Set<Move> currentGreenSquares;

    private static final class AIStatus {

        private boolean aiPlayer;
        private Piece.PieceColor aiColor;

        private AIStatus() {
            aiPlayer = false;
            aiColor = Math.random() > 0.5 ? Piece.PieceColor.WHITE : Piece.PieceColor.BLACK;
        }

        private void resetAI() {
            aiColor = Math.random() > 0.5 ? Piece.PieceColor.WHITE : Piece.PieceColor.BLACK;
        }
    }

    private final AIStatus ai;
    private static final Color LIGHT_SQUARE = new Color(240, 217, 181);
    private static final Color DARK_SQUARE = new Color(181, 136, 99);

    UIBoard(UIStatusBar uiStatusBar, UIPopup uiPopup, GridLayout gridLayout) {
        super(gridLayout);
        this.uiStatusBar = uiStatusBar;
        this.uiPopup = uiPopup;
        this.logicBoard = new Board();
        this.squares = new JButton[8][8];
        this.ai = new AIStatus();
        initializeBoard();
    }

    private void initializeBoard() {
        squareSelected = null;
        currentGreenSquares = null;

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

        uiStatusBar.setStatus("Initialized board");
    }

    void resetGame() {
        uiStatusBar.setStatus("New Game");
        logicBoard.resetBoard();
        ai.resetAI();
        updateBoard();
    }

    void resignGame() {
        uiStatusBar.setStatus(logicBoard.currentPlayerColor + " Resign");
        resetGame();
    }

    void drawGame() {
        uiStatusBar.setStatus(logicBoard.currentPlayerColor + " Draw");
        resetGame();
    }

    void exitGame() {
        uiStatusBar.setStatus("Exiting...");
        System.exit(0);
    }

    void toggleAI() {
        ai.aiPlayer = !ai.aiPlayer;
        uiStatusBar.setStatus(String.format("AI Player %s with color=%s", ai.aiPlayer ?
                "enabled" : "disabled", ai.aiColor));
    }

    /**
     * Updates the graphical board
     */
    private void updateBoard() {
        uiStatusBar.setStatus("Updating...");
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                squares[i][j].setText(Objects.requireNonNullElse(logicBoard.getPiece(i, j), "").toString());
            }
        }
        uiStatusBar.setStatus((logicBoard.getTurn() % 2 == 1 ? "White" : "Black") + "'s turn");

        if (ai.aiPlayer && logicBoard.currentPlayerColor == ai.aiColor) {
            logicBoard.aiMove();
            updateBoard();
        }
    }

    private void manageClick(Notation notation) {
        if (squareSelected == null && logicBoard.getPiece(notation) != null) {
            // Show possible moving squares
            squareSelected = notation;
            currentGreenSquares =
                    logicBoard.getPieceLegalMoves(notation);
            for (Move m : currentGreenSquares) {
                byte[] pos = m.end().getPosition();
                squares[pos[0]][pos[1]].setBackground(Color.GREEN);
            }
        } else {
            // Finalize move
            if (currentGreenSquares != null) {
                for (Move m : currentGreenSquares) {
                    byte[] pos = m.end().getPosition();
                    squares[pos[0]][pos[1]].setBackground((pos[0] + pos[1]) % 2 == 0 ?
                            LIGHT_SQUARE : DARK_SQUARE);
                }
                Move selected = currentGreenSquares.stream().filter(m -> m.end().equals(notation)).findFirst().orElse(null);
                if (selected != null) {
                    uiStatusBar.setStatus("Move " + selected);
                    try {
                        // Promotion
                        uiPopup.show(this, 0, 0);
                        if (selected.moveType() == Move.MoveType.PROMOTION) {
                            selected = new Move(selected.start(), selected.end(),
                                    selected.moveType(), Piece.PieceType.QUEEN);
                        }

                        Piece captured = logicBoard.movePiece(selected);
                        System.out.println("Captured: " + captured);
                        boolean gameOver = switch (logicBoard.gameStatus()) {
                            case 2, 3:
                                yield true;
                            default:
                                yield false;
                        };
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

    HashMap<String, JButton> buttons;

    UIToolBar(UIBoard uiBoard) {
        if (uiBoard == null) {
            throw new IllegalStateException("Initialize uiBoard");
        }

        this.uiBoard = uiBoard;
        this.buttons = new HashMap<>();
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
        addButtonToToolbar(this, "AI", e -> this.toggleAI());
        this.buttons.get("AI").setBackground(Color.RED);
    }

    private void addButtonToToolbar(final JToolBar toolBar, final String buttonText,
                                    final ActionListener actionListener) {
        final JButton button = new JButton(buttonText);
        button.addActionListener(actionListener);
        toolBar.add(button);
        if (buttons.put(buttonText, button) != null) {
            throw new IllegalStateException("Button already exists");
        }
    }

    private void toggleAI() {
        uiBoard.toggleAI();
        JButton aiButton = this.buttons.get("AI");
        aiButton.setBackground(aiButton.getBackground() == Color.GREEN ? Color.RED : Color.GREEN);
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
        System.out.println(status);
        setText(status);
    }
}

class UIPopup extends JPopupMenu {

}