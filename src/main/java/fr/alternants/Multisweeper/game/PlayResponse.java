package fr.alternants.Multisweeper.game;

import fr.alternants.Multisweeper.game.core.Cell;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlayResponse {
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CellResponse {
        private Integer row;
        private Integer col;
        private Cell cell;
    }

    private List<CellResponse> changedCells;
    private Boolean isGameEnded;
    private Boolean isGameWin;
    private Integer roomId;
    private String username;
}
