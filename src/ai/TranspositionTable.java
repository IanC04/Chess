package ai;

/**
 * Idea from
 * <a href="https://mediocrechess.blogspot.com/2007/01/guide-transposition-tables.html">Website</a>
 */
public class TranspositionTable {
    private static class HashEntry {
        long zobrist;
        int depth;
        int flag;
        int eval;
        int ancient;
        Move move;

        HashEntry(long zobrist, int depth, int flag,
                  int eval, int ancient, Move move) {
            this.zobrist = zobrist;
            this.depth = depth;
            this.flag = flag;
            this.eval = eval;
            this.ancient = ancient;
            this.move = move;
        }
    }
}
