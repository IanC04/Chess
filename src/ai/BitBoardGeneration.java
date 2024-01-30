package ai;

import static ai.BitBoards.*;

class BitBoardGeneration {

    /**
     * Square bitboards generation
     */
    private long[] SQUARE_TO_BITBOARD_Generation() {
        long[] SQUARE_TO_BITBOARD = new long[64];
        long position = 1L;
        for (int i = 0; i < 64; ++i) {
            SQUARE_TO_BITBOARD[i] = position;
            position <<= 1;
        }

        System.out.println("{");
        for (long i : SQUARE_TO_BITBOARD) {
            System.out.println("0x" + Long.toHexString(i) + "L,");
        }
        System.out.println("};");

        return SQUARE_TO_BITBOARD;
    }

    /**
     * Pawn move generation
     */
    private long[][] PAWN_POSSIBLE_CAPTURES_Generation() {
        long[] WHITE_PAWN_POSSIBLE_CAPTURES = new long[64];
        for (int i = A2; i < A8; i++) {
            long squarePossibleCaptures = 0L;
            if (i % 8 != 0) {
                squarePossibleCaptures |= SQUARE_TO_BITBOARD[i + 7];
            }
            if (i % 8 != 7) {
                squarePossibleCaptures |= SQUARE_TO_BITBOARD[i + 9];
            }
            WHITE_PAWN_POSSIBLE_CAPTURES[i] = squarePossibleCaptures;
        }

        System.out.println("{");
        for (long i : WHITE_PAWN_POSSIBLE_CAPTURES) {
            System.out.println("0x" + Long.toHexString(i) + "L,");
        }
        System.out.println("};");

        long[] BLACK_PAWN_POSSIBLE_CAPTURES = new long[64];
        for (int i = A2; i < A8; i++) {
            long squarePossibleCaptures = 0L;
            if (i % 8 != 0) {
                squarePossibleCaptures |= SQUARE_TO_BITBOARD[i - 9];
            }
            if (i % 8 != 7) {
                squarePossibleCaptures |= SQUARE_TO_BITBOARD[i - 7];
            }
            BLACK_PAWN_POSSIBLE_CAPTURES[i] = squarePossibleCaptures;
        }

        System.out.println("{");
        for (long i : BLACK_PAWN_POSSIBLE_CAPTURES) {
            System.out.println("0x" + Long.toHexString(i) + "L,");
        }
        System.out.println("}");

        return new long[][]{WHITE_PAWN_POSSIBLE_CAPTURES, BLACK_PAWN_POSSIBLE_CAPTURES};
    }

    /**
     * Knight move generation
     */

    private long[] KNIGHT_POSSIBLE_MOVES_Generation() {
        long[] KNIGHT_POSSIBLE_MOVES = new long[64];
        for (int i = A1; i <= H8; i++) {
            long squarePossibleMoves = 0L;
            int rank = i / 8, file = i % 8;
            if (rank < 6 && file < 7) {
                squarePossibleMoves |= SQUARE_TO_BITBOARD[i + 17];
            }
            if (rank < 6 && file > 0) {
                squarePossibleMoves |= SQUARE_TO_BITBOARD[i + 15];
            }
            if (rank < 7 && file < 6) {
                squarePossibleMoves |= SQUARE_TO_BITBOARD[i + 10];
            }
            if (rank < 7 && file > 1) {
                squarePossibleMoves |= SQUARE_TO_BITBOARD[i + 6];
            }
            if (rank > 0 && file < 6) {
                squarePossibleMoves |= SQUARE_TO_BITBOARD[i - 6];
            }
            if (rank > 0 && file > 1) {
                squarePossibleMoves |= SQUARE_TO_BITBOARD[i - 10];
            }
            if (rank > 1 && file < 7) {
                squarePossibleMoves |= SQUARE_TO_BITBOARD[i - 15];
            }
            if (rank > 1 && file > 0) {
                squarePossibleMoves |= SQUARE_TO_BITBOARD[i - 17];
            }
            KNIGHT_POSSIBLE_MOVES[i] = squarePossibleMoves;
        }

        System.out.println("{");
        for (long i : KNIGHT_POSSIBLE_MOVES) {
            System.out.println("0x" + Long.toHexString(i) + "L,");
        }
        System.out.println("};");

        return KNIGHT_POSSIBLE_MOVES;
    }

