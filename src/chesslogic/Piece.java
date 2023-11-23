package chesslogic;

public class Piece {
    enum Type {
        PAWN, ROOK, HORSE, BISHOP, QUEEN, KING
    }

    enum PieceColor {
        WHITE, BLACK
    }

    Notation position;

    PieceColor C;
    Type T;

    Piece(PieceColor c, Type t) {
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