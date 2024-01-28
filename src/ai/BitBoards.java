package ai;

import java.util.Arrays;

class BitBoards {
    static final int PAWN_VAL = 100, ROOK_VAL = 500, KNIGHT_VAL = 300, BISHOP_VAL = 300,
            QUEEN_VAL = 900, KING_VAL = 10000;
    /**
     * Returns the positional value of the piece with respect to the board from white's POV
     * <br>Inspired by
     * <a href="https://github.com/bartekspitza/sophia/blob/master/src/evaluation.c">GitHub</a>
     */
    static final int[] WHITE_PAWN_POS_VALUES = {
            0, 0, 0, 0, 0, 0, 0, 0,
            5, 10, 10, -20, -20, 10, 10, 5,
            5, -5, -10, 0, 0, -10, -5, 5,
            0, 0, 0, 20, 20, 0, 0, 0,
            5, 5, 10, 25, 25, 10, 5, 5,
            10, 10, 20, 30, 30, 20, 10, 10,
            50, 50, 50, 50, 50, 50, 50, 50,
            0, 0, 0, 0, 0, 0, 0, 0};

    static final int[] BLACK_PAWN_POS_VALUES = new int[64];

    static {
        for (int i = 0; i < 64; i++) {
            BLACK_PAWN_POS_VALUES[i] = WHITE_PAWN_POS_VALUES[63 - i];
        }
    }

    static final int[] WHITE_KNIGHT_POS_VALUES = {
            -50, -40, -30, -30, -30, -30, -40, -50,
            -40, -20, 0, 5, 5, 0, -20, -40,
            -30, 5, 10, 15, 15, 10, 5, -30,
            -30, 0, 15, 20, 20, 15, 0, -30,
            -30, 5, 15, 20, 20, 15, 5, -30,
            -30, 0, 10, 15, 15, 10, 0, -30,
            -40, -20, 0, 0, 0, 0, -20, -40,
            -50, -40, -30, -30, -30, -30, -40, -50};
    static final int[] BLACK_KNIGHT_POS_VALUES = new int[64];

    static {
        for (int i = 0; i < 64; i++) {
            BLACK_KNIGHT_POS_VALUES[i] = WHITE_KNIGHT_POS_VALUES[63 - i];
        }
    }

    static final int[] WHITE_BISHOP_POS_VALUES = {
            -20, -10, -10, -10, -10, -10, -10, -20,
            -10, 5, 0, 0, 0, 0, 5, -10,
            -10, 10, 10, 10, 10, 10, 10, -10,
            -10, 0, 10, 10, 10, 10, 0, -10,
            -10, 5, 5, 10, 10, 5, 5, -10,
            -10, 0, 5, 10, 10, 5, 0, -10,
            -10, 0, 0, 0, 0, 0, 0, -10,
            -20, -10, -10, -10, -10, -10, -10, -20};
    static final int[] BLACK_BISHOP_POS_VALUES = new int[64];

    static {
        for (int i = 0; i < 64; i++) {
            BLACK_BISHOP_POS_VALUES[i] = WHITE_BISHOP_POS_VALUES[63 - i];
        }
    }

    static final int[] WHITE_ROOK_POS_VALUES = {
            0, 0, 5, 10, 10, 5, 0, 0,
            -5, 0, 0, 0, 0, 0, 0, -5,
            -5, 0, 0, 0, 0, 0, 0, -5,
            -5, 0, 0, 0, 0, 0, 0, -5,
            -5, 0, 0, 0, 0, 0, 0, -5,
            -5, 0, 0, 0, 0, 0, 0, -5,
            5, 10, 10, 10, 10, 10, 10, 5,
            0, 0, 0, 0, 0, 0, 0, 0};
    static final int[] BLACK_ROOK_POS_VALUES = new int[64];

    static {
        for (int i = 0; i < 64; i++) {
            BLACK_ROOK_POS_VALUES[i] = WHITE_ROOK_POS_VALUES[63 - i];
        }
    }

    static final int[] WHITE_QUEEN_POS_VALUES = {
            -20, -10, -10, -5, -5, -10, -10, -20,
            -10, 0, 5, 0, 0, 0, 0, -10,
            -10, 5, 5, 5, 5, 5, 0, -10,
            0, 0, 5, 5, 5, 5, 0, -5,
            -5, 0, 5, 5, 5, 5, 0, -5,
            -10, 0, 5, 5, 5, 5, 0, -10,
            -10, 0, 0, 0, 0, 0, 0, -10,
            -20, -10, -10, -5, -5, -10, -10, -20};

    static final int[] BLACK_QUEEN_POS_VALUES = new int[64];

    static {
        for (int i = 0; i < 64; i++) {
            BLACK_QUEEN_POS_VALUES[i] = WHITE_QUEEN_POS_VALUES[63 - i];
        }
    }

    static final int[] WHITE_KING_POS_VALUES = {
            20, 30, 10, 0, 0, 10, 30, 20,
            20, 20, 0, 0, 0, 0, 20, 20,
            -10, -20, -20, -20, -20, -20, -20, -10,
            -20, -30, -30, -40, -40, -30, -30, -20,
            -30, -40, -40, -50, -50, -40, -40, -30,
            -30, -40, -40, -50, -50, -40, -40, -30,
            -30, -40, -40, -50, -50, -40, -40, -30,
            -30, -40, -40, -50, -50, -40, -40, -30};
    static final int[] BLACK_KING_POS_VALUES = new int[64];

    static {
        for (int i = 0; i < 64; i++) {
            BLACK_KING_POS_VALUES[i] = WHITE_KING_POS_VALUES[63 - i];
        }
    }

    static final int A1 = 0, H1 = 7, A2 = 8, A8 = 56, H8 = 63,
            WHITE_KING_START = 4,
            BLACK_KING_START = 60;
    static final long RANK_1 = 0xFFL, RANK_2 = 0xFFL << 8, RANK_3 = 0xFFL << 16, RANK_4 =
            0xFFL << 24, RANK_5 = 0xFFL << 32, RANK_6 = 0xFFL << 40, RANK_7 = 0xFFL << 48, RANK_8
            = 0xFFL << 56, WHITE_KiNG_LEfT_CASTLE_OPEN = 0xEL, WHITE_KING_RIGHT_CASTLE_OPEN = 0x60L,
            BLACK_KING_LEFT_CASTLE_OPEN = 0xEL << 56, BLACK_KING_RIGHT_CASTLE_OPEN =
            0x60L << 56, WHITE_KING_LEFT_SAFE_NEEDED = 0x1CL, WHITE_KING_RIGHT_SAFE_NEEDED = 0x70L,
            BLACK_KING_LEFT_SAFE_NEEDED = 0x1CL << 56, BLACK_KING_RIGHT_SAFE_NEEDED = 0x70L << 56;
    static final long[] SQUARE_TO_BITBOARD = {0x1L, 0x2L, 0x4L, 0x8L, 0x10L, 0x20L, 0x40L, 0x80L
            , 0x100L, 0x200L, 0x400L, 0x800L, 0x1000L, 0x2000L, 0x4000L, 0x8000L, 0x10000L,
            0x20000L, 0x40000L, 0x80000L, 0x100000L, 0x200000L, 0x400000L, 0x800000L, 0x1000000L
            , 0x2000000L, 0x4000000L, 0x8000000L, 0x10000000L, 0x20000000L, 0x40000000L,
            0x80000000L, 0x100000000L, 0x200000000L, 0x400000000L, 0x800000000L, 0x1000000000L,
            0x2000000000L, 0x4000000000L, 0x8000000000L, 0x10000000000L, 0x20000000000L,
            0x40000000000L, 0x80000000000L, 0x100000000000L, 0x200000000000L, 0x400000000000L,
            0x800000000000L, 0x1000000000000L, 0x2000000000000L, 0x4000000000000L,
            0x8000000000000L, 0x10000000000000L, 0x20000000000000L, 0x40000000000000L,
            0x80000000000000L, 0x100000000000000L, 0x200000000000000L, 0x400000000000000L,
            0x800000000000000L, 0x1000000000000000L, 0x2000000000000000L, 0x4000000000000000L,
            0x8000000000000000L};

