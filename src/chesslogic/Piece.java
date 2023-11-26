package chesslogic;

import java.util.HashSet;
import java.util.Set;

import static chesslogic.Notation.*;
import static chesslogic.Piece.PieceColor.*;

public class Piece {
    enum Type {
        PAWN, ROOK, HORSE, BISHOP, QUEEN, KING
    }

    enum PieceColor {
        WHITE, BLACK
    }

    Notation position;

    PieceColor C;
    Type T;

    /**
     * Reference to the board this piece is on
     */
    private Board board;

    private boolean hasMoved;

    Piece(PieceColor c, Type t, Board b) {
        C = c;
        T = t;
        board = b;
        hasMoved = false;
    }

    boolean setPosition(Notation newPos) {
        if (newPos == this.position) {
            return false;
        }
        this.position = newPos;
        hasMoved = true;
        return true;
    }

    Set<Notation> possibleMoves() {
        return switch (T) {
            case PAWN -> possiblePawnMoves();
            case ROOK -> possibleRookMoves();
            case HORSE -> possibleHorseMoves();
            case BISHOP -> possibleBishopMoves();
            case QUEEN -> queenMoves();
            case KING -> kingMoves();
        };
    }

    /**
     * Get all possible moves for a pawn
     *
     * @return
     */
    private Set<Notation> possiblePawnMoves() {
        Set<Notation> moves = new HashSet<>();
        int direction = C == WHITE ? 1 : -1;
        int pos = position.getPosition();

        // If pawn on starting square, can move 2 squares if not blocked
        if (!hasMoved && board.isFree(Notation.get(pos + 8 * direction)) && board.isFree(Notation.get(pos + 16 * direction))) {
            moves.add(Notation.get(pos + 16 * direction));
        }


        int[] possible = {pos + 7 * direction, pos + 9 * direction};
        for (int i : possible) {
            if (board.inBounds(i) && board.isEnemy(C, Notation.get(i))) {
                moves.add(Notation.get(i));
            }
        }
        if (board.inBounds(pos + 8 * direction) && board.isFree(Notation.get(pos + 8 * direction))) {
            moves.add(Notation.get(pos + 8 * direction));
        }

        return moves;
    }

    private Set<Notation> possibleRookMoves() {
        Set<Notation> moves = new HashSet<>();
        int pos = position.getPosition();

        // Check squares to the right
        for (int i = pos + 1; i % 8 != 0; i++) {
            if (board.isFriendly(C, Notation.get(i))) {
                break;
            }
            moves.add(Notation.get(i));
            if (board.isEnemy(C, Notation.get(i))) {
                break;
            }
        }

        // Check squares to the left
        for (int i = pos - 1; i % 8 != 7; i--) {
            if (board.isFriendly(C, Notation.get(i))) {
                break;
            }
            moves.add(Notation.get(i));
            if (board.isEnemy(C, Notation.get(i))) {
                break;
            }
        }

        // Check squares above
        for (int i = pos + 8; i < 64; i += 8) {
            if (board.isFriendly(C, Notation.get(i))) {
                break;
            }
            moves.add(Notation.get(i));
            if (board.isEnemy(C, Notation.get(i))) {
                break;
            }
        }

        // Check squares below
        for (int i = pos - 8; i >= 0; i -= 8) {
            if (board.isFriendly(C, Notation.get(i))) {
                break;
            }
            moves.add(Notation.get(i));
            if (board.isEnemy(C, Notation.get(i))) {
                break;
            }
        }

        return moves;
    }

    private Set<Notation> possibleHorseMoves() {
        Set<Notation> moves = new HashSet<>();
        int pos = position.getPosition();
        int[] possible = {pos + 17, pos + 10, pos - 6, pos - 15, pos - 17, pos - 10, pos + 6,
                pos + 15};

        for (int i : possible) {
            if (board.inBounds(i) && !board.isFriendly(C, Notation.get(i))) {
                moves.add(Notation.get(i));
            }
        }

        return moves;
    }

    private Set<Notation> possibleBishopMoves() {
        Set<Notation> moves = new HashSet<>();
        int pos = position.getPosition();

        // Check squares to the upper right
        for (int i = pos + 9; i % 8 != 0 && i < 64; i += 9) {
            if (board.isFriendly(C, Notation.get(i))) {
                break;
            }
            moves.add(Notation.get(i));
            if (board.isEnemy(C, Notation.get(i))) {
                break;
            }
        }

        // Check squares to the lower right
        for (int i = pos - 7; i % 8 != 0 && i >= 0; i -= 7) {
            if (board.isFriendly(C, Notation.get(i))) {
                break;
            }
            moves.add(Notation.get(i));
            if (board.isEnemy(C, Notation.get(i))) {
                break;
            }
        }

        // Check squares to the lower left
        for (int i = pos - 9; i % 8 != 7 && i >= 0; i -= 9) {
            if (board.isFriendly(C, Notation.get(i))) {
                break;
            }
            moves.add(Notation.get(i));
            if (board.isEnemy(C, Notation.get(i))) {
                break;
            }
        }

        // Check squares to the upper left
        for (int i = pos + 7; i % 8 != 7 && i < 64; i += 7) {
            if (board.isFriendly(C, Notation.get(i))) {
                break;
            }
            moves.add(Notation.get(i));
            if (board.isEnemy(C, Notation.get(i))) {
                break;
            }
        }

        return moves;
    }

    public String toString() {
        return String.format("%s%s@%s", C == WHITE ? "W" : "B", switch (T) {
            case PAWN -> "P";
            case ROOK -> "R";
            case HORSE -> "H";
            case BISHOP -> "B";
            case QUEEN -> "Q";
            case KING -> "K";
        }, position);
    }
}