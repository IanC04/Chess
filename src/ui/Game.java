/*
    Written by Ian Chen on 11/22/2023
    GitHub: https://github.com/IanC04
 */

package ui;

import java.awt.*;
import java.awt.event.*;
import java.util.Objects;
import javax.swing.*;

import logic.Board;

public class Game extends JFrame {
    private final JPanel boardPanel;
    private final Board chessBoard;
    private final JButton[][] boardSquares;
    private final JToolBar toolBar;

    Game() {
        // Initialize final variables
        boardPanel = new JPanel(new GridLayout(0, 9));
        chessBoard = new Board();
        boardSquares = new JButton[8][8];
        toolBar = new JToolBar();
        initializeBoard();
        initializeToolbar();
        initializeUI();
    }

    private void initializeBoard() {
//        System.out.println(chessBoard);

        for (int i = 7; i >= 0; --i) {
            for (int j = -1; j < 8; ++j) {
                if (j == -1) {
                    boardPanel.add(new JLabel(Integer.toString(8 - i),
                            SwingConstants.CENTER));
                    continue;
                }
                JButton square = new JButton();
                square.setPreferredSize(new Dimension(100, 100));
                square.setFont(new Font("", Font.PLAIN, 40));
                square.setText(Objects.requireNonNullElse(chessBoard.getPiece(i, j), "").toString());
                square.setBackground((i + j) % 2 == 0 ? Color.WHITE : Color.BLACK);
                boardSquares[i][j] = square;
                boardPanel.add(boardSquares[i][j]);
            }
        }
        boardPanel.add(new JLabel(""), BorderLayout.NORTH);
        for (int i = 0; i < 8; ++i) {
            boardPanel.add(new JLabel(Character.toString((char) ('A' + i)),
                    SwingConstants.CENTER));
        }
    }

    private void initializeToolbar() {
        toolBar.setFloatable(false);
        addButtonToToolbar(toolBar, "New Game", e -> System.out.println("New Game"));
        toolBar.addSeparator();
        addButtonToToolbar(toolBar, "Resign", e -> System.out.println("New Game"));
        toolBar.addSeparator();
        addButtonToToolbar(toolBar, "Draw", e -> System.out.println("New Game"));
        add(toolBar, BorderLayout.NORTH);
    }

    private void addButtonToToolbar(final JToolBar toolBar, final String buttonText,
                                    final ActionListener actionListener) {
        final JButton button = new JButton(buttonText);
        button.addActionListener(actionListener);
        toolBar.add(button);
    }

    /**
     * Initializes the UI
     * board MUST be initialized before calling this method
     */
    private void initializeUI() {
        setTitle("Chess");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setResizable(false);
        setLayout(new BorderLayout());

        add(boardPanel, BorderLayout.CENTER);

        pack();
        setLocationByPlatform(true);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Game::new);
    }
}
