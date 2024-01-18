package ai;

import java.util.Arrays;

import static ai.BitBoards.*;
import static ai.Move.PieceType.*;

public class MoveGeneration {

    static boolean hasLegalMoves(BitBoards state) {
        Move[] moves = generateLegalMoves(state);
        return moves.length != 0;
    }

    /**
     * Generate all legal moves for the current state, which is a subset of all possible moves
     *
     * @param state current state
     * @return all legal moves
     */
    static Move[] generateLegalMoves(BitBoards state) {
        Move[] moves = generateMoves(state);
        int index = 0;
        for (Move move : moves) {
            if (Move.validate(state, move)) {
                moves[index++] = move;
            }
        }

        return Arrays.copyOf(moves, index);
    }


    /**
     * Generate all possible moves for the current state
     * <a href="https://chess.stackexchange.com/questions/4490/maximum-possible-movement-in-a-turn">Max: 218</a>
     *
     * @param state current state
     * @return all possible moves
     */
    private static Move[] generateMoves(BitBoards state) {
        Move[] moves = new Move[256];
        int index = 0;
        long friendlyPawns = state.whiteToMove ? state.whitePawns : state.blackPawns;
        long friendlyRooks = state.whiteToMove ? state.whiteRooks : state.blackRooks;
        long friendlyKnights = state.whiteToMove ? state.whiteKnights : state.blackKnights;
        long friendlyBishops = state.whiteToMove ? state.whiteBishops : state.blackBishops;
        long friendlyQueens = state.whiteToMove ? state.whiteQueens : state.blackQueens;
        long friendlyKing = state.whiteToMove ? state.whiteKing : state.blackKing;

        index = generatePawnMoves(state, moves, index, friendlyPawns);
        index = generateRookMoves(state, moves, index, friendlyRooks, false);
        index = generateKnightMoves(state, moves, index, friendlyKnights);
        index = generateBishopMoves(state, moves, index, friendlyBishops, false);
        index = generateQueenMoves(state, moves, index, friendlyQueens);
        index = generateKingMoves(state, moves, index, friendlyKing);
        if (index > 218) {
            throw new IllegalStateException("Invalid number of moves");
        }

        // Truncates array
        return Arrays.copyOf(moves, index);
    }

    private static int generatePawnMoves(BitBoards state, Move[] moves, int index, long friendlyPawns) {
        long singleMove = (state.whiteToMove ? (friendlyPawns << 8) : (friendlyPawns >>> 8)) & ~state.allPieces;
        long doubleMove = (state.whiteToMove ? ((singleMove & RANK_3) << 8) : ((singleMove & RANK_6) >>> 8)) & ~state.allPieces;

        // Pawn single moves
        while (singleMove != 0) {
            if (Long.bitCount(singleMove) > 8) {
                throw new IllegalStateException("Invalid number of pawn double moves");
            }

            int end = Long.numberOfTrailingZeros(singleMove);
            int start = state.whiteToMove ? end - 8 : end + 8;
            index = addPawnMove(moves, index, start, end, state.whiteToMove, false, state);
            singleMove ^= SQUARE_TO_BITBOARD[end];
        }

        // Pawn double moves
        while (doubleMove != 0) {
            if (Long.bitCount(doubleMove) > 8) {
                throw new IllegalStateException("Invalid number of pawn double moves");
            }

            int end = Long.numberOfTrailingZeros(doubleMove);
            int start = state.whiteToMove ? end - 16 : end + 16;
            index = addPawnMove(moves, index, start, end, state.whiteToMove, true, state);
            doubleMove ^= SQUARE_TO_BITBOARD[end];
        }

        // En passant
        if (state.enPassantIndex != -1) {
            long enPassant;
            if (state.whiteToMove) {
                enPassant = BLACK_PAWN_POSSIBLE_CAPTURES[state.enPassantIndex];
            } else {
                enPassant = WHITE_PAWN_POSSIBLE_CAPTURES[state.enPassantIndex];
            }
            if (Long.bitCount(enPassant) > 2) {
                throw new IllegalStateException("Invalid number of en passant moves");
            }

            enPassant &= friendlyPawns;
            while (enPassant != 0) {
                int start = Long.numberOfTrailingZeros(enPassant);
                moves[index++] = new Move(start, state.enPassantIndex, Move.MoveType.EN_PASSANT, PAWN);
                enPassant ^= SQUARE_TO_BITBOARD[start];
            }
        }

        // Pawn captures
        while (friendlyPawns != 0) {
            int start = Long.numberOfTrailingZeros(friendlyPawns);
            long pawnMoves = (state.whiteToMove ? WHITE_PAWN_POSSIBLE_CAPTURES[start] :
                    BLACK_PAWN_POSSIBLE_CAPTURES[start]) & state.blackPieces;
            if (Long.bitCount(pawnMoves) > 2) {
                throw new IllegalStateException("Invalid number of pawn moves");
            }

            while (pawnMoves != 0) {
                int end = Long.numberOfTrailingZeros(pawnMoves);
                index = addPawnMove(moves, index, start, end, state.whiteToMove, false, state);
                pawnMoves ^= SQUARE_TO_BITBOARD[end];
            }
            friendlyPawns ^= SQUARE_TO_BITBOARD[start];
        }

        return index;
    }

