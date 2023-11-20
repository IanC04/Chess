import java.awt.*;

public class Square {
    Piece piece;
    final Color COLOR;
    final int ROW;
    final char COLUMN;
    public Square(int r, char c) {
        ROW = r;
        COLUMN = c;
        if ((ROW + COLUMN) >> 1 % 2 == 0) {
            COLOR = Color.WHITE;
        }
        else {
            COLOR = Color.BLACK;
        }
    }
}