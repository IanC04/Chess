package logic;

import ai.Minimax;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

import static logic.Notation.*;
import static logic.Piece.PieceColor.*;
import static logic.Piece.PieceType.*;

public class Board {
    // Singleton
    private static boolean INITIALIZED = false;

    /**
     * Piece array to support algebraic notation indexing
     */
    private final Piece[][] CHESS_BOARD;

    /**
     * true if white's turn, false if black's
     */
    public Piece.PieceColor currentPlayerColor;

    /**
     * The current game turn
     */
    private int turn;

    /**
     * AI minimax algorithm
     */
    private final Minimax ai;

    /**
     * Player statuses
     */
    private final PlayerStatus whiteStatus;
    private final PlayerStatus blackStatus;


    private static class PlayerStatus {
        Notation king;
        GameStatus gameStatus;
        final Piece.PieceColor color;

        final HashMap<Piece, Set<Move>> allLegalMoves;

        private enum GameStatus {
            NORMAL, CHECK, STALEMATE, CHECKMATE
        }

        private PlayerStatus(Piece.PieceColor color) {
            this(color, color == WHITE ? E1 : E8, GameStatus.NORMAL);
        }

        private PlayerStatus(Piece.PieceColor color, Notation king, GameStatus gameStatus) {
            this.color = color;
            this.king = king;
            this.gameStatus = gameStatus;
            this.allLegalMoves = new HashMap<>();
        }

        public void reset() {
            this.king = color == WHITE ? E1 : E8;
            this.gameStatus = GameStatus.NORMAL;
            this.allLegalMoves.clear();
        }

        @Override
        public String toString() {
            return String.format("%s with king at %s", color, king);
        }
    }

    public Board() {
        if (Board.INITIALIZED) {
            throw new IllegalStateException("Board already exists.");
        }
        Board.INITIALIZED = true;

        this.CHESS_BOARD = new Piece[8][8];
        this.ai = new Minimax(this);
        this.whiteStatus = new PlayerStatus(WHITE);
        this.blackStatus = new PlayerStatus(BLACK);
        resetBoard();
    }

    /**
     * Resets the board to the initial position
     */
    public void resetBoard() {
        // Both sets of pawns
        for (int i = 0; i < CHESS_BOARD.length; ++i) {
            Notation nW = Notation.get(A2.getPosition()[0], i);
            Notation nB = Notation.get(A7.getPosition()[0], i);
            CHESS_BOARD[nW.getPosition()[0]][i] = new Piece(WHITE, PAWN, this);
            CHESS_BOARD[nB.getPosition()[0]][i] = new Piece(BLACK, PAWN, this);
        }

        byte[] pos;

        // White pieces
        pos = A1.getPosition();
        CHESS_BOARD[pos[0]][pos[1]] = new Piece(WHITE, ROOK, this);
        pos = B1.getPosition();
        CHESS_BOARD[pos[0]][pos[1]] = new Piece(WHITE, KNIGHT, this);
        pos = C1.getPosition();
        CHESS_BOARD[pos[0]][pos[1]] = new Piece(WHITE, BISHOP, this);
        pos = D1.getPosition();
        CHESS_BOARD[pos[0]][pos[1]] = new Piece(WHITE, QUEEN, this);
        pos = E1.getPosition();
        CHESS_BOARD[pos[0]][pos[1]] = new Piece(WHITE, KING, this);
        pos = F1.getPosition();
        CHESS_BOARD[pos[0]][pos[1]] = new Piece(WHITE, BISHOP, this);
        pos = G1.getPosition();
        CHESS_BOARD[pos[0]][pos[1]] = new Piece(WHITE, KNIGHT, this);
        pos = H1.getPosition();
        CHESS_BOARD[pos[0]][pos[1]] = new Piece(WHITE, ROOK, this);

        // Black pieces
        pos = A8.getPosition();
        CHESS_BOARD[pos[0]][pos[1]] = new Piece(BLACK, ROOK, this);
        pos = B8.getPosition();
        CHESS_BOARD[pos[0]][pos[1]] = new Piece(BLACK, KNIGHT, this);
        pos = C8.getPosition();
        CHESS_BOARD[pos[0]][pos[1]] = new Piece(BLACK, BISHOP, this);
        pos = D8.getPosition();
        CHESS_BOARD[pos[0]][pos[1]] = new Piece(BLACK, QUEEN, this);
        pos = E8.getPosition();
        CHESS_BOARD[pos[0]][pos[1]] = new Piece(BLACK, KING, this);
        pos = F8.getPosition();
        CHESS_BOARD[pos[0]][pos[1]] = new Piece(BLACK, BISHOP, this);
        pos = G8.getPosition();
        CHESS_BOARD[pos[0]][pos[1]] = new Piece(BLACK, KNIGHT, this);
        pos = H8.getPosition();
        CHESS_BOARD[pos[0]][pos[1]] = new Piece(BLACK, ROOK, this);

        // Clear middle of board
        for (int i = 2; i <= 5; i++) {
            Arrays.fill(CHESS_BOARD[i], null);
        }

        // White goes first
        this.currentPlayerColor = WHITE;
        this.turn = 1;
        this.whiteStatus.reset();
        this.blackStatus.reset();
        generateAllLegalMoves(this.whiteStatus);
    }

