import java.awt.Color;

public class Piece {
    enum Type {
        PAWN, ROOK, HORSE, BISHOP, QUEEN, KING;
    }

    enum PieceColor {
        WHITE(Color.WHITE), BLACK(Color.BLACK);
        final Color COLOR;

        PieceColor(Color c) {
            this.COLOR = c;
        }
    }

    Color C;
    Type T;


    public Piece() {
        C = Color.WHITE;
        T = Type.PAWN;
    }

    boolean move() {
        return false;
    }

    void possibleMoves() {
        return;
    }

    public String toString() {
        return C + " " + T;
    }
}