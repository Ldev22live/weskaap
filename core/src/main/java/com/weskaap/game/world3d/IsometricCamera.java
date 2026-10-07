package com.weskaap.game.world3d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.weskaap.game.player.Hero;
import com.weskaap.game.world.CameraMode;

public class IsometricCamera {

    private static final float ZOOM = 2.2f;
    private static final float PLATFORMER_ZOOM = 0.5f;
    private static final float ISOMETRIC_DISTANCE = 1400f;
    private static final float ISOMETRIC_HEIGHT = 1100f;
    private static final float PLATFORMER_HEIGHT = 150f;
    private static final float PLATFORMER_DISTANCE = 1000f;

    private final OrthographicCamera camera;
    private final Vector3 target = new Vector3();
    private final Vector3 position = new Vector3();
    private final float worldWidth;
    private final float worldDepth;
    private CameraMode lastMode = null;

    public IsometricCamera(float viewportWidth, float viewportHeight, float worldWidth, float worldDepth) {
        camera = new OrthographicCamera(viewportWidth, viewportHeight);
        camera.near = 0.1f;
        camera.far = 8000f;
        this.worldWidth = worldWidth;
        this.worldDepth = worldDepth;
    }

    public void update(Hero hero) {
        update(hero, CameraMode.ISOMETRIC);
    }

    public void update(Hero hero, CameraMode mode) {
        float x = hero.getX();
        float z = hero.getY();

        if (mode == CameraMode.PLATFORMER) {
            camera.zoom = PLATFORMER_ZOOM;
            position.set(x, PLATFORMER_HEIGHT, z + PLATFORMER_DISTANCE);
            target.set(x, PLATFORMER_HEIGHT, z);
        } else {
            camera.zoom = ZOOM;
            position.set(x - ISOMETRIC_DISTANCE, ISOMETRIC_HEIGHT, z - ISOMETRIC_DISTANCE);
            target.set(x, 0f, z);
        }

        camera.position.set(position);
        camera.lookAt(target);
        camera.up.set(0f, 1f, 0f);
        camera.update();

        if (Gdx.app != null && mode != lastMode) {
            Gdx.app.log("CameraDebug", "Camera mode: " + mode);
            lastMode = mode;
        }
    }

    public void resize(float viewportWidth, float viewportHeight) {
        camera.viewportWidth = viewportWidth;
        camera.viewportHeight = viewportHeight;
        camera.update();
    }

    public OrthographicCamera getCamera() {
        return camera;
    }
}