    // Square bitboards generation
    /*static final long[] SQUARE_TO_BITBOARD = new long[64];

    static {
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
    }*/

    static final long[] WHITE_PAWN_POSSIBLE_CAPTURES = {0x0L, 0x0L, 0x0L, 0x0L, 0x0L, 0x0L, 0x0L,
            0x0L, 0x20000L, 0x50000L, 0xa0000L, 0x140000L, 0x280000L, 0x500000L, 0xa00000L,
            0x400000L, 0x2000000L, 0x5000000L, 0xa000000L, 0x14000000L, 0x28000000L, 0x50000000L
            , 0xa0000000L, 0x40000000L, 0x200000000L, 0x500000000L, 0xa00000000L, 0x1400000000L,
            0x2800000000L, 0x5000000000L, 0xa000000000L, 0x4000000000L, 0x20000000000L,
            0x50000000000L, 0xa0000000000L, 0x140000000000L, 0x280000000000L, 0x500000000000L,
            0xa00000000000L, 0x400000000000L, 0x2000000000000L, 0x5000000000000L,
            0xa000000000000L, 0x14000000000000L, 0x28000000000000L, 0x50000000000000L,
            0xa0000000000000L, 0x40000000000000L, 0x200000000000000L, 0x500000000000000L,
            0xa00000000000000L, 0x1400000000000000L, 0x2800000000000000L, 0x5000000000000000L,
            0xa000000000000000L, 0x4000000000000000L, 0x0L, 0x0L, 0x0L, 0x0L, 0x0L, 0x0L, 0x0L,
            0x0L};
    static final long[] BLACK_PAWN_POSSIBLE_CAPTURES = {0x0L, 0x0L, 0x0L, 0x0L, 0x0L, 0x0L, 0x0L,
            0x0L, 0x2L, 0x5L, 0xaL, 0x14L, 0x28L, 0x50L, 0xa0L, 0x40L, 0x200L, 0x500L, 0xa00L,
            0x1400L, 0x2800L, 0x5000L, 0xa000L, 0x4000L, 0x20000L, 0x50000L, 0xa0000L, 0x140000L,
            0x280000L, 0x500000L, 0xa00000L, 0x400000L, 0x2000000L, 0x5000000L, 0xa000000L,
            0x14000000L, 0x28000000L, 0x50000000L, 0xa0000000L, 0x40000000L, 0x200000000L,
            0x500000000L, 0xa00000000L, 0x1400000000L, 0x2800000000L, 0x5000000000L,
            0xa000000000L, 0x4000000000L, 0x20000000000L, 0x50000000000L, 0xa0000000000L,
            0x140000000000L, 0x280000000000L, 0x500000000000L, 0xa00000000000L, 0x400000000000L,
            0x0L, 0x0L, 0x0L, 0x0L, 0x0L, 0x0L, 0x0L, 0x0L};

    // Pawn move generation
    /*static final long[] WHITE_PAWN_POSSIBLE_CAPTURES = new long[64], BLACK_PAWN_POSSIBLE_CAPTURES = new long[64];

    static {
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
    }*/

    static final long[] KNIGHT_POSSIBLE_MOVES = {0x20400L, 0x50800L, 0xa1100L, 0x142200L,
            0x284400L, 0x508800L, 0xa01000L, 0x402000L, 0x2040004L, 0x5080008L, 0xa110011L,
            0x14220022L, 0x28440044L, 0x50880088L, 0xa0100010L, 0x40200020L, 0x204000402L,
            0x508000805L, 0xa1100110aL, 0x1422002214L, 0x2844004428L, 0x5088008850L,
            0xa0100010a0L, 0x4020002040L, 0x20400040200L, 0x50800080500L, 0xa1100110a00L,
            0x142200221400L, 0x284400442800L, 0x508800885000L, 0xa0100010a000L, 0x402000204000L,
            0x2040004020000L, 0x5080008050000L, 0xa1100110a0000L, 0x14220022140000L,
            0x28440044280000L, 0x50880088500000L, 0xa0100010a00000L, 0x40200020400000L,
            0x204000402000000L, 0x508000805000000L, 0xa1100110a000000L, 0x1422002214000000L,
            0x2844004428000000L, 0x5088008850000000L, 0xa0100010a0000000L, 0x4020002040000000L,
            0x400040200000000L, 0x800080500000000L, 0x1100110a00000000L, 0x2200221400000000L,
            0x4400442800000000L, 0x8800885000000000L, 0x100010a000000000L, 0x2000204000000000L,
            0x4020000000000L, 0x8050000000000L, 0x110a0000000000L, 0x22140000000000L,
            0x44280000000000L, 0x88500000000000L, 0x10a00000000000L, 0x20400000000000L};

    // Knight move generation
    /*static final long[] KNIGHT_POSSIBLE_MOVES = new long[64];

    static {
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
    }*/

    static final long[] KING_POSSIBLE_MOVES = {0x302L, 0x705L, 0xe0aL, 0x1c14L, 0x3828L, 0x7050L
            , 0xe0a0L, 0xc040L, 0x30203L, 0x70507L, 0xe0a0eL, 0x1c141cL, 0x382838L, 0x705070L,
            0xe0a0e0L, 0xc040c0L, 0x3020300L, 0x7050700L, 0xe0a0e00L, 0x1c141c00L, 0x38283800L,
            0x70507000L, 0xe0a0e000L, 0xc040c000L, 0x302030000L, 0x705070000L, 0xe0a0e0000L,
            0x1c141c0000L, 0x3828380000L, 0x7050700000L, 0xe0a0e00000L, 0xc040c00000L,
            0x30203000000L, 0x70507000000L, 0xe0a0e000000L, 0x1c141c000000L, 0x382838000000L,
            0x705070000000L, 0xe0a0e0000000L, 0xc040c0000000L, 0x3020300000000L,
            0x7050700000000L, 0xe0a0e00000000L, 0x1c141c00000000L, 0x38283800000000L,
            0x70507000000000L, 0xe0a0e000000000L, 0xc040c000000000L, 0x302030000000000L,
            0x705070000000000L, 0xe0a0e0000000000L, 0x1c141c0000000000L, 0x3828380000000000L,
            0x7050700000000000L, 0xe0a0e00000000000L, 0xc040c00000000000L, 0x203000000000000L,
            0x507000000000000L, 0xa0e000000000000L, 0x141c000000000000L, 0x2838000000000000L,
            0x5070000000000000L, 0xa0e0000000000000L, 0x40c0000000000000L};

