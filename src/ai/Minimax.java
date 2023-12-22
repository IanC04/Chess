package ai;

import logic.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Minimax {

    // Game state to evaluate
    private final Board state;

    public Minimax(Board state) {
        this.state = state;
    }

    public Move getBestMove(boolean isWhite) {
        Move move = minimax(4, state, isWhite);

        return move;
    }

    private Move minimax(int depth, Board board, boolean currentColor) {
        Move bestMove = new Move(Integer.MIN_VALUE);
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                Piece source = board.getPiece(i, j);
                if (source != null && source.isWhite() == currentColor) {
                    Notation start = Notation.get(i, j);
                    Set<Move> possibleMoves = board.getPossibleMoves(start);

                    for (Move move : possibleMoves) {
                        Board tempBoard = new Board(board);
                        tempBoard.updateBoard(move);

                        int currentMoveValue = minimaxHelper(depth - 1, tempBoard,
                                !currentColor,
                                false);
                        if (bestMove.value() < currentMoveValue) {
                            bestMove = move;
                        }
                    }
                }
            }
        }

        if (bestMove.start() == null || bestMove.end() == null) {
            throw new IllegalStateException("No move found error or stalemate");
        }
        return bestMove;
    }

    private int minimaxHelper(int depth, Board board, boolean currentColor,
                              boolean isMaximizingPlayer) {
        if (depth == 0) {
            return board.evaluate(currentColor);
        }
        int bestMove = isMaximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                Piece source = board.getPiece(i, j);
                if (source != null && source.isWhite() == currentColor) {
                    Notation start = Notation.get(i, j);
                    Set<Move> possibleMoves = board.getPossibleMoves(start);

                    for (Move move : possibleMoves) {
                        Board tempBoard = new Board(board);
                        tempBoard.updateBoard(move);

                        int currentMoveValue = minimaxHelper(depth - 1, tempBoard,
                                !currentColor,
                                !isMaximizingPlayer);
                        if (isMaximizingPlayer) {
                            bestMove = Math.max(bestMove, currentMoveValue);
                        } else {
                            bestMove = Math.min(bestMove, currentMoveValue);
                        }
                    }
                }
            }
        }

        return bestMove;
    }
}
