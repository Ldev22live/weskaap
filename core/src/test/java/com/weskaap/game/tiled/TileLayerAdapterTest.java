package com.weskaap.game.tiled;

import com.badlogic.gdx.math.Rectangle;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TileLayerAdapterTest {

    private static final float TILE = 32f;
    private static final float SCALE = 2.0f;

    private final TiledCoordinateConverter converter =
        new TiledCoordinateConverter(1600f, 1600f, SCALE);
    private final TileLayerAdapter adapter = new TileLayerAdapter(converter, TILE, TILE);

    @Test
    void emptyLayerProducesNoRectangles() {
        int[] tiles = new int[100];
        TileLayerData layer = new TileLayerData("Empty", 10, 10, tiles);

        assertTrue(adapter.toWorldRectangles(layer).isEmpty());
    }

    @Test
    void contiguousRunMergesIntoSingleRectangle() {
        int[] tiles = new int[100];
        Arrays.fill(tiles, 0, 5, 2); // row 0, cols 0-4
        TileLayerData layer = new TileLayerData("Test", 10, 10, tiles);

        List<Rectangle> rects = adapter.toWorldRectangles(layer);
        assertEquals(1, rects.size());
        Rectangle r = rects.get(0);
        assertEquals(0f, r.x, 0.001f);
        assertEquals(3136f, r.y, 0.001f);
        assertEquals(320f, r.width, 0.001f);
        assertEquals(64f, r.height, 0.001f);
    }

    @Test
    void separateRunsProduceSeparateRectangles() {
        int[] tiles = new int[100];
        tiles[0] = 2;
        tiles[2] = 2;
        TileLayerData layer = new TileLayerData("Test", 10, 10, tiles);

        List<Rectangle> rects = adapter.toWorldRectangles(layer);
        assertEquals(2, rects.size());
        assertEquals(0f, rects.get(0).x, 0.001f);
        assertEquals(128f, rects.get(1).x, 0.001f);
    }

    @Test
    void rowsAreNotMergedVertically() {
        int[] tiles = new int[100];
        tiles[0] = 2;
        tiles[10] = 2; // same column, next row
        TileLayerData layer = new TileLayerData("Test", 10, 10, tiles);

        List<Rectangle> rects = adapter.toWorldRectangles(layer);
        assertEquals(2, rects.size());
    }

    @Test
    void retreatRoadsStripConvertsToExpectedWorldRectangles() {
        AreaData area = new TiledAreaLoader().load("retreat_location.tmx");
        TiledCoordinateConverter retreatConverter =
            new TiledCoordinateConverter(1600f, 1600f, SCALE);
        TileLayerAdapter retreatAdapter = new TileLayerAdapter(retreatConverter, 32f, 32f);

        List<Rectangle> rects = retreatAdapter.toWorldRectangles(area.getTileLayer("Roads"));

        assertEquals(2, rects.size(), "Roads rows 25 and 26 each merge into one strip");
        assertTrue(rects.stream().anyMatch(r -> r.x == 0f && r.width == 3200f
            && Math.abs(r.y - 1536f) < 0.001f && Math.abs(r.height - 64f) < 0.001f));
        assertTrue(rects.stream().anyMatch(r -> r.x == 0f && r.width == 3200f
            && Math.abs(r.y - 1472f) < 0.001f && Math.abs(r.height - 64f) < 0.001f));
    }
}
