/*
    Written by Ian Chen on 11/22/2023
    GitHub: https://github.com/IanC04
 */

package ui;

import javax.swing.*;

public class Game {
    private JPanel Board;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Game");
        frame.setContentPane(new Game().Board);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
