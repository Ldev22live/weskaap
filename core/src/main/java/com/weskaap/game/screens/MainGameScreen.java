package com.weskaap.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.weskaap.game.world.GameWorld;

public class MainGameScreen implements Screen {
    private final GameWorld world;
    private final OrthographicCamera camera;
    private final OrthographicCamera uiCamera;
    private final SpriteBatch spriteBatch;
    private final BitmapFont font;

    public MainGameScreen() {
        world = new GameWorld();
        camera = new OrthographicCamera();
        uiCamera = new OrthographicCamera();
        spriteBatch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        world.update(delta);
        updateCamera();

        Gdx.gl.glClearColor(0.05f, 0.05f, 0.08f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        world.setProjectionMatrix(camera.combined);
        world.render();
        renderUi();
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        uiCamera.setToOrtho(false, width, height);
        updateCamera();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        world.dispose();
        spriteBatch.dispose();
        font.dispose();
    }

    private void renderUi() {
        spriteBatch.setProjectionMatrix(uiCamera.combined);
        spriteBatch.begin();
        String interactionMessage = world.getInteractionMessage();
        if (interactionMessage != null) {
            font.draw(spriteBatch, interactionMessage, 20f, uiCamera.viewportHeight - 20f);
        }
        String combatMessage = world.getCombatMessage();
        if (combatMessage != null) {
            font.draw(spriteBatch, combatMessage, 20f, uiCamera.viewportHeight - 40f);
        }
        if (world.hasCurrentInteractable()) {
            font.draw(spriteBatch, "[E] Interact", 20f, 30f);
        }
        spriteBatch.end();
    }

    private void updateCamera() {
        float halfWidth = camera.viewportWidth * camera.zoom / 2f;
        float halfHeight = camera.viewportHeight * camera.zoom / 2f;
        camera.position.set(
            clampCameraAxis(world.getHero().getX(), halfWidth, GameWorld.WIDTH),
            clampCameraAxis(world.getHero().getY(), halfHeight, GameWorld.HEIGHT),
            0f
        );
        camera.update();
    }

    private float clampCameraAxis(float target, float halfViewport, float worldSize) {
        if (halfViewport * 2f >= worldSize) {
            return worldSize / 2f;
        }
        return MathUtils.clamp(target, halfViewport, worldSize - halfViewport);
    }
}