    // King move generation
    /*static final long[] KING_POSSIBLE_MOVES = new long[64];

    static {
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
    }*/

    static final long[] ROOK_BLOCKER_MASK = {0x101010101017eL, 0x202020202027cL,
            0x404040404047aL, 0x8080808080876L, 0x1010101010106eL, 0x2020202020205eL,
            0x4040404040403eL, 0x8080808080807eL, 0x1010101017e00L, 0x2020202027c00L,
            0x4040404047a00L, 0x8080808087600L, 0x10101010106e00L, 0x20202020205e00L,
            0x40404040403e00L, 0x80808080807e00L, 0x10101017e0100L, 0x20202027c0200L,
            0x40404047a0400L, 0x8080808760800L, 0x101010106e1000L, 0x202020205e2000L,
            0x404040403e4000L, 0x808080807e8000L, 0x101017e010100L, 0x202027c020200L,
            0x404047a040400L, 0x8080876080800L, 0x1010106e101000L, 0x2020205e202000L,
            0x4040403e404000L, 0x8080807e808000L, 0x1017e01010100L, 0x2027c02020200L,
            0x4047a04040400L, 0x8087608080800L, 0x10106e10101000L, 0x20205e20202000L,
            0x40403e40404000L, 0x80807e80808000L, 0x17e0101010100L, 0x27c0202020200L,
            0x47a0404040400L, 0x8760808080800L, 0x106e1010101000L, 0x205e2020202000L,
            0x403e4040404000L, 0x807e8080808000L, 0x7e010101010100L, 0x7c020202020200L,
            0x7a040404040400L, 0x76080808080800L, 0x6e101010101000L, 0x5e202020202000L,
            0x3e404040404000L, 0x7e808080808000L, 0x7e01010101010100L, 0x7c02020202020200L,
            0x7a04040404040400L, 0x7608080808080800L, 0x6e10101010101000L, 0x5e20202020202000L,
            0x3e40404040404000L, 0x7e80808080808000L};
    static final long[] BISHOP_BLOCKER_MASK = {0x40201008040200L, 0x402010080400L, 0x4020100a00L
            , 0x40221400L, 0x2442800L, 0x204085000L, 0x20408102000L, 0x2040810204000L,
            0x20100804020000L, 0x40201008040000L, 0x4020100a0000L, 0x4022140000L, 0x244280000L,
            0x20408500000L, 0x2040810200000L, 0x4081020400000L, 0x10080402000200L,
            0x20100804000400L, 0x4020100a000a00L, 0x402214001400L, 0x24428002800L,
            0x2040850005000L, 0x4081020002000L, 0x8102040004000L, 0x8040200020400L,
            0x10080400040800L, 0x20100a000a1000L, 0x40221400142200L, 0x2442800284400L,
            0x4085000500800L, 0x8102000201000L, 0x10204000402000L, 0x4020002040800L,
            0x8040004081000L, 0x100a000a102000L, 0x22140014224000L, 0x44280028440200L,
            0x8500050080400L, 0x10200020100800L, 0x20400040201000L, 0x2000204081000L,
            0x4000408102000L, 0xa000a10204000L, 0x14001422400000L, 0x28002844020000L,
            0x50005008040200L, 0x20002010080400L, 0x40004020100800L, 0x20408102000L,
            0x40810204000L, 0xa1020400000L, 0x142240000000L, 0x284402000000L, 0x500804020000L,
            0x201008040200L, 0x402010080400L, 0x2040810204000L, 0x4081020400000L,
            0xa102040000000L, 0x14224000000000L, 0x28440200000000L, 0x50080402000000L,
            0x20100804020000L, 0x40201008040200L};

    // Rook and bishop blocker mask generation
    /*static final long[] ROOK_BLOCKER_MASK = new long[64], BISHOP_BLOCKER_MASK = new long[64];

    static {
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

        // Display the bitboard
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

        // System.out.println("{");
        // for (long i : ROOK_BLOCKER_MASK) {
        //     System.out.println("0x" + Long.toHexString(i) + "L,");
        // }
        // System.out.println("};");

        // System.out.println("{");
        // for (long i : BISHOP_BLOCKER_MASK) {
        //     System.out.println("0x" + Long.toHexString(i) + "L,");
        // }
        // System.out.println("};");
    }*/

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

    // Rook and bishop move generation
    /*static final long[] ROOK_POSSIBLE_MOVES = new long[64], BISHOP_POSSIBLE_MOVES = new long[64];

    static {
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

        // Display the bitboard
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

        // System.out.println("{");
        // for (long i : ROOK_POSSIBLE_MOVES) {
        //     System.out.println("0x" + Long.toHexString(i) + "L,");
        // }
        // System.out.println("};");

        // System.out.println("{");
        // for (long i : BISHOP_BLOCKER_MASK) {
        //     System.out.println("0x" + Long.toHexString(i) + "L,");
        // }
        // System.out.println("};");
    }*/

