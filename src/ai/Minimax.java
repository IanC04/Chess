package ai;

import logic.Board;

/**
 * Minimax wrapper class
 */
public class Minimax {

    // Game state to evaluate
    private final Board STATE;

    private final BitBoards bitBoards;

    public Minimax(Board state) {
        this.STATE = state;
        this.bitBoards = new BitBoards();
    }

    /**
     * Get the best move for the current state, two bits set for each move
     * TODO: Implement
     *
     * @param FEN string encoding current board position
     * @return the best move
     */
    public long getBestMove(String FEN) {
        // bitBoards.generateBitBoards(FEN);
        // long bestMove = minimax(4, )
        return 0;
    }

    /**
     * Recursive minimax algorithm
     *
     * @param depth
     * @param alpha
     * @param beta
     * @return
     */
    private long minimax(int depth, Board board, int alpha, int beta, boolean maximizingPlayer) {
        return 0;
    }

    private long evaluateBoard(Board board) {
        return 0;
    }
}
