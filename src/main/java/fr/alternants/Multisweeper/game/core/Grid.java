package fr.alternants.Multisweeper.game.core;

import lombok.ToString;

import java.util.Random;

@ToString
public class Grid {

    private final int rows;
    private final int cols;
    private int nbBombs;

    private Cell[][] cells;

    public Grid(int rows, int cols, Difficulty difficulty) {
        this.rows = rows;
        this.cols = cols;
        this.nbBombs = 0;

        this.cells = new Cell[rows][cols];

        initBombs(difficulty);
        initBombAround();

    }

    private void initBombs(Difficulty difficulty) {
        Random rand = new Random();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cells[r][c] = new Cell();
                if (rand.nextFloat() < difficulty.getValue()) {
                    cells[r][c].setBomb(true);
                    nbBombs++;
                }
            }
        }
    }

    private void initBombAround() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int bombAround = 0;
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        if (i == 0 && j == 0) continue; // Skip the cell itself
                        int newRow = r + i;
                        int newCol = c + j;
                        if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols) {
                            if (cells[newRow][newCol].isBomb()) {
                                bombAround++;
                            }
                        }
                    }
                }
                cells[r][c].setBombAround(bombAround);
            }
        }
    }

    public void play(int row, int col) {

    }
}
