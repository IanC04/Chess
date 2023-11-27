package logic;

import java.util.Arrays;

import static logic.Notation.*;
import static logic.Piece.PieceColor.*;
import static logic.Piece.Type.*;

public class Board {

    /**
     * Stored as 1-D array to support algebraic notation indexing
     */
    private final Piece[][] CHESS_BOARD;

    /**
     * true if white's turn, false if black's
     */
    boolean whiteToPlay;

    public Board() {
        CHESS_BOARD = new Piece[8][8];
        whiteToPlay = true;
        resetBoard();
    }

    void resetBoard() {
        // Both sets of pawns
        for (int i = 0; i < CHESS_BOARD.length; ++i) {
            Notation nW = Notation.get(A2.getPosition()[0], i);
            Notation nB = Notation.get(A7.getPosition()[0], i);
            CHESS_BOARD[nW.getPosition()[0]][i] = new Piece(WHITE, PAWN, nW, this);
            CHESS_BOARD[nB.getPosition()[0]][i] = new Piece(BLACK, PAWN, nB, this);
        }

        byte[] pos;

        // White pieces
        pos = A1.getPosition();
        CHESS_BOARD[pos[0]][pos[1]] = new Piece(WHITE, ROOK, A1, this);
        pos = B1.getPosition();
        CHESS_BOARD[pos[0]][pos[1]] = new Piece(WHITE, HORSE, B1, this);
        pos = C1.getPosition();
        CHESS_BOARD[pos[0]][pos[1]] = new Piece(WHITE, BISHOP, C1, this);
        pos = D1.getPosition();
        CHESS_BOARD[pos[0]][pos[1]] = new Piece(WHITE, QUEEN, D1, this);
        pos = E1.getPosition();
        CHESS_BOARD[pos[0]][pos[1]] = new Piece(WHITE, KING, E1, this);
        pos = F1.getPosition();
        CHESS_BOARD[pos[0]][pos[1]] = new Piece(WHITE, BISHOP, F1, this);
        pos = G1.getPosition();
        CHESS_BOARD[pos[0]][pos[1]] = new Piece(WHITE, HORSE, G1, this);
        pos = H1.getPosition();
        CHESS_BOARD[pos[0]][pos[1]] = new Piece(WHITE, ROOK, H1, this);

        // Black pieces
        pos = A8.getPosition();
        CHESS_BOARD[pos[0]][pos[1]] = new Piece(BLACK, ROOK, A8, this);
        pos = B8.getPosition();
        CHESS_BOARD[pos[0]][pos[1]] = new Piece(BLACK, HORSE, B8, this);
        pos = C8.getPosition();
        CHESS_BOARD[pos[0]][pos[1]] = new Piece(BLACK, BISHOP, C8, this);
        pos = D8.getPosition();
        CHESS_BOARD[pos[0]][pos[1]] = new Piece(BLACK, QUEEN, D8, this);
        pos = E8.getPosition();
        CHESS_BOARD[pos[0]][pos[1]] = new Piece(BLACK, KING, E8, this);
        pos = F8.getPosition();
        CHESS_BOARD[pos[0]][pos[1]] = new Piece(BLACK, BISHOP, F8, this);
        pos = G8.getPosition();
        CHESS_BOARD[pos[0]][pos[1]] = new Piece(BLACK, HORSE, G8, this);
        pos = H8.getPosition();
        CHESS_BOARD[pos[0]][pos[1]] = new Piece(BLACK, ROOK, H8, this);

        // Clear middle of board
        for (int i = 2; i <= 5; i++) {
            Arrays.fill(CHESS_BOARD[i], null);
        }

        // White goes first
        whiteToPlay = true;
    }

    private void movePiece(Notation oldPos, Notation newPos) {
        byte[] oldPosArr = oldPos.getPosition();
        byte[] newPosArr = oldPos.getPosition();
        if (CHESS_BOARD[oldPosArr[0]][oldPosArr[1]] == null) {
            throw new IllegalArgumentException("No piece exists at " + oldPos);
        }
        boolean success = CHESS_BOARD[oldPosArr[0]][oldPosArr[1]].setPosition(newPos);
        if (!success) {
            throw new IllegalArgumentException("Piece already exists at " + newPos);
        }
    }

    /**
     * Returns the piece at the given position
     *
     * @param pos
     * @return
     */
    Piece getPiece(Notation pos) {
        byte[] posArr = pos.getPosition();
        return CHESS_BOARD[posArr[0]][posArr[1]];
    }

    public Piece getPiece(int row, int col) {
        return CHESS_BOARD[row][col];
    }

    boolean isFree(Notation pos) {
        byte[] posArr = pos.getPosition();
        return CHESS_BOARD[posArr[0]][posArr[1]] == null;
    }

    /**
     * Returns true if the piece at pos is friendly (same color)
     *
     * @param col
     * @param pos
     * @return
     */
    boolean isFriendly(Piece.PieceColor col, Notation pos) {
        byte[] posArr = pos.getPosition();
        return CHESS_BOARD[posArr[0]][posArr[1]].C == col;
    }

    /**
     * Returns true if the piece at pos is an enemy (different color)
     *
     * @param col
     * @param pos
     * @return
     */
    boolean isEnemy(Piece.PieceColor col, Notation pos) {
        byte[] posArr = pos.getPosition();
        return CHESS_BOARD[posArr[0]][posArr[1]] != null && CHESS_BOARD[posArr[0]][posArr[1]].C != col;
    }

    boolean inBounds(int pos) {
        return pos >= 0 && pos < 64;
    }

    boolean inBounds(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    public String toString() {
        StringBuilder output = new StringBuilder();
        for (int i = CHESS_BOARD.length - 1; i >= 0; --i) {
            for (Piece piece : CHESS_BOARD[i]) {
                output.append(piece == null ? ' ' : piece);
            }
            output.append('\n');
        }
        return output.toString();
    }
}
