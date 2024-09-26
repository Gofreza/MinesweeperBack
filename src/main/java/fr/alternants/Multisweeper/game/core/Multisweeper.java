package fr.alternants.Multisweeper.game.core;

import fr.alternants.Multisweeper.game.PlayResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Data
@Slf4j
public class Multisweeper {
    private static Cell[][] getGrid(int rows, int cols, Difficulty difficulty) {
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

    // Game
    private boolean isGameWin;
    private boolean isGameEnded;
    private boolean isMultiplayer;

    // Stats
    private int bombsDefused;
    private int bombsExplosion;
    private int cellsRevealed;
    private int nbBombs;
    private int nbFlags;


    public Multisweeper(int rows, int cols, boolean isMultiplayer, Difficulty difficulty) {
        this(rows, cols, isMultiplayer, difficulty, Multisweeper.getGrid(rows, cols, difficulty));
    }

    public Multisweeper(int rows, int cols, boolean isMultiplayer, Difficulty difficulty, Cell[][] grid) {
        this.grid = grid;

        // Utils
        this.difficulty = difficulty;
        this.rows = rows;
        this.cols = cols;

        // Game
        isGameWin = false;
        isGameEnded = false;
        this.isMultiplayer = isMultiplayer;

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
                    if (!grid[newRow][newCol].isVisible() && !grid[newRow][newCol].isFlagged()) {
                        setVisible(newRow, newCol);
                        if(grid[newRow][newCol].isBomb()) setExploded(newRow, newCol);
                        responses.add(new PlayResponse.CellResponse(newRow, newCol, grid[newRow][newCol]));
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

    public PlayResponse.CellResponse flag(int row, int col){
        Cell cell = grid[row][col];
        if(cell.isVisible()) return new PlayResponse.CellResponse(row, col, cell); // Can't flag a visible cell

        cell.setFlagged(!cell.isFlagged());
        if(cell.isFlagged()) {
            nbFlags++;
            return new PlayResponse.CellResponse(row, col, new Cell().setFlagged(true));
        } else {
            nbFlags--;
            return new PlayResponse.CellResponse(row, col, new Cell());
        }
    }

    public List<PlayResponse.CellResponse> getVisibleGrid() {
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
        sb.append("Multisweeper{");
        sb.append("isGameWin=").append(isGameWin);
        sb.append(", isGameEnded=").append(isGameEnded);
        sb.append(", isMultiplayer=").append(isMultiplayer);
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
