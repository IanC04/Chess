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

    /**
     * Stored as 1-D array to support algebraic notation indexing
     */
    private final Piece[][] CHESS_BOARD;

    /**
     * true if white's turn, false if black's
     */
    private boolean whiteToPlay;

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

        HashMap<Piece, Set<Move>> allPossibleMoves;

        private enum GameStatus {
            NORMAL, CHECK, CHECKMATE, STALEMATE
        }

        private PlayerStatus(Piece.PieceColor color) {
            this(color, color == WHITE ? E1 : E8, GameStatus.NORMAL);
        }

        private PlayerStatus(Piece.PieceColor color, Notation king, GameStatus gameStatus) {
            this.color = color;
            this.king = king;
            this.gameStatus = gameStatus;
            this.allPossibleMoves = new HashMap<>();
        }

        public PlayerStatus copy() {
            return new PlayerStatus(this.color, this.king, this.gameStatus);
        }

        public void reset() {
            this.king = color == WHITE ? E1 : E8;
            this.gameStatus = GameStatus.NORMAL;
            this.allPossibleMoves.clear();
        }
    }

    public Board() {
        this.CHESS_BOARD = new Piece[8][8];
        this.whiteToPlay = true;
        this.ai = new Minimax(this);
        this.whiteStatus = new PlayerStatus(WHITE);
        this.blackStatus = new PlayerStatus(BLACK);
        resetBoard();
    }

    /**
     * Must be used only when generating temporary boards
     *
     * @param board to copy
     */
    public Board(Board board) {
        this.CHESS_BOARD = new Piece[8][8];
        this.whiteToPlay = board.whiteToPlay;
        this.ai = null;
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < CHESS_BOARD[i].length; ++j) {
                Piece piece = board.CHESS_BOARD[i][j];
                if (piece == null) {
                    continue;
                }
                CHESS_BOARD[i][j] = piece.copy(this);
            }
        }
        this.turn = board.turn;
        this.whiteStatus = board.whiteStatus.copy();
        this.blackStatus = board.blackStatus.copy();
        this.whiteStatus.allPossibleMoves = new HashMap<>(board.whiteStatus.allPossibleMoves);
        this.blackStatus.allPossibleMoves = new HashMap<>(board.blackStatus.allPossibleMoves);
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
        this.whiteToPlay = true;
        this.turn = 1;
        this.whiteStatus.reset();
        this.blackStatus.reset();
        this.generateAllPossibleMoves(whiteStatus);
        this.generateAllPossibleMoves(blackStatus);
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
        return null;
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
     * TODO: Fix game status issues
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
        if (getPiece(oldPos).getColor() != (whiteToPlay ? WHITE : BLACK)) {
            throw new IllegalArgumentException("Piece color does not match turn");
        }

        Piece originalPiece = getPiece(oldPos);
        Piece captured = switch (move.moveType()) {
            case NORMAL -> movePieceNormal(oldPos, newPos);
            case EN_PASSANT -> movePieceEnPassant(oldPos, newPos);
            case CASTLE -> movePieceCastle(oldPos, newPos);
            case PROMOTION -> movePiecePromotion(oldPos, newPos, getPromotionType());
        };

        if (move.moveType() == Move.MoveType.PROMOTION) {
            originalPiece = getPiece(newPos);
        }
        updateStatus(originalPiece, move);

        originalPiece.moved(turn);
        whiteToPlay = !whiteToPlay;
        ++turn;

        return captured;
    }

    /**
     * TODO: Add functionality for promotion type selection
     *
     * @return promotion type
     */
    private Piece.PieceType getPromotionType() {
        return QUEEN;
    }

    private void updateBoard(Notation oldPos, Notation newPos) {
        byte[] newPosArr = newPos.getPosition();
        CHESS_BOARD[newPosArr[0]][newPosArr[1]] = getPiece(oldPos);
        byte[] oldPosArr = oldPos.getPosition();
        CHESS_BOARD[oldPosArr[0]][oldPosArr[1]] = null;
    }

    void updateBoard(Move move) {
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
    public Set<Move> getAllPossibleMoves() {
        PlayerStatus status = whiteToPlay ? whiteStatus : blackStatus;
        if (status.allPossibleMoves.isEmpty()) {
            throw new IllegalStateException("No possible moves generated");
        }

        return status.allPossibleMoves.values().stream().flatMap(Set::stream).collect(Collectors.toSet());
    }

    /**
     * Returns all possible moves of the piece at pos
     *
     * @param pos position
     * @return set of possible moves
     */
    public Set<Move> getPiecePossibleMoves(Notation pos) {
        if (isFree(pos)) {
            throw new IllegalArgumentException("No piece exists at " + pos);
        }

        PlayerStatus status = getPiece(pos).getColor() == WHITE ? whiteStatus : blackStatus;
        if (status.allPossibleMoves.isEmpty()) {
            throw new IllegalStateException("No possible moves generated");
        }
        return status.allPossibleMoves.get(getPiece(pos));
    }

    private void generateAllPossibleMoves(PlayerStatus status) {
        for (Notation currentPosition : Notation.ALL_VALUES) {
            if (!isFriendly(status.color, currentPosition)) {
                continue;
            }
            Piece piece = getPiece(currentPosition);
            Set<Move> possibleMoves =
                    piece.possibleMoves(currentPosition).stream().filter(m -> kingSafe(whiteToPlay
                            , m)).collect(Collectors.toSet());
            status.allPossibleMoves.put(piece, possibleMoves);
        }
    }

    /**
     * Returns true if the king is safe after the given move
     *
     * @param white player
     * @param move move made
     * @return true if king is safe
     */
    private boolean kingSafe(boolean white, Move move) {
        // Board tempBoard = new Board(this);
        // tempBoard.updateBoard(move);
        //
        // Piece.PieceColor color = white ? WHITE : BLACK;
        // PlayerStatus player = white ? whiteStatus : blackStatus;
        // for (Notation pos : Notation.ALL_VALUES) {
        //     if (tempBoard.isEnemy(color, pos)) {
        //         if (tempBoard.getPiece(pos).getType() == KING) {
        //             continue;
        //         }
        //         Set<Move> possibleMoves = tempBoard.getPiece(pos).possibleMoves(pos);
        //         if (possibleMoves.stream().anyMatch(m -> m.end().equals(player.king))) {
        //             return false;
        //         }
        //     }
        // }
        return true;
    }

    /**
     * Returns true if the player is in check
     *
     * @param white player
     * @return true if in check
     */
    public boolean inCheck(boolean white) {
        return (white ? whiteStatus.gameStatus : blackStatus.gameStatus) == PlayerStatus.GameStatus.CHECK;
    }

    public boolean inCheckMate(boolean white) {
        return (white ? whiteStatus.gameStatus : blackStatus.gameStatus) == PlayerStatus.GameStatus.CHECKMATE;
    }

    public boolean inStaleMate(boolean white) {
        return (white ? whiteStatus.gameStatus : blackStatus.gameStatus) == PlayerStatus.GameStatus.STALEMATE;
    }

    /**
     * Checks status of the game with respect to the current player
     *
     * @return Zero if normal, One if checked, Two if stalemated, Three if checkmated
     */
    public int gameStatus() {
        PlayerStatus player = whiteToPlay ? whiteStatus : blackStatus;
        return player.gameStatus.ordinal();
    }

    /**
     * Updates the status of the game
     *
     * @param pieceMoved piece that was moved
     * @param move       move that was made
     */
    private void updateStatus(Piece pieceMoved, Move move) {
        if (pieceMoved.getType() == KING) {
            if (whiteToPlay) {
                whiteStatus.king = move.end();
            } else {
                blackStatus.king = move.end();
            }
        }

        // Refresh move sets
        whiteStatus.allPossibleMoves.clear();
        blackStatus.allPossibleMoves.clear();
        generateAllPossibleMoves(whiteStatus);
        generateAllPossibleMoves(blackStatus);

        PlayerStatus player = whiteToPlay ? whiteStatus : blackStatus;
        PlayerStatus opponent = whiteToPlay ? blackStatus : whiteStatus;
        boolean checked = checkIfInitiatedCheck(whiteToPlay, move);
        if (checked) {
            opponent.gameStatus = PlayerStatus.GameStatus.CHECK;
            boolean mated = checkIfInitiatedMate(whiteToPlay);
            if (mated) {
                opponent.gameStatus = PlayerStatus.GameStatus.CHECKMATE;
            }
        } else {
            boolean stalemate = checkIfInitiatedStalemate(whiteToPlay);
            if (stalemate) {
                player.gameStatus = PlayerStatus.GameStatus.STALEMATE;
                opponent.gameStatus = PlayerStatus.GameStatus.STALEMATE;
            } else {
                player.gameStatus = PlayerStatus.GameStatus.NORMAL;
                opponent.gameStatus = PlayerStatus.GameStatus.NORMAL;
            }
        }
    }

    /**
     * Checks if the player is checking opponent by checking if king is on dangerous square
     *
     * @param white player
     * @return true if checked
     */
    private boolean checkIfInitiatedCheck(boolean white, Move move) {
        PlayerStatus opponent = white ? blackStatus : whiteStatus;
        PlayerStatus player = white ? whiteStatus : blackStatus;
        getPiecePossibleMoves(move.end());
        return player.allPossibleMoves.values().stream().anyMatch(pieces -> pieces.stream().anyMatch(m -> m.end().equals(opponent.king)));
    }

    /**
     * Checks if the player is mated by checking if all possible moves result in king in check
     *
     * @param white player
     * @return true if mated
     */
    private boolean checkIfInitiatedMate(boolean white) {
        if (!inCheck(!white)) {
            throw new IllegalStateException("Player not in check, cannot be checkmate");
        }
        PlayerStatus opponent = white ? blackStatus : whiteStatus;
        return opponent.allPossibleMoves.values().stream().allMatch(Set::isEmpty);
    }

    /**
     * Checks if the player is stalemated by checking if there are no possible moves
     *
     * @param white player
     * @return true if stalemate
     */
    private boolean checkIfInitiatedStalemate(boolean white) {
        if (inCheck(!white)) {
            throw new IllegalStateException("Player in check, cannot be stalemate");
        }
        PlayerStatus opponent = white ? blackStatus : whiteStatus;
        return opponent.allPossibleMoves.values().stream().allMatch(Set::isEmpty);
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
    public static boolean inBounds(Notation pos) {
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
    public static boolean inBounds(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    /**
     * Returns the bitboard representation of the board
     *
     * @return bitboard
     */
    public int[][] getBitBoard() {
        int[][] bitBoard = new int[CHESS_BOARD.length][CHESS_BOARD[0].length];
        for (int i = 0; i < CHESS_BOARD.length; ++i) {
            for (int j = 0; j < CHESS_BOARD[i].length; ++j) {
                Piece piece = CHESS_BOARD[i][j];
                if (piece == null) {
                    continue;
                }
                bitBoard[i][j] = piece.getBitRepresentation();
            }
        }
        return bitBoard;
    }

    public void aiMove() {
        Move move = ai.getBestMove(whiteToPlay);
        movePiece(move);
    }

    public int evaluate(boolean whiteToPlay) {
        int score = 0;
        Piece.PieceColor currentColor = whiteToPlay ? WHITE : BLACK;
        for (Notation pos : Notation.ALL_VALUES) {
            Piece piece = getPiece(pos);
            if (piece == null) {
                continue;
            }
            if (isFriendly(currentColor, pos)) {
                score += piece.getScore(pos);
            } else {
                score -= piece.getScore(pos);
            }
        }
        return score;
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
