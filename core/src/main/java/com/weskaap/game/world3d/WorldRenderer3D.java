package com.weskaap.game.world3d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.weskaap.game.enemy.PrototypeEnemy;
import com.weskaap.game.interaction.Interactable;
import com.weskaap.game.interaction.PrototypeNpc;
import com.weskaap.game.item.PrototypeItem;
import com.weskaap.game.player.Hero;
import com.weskaap.game.world.GameWorld;

import java.util.ArrayList;
import java.util.List;

public class WorldRenderer3D implements Disposable {

    private static final Color COLOR_GROUND = new Color(0.22f, 0.32f, 0.24f, 1f);
    private static final Color COLOR_GROUND_ALT = new Color(0.20f, 0.30f, 0.22f, 1f);
    private static final Color COLOR_HERO = new Color(0.95f, 0.72f, 0.25f, 1f);
    private static final Color COLOR_NPC = new Color(0.30f, 0.65f, 0.95f, 1f);
    private static final Color COLOR_ENEMY = new Color(0.90f, 0.28f, 0.22f, 1f);
    private static final Color COLOR_ITEM = new Color(0.30f, 0.92f, 0.50f, 1f);
    private static final Color COLOR_BUILDING = new Color(0.45f, 0.40f, 0.35f, 1f);
    private static final Color COLOR_ATTACK = new Color(1f, 0.85f, 0.30f, 1f);

    private static final float TILE_SIZE = 160f;
    private static final float TILE_HEIGHT = 4f;
    private static final float GROUND_Y = TILE_HEIGHT / 2f;

    private final ModelBatch modelBatch;
    private final Environment environment;
    private final ModelBuilder modelBuilder;

    private final Model groundModel;
    private final Model heroModel;
    private final Model npcModel;
    private final Model enemyModel;
    private final Model itemModel;
    private final Model buildingModel;
    private final Model attackModel;

    private final ModelInstance groundInstance;
    private final ModelInstance heroInstance;
    private final List<ModelInstance> obstacleInstances;
    private final List<ModelInstance> interactableInstances;
    private final List<ModelInstance> enemyInstances;
    private final List<ModelInstance> itemInstances;
    private final ModelInstance attackInstance;

    private final Vector3 work = new Vector3();

