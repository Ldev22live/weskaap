package com.weskaap.game.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.weskaap.game.combat.CombatController;
import com.weskaap.game.combat.Combatant;
import com.weskaap.game.enemy.EnemyAiController;
import com.weskaap.game.enemy.PrototypeEnemy;
import com.weskaap.game.interaction.Interactable;
import com.weskaap.game.interaction.InteractionController;
import com.weskaap.game.interaction.PrototypeNpc;
import com.weskaap.game.inventory.InventoryController;
import com.weskaap.game.item.Item;
import com.weskaap.game.item.ItemType;
import com.weskaap.game.item.PrototypeItem;
import com.weskaap.game.player.Hero;
import com.weskaap.game.player.HeroController;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameWorld {
	public static final float WIDTH = 3200f;
	public static final float HEIGHT = 2400f;

	private static final float GRID_SIZE = 128f;
	private static final float MESSAGE_DURATION = 3f;
	private static final float COMBAT_MESSAGE_DURATION = 2f;
	private static final Color GROUND_COLOR = new Color(0.12f, 0.19f, 0.13f, 1f);
	private static final Color HERO_COLOR = new Color(0.9f, 0.68f, 0.2f, 1f);
	private static final Color NPC_COLOR = new Color(0.2f, 0.55f, 0.9f, 1f);
	private static final Color ENEMY_COLOR = new Color(0.75f, 0.18f, 0.16f, 1f);
	private static final Color ITEM_COLOR = new Color(0.2f, 0.9f, 0.4f, 1f);
	private static final Color ATTACK_COLOR = new Color(1f, 0.75f, 0.2f, 1f);
	private static final Color OBSTACLE_COLOR = new Color(0.32f, 0.29f, 0.25f, 1f);
	private static final Color GRID_COLOR = new Color(0.22f, 0.32f, 0.23f, 1f);

	private final ShapeRenderer shapeRenderer;
	private final Hero hero;
	private final HeroController heroController;
	private final InteractionController interactionController;
	private final InventoryController inventoryController;
	private final CombatController combatController;
	private final List<Rectangle> obstacles;
	private final List<Rectangle> collisionBounds;
	private final List<Interactable> interactables;
	private final List<PrototypeItem> worldItems;
	private final List<PrototypeEnemy> enemies;
	private final List<EnemyAiController> enemyAiControllers;
	private Interactable currentInteractable;
	private String interactionMessage;
	private float interactionMessageTimer;
	private String combatMessage;
	private float combatMessageTimer;
	private String aiStateMessage;
	private boolean inventoryVisible;

	public GameWorld() {
		shapeRenderer = new ShapeRenderer();
		hero = new Hero(WIDTH / 2f, HEIGHT / 2f, 240f);
		heroController = new HeroController();
		interactionController = new InteractionController();
		inventoryController = new InventoryController();
		combatController = new CombatController();
		obstacles = createObstacles();
		worldItems = createWorldItems();
		interactables = createInteractables();
		enemies = createEnemies();
		enemyAiControllers = createEnemyAiControllers();
		collisionBounds = createCollisionBounds();
	}

	public Hero getHero() {
		return hero;
	}

	public List<Rectangle> getObstacles() {
		return obstacles;
	}

	public String getInteractionMessage() {
		return interactionMessage;
	}

	public String getCombatMessage() {
		return combatMessage;
	}

	public String getAiStateMessage() {
		return aiStateMessage;
	}

	public String getHeroStatusMessage() {
		return "Hero HP: " + hero.getHealth() + " / " + hero.getMaximumHealth();
	}

	public boolean hasCurrentInteractable() {
		return currentInteractable != null;
	}

	public boolean isInventoryVisible() {
		return inventoryVisible;
	}

	public List<PrototypeItem> getWorldItems() {
		return worldItems;
	}

	public String getInventoryText() {
		StringBuilder builder = new StringBuilder();
		builder.append("Inventory: ").append(hero.getInventory().size()).append(" / ")
			.append(hero.getInventory().getCapacity());
		for (Item item : hero.getInventory().getItems()) {
			builder.append("\n- ").append(item.toString());
		}
		return builder.toString();
	}

	public void update(float delta) {
		updateInteractionMessage(delta);
		updateCombatMessage(delta);
		if (inventoryController.isToggleRequested()) {
			inventoryVisible = !inventoryVisible;
		}
		Vector2 movement = heroController.getMovement(hero, delta);
		if (hero.isAlive()) {
			CollisionResolver.move(hero, movement.x, movement.y, collisionBounds, WIDTH, HEIGHT);
		}
		updateEnemyAi(delta);
		currentInteractable = findNearestInteractable();
		if (hero.isAlive() && currentInteractable != null && interactionController.isInteractionRequested()) {
			interactionMessage = currentInteractable.interact();
			interactionMessageTimer = MESSAGE_DURATION;
			if (currentInteractable instanceof PrototypeItem) {
				collectItem((PrototypeItem) currentInteractable);
			}
		}
		if (hero.isAlive() && combatController.update(delta, hero, enemies) && combatController.getHitCount() > 0) {
			updateCombatFeedback(combatController.getLastHit());
			removeDeadEnemies();
		}
	}

	public void render() {
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(GROUND_COLOR);
		shapeRenderer.rect(0f, 0f, WIDTH, HEIGHT);
		shapeRenderer.setColor(OBSTACLE_COLOR);
		for (Rectangle obstacle : obstacles) {
			shapeRenderer.rect(obstacle.x, obstacle.y, obstacle.width, obstacle.height);
		}
		shapeRenderer.setColor(NPC_COLOR);
		for (Interactable interactable : interactables) {
			if (interactable instanceof PrototypeItem) {
				continue;
			}
			Rectangle bounds = interactable.getBounds();
			shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
		}
		shapeRenderer.setColor(ITEM_COLOR);
		for (PrototypeItem item : worldItems) {
			Rectangle bounds = item.getBounds();
			shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
		}
		shapeRenderer.setColor(ENEMY_COLOR);
		for (PrototypeEnemy enemy : enemies) {
			Rectangle bounds = enemy.getCombatBounds();
			shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
		}
		shapeRenderer.setColor(HERO_COLOR);
		shapeRenderer.rect(hero.getX() - Hero.SIZE / 2f, hero.getY() - Hero.SIZE / 2f, Hero.SIZE, Hero.SIZE);
		shapeRenderer.end();

		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		shapeRenderer.setColor(GRID_COLOR);
		for (float x = 0f; x <= WIDTH; x += GRID_SIZE) {
			shapeRenderer.line(x, 0f, x, HEIGHT);
		}
		for (float y = 0f; y <= HEIGHT; y += GRID_SIZE) {
			shapeRenderer.line(0f, y, WIDTH, y);
		}
		if (combatController.isAttackVisible()) {
			Rectangle attackBounds = combatController.getAttackBounds();
			shapeRenderer.setColor(ATTACK_COLOR);
			shapeRenderer.rect(attackBounds.x, attackBounds.y, attackBounds.width, attackBounds.height);
		}
		shapeRenderer.end();
	}

	public void setProjectionMatrix(com.badlogic.gdx.math.Matrix4 projectionMatrix) {
		shapeRenderer.setProjectionMatrix(projectionMatrix);
	}

	private List<Rectangle> createObstacles() {
		List<Rectangle> worldObstacles = new ArrayList<>();
		worldObstacles.add(new Rectangle(1050f, 700f, 300f, 500f));
		worldObstacles.add(new Rectangle(1780f, 1080f, 360f, 140f));
		worldObstacles.add(new Rectangle(1450f, 1500f, 180f, 360f));
		worldObstacles.add(new Rectangle(2350f, 550f, 420f, 300f));
		return worldObstacles;
	}

	private List<Interactable> createInteractables() {
		List<Interactable> worldInteractables = new ArrayList<>();
		worldInteractables.add(new PrototypeNpc(1700f, 1320f, "Hello, Hero!"));
		worldInteractables.addAll(worldItems);
		return worldInteractables;
	}

	private List<PrototypeItem> createWorldItems() {
		List<PrototypeItem> items = new ArrayList<>();
		items.add(new PrototypeItem(1400f, 1180f,
			new Item("basic-sword", "Basic Sword", "A simple starter blade.", ItemType.WEAPON)));
		items.add(new PrototypeItem(1900f, 1500f,
			new Item("healing-potion", "Healing Potion", "A simple restorative tonic.",
				ItemType.CONSUMABLE, true, 20, 3)));
		items.add(new PrototypeItem(1600f, 900f,
			new Item("leather-armour", "Leather Armour", "Basic protective gear.", ItemType.ARMOUR)));
		items.add(new PrototypeItem(2100f, 1600f,
			new Item("quest-relic", "Quest Relic", "An old relic for an important quest.", ItemType.QUEST)));
		return items;
	}

	private List<PrototypeEnemy> createEnemies() {
		List<PrototypeEnemy> worldEnemies = new ArrayList<>();
		worldEnemies.add(new PrototypeEnemy(900f, 1000f, 100, EnemyAiController.ENEMY_SPEED));
		worldEnemies.add(new PrototypeEnemy(2500f, 1800f, 100, EnemyAiController.ENEMY_SPEED));
		return worldEnemies;
	}

	private List<EnemyAiController> createEnemyAiControllers() {
		List<EnemyAiController> controllers = new ArrayList<>();
		for (PrototypeEnemy enemy : enemies) {
			controllers.add(new EnemyAiController(enemy));
		}
		return controllers;
	}

	private List<Rectangle> createCollisionBounds() {
		List<Rectangle> worldCollisionBounds = new ArrayList<>(obstacles);
		for (Interactable interactable : interactables) {
			worldCollisionBounds.add(interactable.getBounds());
		}
		for (PrototypeEnemy enemy : enemies) {
			worldCollisionBounds.add(enemy.getCombatBounds());
		}
		return worldCollisionBounds;
	}

	private void collectItem(PrototypeItem prototypeItem) {
		if (hero.getInventory().add(prototypeItem.getItem())) {
			worldItems.remove(prototypeItem);
			interactables.remove(prototypeItem);
			collisionBounds.remove(prototypeItem.getBounds());
			if (currentInteractable == prototypeItem) {
				currentInteractable = null;
			}
		} else {
			interactionMessage = "Inventory full!";
			interactionMessageTimer = MESSAGE_DURATION;
		}
	}

	private Interactable findNearestInteractable() {
		Interactable nearest = null;
		float nearestDistance = Float.MAX_VALUE;
		for (Interactable interactable : interactables) {
			float distance = interactable.getInteractionPosition().dst2(hero.getPosition());
			if (interactable.canInteract(hero) && distance < nearestDistance) {
				nearest = interactable;
				nearestDistance = distance;
			}
		}
		return nearest;
	}

	private void updateInteractionMessage(float delta) {
		if (interactionMessageTimer <= 0f) {
			return;
		}
		interactionMessageTimer = Math.max(0f, interactionMessageTimer - delta);
		if (interactionMessageTimer == 0f) {
			interactionMessage = null;
		}
	}

	private void updateCombatFeedback(Combatant combatant) {
		if (combatant.isAlive()) {
			combatMessage = "Damage: " + CombatController.BASIC_ATTACK_DAMAGE + " | Enemy HP: "
				+ combatant.getHealth() + " / " + combatant.getMaximumHealth();
		} else {
			combatMessage = "Damage: " + CombatController.BASIC_ATTACK_DAMAGE + " | Enemy defeated!";
		}
		combatMessageTimer = COMBAT_MESSAGE_DURATION;
	}

	private void updateCombatMessage(float delta) {
		if (combatMessageTimer <= 0f) {
			return;
		}
		combatMessageTimer = Math.max(0f, combatMessageTimer - delta);
		if (combatMessageTimer == 0f) {
			combatMessage = null;
		}
	}

	private void updateEnemyAi(float delta) {
		StringBuilder builder = new StringBuilder();
		boolean heroWasAlive = hero.isAlive();
		List<Rectangle> otherCollisionBounds = new ArrayList<>(collisionBounds);
		for (EnemyAiController controller : enemyAiControllers) {
			otherCollisionBounds.remove(controller.getEnemy().getCombatBounds());
			controller.update(delta, hero, otherCollisionBounds, WIDTH, HEIGHT);
			otherCollisionBounds.add(controller.getEnemy().getCombatBounds());
			if (controller.getLastAttackDamage() > 0) {
				updateHeroDamageFeedback(controller.getLastAttackDamage());
			}
			if (builder.length() > 0) {
				builder.append(" | ");
			}
			builder.append("Enemy: ").append(controller.getState())
				.append(" (").append(Math.round(controller.getDistanceToHero(hero))).append(")");
		}
		if (heroWasAlive && !hero.isAlive()) {
			combatMessage = "Hero defeated!";
			combatMessageTimer = COMBAT_MESSAGE_DURATION;
		}
		aiStateMessage = builder.length() > 0 ? builder.toString() : null;
	}

	private void updateHeroDamageFeedback(int damage) {
		if (hero.isAlive()) {
			combatMessage = "Enemy attacks! Hero HP: " + hero.getHealth() + " / " + hero.getMaximumHealth();
		} else {
			combatMessage = "Enemy attacks! Hero defeated!";
		}
		combatMessageTimer = COMBAT_MESSAGE_DURATION;
	}

	private void removeDeadEnemies() {
		Iterator<PrototypeEnemy> iterator = enemies.iterator();
		while (iterator.hasNext()) {
			PrototypeEnemy enemy = iterator.next();
			if (!enemy.isAlive()) {
				collisionBounds.remove(enemy.getCombatBounds());
				iterator.remove();
			}
		}
	}

	public void dispose() {
		shapeRenderer.dispose();
	}
}
