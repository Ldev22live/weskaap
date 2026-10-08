package com.weskaap.game.tiled;

import com.badlogic.gdx.math.Vector2;

/**
 * Raw data for a spawn point parsed from a Tiled objectgroup.
 *
 * <p>Spawn points are generic: they may represent player spawns, enemy spawns,
 * or any other respawn/starting location. The type field lets consumers decide
 * how to use the position.
 */
public final class SpawnData {

    private final String id;
    private final String name;
    private final String type;
    private final Vector2 position;

    public SpawnData(String id, String name, String type, Vector2 position) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Spawn id cannot be blank");
        }
        this.id = id;
        this.name = name == null ? "" : name;
        this.type = type == null ? "" : type;
        this.position = position == null ? new Vector2() : new Vector2(position);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Vector2 getPosition() {
        return new Vector2(position);
    }
}
