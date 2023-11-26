package chesslogic;

import java.util.Arrays;

import static chesslogic.Notation.*;
import static chesslogic.Piece.PieceColor.*;
import static chesslogic.Piece.Type.*;

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
        Arrays.fill(CHESS_BOARD[A2.getPosition()[0]], new Piece(WHITE, PAWN, this));
        Arrays.fill(CHESS_BOARD[A7.getPosition()[0]], new Piece(BLACK, PAWN, this));

        byte[] pos;

        // White pieces
        pos = A1.getPosition();
        CHESS_BOARD[pos[0]][pos[1]] = new Piece(WHITE, ROOK, this);
        pos = B1.getPosition();
        CHESS_BOARD[pos[0]][pos[1]] = new Piece(WHITE, HORSE, this);
        pos = C1.getPosition();
        CHESS_BOARD[pos[0]][pos[1]] = new Piece(WHITE, BISHOP, this);
        pos = D1.getPosition();
        CHESS_BOARD[pos[0]][pos[1]] = new Piece(WHITE, QUEEN, this);
        pos = E1.getPosition();
        CHESS_BOARD[pos[0]][pos[1]] = new Piece(WHITE, KING, this);
        pos = F1.getPosition();
        CHESS_BOARD[pos[0]][pos[1]] = new Piece(WHITE, BISHOP, this);
        pos = G1.getPosition();
        CHESS_BOARD[pos[0]][pos[1]] = new Piece(WHITE, HORSE, this);
        pos = H1.getPosition();
        CHESS_BOARD[pos[0]][pos[1]] = new Piece(WHITE, ROOK, this);

        // Black pieces
        pos = A8.getPosition();
        CHESS_BOARD[pos[0]][pos[1]] = new Piece(BLACK, ROOK, this);
        pos = B8.getPosition();
        CHESS_BOARD[pos[0]][pos[1]] = new Piece(BLACK, HORSE, this);
        pos = C8.getPosition();
        CHESS_BOARD[pos[0]][pos[1]] = new Piece(BLACK, BISHOP, this);
        pos = D8.getPosition();
        CHESS_BOARD[pos[0]][pos[1]] = new Piece(BLACK, QUEEN, this);
        pos = E8.getPosition();
        CHESS_BOARD[pos[0]][pos[1]] = new Piece(BLACK, KING, this);
        pos = F8.getPosition();
        CHESS_BOARD[pos[0]][pos[1]] = new Piece(BLACK, BISHOP, this);
        pos = G8.getPosition();
        CHESS_BOARD[pos[0]][pos[1]] = new Piece(BLACK, HORSE, this);
        pos = H8.getPosition();
        CHESS_BOARD[pos[0]][pos[1]] = new Piece(BLACK, ROOK, this);

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

    public String toString() {
        StringBuilder output = new StringBuilder();
        for (Piece[] row : CHESS_BOARD) {
            for (Piece piece : row) {
                if (piece == null) {
                    output.append(" ");
                } else {
                    output.append(piece);
                }
            }
            output.append("\n");
        }
        return output.toString();
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
}
