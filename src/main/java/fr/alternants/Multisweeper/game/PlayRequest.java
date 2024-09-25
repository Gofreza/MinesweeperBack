package fr.alternants.Multisweeper.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlayRequest {
    private Integer row;
    private Integer col;
    private Long roomId;
    private Long playerId;
}
