package com.weskaap.game.loot;

import com.weskaap.game.item.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class LootGenerator {
    private final Random random;

    public LootGenerator() {
        this(new Random());
    }

    public LootGenerator(long seed) {
        this(new Random(seed));
    }

    public LootGenerator(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("Random source must not be null");
        }
        this.random = random;
    }

    public List<Item> generate(LootTable table) {
        if (table == null) {
            return Collections.emptyList();
        }

        List<Item> drops = new ArrayList<>();
        for (LootDrop entry : table.getEntries()) {
            if (drops.size() >= table.getMaxDrops()) {
                break;
            }
            if (random.nextFloat() < entry.getChance()) {
                drops.add(entry.getItem().copy());
            }
        }
        return drops;
    }
}