    /**
     * Precomputed magic bitboards for rooks and bishops
     * <br>Inspired by
     * <a href="https://github.com/bartekspitza/sophia/blob/e5fdb283a96c6a4879e7eaf79a098c32bcdbcb0e/src/magics.c">Bartek Spitza</a>
     */
    static final long[] ROOK_MAGICS = {0xa8002c000108020L, 0x6c00049b0002001L,
            0x100200010090040L, 0x2480041000800801L, 0x280028004000800L, 0x900410008040022L,
            0x280020001001080L, 0x2880002041000080L, 0xa000800080400034L, 0x4808020004000L,
            0x2290802004801000L, 0x411000d00100020L, 0x402800800040080L, 0xb000401004208L,
            0x2409000100040200L, 0x1002100004082L, 0x22878001e24000L, 0x1090810021004010L,
            0x801030040200012L, 0x500808008001000L, 0xa08018014000880L, 0x8000808004000200L,
            0x201008080010200L, 0x801020000441091L, 0x800080204005L, 0x1040200040100048L,
            0x120200402082L, 0xd14880480100080L, 0x12040280080080L, 0x100040080020080L,
            0x9020010080800200L, 0x813241200148449L, 0x491604001800080L, 0x100401000402001L,
            0x4820010021001040L, 0x400402202000812L, 0x209009005000802L, 0x810800601800400L,
            0x4301083214000150L, 0x204026458e001401L, 0x40204000808000L, 0x8001008040010020L,
            0x8410820820420010L, 0x1003001000090020L, 0x804040008008080L, 0x12000810020004L,
            0x1000100200040208L, 0x430000a044020001L, 0x280009023410300L, 0xe0100040002240L,
            0x200100401700L, 0x2244100408008080L, 0x8000400801980L, 0x2000810040200L,
            0x8010100228810400L, 0x2000009044210200L, 0x4080008040102101L, 0x40002080411d01L,
            0x2005524060000901L, 0x502001008400422L, 0x489a000810200402L, 0x1004400080a13L,
            0x4000011008020084L, 0x26002114058042L};
    static final long[] BISHOP_MAGICS = {0x89a1121896040240L, 0x2004844802002010L,
            0x2068080051921000L, 0x62880a0220200808L, 0x4042004000000L, 0x100822020200011L,
            0xc00444222012000aL, 0x28808801216001L, 0x400492088408100L,
            0x201c401040c0084L, 0x840800910a0010L, 0x82080240060L, 0x2000840504006000L,
            0x30010c4108405004L, 0x1008005410080802L, 0x8144042209100900L,
            0x208081020014400L, 0x4800201208ca00L, 0xf18140408012008L,
            0x1004002802102001L, 0x841000820080811L, 0x40200200a42008L, 0x800054042000L,
            0x88010400410c9000L, 0x520040470104290L, 0x1004040051500081L,
            0x2002081833080021L, 0x400c00c010142L, 0x941408200c002000L,
            0x658810000806011L, 0x188071040440a00L, 0x4800404002011c00L,
            0x104442040404200L, 0x511080202091021L, 0x4022401120400L,
            0x80c0040400080120L, 0x8040010040820802L, 0x480810700020090L,
            0x102008e00040242L, 0x809005202050100L, 0x8002024220104080L,
            0x431008804142000L, 0x19001802081400L, 0x200014208040080L,
            0x3308082008200100L, 0x41010500040c020L, 0x4012020c04210308L,
            0x208220a202004080L, 0x111040120082000L, 0x6803040141280a00L,
            0x2101004202410000L, 0x8200000041108022L, 0x21082088000L, 0x2410204010040L,
            0x40100400809000L, 0x822088220820214L, 0x40808090012004L, 0x910224040218c9L,
            0x402814422015008L, 0x90014004842410L, 0x1000042304105L, 0x10008830412a00L,
            0x2520081090008908L, 0x40102000a0a60140L};

    static final int[] ROOK_RELEVANT_BITS = {
            12, 11, 11, 11, 11, 11, 11, 12,
            11, 10, 10, 10, 10, 10, 10, 11,
            11, 10, 10, 10, 10, 10, 10, 11,
            11, 10, 10, 10, 10, 10, 10, 11,
            11, 10, 10, 10, 10, 10, 10, 11,
            11, 10, 10, 10, 10, 10, 10, 11,
            11, 10, 10, 10, 10, 10, 10, 11,
            12, 11, 11, 11, 11, 11, 11, 12};

    static final int[] BISHOP_RELEVANT_BITS = {
            6, 5, 5, 5, 5, 5, 5, 6,
            5, 5, 5, 5, 5, 5, 5, 5,
            5, 5, 7, 7, 7, 7, 5, 5,
            5, 5, 7, 9, 9, 7, 5, 5,
            5, 5, 7, 9, 9, 7, 5, 5,
            5, 5, 7, 7, 7, 7, 5, 5,
            5, 5, 5, 5, 5, 5, 5, 5,
            6, 5, 5, 5, 5, 5, 5, 6};

    static final long[][] ROOK_ATTACKS = new long[64][];
    static final long[][] BISHOP_ATTACKS = new long[64][];

    // TODO: Check for correctness
    static {
        for (int i = 0; i < 64; i++) {
            int positions = 1 << ROOK_RELEVANT_BITS[i];
            ROOK_ATTACKS[i] = new long[positions];

            for (int position = 0; position < positions; position++) {
                long blockers = occupancyVariation(position, ROOK_RELEVANT_BITS[i],
                        ROOK_BLOCKER_MASK[i]);
                int index = (int) ((blockers * ROOK_MAGICS[i]) >>> (64 - ROOK_RELEVANT_BITS[i]));
                ROOK_ATTACKS[i][index] = slidingPieceMoveHelper(i, blockers, true);
            }
        }

        for (int i = 0; i < 64; i++) {
            int positions = 1 << BISHOP_RELEVANT_BITS[i];
            BISHOP_ATTACKS[i] = new long[positions];

            for (int position = 0; position < positions; position++) {
                long blockers = occupancyVariation(position, BISHOP_RELEVANT_BITS[i],
                        BISHOP_BLOCKER_MASK[i]);
                int index = (int) ((blockers * BISHOP_MAGICS[i]) >>> (64 - BISHOP_RELEVANT_BITS[i]));
                BISHOP_ATTACKS[i][index] = slidingPieceMoveHelper(i, blockers, false);
            }
        }
    }

    private static long occupancyVariation(int index, int bits, long mask) {
        long result = 0L;
        for (int i = 0; i < bits; i++) {
            int occupancyIndex = Long.numberOfTrailingZeros(mask);
            mask ^= SQUARE_TO_BITBOARD[occupancyIndex];

            if ((1 << i & index) != 0) {
                result |= 1L << occupancyIndex;
            }
        }

        return result;
    }

    private static long slidingPieceMoveHelper(int index, long blockers, boolean rook) {
        long attack = 0L;

        if (rook) {
            for (int up = index + 8; up <= H8; up += 8) {
                attack |= SQUARE_TO_BITBOARD[up];
                if ((blockers & SQUARE_TO_BITBOARD[up]) != 0) {
                    break;
                }
            }
            for (int down = index - 8; down >= A1; down -= 8) {
                attack |= SQUARE_TO_BITBOARD[down];
                if ((blockers & SQUARE_TO_BITBOARD[down]) != 0) {
                    break;
                }
            }
            for (int left = index - 1; left >= A1 && left % 8 != 7; left--) {
                attack |= SQUARE_TO_BITBOARD[left];
                if ((blockers & SQUARE_TO_BITBOARD[left]) != 0) {
                    break;
                }
            }
            for (int right = index + 1; right <= H8 && right % 8 != 0; right++) {
                attack |= SQUARE_TO_BITBOARD[right];
                if ((blockers & SQUARE_TO_BITBOARD[right]) != 0) {
                    break;
                }
            }
        } else {
            for (int upLeft = index + 7; upLeft <= H8 && upLeft % 8 != 7; upLeft += 7) {
                attack |= SQUARE_TO_BITBOARD[upLeft];
                if ((blockers & SQUARE_TO_BITBOARD[upLeft]) != 0) {
                    break;
                }
            }
            for (int upRight = index + 9; upRight <= H8 && upRight % 8 != 0; upRight += 9) {
                attack |= SQUARE_TO_BITBOARD[upRight];
                if ((blockers & SQUARE_TO_BITBOARD[upRight]) != 0) {
                    break;
                }
            }
            for (int downLeft = index - 9; downLeft >= A1 && downLeft % 8 != 7; downLeft -= 9) {
                attack |= SQUARE_TO_BITBOARD[downLeft];
                if ((blockers & SQUARE_TO_BITBOARD[downLeft]) != 0) {
                    break;
                }
            }
            for (int downRight = index - 7; downRight >= A1 && downRight % 8 != 0; downRight -= 7) {
                attack |= SQUARE_TO_BITBOARD[downRight];
                if ((blockers & SQUARE_TO_BITBOARD[downRight]) != 0) {
                    break;
                }
            }
        }
        return attack;
    }

