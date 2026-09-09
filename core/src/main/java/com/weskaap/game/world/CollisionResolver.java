package com.weskaap.game.world;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.weskaap.game.player.Hero;

import java.util.List;

public final class CollisionResolver {
    private CollisionResolver() {
    }

    public static void move(Hero hero, float xAmount, float yAmount, List<Rectangle> obstacles,
                            float worldWidth, float worldHeight) {
        moveX(hero, xAmount, obstacles, worldWidth);
        moveY(hero, yAmount, obstacles, worldHeight);
    }

    private static void moveX(Hero hero, float amount, List<Rectangle> obstacles, float worldWidth) {
        Rectangle bounds = hero.getCollisionBounds();
        float halfWidth = bounds.width / 2f;
        float targetX = MathUtils.clamp(hero.getX() + amount, halfWidth, worldWidth - halfWidth);

        for (Rectangle obstacle : obstacles) {
            if (!rangesOverlap(bounds.y, bounds.y + bounds.height, obstacle.y, obstacle.y + obstacle.height)) {
                continue;
            }
            if (amount > 0f && bounds.x + bounds.width <= obstacle.x && targetX + halfWidth > obstacle.x) {
                targetX = Math.min(targetX, obstacle.x - halfWidth);
            } else if (amount < 0f && bounds.x >= obstacle.x + obstacle.width
                && targetX - halfWidth < obstacle.x + obstacle.width) {
                targetX = Math.max(targetX, obstacle.x + obstacle.width + halfWidth);
            }
        }

        hero.setPosition(targetX, hero.getY());
    }

    private static void moveY(Hero hero, float amount, List<Rectangle> obstacles, float worldHeight) {
        Rectangle bounds = hero.getCollisionBounds();
        float halfHeight = bounds.height / 2f;
        float targetY = MathUtils.clamp(hero.getY() + amount, halfHeight, worldHeight - halfHeight);

        for (Rectangle obstacle : obstacles) {
            if (!rangesOverlap(bounds.x, bounds.x + bounds.width, obstacle.x, obstacle.x + obstacle.width)) {
                continue;
            }
            if (amount > 0f && bounds.y + bounds.height <= obstacle.y && targetY + halfHeight > obstacle.y) {
                targetY = Math.min(targetY, obstacle.y - halfHeight);
            } else if (amount < 0f && bounds.y >= obstacle.y + obstacle.height
                && targetY - halfHeight < obstacle.y + obstacle.height) {
                targetY = Math.max(targetY, obstacle.y + obstacle.height + halfHeight);
            }
        }

        hero.setPosition(hero.getX(), targetY);
    }

    private static boolean rangesOverlap(float firstMin, float firstMax, float secondMin, float secondMax) {
        return firstMin < secondMax && firstMax > secondMin;
    }
}
