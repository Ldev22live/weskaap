package com.weskaap.game.loot;

import com.weskaap.game.item.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class LootTable {
    private final List<LootDrop> drops;
    private final int maxDrops;

    public LootTable(int maxDrops) {
        if (maxDrops < 0) {
            throw new IllegalArgumentException("Maximum drops must not be negative");
        }
        this.maxDrops = maxDrops;
        this.drops = new ArrayList<>();
    }

    public void add(Item item, float chance) {
        if (item == null) {
            return;
        }
        drops.add(new LootDrop(item, chance));
    }

    public List<LootDrop> getEntries() {
        return Collections.unmodifiableList(new ArrayList<>(drops));
    }

    public int getMaxDrops() {
        return maxDrops;
    }
}