    long whitePawns, whiteKnights, whiteBishops, whiteRooks, whiteQueens, whiteKing;
    long blackPawns, blackKnights, blackBishops, blackRooks, blackQueens, blackKing;
    long whitePieces, blackPieces, allPieces;
    boolean whiteToMove;
    int enPassantIndex;
    int halfMoveClock;
    int moveCounter;
    int castleRights;

    enum GameStatus {
        // Unsure if these are all the game statuses
        NORMAL, CHECK, CHECKMATE, STALEMATE, FIFTY_MOVE_RULE, THREEFOLD_REPETITION,
        INSUFFICIENT_MATERIAL
    }

    GameStatus gameStatus;

    /**
     * Initial bitboard and should only be called once each time the best move is requested
     *
     * @param FEN string to parse
     */
    BitBoards(String FEN) {
        String[] FENParts = FEN.split(" ");
        String positions = FENParts[0], whiteToMove = FENParts[1], castleRights = FENParts[2],
                enPassant = FENParts[3], halfMoveClock = FENParts[4], moveCounter = FENParts[5];
        String[] positionParts = positions.split("/");
        int position = 56;
        for (String row : positionParts) {
            for (int i = 0; i < row.length(); i++) {
                switch (row.charAt(i)) {
                    case 'P':
                        whitePawns |= 1L << position;
                        whitePieces |= 1L << position;
                        break;
                    case 'N':
                        whiteKnights |= 1L << position;
                        whitePieces |= 1L << position;
                        break;
                    case 'B':
                        whiteBishops |= 1L << position;
                        whitePieces |= 1L << position;
                        break;
                    case 'R':
                        whiteRooks |= 1L << position;
                        whitePieces |= 1L << position;
                        break;
                    case 'Q':
                        whiteQueens |= 1L << position;
                        whitePieces |= 1L << position;
                        break;
                    case 'K':
                        whiteKing |= 1L << position;
                        whitePieces |= 1L << position;
                        break;
                    case 'p':
                        blackPawns |= 1L << position;
                        blackPieces |= 1L << position;
                        break;
                    case 'n':
                        blackKnights |= 1L << position;
                        blackPieces |= 1L << position;
                        break;
                    case 'b':
                        blackBishops |= 1L << position;
                        blackPieces |= 1L << position;
                        break;
                    case 'r':
                        blackRooks |= 1L << position;
                        blackPieces |= 1L << position;
                        break;
                    case 'q':
                        blackQueens |= 1L << position;
                        blackPieces |= 1L << position;
                        break;
                    case 'k':
                        blackKing |= 1L << position;
                        blackPieces |= 1L << position;
                        break;
                    default:
                        position += row.charAt(i) - '0';
                        continue;
                }
                ++position;
            }
            position -= 16;
        }
        this.allPieces = whitePieces | blackPieces;
        this.whiteToMove = whiteToMove.equals("w");
        for (char c : castleRights.toCharArray()) {
            switch (c) {
                case 'K':
                    this.castleRights |= 1;
                    break;
                case 'Q':
                    this.castleRights |= 2;
                    break;
                case 'k':
                    this.castleRights |= 4;
                    break;
                case 'q':
                    this.castleRights |= 8;
                    break;
            }
        }
        this.enPassantIndex = enPassant.equals("-") ? -1 : Move.notationToIndex(enPassant);
        this.halfMoveClock = Integer.parseInt(halfMoveClock);
        this.moveCounter = Integer.parseInt(moveCounter);
    }

    private BitBoards(BitBoards state) {
        this.whitePawns = state.whitePawns;
        this.whiteKnights = state.whiteKnights;
        this.whiteBishops = state.whiteBishops;
        this.whiteRooks = state.whiteRooks;
        this.whiteQueens = state.whiteQueens;
        this.whiteKing = state.whiteKing;
        this.blackPawns = state.blackPawns;
        this.blackKnights = state.blackKnights;
        this.blackBishops = state.blackBishops;
        this.blackRooks = state.blackRooks;
        this.blackQueens = state.blackQueens;
        this.blackKing = state.blackKing;
        this.whitePieces = state.whitePieces;
        this.blackPieces = state.blackPieces;
        this.allPieces = state.allPieces;
        this.whiteToMove = state.whiteToMove;
        this.enPassantIndex = state.enPassantIndex;
        this.halfMoveClock = state.halfMoveClock;
        this.moveCounter = state.moveCounter;
        this.castleRights = state.castleRights;
        this.gameStatus = state.gameStatus;
    }

    /**
     * Called when validating a move
     *
     * @param move move to make
     * @return new state
     */
    BitBoards tryMove(Move move) {
        // System.out.println("Trying move: " + move);
        BitBoards newState = switch (move.moveType()) {
            case NORMAL -> tryMoveNormal(move);
            case EN_PASSANT -> tryMoveEnPassant(move);
            case CASTLE_LEFT -> tryMoveCastleLeft(move);
            case CASTLE_RIGHT -> tryMoveCastleRight(move);
            case PAWN_DOUBLE_MOVE -> tryMovePawnDouble(move);
            case PROMOTE_ROOK, PROMOTE_KNIGHT, PROMOTE_BISHOP, PROMOTE_QUEEN ->
                    tryMovePromotion(move);
            default ->
                    throw new IllegalStateException("Unexpected value in make move: " + move.moveType());
        };
        newState.whiteToMove = !this.whiteToMove;
        ++newState.moveCounter;
        return newState;
    }

    /**
     * Called when making a move and updating the game status
     * TODO: Fix bug where white and black pieces overlap
     *
     * @param move move to make
     * @return new state
     */
    BitBoards makeMove(Move move) {
        BitBoards newState = tryMove(move);
        if ((newState.whitePieces & newState.blackPieces) != 0) {
            throw new IllegalStateException(String.format("""
                    %s caused white and black pieces to overlap from
                    %s
                    to
                    %s""", move, this, newState));
        }

        updateGameStatus(newState);
        return newState;
    }

