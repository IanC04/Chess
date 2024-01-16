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
    static final int A1 = 0, H1 = 7, A2 = 8, A8 = 56, H8 = 63,
            WHITE_KING_START = 4,
            BLACK_KING_START = 60;
    static final long RANK_1 = 0xFFL, RANK_2 = 0xFFL << 8, RANK_3 = 0xFFL << 16, RANK_4 =
            0xFFL << 24, RANK_5 = 0xFFL << 32, RANK_6 = 0xFFL << 40, RANK_7 = 0xFFL << 48, RANK_8
            = 0xFFL << 56, WHITE_KiNG_LEfT_CASTLE_OPEN = 0xEL, WHITE_KING_RIGHT_CASTLE_OPEN = 0x60L,
            BLACK_KING_LEFT_CASTLE_OPEN = 0xEL << 56, BLACK_KING_RIGHT_CASTLE_OPEN =
            0x60L << 56, WHITE_KING_LEFT_SAFE_NEEDED = 0x1CL, WHITE_KING_RIGHT_SAFE_NEEDED = 0x70L,
            BLACK_KING_LEFT_SAFE_NEEDED = 0x1CL << 56, BLACK_KING_RIGHT_SAFE_NEEDED = 0x70L << 56;
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
    }

    static final long[] KNIGHT_POSSIBLE_MOVES = new long[64];

    static {
        for (int i = A1; i <= H8; i++) {
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
        for (int i = A1; i <= H8; i++) {
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

        for (int i = A1; i <= H8; i++) {
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

    // TODO: Precompute since will never change
    static final long[][] ROOK_ATTACKS = new long[64][], BISHOP_ATTACKS = new long[64][];

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
        BitBoards newState = switch (move.moveType()) {
            case NORMAL -> makeMoveNormal(move);
            case EN_PASSANT -> makeMoveEnPassant(move);
            case CASTLE_LEFT -> makeMoveCastleLeft(move);
            case CASTLE_RIGHT -> makeMoveCastleRight(move);
            case PAWN_DOUBLE_MOVE -> makeMovePawnDouble(move);
            case PROMOTE_ROOK, PROMOTE_KNIGHT, PROMOTE_BISHOP, PROMOTE_QUEEN ->
                    makeMovePromotion(move);
            default ->
                    throw new IllegalStateException("Unexpected value in make move: " + move.moveType());
        };
        newState.whiteToMove = !whiteToMove;
        ++newState.moveCounter;
        return newState;
    }

    /**
     * Called when making a move and updating the game status
     *
     * @param move move to make
     * @return new state
     */
    BitBoards makeMove(Move move) {
        BitBoards newState = tryMove(move);
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
            case CHECKMATE, STALEMATE, FIFTY_MOVE_RULE, THREEFOLD_REPETITION, INSUFFICIENT_MATERIAL -> true;
            case NORMAL, CHECK -> false;
        };
    }

    /**
     * @param move move to make
     * @return new state
     */
    private BitBoards makeMoveNormal(Move move) {
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
                newState.blackRooks &= ~endBitboard;
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
                newState.whiteRooks &= ~endBitboard;
                newState.whiteKnights &= ~endBitboard;
                newState.whiteBishops &= ~endBitboard;
                newState.whiteQueens &= ~endBitboard;
                newState.whiteKing &= ~endBitboard;
                newState.whitePieces &= ~endBitboard;
            }
        }
        newState.allPieces &= ~startBitboard;
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
    private BitBoards makeMoveEnPassant(Move move) {
        BitBoards newState = new BitBoards(this);
        int start = move.start();
        long startBitboard = SQUARE_TO_BITBOARD[start];
        int end = move.end();
        long endBitboard = SQUARE_TO_BITBOARD[end];
        if (newState.whiteToMove) {
            newState.whitePawns ^= startBitboard | endBitboard;
            newState.whitePieces ^= startBitboard | endBitboard;
            newState.allPieces ^= startBitboard | endBitboard;
            newState.blackPawns ^= SQUARE_TO_BITBOARD[end - 8];
            newState.blackPieces ^= SQUARE_TO_BITBOARD[end - 8];
            newState.allPieces ^= SQUARE_TO_BITBOARD[end - 8];
        } else {
            newState.blackPawns ^= startBitboard | endBitboard;
            newState.blackPieces ^= startBitboard | endBitboard;
            newState.allPieces ^= startBitboard | endBitboard;
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
    private BitBoards makeMoveCastleLeft(Move move) {
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
    private BitBoards makeMoveCastleRight(Move move) {
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
    private BitBoards makeMovePawnDouble(Move move) {
        BitBoards newState = new BitBoards(this);
        long startBitboard = SQUARE_TO_BITBOARD[move.start()], endBitboard =
                SQUARE_TO_BITBOARD[move.end()];
        if (newState.whiteToMove) {
            newState.whitePawns ^= startBitboard | endBitboard;
            newState.whitePieces ^= startBitboard | endBitboard;
            newState.allPieces ^= startBitboard | endBitboard;
            newState.enPassantIndex = move.end() - 8;
        } else {
            newState.blackPawns ^= startBitboard | endBitboard;
            newState.blackPieces ^= startBitboard | endBitboard;
            newState.allPieces ^= startBitboard | endBitboard;
            newState.enPassantIndex = move.end() + 8;
        }
        newState.halfMoveClock = 0;

        return newState;
    }

    private BitBoards makeMovePromotion(Move move) {
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

                newState.whiteRooks ^= endBitboard;
                newState.whiteKnights ^= endBitboard;
                newState.whiteBishops ^= endBitboard;
                newState.whiteQueens ^= endBitboard;
                newState.whitePieces ^= endBitboard;
            }
        }
        newState.allPieces ^= startBitboard | endBitboard;

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

    GameStatus getGameStatus() {
        return gameStatus;
    }

    /**
     * TODO: evaluate current board
     *
     * @return value of the board
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
