package chesslogic;

import java.util.Arrays;

import static chesslogic.Notation.*;
import static chesslogic.Piece.PieceColor.*;
import static chesslogic.Piece.Type.*;

public class Board {

    /**
     * Stored as 1-D array to support algebraic notation indexing
     */
    private final Piece[] CHESS_BOARD;

    public Board() {
        CHESS_BOARD = new Piece[64];
        resetBoard();
    }

    void resetBoard() {
        Arrays.fill(CHESS_BOARD, A2.getPosition(), H2.getPosition(),
                new Piece(WHITE, PAWN));
        Arrays.fill(CHESS_BOARD, A7.getPosition(), H7.getPosition(),
                new Piece(BLACK, PAWN));

        CHESS_BOARD[A1.getPosition()] = new Piece(WHITE, ROOK);
        CHESS_BOARD[B1.getPosition()] = new Piece(WHITE, HORSE);
        CHESS_BOARD[C1.getPosition()] = new Piece(WHITE, BISHOP);
        CHESS_BOARD[D1.getPosition()] = new Piece(WHITE, QUEEN);
        CHESS_BOARD[E1.getPosition()] = new Piece(WHITE, KING);
        CHESS_BOARD[F1.getPosition()] = new Piece(WHITE, BISHOP);
        CHESS_BOARD[G1.getPosition()] = new Piece(WHITE, HORSE);
        CHESS_BOARD[H1.getPosition()] = new Piece(WHITE, ROOK);
        CHESS_BOARD[A8.getPosition()] = new Piece(BLACK, ROOK);
        CHESS_BOARD[B8.getPosition()] = new Piece(BLACK, HORSE);
        CHESS_BOARD[C8.getPosition()] = new Piece(BLACK, BISHOP);
        CHESS_BOARD[D8.getPosition()] = new Piece(BLACK, QUEEN);
        CHESS_BOARD[E8.getPosition()] = new Piece(BLACK, KING);
        CHESS_BOARD[F8.getPosition()] = new Piece(BLACK, BISHOP);
        CHESS_BOARD[G8.getPosition()] = new Piece(BLACK, HORSE);
        CHESS_BOARD[H8.getPosition()] = new Piece(BLACK, ROOK);

        // Clear middle of board
        Arrays.fill(CHESS_BOARD, A3.getPosition(), H6.getPosition() + 1, null);
    }

    private void movePiece(Notation oldPos, Notation newPos) {
        if (CHESS_BOARD[oldPos.getPosition()] == null) {
            throw new IllegalArgumentException("No piece exists at " + oldPos);
        }
        boolean success = CHESS_BOARD[oldPos.getPosition()].setPosition(newPos);
        if (!success) {
            throw new IllegalArgumentException("Piece already exists at " + newPos);
        }
    }

    public String toString() {
        StringBuilder output = new StringBuilder();
        for (Piece piece : CHESS_BOARD) {
            if (piece == null)
                output.append(" ");
            else
                output.append(piece);
        }
        return output.toString();
    }
}
