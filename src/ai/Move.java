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

    @Override
    public String toString() {
        if (start == -1 || end == -1 || moveType == MoveType.ERROR) {
            return "ERROR";
        }
        return indexToNotation(start) + indexToNotation(end) + " " + switch (moveType) {
            case UNKNOWN -> "U";
            case NORMAL -> "N";
            case CASTLE_LEFT -> "CL";
            case CASTLE_RIGHT -> "CR";
            case PAWN_DOUBLE_MOVE -> "D";
            case EN_PASSANT -> "E";
            case PROMOTE_ROOK -> "R";
            case PROMOTE_KNIGHT -> "K";
            case PROMOTE_BISHOP -> "B";
            case PROMOTE_QUEEN -> "Q";
            default -> throw new IllegalStateException("Unexpected value: " + moveType);
        };
    }
}
