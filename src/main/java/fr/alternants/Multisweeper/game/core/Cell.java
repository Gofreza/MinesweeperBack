package fr.alternants.Multisweeper.game.core;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class Cell { // Default value : 0, null, false

    private boolean isBomb;
    private int bombAround;
    private boolean isFlagged;
    private boolean isVisible;
    private boolean isExploded;

    @Override
    public String toString() {
        return "Cell{"+(isVisible?(isBomb?"B":bombAround):"■")+"}";
    }

}