    /**
     * Moves the piece at oldPos to newPos
     * Returns the piece that was captured, or null if no piece was captured
     *
     * @param oldPos original position
     * @param newPos new position
     * @return the captured piece, if any
     */
    private Piece movePieceNormal(Notation oldPos, Notation newPos) {
        Piece piece = getPiece(oldPos);
        if (piece == null) {
            throw new IllegalArgumentException("No piece exists at " + oldPos);
        }
        Piece captured = getPiece(newPos);

        updateBoard(oldPos, newPos);
        return captured;
    }

    private Piece movePieceEnPassant(Notation oldPos, Notation newPos) throws IllegalArgumentException {
        Piece piece = getPiece(oldPos);
        Piece captured = getPiece(newPos);
        if (captured != null) {
            throw new IllegalArgumentException("En-passant into a piece " + newPos);
        }
        if (piece.getType() != PAWN) {
            throw new IllegalArgumentException("En-passant not from a pawn");
        }
        updateBoard(oldPos, newPos);
        captured = getPiece(Notation.get(oldPos.getPosition()[0], newPos.getPosition()[1]));
        CHESS_BOARD[oldPos.getPosition()[0]][newPos.getPosition()[1]] = null;
        return captured;
    }

    private Piece movePieceCastle(Notation oldPos, Notation newPos) throws IllegalArgumentException {
        Piece piece = getPiece(oldPos);
        Piece captured = getPiece(newPos);
        if (piece.getType() != KING) {
            throw new IllegalArgumentException("Castling not from a king");
        }
        if (captured != null) {
            throw new IllegalArgumentException("Castling into a piece " + newPos);
        }
        updateBoard(oldPos, newPos);
        updateBoard(Notation.get(oldPos.getPosition()[0], (newPos.getPosition()[1] > oldPos.getPosition()[1]) ? 7 : 0),
                Notation.get(oldPos.getPosition()[0], (newPos.getPosition()[1] > oldPos.getPosition()[1]) ? 5 : 3));
        return captured;
    }

    private Piece movePiecePromotion(Notation oldPos, Notation newPos,
                                     Piece.PieceType promotionType) throws IllegalArgumentException {
        Piece piece = getPiece(oldPos);
        if (piece.getType() != PAWN) {
            throw new IllegalArgumentException("Promotion not from a pawn");
        }
        Piece captured = getPiece(newPos);
        updateBoard(oldPos, newPos);

        piece = new Piece(piece.getColor(), promotionType, this);
        CHESS_BOARD[newPos.getPosition()[0]][newPos.getPosition()[1]] = piece;
        return captured;
    }

