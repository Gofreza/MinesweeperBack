package fr.alternants.Multisweeper.game.core;

import lombok.Getter;

@Getter
public enum Difficulty {
    EASY(0.1f), MEDIUM(0.15f), HARD(0.2f), VERYHARD(1f);

    private final float value;

    Difficulty(float v) {
        this.value = v;
    }
}
