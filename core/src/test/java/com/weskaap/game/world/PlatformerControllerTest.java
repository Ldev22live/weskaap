package com.weskaap.game.world;

import com.badlogic.gdx.math.Vector2;
import com.weskaap.game.building.Interior;
import com.weskaap.game.player.Hero;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlatformerControllerTest {
    private final PlatformerController controller = new PlatformerController();
    private final Interior interior = new Interior("test", 400f, 300f, 50f, 80f, 350f, 80f,
        new Vector2(0f, 0f));
    private final Hero hero = new Hero(50f, 80f, 240f);

    @Test
    void resetPlacesHeroOnFloor() {
        controller.reset(hero, interior);

        assertEquals(20f, hero.getPlatformerHeight());
        assertTrue(hero.isGrounded());
        assertEquals(0f, hero.getPlatformerVelocityY());
    }

    @Test
    void heroJumpsAndFallsBackToFloor() {
        controller.reset(hero, interior);
        controller.update(hero, interior, 0f, true, 0.016f);

        assertFalse(hero.isGrounded());
        assertTrue(hero.getPlatformerHeight() > 20f);

        for (int i = 0; i < 120; i++) {
            controller.update(hero, interior, 0f, false, 0.016f);
        }

        assertEquals(20f, hero.getPlatformerHeight(), 0.001f);
        assertTrue(hero.isGrounded());
    }

    @Test
    void heroLandsOnElevatedPlatform() {
        hero.setPosition(100f, 80f);
        hero.setPlatformerHeight(130f);
        hero.setPlatformerVelocityY(-100f);

        for (int i = 0; i < 60 && !hero.isGrounded(); i++) {
            controller.update(hero, interior, 0f, false, 0.016f);
        }

        assertEquals(84f, hero.getPlatformerHeight(), 0.001f);
        assertTrue(hero.isGrounded());
    }

    @Test
    void horizontalMovementStaysWithinInterior() {
        controller.reset(hero, interior);

        for (int i = 0; i < 300; i++) {
            controller.update(hero, interior, -1f, false, 0.016f);
        }
        assertEquals(Hero.COLLISION_WIDTH / 2f, hero.getX(), 0.001f);

        for (int i = 0; i < 300; i++) {
            controller.update(hero, interior, 1f, false, 0.016f);
        }
        assertEquals(interior.getWidth() - Hero.COLLISION_WIDTH / 2f, hero.getX(), 0.001f);
    }
}
