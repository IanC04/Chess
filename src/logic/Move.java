package logic;

public record Move(Notation start, Notation end, int value, MoveType moveType) implements Comparable<Move> {

    public enum MoveType {
        NORMAL, CASTLE, EN_PASSANT, PROMOTION
    }

    public Move(int value) {
        this(null, null, value, MoveType.NORMAL);
    }

    public Move(Notation start, Notation end) {
        this(start, end, 0, MoveType.NORMAL);
    }

    public Move(Notation start, Notation end, MoveType moveType) {
        this(start, end, 0, moveType);
    }
    @Override
    public int compareTo(Move o) {
        return Integer.compare(value, o.value);
    }

    @Override
    public String toString() {
        return String.format("%s %s", start, end) + (moveType != MoveType.NORMAL ? " " + moveType : "");
    }
}