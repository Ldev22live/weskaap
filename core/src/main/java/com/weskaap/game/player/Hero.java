package com.weskaap.game.player;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.weskaap.game.combat.Combatant;
import com.weskaap.game.equipment.Equipment;
import com.weskaap.game.inventory.Inventory;

public class Hero implements Combatant {
    public static final float SIZE = 32f;
    public static final float COLLISION_WIDTH = 28f;
    public static final float COLLISION_HEIGHT = 28f;
    public static final int BASE_MAXIMUM_HEALTH = 100;
    public static final int BASE_ATTACK = 0;
    public static final int BASE_ARMOUR = 0;

    private final Vector2 position;
    private final Vector2 facingDirection;
    private final Rectangle collisionBounds;
    private final float baseMovementSpeed;
    private final Inventory inventory;
    private final Equipment equipment;
    private int health;

    public Hero(float x, float y, float movementSpeed) {
        position = new Vector2(x, y);
        facingDirection = new Vector2(0f, -1f);
        collisionBounds = new Rectangle();
        baseMovementSpeed = movementSpeed;
        inventory = new Inventory();
        equipment = new Equipment();
        health = BASE_MAXIMUM_HEALTH;
        updateCollisionBounds();
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }

    public float getBaseMovementSpeed() {
        return baseMovementSpeed;
    }

    public float getMovementSpeed() {
        return baseMovementSpeed + equipment.getTotalStats().getMovementModifier();
    }

    public int getBaseAttack() {
        return BASE_ATTACK;
    }

    public int getTotalAttack() {
        return BASE_ATTACK + equipment.getTotalStats().getDamage() + equipment.getTotalStats().getAttackBonus();
    }

    public int getBaseArmour() {
        return BASE_ARMOUR;
    }

    public int getTotalArmour() {
        return BASE_ARMOUR + equipment.getTotalStats().getArmour();
    }

    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public int getMaximumHealth() {
        return BASE_MAXIMUM_HEALTH + equipment.getTotalStats().getHealthBonus();
    }

    public boolean isAtFullHealth() {
        return health >= getMaximumHealth();
    }

    public void heal(int amount) {
        if (amount > 0 && isAlive()) {
            health = Math.min(getMaximumHealth(), health + amount);
        }
    }

    @Override
    public void takeDamage(int amount) {
        if (amount > 0 && isAlive()) {
            health = Math.max(0, health - amount);
        }
    }

    public Vector2 getFacingDirection() {
        return facingDirection;
    }

    public void setFacingDirection(float x, float y) {
        if (Math.abs(x) >= Math.abs(y)) {
            facingDirection.set(Math.signum(x), 0f);
        } else {
            facingDirection.set(0f, Math.signum(y));
        }
    }

    @Override
    public Rectangle getCombatBounds() {
        return collisionBounds;
    }

    public Rectangle getCollisionBounds() {
        return collisionBounds;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void moveBy(float xAmount, float yAmount) {
        position.add(xAmount, yAmount);
        updateCollisionBounds();
    }

    public void setPosition(float x, float y) {
        position.set(x, y);
        updateCollisionBounds();
    }

    private void updateCollisionBounds() {
        collisionBounds.set(
            position.x - COLLISION_WIDTH / 2f,
            position.y - COLLISION_HEIGHT / 2f,
            COLLISION_WIDTH,
            COLLISION_HEIGHT
        );
    }
}
