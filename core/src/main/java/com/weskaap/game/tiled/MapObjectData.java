package com.weskaap.game.tiled;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Raw data for a single object parsed from a Tiled objectgroup.
 *
 * <p>Objects are generic: they may represent entrances, landmarks, vehicles,
 * interactables, or any other gameplay entity. The loader preserves the Tiled
 * name, type, geometry, and custom properties; higher-level systems decide how
 * to interpret them.
 */
public final class MapObjectData {

    private final String id;
    private final String name;
    private final String type;
    private final Rectangle bounds;
    private final Vector2 position;
    private final Map<String, String> properties;

    public MapObjectData(String id, String name, String type,
                         Rectangle bounds, Vector2 position,
                         Map<String, String> properties) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Object id cannot be blank");
        }
        this.id = id;
        this.name = name == null ? "" : name;
        this.type = type == null ? "" : type;
        this.bounds = bounds == null ? new Rectangle() : new Rectangle(bounds);
        this.position = position == null ? new Vector2() : new Vector2(position);
        this.properties = properties == null ? Map.of() : Collections.unmodifiableMap(new HashMap<>(properties));
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

    public Rectangle getBounds() {
        return new Rectangle(bounds);
    }

    public Vector2 getPosition() {
        return new Vector2(position);
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public boolean hasProperty(String key) {
        return properties.containsKey(key);
    }

    public String getProperty(String key) {
        return properties.get(key);
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getOrDefault(key, defaultValue);
    }
}