    private void updateGameStatus(BitBoards newState) {
        boolean inCheck = safeSquare(newState.whiteToMove, newState.whiteToMove ? newState.whiteKing :
                newState.blackKing);
        boolean hasLegalMove = MoveGeneration.hasLegalMoves(newState);
        if (inCheck) {
            newState.gameStatus = hasLegalMove ? GameStatus.CHECK : GameStatus.CHECKMATE;
        } else {
            newState.gameStatus = hasLegalMove ? GameStatus.NORMAL : GameStatus.STALEMATE;
        }

        if (newState.halfMoveClock >= 100) {
            newState.gameStatus = GameStatus.FIFTY_MOVE_RULE;
        }
        if (Long.bitCount(newState.whitePawns | newState.blackPawns | newState.whiteRooks | newState.blackRooks | newState.whiteQueens | newState.blackQueens) == 0) {
            int knightCount = Long.bitCount(newState.whiteKnights | newState.blackKnights);
            int bishopCount = Long.bitCount(newState.whiteBishops | newState.blackBishops);
            if (knightCount <= 2 || bishopCount <= 1) {
                newState.gameStatus = GameStatus.INSUFFICIENT_MATERIAL;
            }
        }
    }

    boolean gameOver() {
        return switch (gameStatus) {
            case CHECKMATE, STALEMATE, FIFTY_MOVE_RULE, THREEFOLD_REPETITION, INSUFFICIENT_MATERIAL ->
                    true;
            case NORMAL, CHECK -> false;
        };
    }

    /**
     * @param move move to make
     * @return new state
     */
    private BitBoards tryMoveNormal(Move move) {
        BitBoards newState = new BitBoards(this);
        long startBitboard = SQUARE_TO_BITBOARD[move.start()], endBitboard =
                SQUARE_TO_BITBOARD[move.end()];
        long moveBitboard = startBitboard | endBitboard;
        if (newState.whiteToMove) {
            switch (move.pieceType()) {
                case PAWN -> newState.whitePawns ^= moveBitboard;
                case ROOK -> {
                    newState.castleRights &= move.start() == H1 ? 0b1110 : 0b1101;
                    newState.whiteRooks ^= moveBitboard;
                }
                case KNIGHT -> newState.whiteKnights ^= moveBitboard;
                case BISHOP -> newState.whiteBishops ^= moveBitboard;
                case QUEEN -> newState.whiteQueens ^= moveBitboard;
                case KING -> {
                    newState.castleRights &= 0b1100;
                    newState.whiteKing ^= moveBitboard;
                }
                default -> throw new IllegalStateException("Unexpected value in make move " +
                        "normal: " + move.pieceType());
            }
            newState.whitePieces ^= moveBitboard;
            if ((newState.blackPieces & endBitboard) != 0) {
                newState.halfMoveClock = 0;
                newState.blackPawns &= ~endBitboard;
                if ((newState.blackRooks & endBitboard) != 0) {
                    newState.castleRights &= move.end() == H8 ? 0b0111 : 0b1011;
                    newState.blackRooks &= ~endBitboard;
                }
                newState.blackKnights &= ~endBitboard;
                newState.blackBishops &= ~endBitboard;
                newState.blackQueens &= ~endBitboard;
                newState.blackKing &= ~endBitboard;
                newState.blackPieces &= ~endBitboard;
            }
        } else {
            switch (move.pieceType()) {
                case PAWN -> newState.blackPawns ^= moveBitboard;
                case ROOK -> {
                    newState.castleRights &= move.start() == H8 ? 0b1011 : 0b0111;
                    newState.blackRooks ^= moveBitboard;
                }
                case KNIGHT -> newState.blackKnights ^= moveBitboard;
                case BISHOP -> newState.blackBishops ^= moveBitboard;
                case QUEEN -> newState.blackQueens ^= moveBitboard;
                case KING -> {
                    newState.castleRights &= 0b0011;
                    newState.blackKing ^= moveBitboard;
                }
                default -> throw new IllegalStateException("Unexpected value in make move " +
                        "normal: " + move.pieceType());
            }
            newState.blackPieces ^= moveBitboard;
            if ((newState.whitePieces & endBitboard) != 0) {
                newState.halfMoveClock = 0;
                newState.whitePawns &= ~endBitboard;
                if ((newState.whiteRooks & endBitboard) != 0) {
                    newState.castleRights &= move.end() == H1 ? 0b1101 : 0b1110;
                    newState.whiteRooks &= ~endBitboard;
                }
                newState.whiteKnights &= ~endBitboard;
                newState.whiteBishops &= ~endBitboard;
                newState.whiteQueens &= ~endBitboard;
                newState.whiteKing &= ~endBitboard;
                newState.whitePieces &= ~endBitboard;
            }
        }
        newState.allPieces &= ~startBitboard;
        newState.allPieces |= endBitboard;
        newState.enPassantIndex = -1;
        ++newState.halfMoveClock;

        return newState;
    }

    /**
     * Pawn capture using en passant
     *
     * @param move move to make
     * @return new state
     */
    private BitBoards tryMoveEnPassant(Move move) {
        BitBoards newState = new BitBoards(this);
        int start = move.start();
        long startBitboard = SQUARE_TO_BITBOARD[start];
        int end = move.end();
        long endBitboard = SQUARE_TO_BITBOARD[end];
        long moveBitboard = startBitboard | endBitboard;
        if (newState.whiteToMove) {
            newState.whitePawns ^= moveBitboard;
            newState.whitePieces ^= moveBitboard;
            newState.allPieces ^= moveBitboard;
            newState.blackPawns ^= SQUARE_TO_BITBOARD[end - 8];
            newState.blackPieces ^= SQUARE_TO_BITBOARD[end - 8];
            newState.allPieces ^= SQUARE_TO_BITBOARD[end - 8];
        } else {
            newState.blackPawns ^= moveBitboard;
            newState.blackPieces ^= moveBitboard;
            newState.allPieces ^= moveBitboard;
            newState.whitePawns ^= SQUARE_TO_BITBOARD[end + 8];
            newState.whitePieces ^= SQUARE_TO_BITBOARD[end + 8];
            newState.allPieces ^= SQUARE_TO_BITBOARD[end + 8];
        }
        newState.enPassantIndex = -1;
        newState.halfMoveClock = 0;

        return newState;
    }

