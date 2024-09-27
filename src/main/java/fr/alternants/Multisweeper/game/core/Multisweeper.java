package fr.alternants.Multisweeper.game.core;

import lombok.Data;

import java.util.HashMap;

@Data
public class Multisweeper {
    private Cell[][] baseGrid;
    private HashMap<String, Solosweeper> games;

    public Multisweeper(int rows, int cols, Difficulty difficulty) {
        this.baseGrid = Solosweeper.getGrid(rows, cols, difficulty);
        this.games = new HashMap<>();
        this.difficulty = difficulty;
        this.rows = rows;
        this.cols = cols;
    }

    // Utils
    private Difficulty difficulty;
    private int rows;
    private int cols;

    // Game
    private String winner;
    private boolean isGameEnded;

    public void addPlayer(String playerId) {
        this.games.put(playerId, null);
    }

    public void removePlayer(String playerId) {
        this.games.remove(playerId);
    }

    public void startGames() {
        this.games.replaceAll((_, _) -> new Solosweeper(this.rows, this.cols, this.difficulty, this.baseGrid));
    }

    public void play(String playerId, int row, int col) {
        Solosweeper solosweeper = this.games.get(playerId);
        if (solosweeper != null) {
            solosweeper.play(row, col);
        }
    }

    public void flag(String playerId, int row, int col) {
        Solosweeper solosweeper = this.games.get(playerId);
        if (solosweeper != null) {
            solosweeper.flag(row, col);
        }
    }

    public Boolean checkEnded(String playerId) {
        Solosweeper solosweeper = this.games.get(playerId);
        if (solosweeper != null) {
            solosweeper.checkGameWin();
            if(solosweeper.isGameWin()) {
                this.winner = solosweeper.getUsername();
                this.isGameEnded = true;
                return true;
            }
        }
        return false;
    }

}
