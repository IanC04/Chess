package logic;

import ai.Minimax;

import java.util.Arrays;
import java.util.Set;

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
    private boolean whiteToPlay;

    /**
     * AI minimax algorithm
     */
    private final Minimax ai;

    public Board() {
        this.CHESS_BOARD = new Piece[8][8];
        this.whiteToPlay = true;
        this.ai = new Minimax(this);
        resetBoard();
    }

    /**
     * Must be used only when generating temporary boards
     *
     * @param board to copy
     */
    public Board(Board board) {
        this.CHESS_BOARD = new Piece[8][8];
        this.whiteToPlay = board.whiteToPlay;
        this.ai = null;
        for (int i = 0; i < 8; ++i) {
            System.arraycopy(board.CHESS_BOARD[i], 0, this.CHESS_BOARD[i], 0, 8);
        }
    }

    /**
     * Resets the board to the initial position
     */
    public void resetBoard() {
        // Both sets of pawns
        for (int i = 0; i < CHESS_BOARD.length; ++i) {
            Notation nW = Notation.get(A2.getPosition()[0], i);
            Notation nB = Notation.get(A7.getPosition()[0], i);
            CHESS_BOARD[nW.getPosition()[0]][i] = new Piece(WHITE, PAWN, this);
            CHESS_BOARD[nB.getPosition()[0]][i] = new Piece(BLACK, PAWN, this);
        }

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

    /**
     * Moves the piece at oldPos to newPos
     * Returns the piece that was captured, or null if no piece was captured
     *
     * @param oldPos original position
     * @param newPos new position
     * @return the captured piece, if any
     */
    public Piece movePiece(Notation oldPos, Notation newPos) {
        if (getPiece(oldPos) == null) {
            throw new IllegalArgumentException("No piece exists at " + oldPos);
        }
        if (getPiece(oldPos).getColor() != (whiteToPlay ? WHITE : BLACK)) {
            throw new IllegalArgumentException("Piece color does not match turn");
        }
        getPiece(oldPos).moved();
        Piece captured = aiMovePiece(oldPos, newPos);
        whiteToPlay = !whiteToPlay;

        return captured;
    }

    public Piece aiMovePiece(Notation oldPos, Notation newPos) {
        Piece captured = getPiece(newPos);
        if (oldPos.equals(newPos)) {
            throw new IllegalArgumentException("Piece already exists at " + newPos);
        } else {
            byte[] newPosArr = newPos.getPosition();
            CHESS_BOARD[newPosArr[0]][newPosArr[1]] = getPiece(oldPos);
            byte[] oldPosArr = oldPos.getPosition();
            CHESS_BOARD[oldPosArr[0]][oldPosArr[1]] = null;
        }
        return captured;
    }

    /**
     * Returns the piece at the given position
     *
     * @param pos position
     * @return piece at pos
     */
    public Piece getPiece(Notation pos) {
        byte[] posArr = pos.getPosition();
        return CHESS_BOARD[posArr[0]][posArr[1]];
    }

    /**
     * Returns the piece at the given position
     *
     * @param row index
     * @param col index
     * @return piece at pos
     */
    public Piece getPiece(int row, int col) {
        return CHESS_BOARD[row][col];
    }

    /**
     * Returns all possible moves of the piece at pos
     *
     * @param pos position
     * @return set of possible moves
     */
    public Set<Notation> getPossibleMoves(Notation pos) {
        if (isFree(pos)) {
            throw new IllegalArgumentException("No piece exists at " + pos);
        }
        return getPiece(pos).possibleMoves(pos);
    }

    /**
     * Returns true if the piece at pos is free (no piece)
     *
     * @param pos position
     * @return true if free
     */
    boolean isFree(Notation pos) {
        return getPiece(pos) == null;
    }

    /**
     * Returns true if the piece at pos is friendly (same color)
     *
     * @param col piece color
     * @param pos position
     * @return true if friendly
     */
    boolean isFriendly(Piece.PieceColor col, Notation pos) {
        Piece piece = getPiece(pos);
        if (piece == null) {
            return false;
        }
        return piece.getColor() == col;
    }

    /**
     * Returns true if the piece at pos is an enemy (different color)
     *
     * @param col piece color
     * @param pos position
     * @return true if enemy
     */
    boolean isEnemy(Piece.PieceColor col, Notation pos) {
        // return !isFree(pos) && !isFriendly(col, pos);
        Piece piece = getPiece(pos);
        if (piece == null) {
            return false;
        }
        return piece.getColor() != col;
    }

    /**
     * Returns true if the given position is in bounds
     *
     * @param pos Flattened representation of position
     * @return true if in bounds
     */
    public static boolean inBounds(int pos) {
        return pos >= 0 && pos < 64;
    }

    /**
     * Returns true if the given position is in bounds
     *
     * @param pos position
     * @return true if in bounds
     */
    public static boolean inBounds(Notation pos) {
        byte[] posArr = pos.getPosition();
        return posArr[0] >= 0 && posArr[0] < 8 && posArr[1] >= 0 && posArr[1] < 8;
    }

    /**
     * Returns true if the given position is in bounds
     *
     * @param row index
     * @param col index
     * @return true if in bounds
     */
    public static boolean inBounds(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    /**
     * Returns the bitboard representation of the board
     *
     * @return bitboard
     */
    public int[][] getBitBoard() {
        int[][] bitBoard = new int[CHESS_BOARD.length][CHESS_BOARD[0].length];
        for (int i = 0; i < CHESS_BOARD.length; ++i) {
            for (int j = 0; j < CHESS_BOARD[i].length; ++j) {
                Piece piece = CHESS_BOARD[i][j];
                if (piece == null) {
                    continue;
                }
                bitBoard[i][j] = piece.getBitRepresentation();
            }
        }
        return bitBoard;
    }

    /**
     * Returns the color of the player whose turn it is
     *
     * @return string name of the color
     */
    public String turn() {
        return whiteToPlay ? "White" : "Black";
    }

    public void aiMove() {
        Notation[] move = ai.getBestMove(whiteToPlay);
        movePiece(move[0], move[1]);
    }

    public int evaluate(boolean whiteToPlay) {
        int score = 0;
        for (Notation notation : Notation.values()) {
            Piece piece = getPiece(notation);
            if (piece == null || isEnemy(whiteToPlay ? BLACK : WHITE, notation)) {
                continue;
            }
            score += piece.getScore(notation);
        }
        return score;
    }

    /**
     * Outputs the board in a human-readable format
     *
     * @return string representation of the board
     */
    @Override
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
