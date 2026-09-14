package com.weskaap.game.consumable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.weskaap.game.inventory.Inventory;
import com.weskaap.game.item.Item;
import com.weskaap.game.player.Hero;

import java.util.HashMap;
import java.util.Map;

public class ConsumableController {
    public static final String HEALING_POTION_ID = "healing-potion";
    public static final int HEALING_POTION_AMOUNT = 25;

    private final Map<String, ConsumableEffect> effects = new HashMap<>();

    public ConsumableController() {
        effects.put(HEALING_POTION_ID, new HealingEffect(HEALING_POTION_AMOUNT));
    }

    public void register(String itemId, ConsumableEffect effect) {
        if (itemId == null || itemId.isBlank() || effect == null) {
            return;
        }
        effects.put(itemId, effect);
    }

    public ItemUseResult update(Hero hero, Inventory inventory) {
        if (!Gdx.input.isKeyJustPressed(Input.Keys.H)) {
            return null;
        }
        return use(HEALING_POTION_ID, hero, inventory);
    }

    public ItemUseResult use(String itemId, Hero hero, Inventory inventory) {
        if (itemId == null || itemId.isBlank()) {
            return ItemUseResult.INVALID_ITEM;
        }
        if (hero == null || !hero.isAlive()) {
            return ItemUseResult.HERO_DEAD;
        }
        ConsumableEffect effect = effects.get(itemId);
        if (effect == null) {
            return ItemUseResult.NOT_CONSUMABLE;
        }
        if (!inventory.has(itemId)) {
            return ItemUseResult.ITEM_NOT_FOUND;
        }
        if (!effect.canApply(hero)) {
            return ItemUseResult.ALREADY_FULL_HEALTH;
        }

        Item item = inventory.get(itemId);
        if (!inventory.remove(itemId, 1)) {
            return ItemUseResult.ITEM_NOT_FOUND;
        }
        if (!effect.apply(hero)) {
            inventory.add(item.copy());
            return ItemUseResult.CANNOT_APPLY;
        }
        return ItemUseResult.SUCCESS;
    }
}
