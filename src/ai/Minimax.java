package ai;

import logic.*;

public class Minimax {

    // Game state to evaluate
    private final Board state;

    public Minimax(Board state) {
        this.state = state;
    }

    public Move getBestMove(boolean isWhite) {
        Board tempBoard = new Board(state);
        Move bestMove = minimax(3, tempBoard, Integer.MIN_VALUE, Integer.MAX_VALUE, isWhite);
        System.out.println("Best move: " + bestMove);
        return bestMove;
    }

    private Move minimax(int depth, Board board, int alpha, int beta, boolean maximizingPlayer) {
        if (depth == 0 || board.inStaleMate(!maximizingPlayer) || board.inCheckMate(!maximizingPlayer)) {
            return new Move(board.evaluate(maximizingPlayer));
        }

        Move bestMove;
        if (maximizingPlayer) {
            bestMove = new Move(Integer.MIN_VALUE);
            for (Move move : board.getAllPossibleMoves()) {
                Board tempBoard = new Board(board);
                tempBoard.movePiece(move);
                Move currentMove = minimax(depth - 1, tempBoard, alpha, beta,
                        false);
                if (currentMove.value() > bestMove.value()) {
                    bestMove = new Move(move, currentMove.value());
                }
                alpha = Math.max(alpha, bestMove.value());
                if (beta <= alpha) {
                    break;
                }
            }
        } else {
            bestMove = new Move(Integer.MAX_VALUE);
            for (Move move : board.getAllPossibleMoves()) {
                Board tempBoard = new Board(board);
                tempBoard.movePiece(move);
                Move currentMove = minimax(depth - 1, tempBoard, alpha, beta,
                        true);
                if (currentMove.value() < bestMove.value()) {
                    bestMove = new Move(move, currentMove.value());
                }
                alpha = Math.min(alpha, bestMove.value());
                if (beta <= alpha) {
                    break;
                }
            }
        }

        if (bestMove.start() == null || bestMove.end() == null) {
            throw new IllegalStateException("No move found error or stalemate: " + bestMove);
        }
        return bestMove;
    }
}