    public WorldRenderer3D(GameWorld world) {
        modelBatch = new ModelBatch();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.75f, 0.75f, 0.80f, 1f));
        environment.add(new DirectionalLight().set(0.95f, 0.90f, 0.80f, -0.8f, -1.2f, -0.8f));

        modelBuilder = new ModelBuilder();

        groundModel = createGroundModel();
        heroModel = createBox(Hero.SIZE, Hero.SIZE, Hero.SIZE, COLOR_HERO);
        npcModel = createBox(PrototypeNpc.SIZE, PrototypeNpc.SIZE, PrototypeNpc.SIZE, COLOR_NPC);
        enemyModel = createBox(PrototypeEnemy.SIZE, PrototypeEnemy.SIZE, PrototypeEnemy.SIZE, COLOR_ENEMY);
        itemModel = createBox(PrototypeItem.SIZE, PrototypeItem.SIZE, PrototypeItem.SIZE, COLOR_ITEM);
        buildingModel = createBox(1f, 1f, 1f, COLOR_BUILDING);
        attackModel = createBox(1f, 12f, 1f, COLOR_ATTACK);

        groundInstance = new ModelInstance(groundModel);

        heroInstance = new ModelInstance(heroModel);
        obstacleInstances = new ArrayList<>();
        interactableInstances = new ArrayList<>();
        enemyInstances = new ArrayList<>();
        itemInstances = new ArrayList<>();
        attackInstance = new ModelInstance(attackModel);

        createObstacleInstances(world.getObstacles());
        createInteractableInstances(world);
        createEnemyInstances(world);
    }

    public void render(IsometricCamera camera, GameWorld world) {
        updateHero(world.getHero());
        updateEnemies(world);
        updateItems(world);
        updateAttack(world);

        modelBatch.begin(camera.getCamera());
        modelBatch.render(groundInstance, environment);
        for (ModelInstance obstacle : obstacleInstances) {
            modelBatch.render(obstacle, environment);
        }
        for (ModelInstance interactable : interactableInstances) {
            modelBatch.render(interactable, environment);
        }
        for (ModelInstance item : itemInstances) {
            modelBatch.render(item, environment);
        }
        for (ModelInstance enemy : enemyInstances) {
            modelBatch.render(enemy, environment);
        }
        if (world.getCombatController().isAttackVisible()) {
            modelBatch.render(attackInstance, environment);
        }
        modelBatch.render(heroInstance, environment);
        modelBatch.end();
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        groundModel.dispose();
        heroModel.dispose();
        npcModel.dispose();
        enemyModel.dispose();
        itemModel.dispose();
        buildingModel.dispose();
        attackModel.dispose();
    }

    private void updateHero(Hero hero) {
        heroInstance.transform.setToTranslation(work.set(hero.getX(), GROUND_Y + Hero.SIZE / 2f, hero.getY()));
    }

    private void updateEnemies(GameWorld world) {
        List<PrototypeEnemy> enemies = world.getEnemies();
        for (int i = 0; i < enemies.size(); i++) {
            PrototypeEnemy enemy = enemies.get(i);
            ModelInstance instance = enemyInstances.get(i);
            instance.transform.setToTranslation(work.set(enemy.getPosition().x,
                GROUND_Y + PrototypeEnemy.SIZE / 2f, enemy.getPosition().y));
        }
    }

    private void updateItems(GameWorld world) {
        List<PrototypeItem> items = world.getWorldItems();
        itemInstances.clear();
        for (PrototypeItem item : items) {
            ModelInstance instance = new ModelInstance(itemModel);
            instance.transform.setToTranslation(work.set(item.getPosition().x,
                GROUND_Y + PrototypeItem.SIZE / 2f, item.getPosition().y));
            itemInstances.add(instance);
        }
    }

    private void updateAttack(GameWorld world) {
        if (!world.getCombatController().isAttackVisible()) {
            return;
        }
        Rectangle bounds = world.getCombatController().getAttackBounds();
        attackInstance.transform.setToTranslation(work.set(bounds.x + bounds.width / 2f,
            GROUND_Y + 6f, bounds.y + bounds.height / 2f));
        attackInstance.transform.setToScaling(bounds.width, 1f, bounds.height);
    }

    private void createObstacleInstances(List<Rectangle> obstacles) {
        for (Rectangle obstacle : obstacles) {
            ModelInstance instance = new ModelInstance(buildingModel);
            float width = obstacle.width;
            float depth = obstacle.height;
            float height = 80f;
            instance.transform.setToScaling(width, height, depth);
            instance.transform.setTranslation(work.set(obstacle.x + width / 2f,
                GROUND_Y + height / 2f, obstacle.y + depth / 2f));
            obstacleInstances.add(instance);
        }
    }

    private void createInteractableInstances(GameWorld world) {
        for (Interactable interactable : world.getInteractables()) {
            if (interactable instanceof PrototypeItem || interactable instanceof PrototypeEnemy) {
                continue;
            }
            Rectangle bounds = interactable.getBounds();
            ModelInstance instance = new ModelInstance(npcModel);
            float x = bounds.x + bounds.width / 2f;
            float z = bounds.y + bounds.height / 2f;
            instance.transform.setToTranslation(work.set(x,
                GROUND_Y + PrototypeNpc.SIZE / 2f, z));
            interactableInstances.add(instance);
        }
    }

    private void createEnemyInstances(GameWorld world) {
        for (PrototypeEnemy enemy : world.getEnemies()) {
            ModelInstance instance = new ModelInstance(enemyModel);
            instance.transform.setToTranslation(work.set(enemy.getPosition().x,
                GROUND_Y + PrototypeEnemy.SIZE / 2f, enemy.getPosition().y));
            enemyInstances.add(instance);
        }
    }

    private Model createGroundModel() {
        int tilesX = (int) Math.ceil(GameWorld.WIDTH / TILE_SIZE);
        int tilesZ = (int) Math.ceil(GameWorld.HEIGHT / TILE_SIZE);

        modelBuilder.begin();
        Material material = new Material(ColorAttribute.createDiffuse(Color.WHITE));
        MeshPartBuilder partBuilder = modelBuilder.part("ground", GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal,
            material);

        Matrix4 tileTransform = new Matrix4();
        for (int i = 0; i < tilesX; i++) {
            for (int j = 0; j < tilesZ; j++) {
                float x = i * TILE_SIZE + TILE_SIZE / 2f;
                float z = j * TILE_SIZE + TILE_SIZE / 2f;
                tileTransform.idt();
                tileTransform.translate(x, GROUND_Y, z);
                tileTransform.scl(TILE_SIZE, TILE_HEIGHT, TILE_SIZE);
                partBuilder.setVertexTransform(tileTransform);
                boolean alt = (i + j) % 2 == 1;
                partBuilder.setColor(alt ? COLOR_GROUND_ALT : COLOR_GROUND);
                partBuilder.box(1f, 1f, 1f);
            }
        }
        return modelBuilder.end();
    }

    private Model createBox(float width, float height, float depth, Color color) {
        Material material = new Material(ColorAttribute.createDiffuse(color));
        return modelBuilder.createBox(width, height, depth, material,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
    }
}
