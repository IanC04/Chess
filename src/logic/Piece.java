package logic;

import java.util.HashSet;
import java.util.Set;

import static logic.Piece.PieceColor.*;

public class Piece {
    enum Type {
        PAWN, ROOK, HORSE, BISHOP, QUEEN, KING
    }

    enum PieceColor {
        WHITE, BLACK
    }

    private final PieceColor C;
    private final Type T;

    /**
     * Reference to the board this piece is on
     */
    private final Board board;

    private boolean hasMoved;

    Piece(PieceColor c, Type t, Board b) {
        C = c;
        T = t;
        board = b;
        hasMoved = false;
    }

    void moved() {
        this.hasMoved = true;
    }

    public boolean isWhite() {
        return C.equals(WHITE);
    }

    PieceColor getColor() {
        return C;
    }

    /**
     * Returns the bit representation of this piece
     *
     * @return bit representation
     */
    public int getBitRepresentation() {
        return switch (T) {
            case PAWN -> C == WHITE ? 0b0001 : 0b1001;
            case ROOK -> C == WHITE ? 0b0010 : 0b1010;
            case HORSE -> C == WHITE ? 0b0011 : 0b1011;
            case BISHOP -> C == WHITE ? 0b0100 : 0b1100;
            case QUEEN -> C == WHITE ? 0b0101 : 0b1101;
            case KING -> C == WHITE ? 0b0110 : 0b1110;
        };
    }


    Set<Notation> possibleMoves(Notation pos) {
        return switch (T) {
            case PAWN -> possiblePawnMoves(pos);
            case ROOK -> possibleRookMoves(pos);
            case HORSE -> possibleHorseMoves(pos);
            case BISHOP -> possibleBishopMoves(pos);
            case QUEEN -> possibleQueenMoves(pos);
            case KING -> possibleKingMoves(pos);
        };
    }

    /**
     * Get all possible moves for a pawn
     * TODO: Implement en passant
     *
     * @return set of possible moves
     */
    private Set<Notation> possiblePawnMoves(Notation pos) {
        Set<Notation> moves = new HashSet<>();
        int direction = C == WHITE ? 1 : -1;
        byte[] posArr = pos.getPosition();

        // If pawn on starting square, can move 2 squares if not blocked
        if (!hasMoved && board.isFree(Notation.get(posArr[0] + direction, posArr[1])) && board.isFree(Notation.get(posArr[0] + 2 * direction, posArr[1]))) {
            moves.add(Notation.get(posArr[0] + 2 * direction, posArr[1]));
        }


        int[][] possible = {{posArr[0] + direction, posArr[1] - 1}, {posArr[0] + direction,
                posArr[1] + 1}};
        for (int[] i : possible) {
            if (Board.inBounds(i[0], i[1]) && board.isEnemy(C, Notation.get(i[0], i[1]))) {
                moves.add(Notation.get(i[0], i[1]));
            }
        }
        if (Board.inBounds(posArr[0] + direction, posArr[1]) && board.isFree(Notation.get(posArr[0] + direction, +posArr[1]))) {
            moves.add(Notation.get(posArr[0] + direction, posArr[1]));
        }

        return moves;
    }