    /**
     * Moves the piece at oldPos to newPos
     *
     * @param move move made
     * @return the captured piece, if any
     * @throws IllegalArgumentException if the move is invalid
     */
    public Piece movePiece(Move move) throws IllegalArgumentException {
        Notation oldPos = move.start();
        Notation newPos = move.end();
        if (oldPos.equals(newPos)) {
            throw new IllegalArgumentException("Moving nowhere");
        }
        if (getPiece(oldPos) == null) {
            throw new IllegalArgumentException("No piece exists at " + oldPos);
        }
        if (getPiece(oldPos).getColor().ordinal() != currentPlayerColor.ordinal()) {
            throw new IllegalArgumentException("Piece color does not match turn");
        }

        Piece originalPiece = getPiece(oldPos);
        Piece captured = switch (move.moveType()) {
            case NORMAL -> movePieceNormal(oldPos, newPos);
            case EN_PASSANT -> movePieceEnPassant(oldPos, newPos);
            case CASTLE -> movePieceCastle(oldPos, newPos);
            case PROMOTION -> movePiecePromotion(oldPos, newPos, move.promoteTo());
        };

        if (move.moveType() == Move.MoveType.PROMOTION) {
            originalPiece = getPiece(newPos);
        }
        originalPiece.moved(turn);
        currentPlayerColor = Piece.PieceColor.opposite(currentPlayerColor);
        ++turn;

        updateStatus(originalPiece, move);
        return captured;
    }

    /**
     * Writes the move to the CHESS_BOARD field
     *
     * @param oldPos initial position
     * @param newPos ending position
     */
    private void updateBoard(Notation oldPos, Notation newPos) {
        byte[] newPosArr = newPos.getPosition();
        CHESS_BOARD[newPosArr[0]][newPosArr[1]] = getPiece(oldPos);
        byte[] oldPosArr = oldPos.getPosition();
        CHESS_BOARD[oldPosArr[0]][oldPosArr[1]] = null;
    }

    /**
     * Writes the move to the CHESS_BOARD field
     *
     * @param move move made
     */
    private void updateBoard(Move move) {
        updateBoard(move.start(), move.end());
    }

    /**
     * Returns the piece at the given position
     *
     * @param pos position
     * @return piece at pos
     */
    public Piece getPiece(Notation pos) {
        byte[] posArr = pos.getPosition();
        return CHESS_BOARD[posArr[0]][posArr[1]];
    }

    /**
     * Returns the piece at the given position
     *
     * @param row index
     * @param col index
     * @return piece at pos
     */
    public Piece getPiece(int row, int col) {
        return CHESS_BOARD[row][col];
    }

    /**
     * Getter for the current game turn
     *
     * @return turn
     */
    public int getTurn() {
        return turn;
    }

    /**
     * Returns all possible moves of the current player
     *
     * @return set of possible moves
     */
    public Set<Move> getAllLegalMoves() {
        PlayerStatus status = currentPlayerColor == Piece.PieceColor.WHITE ? whiteStatus :
                blackStatus;
        if (status.allLegalMoves.isEmpty()) {
            throw new IllegalStateException("No possible moves generated");
        }

        return status.allLegalMoves.values().stream().flatMap(Set::stream).collect(Collectors.toSet());
    }

    /**
     * Returns all legal moves of the piece at pos
     *
     * @param pos position
     * @return set of possible moves
     */
    public Set<Move> getPieceLegalMoves(Notation pos) {
        if (isFree(pos)) {
            throw new IllegalArgumentException("No piece exists at " + pos);
        }

        PlayerStatus status = getPiece(pos).getColor() == WHITE ? whiteStatus : blackStatus;
        if (status.allLegalMoves.isEmpty()) {
            throw new IllegalStateException("Legal Moves not generated.");
        }
        return status.allLegalMoves.get(getPiece(pos));
    }

