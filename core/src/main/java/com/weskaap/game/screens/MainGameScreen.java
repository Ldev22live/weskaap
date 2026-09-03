package com.weskaap.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.weskaap.game.world.GameWorld;

public class MainGameScreen implements Screen {
    private final GameWorld world;
    private final OrthographicCamera camera;

    public MainGameScreen() {
        world = new GameWorld();
        camera = new OrthographicCamera();
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(world.getHero().getX(), world.getHero().getY(), 0f);
        camera.update();
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        world.update(delta);
        updateCamera(delta);

        Gdx.gl.glClearColor(0.05f, 0.05f, 0.08f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        world.setProjectionMatrix(camera.combined);
        world.render();
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
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
    }

    private void updateCamera(float delta) {
        float followAmount = Math.min(delta * 8f, 1f);
        camera.position.x += (world.getHero().getX() - camera.position.x) * followAmount;
        camera.position.y += (world.getHero().getY() - camera.position.y) * followAmount;
        camera.update();
    }
}
