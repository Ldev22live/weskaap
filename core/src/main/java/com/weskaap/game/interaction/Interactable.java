package com.weskaap.game.interaction;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.weskaap.game.player.Hero;

public interface Interactable {
    Vector2 getInteractionPosition();

    Rectangle getBounds();

    float getInteractionRange();

    String interact();

    default String getPromptText() {
        return "";
    }

    default String getId() {
        return "";
    }

    default boolean canInteract(Hero hero) {
        return getInteractionPosition().dst2(hero.getPosition())
            <= getInteractionRange() * getInteractionRange();
    }
}
