package ai;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Minimax {
    private final HashMap<String, Move[]> OPENING_BOOK;

    // TODO: Implement transposition table
    private final TranspositionTable TRANSPOSITION_TABLE = new TranspositionTable();

    public Minimax() {
        this.OPENING_BOOK = new HashMap<>();
        parseOpeningBook();
        // parseMagicBitBoards();
    }

    private void parseOpeningBook() {
        try (Scanner scanner = new Scanner(new File("src/ai/Computations/opening_book.txt"))) {
            String position = null;
            List<Move> currentMoves = new ArrayList<>();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                // Skip comments and empty lines
                if (line.startsWith("#") || line.isBlank()) {
                    continue;
                }
                if (line.startsWith("pos")) {
                    if (position != null) {
                        OPENING_BOOK.put(position, currentMoves.toArray(new Move[0]));
                        currentMoves.clear();
                    }
                    position = line.substring(4);
                    continue;
                }
                String[] args = line.split(" ");
                int start = Move.notationToIndex(args[0].substring(0, 2));
                int end = Move.notationToIndex(args[0].substring(2));
                int value = Integer.parseInt(args[1]);
                Move move = new Move(start, end, value);
                currentMoves.add(move);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Opening book not found");
        } catch (StringIndexOutOfBoundsException e) {
            System.err.println("Opening book is malformed");
        }
    }

    private String checkOpeningBook(String FEN) {
        // Opening moves
        String bookKey = removeMoveCounter(FEN);
        // System.out.println("Key: " + bookKey);
        if (OPENING_BOOK.containsKey(bookKey)) {
            Move[] moves = OPENING_BOOK.get(bookKey);
            Move move = moves[(int) (Math.random() * moves.length)];
            return Move.indexToNotation(move.start()) + Move.indexToNotation(move.end());
        } else if (!bookKey.endsWith("-")) {
            bookKey = bookKey.substring(0, bookKey.lastIndexOf(' ')) + " -";
            if (OPENING_BOOK.containsKey(bookKey)) {
                Move[] moves = OPENING_BOOK.get(bookKey);
                Move move = moves[(int) (Math.random() * moves.length)];
                return Move.indexToNotation(move.start()) + Move.indexToNotation(move.end());
            }
        }

        return null;
    }

    /**
     * Get the best move for the current state
     *
     * @param FEN string encoding current board position
     * @return the best move NOT in algebraic notation. Format: "a1a2 T"
     */
    public String getBestMove(String FEN) {
        String openingMove = checkOpeningBook(FEN);
        if (openingMove != null) {
            return openingMove;
        }
        BitBoards state = new BitBoards(FEN);
        Move bestMove = rootNegaMax(state, 5, Integer.MIN_VALUE, Integer.MAX_VALUE,
                state.whiteToMove);
        if (bestMove == null) {
            throw new IllegalStateException("No move found");
        }
        return bestMove.toString();
    }

    private String removeMoveCounter(String FEN) {
        return FEN.substring(0, FEN.substring(0, FEN.lastIndexOf(' ')).lastIndexOf(' '));
    }

    /**
     * Recursive minimax algorithm
     *
     * @param depth current depth
     * @param alpha minimum score
     * @param beta  maximum score
     * @return the best move
     */
    private Move rootNegaMax(BitBoards state, int depth, int alpha, int beta, boolean color) {
        if (depth == 0) {
            throw new IllegalArgumentException("Depth must be greater than 0");
        }

        Move[] allMoves = MoveGeneration.generateLegalMoves(state);
        Move bestMove = new Move();
        for (Move move : allMoves) {
            BitBoards newState = state.makeMove(move);
            int score = -negaMax(newState, depth - 1, -beta, -alpha, !color);
            if (score > bestMove.value()) {
                bestMove = new Move(move.start(), move.end(), move.moveType(),
                        move.pieceType(), score);
            }
        }
        return bestMove;
    }

    /**
     * Recursive negamax algorithm
     * TODO: Fix algorithm
     *
     * @param state current state
     * @param depth current depth
     * @param alpha minimum score
     * @param beta maximum score
     * @param color color to move
     * @return the best score
     */
    private int negaMax(BitBoards state, int depth, int alpha, int beta, boolean color) {
        if (depth == 0 || state.gameOver()) {
            return state.evaluateBoard();
        }

        Move[] allMoves = MoveGeneration.generateLegalMoves(state);
        int bestValue = Integer.MIN_VALUE;
        for (Move move : allMoves) {
            int value = -negaMax(state.makeMove(move), depth - 1, -beta, -alpha, !color);
            bestValue = Math.max(bestValue, value);
            alpha = Math.max(alpha, value);
            if (alpha >= beta) {
                break;
            }
        }
        return bestValue;
    }

}
