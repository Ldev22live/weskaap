package com.weskaap.game.consumable;

import com.weskaap.game.player.Hero;

public final class HealingEffect implements ConsumableEffect {
    private final int amount;

    public HealingEffect(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Healing amount must be positive");
        }
        this.amount = amount;
    }

    @Override
    public boolean canApply(Hero hero) {
        return hero != null && hero.isAlive() && !hero.isAtFullHealth();
    }

    @Override
    public boolean apply(Hero hero) {
        if (!canApply(hero)) {
            return false;
        }
        hero.heal(amount);
        return true;
    }

    public int getAmount() {
        return amount;
    }
}
