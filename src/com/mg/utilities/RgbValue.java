package com.mg.utilities;

public class RgbValue {
    private final int R;
    private final int G;
    private final int B;

    private RgbValue(int R, int G, int B) {
        this.R = R;
        this.G = G;
        this.B = B;
    }

    public static RgbValue of(int R, int G, int B) {
        return new RgbValue(R, G, B);
    }

    public int getR() {
        return R;
    }

    public int getG() {
        return G;
    }

    public int getB() {
        return B;
    }

//    public int asHex() {
//        StringBuilder builder = new StringBuilder();
//        builder.append(Integer.toHexString(this.getR()))
//            .append(Integer.toHexString(this.getG()))
//            .append(Integer.toHexString(this.getB()));
//        return Integer.parseInt(builder.toString(), 16);
//    }
}
