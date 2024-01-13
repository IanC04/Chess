package ai;

import static ai.BitBoards.*;

record Move(int start, int end, MoveType moveType, int value, boolean valid) {
    enum MoveType {
        ERROR, NORMAL, CASTLE_LEFT, CASTLE_RIGHT, PAWN_DOUBLE_MOVE, EN_PASSANT, PROMOTE_ROOK,
        PROMOTE_KNIGHT, PROMOTE_BISHOP, PROMOTE_QUEEN;

        static final MoveType[] PROMOTION_TYPES = {PROMOTE_ROOK, PROMOTE_KNIGHT, PROMOTE_BISHOP,
                PROMOTE_QUEEN};
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
        this(-1, -1, MoveType.ERROR, Integer.MIN_VALUE, false);
    }

    Move(String start, String end, int value) {
        this(notationToIndex(start), notationToIndex(end), MoveType.NORMAL, value, true);
    }

    Move(int start, int end, MoveType moveType) {
        this(start, end, moveType, Integer.MIN_VALUE, true);
    }

    Move(int start, int end, MoveType moveType, int value) {
        this(start, end, moveType, value, true);
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

        if (move.moveType == MoveType.CASTLE_LEFT) {

        }

        return true;
    }

    @Override
    public String toString() {
        return indexToNotation(start) + indexToNotation(end) + " " + switch (moveType) {
            case ERROR -> "ERROR";
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
