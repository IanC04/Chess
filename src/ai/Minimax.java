package ai;

/**
 * Minimax wrapper class
 */
public class Minimax {
    private final BitBoards bitBoards;

    public Minimax() {
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
     * @param maximizingPlayer
     * @return
     */
    private long minimax(int depth, int alpha, int beta, boolean maximizingPlayer) {
        return 0;
    }

    private long evaluateBoard() {
        return 0;
    }
}
