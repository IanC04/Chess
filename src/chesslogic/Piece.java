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
            case QUEEN -> possibleQueenMoves();
            case KING -> possibleKingMoves();
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
        byte[] posArr = position.getPosition();

        // If pawn on starting square, can move 2 squares if not blocked
        if (!hasMoved && board.isFree(Notation.get(posArr[0] + direction, posArr[1])) && board.isFree(Notation.get(posArr[0] + 2 * direction, posArr[1]))) {
            moves.add(Notation.get(posArr[0] + 2 * direction, posArr[1]));
        }


        int[][] possible = {{posArr[0] + direction, posArr[1] - 1}, {posArr[0] + direction,
                posArr[1] + 1}};
        for (int[] i : possible) {
            if (board.inBounds(i[0], i[1]) && board.isEnemy(C, Notation.get(i[0], i[1]))) {
                moves.add(Notation.get(i[0], i[1]));
            }
        }
        if (board.inBounds(posArr[0] + direction, posArr[1]) && board.isFree(Notation.get(posArr[0] + direction, +posArr[1]))) {
            moves.add(Notation.get(posArr[0] + direction, posArr[1]));
        }

        return moves;
    }

    private Set<Notation> possibleRookMoves() {
        Set<Notation> moves = new HashSet<>();
        byte[] posArr = position.getPosition();

        // Check squares to the right
        for (int i = posArr[1] + 1; i < 8; ++i) {
            if (board.isFriendly(C, Notation.get(posArr[0], i))) {
                break;
            }
            moves.add(Notation.get(posArr[0], i));
            if (board.isEnemy(C, Notation.get(posArr[0], i))) {
                break;
            }
        }

        // Check squares to the left
        for (int i = posArr[1] - 1; i >= 0; --i) {
            if (board.isFriendly(C, Notation.get(posArr[0], i))) {
                break;
            }
            moves.add(Notation.get(posArr[0], i));
            if (board.isEnemy(C, Notation.get(posArr[0], i))) {
                break;
            }
        }

        // Check squares above
        for (int i = posArr[0] + 1; i < 8; ++i) {
            if (board.isFriendly(C, Notation.get(i, posArr[1]))) {
                break;
            }
            moves.add(Notation.get(i, posArr[1]));
            if (board.isEnemy(C, Notation.get(i, posArr[1]))) {
                break;
            }
        }

        // Check squares below
        for (int i = posArr[0] - 1; i >= 0; --i) {
            if (board.isFriendly(C, Notation.get(i, posArr[1]))) {
                break;
            }
            moves.add(Notation.get(i, posArr[1]));
            if (board.isEnemy(C, Notation.get(i, posArr[1]))) {
                break;
            }
        }

        return moves;
    }

    private Set<Notation> possibleHorseMoves() {
        Set<Notation> moves = new HashSet<>();
        byte[] posArr = position.getPosition();
        int[][] possible = {{posArr[0] + 2, posArr[1] + 1}, {posArr[0] + 1, posArr[1] + 2},
                {posArr[0] - 1, posArr[1] + 2}, {posArr[0] - 2, posArr[1] + 1}, {posArr[0] - 2,
                posArr[1] - 1}, {posArr[0] - 1, posArr[1] - 2}, {posArr[0] + 1, posArr[1] - 2},
                {posArr[0] + 2, posArr[1] - 1}};

        for (int[] i : possible) {
            if (board.inBounds(i[0], i[1]) && !board.isFriendly(C, Notation.get(i[0], i[1]))) {
                moves.add(Notation.get(i[0], i[1]));
            }
        }

        return moves;
    }

    private Set<Notation> possibleBishopMoves() {
        Set<Notation> moves = new HashSet<>();
        byte[] posArr = position.getPosition();

        // Check squares to the upper right
        for (int i = 1; i < 8; ++i) {
            if (!board.inBounds(posArr[0] + i, posArr[1] + i) && board.isFriendly(C,
                    Notation.get(posArr[0] + i, posArr[1] + i))) {
                break;
            }
            moves.add(Notation.get(posArr[0] + i, posArr[1] + i));
            if (board.isEnemy(C, Notation.get(posArr[0] + i, posArr[1] + i))) {
                break;
            }
        }

        // Check squares to the lower right
        for (int i = 1; i < 8; ++i) {
            if (!board.inBounds(posArr[0] - i, posArr[1] + i) && board.isFriendly(C,
                    Notation.get(posArr[0] - i, posArr[1] + i))) {
                break;
            }
            moves.add(Notation.get(posArr[0] - i, posArr[1] + i));
            if (board.isEnemy(C, Notation.get(posArr[0] - i, posArr[1] + i))) {
                break;
            }
        }

        // Check squares to the lower left
        for (int i = 1; i < 8; ++i) {
            if (!board.inBounds(posArr[0] - i, posArr[1] - i) && board.isFriendly(C,
                    Notation.get(posArr[0] - i, posArr[1] - i))) {
                break;
            }
            moves.add(Notation.get(posArr[0] - i, posArr[1] - i));
            if (board.isEnemy(C, Notation.get(posArr[0] - i, posArr[1] - i))) {
                break;
            }
        }

        // Check squares to the upper left
        for (int i = 1; i < 8; ++i) {
            if (!board.inBounds(posArr[0] + i, posArr[1] - i) && board.isFriendly(C,
                    Notation.get(posArr[0] + i, posArr[1] - i))) {
                break;
            }
            moves.add(Notation.get(posArr[0] + i, posArr[1] - i));
            if (board.isEnemy(C, Notation.get(posArr[0] + i, posArr[1] - i))) {
                break;
            }
        }

        return moves;
    }

    private Set<Notation> possibleQueenMoves() {
        Set<Notation> moves = new HashSet<>();
        moves.addAll(possibleRookMoves());
        moves.addAll(possibleBishopMoves());

        return moves;
    }

    private Set<Notation> possibleKingMoves() {
        Set<Notation> moves = new HashSet<>();
        byte[] posArr = position.getPosition();
        int[][] possible = {{posArr[0] + 1, posArr[1]}, {posArr[0] + 1, posArr[1] + 1},
                {posArr[0], posArr[1] + 1}, {posArr[0] - 1, posArr[1] + 1}, {posArr[0] - 1,
                posArr[1]}, {posArr[0] - 1, posArr[1] - 1}, {posArr[0], posArr[1] - 1},
                {posArr[0] + 1, posArr[1] - 1}};

        for (int[] i : possible) {
            if (board.inBounds(i[0], i[1]) && !board.isFriendly(C, Notation.get(i[0], i[1]))) {
                moves.add(Notation.get(i[0], i[1]));
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