package fr.alternants.Multisweeper.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewGameResponse {
    private String username;
    private Integer roomId;
    private Integer bombNumber;
}
