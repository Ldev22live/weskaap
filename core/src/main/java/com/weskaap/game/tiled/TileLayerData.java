package com.weskaap.game.tiled;

import java.util.Arrays;

/**
 * Raw data for a single Tiled tile layer parsed from a TMX {@code <layer>}.
 *
 * <p>Tiles are stored row-major in Tiled order: index {@code 0} is the top-left
 * tile, X increases right, Y increases downward. GID {@code 0} means empty.
 */
public final class TileLayerData {

    private final String name;
    private final int widthTiles;
    private final int heightTiles;
    private final int[] tiles;

    public TileLayerData(String name, int widthTiles, int heightTiles, int[] tiles) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Tile layer name cannot be blank");
        }
        if (widthTiles <= 0 || heightTiles <= 0) {
            throw new IllegalArgumentException("Tile layer dimensions must be positive");
        }
        if (tiles == null || tiles.length != widthTiles * heightTiles) {
            throw new IllegalArgumentException(
                "Tile array length " + (tiles == null ? 0 : tiles.length)
                    + " does not match " + widthTiles + "x" + heightTiles);
        }
        this.name = name;
        this.widthTiles = widthTiles;
        this.heightTiles = heightTiles;
        this.tiles = Arrays.copyOf(tiles, tiles.length);
    }

    public String getName() {
        return name;
    }

    public int getWidthTiles() {
        return widthTiles;
    }

    public int getHeightTiles() {
        return heightTiles;
    }

    /** Returns the tile GID at the given Tiled column/row, or {@code 0} if out of bounds. */
    public int getTile(int x, int y) {
        if (x < 0 || x >= widthTiles || y < 0 || y >= heightTiles) {
            return 0;
        }
        return tiles[y * widthTiles + x];
    }

    public boolean isEmpty() {
        for (int tile : tiles) {
            if (tile != 0) {
                return false;
            }
        }
        return true;
    }

    public int countNonZero() {
        int count = 0;
        for (int tile : tiles) {
            if (tile != 0) {
                count++;
            }
        }
        return count;
    }
}
