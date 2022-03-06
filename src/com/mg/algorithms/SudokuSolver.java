package com.mg.algorithms;

import com.mg.exceptions.GridNotValidException;
import com.mg.gui.Drawable;

import java.util.concurrent.ThreadLocalRandom;
import java.awt.*;

/**
 * This final class contains the methods for
 * generating random sudoku grids and solving them
 * @author Mariusz Gworek
 * @version 1.0
 */
public final class SudokuSolver {
    private Drawable drawable;

    private SudokuSolver() {}

    public SudokuSolver(Drawable drawable) {
        this.drawable = drawable;
    }

    /**
     * This method is used to check whether the row, in which the index
     * is located can be populated with a given number
     * @param grid 2d int array, contains a sudoku to solve
     * @param num number which is potentially used to populate the current index
     * @param row row at which we try to insert a number
     * @return true if placement is valid, else false
     */
    private boolean isRowClear(int[][] grid, int num, int row) {
        for (int i = 0; i < grid[row].length; i++) {
            if (grid[row][i] == num) {
                return false;
            }
        }
        return true;
    }

    /**
     * This method is used to check whether the column, in which the index
     * is located can be populated with a given number
     * @param grid 2d int array, contains a sudoku to solve
     * @param num number which is potentially used to populate the current index
     * @param column column at which we try to insert a number
     * @return true if placement is valid, else false
     */
    private boolean isColumnClear(int[][] grid, int num, int column) {
        for (int[] ints : grid) {
            if (ints[column] == num) {
                return false;
            }
        }
        return true;
    }

    /**
     * This method is used to check whether the square, in which the current index
     * is located can be populated with a given number
     * @param grid 2d int array, contains a sudoku to solve
     * @param num number which is potentially used to populate the current index
     * @param row row at which we try to insert a number
     * @param column column at which we try to insert a number
     * @return true if placement is valid, else false
     */
    private boolean isSquareClear(int[][] grid, int num, int row, int column) {
        int squareLeftTopRow = row - row % 3;
        int squareLeftTopColumn = column - column % 3;

        for (int i = squareLeftTopRow; i < squareLeftTopRow + 3; i++) {
            for (int j = squareLeftTopColumn; j < squareLeftTopColumn + 3; j++) {
                if (grid[i][j] == num) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * This method wraps all the methods which check for validity of placement
     * @implNote USE THIS METHOD INSTEAD OF USING REST OF THEM SEPARATELY
     * @param grid 2d int array, contains a sudoku to solve
     * @param num current number that we try to insert at the spot with 0 in it
     * @param row row at which we try to insert @param num
     * @param column column at which we try to insert @param num
     * @return true if placement is valid, else false
     */
    private boolean isValidPlacement(int[][] grid, int num, int row, int column) {
        return isRowClear(grid, num, row) &&
            isColumnClear(grid, num, column) &&
            isSquareClear(grid, num, row, column);
    }

    private void checkIfGridIsValid(int gridX, int gridY) {
        if (!(gridX % 3 == 0 && gridY % 3 == 0)) {
            throw new GridNotValidException("The grid has to be divisible by 3!");
        } else if ((gridX < 1 || gridY < 1)) {
            throw new GridNotValidException("The grid cannot be 0 by 0!");
        }
    }

    private boolean solveSudoku(int[][] grid) {
        checkIfGridIsValid(grid[0].length, grid.length);
        return solveSudoku(grid, grid[0].length, grid.length, 150);
    }

    /**
     * This method is a simplified solveSudoku method
     * it automatically figures out the size of the grid
     * @param grid a 2d int array to solve (int[][])
     * @param algoSpeed speed of the algorithm execution
     * @throws GridNotValidException if grid size is not divisible by 3 or grid is 0 by 0
     * @return true if a grid has a solution, false if it doesn't
     */
    public boolean solveSudoku(int[][] grid, int algoSpeed) {
        checkIfGridIsValid(grid[0].length, grid.length);
        return solveSudoku(grid, grid[0].length, grid.length, algoSpeed);
    }

    /**
     * This method solves a given sudoku grid
     * @param grid a 2d int array to solve (int[][])
     * @param gridX length of a row
     * @param gridY length of a column
     * @param algoSpeed speed of the algorithm execution
     * @return true if a grid has a solution, false if it doesn't
     */
    public boolean solveSudoku(int[][] grid, int gridX, int gridY, int algoSpeed) {
        for (int i = 0; i < gridX; i++) {
            for (int j = 0; j < gridY; j++) {
                if (grid[i][j] == 0) {
                    for (int k = 1; k <= 9; k++) {
                        if (isValidPlacement(grid, k, i, j)) {
                            grid[i][j] = k;
                            if (drawable != null) {
                                EventQueue.invokeLater(drawable::drawData);
                                try {
                                    Thread.sleep(algoSpeed);
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                }
                            }

                            if (solveSudoku(grid, gridX, gridY, algoSpeed)) {
                                return true;
                            } else {
                                grid[i][j] = 0;
                                if (drawable != null) {
                                    EventQueue.invokeLater(drawable::drawData);
                                    try {
                                        Thread.sleep(algoSpeed);
                                    } catch (InterruptedException e) {
                                        Thread.currentThread().interrupt();
                                    }
                                }
                            }
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * This method is a simplified version of generateSudokuBoard
     * it passes the width and height as 9, 9
     * @return a randomly generated sudoku grid as 2d int array (int[][])
     */
    public static int[][] generateSudokuBoard() {
        return generateSudokuBoard(9, 9);
    }

    /**
     * This method generates a random sudoku grid
     * @param gridX width of a grid
     * @param gridY height of a grid
     * @throws GridNotValidException if grid size is not divisible by 3 or grid is 0 by 0
     * @return a randomly generated sudoku grid as 2d int array (int[][])
     */
    public static int[][] generateSudokuBoard(int gridX, int gridY) {
        if (!(gridX % 3 == 0 && gridY % 3 == 0)) {
            throw new GridNotValidException("The grid has to be divisible by 3!");
        } else if ((gridX == 0 || gridY == 0)) {
            throw new GridNotValidException("The grid cannot be 0 by 0!");
        }

        int[][] board = new int[gridX][gridY];
//        boolean makeZero;

        /*
        Simple pseudorandom mechanism which allows the sudoku grid
        to be slightly different every time it's launched
        Populates the upper left corners of 3x3 squares
        diagonally from top left to bottom right square
        */
        for (int i = 0; i < gridX; i += 3) {
            board[i][i] = ThreadLocalRandom.current().nextInt(1, 10);
        }

        // CODE TO GENERATE MORE COMPLETE SUDOKU BOARD

//        new SudokuSolver().solveSudoku(board);

//        for (int i = 0; i < gridX; i++) {
//            for (int j = 0; j < gridY; j++) {
//                makeZero = ThreadLocalRandom.current().nextBoolean();
//                if (makeZero) {
//                    board[i][j] = 0;
//                }
//            }
//        }

        return board;
    }
}
