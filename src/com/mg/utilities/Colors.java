package com.mg.utilities;

public enum Colors {
    BLUE(RgbValue.of(173, 216, 230)),
    RED(RgbValue.of(255, 114, 118)),
    GREEN(RgbValue.of(144, 238, 144)),
    DARK_GREEN(RgbValue.of(0, 153, 0)),
    YELLOW(RgbValue.of(230, 230, 0));

    private final RgbValue rgbValue;

    Colors(RgbValue rgbVal) {
        this.rgbValue = rgbVal;
    }

    public RgbValue getRgbValue() {
        return RgbValue.of(this.rgbValue.getR(), this.rgbValue.getG(), this.rgbValue.getB());
    }
}