    /**
     * Get all possible moves for a rook
     * TODO: Implement castling
     *
     * @return set of possible moves
     */
    private Set<Notation> possibleRookMoves(Notation pos) {
        Set<Notation> moves = new HashSet<>();
        byte[] posArr = pos.getPosition();

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

    private Set<Notation> possibleHorseMoves(Notation pos) {
        Set<Notation> moves = new HashSet<>();
        byte[] posArr = pos.getPosition();
        int[][] possible = {{posArr[0] + 2, posArr[1] + 1}, {posArr[0] + 1, posArr[1] + 2},
                {posArr[0] - 1, posArr[1] + 2}, {posArr[0] - 2, posArr[1] + 1}, {posArr[0] - 2,
                posArr[1] - 1}, {posArr[0] - 1, posArr[1] - 2}, {posArr[0] + 1, posArr[1] - 2},
                {posArr[0] + 2, posArr[1] - 1}};

        for (int[] i : possible) {
            if (Board.inBounds(i[0], i[1]) && !board.isFriendly(C, Notation.get(i[0], i[1]))) {
                moves.add(Notation.get(i[0], i[1]));
            }
        }

        return moves;
    }

    private Set<Notation> possibleBishopMoves(Notation pos) {
        Set<Notation> moves = new HashSet<>();
        byte[] posArr = pos.getPosition();

        // Check squares to the upper right
        for (int i = 1; i < 8; ++i) {
            if (!Board.inBounds(posArr[0] + i, posArr[1] + i) || board.isFriendly(C,
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
            if (!Board.inBounds(posArr[0] - i, posArr[1] + i) || board.isFriendly(C,
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
            if (!Board.inBounds(posArr[0] - i, posArr[1] - i) || board.isFriendly(C,
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
            if (!Board.inBounds(posArr[0] + i, posArr[1] - i) || board.isFriendly(C,
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

    private Set<Notation> possibleQueenMoves(Notation pos) {
        Set<Notation> moves = new HashSet<>();
        moves.addAll(possibleRookMoves(pos));
        moves.addAll(possibleBishopMoves(pos));

        return moves;
    }

    private Set<Notation> possibleKingMoves(Notation pos) {
        Set<Notation> moves = new HashSet<>();
        byte[] posArr = pos.getPosition();
        int[][] possible = {{posArr[0] + 1, posArr[1]}, {posArr[0] + 1, posArr[1] + 1},
                {posArr[0], posArr[1] + 1}, {posArr[0] - 1, posArr[1] + 1}, {posArr[0] - 1,
                posArr[1]}, {posArr[0] - 1, posArr[1] - 1}, {posArr[0], posArr[1] - 1},
                {posArr[0] + 1, posArr[1] - 1}};

        for (int[] i : possible) {
            if (Board.inBounds(i[0], i[1]) && !board.isFriendly(C, Notation.get(i[0], i[1]))) {
                moves.add(Notation.get(i[0], i[1]));
            }
        }

        return moves;
    }

    int getScore(Notation pos) {
        return Math.abs(7 - pos.getPosition()[0]) + Math.abs(7 - pos.getPosition()[1]);
    }

    /**
     * Returns the unicode character for this piece
     *
     * @return unicode character
     */
    public char getUnicode() {
        return switch (T) {
            case PAWN -> C == WHITE ? '♙' : '♟';
            case ROOK -> C == WHITE ? '♖' : '♜';
            case HORSE -> C == WHITE ? '♘' : '♞';
            case BISHOP -> C == WHITE ? '♗' : '♝';
            case QUEEN -> C == WHITE ? '♕' : '♛';
            case KING -> C == WHITE ? '♔' : '♚';
        };
    }


    /**
     * Returns the piece represented by the bit representation
     *
     * @param bitRepresentation bit representation specified in Piece class
     * @return unicode character
     */
    public static char getPiece(int bitRepresentation) {
        boolean isWhite = (bitRepresentation & 0b1000) == 0;
        return switch (bitRepresentation) {
            case 0b0001, 0b1001 -> isWhite ? '♙' : '♟';
            case 0b0010, 0b1010 -> isWhite ? '♖' : '♜';
            case 0b0011, 0b1011 -> isWhite ? '♘' : '♞';
            case 0b0100, 0b1100 -> isWhite ? '♗' : '♝';
            case 0b0101, 0b1101 -> isWhite ? '♕' : '♛';
            case 0b0110, 0b1110 -> isWhite ? '♔' : '♚';
            default ->
                    throw new IllegalArgumentException("Invalid bit representation: " + Integer.toBinaryString(bitRepresentation));
        };
    }

    @Override
    public String toString() {
        return Character.toString(getUnicode());
    }
}