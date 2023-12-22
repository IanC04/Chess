package logic;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static logic.Piece.PieceColor.*;

public record Piece(PieceColor C, PieceType T, Board board, MutableState mutableState) {
    enum PieceType {
        PAWN, ROOK, HORSE, BISHOP, QUEEN, KING
    }


    enum PieceColor {
        WHITE, BLACK
    }

    /**
     * Mutable state of the piece
     */
    private static class MutableState {

        private int lastMove = -1;

        private boolean pawnDoubleMove = false;

        public void setLastMove(int turn) {
            this.lastMove = turn;
        }

        public void setPawnDoubleMove(boolean pawnDoubleMove) {
            if (this.pawnDoubleMove) {
                throw new IllegalStateException("Pawn has already moved twice");
            }
            this.pawnDoubleMove = pawnDoubleMove;
        }
    }

    Piece(PieceColor C, PieceType T, Board board) {
        this(C, T, board, new MutableState());
    }

    void moved(int turn) {
        if (mutableState.lastMove >= turn) {
            throw new IllegalStateException("Piece has already moved in the future?");
        }
        mutableState.setLastMove(turn);
    }

    void setDoubleMove() {
        mutableState.setPawnDoubleMove(true);
    }

    public boolean isWhite() {
        return C.equals(WHITE);
    }

    PieceColor getColor() {
        return C;
    }