    /**
     * King move generation
     */


    private long[] KING_POSSIBLE_MOVES_Generation() {
        long[] KING_POSSIBLE_MOVES = new long[64];
        for (int i = 0; i <= H8; i++) {
            long squarePossibleMoves = 0L;
            if (i % 8 != 0) {
                squarePossibleMoves |= SQUARE_TO_BITBOARD[i - 1];
                if (i < 56) {
                    squarePossibleMoves |= SQUARE_TO_BITBOARD[i + 7];
                }
                if (i > 7) {
                    squarePossibleMoves |= SQUARE_TO_BITBOARD[i - 9];
                }
            }
            if (i % 8 != 7) {
                squarePossibleMoves |= SQUARE_TO_BITBOARD[i + 1];
                if (i < 56) {
                    squarePossibleMoves |= SQUARE_TO_BITBOARD[i + 9];
                }
                if (i > 7) {
                    squarePossibleMoves |= SQUARE_TO_BITBOARD[i - 7];
                }
            }
            if (i < 56) {
                squarePossibleMoves |= SQUARE_TO_BITBOARD[i + 8];
            }
            if (i > 7) {
                squarePossibleMoves |= SQUARE_TO_BITBOARD[i - 8];
            }
            KING_POSSIBLE_MOVES[i] = squarePossibleMoves;
        }

        System.out.println("{");
        for (long i : KING_POSSIBLE_MOVES) {
            System.out.println("0x" + Long.toHexString(i) + "L,");
        }
        System.out.println("};");

        return KING_POSSIBLE_MOVES;
    }

    /**
     * Rook and bishop blocker mask generation
     */
    private long[][] ROOK_AND_BISHOP_BLOCKER_MASK_Generation() {
        long[] ROOK_BLOCKER_MASK = new long[64];
        for (int i = A1; i <= H8; i++) {
            long squarePossibleMoves = 0L;
            for (int up = i + 8; up < A8; up += 8) {
                squarePossibleMoves |= SQUARE_TO_BITBOARD[up];
            }
            for (int down = i - 8; down > H1; down -= 8) {
                squarePossibleMoves |= SQUARE_TO_BITBOARD[down];
            }
            for (int left = i - 1; left >= 0 && left % 8 != 7 && left % 8 != 0; left--) {
                squarePossibleMoves |= SQUARE_TO_BITBOARD[left];
            }
            for (int right = i + 1; right < 64 && right % 8 != 7 && right % 8 != 0; right++) {
                squarePossibleMoves |= SQUARE_TO_BITBOARD[right];
            }
            ROOK_BLOCKER_MASK[i] = squarePossibleMoves;
        }

        long[] BISHOP_BLOCKER_MASK = new long[64];
        for (int i = A1; i <= H8; i++) {
            long squarePossibleMoves = 0L;
            for (int upLeft = i + 7; upLeft < A8 && upLeft % 8 != 0 && upLeft % 8 != 7; upLeft += 7) {
                squarePossibleMoves |= SQUARE_TO_BITBOARD[upLeft];
            }
            for (int upRight = i + 9; upRight < A8 && upRight % 8 != 0 && upRight % 8 != 7; upRight += 9) {
                squarePossibleMoves |= SQUARE_TO_BITBOARD[upRight];
            }
            for (int downLeft = i - 9; downLeft > H1 && downLeft % 8 != 0 && downLeft % 8 != 7; downLeft -= 9) {
                squarePossibleMoves |= SQUARE_TO_BITBOARD[downLeft];
            }
            for (int downRight = i - 7; downRight > H1 && downRight % 8 != 0 && downRight % 8 != 7; downRight -= 7) {
                squarePossibleMoves |= SQUARE_TO_BITBOARD[downRight];
            }
            BISHOP_BLOCKER_MASK[i] = squarePossibleMoves;
        }

        // Display Bishop blocker mask
        for (Long i : BISHOP_BLOCKER_MASK) {
            String temp = Long.toBinaryString(i);
            temp = new StringBuilder(temp).reverse().toString();
            temp = temp + "0".repeat(64 - temp.length());
            for (int j = 7; j >= 0; --j) {
                for (int k = 0; k < 8; k++) {
                    System.out.print(temp.charAt(j * 8 + k));
                }
                System.out.println();
            }
            System.out.println();
        }

        // Display Rook blocker mask
        System.out.println("{");
        for (long i : ROOK_BLOCKER_MASK) {
            System.out.println("0x" + Long.toHexString(i) + "L,");
        }
        System.out.println("};");

        System.out.println("{");
        for (long i : BISHOP_BLOCKER_MASK) {
            System.out.println("0x" + Long.toHexString(i) + "L,");
        }
        System.out.println("};");

        return new long[][]{ROOK_BLOCKER_MASK, BISHOP_BLOCKER_MASK};
    }

