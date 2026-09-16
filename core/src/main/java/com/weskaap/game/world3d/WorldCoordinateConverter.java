package com.weskaap.game.world3d;

import com.badlogic.gdx.math.Vector3;

public final class WorldCoordinateConverter {

    private WorldCoordinateConverter() {
    }

    public static Vector3 to3D(float worldX, float worldY) {
        return new Vector3(worldX, 0f, worldY);
    }

    public static Vector3 to3D(float worldX, float worldY, float height) {
        return new Vector3(worldX, height, worldY);
    }
}
