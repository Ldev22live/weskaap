package com.weskaap.game.interaction;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.weskaap.game.tiled.MapObjectData;

/**
 * An interactable marker for a Tiled entrance object whose interior does not yet
 * exist in the project.
 *
 * <p>The marker preserves the data relationship (name, position, {@code interiorId})
 * so that a later milestone can replace it with a real {@link BuildingEntrance}
 * once the referenced interior is implemented. Interacting reports that the door
 * is locked; no transition occurs and no gameplay content is invented.
 */
public final class TiledEntrance implements Interactable {

    private static final float INTERACTION_RANGE = 80f;

    private final String id;
    private final String name;
    private final String interiorId;
    private final Vector2 position;
    private final Rectangle bounds;

    public TiledEntrance(MapObjectData object) {
        if (object == null) {
            throw new IllegalArgumentException("Object cannot be null");
        }
        this.id = "tiled_entrance_" + object.getId();
        this.name = object.getName();
        this.interiorId = object.getProperty("interiorId", "");
        this.position = object.getPosition();
        this.bounds = object.getBounds();
    }

    @Override
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getInteriorId() {
        return interiorId;
    }

    @Override
    public Vector2 getInteractionPosition() {
        return new Vector2(position);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(bounds);
    }

    @Override
    public float getInteractionRange() {
        return INTERACTION_RANGE;
    }

    @Override
    public String interact() {
        return "The door is locked.";
    }

    @Override
    public String getPromptText() {
        return "Door";
    }
}
