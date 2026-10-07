package com.weskaap.game.world;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.weskaap.game.building.Interior;
import com.weskaap.game.building.InteriorPlatform;
import com.weskaap.game.player.Hero;

public class PlatformerController {
    public static final float GRAVITY = -900f;
    public static final float JUMP_VELOCITY = 430f;

    public void update(Hero hero, Interior interior, float horizontalInput, boolean jumpRequested, float delta) {
        float halfWidth = Hero.COLLISION_WIDTH / 2f;
        float x = MathUtils.clamp(hero.getX() + horizontalInput * hero.getMovementSpeed() * delta,
            halfWidth, interior.getWidth() - halfWidth);
        if (horizontalInput != 0f) {
            hero.setFacingDirection(horizontalInput, 0f);
        }
        hero.setPosition(x, interior.getPlayerSpawn().y);

        if (jumpRequested && hero.isGrounded()) {
            hero.setPlatformerVelocityY(JUMP_VELOCITY);
            hero.setGrounded(false);
        }

        float previousBottom = hero.getPlatformerHeight();
        float velocity = hero.getPlatformerVelocityY() + GRAVITY * delta;
        float nextBottom = previousBottom + velocity * delta;
        boolean landed = false;

        if (velocity <= 0f) {
            for (InteriorPlatform platform : interior.getPlatforms()) {
                Rectangle bounds = platform.getBounds();
                float platformTop = bounds.y + bounds.height;
                boolean overlapsHorizontally = x + halfWidth > bounds.x && x - halfWidth < bounds.x + bounds.width;
                if (overlapsHorizontally && previousBottom >= platformTop && nextBottom <= platformTop) {
                    nextBottom = platformTop;
                    velocity = 0f;
                    landed = true;
                }
            }
        }

        hero.setPlatformerHeight(nextBottom);
        hero.setPlatformerVelocityY(velocity);
        hero.setGrounded(landed);
    }

    public void reset(Hero hero, Interior interior) {
        Rectangle floor = interior.getPlatforms().get(0).getBounds();
        hero.setPlatformerHeight(floor.y + floor.height);
        hero.setPlatformerVelocityY(0f);
        hero.setGrounded(true);
    }
}
