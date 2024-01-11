package ai;

class BitBoards {
    /**
     * Returns the positional value of the piece with respect to the board from white's POV
     * <br>Inspired by
     * <a href="https://github.com/bartekspitza/sophia/blob/master/src/evaluation.c">GitHub</a>
     */
    private static final int[][] WHITE_PAWN_VALUES = {
            {0, 0, 0, 0, 0, 0, 0, 0},
            {50, 50, 50, 50, 50, 50, 50, 50},
            {10, 10, 20, 30, 30, 20, 10, 10},
            {5, 5, 10, 25, 25, 10, 5, 5},
            {0, 0, 0, 20, 20, 0, 0, 0},
            {5, -5, -10, 0, 0, -10, -5, 5},
            {5, 10, 10, -20, -20, 10, 10, 5},
            {0, 0, 0, 0, 0, 0, 0, 0}
    };

    private static final int[][] WHITE_KNIGHT_VALUES = {
            {-50, -40, -30, -30, -30, -30, -40, -50},
            {-40, -20, 0, 0, 0, 0, -20, -40},
            {-30, 0, 10, 15, 15, 10, 0, -30},
            {-30, 5, 15, 20, 20, 15, 5, -30},
            {-30, 0, 15, 20, 20, 15, 0, -30},
            {-30, 5, 10, 15, 15, 10, 5, -30},
            {-40, -20, 0, 5, 5, 0, -20, -40},
            {-50, -40, -30, -30, -30, -30, -40, -50}
    };

    private static final int[][] WHITE_BISHOP_VALUES = {
            {-20, -10, -10, -10, -10, -10, -10, -20},
            {-10, 0, 0, 0, 0, 0, 0, -10},
            {-10, 0, 5, 10, 10, 5, 0, -10},
            {-10, 5, 5, 10, 10, 5, 5, -10},
            {-10, 0, 10, 10, 10, 10, 0, -10},
            {-10, 10, 10, 10, 10, 10, 10, -10},
            {-10, 5, 0, 0, 0, 0, 5, -10},
            {-20, -10, -10, -10, -10, -10, -10, -20}};

    private static final int[][] WHITE_ROOK_VALUES = {
            {0, 0, 0, 0, 0, 0, 0, 0},
            {5, 10, 10, 10, 10, 10, 10, 5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {0, 0, 0, 5, 5, 0, 0, 0}};
    private static final int[][] WHITE_QUEEN_VALUES = {
            {-20, -10, -10, -5, -5, -10, -10, -20},
            {-10, 0, 0, 0, 0, 0, 0, -10},
            {-10, 0, 5, 5, 5, 5, 0, -10},
            {-5, 0, 5, 5, 5, 5, 0, -5},
            {0, 0, 5, 5, 5, 5, 0, -5},
            {-10, 5, 5, 5, 5, 5, 0, -10},
            {-10, 0, 5, 0, 0, 0, 0, -10},
            {-20, -10, -10, -5, -5, -10, -10, -20}};
    private static final int[][] WHITE_KING_VALUES = {
            {20, 30, 10, 0, 0, 10, 30, 20},
            {20, 20, 0, 0, 0, 0, 20, 20},
            {-10, -20, -20, -20, -20, -20, -20, -10},
            {-20, -30, -30, -40, -40, -30, -30, -20},
            {-30, -40, -40, -50, -50, -40, -40, -30},
            {-30, -40, -40, -50, -50, -40, -40, -30},
            {-30, -40, -40, -50, -50, -40, -40, -30},
            {-30, -40, -40, -50, -50, -40, -40, -30}};
    private long whitePawns, whiteKnights, whiteBishops, whiteRooks, whiteQueens, whiteKing;
    private long blackPawns, blackKnights, blackBishops, blackRooks, blackQueens, blackKing;
    private long whitePieces, blackPieces;
    private boolean whiteToMove;
    private byte enPassant;
    private int halfMoveClock;
    private int moveCounter;
    private byte castleRights;
    boolean checkmate, stalemate;

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
        this.enPassant = enPassant.equals("-") ? -1 : Move.notationToIndex(enPassant);
        this.halfMoveClock = Integer.parseInt(halfMoveClock);
        this.moveCounter = Integer.parseInt(moveCounter);
    }

    BitBoards(BitBoards old, Move move) {
        this.whitePawns = old.whitePawns;
        this.whiteKnights = old.whiteKnights;
        this.whiteBishops = old.whiteBishops;
        this.whiteRooks = old.whiteRooks;
        this.whiteQueens = old.whiteQueens;
        this.whiteKing = old.whiteKing;
        this.blackPawns = old.blackPawns;
        this.blackKnights = old.blackKnights;
        this.blackBishops = old.blackBishops;
        this.blackRooks = old.blackRooks;
        this.blackQueens = old.blackQueens;
        this.blackKing = old.blackKing;
        this.whitePieces = old.whitePieces;
        this.blackPieces = old.blackPieces;
        this.whiteToMove = !old.whiteToMove;
        this.enPassant = old.enPassant;
        this.halfMoveClock = old.halfMoveClock;
        this.moveCounter = old.moveCounter;
        this.castleRights = old.castleRights;
        this.checkmate = old.checkmate;
        this.stalemate = old.stalemate;
        // TODO: Make this more efficient
    }

    BitBoards makeMove(Move move) {
        return new BitBoards(this, move);
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
