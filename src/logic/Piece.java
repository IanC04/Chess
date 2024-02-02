package logic;

import java.util.*;

import static logic.Piece.PieceColor.*;

public record Piece(PieceColor C, PieceType T, Board board, State state) {
    public enum PieceType {
        PAWN, ROOK, KNIGHT, BISHOP, QUEEN, KING
    }


    public enum PieceColor {
        WHITE, BLACK;

        public static PieceColor opposite(PieceColor color) {
            return color == WHITE ? BLACK : WHITE;
        }
    }

    /**
     * Mutable state of the piece
     */
    private static class State {
        private final List<Integer> turns;

        State() {
            this.turns = new ArrayList<>();
        }

        State(List<Integer> turns) {
            this.turns = new ArrayList<>(turns);
        }

        State copy() {
            return new State(this.turns);
        }

        private void addMove(int turn) {
            turns.add(turn);
        }
    }

    Piece(PieceColor C, PieceType T, Board board) {
        this(C, T, board, new State());
    }

    public Piece promote(PieceType T) {
        return new Piece(this.C, T, this.board, this.state.copy());
    }

    void moved(int turn) {
        if (state.turns.stream().anyMatch(i -> i >= turn)) {
            throw new IllegalStateException("Piece has already moved in the future?");
        }

        state.addMove(turn);
    }

    PieceColor getColor() {
        return C;
    }

    PieceType getType() {
        return T;
    }


    Set<Move> possibleMoves(Notation pos) {
        if (board.getPiece(pos) == null) {
            throw new IllegalArgumentException("No piece at " + pos);
        }
        return switch (T) {
            case PAWN -> possiblePawnMoves(board, this, pos);
            case ROOK -> possibleRookMoves(board, this, pos);
            case KNIGHT -> possibleHorseMoves(board, this, pos);
            case BISHOP -> possibleBishopMoves(board, this, pos);
            case QUEEN -> possibleQueenMoves(board, this, pos);
            case KING -> possibleKingMoves(board, this, pos);
        };
    }

    /**
     * Get all possible moves for a pawn
     *
     * @return set of possible moves
     */
    private static Set<Move> possiblePawnMoves(Board board, Piece piece, Notation pos) {
        Set<Move> moves = new HashSet<>();
        int direction = piece.C == WHITE ? 1 : -1;
        byte[] posArr = pos.getPosition();

        // Move single forward
        if (Board.inBounds(posArr[0] + direction, posArr[1]) && board.isFree(Notation.get(posArr[0] + direction, posArr[1]))) {
            Move move = new Move(pos, Notation.get(posArr[0] + direction, posArr[1]),
                    (posArr[0] + direction == 0 || posArr[0] + direction == 7) ? Move.MoveType.PROMOTION : Move.MoveType.NORMAL);
            moves.add(move);

            // If pawn on starting square, can move 2 squares if not blocked
            if (piece.state.turns.isEmpty() && board.isFree(Notation.get(posArr[0] + 2 * direction, posArr[1]))) {
                move = new Move(pos, Notation.get(posArr[0] + 2 * direction, posArr[1]));
                moves.add(move);
            }
        }

        // Capture diagonally
        int newRow = posArr[0] + direction;
        if (newRow >= 0 && newRow < 8) {
            int newColumn = posArr[1] - 1;
            if (newColumn >= 0 && board.isEnemy(piece.C, Notation.get(newRow, newColumn))) {
                Notation notation = Notation.get(newRow, newColumn);
                if (Board.inBounds(notation) && board.isEnemy(piece.C, notation)) {
                    Move.MoveType moveType = switch (newRow) {
                        case 0, 7 -> Move.MoveType.PROMOTION;
                        default -> Move.MoveType.NORMAL;
                    };
                    Move move = new Move(pos, notation, moveType);
                    moves.add(move);
                }
            }

            newColumn = posArr[1] + 1;
            if (newColumn < 8 && board.isEnemy(piece.C, Notation.get(newRow, newColumn))) {
                Notation notation = Notation.get(newRow, newColumn);
                if (Board.inBounds(notation) && board.isEnemy(piece.C, notation)) {
                    Move.MoveType moveType = switch (newRow) {
                        case 0, 7 -> Move.MoveType.PROMOTION;
                        default -> Move.MoveType.NORMAL;
                    };
                    Move move = new Move(pos, notation, moveType);
                    moves.add(move);
                }
            }
        }

        // En-passant
        if (posArr[0] == (piece.C == WHITE ? 4 : 3)) {
            int newColumn = posArr[1] - 1;
            if (newColumn >= 0 && board.isEnemy(piece.C, Notation.get(posArr[0], newColumn))) {
                Piece enemyPiece = board.getPiece(posArr[0], newColumn);
                boolean canEnPassant =
                        enemyPiece.T == PieceType.PAWN && enemyPiece.state.turns.size() == 1 && enemyPiece.state.turns.get(0) == board.getTurn() - 1;
                if (canEnPassant) {
                    if (board.isEnemy(piece.C, Notation.get(posArr[0] + direction,
                            newColumn))) {
                        throw new IllegalStateException("Enemy pawn moved through another enemy " +
                                "piece");
                    }
                    Move move = new Move(pos, Notation.get(posArr[0] + direction, newColumn),
                            Move.MoveType.EN_PASSANT);
                    moves.add(move);
                }
            }

            newColumn = posArr[1] + 1;
            if (newColumn < 8 && board.isEnemy(piece.C, Notation.get(posArr[0], newColumn))) {
                Piece enemyPiece = board.getPiece(posArr[0], newColumn);
                boolean canEnPassant =
                        enemyPiece.T == PieceType.PAWN && enemyPiece.state.turns.size() == 1 && enemyPiece.state.turns.get(0) == board.getTurn() - 1;
                if (canEnPassant) {
                    Move move = new Move(pos, Notation.get(posArr[0] + direction, newColumn),
                            Move.MoveType.EN_PASSANT);
                    moves.add(move);
                }
            }
        }

        return moves;
    }

