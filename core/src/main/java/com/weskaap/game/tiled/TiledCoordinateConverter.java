package com.weskaap.game.tiled;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Converts Tiled map coordinates into WesKaap game-world coordinates.
 *
 * <p>Tiled conventions:
 * <ul>
 *   <li>Origin is the top-left corner of the map.</li>
 *   <li>X increases to the right.</li>
 *   <li>Y increases downward.</li>
 *   <li>Object bounds are expressed in pixels relative to that origin.</li>
 * </ul>
 *
 * <p>WesKaap conventions:
 * <ul>
 *   <li>Origin is the bottom-left corner of the world.</li>
 *   <li>X increases to the right.</li>
 *   <li>Y increases upward.</li>
 *   <li>World units are larger than pixels.</li>
 * </ul>
 *
 * <p>The converter is configured with the Tiled map size in pixels and a uniform
 * scale factor (world units per pixel). For M12 a scale of {@code 2.0f} is used:
 * the 50x50, 32px tile Retreat TMX (1600x1600 pixels) maps to 3200x3200 world units.
 * This exceeds {@code GameWorld.HEIGHT} (2400); that aspect-ratio mismatch is a
 * known M12 limitation documented in {@code docs/m12-tiled-data-foundation.md}.
 */
public final class TiledCoordinateConverter {

    private final float mapPixelWidth;
    private final float mapPixelHeight;
    private final float scale;

    public TiledCoordinateConverter(float mapPixelWidth, float mapPixelHeight, float scale) {
        if (mapPixelWidth <= 0f || mapPixelHeight <= 0f) {
            throw new IllegalArgumentException("Map pixel dimensions must be positive");
        }
        if (scale <= 0f) {
            throw new IllegalArgumentException("Scale must be positive");
        }
        this.mapPixelWidth = mapPixelWidth;
        this.mapPixelHeight = mapPixelHeight;
        this.scale = scale;
    }

    public float getMapPixelWidth() {
        return mapPixelWidth;
    }

    public float getMapPixelHeight() {
        return mapPixelHeight;
    }

    public float getScale() {
        return scale;
    }

    public float getWorldWidth() {
        return mapPixelWidth * scale;
    }

    public float getWorldHeight() {
        return mapPixelHeight * scale;
    }

    /** Converts a Tiled X pixel coordinate to a world X coordinate. */
    public float toWorldX(float tiledX) {
        return tiledX * scale;
    }

    /** Converts a Tiled pixel height/length along X to world units. */
    public float toWorldWidth(float tiledWidth) {
        return tiledWidth * scale;
    }

    /**
     * Converts a Tiled Y pixel coordinate to a world Y coordinate.
     *
     * <p>Tiled Y is measured from the top of the map and increases downward.
     * World Y is measured from the bottom of the world and increases upward.
     */
    public float toWorldY(float tiledY) {
        return (mapPixelHeight - tiledY) * scale;
    }

    /** Converts a Tiled pixel height/length along Y to world units. */
    public float toWorldHeight(float tiledHeight) {
        return tiledHeight * scale;
    }

    /**
     * Converts a Tiled rectangle to a world rectangle.
     *
     * <p>The returned {@link Rectangle} uses the bottom-left origin convention:
     * {@code x} and {@code y} are the bottom-left corner, and {@code width} and
     * {@code height} extend right and up.
     */
    public Rectangle toWorldRectangle(float tiledX, float tiledY, float tiledWidth, float tiledHeight) {
        float worldX = toWorldX(tiledX);
        float worldY = toWorldY(tiledY + tiledHeight);
        float worldWidth = toWorldWidth(tiledWidth);
        float worldHeight = toWorldHeight(tiledHeight);
        return new Rectangle(worldX, worldY, worldWidth, worldHeight);
    }

    /** Converts the centre of a Tiled rectangle to a world position. */
    public Vector2 toWorldPosition(float tiledX, float tiledY, float tiledWidth, float tiledHeight) {
        Rectangle worldRect = toWorldRectangle(tiledX, tiledY, tiledWidth, tiledHeight);
        return new Vector2(worldRect.x + worldRect.width / 2f, worldRect.y + worldRect.height / 2f);
    }
}
