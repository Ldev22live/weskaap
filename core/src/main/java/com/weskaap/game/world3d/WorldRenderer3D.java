package com.weskaap.game.world3d;

import com.badlogic.gdx.Gdx;
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
import com.weskaap.game.building.Building;
import com.weskaap.game.building.BuildingEntrance;
import com.weskaap.game.building.BuildingExit;
import com.weskaap.game.building.Interior;
import com.weskaap.game.building.InteriorPlatform;
import com.weskaap.game.enemy.PrototypeEnemy;
import com.weskaap.game.interaction.Interactable;
import com.weskaap.game.interaction.PrototypeNpc;
import com.weskaap.game.interaction.TravelPoint;
import com.weskaap.game.item.PrototypeItem;
import com.weskaap.game.player.Hero;
import com.weskaap.game.world.AreaId;
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
    private static final Color COLOR_DOOR = new Color(0.95f, 0.55f, 0.15f, 1f);
    private static final Color COLOR_ROOF = new Color(0.50f, 0.30f, 0.25f, 1f);
    private static final Color COLOR_ATTACK = new Color(1f, 0.85f, 0.30f, 1f);
    private static final Color COLOR_INTERIOR_BACKGROUND = new Color(0.34f, 0.35f, 0.32f, 1f);
    private static final Color COLOR_PLATFORM = new Color(0.12f, 0.11f, 0.08f, 1f);
    private static final Color COLOR_ROAD = new Color(0.22f, 0.23f, 0.24f, 1f);
    private static final Color COLOR_STATION = new Color(0.80f, 0.70f, 0.18f, 1f);

    private static final float TILE_SIZE = 160f;
    private static final float TILE_HEIGHT = 4f;
    private static final float GROUND_Y = TILE_HEIGHT / 2f;

    private final ModelBatch modelBatch;
    private final Environment environment;
    private final ModelBuilder modelBuilder;

    private final Model groundModel;
    private final Model interiorFloorModel;
    private final Model heroModel;
    private final Model npcModel;
    private final Model enemyModel;
    private final Model itemModel;
    private final Model buildingModel;
    private final Model doorModel;
    private final Model roofModel;
    private final Model attackModel;
    private final Model interiorBackgroundModel;
    private final Model platformModel;
    private final Model roadModel;
    private final Model stationMarkerModel;

    private final ModelInstance groundInstance;
    private final ModelInstance heroInstance;
    private final List<ModelInstance> obstacleInstances;
    private final List<ModelInstance> buildingWallInstances;
    private final List<ModelInstance> roofInstances;
    private final List<ModelInstance> interactableInstances;
    private final List<ModelInstance> enemyInstances;
    private final List<ModelInstance> itemInstances;
    private final ModelInstance attackInstance;
    private final List<ModelInstance> interiorGroundInstances;
    private final List<ModelInstance> interiorObstacleInstances;
    private final List<ModelInstance> interiorBackgroundInstances;
    private final List<ModelInstance> platformInstances;
    private final List<ModelInstance> roadInstances;

    private final Vector3 work = new Vector3();
    private final Vector3 scale = new Vector3();
    private boolean doorLogged = false;
    private AreaId renderedArea;

    public WorldRenderer3D(GameWorld world) {
        modelBatch = new ModelBatch();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.75f, 0.75f, 0.80f, 1f));
        environment.add(new DirectionalLight().set(0.95f, 0.90f, 0.80f, -0.8f, -1.2f, -0.8f));

        modelBuilder = new ModelBuilder();

        groundModel = createGroundModel();
        interiorFloorModel = createBox(1f, TILE_HEIGHT, 1f, COLOR_GROUND);
        heroModel = createBox(Hero.SIZE, Hero.SIZE, Hero.SIZE, COLOR_HERO);
        npcModel = createBox(PrototypeNpc.SIZE, PrototypeNpc.SIZE, PrototypeNpc.SIZE, COLOR_NPC);
        enemyModel = createBox(PrototypeEnemy.SIZE, PrototypeEnemy.SIZE, PrototypeEnemy.SIZE, COLOR_ENEMY);
        itemModel = createBox(PrototypeItem.SIZE, PrototypeItem.SIZE, PrototypeItem.SIZE, COLOR_ITEM);
        buildingModel = createBox(1f, 1f, 1f, COLOR_BUILDING);
        doorModel = createBox(1f, 1f, 1f, COLOR_DOOR);
        roofModel = createBox(1f, 1f, 1f, COLOR_ROOF);
        attackModel = createBox(1f, 12f, 1f, COLOR_ATTACK);
        interiorBackgroundModel = createBox(1f, 1f, 1f, COLOR_INTERIOR_BACKGROUND);
        platformModel = createBox(1f, 1f, 1f, COLOR_PLATFORM);
        roadModel = createBox(1f, 1f, 1f, COLOR_ROAD);
        stationMarkerModel = createBox(1f, 1f, 1f, COLOR_STATION);

        groundInstance = new ModelInstance(groundModel);

        heroInstance = new ModelInstance(heroModel);
        obstacleInstances = new ArrayList<>();
        buildingWallInstances = new ArrayList<>();
        roofInstances = new ArrayList<>();
        interactableInstances = new ArrayList<>();
        enemyInstances = new ArrayList<>();
        itemInstances = new ArrayList<>();
        attackInstance = new ModelInstance(attackModel);
        interiorGroundInstances = new ArrayList<>();
        interiorObstacleInstances = new ArrayList<>();
        interiorBackgroundInstances = new ArrayList<>();
        platformInstances = new ArrayList<>();
        roadInstances = new ArrayList<>();

        createRoadInstances(world);
        createObstacleInstances(world.getObstacles());
        createBuildingInstances(world);
        createEnemyInstances(world);
        renderedArea = world.getActiveArea();
    }

    public void render(IsometricCamera camera, GameWorld world) {
        refreshExteriorInstances(world);
        updateHero(world);
        updateInteractableInstances(world);
        updateInteriorInstances(world);

        modelBatch.begin(camera.getCamera());
        if (world.isInBuilding()) {
            for (ModelInstance background : interiorBackgroundInstances) {
                modelBatch.render(background, environment);
            }
            for (ModelInstance obstacle : interiorObstacleInstances) {
                modelBatch.render(obstacle, environment);
            }
            for (ModelInstance platform : platformInstances) {
                modelBatch.render(platform, environment);
            }
            for (ModelInstance interactable : interactableInstances) {
                modelBatch.render(interactable, environment);
            }
            if (world.getActiveInterior() != null && world.getActiveInterior().isCombatAllowed()) {
                updateEnemies(world);
                for (ModelInstance enemy : enemyInstances) {
                    modelBatch.render(enemy, environment);
                }
                updateAttack(world);
                if (world.getCombatController().isAttackVisible()) {
                    modelBatch.render(attackInstance, environment);
                }
            }
        } else {
            modelBatch.render(groundInstance, environment);
            for (ModelInstance road : roadInstances) {
                modelBatch.render(road, environment);
            }
            for (ModelInstance obstacle : obstacleInstances) {
                modelBatch.render(obstacle, environment);
            }
            for (ModelInstance wall : buildingWallInstances) {
                modelBatch.render(wall, environment);
            }
            for (ModelInstance roof : roofInstances) {
                modelBatch.render(roof, environment);
            }
            for (ModelInstance interactable : interactableInstances) {
                modelBatch.render(interactable, environment);
            }
            updateItems(world);
            for (ModelInstance item : itemInstances) {
                modelBatch.render(item, environment);
            }
            updateEnemies(world);
            for (ModelInstance enemy : enemyInstances) {
                modelBatch.render(enemy, environment);
            }
            updateAttack(world);
            if (world.getCombatController().isAttackVisible()) {
                modelBatch.render(attackInstance, environment);
            }
        }
        modelBatch.render(heroInstance, environment);
        modelBatch.end();
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        groundModel.dispose();
        interiorFloorModel.dispose();
        heroModel.dispose();
        npcModel.dispose();
        enemyModel.dispose();
        itemModel.dispose();
        buildingModel.dispose();
        doorModel.dispose();
        roofModel.dispose();
        attackModel.dispose();
        interiorBackgroundModel.dispose();
        platformModel.dispose();
        roadModel.dispose();
        stationMarkerModel.dispose();
    }

    private void refreshExteriorInstances(GameWorld world) {
        if (world.getActiveArea() == renderedArea) return;
        obstacleInstances.clear();
        buildingWallInstances.clear();
        roofInstances.clear();
        roadInstances.clear();
        enemyInstances.clear();
        createRoadInstances(world);
        createObstacleInstances(world.getObstacles());
        createBuildingInstances(world);
        createEnemyInstances(world);
        renderedArea = world.getActiveArea();
        doorLogged = false;
    }

    private void updateHero(GameWorld world) {
        Hero hero = world.getHero();
        float height = world.isInBuilding() ? hero.getPlatformerHeight() + Hero.SIZE / 2f : GROUND_Y + Hero.SIZE / 2f;
        heroInstance.transform.setToTranslation(work.set(hero.getX(), height, hero.getY()));
    }

    private void updateEnemies(GameWorld world) {
        List<PrototypeEnemy> enemies = world.getEnemies();
        float baseHeight = world.isInBuilding() && world.getActiveInterior() != null
            ? getInteriorFloorHeight(world.getActiveInterior())
            : GROUND_Y;
        for (int i = 0; i < enemies.size(); i++) {
            PrototypeEnemy enemy = enemies.get(i);
            ModelInstance instance = enemyInstances.get(i);
            instance.transform.setToTranslation(work.set(enemy.getPosition().x,
                baseHeight + PrototypeEnemy.SIZE / 2f, enemy.getPosition().y));
        }
    }

    private float getInteriorFloorHeight(Interior interior) {
        float lowestTop = Float.MAX_VALUE;
        for (InteriorPlatform platform : interior.getPlatforms()) {
            Rectangle bounds = platform.getBounds();
            float top = bounds.y + bounds.height;
            if (top < lowestTop) {
                lowestTop = top;
            }
        }
        return lowestTop == Float.MAX_VALUE ? GROUND_Y : lowestTop;
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

    private void updateInteractableInstances(GameWorld world) {
        interactableInstances.clear();
        for (Interactable interactable : world.getInteractables()) {
            if (interactable instanceof PrototypeItem || interactable instanceof PrototypeEnemy) {
                continue;
            }
            Rectangle bounds = interactable.getBounds();
            float x = bounds.x + bounds.width / 2f;
            float z = bounds.y + bounds.height / 2f;
            if (interactable instanceof BuildingEntrance) {
                BuildingEntrance entrance = (BuildingEntrance) interactable;
                Building building = entrance.getBuilding();
                float height = building.getHeight();
                float depth = Building.WALL_THICKNESS;
                ModelInstance instance = new ModelInstance(doorModel);
                instance.transform.setToScaling(bounds.width, height, depth);
                instance.transform.setTranslation(work.set(x, GROUND_Y + height / 2f, z));
                interactableInstances.add(instance);
                if (Gdx.app != null && !doorLogged) {
                    Gdx.app.log("BuildingDebug", "Door model created for entrance: " + building.getId()
                        + " position=(" + x + "," + z + ")");
                    doorLogged = true;
                }
            } else if (interactable instanceof BuildingExit) {
                float height = 80f;
                float depth = Building.WALL_THICKNESS;
                ModelInstance instance = new ModelInstance(doorModel);
                instance.transform.setToScaling(bounds.width, height, depth);
                instance.transform.setTranslation(work.set(x, GROUND_Y + height / 2f, z));
                interactableInstances.add(instance);
            } else if (interactable instanceof PrototypeNpc) {
                float npcHeight = world.isInBuilding() ? 20f + PrototypeNpc.SIZE / 2f : GROUND_Y + PrototypeNpc.SIZE / 2f;
                ModelInstance instance = new ModelInstance(npcModel);
                instance.transform.setToTranslation(work.set(x, npcHeight, z));
                interactableInstances.add(instance);
            } else if (interactable instanceof TravelPoint) {
                ModelInstance instance = new ModelInstance(stationMarkerModel);
                instance.transform.setToScaling(120f, 90f, 20f);
                instance.transform.setTranslation(work.set(x, GROUND_Y + 45f, z));
                interactableInstances.add(instance);
            }
        }
    }

    private void createRoadInstances(GameWorld world) {
        for (Rectangle road : world.getAreaGroundFeatures()) {
            ModelInstance instance = new ModelInstance(roadModel);
            instance.transform.setToScaling(road.width, 2f, road.height);
            instance.transform.setTranslation(work.set(road.x + road.width / 2f, GROUND_Y + 2f,
                road.y + road.height / 2f));
            roadInstances.add(instance);
        }
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

    private void createBuildingInstances(GameWorld world) {
        for (Building building : world.getBuildings()) {
            for (Rectangle wall : building.getWallObstacles()) {
                ModelInstance instance = new ModelInstance(buildingModel);
                float width = wall.width;
                float depth = wall.height;
                float height = building.getHeight();
                instance.transform.setToScaling(width, height, depth);
                instance.transform.setTranslation(work.set(wall.x + width / 2f,
                    GROUND_Y + height / 2f, wall.y + depth / 2f));
                buildingWallInstances.add(instance);
            }

            Rectangle roof = building.getRoofBounds();
            ModelInstance roofInstance = new ModelInstance(roofModel);
            roofInstance.transform.setToScaling(roof.width, Building.ROOF_THICKNESS, roof.height);
            roofInstance.transform.setTranslation(work.set(roof.x + roof.width / 2f,
                GROUND_Y + building.getHeight() + Building.ROOF_THICKNESS / 2f, roof.y + roof.height / 2f));
            roofInstances.add(roofInstance);
        }
    }

    private void updateInteriorInstances(GameWorld world) {
        interiorBackgroundInstances.clear();
        interiorObstacleInstances.clear();
        platformInstances.clear();
        Interior interior = world.getActiveInterior();
        if (interior == null) {
            return;
        }

        float lane = interior.getPlayerSpawn().y;
        ModelInstance background = new ModelInstance(interiorBackgroundModel);
        background.transform.setToScaling(interior.getWidth(), interior.getHeight(), 8f);
        background.transform.setTranslation(work.set(interior.getWidth() / 2f, interior.getHeight() / 2f, lane - 24f));
        interiorBackgroundInstances.add(background);

        for (InteriorPlatform platform : interior.getPlatforms()) {
            Rectangle bounds = platform.getBounds();
            ModelInstance instance = new ModelInstance(platformModel);
            instance.transform.setToScaling(bounds.width, bounds.height, 24f);
            instance.transform.setTranslation(work.set(bounds.x + bounds.width / 2f,
                bounds.y + bounds.height / 2f, lane));
            platformInstances.add(instance);
        }

        float boundaryHeight = interior.getHeight();
        for (float x : new float[]{0f, interior.getWidth()}) {
            ModelInstance boundary = new ModelInstance(platformModel);
            boundary.transform.setToScaling(12f, boundaryHeight, 24f);
            boundary.transform.setTranslation(work.set(x, boundaryHeight / 2f, lane));
            interiorObstacleInstances.add(boundary);
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