    /**
     * Get all possible moves for a rook
     *
     * @return set of possible moves
     */
    private static Set<Move> possibleRookMoves(Board board, Piece piece, Notation pos) {
        Set<Move> moves = new HashSet<>();
        byte[] posArr = pos.getPosition();

        // Check squares to the right
        for (int i = posArr[1] + 1; i < 8; ++i) {
            Notation square = Notation.get(posArr[0], i);
            if (piece.addSlidingSquare(board, piece, pos, square, moves)) {
                break;
            }
        }

        // Check squares to the left
        for (int i = posArr[1] - 1; i >= 0; --i) {
            Notation square = Notation.get(posArr[0], i);
            if (piece.addSlidingSquare(board, piece, pos, square, moves)) {
                break;
            }
        }

        // Check squares above
        for (int i = posArr[0] + 1; i < 8; ++i) {
            Notation square = Notation.get(i, posArr[1]);
            if (piece.addSlidingSquare(board, piece, pos, square, moves)) {
                break;
            }
        }

        // Check squares below
        for (int i = posArr[0] - 1; i >= 0; --i) {
            Notation square = Notation.get(i, posArr[1]);
            if (piece.addSlidingSquare(board, piece, pos, square, moves)) {
                break;
            }
        }

        return moves;
    }

    /**
     * @return if the piece is done adding squares
     */
    private boolean addSlidingSquare(Board board, Piece piece, Notation initial, Notation pos,
                                     Set<Move> moveSet) {
        if (board.isFriendly(piece.C, pos)) {
            return true;
        }
        Move move = new Move(initial, pos);
        moveSet.add(move);
        return board.isEnemy(piece.C, pos);
    }

