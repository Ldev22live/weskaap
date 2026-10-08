package com.weskaap.game.tiled;

import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts non-zero tiles in a {@link TileLayerData} into world-space rectangles.
 *
 * <p>Horizontally contiguous runs of identical non-zero GIDs on the same row are
 * merged into a single rectangle. This produces flat ground-feature rectangles
 * suitable for the existing placeholder renderer without duplicating geometry.
 */
public final class TileLayerAdapter {

    private final TiledCoordinateConverter converter;
    private final float tileWidthPixels;
    private final float tileHeightPixels;

    public TileLayerAdapter(TiledCoordinateConverter converter, float tileWidthPixels, float tileHeightPixels) {
        if (converter == null) {
            throw new IllegalArgumentException("Converter cannot be null");
        }
        this.converter = converter;
        this.tileWidthPixels = tileWidthPixels;
        this.tileHeightPixels = tileHeightPixels;
    }

    /**
     * Converts a tile layer to merged world rectangles.
     * Each horizontal run of equal non-zero GIDs becomes one rectangle.
     */
    public List<Rectangle> toWorldRectangles(TileLayerData layer) {
        List<Rectangle> result = new ArrayList<>();
        for (int row = 0; row < layer.getHeightTiles(); row++) {
            int runStart = -1;
            int runGid = 0;
            for (int col = 0; col <= layer.getWidthTiles(); col++) {
                int gid = col < layer.getWidthTiles() ? layer.getTile(col, row) : 0;
                if (gid != runGid) {
                    if (runGid != 0) {
                        result.add(toWorldRect(runStart, row, col - runStart));
                    }
                    runStart = col;
                    runGid = gid;
                }
            }
        }
        return result;
    }

    private Rectangle toWorldRect(int startCol, int row, int lengthTiles) {
        float tiledX = startCol * tileWidthPixels;
        float tiledY = row * tileHeightPixels;
        float tiledW = lengthTiles * tileWidthPixels;
        float tiledH = tileHeightPixels;
        return converter.toWorldRectangle(tiledX, tiledY, tiledW, tiledH);
    }
}
