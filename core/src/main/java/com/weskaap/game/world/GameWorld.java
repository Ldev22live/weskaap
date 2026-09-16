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
import com.weskaap.game.equipment.EquipmentSlot;
import com.weskaap.game.inventory.InventoryController;
import com.weskaap.game.item.Item;
import com.weskaap.game.item.ItemStats;
import com.weskaap.game.item.ItemType;
import com.weskaap.game.item.PrototypeItem;
import com.weskaap.game.loot.LootGenerator;
import com.weskaap.game.loot.LootTable;
import com.weskaap.game.player.Hero;
import com.weskaap.game.consumable.ConsumableController;
import com.weskaap.game.consumable.ItemUseResult;
import com.weskaap.game.player.HeroController;
import com.weskaap.game.quest.Quest;
import com.weskaap.game.quest.QuestController;
import com.weskaap.game.quest.QuestLog;
import com.weskaap.game.quest.QuestLogController;
import com.weskaap.game.quest.QuestObjective;
import com.weskaap.game.quest.QuestObjectiveType;
import com.weskaap.game.dialogue.DialogueController;
import com.weskaap.game.dialogue.DialogueInputController;
import com.weskaap.game.dialogue.DialogueRepository;
import com.weskaap.game.world3d.WorldRenderer3D;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameWorld {
	public static final float WIDTH = 3200f;
	public static final float HEIGHT = 2400f;

	private static final float GRID_SIZE = 128f;
	private static final float MESSAGE_DURATION = 3f;
	private static final float COMBAT_MESSAGE_DURATION = 2f;
	private static final float QUEST_MESSAGE_DURATION = 3f;
	private static final Color GROUND_COLOR = new Color(0.12f, 0.19f, 0.13f, 1f);
	private static final Color HERO_COLOR = new Color(0.9f, 0.68f, 0.2f, 1f);
	private static final Color NPC_COLOR = new Color(0.2f, 0.55f, 0.9f, 1f);
	private static final Color ENEMY_COLOR = new Color(0.75f, 0.18f, 0.16f, 1f);
	private static final Color ITEM_COLOR = new Color(0.2f, 0.9f, 0.4f, 1f);
	private static final Color ATTACK_COLOR = new Color(1f, 0.75f, 0.2f, 1f);
	private static final Color OBSTACLE_COLOR = new Color(0.32f, 0.29f, 0.25f, 1f);
	private static final Color GRID_COLOR = new Color(0.22f, 0.32f, 0.23f, 1f);

	private final ShapeRenderer shapeRenderer;
	private final WorldRenderer3D worldRenderer3D;
	private final Hero hero;
	private final HeroController heroController;
	private final InteractionController interactionController;
	private final InventoryController inventoryController;
	private final CombatController combatController;
	private final LootGenerator lootGenerator;
	private final ConsumableController consumableController;
	private final QuestLogController questLogController;
	private final DialogueController dialogueController;
	private final DialogueInputController dialogueInputController;
	private final QuestController questController;
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
	private boolean questLogVisible;
	private String questMessage;
	private float questMessageTimer;

	public GameWorld() {
		shapeRenderer = new ShapeRenderer();
		hero = new Hero(WIDTH / 2f, HEIGHT / 2f, 240f);
		setupStartingEquipment();
		heroController = new HeroController();
		interactionController = new InteractionController();
		inventoryController = new InventoryController();
		combatController = new CombatController();
		lootGenerator = new LootGenerator();
		consumableController = new ConsumableController();
		questLogController = new QuestLogController();
		dialogueController = new DialogueController();
		dialogueInputController = new DialogueInputController(dialogueController);
		questController = new QuestController(hero.getQuestLog());
		dialogueController.setQuestController(questController);
		setupQuests();
		obstacles = createObstacles();
		worldItems = createWorldItems();
		interactables = createInteractables();
		enemies = createEnemies();
		enemyAiControllers = createEnemyAiControllers();
		collisionBounds = createCollisionBounds();
		worldRenderer3D = new WorldRenderer3D(this);
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

	public Interactable getCurrentInteractable() {
		return currentInteractable;
	}

	public boolean isInventoryVisible() {
		return inventoryVisible;
	}

	public boolean isQuestLogVisible() {
		return questLogVisible;
	}

	public String getQuestMessage() {
		return questMessage;
	}

	public boolean isDialogueActive() {
		return dialogueController.isActive();
	}

	public DialogueController getDialogueController() {
		return dialogueController;
	}

	public List<PrototypeItem> getWorldItems() {
		return worldItems;
	}

	public List<PrototypeEnemy> getEnemies() {
		return enemies;
	}

	public List<Interactable> getInteractables() {
		return interactables;
	}

	public CombatController getCombatController() {
		return combatController;
	}

	public String getInventoryText() {
		StringBuilder builder = new StringBuilder();
		builder.append("Inventory: ").append(hero.getInventory().size()).append(" / ")
			.append(hero.getInventory().getCapacity());
		builder.append("\nEquipped:");
		for (EquipmentSlot slot : EquipmentSlot.values()) {
			Item equipped = hero.getEquipment().getEquipped(slot);
			builder.append("\n- ").append(slot).append(": ")
				.append(equipped != null ? equipped.getName() : "none");
		}
		builder.append("\nStats: ATK ").append(hero.getTotalAttack())
			.append(" | ARM ").append(hero.getTotalArmour());
		for (Item item : hero.getInventory().getItems()) {
			builder.append("\n- ").append(item.toString());
		}
		return builder.toString();
	}

	public String getQuestLogText() {
		StringBuilder builder = new StringBuilder();
		builder.append("QUEST LOG");
		for (Quest quest : hero.getQuestLog().getQuests()) {
			builder.append("\n\n").append(quest.getTitle());
			builder.append("\n").append(quest.getDescription());
			for (QuestObjective objective : quest.getObjectives()) {
				builder.append("\n").append(objective.getDescription());
				builder.append("\nProgress: ").append(objective.getCurrentAmount())
					.append(" / ").append(objective.getRequiredAmount());
			}
			builder.append("\nStatus: ").append(quest.getState());
		}
		return builder.toString();
	}

	public void update(float delta) {
		updateInteractionMessage(delta);
		updateCombatMessage(delta);
		updateQuestMessage(delta);
		if (inventoryController.isToggleRequested()) {
			inventoryVisible = !inventoryVisible;
		}
		if (questLogController.isToggleRequested()) {
			questLogVisible = !questLogVisible;
		}
		if (dialogueController.isActive()) {
			dialogueInputController.update();
			updateEnemyAi(delta);
			return;
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
			} else if (currentInteractable instanceof PrototypeNpc) {
				handleNpcInteraction((PrototypeNpc) currentInteractable);
			}
		}
		if (hero.isAlive() && combatController.update(delta, hero, enemies) && combatController.getHitCount() > 0) {
			updateCombatFeedback(combatController.getLastHit());
			removeDeadEnemies();
		}
		ItemUseResult consumableResult = consumableController.update(hero, hero.getInventory());
		if (consumableResult != null) {
			updateConsumableFeedback(consumableResult);
		}
	}

	public void render3D(com.weskaap.game.world3d.IsometricCamera camera) {
		worldRenderer3D.render(camera, this);
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
		worldInteractables.add(new PrototypeNpc(1700f, 1320f, "Hello, Hero! I have a task for you.",
			"quest_first_steps", "speak-local-guide"));
		worldInteractables.add(new PrototypeNpc(2100f, 1100f, "Howzit, neighbour.",
			"quest_meet_the_neighbour", "speak-neighbour"));
		worldInteractables.add(new PrototypeNpc(1300f, 1000f, "Tubby Angel awaits.",
			DialogueRepository.createTubbyAngelDialogue()));
		worldInteractables.addAll(worldItems);
		return worldInteractables;
	}

	private void setupQuests() {
		Quest firstSteps = new Quest("quest_first_steps", "First Steps", "Speak with the local guide.",
			List.of(new QuestObjective("speak-local-guide", "Speak with the local guide",
				QuestObjectiveType.TALK_TO_NPC, "speak-local-guide", 1)));
		Quest meetTheNeighbour = new Quest("quest_meet_the_neighbour", "Meet the Neighbour",
			"Interact with another person in the area.",
			List.of(new QuestObjective("speak-neighbour", "Speak with the neighbour",
				QuestObjectiveType.TALK_TO_NPC, "speak-neighbour", 1)));
		hero.getQuestLog().add(firstSteps);
		hero.getQuestLog().add(meetTheNeighbour);
	}

	private void setupStartingEquipment() {
		Item basicSword = new Item("basic-sword", "Basic Sword", "A simple starter blade.",
			ItemType.WEAPON, false, 1, 1, new ItemStats(5, 0, 0, 0, 0f));
		Item leatherArmour = new Item("leather-armour", "Leather Armour", "Basic protective gear.",
			ItemType.ARMOUR, false, 1, 1, new ItemStats(0, 3, 0, 0, 0f));
		hero.getInventory().add(basicSword);
		hero.getInventory().add(leatherArmour);
		hero.getEquipment().equip(hero.getInventory(), basicSword);
		hero.getEquipment().equip(hero.getInventory(), leatherArmour);
	}

	private List<PrototypeItem> createWorldItems() {
		List<PrototypeItem> items = new ArrayList<>();
		items.add(new PrototypeItem(1900f, 1500f,
			new Item("healing-potion", "Healing Potion", "A simple restorative tonic.",
				ItemType.CONSUMABLE, true, 20, 3)));
		items.add(new PrototypeItem(2100f, 1600f,
			new Item("quest-relic", "Quest Relic", "An old relic for an important quest.", ItemType.QUEST)));
		return items;
	}

	private List<PrototypeEnemy> createEnemies() {
		List<PrototypeEnemy> worldEnemies = new ArrayList<>();
		LootTable basicLoot = createBasicLootTable();
		PrototypeEnemy firstEnemy = new PrototypeEnemy(900f, 1000f, 100, EnemyAiController.ENEMY_SPEED);
		firstEnemy.setLootTable(basicLoot);
		PrototypeEnemy secondEnemy = new PrototypeEnemy(2500f, 1800f, 100, EnemyAiController.ENEMY_SPEED);
		secondEnemy.setLootTable(basicLoot);
		worldEnemies.add(firstEnemy);
		worldEnemies.add(secondEnemy);
		return worldEnemies;
	}

	private LootTable createBasicLootTable() {
		LootTable table = new LootTable(2);
		table.add(new Item("healing-potion", "Healing Potion", "A simple restorative tonic.",
			ItemType.CONSUMABLE, true, 20, 1), 0.5f);
		table.add(new Item("basic-sword", "Basic Sword", "A simple starter blade.",
			ItemType.WEAPON, false, 1, 1, new ItemStats(5, 0, 0, 0, 0f)), 0.1f);
		table.add(new Item("leather-armour", "Leather Armour", "Basic protective gear.",
			ItemType.ARMOUR, false, 1, 1, new ItemStats(0, 3, 0, 0, 0f)), 0.1f);
		table.add(new Item("quest-relic", "Quest Relic", "An old relic for an important quest.", ItemType.QUEST), 0.05f);
		return table;
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

	private void handleNpcInteraction(PrototypeNpc npc) {
		if (npc.hasDialogue()) {
			dialogueController.startDialogue(npc.getDialogue());
			return;
		}
		handleQuestInteraction(npc);
	}

	private void handleQuestInteraction(PrototypeNpc npc) {
		if (!npc.hasQuestObjective()) {
			return;
		}
		String questId = npc.getQuestId();
		String objectiveId = npc.getObjectiveId();
		Quest quest = questController.getQuest(questId);
		if (quest == null) {
			return;
		}
		if (questController.isQuestAvailable(questId)) {
			if (questController.startQuest(questId)) {
				questMessage = "Quest started: " + quest.getTitle();
				questMessageTimer = QUEST_MESSAGE_DURATION;
			}
		}
		questController.updateObjective(objectiveId);
		if (questController.isQuestCompleted(questId)) {
			questMessage = "Quest completed: " + quest.getTitle();
			questMessageTimer = QUEST_MESSAGE_DURATION;
		}
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
		int attackDamage = CombatController.BASIC_ATTACK_DAMAGE + hero.getTotalAttack();
		if (combatant.isAlive()) {
			combatMessage = "Damage: " + attackDamage + " | Enemy HP: "
				+ combatant.getHealth() + " / " + combatant.getMaximumHealth();
		} else {
			combatMessage = "Damage: " + attackDamage + " | Enemy defeated!";
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

	private void updateQuestMessage(float delta) {
		if (questMessageTimer <= 0f) {
			return;
		}
		questMessageTimer = Math.max(0f, questMessageTimer - delta);
		if (questMessageTimer == 0f) {
			questMessage = null;
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

	private void updateConsumableFeedback(ItemUseResult result) {
		switch (result) {
			case SUCCESS:
				interactionMessage = "Used Healing Potion. +" + ConsumableController.HEALING_POTION_AMOUNT + " HP";
				break;
			case ALREADY_FULL_HEALTH:
				interactionMessage = "Health is already full.";
				break;
			case HERO_DEAD:
				interactionMessage = "Cannot use items while dead.";
				break;
			case ITEM_NOT_FOUND:
				interactionMessage = "No Healing Potion in inventory.";
				break;
			case NOT_CONSUMABLE:
				interactionMessage = "Item is not consumable.";
				break;
			case INVALID_ITEM:
				interactionMessage = "Invalid item.";
				break;
			case CANNOT_APPLY:
				interactionMessage = "Cannot use item right now.";
				break;
		}
		interactionMessageTimer = MESSAGE_DURATION;
	}

	private void removeDeadEnemies() {
		Iterator<PrototypeEnemy> iterator = enemies.iterator();
		while (iterator.hasNext()) {
			PrototypeEnemy enemy = iterator.next();
			if (!enemy.isAlive()) {
				if (!enemy.hasDroppedLoot() && enemy.getLootTable() != null) {
					dropLoot(enemy);
				}
				collisionBounds.remove(enemy.getCombatBounds());
				iterator.remove();
			}
		}
	}

	private void dropLoot(PrototypeEnemy enemy) {
		List<Item> drops = lootGenerator.generate(enemy.getLootTable());
		enemy.markLootDropped();
		if (drops.isEmpty()) {
			combatMessage = "Enemy defeated!";
			combatMessageTimer = COMBAT_MESSAGE_DURATION;
			return;
		}

		combatMessage = "Enemy defeated! Loot dropped!";
		combatMessageTimer = COMBAT_MESSAGE_DURATION;
		float x = enemy.getPosition().x;
		float y = enemy.getPosition().y;
		float spacing = PrototypeItem.SIZE + 10f;
		for (int i = 0; i < drops.size(); i++) {
			float offsetX = (i - (drops.size() - 1) / 2f) * spacing;
			float dropX = Math.max(PrototypeItem.SIZE / 2f, Math.min(WIDTH - PrototypeItem.SIZE / 2f, x + offsetX));
			float dropY = Math.max(PrototypeItem.SIZE / 2f, Math.min(HEIGHT - PrototypeItem.SIZE / 2f, y + 20f));
			PrototypeItem prototypeItem = new PrototypeItem(dropX, dropY, drops.get(i).copy());
			worldItems.add(prototypeItem);
			interactables.add(prototypeItem);
			collisionBounds.add(prototypeItem.getBounds());
		}
	}

	public void dispose() {
		shapeRenderer.dispose();
		worldRenderer3D.dispose();
	}
}
