import java.util.Arrays;

public class Board {

    /**
     * Stored as 1-D array to support algebraic notation indexing
     */
    private final Piece[] CHESS_BOARD;

    public Board() {
        CHESS_BOARD = new Piece[64];
        resetBoard();
    }

    void resetBoard() {
        Arrays.fill(CHESS_BOARD, Notation.A2.getPosition(), Notation.H2.getPosition(),
                new Piece(Piece.PieceColor.WHITE,
                        Piece.Type.PAWN));
        Arrays.fill(CHESS_BOARD, Notation.A7.getPosition(), Notation.H7.getPosition(),
                new Piece(Piece.PieceColor.BLACK,
                        Piece.Type.PAWN));

        CHESS_BOARD[Notation.A1.getPosition()] = new Piece(Piece.PieceColor.WHITE, Piece.Type.ROOK);
        CHESS_BOARD[Notation.B1.getPosition()] = new Piece(Piece.PieceColor.WHITE, Piece.Type.HORSE);
        CHESS_BOARD[Notation.C1.getPosition()] = new Piece(Piece.PieceColor.WHITE, Piece.Type.BISHOP);
        CHESS_BOARD[Notation.D1.getPosition()] = new Piece(Piece.PieceColor.WHITE, Piece.Type.QUEEN);
        CHESS_BOARD[Notation.E1.getPosition()] = new Piece(Piece.PieceColor.WHITE, Piece.Type.KING);
        CHESS_BOARD[Notation.F1.getPosition()] = new Piece(Piece.PieceColor.WHITE, Piece.Type.BISHOP);
        CHESS_BOARD[Notation.G1.getPosition()] = new Piece(Piece.PieceColor.WHITE, Piece.Type.HORSE);
        CHESS_BOARD[Notation.H1.getPosition()] = new Piece(Piece.PieceColor.WHITE, Piece.Type.ROOK);
        CHESS_BOARD[Notation.A8.getPosition()] = new Piece(Piece.PieceColor.BLACK, Piece.Type.ROOK);
        CHESS_BOARD[Notation.B8.getPosition()] = new Piece(Piece.PieceColor.BLACK, Piece.Type.HORSE);
        CHESS_BOARD[Notation.C8.getPosition()] = new Piece(Piece.PieceColor.BLACK, Piece.Type.BISHOP);
        CHESS_BOARD[Notation.D8.getPosition()] = new Piece(Piece.PieceColor.BLACK, Piece.Type.QUEEN);
        CHESS_BOARD[Notation.E8.getPosition()] = new Piece(Piece.PieceColor.BLACK, Piece.Type.KING);
        CHESS_BOARD[Notation.F8.getPosition()] = new Piece(Piece.PieceColor.BLACK, Piece.Type.BISHOP);
        CHESS_BOARD[Notation.G8.getPosition()] = new Piece(Piece.PieceColor.BLACK, Piece.Type.HORSE);
        CHESS_BOARD[Notation.H8.getPosition()] = new Piece(Piece.PieceColor.BLACK, Piece.Type.ROOK);
        Arrays.fill(CHESS_BOARD, Notation.A3.getPosition(), Notation.H6.getPosition() + 1, null);
    }

    private void movePiece(Notation oldPos, Notation newPos) {
        if (CHESS_BOARD[oldPos.getPosition()] == null) {
            throw new IllegalArgumentException("No piece exists at " + oldPos);
        }
        boolean success = CHESS_BOARD[oldPos.getPosition()].setPosition(newPos);
        if (!success) {
            throw new IllegalArgumentException("Piece already exists at " + newPos);
        }
    }

    public String toString() {
        StringBuilder output = new StringBuilder();
        for (Piece piece : CHESS_BOARD) {
            if (piece == null)
                output.append(" ");
            else
                output.append(piece);
        }
        return output.toString();
    }
}