    PieceType getType() {
        return T;
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


    Set<Move> possibleMoves(Notation pos) {
        if (board.getPiece(pos) == null) {
            throw new IllegalArgumentException("No piece at " + pos);
        }
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
     *
     * @return set of possible moves
     */
    private Set<Move> possiblePawnMoves(Notation pos) {
        Set<Move> moves = new HashSet<>();
        int direction = C == WHITE ? 1 : -1;
        byte[] posArr = pos.getPosition();

        // If pawn on starting square, can move 2 squares if not blocked
        if (mutableState.lastMove == -1 && board.isFree(Notation.get(posArr[0] + direction,
                posArr[1])) && board.isFree(Notation.get(posArr[0] + 2 * direction, posArr[1]))) {
            Move move = new Move(pos, Notation.get(posArr[0] + 2 * direction, posArr[1]));
            moves.add(move);
        }

        // Capture diagonally
        int[][] possible = {{posArr[0] + direction, posArr[1] - 1}, {posArr[0] + direction,
                posArr[1] + 1}};
        for (int[] i : possible) {
            if (Board.inBounds(i[0], i[1]) && board.isEnemy(C, Notation.get(i[0], i[1]))) {
                Move move = new Move(pos, Notation.get(i[0], i[1]), (i[0] == 0 || i[0] == 7) ? Move.MoveType.PROMOTION : Move.MoveType.NORMAL);
                moves.add(move);
            }
        }
        // Move forward
        if (Board.inBounds(posArr[0] + direction, posArr[1]) && board.isFree(Notation.get(posArr[0] + direction, +posArr[1]))) {
            Move move = new Move(pos, Notation.get(posArr[0] + direction, posArr[1]),
                    (posArr[0] + direction == 0 || posArr[0] + direction == 7) ? Move.MoveType.PROMOTION : Move.MoveType.NORMAL);
            moves.add(move);
        }

        // En-passant
        if (posArr[0] == (C == WHITE ? 4 : 3)) {
            int[][] possibleEnPassant = {{posArr[0], posArr[1] - 1}, {posArr[0], posArr[1] + 1}};
            for (int[] i : possibleEnPassant) {
                if (Board.inBounds(i[0], i[1]) && board.isEnemy(C, Notation.get(i[0], i[1]))) {
                    Piece piece = board.getPiece(i[0], i[1]);
                    if (piece.T == PieceType.PAWN && piece.mutableState.lastMove == board.getTurn() - 1 && piece.mutableState.pawnDoubleMove) {
                        Move move = new Move(pos, Notation.get(posArr[0] + direction, i[1]), Move.MoveType.EN_PASSANT);
                        moves.add(move);
                    }
                }
            }
        }

        return moves;
    }

    /**
     * Get all possible moves for a rook
     *
     * @return set of possible moves
     */
    private Set<Move> possibleRookMoves(Notation pos) {
        Set<Move> moves = new HashSet<>();
        byte[] posArr = pos.getPosition();

        // Check squares to the right
        for (int i = posArr[1] + 1; i < 8; ++i) {
            if (board.isFriendly(C, Notation.get(posArr[0], i))) {
                break;
            }
            Move move = new Move(pos, Notation.get(posArr[0], i));
            moves.add(move);
            if (board.isEnemy(C, Notation.get(posArr[0], i))) {
                break;
            }
        }

        // Check squares to the left
        for (int i = posArr[1] - 1; i >= 0; --i) {
            if (board.isFriendly(C, Notation.get(posArr[0], i))) {
                break;
            }
            Move move = new Move(pos, Notation.get(posArr[0], i));
            moves.add(move);
            if (board.isEnemy(C, Notation.get(posArr[0], i))) {
                break;
            }
        }

        // Check squares above
        for (int i = posArr[0] + 1; i < 8; ++i) {
            if (board.isFriendly(C, Notation.get(i, posArr[1]))) {
                break;
            }
            Move move = new Move(pos, Notation.get(i, posArr[1]));
            moves.add(move);
            if (board.isEnemy(C, Notation.get(i, posArr[1]))) {
                break;
            }
        }

        // Check squares below
        for (int i = posArr[0] - 1; i >= 0; --i) {
            if (board.isFriendly(C, Notation.get(i, posArr[1]))) {
                break;
            }
            Move move = new Move(pos, Notation.get(i, posArr[1]));
            moves.add(move);
            if (board.isEnemy(C, Notation.get(i, posArr[1]))) {
                break;
            }
        }

        return moves;
    }

    /**
     * Get all possible moves for a horse
     *
     * @param pos position of the horse
     * @return set of possible moves
     */
    private Set<Move> possibleHorseMoves(Notation pos) {
        Set<Move> moves = new HashSet<>();
        byte[] posArr = pos.getPosition();
        int[][] possible = {{posArr[0] + 2, posArr[1] + 1}, {posArr[0] + 1, posArr[1] + 2},
                {posArr[0] - 1, posArr[1] + 2}, {posArr[0] - 2, posArr[1] + 1}, {posArr[0] - 2,
                posArr[1] - 1}, {posArr[0] - 1, posArr[1] - 2}, {posArr[0] + 1, posArr[1] - 2},
                {posArr[0] + 2, posArr[1] - 1}};

        for (int[] i : possible) {
            if (Board.inBounds(i[0], i[1]) && !board.isFriendly(C, Notation.get(i[0], i[1]))) {
                Move move = new Move(pos, Notation.get(i[0], i[1]));
                moves.add(move);
            }
        }

        return moves;
    }

    /**
     * Get all possible moves for a bishop
     *
     * @param pos position of the bishop
     * @return set of possible moves
     */
    private Set<Move> possibleBishopMoves(Notation pos) {
        Set<Move> moves = new HashSet<>();
        byte[] posArr = pos.getPosition();

        // Check squares to the upper right
        for (int i = 1; i < 8; ++i) {
            if (!Board.inBounds(posArr[0] + i, posArr[1] + i) || board.isFriendly(C,
                    Notation.get(posArr[0] + i, posArr[1] + i))) {
                break;
            }
            Move move = new Move(pos, Notation.get(posArr[0] + i, posArr[1] + i));
            moves.add(move);
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
            Move move = new Move(pos, Notation.get(posArr[0] - i, posArr[1] + i));
            moves.add(move);
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
            Move move = new Move(pos, Notation.get(posArr[0] - i, posArr[1] - i));
            moves.add(move);
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
            Move move = new Move(pos, Notation.get(posArr[0] + i, posArr[1] - i));
            moves.add(move);
            if (board.isEnemy(C, Notation.get(posArr[0] + i, posArr[1] - i))) {
                break;
            }
        }

        return moves;
    }

    /**
     * Get all possible moves for a queen, which is just the union of the possible moves for a rook and a bishop
     *
     * @param pos position of the queen
     * @return set of possible moves
     */
    private Set<Move> possibleQueenMoves(Notation pos) {
        Set<Move> moves = new HashSet<>();
        moves.addAll(possibleRookMoves(pos));
        moves.addAll(possibleBishopMoves(pos));

        return moves;
    }

    /**
     * Get all possible moves for a king
     *
     * @param pos position of the king
     * @return set of possible moves
     */
    private Set<Move> possibleKingMoves(Notation pos) {
        Set<Move> moves = new HashSet<>();
        byte[] posArr = pos.getPosition();
        int[][] possible = {{posArr[0] + 1, posArr[1]}, {posArr[0] + 1, posArr[1] + 1},
                {posArr[0], posArr[1] + 1}, {posArr[0] - 1, posArr[1] + 1}, {posArr[0] - 1,
                posArr[1]}, {posArr[0] - 1, posArr[1] - 1}, {posArr[0], posArr[1] - 1},
                {posArr[0] + 1, posArr[1] - 1}};

        for (int[] i : possible) {
            if (!Board.inBounds(i[0], i[1])) {
                continue;
            }
            Notation notation = Notation.get(i[0], i[1]);
            if (!board.isFriendly(C, notation) && !dangerSquareForKing(notation)) {
                Move move = new Move(pos, Notation.get(i[0], i[1]));
                moves.add(move);
            }
        }

        // Castling
        if (mutableState.lastMove == -1) {
            if (canCastleLeft(pos)) {
                Move move = new Move(pos, Notation.get(posArr[0], 2), Move.MoveType.CASTLE);
                moves.add(move);
            }
            if (canCastleRight(pos)) {
                Move move = new Move(pos, Notation.get(posArr[0], 6), Move.MoveType.CASTLE);
                moves.add(move);
            }
        }

        return moves;
    }

    private boolean canCastleLeft(Notation pos) {
        byte[] posArr = pos.getPosition();
        if (board.getPiece(posArr[0], 0) != null && board.getPiece(posArr[0], 0).T == PieceType.ROOK && board.getPiece(posArr[0], 0).mutableState.lastMove == -1) {
            for (int i = 2; i <= posArr[1]; ++i) {
                if (board.getPiece(posArr[0], i) != null || dangerSquareForKing(Notation.get(posArr[0], i))) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    private boolean canCastleRight(Notation pos) {
        byte[] posArr = pos.getPosition();
        if (board.getPiece(posArr[0], 7) != null && board.getPiece(posArr[0], 7).T == PieceType.ROOK && board.getPiece(posArr[0], 7).mutableState.lastMove == -1) {
            for (int i = posArr[1]; i < 7; ++i) {
                if (board.getPiece(posArr[0], i) != null || dangerSquareForKing(Notation.get(posArr[0], i))) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    /**
     * Returns whether the square is safe for the king
     *
     * @param pos position of the square to check
     * @return whether the square is safe for the king
     */
    public boolean dangerSquareForKing(Notation pos) {
        byte[] posArr = pos.getPosition();
        boolean danger = false;
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                if (board.isEnemy(C, Notation.get(i, j))) {
                    Piece piece = board.getPiece(i, j);
                    danger = switch (piece.getType()) {
                        case PAWN -> {
                            int direction = piece.getColor() == WHITE ? 1 : -1;
                            if (posArr[0] == i + direction && Math.abs(posArr[1] - j) == 1) {
                                System.out.println(piece);
                                yield true;
                            }
                            yield false;
                        }
                        case KING -> Math.abs(posArr[0] - i) <= 1 && Math.abs(posArr[1] - j) <= 1;
                        default -> board.getPiece(i, j).possibleMoves(Notation.get(i, j)).
                                stream().map(Move::end).collect(Collectors.toSet()).contains(pos);
                    };
                    if (danger) {
                        return true;
                    }
                }
            }
        }
        return danger;
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