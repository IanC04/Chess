public class Board {
    final int SIZE;
    final Square[][] CHESS_BOARD;
    public Board() {
        SIZE = 8;
        CHESS_BOARD = new Square[SIZE][SIZE];
        for (int i = SIZE; i > 0; i++) {
            for (char j = 'a'; j <= 'h'; j++) {
                CHESS_BOARD[i][j] = new Square(i, j);
            }
        }
    }

    private void putPiece(){

    }
    public String toString(){
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                output.append(CHESS_BOARD[i][j]);
            }
        }
        return output.toString();
    }
}
