package com.weskaap.game.world3d;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.weskaap.game.player.Hero;

public class IsometricCamera {

    private static final float ZOOM = 2.2f;

    private final OrthographicCamera camera;
    private final Vector3 target = new Vector3();
    private final Vector3 position = new Vector3();
    private final float worldWidth;
    private final float worldDepth;

    public IsometricCamera(float viewportWidth, float viewportHeight, float worldWidth, float worldDepth) {
        camera = new OrthographicCamera(viewportWidth * ZOOM, viewportHeight * ZOOM);
        camera.near = 0.1f;
        camera.far = 8000f;
        this.worldWidth = worldWidth;
        this.worldDepth = worldDepth;
    }

    public void update(Hero hero) {
        float x = hero.getX();
        float z = hero.getY();
        target.set(x, 0f, z);

        float distance = 1400f;
        float height = 1100f;
        position.set(x + distance, height, z + distance);

        camera.position.set(position);
        camera.lookAt(target);
        camera.up.set(0f, 1f, 0f);

        float halfWidth = camera.viewportWidth * 0.5f;
        float halfDepth = camera.viewportHeight * 0.5f;
        float minX = Math.min(worldWidth - halfWidth, Math.max(halfWidth, position.x));
        float minZ = Math.min(worldDepth - halfDepth, Math.max(halfDepth, position.z));
        camera.position.x = Math.max(0f, Math.min(worldWidth, minX));
        camera.position.z = Math.max(0f, Math.min(worldDepth, minZ));

        camera.update();
    }

    public void resize(float viewportWidth, float viewportHeight) {
        camera.viewportWidth = viewportWidth * ZOOM;
        camera.viewportHeight = viewportHeight * ZOOM;
        camera.update();
    }

    public OrthographicCamera getCamera() {
        return camera;
    }
}
