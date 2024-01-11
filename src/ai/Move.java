package ai;

record Move(byte start, byte end, MoveType moveType, int value) {
    enum MoveType {
        ERROR, NORMAL, CASTLE, PAWN_DOUBLE, EN_PASSANT, PROMOTE_ROOK, PROMOTE_KNIGHT,
        PROMOTE_BISHOP, PROMOTE_QUEEN
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
        this((byte) -1, (byte) -1, MoveType.ERROR, Integer.MIN_VALUE);
    }

    Move(String start, String end, int value) {
        this(notationToIndex(start), notationToIndex(end), MoveType.NORMAL, value);
    }

    static byte notationToIndex(String pos) {
        if (pos.length() != 2 || !Character.isLetter(pos.charAt(0)) || !Character.isDigit(pos.charAt(1))) {
            throw new IllegalArgumentException("Invalid position: " + pos);
        }
        return (byte) (Character.toLowerCase(pos.charAt(0)) - 'a' + 8 * (pos.charAt(1) - '1'));
    }

    static String indexToNotation(byte index) {
        return SQUARE_NAMES[index];
    }

    @Override
    public String toString() {
        return indexToNotation(start()) + indexToNotation(end()) + " " + switch (moveType()) {
            case NORMAL -> "N";
            case CASTLE -> "C";
            case PAWN_DOUBLE -> "D";
            case EN_PASSANT -> "E";
            case PROMOTE_ROOK -> "R";
            case PROMOTE_KNIGHT -> "K";
            case PROMOTE_BISHOP -> "B";
            case PROMOTE_QUEEN -> "Q";
            default -> "E";
        };
    }
}
