package com.weskaap.game.item;

public final class ItemStats {
    public static final ItemStats ZERO = new ItemStats(0, 0, 0, 0, 0f);

    private final int damage;
    private final int armour;
    private final int healthBonus;
    private final int attackBonus;
    private final float movementModifier;

    public ItemStats() {
        this(0, 0, 0, 0, 0f);
    }

    public ItemStats(int damage, int armour, int healthBonus, int attackBonus, float movementModifier) {
        this.damage = Math.max(0, damage);
        this.armour = Math.max(0, armour);
        this.healthBonus = Math.max(0, healthBonus);
        this.attackBonus = Math.max(0, attackBonus);
        this.movementModifier = movementModifier;
    }

    public int getDamage() {
        return damage;
    }

    public int getArmour() {
        return armour;
    }

    public int getHealthBonus() {
        return healthBonus;
    }

    public int getAttackBonus() {
        return attackBonus;
    }

    public float getMovementModifier() {
        return movementModifier;
    }

    public ItemStats add(ItemStats other) {
        if (other == null) {
            return this;
        }
        return new ItemStats(
            damage + other.damage,
            armour + other.armour,
            healthBonus + other.healthBonus,
            attackBonus + other.attackBonus,
            movementModifier + other.movementModifier
        );
    }
}
