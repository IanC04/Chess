public class Piece {

    Notation position;

    enum Type {
        PAWN, ROOK, HORSE, BISHOP, QUEEN, KING
    }

    enum PieceColor {
        WHITE, BLACK
    }

    PieceColor C;
    Type T;

    public Piece(PieceColor c, Type t) {
        C = c;
        T = t;
    }

    boolean setPosition(Notation newPos) {
        if (newPos == this.position) {
            return false;
        }
        this.position = newPos;
        return true;
    }

    void possibleMoves() {
        return;
    }

    public String toString() {
        return C + " " + T;
    }
}