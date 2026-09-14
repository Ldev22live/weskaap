package com.weskaap.game.loot;

import com.weskaap.game.item.Item;

public final class LootDrop {
    private final Item item;
    private final float chance;

    public LootDrop(Item item, float chance) {
        if (item == null) {
            throw new IllegalArgumentException("Loot drop item must not be null");
        }
        if (chance <= 0f || chance > 1f) {
            throw new IllegalArgumentException("Loot drop chance must be between 0 and 1");
        }
        this.item = item.copy();
        this.chance = chance;
    }

    public Item getItem() {
        return item.copy();
    }

    public float getChance() {
        return chance;
    }
}
