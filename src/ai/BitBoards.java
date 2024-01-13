package ai;

class BitBoards {
    /**
     * Returns the positional value of the piece with respect to the board from white's POV
     * <br>Inspired by
     * <a href="https://github.com/bartekspitza/sophia/blob/master/src/evaluation.c">GitHub</a>
     */
    static final int[][] WHITE_PAWN_VALUES = {
            {0, 0, 0, 0, 0, 0, 0, 0},
            {50, 50, 50, 50, 50, 50, 50, 50},
            {10, 10, 20, 30, 30, 20, 10, 10},
            {5, 5, 10, 25, 25, 10, 5, 5},
            {0, 0, 0, 20, 20, 0, 0, 0},
            {5, -5, -10, 0, 0, -10, -5, 5},
            {5, 10, 10, -20, -20, 10, 10, 5},
            {0, 0, 0, 0, 0, 0, 0, 0}
    };
    static final int[][] WHITE_KNIGHT_VALUES = {
            {-50, -40, -30, -30, -30, -30, -40, -50},
            {-40, -20, 0, 0, 0, 0, -20, -40},
            {-30, 0, 10, 15, 15, 10, 0, -30},
            {-30, 5, 15, 20, 20, 15, 5, -30},
            {-30, 0, 15, 20, 20, 15, 0, -30},
            {-30, 5, 10, 15, 15, 10, 5, -30},
            {-40, -20, 0, 5, 5, 0, -20, -40},
            {-50, -40, -30, -30, -30, -30, -40, -50}
    };
    static final int[][] WHITE_BISHOP_VALUES = {
            {-20, -10, -10, -10, -10, -10, -10, -20},
            {-10, 0, 0, 0, 0, 0, 0, -10},
            {-10, 0, 5, 10, 10, 5, 0, -10},
            {-10, 5, 5, 10, 10, 5, 5, -10},
            {-10, 0, 10, 10, 10, 10, 0, -10},
            {-10, 10, 10, 10, 10, 10, 10, -10},
            {-10, 5, 0, 0, 0, 0, 5, -10},
            {-20, -10, -10, -10, -10, -10, -10, -20}};
    static final int[][] WHITE_ROOK_VALUES = {
            {0, 0, 0, 0, 0, 0, 0, 0},
            {5, 10, 10, 10, 10, 10, 10, 5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {0, 0, 0, 5, 5, 0, 0, 0}};
    static final int[][] WHITE_QUEEN_VALUES = {
            {-20, -10, -10, -5, -5, -10, -10, -20},
            {-10, 0, 0, 0, 0, 0, 0, -10},
            {-10, 0, 5, 5, 5, 5, 0, -10},
            {-5, 0, 5, 5, 5, 5, 0, -5},
            {0, 0, 5, 5, 5, 5, 0, -5},
            {-10, 5, 5, 5, 5, 5, 0, -10},
            {-10, 0, 5, 0, 0, 0, 0, -10},
            {-20, -10, -10, -5, -5, -10, -10, -20}};
    static final int[][] WHITE_KING_VALUES = {
            {20, 30, 10, 0, 0, 10, 30, 20},
            {20, 20, 0, 0, 0, 0, 20, 20},
            {-10, -20, -20, -20, -20, -20, -20, -10},
            {-20, -30, -30, -40, -40, -30, -30, -20},
            {-30, -40, -40, -50, -50, -40, -40, -30},
            {-30, -40, -40, -50, -50, -40, -40, -30},
            {-30, -40, -40, -50, -50, -40, -40, -30},
            {-30, -40, -40, -50, -50, -40, -40, -30}};
    static final long RANK_1 = 0xFFL, RANK_2 = 0xFF00L, RANK_3 = 0xFF0000L, RANK_4 = 0xFF000000L,
            RANK_5 = 0xFF00000000L, RANK_6 = 0xFF0000000000L, RANK_7 = 0xFF000000000000L, RANK_8
            = 0xFF00000000000000L, A1 = 0, H1 = 7, A2 = 8, A8 = 56, H8 = 63, A_FILE =
            0x0101010101010101L, H_FILE = 0x8080808080808080L, WHITE_KING_START = 4, BLACK_KING_START = 60;
    static final long[] SQUARE_TO_BITBOARD = new long[64];

    static {
        long position = 1L;
        for (int i = 0; i < 64; ++i) {
            SQUARE_TO_BITBOARD[i] = position;
            position <<= 1;
        }
    }

    // TODO: Precompute all possible moves since they will never change
    static final long[] WHITE_PAWN_POSSIBLE_CAPTURES = new long[64], BLACK_PAWN_POSSIBLE_CAPTURES = new long[64];

    static {
        for (int i = (int) A2; i < A8; i++) {
            long squarePossibleCaptures = 0L;
            if (i % 8 != 0) {
                squarePossibleCaptures |= SQUARE_TO_BITBOARD[i + 7];
            }
            if (i % 8 != 7) {
                squarePossibleCaptures |= SQUARE_TO_BITBOARD[i + 9];
            }
            WHITE_PAWN_POSSIBLE_CAPTURES[i] = squarePossibleCaptures;
        }

        for (int i = (int) A2; i < A8; i++) {
            long squarePossibleCaptures = 0L;
            if (i % 8 != 0) {
                squarePossibleCaptures |= SQUARE_TO_BITBOARD[i - 9];
            }
            if (i % 8 != 7) {
                squarePossibleCaptures |= SQUARE_TO_BITBOARD[i - 7];
            }
            BLACK_PAWN_POSSIBLE_CAPTURES[i] = squarePossibleCaptures;
        }
    }

    static final long[] KNIGHT_POSSIBLE_MOVES = new long[64];

    static {
        for (int i = (int) A1; i <= H8; i++) {
            long squarePossibleMoves = 0L;
            if (i % 8 < 6) {
                if (i < 48) {
                    squarePossibleMoves |= SQUARE_TO_BITBOARD[i + 17];
                }
                if (i > 15) {
                    squarePossibleMoves |= SQUARE_TO_BITBOARD[i - 15];
                }
            }
            if (i % 8 < 7) {
                if (i < 40) {
                    squarePossibleMoves |= SQUARE_TO_BITBOARD[i + 10];
                }
                if (i > 23) {
                    squarePossibleMoves |= SQUARE_TO_BITBOARD[i - 6];
                }
            }
            if (i % 8 > 0) {
                if (i < 40) {
                    squarePossibleMoves |= SQUARE_TO_BITBOARD[i + 6];
                }
                if (i > 23) {
                    squarePossibleMoves |= SQUARE_TO_BITBOARD[i - 10];
                }
            }
            if (i % 8 > 1) {
                if (i < 48) {
                    squarePossibleMoves |= SQUARE_TO_BITBOARD[i + 15];
                }
                if (i > 15) {
                    squarePossibleMoves |= SQUARE_TO_BITBOARD[i - 17];
                }
            }
            KNIGHT_POSSIBLE_MOVES[i] = squarePossibleMoves;
        }
    }

    static final long[] ROOK_POSSIBLE_MOVES = new long[64], BISHOP_POSSIBLE_MOVES = new long[64];

    static {
        for (int i = 0; i <= H8; i++) {
            long squarePossibleMoves = 0L;
            for (int up = i + 8; up < 64; up += 8) {
                squarePossibleMoves |= SQUARE_TO_BITBOARD[up];
            }
            for (int down = i - 8; down >= 0; down -= 8) {
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

        for (int i = 0; i <= H8; i++) {
            long squarePossibleMoves = 0L;
            for (int upLeft = i + 7; upLeft < 64 && upLeft % 8 != 0; upLeft += 7) {
                squarePossibleMoves |= SQUARE_TO_BITBOARD[upLeft];
            }
            for (int upRight = i + 9; upRight < 64 && upRight % 8 != 7; upRight += 9) {
                squarePossibleMoves |= SQUARE_TO_BITBOARD[upRight];
            }
            for (int downLeft = i - 9; downLeft >= 0 && downLeft % 8 != 0; downLeft -= 9) {
                squarePossibleMoves |= SQUARE_TO_BITBOARD[downLeft];
            }
            for (int downRight = i - 7; downRight >= 0 && downRight % 8 != 7; downRight -= 7) {
                squarePossibleMoves |= SQUARE_TO_BITBOARD[downRight];
            }
            BISHOP_POSSIBLE_MOVES[i] = squarePossibleMoves;
        }
    }

    static final long[] KING_POSSIBLE_MOVES = new long[64];

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
    }

    // TODO: Precompute since will never change
    static final long[] ROOK_MAGICS = new long[64], BISHOP_MAGICS = new long[64];

    long whitePawns, whiteKnights, whiteBishops, whiteRooks, whiteQueens, whiteKing;
    long blackPawns, blackKnights, blackBishops, blackRooks, blackQueens, blackKing;
    long whitePieces, blackPieces, allPieces;
    boolean whiteToMove;
    int enPassantIndex;
    int halfMoveClock;
    int moveCounter;
    int castleRights;

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
    }

    BitBoards makeMove(Move move) {
        BitBoards newState = switch (move.moveType()) {
            case EN_PASSANT -> makeMoveEnPassant(move);
            case CASTLE_LEFT -> makeMoveCastleLeft(move);
            case CASTLE_RIGHT -> makeMoveCastleRight(move);
            default -> makeMoveNormal(move);
        };
        newState.whiteToMove = !whiteToMove;
        ++newState.moveCounter;

        return newState;
    }

    /**
     * TODO: Implement
     *
     * @param move move to make
     * @return new state
     */
    private BitBoards makeMoveEnPassant(Move move) {
        BitBoards newState = new BitBoards(this);

        return newState;
    }

    /**
     * TODO: Implement
     *
     * @param move
     * @return
     */
    private BitBoards makeMoveCastleLeft(Move move) {
        BitBoards newState = new BitBoards(this);

        return newState;
    }

    /**
     * TODO: Implement
     *
     * @param move
     * @return
     */
    private BitBoards makeMoveCastleRight(Move move) {
        BitBoards newState = new BitBoards(this);

        return newState;
    }

    /**
     * TODO: Implement
     *
     * @param move
     * @return
     */
    private BitBoards makeMoveNormal(Move move) {
        // Set enPassantIndex if pawn double move
        BitBoards newState = new BitBoards(this);

        return newState;
    }

    /**
     * TODO: Implement
     *
     * @return
     */
    int evaluateBoard() {
        return 0;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 7; i >= 0; --i) {
            for (int j = 0; j < 8; ++j) {
                long position = 1L << (8 * i + j);
                if ((whitePawns & position) != 0) {
                    sb.append("P");
                } else if ((whiteKnights & position) != 0) {
                    sb.append("N");
                } else if ((whiteBishops & position) != 0) {
                    sb.append("B");
                } else if ((whiteRooks & position) != 0) {
                    sb.append("R");
                } else if ((whiteQueens & position) != 0) {
                    sb.append("Q");
                } else if ((whiteKing & position) != 0) {
                    sb.append("K");
                } else if ((blackPawns & position) != 0) {
                    sb.append("p");
                } else if ((blackKnights & position) != 0) {
                    sb.append("n");
                } else if ((blackBishops & position) != 0) {
                    sb.append("b");
                } else if ((blackRooks & position) != 0) {
                    sb.append("r");
                } else if ((blackQueens & position) != 0) {
                    sb.append("q");
                } else if ((blackKing & position) != 0) {
                    sb.append("k");
                } else {
                    sb.append(" ");
                }
            }
        }

        return sb.toString();
    }
}
