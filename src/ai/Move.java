package ai;

import static ai.BitBoards.*;

record Move(int start, int end, MoveType moveType, PieceType pieceType, int value, boolean valid) {
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
        this(-1, -1, MoveType.ERROR, PieceType.ERROR, Integer.MIN_VALUE, false);
    }

    Move(int start, int end, int value) {
        this(start, end, MoveType.UNKNOWN, PieceType.UNKNOWN, value, true);
    }

    Move(int start, int end, MoveType moveType, PieceType pieceType) {
        this(start, end, moveType, pieceType, Integer.MIN_VALUE, true);
    }

    Move(int start, int end, MoveType moveType, PieceType pieceType, int value) {
        this(start, end, moveType, pieceType, value, true);
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

    static boolean validate(BitBoards state, Move move) {
        if (move.start < A1 || move.start > H8 || move.end < A1 || move.end > H8) {
            throw new IllegalArgumentException("Invalid move: " + move);
        }
        if (move.moveType() == MoveType.CASTLE_LEFT) {
            // TODO
        } else if (move.moveType() == MoveType.CASTLE_RIGHT) {
            // TODO
        }
        BitBoards tempState = state.makeMove(move);
        if (tempState.safeIndex(state.whiteToMove, state.whiteToMove ? tempState.whiteKing : tempState.blackKing)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return indexToNotation(start) + indexToNotation(end) + " " + switch (moveType) {
            case ERROR -> "ERR";
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
        };
    }
}
