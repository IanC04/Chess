/*
    Written by Ian Chen on 11/22/2023
    GitHub: https://github.com/IanC04
 */

package ui;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.DirectoryChooser;

import logic.Board;
import logic.Move;
import logic.Notation;
import logic.Piece;

public class Game extends JFrame {
    /**
     * <a href="https://stackoverflow.com/questions/57620350/how-to-fix-exception-when-creating-jfxpanel-in-swing-with-javafx-12">StackOverflow</a>
     */
    private final JFXPanel jfxPanel;
    private final UIStatusBar uiStatusBar;
    private final UIBoard uiBoard;
    private final UIToolBar uiToolBar;

    private Game() {
        jfxPanel = new JFXPanel(); // Initializes JavaFX environment
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
    private final Board logicBoard;
    private final JButton[][] squares;

    private static final class Selection {
        private Piece currentlySelectedPiece;
        private Notation squareSelected;
        private Set<Move> currentGreenSquares;

        void reset() {
            currentlySelectedPiece = null;
            squareSelected = null;
            currentGreenSquares = null;
        }
    }

    private final Selection selection;

    private static final class AIStatus {
        private boolean aiPlayer;
        private Piece.PieceColor aiColor;

        private AIStatus() {
            aiPlayer = false;
            resetAI();
        }

        private void resetAI() {
            aiColor = Math.random() >= 0.5 ? Piece.PieceColor.WHITE : Piece.PieceColor.BLACK;
        }
    }

    private final AIStatus ai;
    private static final Color LIGHT_SQUARE = new Color(240, 217, 181);
    private static final Color DARK_SQUARE = new Color(181, 136, 99);
    private static final Color HIGHLIGHT_LEGAL = Color.GREEN;
    private static final Color HIGHLIGHT_CHECK = Color.RED;

    UIBoard(UIStatusBar uiStatusBar, GridLayout gridLayout) {
        super(gridLayout);
        this.uiStatusBar = uiStatusBar;
        this.logicBoard = new Board();
        this.squares = new JButton[8][8];
        this.ai = new AIStatus();
        this.selection = new Selection();
        this.selection.reset();
        initializeBoard();
    }

    private void initializeBoard() {
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
        resetBoardBackground();
        updateBoard();
        setEnabled(true);
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
        updateBoard();
    }

    void downloadStates() {
        List<String> currentStates = logicBoard.getGameStates();
        String fileName =
                LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));

