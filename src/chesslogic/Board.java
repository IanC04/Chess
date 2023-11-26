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

    /**
     * true if white's turn, false if black's
     */
    boolean whiteToPlay;

    public Board() {
        CHESS_BOARD = new Piece[64];
        whiteToPlay = true;
        resetBoard();
    }

    void resetBoard() {
        Arrays.fill(CHESS_BOARD, A2.getPosition(), H2.getPosition(),
                new Piece(WHITE, PAWN, this));
        Arrays.fill(CHESS_BOARD, A7.getPosition(), H7.getPosition(),
                new Piece(BLACK, PAWN, this));

        CHESS_BOARD[A1.getPosition()] = new Piece(WHITE, ROOK, this);
        CHESS_BOARD[B1.getPosition()] = new Piece(WHITE, HORSE, this);
        CHESS_BOARD[C1.getPosition()] = new Piece(WHITE, BISHOP, this);
        CHESS_BOARD[D1.getPosition()] = new Piece(WHITE, QUEEN, this);
        CHESS_BOARD[E1.getPosition()] = new Piece(WHITE, KING, this);
        CHESS_BOARD[F1.getPosition()] = new Piece(WHITE, BISHOP, this);
        CHESS_BOARD[G1.getPosition()] = new Piece(WHITE, HORSE, this);
        CHESS_BOARD[H1.getPosition()] = new Piece(WHITE, ROOK, this);
        CHESS_BOARD[A8.getPosition()] = new Piece(BLACK, ROOK, this);
        CHESS_BOARD[B8.getPosition()] = new Piece(BLACK, HORSE, this);
        CHESS_BOARD[C8.getPosition()] = new Piece(BLACK, BISHOP, this);
        CHESS_BOARD[D8.getPosition()] = new Piece(BLACK, QUEEN, this);
        CHESS_BOARD[E8.getPosition()] = new Piece(BLACK, KING, this);
        CHESS_BOARD[F8.getPosition()] = new Piece(BLACK, BISHOP, this);
        CHESS_BOARD[G8.getPosition()] = new Piece(BLACK, HORSE, this);
        CHESS_BOARD[H8.getPosition()] = new Piece(BLACK, ROOK, this);

        // Clear middle of board
        Arrays.fill(CHESS_BOARD, A3.getPosition(), H6.getPosition() + 1, null);

        // White goes first
        whiteToPlay = true;
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

    boolean isFree(Notation pos) {
        return CHESS_BOARD[pos.getPosition()] == null;
    }

    /**
     * Returns true if the piece at pos is friendly (same color)
     *
     * @param col
     * @param pos
     * @return
     */
    boolean isFriendly(Piece.PieceColor col, Notation pos) {
        return CHESS_BOARD[pos.getPosition()].C == col;
    }

    /**
     * Returns true if the piece at pos is an enemy (different color)
     *
     * @param col
     * @param pos
     * @return
     */
    boolean isEnemy(Piece.PieceColor col, Notation pos) {
        return CHESS_BOARD[pos.getPosition()] != null && CHESS_BOARD[pos.getPosition()].C != col;
    }

    boolean inBounds(int pos) {
        return pos >= 0 && pos < 64;
    }
}
