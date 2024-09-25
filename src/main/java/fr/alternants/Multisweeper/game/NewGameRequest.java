package fr.alternants.Multisweeper.game;

import fr.alternants.Multisweeper.game.core.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewGameRequest {
    private Integer rows;
    private Integer cols;
    private Difficulty difficulty;
    private Boolean isMultiplayer;
}