    /**
     * Castles left
     *
     * @param move move to make
     * @return new state
     */
    private BitBoards tryMoveCastleLeft(Move move) {
        BitBoards newState = new BitBoards(this);
        long kingBitboard, rookBitboard;
        if (newState.whiteToMove) {
            kingBitboard =
                    SQUARE_TO_BITBOARD[WHITE_KING_START] | SQUARE_TO_BITBOARD[WHITE_KING_START - 2];
            rookBitboard = SQUARE_TO_BITBOARD[A1] | SQUARE_TO_BITBOARD[WHITE_KING_START - 1];
            newState.whiteKing ^= kingBitboard;
            newState.whiteRooks ^= rookBitboard;
            newState.whitePieces ^= kingBitboard | rookBitboard;
            newState.allPieces ^= kingBitboard | rookBitboard;
            newState.castleRights &= 0b1100;
        } else {
            kingBitboard =
                    SQUARE_TO_BITBOARD[BLACK_KING_START] | SQUARE_TO_BITBOARD[BLACK_KING_START - 2];
            rookBitboard = SQUARE_TO_BITBOARD[A8] | SQUARE_TO_BITBOARD[BLACK_KING_START - 1];
            newState.blackKing ^= kingBitboard;
            newState.blackRooks ^= rookBitboard;
            newState.blackPieces ^= kingBitboard | rookBitboard;
            newState.allPieces ^= kingBitboard | rookBitboard;
            newState.castleRights &= 0b0011;
        }
        newState.enPassantIndex = -1;

        return newState;
    }

    /**
     * Castles right
     *
     * @param move move to make
     * @return new state
     */
    private BitBoards tryMoveCastleRight(Move move) {
        BitBoards newState = new BitBoards(this);
        long kingBitboard, rookBitboard;
        if (newState.whiteToMove) {
            kingBitboard =
                    SQUARE_TO_BITBOARD[WHITE_KING_START] | SQUARE_TO_BITBOARD[WHITE_KING_START + 2];
            rookBitboard = SQUARE_TO_BITBOARD[H1] | SQUARE_TO_BITBOARD[WHITE_KING_START + 1];
            newState.whiteKing ^= kingBitboard;
            newState.whiteRooks ^= rookBitboard;
            newState.whitePieces ^= kingBitboard | rookBitboard;
            newState.allPieces ^= kingBitboard | rookBitboard;
            newState.castleRights &= 0b1100;
        } else {
            kingBitboard =
                    SQUARE_TO_BITBOARD[BLACK_KING_START] | SQUARE_TO_BITBOARD[BLACK_KING_START + 2];
            rookBitboard = SQUARE_TO_BITBOARD[H8] | SQUARE_TO_BITBOARD[BLACK_KING_START + 1];
            newState.blackKing ^= kingBitboard;
            newState.blackRooks ^= rookBitboard;
            newState.blackPieces ^= kingBitboard | rookBitboard;
            newState.allPieces ^= kingBitboard | rookBitboard;
            newState.castleRights &= 0b0011;
        }
        newState.enPassantIndex = -1;

        return newState;
    }

    /**
     * Pawn double move from the pawn's starting position
     *
     * @param move move to make
     * @return new state
     */
    private BitBoards tryMovePawnDouble(Move move) {
        BitBoards newState = new BitBoards(this);
        long startBitboard = SQUARE_TO_BITBOARD[move.start()], endBitboard =
                SQUARE_TO_BITBOARD[move.end()];
        long moveBitboard = startBitboard | endBitboard;
        if (newState.whiteToMove) {
            newState.whitePawns ^= moveBitboard;
            newState.whitePieces ^= moveBitboard;
            newState.enPassantIndex = move.end() - 8;
        } else {
            newState.blackPawns ^= moveBitboard;
            newState.blackPieces ^= moveBitboard;
            newState.enPassantIndex = move.end() + 8;
        }
        newState.allPieces ^= moveBitboard;
        newState.halfMoveClock = 0;
        if ((SQUARE_TO_BITBOARD[newState.enPassantIndex] & RANK_3 | SQUARE_TO_BITBOARD[newState.enPassantIndex] & RANK_6) == 0) {
            throw new IllegalStateException("Unexpected en passant index: " + newState.enPassantIndex);
        }

        return newState;
    }

    private BitBoards tryMovePromotion(Move move) {
        BitBoards newState = new BitBoards(this);
        long startBitboard = SQUARE_TO_BITBOARD[move.start()], endBitboard =
                SQUARE_TO_BITBOARD[move.end()];
        if (newState.whiteToMove) {
            newState.whitePawns ^= startBitboard;
            newState.whitePieces ^= startBitboard;
            switch (move.moveType()) {
                case PROMOTE_ROOK -> newState.whiteRooks ^= endBitboard;
                case PROMOTE_KNIGHT -> newState.whiteKnights ^= endBitboard;
                case PROMOTE_BISHOP -> newState.whiteBishops ^= endBitboard;
                case PROMOTE_QUEEN -> newState.whiteQueens ^= endBitboard;
                default ->
                        throw new IllegalStateException("Unexpected value in promotion: " + move.moveType());
            }
            newState.whitePieces ^= endBitboard;
            if ((newState.blackPieces & endBitboard) != 0) {
                if ((newState.blackPawns & endBitboard) != 0) {
                    throw new IllegalStateException("Unexpected black pawn at promotion square");
                }

                if (move.end() == H8) {
                    newState.castleRights &= 0b0111;
                } else if (move.end() == A8) {
                    newState.castleRights &= 0b1011;
                }
                newState.blackRooks ^= endBitboard;
                newState.blackKnights ^= endBitboard;
                newState.blackBishops ^= endBitboard;
                newState.blackQueens ^= endBitboard;
                newState.blackPieces ^= endBitboard;
            }
        } else {
            newState.blackPawns ^= startBitboard;
            newState.blackPieces ^= startBitboard;
            switch (move.moveType()) {
                case PROMOTE_ROOK -> newState.blackRooks ^= endBitboard;
                case PROMOTE_KNIGHT -> newState.blackKnights ^= endBitboard;
                case PROMOTE_BISHOP -> newState.blackBishops ^= endBitboard;
                case PROMOTE_QUEEN -> newState.blackQueens ^= endBitboard;
                default ->
                        throw new IllegalStateException("Unexpected value in promotion: " + move.moveType());
            }
            newState.blackPieces ^= endBitboard;
            if ((newState.whitePieces & endBitboard) != 0) {
                if ((newState.whitePawns & endBitboard) != 0) {
                    throw new IllegalStateException("Unexpected white pawn at promotion square");
                }

                if (move.end() == H1) {
                    newState.castleRights &= 0b1101;
                } else if (move.end() == A1) {
                    newState.castleRights &= 0b1110;
                }
                newState.whiteRooks ^= endBitboard;
                newState.whiteKnights ^= endBitboard;
                newState.whiteBishops ^= endBitboard;
                newState.whiteQueens ^= endBitboard;
                newState.whitePieces ^= endBitboard;
            }
        }
        newState.allPieces ^= startBitboard | endBitboard;
        newState.enPassantIndex = -1;

        return newState;
    }

