package com.weskaap.game.combat;

import com.badlogic.gdx.math.Rectangle;

public interface Combatant {
    int getHealth();

    int getMaximumHealth();

    Rectangle getCombatBounds();

    void takeDamage(int amount);

    default boolean isAlive() {
        return getHealth() > 0;
    }
}
