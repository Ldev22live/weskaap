package com.weskaap.game.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.weskaap.game.player.Hero;
import com.weskaap.game.player.HeroController;

public class GameWorld {
	public static final float WIDTH = 3200f;
	public static final float HEIGHT = 2400f;

	private static final float GRID_SIZE = 128f;

	private final ShapeRenderer shapeRenderer;
	private final Hero hero;
	private final HeroController heroController;

	public GameWorld() {
		shapeRenderer = new ShapeRenderer();
		hero = new Hero(WIDTH / 2f, HEIGHT / 2f, 240f);
		heroController = new HeroController();
	}

	public Hero getHero() {
		return hero;
	}

	public void update(float delta) {
		heroController.update(hero, delta);
		hero.setPosition(
			MathUtils.clamp(hero.getX(), Hero.SIZE / 2f, WIDTH - Hero.SIZE / 2f),
			MathUtils.clamp(hero.getY(), Hero.SIZE / 2f, HEIGHT - Hero.SIZE / 2f)
		);
	}

	public void render() {
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(new Color(0.12f, 0.19f, 0.13f, 1f));
		shapeRenderer.rect(0f, 0f, WIDTH, HEIGHT);
		shapeRenderer.setColor(new Color(0.9f, 0.68f, 0.2f, 1f));
		hero.render(shapeRenderer);
		shapeRenderer.end();

		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		shapeRenderer.setColor(new Color(0.22f, 0.32f, 0.23f, 1f));
		for (float x = 0f; x <= WIDTH; x += GRID_SIZE) {
			shapeRenderer.line(x, 0f, x, HEIGHT);
		}
		for (float y = 0f; y <= HEIGHT; y += GRID_SIZE) {
			shapeRenderer.line(0f, y, WIDTH, y);
		}
		shapeRenderer.end();
	}

	public void setProjectionMatrix(com.badlogic.gdx.math.Matrix4 projectionMatrix) {
		shapeRenderer.setProjectionMatrix(projectionMatrix);
	}

	public void dispose() {
		shapeRenderer.dispose();
	}
}