    private static int addPawnMove(Move[] moves, int index, int start, int end, boolean white,
                                   boolean doubleMove, BitBoards state) {
        boolean at_end = (white && (SQUARE_TO_BITBOARD[end] & RANK_8) != 0) || (!white && (SQUARE_TO_BITBOARD[end] & RANK_1) != 0);
        if (at_end) {
            for (Move.MoveType moveType : Move.MoveType.PROMOTION_TYPES) {
                moves[index++] = new Move(start, end, moveType, PAWN);
            }
        } else {
            moves[index++] = new Move(start, end, doubleMove ? Move.MoveType.PAWN_DOUBLE_MOVE :
                    Move.MoveType.NORMAL, PAWN);
        }

        return index;
    }

    private static int generateRookMoves(BitBoards state, Move[] moves, int index,
                                         long friendlyRooks, boolean fromQueen) {
        long friendlyPieces = state.whiteToMove ? state.whitePieces : state.blackPieces;

        while (friendlyRooks != 0) {
            int start = Long.numberOfTrailingZeros(friendlyRooks);
            long rookMoves = getRookAttacks(start, state.allPieces);
            rookMoves &= ~friendlyPieces;
            while (rookMoves != 0) {
                int end = Long.numberOfTrailingZeros(rookMoves);
                moves[index++] = new Move(start, end, Move.MoveType.NORMAL, fromQueen ? QUEEN : ROOK);
                rookMoves ^= SQUARE_TO_BITBOARD[end];
            }
            friendlyRooks ^= SQUARE_TO_BITBOARD[start];
        }

        return index;
    }

    /**
     * WARNING: Includes capturing own pieces as possible attacks
     *
     * @param rookIndex index of the rook to get attacks for
     * @param allPieces all pieces on the board
     * @return all possible rook attacks
     */
    static long getRookAttacks(int rookIndex, long allPieces) {
        long blockers = allPieces & ROOK_BLOCKER_MASK[rookIndex];
        int index = (int) ((blockers * ROOK_MAGICS[rookIndex]) >>> (64 - ROOK_RELEVANT_BITS[rookIndex]));
        return ROOK_ATTACKS[rookIndex][index];
    }

    private static int generateKnightMoves(BitBoards state, Move[] moves, int index, long friendlyKnights) {
        long friendlyPieces = state.whiteToMove ? state.whitePieces : state.blackPieces;

        while (friendlyKnights != 0) {
            int start = Long.numberOfTrailingZeros(friendlyKnights);
            long knightMoves = KNIGHT_POSSIBLE_MOVES[start];
            if (Long.bitCount(knightMoves) > 8) {
                throw new IllegalStateException("Invalid number of knight moves");
            }

            knightMoves &= ~friendlyPieces;
            while (knightMoves != 0) {
                int end = Long.numberOfTrailingZeros(knightMoves);
                moves[index++] = new Move(start, end, Move.MoveType.NORMAL, KNIGHT);
                knightMoves ^= SQUARE_TO_BITBOARD[end];
            }
            friendlyKnights ^= SQUARE_TO_BITBOARD[start];
        }

        return index;
    }

