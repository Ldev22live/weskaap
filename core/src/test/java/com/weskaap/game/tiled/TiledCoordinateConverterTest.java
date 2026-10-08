package com.weskaap.game.tiled;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TiledCoordinateConverterTest {

    private static final float MAP_PIXEL_WIDTH = 1600f;
    private static final float MAP_PIXEL_HEIGHT = 1600f;
    private static final float SCALE = 2.0f;

    private final TiledCoordinateConverter converter =
        new TiledCoordinateConverter(MAP_PIXEL_WIDTH, MAP_PIXEL_HEIGHT, SCALE);

    @Test
    void constructorRejectsInvalidDimensions() {
        assertThrows(IllegalArgumentException.class,
            () -> new TiledCoordinateConverter(0f, 1600f, SCALE));
        assertThrows(IllegalArgumentException.class,
            () -> new TiledCoordinateConverter(1600f, -1f, SCALE));
        assertThrows(IllegalArgumentException.class,
            () -> new TiledCoordinateConverter(1600f, 1600f, 0f));
    }

    @Test
    void worldDimensionsMatchScale() {
        assertEquals(3200f, converter.getWorldWidth(), 0.001f);
        assertEquals(3200f, converter.getWorldHeight(), 0.001f);
    }

    @Test
    void toWorldXScalesHorizontally() {
        assertEquals(0f, converter.toWorldX(0f), 0.001f);
        assertEquals(1600f, converter.toWorldX(800f), 0.001f);
        assertEquals(3200f, converter.toWorldX(1600f), 0.001f);
    }

    @Test
    void toWorldYFlipsVerticalAxis() {
        assertEquals(3200f, converter.toWorldY(0f), 0.001f);
        assertEquals(1600f, converter.toWorldY(800f), 0.001f);
        assertEquals(0f, converter.toWorldY(1600f), 0.001f);
    }

    @Test
    void toWorldRectangleUsesBottomLeftOrigin() {
        Rectangle rect = converter.toWorldRectangle(800f, 864f, 32f, 32f);
        assertEquals(1600f, rect.x, 0.001f);
        assertEquals(1408f, rect.y, 0.001f);
        assertEquals(64f, rect.width, 0.001f);
        assertEquals(64f, rect.height, 0.001f);
    }

    @Test
    void toWorldPositionReturnsCentre() {
        Vector2 centre = converter.toWorldPosition(800f, 864f, 32f, 32f);
        assertEquals(1632f, centre.x, 0.001f);
        assertEquals(1440f, centre.y, 0.001f);
    }
}
