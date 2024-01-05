package logic;

import java.util.*;
import java.util.stream.Collectors;

import static logic.Piece.PieceColor.*;

public record Piece(PieceColor C, PieceType T, Board board, State state) {
    enum PieceType {
        PAWN, ROOK, KNIGHT, BISHOP, QUEEN, KING
    }


    enum PieceColor {
        WHITE, BLACK
    }

    /**
     * Mutable state of the piece
     */
    private static class State {

        private final List<Integer> turns;

        State() {
            this.turns = new ArrayList<>();
        }

        State(List<Integer> turnList) {
            this.turns = new ArrayList<>(turnList);
        }

        State copy() {
            return new State(turns);
        }

        private void addMove(int turn) {
            turns.add(turn);
        }
    }

    Piece(PieceColor C, PieceType T, Board board) {
        this(C, T, board, new State());
    }

    Piece copy(Board newBoard) {
        return new Piece(C, T, newBoard, this.state.copy());
    }

    void moved(int turn) {
        if (state.turns.stream().anyMatch(i -> i >= turn)) {
            throw new IllegalStateException("Piece has already moved in the future?");
        }

        state.addMove(turn);
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
            case KNIGHT -> C == WHITE ? 0b0011 : 0b1011;
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
            case PAWN -> possiblePawnMoves(board, this, pos);
            case ROOK -> possibleRookMoves(board, this, pos);
            case KNIGHT -> possibleHorseMoves(board, this, pos);
            case BISHOP -> possibleBishopMoves(board, this, pos);
            case QUEEN -> possibleQueenMoves(board, this, pos);
            case KING -> possibleKingMoves(board, this, pos);
        };
    }

    /**
     * Get all possible moves for a pawn
     *
     * @return set of possible moves
     */
    private static Set<Move> possiblePawnMoves(Board board, Piece piece, Notation pos) {
        Set<Move> moves = new HashSet<>();
        int direction = piece.C == WHITE ? 1 : -1;
        byte[] posArr = pos.getPosition();

        // Move single forward
        if (Board.inBounds(posArr[0] + direction, posArr[1]) && board.isFree(Notation.get(posArr[0] + direction, posArr[1]))) {
            Move move = new Move(pos, Notation.get(posArr[0] + direction, posArr[1]),
                    (posArr[0] + direction == 0 || posArr[0] + direction == 7) ? Move.MoveType.PROMOTION : Move.MoveType.NORMAL);
            moves.add(move);

            // If pawn on starting square, can move 2 squares if not blocked
            if (piece.state.turns.isEmpty() && board.isFree(Notation.get(posArr[0] + 2 * direction, posArr[1]))) {
                move = new Move(pos, Notation.get(posArr[0] + 2 * direction, posArr[1]));
                moves.add(move);
            }
        }

        // Capture diagonally
        int newRow = posArr[0] + direction;
        if (newRow >= 0 && newRow < 8) {
            int newColumn = posArr[1] - 1;
            if (newColumn >= 0 && board.isEnemy(piece.C, Notation.get(newRow, newColumn))) {
                Notation notation = Notation.get(newRow, newColumn);
                if (Board.inBounds(notation) && board.isEnemy(piece.C, notation)) {
                    Move.MoveType moveType = switch (newRow) {
                        case 0, 7 -> Move.MoveType.PROMOTION;
                        default -> Move.MoveType.NORMAL;
                    };
                    Move move = new Move(pos, notation, moveType);
                    moves.add(move);
                }
            }

            newColumn = posArr[1] + 1;
            if (newColumn < 8 && board.isEnemy(piece.C, Notation.get(newRow, newColumn))) {
                Notation notation = Notation.get(newRow, newColumn);
                if (Board.inBounds(notation) && board.isEnemy(piece.C, notation)) {
                    Move.MoveType moveType = switch (newRow) {
                        case 0, 7 -> Move.MoveType.PROMOTION;
                        default -> Move.MoveType.NORMAL;
                    };
                    Move move = new Move(pos, notation, moveType);
                    moves.add(move);
                }
            }
        }

        // En-passant
        if (posArr[0] == (piece.C == WHITE ? 4 : 3)) {
            int newColumn = posArr[1] - 1;
            if (newColumn >= 0 && board.isEnemy(piece.C, Notation.get(posArr[0], newColumn))) {
                Piece enemyPiece = board.getPiece(posArr[0], newColumn);
                // TODO: Fix en passant
                boolean canEnPassant =
                        enemyPiece.T == PieceType.PAWN && enemyPiece.state.turns.size() == 1 && enemyPiece.state.turns.get(0) == board.getTurn() - 1;
                if (canEnPassant) {
                    Move move = new Move(pos, Notation.get(posArr[0] + direction, newColumn),
                            Move.MoveType.EN_PASSANT);
                    moves.add(move);
                }
            }

            newColumn = posArr[1] + 1;
            if (newColumn < 8 && board.isEnemy(piece.C, Notation.get(posArr[0], newColumn))) {
                Piece enemyPiece = board.getPiece(posArr[0], newColumn);
                // TODO: Fix en passant
                boolean canEnPassant =
                        enemyPiece.T == PieceType.PAWN && enemyPiece.state.turns.size() == 1 && enemyPiece.state.turns.get(0) == board.getTurn() - 1;
                if (canEnPassant) {
                    Move move = new Move(pos, Notation.get(posArr[0] + direction, newColumn),
                            Move.MoveType.EN_PASSANT);
                    moves.add(move);
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
    private static Set<Move> possibleRookMoves(Board board, Piece piece, Notation pos) {
        Set<Move> moves = new HashSet<>();
        byte[] posArr = pos.getPosition();

        // Check squares to the right
        for (int i = posArr[1] + 1; i < 8; ++i) {
            if (board.isFriendly(piece.C, Notation.get(posArr[0], i))) {
                break;
            }
            Move move = new Move(pos, Notation.get(posArr[0], i));
            moves.add(move);
            if (board.isEnemy(piece.C, Notation.get(posArr[0], i))) {
                break;
            }
        }

        // Check squares to the left
        for (int i = posArr[1] - 1; i >= 0; --i) {
            if (board.isFriendly(piece.C, Notation.get(posArr[0], i))) {
                break;
            }
            Move move = new Move(pos, Notation.get(posArr[0], i));
            moves.add(move);
            if (board.isEnemy(piece.C, Notation.get(posArr[0], i))) {
                break;
            }
        }

        // Check squares above
        for (int i = posArr[0] + 1; i < 8; ++i) {
            if (board.isFriendly(piece.C, Notation.get(i, posArr[1]))) {
                break;
            }
            Move move = new Move(pos, Notation.get(i, posArr[1]));
            moves.add(move);
            if (board.isEnemy(piece.C, Notation.get(i, posArr[1]))) {
                break;
            }
        }

        // Check squares below
        for (int i = posArr[0] - 1; i >= 0; --i) {
            if (board.isFriendly(piece.C, Notation.get(i, posArr[1]))) {
                break;
            }
            Move move = new Move(pos, Notation.get(i, posArr[1]));
            moves.add(move);
            if (board.isEnemy(piece.C, Notation.get(i, posArr[1]))) {
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
    private static Set<Move> possibleHorseMoves(Board board, Piece piece, Notation pos) {
        Set<Move> moves = new HashSet<>();
        byte[] posArr = pos.getPosition();

        int[][] possible = {{posArr[0] + 2, posArr[1] + 1}, {posArr[0] + 1, posArr[1] + 2},
                {posArr[0] - 1, posArr[1] + 2}, {posArr[0] - 2, posArr[1] + 1}, {posArr[0] - 2,
                posArr[1] - 1}, {posArr[0] - 1, posArr[1] - 2}, {posArr[0] + 1, posArr[1] - 2},
                {posArr[0] + 2, posArr[1] - 1}};

        for (int[] i : possible) {
            if (Board.inBounds(i[0], i[1]) && !board.isFriendly(piece.C,
                    Notation.get(i[0], i[1]))) {
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
    private static Set<Move> possibleBishopMoves(Board board, Piece piece, Notation pos) {
        Set<Move> moves = new HashSet<>();
        byte[] posArr = pos.getPosition();

        // Check squares to the upper right
        for (int i = 1; i < 8; ++i) {
            if (!Board.inBounds(posArr[0] + i, posArr[1] + i) || board.isFriendly(piece.C,
                    Notation.get(posArr[0] + i, posArr[1] + i))) {
                break;
            }
            Move move = new Move(pos, Notation.get(posArr[0] + i, posArr[1] + i));
            moves.add(move);
            if (board.isEnemy(piece.C, Notation.get(posArr[0] + i, posArr[1] + i))) {
                break;
            }
        }

        // Check squares to the lower right
        for (int i = 1; i < 8; ++i) {
            if (!Board.inBounds(posArr[0] - i, posArr[1] + i) || board.isFriendly(piece.C,
                    Notation.get(posArr[0] - i, posArr[1] + i))) {
                break;
            }
            Move move = new Move(pos, Notation.get(posArr[0] - i, posArr[1] + i));
            moves.add(move);
            if (board.isEnemy(piece.C, Notation.get(posArr[0] - i, posArr[1] + i))) {
                break;
            }
        }

        // Check squares to the lower left
        for (int i = 1; i < 8; ++i) {
            if (!Board.inBounds(posArr[0] - i, posArr[1] - i) || board.isFriendly(piece.C,
                    Notation.get(posArr[0] - i, posArr[1] - i))) {
                break;
            }
            Move move = new Move(pos, Notation.get(posArr[0] - i, posArr[1] - i));
            moves.add(move);
            if (board.isEnemy(piece.C, Notation.get(posArr[0] - i, posArr[1] - i))) {
                break;
            }
        }

        // Check squares to the upper left
        for (int i = 1; i < 8; ++i) {
            if (!Board.inBounds(posArr[0] + i, posArr[1] - i) || board.isFriendly(piece.C,
                    Notation.get(posArr[0] + i, posArr[1] - i))) {
                break;
            }
            Move move = new Move(pos, Notation.get(posArr[0] + i, posArr[1] - i));
            moves.add(move);
            if (board.isEnemy(piece.C, Notation.get(posArr[0] + i, posArr[1] - i))) {
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
    private static Set<Move> possibleQueenMoves(Board board, Piece piece, Notation pos) {
        Set<Move> moves = new HashSet<>();
        moves.addAll(possibleRookMoves(board, piece, pos));
        moves.addAll(possibleBishopMoves(board, piece, pos));
        return moves;
    }

    /**
     * Get all possible moves for a king
     *
     * @param pos position of the king
     * @return set of possible moves
     */
    private static Set<Move> possibleKingMoves(Board board, Piece piece, Notation pos) {
        Set<Move> moves = new HashSet<>();
        byte[] posArr = pos.getPosition();

        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (i == 0 && j == 0 || !Board.inBounds(posArr[0] + i, posArr[1] + j)) {
                    continue;
                }
                Notation notation = Notation.get(posArr[0] + i, posArr[1] + j);
                if (!board.isFriendly(piece.C, notation)) {
                    Move move = new Move(pos, notation);
                    moves.add(move);
                }
            }
        }

        // Castling
        if (piece.state.turns.isEmpty()) {
            boolean canCastleLeft = canCastleLeft(board, piece, pos);
            boolean canCastleRight = canCastleRight(board, piece, pos);
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

    private static boolean canCastleLeft(Board board, Piece piece, Notation pos) {
        byte[] posArr = pos.getPosition();

        Piece leftRook = board.getPiece(posArr[0], 0);
        if (leftRook != null && leftRook.T == PieceType.ROOK && leftRook.state.turns.isEmpty()) {
            if (board.inCheck(piece.C == WHITE)) {
                return false;
            }
            for (int j = 2; j < posArr[1]; ++j) {
                Notation notation = Notation.get(posArr[0], j);
                if (!board.isFree(notation) || dangerousSquare(board, piece, notation)) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    private static boolean canCastleRight(Board board, Piece piece, Notation pos) {
        byte[] posArr = pos.getPosition();

        Piece rightRook = board.getPiece(posArr[0], 7);
        if (rightRook != null && rightRook.T == PieceType.ROOK && rightRook.state.turns.isEmpty()) {
            if (board.inCheck(piece.C == WHITE)) {
                return false;
            }
            for (int j = posArr[1] + 1; j < 7; ++j) {
                Notation notation = Notation.get(posArr[0], j);
                if (!board.isFree(notation) || dangerousSquare(board, piece, notation)) {
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
    private static boolean dangerousSquare(Board board, Piece piece, Notation pos) {
        byte[] posArr = pos.getPosition();

        return Arrays.stream(Notation.ALL_VALUES).filter(notation -> board.isEnemy(piece.C,
                notation)).anyMatch(enemyPos -> {
            Piece enemyPiece = board.getPiece(enemyPos);
            byte[] enemyPosArr = enemyPos.getPosition();

            return switch (enemyPiece.getType()) {
                case PAWN -> {
                    int direction = piece.getColor() == WHITE ? 1 : -1;
                    int columnDiff = posArr[1] - enemyPosArr[1];
                    yield posArr[0] == enemyPosArr[0] + direction && (columnDiff == 1 || columnDiff == -1);
                }
                case KING ->
                        Math.abs(posArr[0] - enemyPosArr[0]) <= 1 && Math.abs(posArr[1] - enemyPosArr[1]) <= 1;
                default -> enemyPiece.possibleMoves(enemyPos).
                        stream().map(Move::end).collect(Collectors.toSet()).contains(pos);
            };
        });
    }

    int getScore(Notation pos) {
        int positionalValue = getPositionalValue(this.T, this.C, pos);
        int materialValue = switch (T) {
            case PAWN -> 1;
            case KNIGHT, BISHOP -> 3;
            case ROOK -> 5;
            case QUEEN -> 9;
            case KING -> 100;
        };
        int gameValue = switch (board.gameStatus()) {
            case 0, 1 -> 0;
            case 2 -> -200;
            case 3 -> -100_000;
            default -> throw new IllegalStateException("Unexpected value: " + board.gameStatus());
        };
        return positionalValue * materialValue + gameValue;
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
            case KNIGHT -> C == WHITE ? '♘' : '♞';
            case BISHOP -> C == WHITE ? '♗' : '♝';
            case QUEEN -> C == WHITE ? '♕' : '♛';
            case KING -> C == WHITE ? '♔' : '♚';
        };
    }


    /**
     * Returns the piece represented by the bit representation
     *
     * @param bitRepresentation the bit representation specified in Piece class
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

    /**
     * Returns the positional value of the piece with respect to the board from white's POV
     * <br>Inspired by
     * <a href="https://github.com/bartekspitza/sophia/blob/master/src/evaluation.c">GitHub</a>
     */
    private static final int[][] PAWN_VALUES = {
            {0, 0, 0, 0, 0, 0, 0, 0},
            {50, 50, 50, 50, 50, 50, 50, 50},
            {10, 10, 20, 30, 30, 20, 10, 10},
            {5, 5, 10, 25, 25, 10, 5, 5},
            {0, 0, 0, 20, 20, 0, 0, 0},
            {5, -5, -10, 0, 0, -10, -5, 5},
            {5, 10, 10, -20, -20, 10, 10, 5},
            {0, 0, 0, 0, 0, 0, 0, 0}
    };

    private static final int[][] KNIGHT_VALUES = {
            {-50, -40, -30, -30, -30, -30, -40, -50},
            {-40, -20, 0, 0, 0, 0, -20, -40},
            {-30, 0, 10, 15, 15, 10, 0, -30},
            {-30, 5, 15, 20, 20, 15, 5, -30},
            {-30, 0, 15, 20, 20, 15, 0, -30},
            {-30, 5, 10, 15, 15, 10, 5, -30},
            {-40, -20, 0, 5, 5, 0, -20, -40},
            {-50, -40, -30, -30, -30, -30, -40, -50}
    };

    private static final int[][] BISHOP_VALUES = {
            {-20, -10, -10, -10, -10, -10, -10, -20},
            {-10, 0, 0, 0, 0, 0, 0, -10},
            {-10, 0, 5, 10, 10, 5, 0, -10},
            {-10, 5, 5, 10, 10, 5, 5, -10},
            {-10, 0, 10, 10, 10, 10, 0, -10},
            {-10, 10, 10, 10, 10, 10, 10, -10},
            {-10, 5, 0, 0, 0, 0, 5, -10},
            {-20, -10, -10, -10, -10, -10, -10, -20}};

    private static final int[][] ROOK_VALUES = {
            {0, 0, 0, 0, 0, 0, 0, 0},
            {5, 10, 10, 10, 10, 10, 10, 5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {0, 0, 0, 5, 5, 0, 0, 0}};
    private static final int[][] QUEEN_VALUES = {
            {-20, -10, -10, -5, -5, -10, -10, -20},
            {-10, 0, 0, 0, 0, 0, 0, -10},
            {-10, 0, 5, 5, 5, 5, 0, -10},
            {-5, 0, 5, 5, 5, 5, 0, -5},
            {0, 0, 5, 5, 5, 5, 0, -5},
            {-10, 5, 5, 5, 5, 5, 0, -10},
            {-10, 0, 5, 0, 0, 0, 0, -10},
            {-20, -10, -10, -5, -5, -10, -10, -20}};
    private static final int[][] KING_VALUES = {
            {20, 30, 10, 0, 0, 10, 30, 20},
            {20, 20, 0, 0, 0, 0, 20, 20},
            {-10, -20, -20, -20, -20, -20, -20, -10},
            {-20, -30, -30, -40, -40, -30, -30, -20},
            {-30, -40, -40, -50, -50, -40, -40, -30},
            {-30, -40, -40, -50, -50, -40, -40, -30},
            {-30, -40, -40, -50, -50, -40, -40, -30},
            {-30, -40, -40, -50, -50, -40, -40, -30}};

    private static int getPositionalValue(PieceType type, PieceColor color, Notation pos) {
        byte[] posArr = pos.getPosition();
        int row = switch (color) {
            case WHITE -> posArr[0];
            case BLACK -> 7 - posArr[0];
        };
        return switch (type) {
            case PAWN -> PAWN_VALUES[row][posArr[1]];
            case KNIGHT -> KNIGHT_VALUES[row][posArr[1]];
            case BISHOP -> BISHOP_VALUES[row][posArr[1]];
            case ROOK -> ROOK_VALUES[row][posArr[1]];
            case QUEEN -> QUEEN_VALUES[row][posArr[1]];
            case KING -> KING_VALUES[row][posArr[1]];
        };
    }

    @Override
    public String toString() {
        return Character.toString(getUnicode());
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Piece piece)) return false;
        return C == piece.C && T == piece.T;
    }
}