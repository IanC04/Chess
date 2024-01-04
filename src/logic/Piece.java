package logic;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static logic.Piece.PieceColor.*;

public record Piece(PieceColor C, PieceType T, Board board, MutablePieceState mutablePieceState) {
    enum PieceType {
        PAWN, ROOK, HORSE, BISHOP, QUEEN, KING
    }


    enum PieceColor {
        WHITE, BLACK
    }

    /**
     * Mutable state of the piece
     */
    private static class MutablePieceState {

        private int lastMove;

        private boolean pawnDoubleMove;

        MutablePieceState() {
            this(-1, false);
        }

        MutablePieceState(int lastMove, boolean pawnDoubleMove) {
            this.lastMove = lastMove;
            this.pawnDoubleMove = pawnDoubleMove;
        }

        MutablePieceState copy() {
            return new MutablePieceState(lastMove, pawnDoubleMove);
        }

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
        this(C, T, board, new MutablePieceState());
    }

    Piece copy(Board newBoard) {
        return new Piece(C, T, newBoard, this.mutablePieceState.copy());
    }

    void moved(int turn) {
        if (mutablePieceState.lastMove >= turn) {
            throw new IllegalStateException("Piece has already moved in the future?");
        }
        mutablePieceState.setLastMove(turn);
    }

    void setDoubleMove() {
        mutablePieceState.setPawnDoubleMove(true);
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
        if (mutablePieceState.lastMove == -1 && board.isFree(Notation.get(posArr[0] + direction,
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
                    if (piece.T == PieceType.PAWN && piece.mutablePieceState.lastMove == board.getTurn() - 1 && piece.mutablePieceState.pawnDoubleMove) {
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
        if (mutablePieceState.lastMove == -1) {
            boolean canCastleLeft = canCastleLeft(pos);
            boolean canCastleRight = canCastleRight(pos);
            if (canCastleLeft) {
                Move move = new Move(pos, Notation.get(posArr[0], 2), Move.MoveType.CASTLE);
                moves.add(move);
            }
            if (canCastleRight) {
                Move move = new Move(pos, Notation.get(posArr[0], 6), Move.MoveType.CASTLE);
                moves.add(move);
            }
        }

        return moves;
    }

    private boolean canCastleLeft(Notation pos) {
        byte[] posArr = pos.getPosition();
        if (board.getPiece(posArr[0], 0) != null && board.getPiece(posArr[0], 0).T == PieceType.ROOK && board.getPiece(posArr[0], 0).mutablePieceState.lastMove == -1) {
            if (board().inCheck(C == WHITE)) {
                return false;
            }
            for (int j = 2; j < posArr[1]; ++j) {
                if (board.getPiece(posArr[0], j) != null || dangerSquareForKing(Notation.get(posArr[0], j))) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    private boolean canCastleRight(Notation pos) {
        byte[] posArr = pos.getPosition();
        if (board.getPiece(posArr[0], 7) != null && board.getPiece(posArr[0], 7).T == PieceType.ROOK && board.getPiece(posArr[0], 7).mutablePieceState.lastMove == -1) {
            if (board().inCheck(C == WHITE)) {
                return false;
            }
            for (int j = posArr[1] + 1; j < 7; ++j) {
                if (board.getPiece(posArr[0], j) != null || dangerSquareForKing(Notation.get(posArr[0], j))) {
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
    private boolean dangerSquareForKing(Notation pos) {
        byte[] posArr = pos.getPosition();
        boolean danger = false;
        for (Notation enemyPos : Notation.values()) {
            if (board.isEnemy(C, enemyPos)) {
                Piece piece = board.getPiece(enemyPos);
                byte[] enemyPosArr = enemyPos.getPosition();
                danger = switch (piece.getType()) {
                    case PAWN -> {
                        int direction = piece.getColor() == WHITE ? 1 : -1;
                        boolean pawnDanger =
                                posArr[0] == enemyPosArr[0] + direction && Math.abs(posArr[1] - enemyPosArr[1]) == 1;
                        yield pawnDanger;
                    }
                    case KING -> {
                        boolean kingDanger =
                                Math.abs(posArr[0] - enemyPosArr[0]) <= 1 && Math.abs(posArr[1] - enemyPosArr[1]) <= 1;
                        yield kingDanger;
                    }
                    default -> {
                        boolean otherDanger = piece.possibleMoves(enemyPos).
                                stream().map(Move::end).collect(Collectors.toSet()).contains(pos);
                        yield otherDanger;
                    }
                };
                if (danger) {
                    return true;
                }
            }

        }
        return false;
    }

    int getScore(Notation pos) {
        int positionalValue =
                Math.abs(7 - pos.getPosition()[0]) + Math.abs(7 - pos.getPosition()[1]);
        int materialValue = switch (T) {
            case PAWN -> 1;
            case HORSE -> 3;
            case BISHOP -> 3;
            case ROOK -> 5;
            case QUEEN -> 9;
            case KING -> 100;
        };
        int gameValue = switch (board.gameStatus()) {
            case 0 -> 0;
            case 1 -> -1000;
            case 2 -> 0;
            case 3 -> -10000;
            default -> throw new IllegalStateException("Unexpected value: " + board.gameStatus());
        };
        return positionalValue + materialValue + gameValue;
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