    private void generateAllLegalMoves(PlayerStatus status) {
        for (Notation currentPosition : Notation.ALL_VALUES) {
            if (!isFriendly(status.color, currentPosition)) {
                continue;
            }
            Piece piece = getPiece(currentPosition);
            Set<Move> possibleMoves =
                    piece.possibleMoves(currentPosition).stream().filter(m -> safeSquare(currentPlayerColor
                            , m, status.king)).collect(Collectors.toSet());
            status.allLegalMoves.put(piece, possibleMoves);
        }
    }

    /**
     * Returns true if the square is safe after the given move
     *
     * @param playerColor  player
     * @param move         move made, null if checking current status
     * @param maybeKingPos position of the current player's king unless it's about to move
     * @return true if king is safe
     */
    boolean safeSquare(Piece.PieceColor playerColor, Move move, Notation maybeKingPos) {
        Piece captured = null;
        if (move != null) {
            captured = getPiece(move.end());
            updateBoard(move);
        }
        final Notation kingPos;
        if (move != null && move.start() == maybeKingPos) {
            kingPos = move.end();
        } else {
            kingPos = maybeKingPos;
        }
        byte[] kingPosArr = kingPos.getPosition();

        boolean unsafe =
                Arrays.stream(Notation.ALL_VALUES).filter(notation -> isEnemy(playerColor,
                        notation)).anyMatch(enemyPos -> {
                    Piece enemyPiece = getPiece(enemyPos);
                    byte[] enemyPosArr = enemyPos.getPosition();

                    return switch (enemyPiece.getType()) {
                        case PAWN -> {
                            int direction = enemyPiece.getColor() == WHITE ? 1 : -1;
                            int columnDiff = kingPosArr[1] - enemyPosArr[1];
                            yield kingPosArr[0] == enemyPosArr[0] + direction && (columnDiff == 1 || columnDiff == -1);
                        }
                        case KING ->
                                Math.abs(kingPosArr[0] - enemyPosArr[0]) <= 1 && Math.abs(kingPosArr[1] - enemyPosArr[1]) <= 1;
                        default -> enemyPiece.possibleMoves(enemyPos).
                                stream().map(Move::end).collect(Collectors.toSet()).contains(kingPos);
                    };
                });

        if (move != null) {
            updateBoard(move.end(), move.start());
            byte[] endPos = move.end().getPosition();
            CHESS_BOARD[endPos[0]][endPos[1]] = captured;
        }
        return !unsafe;
    }

    /**
     * Checks status of the game with respect to the current player
     *
     * @return Zero if normal, One if checked, Two if stalemated, Three if checkmated
     */
    public int gameStatus() {
        PlayerStatus player = currentPlayerColor == Piece.PieceColor.WHITE ? whiteStatus :
                blackStatus;
        return player.gameStatus.ordinal();
    }

    /**
     * Updates the status of the game
     * Called AFTER turn and player are updated
     *
     * @param pieceMoved piece that was moved
     * @param move       move that was made
     */
    private void updateStatus(Piece pieceMoved, Move move) {
        PlayerStatus playerJustMoved = currentPlayerColor == WHITE ?
                blackStatus : whiteStatus;
        if (pieceMoved.getType() == KING) {
            playerJustMoved.king = move.end();
        }

        // Refresh move sets
        playerJustMoved.allLegalMoves.clear();

        PlayerStatus playerToCheckStatus = currentPlayerColor == WHITE ? whiteStatus :
                blackStatus;

        boolean checked = !safeSquare(playerToCheckStatus.color, null,
                playerToCheckStatus.king);
        generateAllLegalMoves(playerToCheckStatus);
        boolean noMoves = getAllLegalMoves().isEmpty();
        if (checked) {
            if (noMoves) {
                playerToCheckStatus.gameStatus = PlayerStatus.GameStatus.CHECKMATE;
            } else {
                playerToCheckStatus.gameStatus = PlayerStatus.GameStatus.CHECK;
            }
        } else if (noMoves) {
            playerToCheckStatus.gameStatus = PlayerStatus.GameStatus.STALEMATE;
        } else {
            playerToCheckStatus.gameStatus = PlayerStatus.GameStatus.NORMAL;
        }
    }

