package ai;

import static ai.BitBoards.*;

record Move(int start, int end, MoveType moveType, PieceType pieceType, int value) {
    enum MoveType {
        ERROR, UNKNOWN, NORMAL, CASTLE_LEFT, CASTLE_RIGHT, PAWN_DOUBLE_MOVE, EN_PASSANT,
        PROMOTE_ROOK,
        PROMOTE_KNIGHT, PROMOTE_BISHOP, PROMOTE_QUEEN;

        static final MoveType[] PROMOTION_TYPES = {PROMOTE_ROOK, PROMOTE_KNIGHT, PROMOTE_BISHOP,
                PROMOTE_QUEEN};
    }

    enum PieceType {
        ERROR, UNKNOWN, PAWN, ROOK, KNIGHT, BISHOP, QUEEN, KING
    }

    private static final String[] SQUARE_NAMES;

    static {
        char[] position = {'a', '1'};
        SQUARE_NAMES = new String[64];
        for (byte i = 0; i < 64; i++) {
            SQUARE_NAMES[i] = new String(position);
            ++position[0];
            if (position[0] == 'i') {
                ++position[1];
                position[0] = 'a';
            }
        }
    }

    Move() {
        this(-1, -1, MoveType.ERROR, PieceType.ERROR, Integer.MIN_VALUE);
    }

    Move(int start, int end, int value) {
        this(start, end, MoveType.UNKNOWN, PieceType.UNKNOWN, value);
    }

    Move(int start, int end, MoveType moveType, PieceType pieceType) {
        this(start, end, moveType, pieceType, Integer.MIN_VALUE);
    }

    static int notationToIndex(String pos) {
        if (pos.length() != 2 || !Character.isLetter(pos.charAt(0)) || !Character.isDigit(pos.charAt(1))) {
            throw new IllegalArgumentException("Invalid position: " + pos);
        }
        return (Character.toLowerCase(pos.charAt(0)) - 'a') + (8 * (pos.charAt(1) - '1'));
    }

    static String indexToNotation(int index) {
        return SQUARE_NAMES[index];
    }

    /**
     * Makes sure the move is a legal move
     *
     * @param state current state
     * @param move  move to validate
     * @return true if the move is legal
     */
    static boolean validate(BitBoards state, Move move) {
        if (move.start < A1 || move.start > H8 || move.end < A1 || move.end > H8) {
            throw new IllegalArgumentException("Invalid move: " + move);
        }
        if (move.moveType == MoveType.ERROR) {
            throw new IllegalArgumentException("Error move type: " + move);
        }

        if (move.moveType() == MoveType.CASTLE_LEFT || move.moveType() == MoveType.CASTLE_RIGHT) {
            boolean valid = validateCastle(state, move);
            if (!valid) {
                return false;
            }
        }

        // Is king checked after the move?
        BitBoards tempState = state.tryMove(move);
        return tempState.safeSquare(state.whiteToMove, state.whiteToMove ? tempState.whiteKing :
                tempState.blackKing);
    }

    private static boolean validateCastle(BitBoards state, Move move) {
        if (move.moveType() == MoveType.CASTLE_LEFT) {
            boolean safe = state.safeSquare(state.whiteToMove, (state.whiteToMove ? state.whiteKing :
                    state.blackKing) >>> 1);
            if (!safe) {
                return false;
            }
        } else {
            boolean safe = state.safeSquare(state.whiteToMove, (state.whiteToMove ? state.whiteKing :
                    state.blackKing) << 1);
            if (!safe) {
                return false;
            }
        }

        return state.safeSquare(state.whiteToMove, (state.whiteToMove ? state.whiteKing :
                state.blackKing));
    }

    // How far the squareIndex is from the center of the board, TODO: currently unused
    private static final int[] DISTANCE_TO_CENTER = {3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2, 2, 2, 2, 2, 3, 3, 2, 1, 1, 1, 1, 2, 3, 3, 2, 1, 0, 0, 1, 2, 3, 3, 2, 1, 0, 0, 1, 2, 3, 3, 2, 1, 1, 1, 1, 2, 3, 3, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3};

    static int movePositionValue(Move move, boolean white) {
        if (white) {
            return switch (move.pieceType) {
                case PAWN -> WHITE_PAWN_POS_VALUES[move.end] - WHITE_PAWN_POS_VALUES[move.start];
                case ROOK -> WHITE_ROOK_POS_VALUES[move.end] - WHITE_ROOK_POS_VALUES[move.start];
                case KNIGHT -> WHITE_KNIGHT_POS_VALUES[move.end] - WHITE_KNIGHT_POS_VALUES[move.start];
                case BISHOP -> WHITE_BISHOP_POS_VALUES[move.end] - WHITE_BISHOP_POS_VALUES[move.start];
                case QUEEN -> WHITE_QUEEN_POS_VALUES[move.end] - WHITE_QUEEN_POS_VALUES[move.start];
                case KING -> WHITE_KING_POS_VALUES[move.end] - WHITE_KING_POS_VALUES[move.start];
                default -> throw new IllegalStateException("Unexpected value: " + move.pieceType);
            };
        }
        return switch (move.pieceType) {
            case PAWN -> BLACK_PAWN_POS_VALUES[move.end] - BLACK_PAWN_POS_VALUES[move.start];
            case ROOK -> BLACK_ROOK_POS_VALUES[move.end] - BLACK_ROOK_POS_VALUES[move.start];
            case KNIGHT -> BLACK_KNIGHT_POS_VALUES[move.end] - BLACK_KNIGHT_POS_VALUES[move.start];
            case BISHOP -> BLACK_BISHOP_POS_VALUES[move.end] - BLACK_BISHOP_POS_VALUES[move.start];
            case QUEEN -> BLACK_QUEEN_POS_VALUES[move.end] - BLACK_QUEEN_POS_VALUES[move.start];
            case KING -> BLACK_KING_POS_VALUES[move.end] - BLACK_KING_POS_VALUES[move.start];
            default -> throw new IllegalStateException("Unexpected value: " + move.pieceType);
        };
    }

    @Override
    public String toString() {
        if (start == -1 || end == -1 || moveType == MoveType.ERROR) {
            return "ERROR";
        }
        return indexToNotation(start) + indexToNotation(end) + " " + moveType;
    }
}