    // Unused
    /*
    static final long[] ROOK_POSSIBLE_MOVES = {0x1010101010101feL, 0x2020202020202fdL,
            0x4040404040404fbL, 0x8080808080808f7L, 0x10101010101010efL, 0x20202020202020dfL,
            0x40404040404040bfL, 0x808080808080807fL, 0x10101010101fe01L, 0x20202020202fd02L,
            0x40404040404fb04L, 0x80808080808f708L, 0x101010101010ef10L, 0x202020202020df20L,
            0x404040404040bf40L, 0x8080808080807f80L, 0x101010101fe0101L, 0x202020202fd0202L,
            0x404040404fb0404L, 0x808080808f70808L, 0x1010101010ef1010L, 0x2020202020df2020L,
            0x4040404040bf4040L, 0x80808080807f8080L, 0x1010101fe010101L, 0x2020202fd020202L,
            0x4040404fb040404L, 0x8080808f7080808L, 0x10101010ef101010L, 0x20202020df202020L,
            0x40404040bf404040L, 0x808080807f808080L, 0x10101fe01010101L, 0x20202fd02020202L,
            0x40404fb04040404L, 0x80808f708080808L, 0x101010ef10101010L, 0x202020df20202020L,
            0x404040bf40404040L, 0x8080807f80808080L, 0x101fe0101010101L, 0x202fd0202020202L,
            0x404fb0404040404L, 0x808f70808080808L, 0x1010ef1010101010L, 0x2020df2020202020L,
            0x4040bf4040404040L, 0x80807f8080808080L, 0x1fe010101010101L, 0x2fd020202020202L,
            0x4fb040404040404L, 0x8f7080808080808L, 0x10ef101010101010L, 0x20df202020202020L,
            0x40bf404040404040L, 0x807f808080808080L, 0xfe01010101010101L, 0xfd02020202020202L,
            0xfb04040404040404L, 0xf708080808080808L, 0xef10101010101010L, 0xdf20202020202020L,
            0xbf40404040404040L, 0x7f80808080808080L};

    static final long[] BISHOP_POSSIBLE_MOVES = {0x40201008040200L, 0x402010080400L,
            0x4020100a00L, 0x40221400L, 0x2442800L, 0x204085000L, 0x20408102000L,
            0x2040810204000L, 0x20100804020000L, 0x40201008040000L, 0x4020100a0000L,
            0x4022140000L, 0x244280000L, 0x20408500000L, 0x2040810200000L, 0x4081020400000L,
            0x10080402000200L, 0x20100804000400L, 0x4020100a000a00L, 0x402214001400L,
            0x24428002800L, 0x2040850005000L, 0x4081020002000L, 0x8102040004000L,
            0x8040200020400L, 0x10080400040800L, 0x20100a000a1000L, 0x40221400142200L,
            0x2442800284400L, 0x4085000500800L, 0x8102000201000L, 0x10204000402000L,
            0x4020002040800L, 0x8040004081000L, 0x100a000a102000L, 0x22140014224000L,
            0x44280028440200L, 0x8500050080400L, 0x10200020100800L, 0x20400040201000L,
            0x2000204081000L, 0x4000408102000L, 0xa000a10204000L, 0x14001422400000L,
            0x28002844020000L, 0x50005008040200L, 0x20002010080400L, 0x40004020100800L,
            0x20408102000L, 0x40810204000L, 0xa1020400000L, 0x142240000000L, 0x284402000000L,
            0x500804020000L, 0x201008040200L, 0x402010080400L, 0x2040810204000L,
            0x4081020400000L, 0xa102040000000L, 0x14224000000000L, 0x28440200000000L,
            0x50080402000000L, 0x20100804020000L, 0x40201008040200L};*/