        DirectoryChooser directoryChooser = new DirectoryChooser();
        File saveDirectory = directoryChooser.showDialog(null);
        if (saveDirectory == null || !saveDirectory.isDirectory()) {
            System.out.println("Invalid save directory");
        } else {
            System.out.println("Saving to " + saveDirectory.getAbsolutePath());
            try (FileOutputStream fos =
                         new FileOutputStream(String.format("%s/%s.txt", saveDirectory, fileName))) {
                for (String state : currentStates) {
                    fos.write(state.getBytes());
                    fos.write("\n".getBytes());
                }
            } catch (Exception e) {
                System.err.println("Error writing to file: " + e.getMessage());
            }
        }
    }

    private void resetBoardBackground() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                squares[i][j].setBackground((i + j) % 2 == 0 ?
                        LIGHT_SQUARE : DARK_SQUARE);
            }
        }
    }

    private void highlightKing() {
        int status = logicBoard.gameStatus();
        Notation kingPos = logicBoard.getKing(logicBoard.currentPlayerColor);
        byte[] kingPosArr = kingPos.getPosition();
        switch (status) {
            case 0, 2 ->
                    squares[kingPosArr[0]][kingPosArr[1]].setBackground((kingPosArr[0] + kingPosArr[1]) % 2 == 0 ?
                            LIGHT_SQUARE : DARK_SQUARE);
            case 1, 3 -> squares[kingPosArr[0]][kingPosArr[1]].setBackground(HIGHLIGHT_CHECK);
            default -> throw new IllegalStateException("Invalid game status");
        }
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
        uiStatusBar.setStatus(String.format("%s's turn", logicBoard.currentPlayerColor));

        if (ai.aiPlayer && logicBoard.currentPlayerColor == ai.aiColor) {
            logicBoard.aiMove();
            highlightKing();
            updateBoard();
        }
    }

    private void manageClick(Notation notation) {
        Piece selectedPiece = logicBoard.getPiece(notation);
        if (selectedPiece == null || selectedPiece.C() != logicBoard.currentPlayerColor) {
            if (selection.currentGreenSquares != null && selection.currentGreenSquares.stream().map(Move::end).anyMatch(notation::equals)) {
                makeMove(notation);
            }
            resetGreenSquares();
        } else {
            resetGreenSquares();
            displayMoves(selectedPiece, notation);
        }
        updateBoard();
    }

    private void makeMove(Notation endPos) {
        // Debug
        System.out.println("Move: " + selection.currentlySelectedPiece + ": " + selection.squareSelected + endPos);

        Set<Move> selectedMoves =
                selection.currentGreenSquares.stream().filter(m -> m.end().equals(endPos)).collect(Collectors.toSet());
        if (selectedMoves.size() != 1) {
            throw new IllegalStateException("Invalid number of moves");
        }
        Move selectedMove = selectedMoves.iterator().next();
        if (selectedMove != null) {
            uiStatusBar.setStatus("Move " + selectedMove);
            try {
                Notation beforeMove = logicBoard.getKing(logicBoard.currentPlayerColor);
                byte[] beforeMoveArr = beforeMove.getPosition();
                squares[beforeMoveArr[0]][beforeMoveArr[1]].setBackground((beforeMoveArr[0] + beforeMoveArr[1]) % 2 == 0 ?
                        LIGHT_SQUARE : DARK_SQUARE);

                if (selectedMove.moveType() == Move.MoveType.PROMOTION) {
                    selectedMove = managePromotion(selectedMove);
                }
                Piece captured = logicBoard.movePiece(selectedMove);
                System.out.println("Captured: " + captured);
                Notation afterMove = logicBoard.getKing(logicBoard.currentPlayerColor);
                byte[] afterMoveArr = afterMove.getPosition();
                squares[afterMoveArr[0]][afterMoveArr[1]].setBackground(afterMoveArr[0] + afterMoveArr[1] % 2 == 0 ?
                        LIGHT_SQUARE : DARK_SQUARE);
                highlightKing();
                boolean gameOver = switch (logicBoard.gameStatus()) {
                    case 0, 1:
                        yield false;
                    case 2, 3:
                        yield true;
                    default:
                        throw new IllegalStateException("Invalid game status");
                };
                if (gameOver) {
                    uiStatusBar.setStatus("Game Over");
                    uiStatusBar.setStatus(String.format("%s wins!",
                            Piece.PieceColor.opposite(logicBoard.currentPlayerColor)));
                    System.out.println("Game Over");
                    // User can still click but no action will be taken
                    setEnabled(false);
                }
            } catch (IllegalArgumentException e) {
                System.err.println("Bad argument: " + e.getMessage());
            } catch (IllegalStateException e) {
                System.err.println("Probably wait error: " + e.getMessage());
            }
        }
    }

    private void displayMoves(Piece piece, Notation pos) {
        selection.currentlySelectedPiece = piece;
        selection.squareSelected = pos;
        selection.currentGreenSquares =
                logicBoard.getPieceLegalMoves(pos);
        for (Move m : selection.currentGreenSquares) {
            byte[] posArr = m.end().getPosition();
            squares[posArr[0]][posArr[1]].setBackground(HIGHLIGHT_LEGAL);
        }
    }

    private void resetGreenSquares() {
        if (selection.currentGreenSquares != null) {
            for (Move m : selection.currentGreenSquares) {
                byte[] pos = m.end().getPosition();
                squares[pos[0]][pos[1]].setBackground((pos[0] + pos[1]) % 2 == 0 ?
                        LIGHT_SQUARE : DARK_SQUARE);
            }
        }
        selection.reset();
    }

    private Move managePromotion(Move move) {
        final String MESSAGE = "Choose Promotion Type", TITLE = "Pawn Promotion";
        int option = JOptionPane.showOptionDialog(this, MESSAGE, TITLE, JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE, null,
                new Piece.PieceType[]{Piece.PieceType.ROOK,
                        Piece.PieceType.KNIGHT, Piece.PieceType.BISHOP,
                        Piece.PieceType.QUEEN}, Piece.PieceType.QUEEN);
        if (option == JOptionPane.DEFAULT_OPTION) {
            option = Piece.PieceType.QUEEN.ordinal();
        } else {
            ++option;
        }
        return new Move(move.start(), move.end(), move.moveType(), Piece.PieceType.values()[option]);
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
        addButtonToToolbar(this, "Download",
                e -> Platform.runLater(() -> uiBoard.downloadStates()));
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
        // System.out.println(status);
        setText(status);
    }
}