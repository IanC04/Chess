package ai;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Minimax {
    private final HashMap<String, Move[]> OPENING_BOOK;

    // TODO: Implement
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
                Move move = new Move(args[0].substring(0, 2), args[0].substring(2),
                        Integer.parseInt(args[1]));
                currentMoves.add(move);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Opening book not found");
        } catch (StringIndexOutOfBoundsException e) {
            System.err.println("Opening book is malformed");
        }
    }

    /**
     * Get the best move for the current state
     * TODO: Implement
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
        Move bestMove = rootNegaMax(state, 4, Integer.MIN_VALUE, Integer.MAX_VALUE,
                state.whiteToMove);
        if (bestMove == null) {
            throw new IllegalStateException("No move found");
        }
        return bestMove.toString();
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
            return new Move();
        }

        Move[] allMoves = MoveGeneration.generateOrderedMoves(state);
        if (allMoves == null || allMoves.length == 0) {
            return new Move();
        }
        Move bestMove = new Move();

        for (Move move : allMoves) {
            System.out.println(move);
            int score = -negaMax(state.makeMove(move), depth - 1, -beta, -alpha,!color);
            if (score > bestMove.value()) {
                bestMove = new Move(move.start(), move.end(), move.moveType(), score);
            }
        }
        return bestMove;
    }

    private int negaMax(BitBoards state, int depth, int alpha, int beta, boolean color) {
        if (depth == 0) {
            return state.evaluateBoard();
        }

        Move[] allMoves = MoveGeneration.generateOrderedMoves(state);
        if (allMoves == null || allMoves.length == 0) {
            return state.evaluateBoard();
        }

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