    /**
     * Returns true if the piece at pos is free (no piece)
     *
     * @param pos position
     * @return true if free
     */
    boolean isFree(Notation pos) {
        return getPiece(pos) == null;
    }

    /**
     * Returns true if the piece at pos is friendly (same color)
     *
     * @param col piece color
     * @param pos position
     * @return true if friendly
     */
    boolean isFriendly(Piece.PieceColor col, Notation pos) {
        Piece piece = getPiece(pos);
        if (piece == null) {
            return false;
        }
        return piece.getColor() == col;
    }

    /**
     * Returns true if the piece at pos is an enemy (different color)
     *
     * @param col piece color
     * @param pos position
     * @return true if enemy
     */
    boolean isEnemy(Piece.PieceColor col, Notation pos) {
        // return !isFree(pos) && !isFriendly(col, pos);
        Piece piece = getPiece(pos);
        if (piece == null) {
            return false;
        }
        return piece.getColor() != col;
    }

    /**
     * Returns true if the given position is in bounds
     *
     * @param pos position
     * @return true if in bounds
     */
    static boolean inBounds(Notation pos) {
        byte[] posArr = pos.getPosition();
        return posArr[0] >= 0 && posArr[0] < 8 && posArr[1] >= 0 && posArr[1] < 8;
    }

    /**
     * Returns true if the given position is in bounds
     *
     * @param row index
     * @param col index
     * @return true if in bounds
     */
    static boolean inBounds(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    /**
     * Returns the FEN string representing the position of the current board
     *
     * @return FEN string
     */
    public String getFENBoard() {
        StringBuilder fen = new StringBuilder();
        for (int i = 7; i >= 0; --i) {
            int emptyLength = 0;
            for (int j = 0; j < 8; ++j) {
                Piece piece = getPiece(i, j);
                if (piece == null) {
                    ++emptyLength;
                    continue;
                }
                if (emptyLength > 0) {
                    fen.append(emptyLength);
                    emptyLength = 0;
                }
                Character p = switch (piece.T()) {
                    case PAWN -> 'p';
                    case ROOK -> 'r';
                    case KNIGHT -> 'n';
                    case BISHOP -> 'b';
                    case QUEEN -> 'q';
                    case KING -> 'k';
                };
                if (piece.C() == WHITE) {
                    p = Character.toUpperCase(p);
                }
                fen.append(p);
            }
            if (emptyLength > 0) {
                fen.append(emptyLength);
            }
        }
        return fen.toString();
    }

    public void aiMove() {
        long move = ai.getBestMove(currentPlayerColor == WHITE);
        if (Long.bitCount(move) != 2) {
            throw new IllegalStateException("Invalid move");
        }

        Notation one = ALL_VALUES[(int) Long.highestOneBit(move)];
        Notation two = ALL_VALUES[(int) Long.lowestOneBit(move)];
        Move moveMade;
        if (isFriendly(currentPlayerColor, one)) {
            moveMade = new Move(one, two);
        } else {
            moveMade = new Move(two, one);
        }
        movePiece(moveMade);
    }

    /**
     * Outputs the board in a human-readable format
     *
     * @return string representation of the board
     */
    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        for (int i = CHESS_BOARD.length - 1; i >= 0; --i) {
            for (Piece piece : CHESS_BOARD[i]) {
                output.append(piece == null ? ' ' : piece);
            }
            output.append('\n');
        }
        return output.toString();
    }
}
