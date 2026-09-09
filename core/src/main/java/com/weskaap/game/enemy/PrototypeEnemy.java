package com.weskaap.game.enemy;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.weskaap.game.combat.Combatant;

public class PrototypeEnemy implements Combatant {
    public static final float SIZE = 32f;

    private final Vector2 position;
    private final Rectangle bounds;
    private final int maximumHealth;
    private int health;

    public PrototypeEnemy(float x, float y, int maximumHealth, float speed) {
        position = new Vector2(x, y);
        bounds = new Rectangle(x - SIZE / 2f, y - SIZE / 2f, SIZE, SIZE);
        this.maximumHealth = maximumHealth;
        health = maximumHealth;
    }

    public Vector2 getPosition() {
        return position;
    }

    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public int getMaximumHealth() {
        return maximumHealth;
    }

    @Override
    public Rectangle getCombatBounds() {
        return bounds;
    }

    @Override
    public void takeDamage(int amount) {
        if (amount > 0 && isAlive()) {
            health = Math.max(0, health - amount);
        }
    }

    public void setPosition(float x, float y) {
        position.set(x, y);
        bounds.set(x - SIZE / 2f, y - SIZE / 2f, SIZE, SIZE);
    }
}