    /**
     * Rook and bishop move generation
     */
    private long[][] ROOK_AND_BISHOP_POSSIBLE_MOVES_Generation() {
        long[] ROOK_POSSIBLE_MOVES = new long[64];
        for (int i = A1; i <= H8; i++) {
            long squarePossibleMoves = 0L;
            for (int up = i + 8; up <= H8; up += 8) {
                squarePossibleMoves |= SQUARE_TO_BITBOARD[up];
            }
            for (int down = i - 8; down >= A1; down -= 8) {
                squarePossibleMoves |= SQUARE_TO_BITBOARD[down];
            }
            for (int left = i - 1; left >= 0 && left % 8 != 7; left--) {
                squarePossibleMoves |= SQUARE_TO_BITBOARD[left];
            }
            for (int right = i + 1; right < 64 && right % 8 != 0; right++) {
                squarePossibleMoves |= SQUARE_TO_BITBOARD[right];
            }
            ROOK_POSSIBLE_MOVES[i] = squarePossibleMoves;
        }

        long[] BISHOP_POSSIBLE_MOVES = new long[64];
        for (int i = A1; i <= H8; i++) {
            long squarePossibleMoves = 0L;
            for (int upLeft = i + 7; upLeft <= H8 && upLeft % 8 != 7; upLeft += 7) {
                squarePossibleMoves |= SQUARE_TO_BITBOARD[upLeft];
            }
            for (int upRight = i + 9; upRight <= H8 && upRight % 8 != 0; upRight += 9) {
                squarePossibleMoves |= SQUARE_TO_BITBOARD[upRight];
            }
            for (int downLeft = i - 9; downLeft >= A1 && downLeft % 8 != 7; downLeft -= 9) {
                squarePossibleMoves |= SQUARE_TO_BITBOARD[downLeft];
            }
            for (int downRight = i - 7; downRight >= A1 && downRight % 8 != 0; downRight -= 7) {
                squarePossibleMoves |= SQUARE_TO_BITBOARD[downRight];
            }
            BISHOP_POSSIBLE_MOVES[i] = squarePossibleMoves;
        }

        // Display Bishop possible moves
        for (Long i : BISHOP_POSSIBLE_MOVES) {
            String temp = Long.toBinaryString(i);
            temp = new StringBuilder(temp).reverse().toString();
            temp = temp + "0".repeat(64 - temp.length());
            for (int j = 7; j >= 0; --j) {
                for (int k = 0; k < 8; k++) {
                    System.out.print(temp.charAt(j * 8 + k));
                }
                System.out.println();
            }
            System.out.println();
        }

        // Display Rook possible moves
        System.out.println("{");
        for (long i : ROOK_POSSIBLE_MOVES) {
            System.out.println("0x" + Long.toHexString(i) + "L,");
        }
        System.out.println("};");

        System.out.println("{");
        for (long i : BISHOP_BLOCKER_MASK) {
            System.out.println("0x" + Long.toHexString(i) + "L,");
        }
        System.out.println("};");

        return new long[][]{ROOK_POSSIBLE_MOVES, BISHOP_POSSIBLE_MOVES};
    }
}