    /**
     * Get all possible moves for a horse
     *
     * @param pos position of the horse
     * @return set of possible moves
     */
    private static Set<Move> possibleHorseMoves(Board board, Piece piece, Notation pos) {
        Set<Move> moves = new HashSet<>();
        byte[] posArr = pos.getPosition();

        int[][] possible = {{posArr[0] + 2, posArr[1] + 1}, {posArr[0] + 1, posArr[1] + 2},
                {posArr[0] - 1, posArr[1] + 2}, {posArr[0] - 2, posArr[1] + 1}, {posArr[0] - 2,
                posArr[1] - 1}, {posArr[0] - 1, posArr[1] - 2}, {posArr[0] + 1, posArr[1] - 2},
                {posArr[0] + 2, posArr[1] - 1}};

        for (int[] i : possible) {
            if (Board.inBounds(i[0], i[1]) && !board.isFriendly(piece.C,
                    Notation.get(i[0], i[1]))) {
                Move move = new Move(pos, Notation.get(i[0], i[1]));
                moves.add(move);
            }
        }

        return moves;
    }

    /**
     * Get all possible moves for a bishop
     *
     * @param pos position of the bishop
     * @return set of possible moves
     */
    private static Set<Move> possibleBishopMoves(Board board, Piece piece, Notation pos) {
        Set<Move> moves = new HashSet<>();
        byte[] posArr = pos.getPosition();

        // Check squares to the upper right
        for (int i = 1; i < 8; ++i) {
            if (!Board.inBounds(posArr[0] + i, posArr[1] + i)) {
                break;
            }
            Notation square = Notation.get(posArr[0] + i, posArr[1] + i);
            if (piece.addSlidingSquare(board, piece, pos, square, moves)) {
                break;
            }
        }

        // Check squares to the lower right
        for (int i = 1; i < 8; ++i) {
            if (!Board.inBounds(posArr[0] - i, posArr[1] + i)) {
                break;
            }
            Notation square = Notation.get(posArr[0] - i, posArr[1] + i);
            if (piece.addSlidingSquare(board, piece, pos, square, moves)) {
                break;
            }
        }

        // Check squares to the lower left
        for (int i = 1; i < 8; ++i) {
            if (!Board.inBounds(posArr[0] - i, posArr[1] - i)) {
                break;
            }
            Notation square = Notation.get(posArr[0] - i, posArr[1] - i);
            if (piece.addSlidingSquare(board, piece, pos, square, moves)) {
                break;
            }
        }

        // Check squares to the upper left
        for (int i = 1; i < 8; ++i) {
            if (!Board.inBounds(posArr[0] + i, posArr[1] - i)) {
                break;
            }
            Notation square = Notation.get(posArr[0] + i, posArr[1] - i);
            if (piece.addSlidingSquare(board, piece, pos, square, moves)) {
                break;
            }
        }

        return moves;
    }

    /**
     * Get all possible moves for a queen, which is just the union of the possible moves for a rook and a bishop
     *
     * @param pos position of the queen
     * @return set of possible moves
     */
    private static Set<Move> possibleQueenMoves(Board board, Piece piece, Notation pos) {
        Set<Move> moves = new HashSet<>();
        moves.addAll(possibleRookMoves(board, piece, pos));
        moves.addAll(possibleBishopMoves(board, piece, pos));
        return moves;
    }

