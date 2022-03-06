package com.mg.exceptions;

public class GridNotValidException extends RuntimeException {
    public GridNotValidException() {
        super("Grid not valid!");
    }

    public GridNotValidException(String reason) {
        super(reason);
    }
}
