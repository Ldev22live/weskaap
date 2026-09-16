package com.weskaap.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.weskaap.game.dialogue.DialogueUi;
import com.weskaap.game.interaction.Interactable;
import com.weskaap.game.ui.BitmapFontWrapper;
import com.weskaap.game.ui.DialogueStage;
import com.weskaap.game.ui.GameHud;
import com.weskaap.game.world.GameWorld;
import com.weskaap.game.world3d.IsometricCamera;
import com.weskaap.game.world3d.WorldCoordinateConverter;

public class MainGameScreen implements Screen {
    private final GameWorld world;
    private final IsometricCamera worldCamera;
    private final OrthographicCamera uiCamera;
    private final SpriteBatch spriteBatch;
    private final BitmapFont font;
    private final BitmapFontWrapper fontWrapper;
    private final ShapeRenderer shapeRenderer;
    private final GameHud gameHud;
    private final DialogueStage dialogueStage;

    public MainGameScreen() {
        world = new GameWorld();
        worldCamera = new IsometricCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),
            GameWorld.WIDTH, GameWorld.HEIGHT);
        uiCamera = new OrthographicCamera();
        spriteBatch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        fontWrapper = new BitmapFontWrapper(font);
        shapeRenderer = new ShapeRenderer();
        gameHud = new GameHud(world, new DialogueUi(world.getDialogueController()),
            Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        dialogueStage = new DialogueStage(world.getDialogueController(), font);
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(dialogueStage);
    }

    @Override
    public void render(float delta) {
        world.update(delta);
        updateCamera();
        updateInteractionPromptPosition();

        Gdx.gl.glClearColor(0.18f, 0.22f, 0.30f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDepthFunc(GL20.GL_LEQUAL);
        Gdx.gl.glDisable(GL20.GL_CULL_FACE);
        world.render3D(worldCamera);

        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDisable(GL20.GL_CULL_FACE);
        renderUi();

        dialogueStage.act(delta);
        dialogueStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        worldCamera.resize(width, height);
        uiCamera.setToOrtho(false, width, height);
        gameHud.resize(world, width, height);
        dialogueStage.getViewport().update(width, height, true);
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
        shapeRenderer.dispose();
        dialogueStage.dispose();
    }

    private void renderUi() {
        spriteBatch.setProjectionMatrix(uiCamera.combined);
        shapeRenderer.setProjectionMatrix(uiCamera.combined);
        spriteBatch.begin();
        gameHud.render(shapeRenderer, spriteBatch, fontWrapper, world,
            uiCamera.viewportWidth, uiCamera.viewportHeight);
        spriteBatch.end();
    }

    private void updateCamera() {
        worldCamera.update(world.getHero());
    }

    private void updateInteractionPromptPosition() {
        if (world.hasCurrentInteractable()) {
            Interactable target = world.getCurrentInteractable();
            Vector3 pos = WorldCoordinateConverter.to3D(
                target.getInteractionPosition().x,
                target.getInteractionPosition().y,
                80f);
            worldCamera.getCamera().project(pos);
            gameHud.setInteractionPromptPosition(pos.x, pos.y);
        }
    }
}