    /**
     * Get all possible moves for a king
     *
     * @param pos position of the king
     * @return set of possible moves
     */
    private static Set<Move> possibleKingMoves(Board board, Piece piece, Notation pos) {
        Set<Move> moves = new HashSet<>();
        byte[] posArr = pos.getPosition();

        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (i == 0 && j == 0 || !Board.inBounds(posArr[0] + i, posArr[1] + j)) {
                    continue;
                }
                Notation notation = Notation.get(posArr[0] + i, posArr[1] + j);
                if (!board.isFriendly(piece.C, notation)) {
                    Move move = new Move(pos, notation);
                    moves.add(move);
                }
            }
        }

        // Castling
        boolean[] castlingRights = castlingRights(board, piece.C);
        // Left
        if (castlingRights[0]) {
            boolean canCastleLeft = castleLeftThisTurn(board, piece, pos);
            if (canCastleLeft) {
                Move move = new Move(pos, Notation.get(posArr[0], 2), Move.MoveType.CASTLE);
                moves.add(move);
            }
        }
        // Right
        if (castlingRights[1]) {
            boolean canCastleRight = castleRightThisTurn(board, piece, pos);
            if (canCastleRight) {
                Move move = new Move(pos, Notation.get(posArr[0], 6), Move.MoveType.CASTLE);
                moves.add(move);
            }
        }

        return moves;
    }

    /**
     * Returns the castling rights for the given color, not if the king can castle this turn
     *
     * @param board containing the pieces
     * @param color of the king
     * @return array of booleans, [0] is left, [1] is right
     */
    static boolean[] castlingRights(Board board, PieceColor color) {
        boolean queenSide = true;
        boolean kingSide = true;
        Piece king = board.getPiece(color == WHITE ? Notation.E1 : Notation.E8);
        if (king == null || king.T != PieceType.KING || king.C != color || !king.state.turns.isEmpty()) {
            queenSide = false;
            kingSide = false;
        }
        Piece leftRook = board.getPiece(color == WHITE ? Notation.A1 : Notation.A8);
        if (leftRook == null || leftRook.T != PieceType.ROOK || leftRook.C != color || !leftRook.state.turns.isEmpty()) {
            queenSide = false;
        }
        Piece rightRook = board.getPiece(color == WHITE ? Notation.H1 : Notation.H8);
        if (rightRook == null || rightRook.T != PieceType.ROOK || rightRook.C != color || !rightRook.state.turns.isEmpty()) {
            kingSide = false;
        }

        return new boolean[]{kingSide, queenSide};
    }

    private static boolean castleLeftThisTurn(Board board, Piece king, Notation pos) {
        Piece kingSpot = board.getPiece(pos);
        if (kingSpot == null || kingSpot.T != PieceType.KING || kingSpot.C != king.C) {
            throw new IllegalArgumentException("No king at " + pos);
        }

        byte[] posArr = pos.getPosition();
        for (int j = 1; j < posArr[1]; ++j) {
            Notation notation = Notation.get(posArr[0], j);
            if (!board.isFree(notation)) {
                return false;
            }
        }
        for (int j = 2; j <= posArr[1]; ++j) {
            Notation notation = Notation.get(posArr[0], j);
            Move move = pos == notation ? null : new Move(pos, notation);
            if (!board.safeSquare(king.C, move, notation)) {
                return false;
            }
        }
        return true;
    }

    private static boolean castleRightThisTurn(Board board, Piece king, Notation pos) {
        Piece kingSpot = board.getPiece(pos);
        if (kingSpot == null || kingSpot.T != PieceType.KING || kingSpot.C != king.C) {
            throw new IllegalArgumentException("No king at " + pos);
        }

        byte[] posArr = pos.getPosition();
        for (int j = posArr[1] + 1; j < 7; ++j) {
            Notation notation = Notation.get(posArr[0], j);
            if (!board.isFree(notation)) {
                return false;
            }
        }
        for (int j = posArr[1]; j < 7; ++j) {
            Notation notation = Notation.get(posArr[0], j);
            Move move = pos == notation ? null : new Move(pos, notation);
            if (!board.safeSquare(king.C, move, notation)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return true if this piece is a pawn that can be captured en-passant
     */
    boolean enPassantTarget() {
        return T == PieceType.PAWN && state.turns.size() == 1 && state.turns.get(0) == board.getTurn() - 1;
    }

    /**
     * Returns the unicode character for this piece
     *
     * @return unicode character
     */
    public char getUnicode() {
        return switch (T) {
            case PAWN -> C == WHITE ? '♙' : '♟';
            case ROOK -> C == WHITE ? '♖' : '♜';
            case KNIGHT -> C == WHITE ? '♘' : '♞';
            case BISHOP -> C == WHITE ? '♗' : '♝';
            case QUEEN -> C == WHITE ? '♕' : '♛';
            case KING -> C == WHITE ? '♔' : '♚';
        };
    }

    @Override
    public String toString() {
        return Character.toString(getUnicode());
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Piece piece)) return false;
        return C == piece.C && T == piece.T;
    }
}