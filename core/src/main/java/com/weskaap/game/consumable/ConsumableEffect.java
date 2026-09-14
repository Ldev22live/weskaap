package com.weskaap.game.consumable;

import com.weskaap.game.player.Hero;

public interface ConsumableEffect {
    boolean canApply(Hero hero);

    boolean apply(Hero hero);
}
