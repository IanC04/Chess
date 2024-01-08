package logic;

public record Move(Notation start, Notation end, MoveType moveType,
                   Piece.PieceType promoteTo) implements Comparable<Move> {

    // TODO: Add capture move type for half move clock
    public enum MoveType {
        NORMAL, CASTLE, EN_PASSANT, PROMOTION
    }

    public Move(Notation start, Notation end) {
        this(start, end, MoveType.NORMAL);
    }

    public Move(Notation start, Notation end, MoveType moveType) {
        this(start, end, moveType, null);
    }
    @Override
    public int compareTo(Move o) {
        return Integer.compare(this.moveType.ordinal(), o.moveType.ordinal());
    }

    @Override
    public String toString() {
        return String.format("%s %s", start, end) + (moveType != MoveType.NORMAL ? " " + moveType : "");
    }
}