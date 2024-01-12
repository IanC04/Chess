package ai;

import static ai.BitBoards.*;

public class MoveGeneration {

    /**
     * Generate all possible moves for the current state, 256 max
     * <a href="https://chess.stackexchange.com/questions/4490/maximum-possible-movement-in-a-turn">Source</a>
     *
     * @param state current state
     * @return all possible moves
     */
    static Move[] generateOrderedMoves(BitBoards state) {
        Move[] moves = new Move[256];
        int index = 0;

        long friendlyPawns = state.whiteToMove ? state.whitePawns : state.blackPawns;
        long friendlyRooks = state.whiteToMove ? state.whiteRooks : state.blackRooks;
        long friendlyKnights = state.whiteToMove ? state.whiteKnights : state.blackKnights;
        long friendlyBishops = state.whiteToMove ? state.whiteBishops : state.blackBishops;
        long friendlyQueens = state.whiteToMove ? state.whiteQueens : state.blackQueens;
        long friendlyKing = state.whiteToMove ? state.whiteKing : state.blackKing;

        // Pawn moves
        long singleMove = (state.whiteToMove ? (friendlyPawns << 8) : (friendlyPawns >>> 8)) & ~state.allPieces;
        long doubleMove = (state.whiteToMove ? ((singleMove & RANK_3) << 8) : ((singleMove & RANK_6) >>> 8)) & ~state.allPieces;

        // Pawn captures
        while (singleMove != 0) {
            int end = Long.numberOfTrailingZeros(singleMove);
            int start = state.whiteToMove ? end - 8 : end + 8;
            index = addPawnMove(moves, index, start, end, state.whiteToMove, state);
            singleMove &= singleMove - 1;
        }
        return null;
    }

    private static int addPawnMove(Move[] moves, int index, int start, int end, boolean white,
                                   BitBoards state) {
        Move move;
        if (white) {
            if ((SQUARE_TO_BITBOARD[end] & RANK_8) != 0) {
                for (Move.MoveType moveType : Move.MoveType.PROMOTION_TYPES) {
                    move = new Move(start, end, moveType);
                    if (Move.validate(state, move)) {
                        moves[index++] = move;
                    }
                }
            } else {
                move = new Move(start, end, Move.MoveType.NORMAL);
                if (Move.validate(state, move)) {
                    moves[index++] = move;
                }
            }
        }
        else {
            if ((SQUARE_TO_BITBOARD[end] & RANK_1) != 0) {
                for (Move.MoveType moveType : Move.MoveType.PROMOTION_TYPES) {
                    move = new Move(start, end, moveType);
                    if (Move.validate(state, move)) {
                        moves[index++] = move;
                    }
                }
            } else {
                move = new Move(start, end, Move.MoveType.NORMAL);
                if (Move.validate(state, move)) {
                    moves[index++] = move;
                }
            }
        }

        return index;
    }
}
