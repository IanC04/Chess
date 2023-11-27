package ai;

import logic.*;

public class Minimax {

    // Game state to evaluate
    private final Board state;

    public Minimax(Board state) {
        this.state = state;
    }

    public Notation[] getBestMove() {
        int[][] bitBoard = state.getBitBoard();

        // Find best move
        int bestValue = Integer.MIN_VALUE;
        Notation bestMove = null;
        for (Board child : state.getChildren()) {
            int value = minimax(4, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
            if (value > bestValue) {
                bestValue = value;
                bestMove = child.getLastMove();
            }
        }
        return new Notation[]{bestMove, bestMove};
    }

    public int minimax(int depth, int alpha, int beta, boolean isMaximizingPlayer) {
        if (depth == 0) {
            return state.evaluate();
        }
        if (isMaximizingPlayer) {
            int bestValue = Integer.MIN_VALUE;
            for (Board child : state.getChildren()) {
                int value = minimax(depth - 1, alpha, beta, false);
                bestValue = Math.max(bestValue, value);
                alpha = Math.max(alpha, value);
                if (beta <= alpha) {
                    break;
                }
            }
            return bestValue;
        } else {
            int bestValue = Integer.MAX_VALUE;
            for (Board child : state.getChildren()) {
                int value = minimax(depth - 1, alpha, beta, true);
                bestValue = Math.min(bestValue, value);
                beta = Math.min(beta, value);
                if (beta <= alpha) {
                    break;
                }
            }
            return bestValue;
        }
    }
}