    /**
     * Checks if the current color of the board is being attacked on the specified square
     *
     * @param color  color of the player to check
     * @param square bitboard of the square to check
     * @return if the index is safe
     */
    boolean safeSquare(boolean color, long square) {
        long enemyPawns = color ? blackPawns : whitePawns;
        long enemyRooks = color ? blackRooks : whiteRooks;
        long enemyKnights = color ? blackKnights : whiteKnights;
        long enemyBishops = color ? blackBishops : whiteBishops;
        long enemyQueens = color ? blackQueens : whiteQueens;
        long enemyKing = color ? blackKing : whiteKing;

        // Pawn attacks
        while (enemyPawns != 0) {
            int enemyPawnIndex = Long.numberOfTrailingZeros(enemyPawns);
            if (color) {
                if ((BLACK_PAWN_POSSIBLE_CAPTURES[enemyPawnIndex] & square) != 0) {
                    return false;
                }
            } else {
                if ((WHITE_PAWN_POSSIBLE_CAPTURES[enemyPawnIndex] & square) != 0) {
                    return false;
                }
            }
            enemyPawns ^= SQUARE_TO_BITBOARD[enemyPawnIndex];
        }

        // Rook attacks
        while (enemyRooks != 0) {
            int enemyRookIndex = Long.numberOfTrailingZeros(enemyRooks);
            if ((MoveGeneration.getRookAttacks(enemyRookIndex, allPieces) & square) != 0) {
                return false;
            }
            enemyRooks ^= SQUARE_TO_BITBOARD[enemyRookIndex];
        }

        // Knight attacks
        while (enemyKnights != 0) {
            int enemyKnightIndex = Long.numberOfTrailingZeros(enemyKnights);
            if ((KNIGHT_POSSIBLE_MOVES[enemyKnightIndex] & square) != 0) {
                return false;
            }
            enemyKnights ^= SQUARE_TO_BITBOARD[enemyKnightIndex];
        }

        // Bishop attacks
        while (enemyBishops != 0) {
            int enemyBishopIndex = Long.numberOfTrailingZeros(enemyBishops);
            if ((MoveGeneration.getBishopAttacks(enemyBishopIndex, allPieces) & square) != 0) {
                return false;
            }
            enemyBishops ^= SQUARE_TO_BITBOARD[enemyBishopIndex];
        }

        // Queen attacks
        while (enemyQueens != 0) {
            int enemyQueenIndex = Long.numberOfTrailingZeros(enemyQueens);
            if (((MoveGeneration.getRookAttacks(enemyQueenIndex, allPieces) | MoveGeneration.getBishopAttacks(enemyQueenIndex, allPieces)) & square) != 0) {
                return false;
            }
            enemyQueens ^= SQUARE_TO_BITBOARD[enemyQueenIndex];
        }

        // King attacks
        while (enemyKing != 0) {
            int enemyKingIndex = Long.numberOfTrailingZeros(enemyKing);
            if ((KING_POSSIBLE_MOVES[enemyKingIndex] & square) != 0) {
                return false;
            }
            enemyKing ^= SQUARE_TO_BITBOARD[enemyKingIndex];
        }
        return true;
    }

    /**
     * @return current game status
     */
    GameStatus getGameStatus() {
        return gameStatus;
    }

    /**
     * @return value of the board
     */
    int evaluateBoard() {
        return switch (gameStatus) {
            // Fine since -Integer.MIN_VALUE == Integer.MIN_VALUE
            case CHECKMATE -> whiteToMove ? Integer.MIN_VALUE : Integer.MAX_VALUE;
            case STALEMATE, FIFTY_MOVE_RULE, THREEFOLD_REPETITION, INSUFFICIENT_MATERIAL -> 0;
            case NORMAL, CHECK -> evaluateBoardNormal();
        };
    }

    /**
     * TODO: Optimize this
     * Precondition: The game is not over
     *
     * @return value of the board
     */
    private int evaluateBoardNormal() {
        int score = 0;
        for (int i = 0; i < 64; i++) {
            long positionBitboard = SQUARE_TO_BITBOARD[i];
            if ((whitePawns & positionBitboard) != 0) {
                score += PAWN_VAL + WHITE_PAWN_POS_VALUES[i];
            } else if ((whiteRooks & positionBitboard) != 0) {
                score += ROOK_VAL + WHITE_ROOK_POS_VALUES[i];
            } else if ((whiteKnights & positionBitboard) != 0) {
                score += KNIGHT_VAL + WHITE_KNIGHT_POS_VALUES[i];
            } else if ((whiteBishops & positionBitboard) != 0) {
                score += BISHOP_VAL + WHITE_BISHOP_POS_VALUES[i];
            } else if ((whiteQueens & positionBitboard) != 0) {
                score += QUEEN_VAL + WHITE_QUEEN_POS_VALUES[i];
            } else if ((whiteKing & positionBitboard) != 0) {
                score += KING_VAL + WHITE_KING_POS_VALUES[i];
            } else if ((blackPawns & positionBitboard) != 0) {
                score -= PAWN_VAL + BLACK_PAWN_POS_VALUES[i];
            } else if ((blackRooks & positionBitboard) != 0) {
                score -= ROOK_VAL + BLACK_ROOK_POS_VALUES[i];
            } else if ((blackKnights & positionBitboard) != 0) {
                score -= KNIGHT_VAL + BLACK_KNIGHT_POS_VALUES[i];
            } else if ((blackBishops & positionBitboard) != 0) {
                score -= BISHOP_VAL + BLACK_BISHOP_POS_VALUES[i];
            } else if ((blackQueens & positionBitboard) != 0) {
                score -= QUEEN_VAL + BLACK_QUEEN_POS_VALUES[i];
            } else if ((blackKing & positionBitboard) != 0) {
                score -= KING_VAL + BLACK_KING_POS_VALUES[i];
            }
        }

        return whiteToMove ? score : -score;
    }

    static String displayLongAsBitboard(long bitBoard) {
        StringBuilder sb = new StringBuilder();
        for (int i = 7; i >= 0; --i) {
            for (int j = 0; j < 8; ++j) {
                long position = 1L << (8 * i + j);
                if ((bitBoard & position) != 0) {
                    sb.append('1');
                } else {
                    sb.append('0');
                }
            }
            sb.append('\n');
        }
        System.out.println(sb.toString());

        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 7; i >= 0; --i) {
            for (int j = 0; j < 8; ++j) {
                long position = 1L << (8 * i + j);
                if ((whitePawns & position) != 0) {
                    sb.append('P');
                } else if ((whiteKnights & position) != 0) {
                    sb.append('N');
                } else if ((whiteBishops & position) != 0) {
                    sb.append('B');
                } else if ((whiteRooks & position) != 0) {
                    sb.append('R');
                } else if ((whiteQueens & position) != 0) {
                    sb.append('Q');
                } else if ((whiteKing & position) != 0) {
                    sb.append('K');
                } else if ((blackPawns & position) != 0) {
                    sb.append('p');
                } else if ((blackKnights & position) != 0) {
                    sb.append('n');
                } else if ((blackBishops & position) != 0) {
                    sb.append('b');
                } else if ((blackRooks & position) != 0) {
                    sb.append('r');
                } else if ((blackQueens & position) != 0) {
                    sb.append('q');
                } else if ((blackKing & position) != 0) {
                    sb.append('k');
                } else {
                    sb.append('.');
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}
