package com.weskaap.game.world;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.weskaap.game.player.Hero;

import java.util.List;

public final class CollisionResolver {
    private CollisionResolver() {
    }

    public static void move(com.badlogic.gdx.math.Rectangle bounds, float xAmount, float yAmount,
                            List<Rectangle> obstacles, float worldWidth, float worldHeight) {
        moveX(bounds, xAmount, obstacles, worldWidth);
        moveY(bounds, yAmount, obstacles, worldHeight);
    }

    public static void move(Hero hero, float xAmount, float yAmount, List<Rectangle> obstacles,
                            float worldWidth, float worldHeight) {
        moveX(hero, xAmount, obstacles, worldWidth);
        moveY(hero, yAmount, obstacles, worldHeight);
    }

    private static void moveX(Hero hero, float amount, List<Rectangle> obstacles, float worldWidth) {
        Rectangle bounds = hero.getCollisionBounds();
        moveX(bounds, amount, obstacles, worldWidth);
        hero.setPosition(bounds.x + bounds.width / 2f, bounds.y + bounds.height / 2f);
    }

    private static void moveX(Rectangle bounds, float amount, List<Rectangle> obstacles, float worldWidth) {
        float halfWidth = bounds.width / 2f;
        float centerX = bounds.x + halfWidth;
        float targetX = MathUtils.clamp(centerX + amount, halfWidth, worldWidth - halfWidth);

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

        bounds.x = targetX - halfWidth;
    }

    private static void moveY(Hero hero, float amount, List<Rectangle> obstacles, float worldHeight) {
        Rectangle bounds = hero.getCollisionBounds();
        moveY(bounds, amount, obstacles, worldHeight);
        hero.setPosition(bounds.x + bounds.width / 2f, bounds.y + bounds.height / 2f);
    }

    private static void moveY(Rectangle bounds, float amount, List<Rectangle> obstacles, float worldHeight) {
        float halfHeight = bounds.height / 2f;
        float centerY = bounds.y + halfHeight;
        float targetY = MathUtils.clamp(centerY + amount, halfHeight, worldHeight - halfHeight);

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

        bounds.y = targetY - halfHeight;
    }

    private static boolean rangesOverlap(float firstMin, float firstMax, float secondMin, float secondMax) {
        return firstMin < secondMax && firstMax > secondMin;
    }
}
