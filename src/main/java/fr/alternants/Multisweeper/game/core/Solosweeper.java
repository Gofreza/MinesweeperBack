package fr.alternants.Multisweeper.game.core;

import fr.alternants.Multisweeper.game.PlayResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Data
@Slf4j
public class Solosweeper {
    private static Cell[][] getGrid(int rows, int cols, int rowStart, int colStart, Difficulty difficulty) {
        Cell[][] cells = Solosweeper.getGrid(rows, cols, difficulty);
        cells[rowStart][colStart].setBomb(false);
        return cells;
    }

    protected static Cell[][] getGrid(int rows, int cols, Difficulty difficulty) {
        Cell[][] cells = new Cell[rows][cols];
        Random rand = new Random();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cells[r][c] = new Cell();
                if (rand.nextFloat() < difficulty.getValue()) {
                    cells[r][c].setBomb(true);
                }
            }
        }

        return cells;
    }


    private Cell[][] grid;

    // Utils
    private Difficulty difficulty;
    private int rows;
    private int cols;
    private String username;

    // Game
    private boolean isGameWin;
    private boolean isGameEnded;

    // Stats
    private int bombsDefused;
    private int bombsExplosion;
    private int cellsRevealed;
    private int nbBombs;
    private int nbFlags;


    public Solosweeper(int rows, int cols, Difficulty difficulty) {
        this(rows, cols, difficulty, Solosweeper.getGrid(rows, cols, difficulty));
    }

    public Solosweeper(int rows, int cols, int rowStart, int colStart, Difficulty difficulty) {
        this(rows, cols, difficulty, Solosweeper.getGrid(rows, cols, rowStart, colStart, difficulty));

        if(rowStart >= 0 && rowStart < rows && colStart >= 0 && colStart < cols)
            setVisible(rowStart, colStart);
    }

    public Solosweeper(int rows, int cols, Difficulty difficulty, Cell[][] grid) {
        this.grid = grid;

        // Utils
        this.difficulty = difficulty;
        this.rows = rows;
        this.cols = cols;

        // Game
        isGameWin = false;
        isGameEnded = false;

        // Stats
        bombsDefused = 0;
        bombsExplosion = 0;
        cellsRevealed = 0;
        this.nbFlags = 0;

        initBombs();
    }

    // Init nbBombs and bombAround
    private void initBombs() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c].isBomb()) nbBombs++; // Is a bomb
                else {
                    int bombAround = 0;
                    for (int i = -1; i <= 1; i++) { // Look bomb around
                        for (int j = -1; j <= 1; j++) {
                            if (i == 0 && j == 0) continue; // Skip the cell itself
                            int newRow = r + i;
                            int newCol = c + j;
                            if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols) {
                                if (grid[newRow][newCol].isBomb()) {
                                    bombAround++;
                                }
                            }
                        }
                    }
                    grid[r][c].setBombAround(bombAround);
                }
            }
        }
    }

    private Cell setVisible(int row, int col) {
        Cell cell = grid[row][col];
        if(!cell.isVisible()) {
            cell.setVisible(true);
            cellsRevealed++;
        }
        return cell;
    }

    private Cell setExploded(int row, int col) {
        Cell cell = grid[row][col];
        cell.setExploded(true); // Loose
        cell.setVisible(true);
        bombsExplosion++;
        isGameEnded = true;
        return cell;
    }

    private List<PlayResponse.CellResponse> getVisibleGrid() {
        List<PlayResponse.CellResponse> responses = new ArrayList<>();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c].isVisible()) {
                    responses.add(new PlayResponse.CellResponse(r, c, grid[r][c]));
                } else if (grid[r][c].isFlagged()) {
                    responses.add(new PlayResponse.CellResponse(r, c, new Cell().setFlagged(true)));
                }
            }
        }
        return responses;
    }

    private List<PlayResponse.CellResponse> getAllGrid() {
        List<PlayResponse.CellResponse> responses = new ArrayList<>();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                responses.add(new PlayResponse.CellResponse(r, c, grid[r][c]));
            }
        }
        return responses;
    }

    private int getNbFlagAround(int row, int col){
        int nbFlagsAround = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newRow = row + i;
                int newCol = col + j;
                if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols) {
                    if (grid[newRow][newCol].isFlagged()) {
                        nbFlagsAround++;
                    }
                }
            }
        }
        log.info("Nb flags around: " + nbFlagsAround + " for cell " + row + " " + col + " with " + grid[row][col].getBombAround() + " bombs around");
        return nbFlagsAround;
    }

    private void propagate(int row, int col, List<PlayResponse.CellResponse> responses) {
        log.info("Propagate on cell " + row + " " + col);
        responses.add(new PlayResponse.CellResponse(row, col, setVisible(row, col)));

        if(grid[row][col].getBombAround() == 0) for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newRow = row + i;
                int newCol = col + j;
                if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols) {
                    if (!grid[newRow][newCol].isVisible() && !grid[newRow][newCol].isFlagged()) {
                        propagate(newRow, newCol, responses);
                    }
                }
            }
        }
    }

    private void revealAllAround(int row, int col, List<PlayResponse.CellResponse> responses) {
        log.info("Reveal all cells around " + row + " " + col);
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newRow = row + i;
                int newCol = col + j;
                if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols) {
                    Cell cell = grid[newRow][newCol];
                    if (!cell.isVisible() && !cell.isFlagged()) {
                        setVisible(newRow, newCol);
                        if(cell.isBomb()) setExploded(newRow, newCol);
                        else if(cell.getBombAround() == 0) propagate(newRow, newCol, responses);
                        responses.add(new PlayResponse.CellResponse(newRow, newCol, cell));
                    }
                }
            }
        }
    }

    public List<PlayResponse.CellResponse> play(int row, int col) {
        List<PlayResponse.CellResponse> responses = new ArrayList<>();
        Cell cell = grid[row][col];

        if (cell.isBomb()) { // Loose
            responses.add(new PlayResponse.CellResponse(row, col, setExploded(row, col)));
            return responses;
        }
        else if(cell.isFlagged()) return responses; // Flagged, can't play
        else if (cell.isVisible()) { // If visible, play if flag around else dont play
            if (cell.getBombAround() == 0) return responses; // No bomb around, no need to play
            else if (getNbFlagAround(row, col) < cell.getBombAround()) return responses; // Not enough flags around
            else revealAllAround(row, col, responses);
        }
        else {
            propagate(row, col, responses);
        }

        return responses;
    }

    public List<PlayResponse.CellResponse> flag(int row, int col){
        Cell cell = grid[row][col];
        if(cell.isVisible()) return play(row, col); // Can't flag a visible cell, so play

        cell.setFlagged(!cell.isFlagged());
        if(cell.isFlagged()) nbFlags++;
        else nbFlags--;

        return List.of(new PlayResponse.CellResponse(row, col, new Cell().setFlagged(cell.isFlagged())));
    }

    public List<PlayResponse.CellResponse> getGrid() {
        if(isGameEnded) return getAllGrid();
        else return getVisibleGrid();
    }

    public void checkGameWin() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (!grid[r][c].isVisible() && !grid[r][c].isBomb()) {
                    return;
                }
            }
        }
        isGameWin = true;
        isGameEnded = true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Solosweeper{");
        sb.append("username='").append(username);
        sb.append("isGameWin=").append(isGameWin);
        sb.append(", isGameEnded=").append(isGameEnded);
        sb.append(", difficulty=").append(difficulty);
        sb.append(", bombsDefused=").append(bombsDefused);
        sb.append(", bombsExplosion=").append(bombsExplosion);
        sb.append(", cellsRevealed=").append(cellsRevealed);
        sb.append(", nbBombs=").append(nbBombs);
        sb.append(", grid=\n");

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                sb.append(grid[r][c].toString()).append(" ");
            }
            sb.append("\n");
        }

        sb.append('}');
        return sb.toString();
    }



}
