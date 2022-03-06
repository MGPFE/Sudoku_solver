package com.mg.gui;

import com.mg.algorithms.SudokuSolver;
import com.mg.utilities.RgbValue;
import com.mg.utilities.Colors;

import java.util.concurrent.ExecutorService;
import javax.swing.event.ChangeListener;
import javax.swing.border.BevelBorder;
import java.util.concurrent.Executors;
import javax.swing.event.ChangeEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import javax.swing.*;
import java.awt.*;

/**
 * This class generates the GUI for sudoku solving visualization.
 * @author Mariusz Gworek
 * @version 1.0
 */
public class Frame extends JFrame implements Drawable, ActionListener, ChangeListener {
    // CONSTANTS
    private final Font FONT = new Font("Consolas", Font.PLAIN, 26);
    // CONTROLS
    private JSpinner algoSpeedSpinner;
    private JPanel gridPanel;
    // LOGIC
    private int[][] data = SudokuSolver.generateSudokuBoard();
    private final JLabel[][] labelsGrid = new JLabel[9][9];
    /**
     * This 2d array is used to check whether the data in the {@link #data data} array
     * has been modified, if it was then number 1 appears at the exact x, y position
     * of the modified number of the {@link #data data} array in this array.
     */
    private int[][] wasFieldEditedArr = new int[9][9];
    private int[][] dataCopy = copyArray();
    private ExecutorService executor;
    private int algoSpeed = 150;

    public Frame() {
        final int WIDTH = 600;
        final int HEIGHT = 640;

        this.setLayout(null);
        this.setResizable(false);
        this.setSize(WIDTH, HEIGHT);
        this.setTitle("Sudoku solver");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        setNimbusLook();
        drawControlPanel();

        drawData();

        this.setVisible(true);
    }

    private void drawControlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setBounds(0, 0, 600, 50);

        JButton solveSudokuBtn = new JButton("Solve");
        solveSudokuBtn.setSize(new Dimension(150, 50));
        solveSudokuBtn.setFont(FONT);
        solveSudokuBtn.addActionListener(this);

        JButton generateSudokuBtn = new JButton("Generate sudoku");
        generateSudokuBtn.setSize(new Dimension(150, 50));
        generateSudokuBtn.setFont(FONT);
        generateSudokuBtn.addActionListener(this);

        JLabel algoSpeedLabel = new JLabel("Speed (5 - Fast, 500 - Slow): ");

        algoSpeedSpinner = new JSpinner(new SpinnerNumberModel(algoSpeed, 5, 500, 5));
        algoSpeedSpinner.addChangeListener(this);

        controlPanel.add(solveSudokuBtn);
        controlPanel.add(generateSudokuBtn);
        controlPanel.add(algoSpeedLabel);
        controlPanel.add(algoSpeedSpinner);
        this.add(controlPanel);
    }

    // DRAW METHODS

    /**
     * This method draws the data on {@link #gridPanel} JPanel
     */
    @Override
    public void drawData() {
        if (gridPanel != null) {
            this.remove(gridPanel);
        }
        gridPanel = new JPanel();
        gridPanel.setBounds(5, 51, 577, 549);
        GridLayout gridLayout = new GridLayout(3, 3);
        gridLayout.setVgap(7);
        gridLayout.setHgap(7);
        gridPanel.setLayout(gridLayout);

        JPanel[] innerGrids = generateInnerGrids();
        populateInnerGrids(innerGrids);

        Arrays.stream(innerGrids).forEach(gridPanel::add);

        this.add(gridPanel);
        gridPanel.updateUI();
    }

    // UTILITY METHODS
    private void setNimbusLook() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (
            ClassNotFoundException |
            InstantiationException |
            IllegalAccessException |
            UnsupportedLookAndFeelException e
        ) {
            e.printStackTrace();
        }
    }

    private JPanel[] generateInnerGrids() {
        // 9 JPanels because we have 9 3x3 squares
        // Each square holds 9 JButtons
        JPanel[] threeByThrees = new JPanel[9];
        for (int i = 0; i < 9; i++) {
            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(3, 3));
            threeByThrees[i] = panel;
        }
        return threeByThrees;
    }

    private void populateInnerGrids(JPanel[] innerGrids) {
        int topLeftRow = 0;
        int topLeftCol = 0;

        for (JPanel square : innerGrids) {
            for (int i = topLeftRow; i < topLeftRow + 3; i++) {
                for (int j = topLeftCol; j < topLeftCol + 3; j++) {
                    JLabel label = new JLabel("" + data[i][j]);
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                    label.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
                    label.setOpaque(true);
                    if (!label.getText().equals("0") && dataCopy[i][j] == 0) {
                        RgbValue green = Colors.GREEN.getRgbValue();
                        label.setBackground(new Color(green.getR(), green.getG(), green.getB()));
                        wasFieldEditedArr[i][j] = 1;
                    } else if (wasFieldEditedArr[i][j] == 1) {
                        RgbValue yellow = Colors.YELLOW.getRgbValue();
                        label.setBackground(new Color(yellow.getR(), yellow.getG(), yellow.getB()));
                    } else if (label.getText().equals("0")) {
                        RgbValue red = Colors.RED.getRgbValue();
                        label.setBackground(new Color(red.getR(), red.getG(), red.getB()));
                    }
                    labelsGrid[i][j] = label;
                    label.setFont(FONT);
                    label.setSize(new Dimension(30, 30));

                    square.add(label);
                }
            }
            topLeftCol += 3;
            if (topLeftCol == 9) {
                topLeftCol = 0;
                topLeftRow += 3;
            }
        }
    }

    private void solveSudoku() {
        executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> new SudokuSolver(this).solveSudoku(data, algoSpeed));
        executor.shutdown();
    }

    /**
     * This method returns a JLabel from {@link #labelsGrid labelsGrid}
     * at the given x and y position
     * @param x Row at which the JLabel resides
     * @param y Column at which the JLabel resides
     * @return JLabel at given x and y position
     */
    public JLabel getLabelFromGrid(int x, int y) {
        return labelsGrid[x][y];
    }

    /**
     * This method copies the {@link #data data} array contents
     * into the {@link #dataCopy dataCopy} array.
     * @return Copy of the original data array.
     */
    private int[][] copyArray() {
        int[][] copy = new int[9][9];
        for (int i = 0; i < 9; i++) {
            System.arraycopy(data[i], 0, copy[i], 0, 9);
        }
        return copy;
    }

    // LISTENERS
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Solve")) {
            if (executor != null) executor.shutdownNow();
            solveSudoku();
            drawData();
        } else if (e.getActionCommand().equals("Generate sudoku")) {
            data = SudokuSolver.generateSudokuBoard();
            dataCopy = copyArray();
            wasFieldEditedArr = new int[9][9];
            if (executor != null) executor.shutdownNow();
            drawData();
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == algoSpeedSpinner) {
            algoSpeed = (int) algoSpeedSpinner.getValue();
        }
    }
}