    private static int generateBishopMoves(BitBoards state, Move[] moves, int index,
                                           long friendlyBishops, boolean fromQueen) {
        long friendlyPieces = state.whiteToMove ? state.whitePieces : state.blackPieces;

        while (friendlyBishops != 0) {
            int start = Long.numberOfTrailingZeros(friendlyBishops);
            long bishopMoves = getBishopAttacks(start, state.allPieces);
            bishopMoves &= ~friendlyPieces;
            while (bishopMoves != 0) {
                int end = Long.numberOfTrailingZeros(bishopMoves);
                moves[index++] = new Move(start, end, Move.MoveType.NORMAL, fromQueen ? QUEEN : BISHOP);
                bishopMoves ^= SQUARE_TO_BITBOARD[end];
            }
            friendlyBishops ^= SQUARE_TO_BITBOARD[start];
        }

        return index;
    }

    /**
     * WARNING: Includes capturing own pieces as possible attacks
     *
     * @param bishopIndex index of the bishop to get attacks for
     * @param allPieces   all pieces on the board
     * @return all possible bishop attacks
     */
    static long getBishopAttacks(int bishopIndex, long allPieces) {
        long relevantSquares = allPieces & BISHOP_BLOCKER_MASK[bishopIndex];
        int index = (int) ((relevantSquares * BISHOP_MAGICS[bishopIndex]) >>> (64 - BISHOP_RELEVANT_BITS[bishopIndex]));
        return BISHOP_ATTACKS[bishopIndex][index];
    }

    private static int generateQueenMoves(BitBoards state, Move[] moves, int index, long friendlyQueens) {
        index = generateRookMoves(state, moves, index, friendlyQueens, true);
        index = generateBishopMoves(state, moves, index, friendlyQueens, true);

        return index;
    }

    private static int generateKingMoves(BitBoards state, Move[] moves, int index, long friendlyKing) {
        if (Long.bitCount(friendlyKing) != 1) {
            throw new IllegalStateException("Invalid number of friendly kings");
        }

        long friendlyPieces = state.whiteToMove ? state.whitePieces : state.blackPieces;

        int start = Long.numberOfTrailingZeros(friendlyKing);
        long kingMoves = KING_POSSIBLE_MOVES[start];
        kingMoves &= ~friendlyPieces;
        while (kingMoves != 0) {
            int end = Long.numberOfTrailingZeros(kingMoves);
            moves[index++] = new Move(start, end, Move.MoveType.NORMAL, KING);
            kingMoves ^= SQUARE_TO_BITBOARD[end];
        }

        return generateCastlingMoves(state, moves, index);
    }

    private static int generateCastlingMoves(BitBoards state, Move[] moves, int index) {
        if (state.whiteToMove) {
            // White left
            if ((state.castleRights & 0b1) != 0 && (state.allPieces & WHITE_KiNG_LEfT_CASTLE_OPEN) == 0) {
                moves[index++] = new Move(WHITE_KING_START, 2, Move.MoveType.CASTLE_LEFT,
                        KING);
            }
            // White right
            if ((state.castleRights & 0b10) != 0 && (state.allPieces & WHITE_KING_RIGHT_CASTLE_OPEN) == 0) {
                moves[index++] = new Move(WHITE_KING_START, 6, Move.MoveType.CASTLE_RIGHT,
                        KING);
            }
        } else {
            // Black left
            if ((state.castleRights & 0b100) != 0 && (state.allPieces & BLACK_KING_LEFT_CASTLE_OPEN) == 0) {
                moves[index++] = new Move(BLACK_KING_START, 58, Move.MoveType.CASTLE_LEFT, KING);
            }
            // Black right
            if ((state.castleRights & 0b1000) != 0 && (state.allPieces & BLACK_KING_RIGHT_CASTLE_OPEN) == 0) {
                moves[index++] = new Move(BLACK_KING_START, 62, Move.MoveType.CASTLE_RIGHT,
                        KING);
            }
        }
        return index;
    }
}