package logic;

public enum Notation {

    A1(0, 0), B1(0, 1), C1(0, 2), D1(0, 3), E1(0, 4), F1(0, 5), G1(0, 6), H1(0, 7),
    A2(1, 0), B2(1, 1), C2(1, 2), D2(1, 3), E2(1, 4), F2(1, 5), G2(1, 6), H2(1, 7),
    A3(2, 0), B3(2, 1), C3(2, 2), D3(2, 3), E3(2, 4), F3(2, 5), G3(2, 6), H3(2, 7),
    A4(3, 0), B4(3, 1), C4(3, 2), D4(3, 3), E4(3, 4), F4(3, 5), G4(3, 6), H4(3, 7),
    A5(4, 0), B5(4, 1), C5(4, 2), D5(4, 3), E5(4, 4), F5(4, 5), G5(4, 6), H5(4, 7),
    A6(5, 0), B6(5, 1), C6(5, 2), D6(5, 3), E6(5, 4), F6(5, 5), G6(5, 6), H6(5, 7),
    A7(6, 0), B7(6, 1), C7(6, 2), D7(6, 3), E7(6, 4), F7(6, 5), G7(6, 6), H7(6, 7),
    A8(7, 0), B8(7, 1), C8(7, 2), D8(7, 3), E8(7, 4), F8(7, 5), G8(7, 6), H8(7, 7);

    private final byte row;
    private final byte col;

    static final Notation[] ALL_VALUES = Notation.values();

    Notation(int r, int c) {
        this.row = (byte) r;
        this.col = (byte) c;
    }

    public byte[] getPosition() {
        return new byte[]{row, col};
    }

    public static Notation get(int r, int c) {
        if (r < 0 || r > 7 || c < 0 || c > 7) {
            throw new IllegalArgumentException("Position must be between [0, 0] x [7, 7]");
        }
        return Notation.ALL_VALUES[r * 8 + c];
    }

    public Notation flip() {
        return Notation.ALL_VALUES[63 - this.ordinal()];
    }
}