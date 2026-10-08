package com.weskaap.game.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.weskaap.game.ally.Ally;
import com.weskaap.game.building.Building;
import com.weskaap.game.building.BuildingEntrance;
import com.weskaap.game.building.BuildingExit;
import com.weskaap.game.building.Interior;
import com.weskaap.game.chapel.ChapelInteriorData;
import com.weskaap.game.combat.CombatController;
import com.weskaap.game.combat.Combatant;
import com.weskaap.game.enemy.EnemyAiController;
import com.weskaap.game.enemy.PrototypeEnemy;
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
import com.weskaap.game.quest.QuestRepository;
import com.weskaap.game.quest.QuestObjectiveType;
import com.weskaap.game.dialogue.DialogueController;
import com.weskaap.game.dialogue.DialogueInputController;
import com.weskaap.game.dialogue.DialogueRepository;
import com.weskaap.game.interaction.Interactable;
import com.weskaap.game.interaction.InteractionController;
import com.weskaap.game.interaction.PrototypeNpc;
import com.weskaap.game.interaction.TravelPoint;
import com.weskaap.game.world3d.WorldRenderer3D;
import com.weskaap.game.travel.TravelController;
import com.weskaap.game.travel.TravelDestinationRepository;
import com.weskaap.game.quest.QuestEvent;
import com.weskaap.game.quest.QuestRewardService;
import com.weskaap.game.story.StoryState;
import com.weskaap.game.tiled.AreaData;
import com.weskaap.game.tiled.TiledAreaLoader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameWorld {
	public static final float WIDTH = 3200f;
	public static final float HEIGHT = 3200f;

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
	private final PlatformerController platformerController;
	private final InteractionController interactionController;
	private final InventoryController inventoryController;
	private final CombatController combatController;
	private final LootGenerator lootGenerator;
	private final ConsumableController consumableController;
	private final QuestLogController questLogController;
	private final DialogueController dialogueController;
	private final DialogueInputController dialogueInputController;
	private final QuestController questController;
	private final StoryState storyState;
	private final List<Rectangle> exteriorObstacles;
	private final List<Rectangle> retreatRoads;
	private final List<Building> buildings;
	private final List<Interactable> exteriorInteractables;
	private final List<PrototypeItem> worldItems;
	private final List<PrototypeEnemy> enemies;
	private final List<EnemyAiController> enemyAiControllers;
	private final TravelPoint retreatStationTravelPoint;
	private final TravelController travelController;
	private final AreaRegistry areaRegistry;
	private final Ally rosebankFriendAlly;

	private List<Rectangle> activeObstacles;
	private List<Interactable> activeInteractables;
	private List<Rectangle> activeCollisionBounds;
	private Building activeBuilding;
	private Vector2 exteriorHeroPosition;
	private List<PrototypeEnemy> savedExteriorEnemies;
	private List<EnemyAiController> savedExteriorEnemyAiControllers;
	private Interactable currentInteractable;
	private Interactable previousInteractable;
	private float currentInteractableDistance;
	private String interactionMessage;
	private float interactionMessageTimer;
	private String combatMessage;
	private float combatMessageTimer;
	private String aiStateMessage;
	private boolean inventoryVisible;
	private boolean questLogVisible;
	private String questMessage;
	private float questMessageTimer;
	private AreaId activeArea;
	private AreaId requestedArea;
	private AreaDefinition activeAreaDefinition;

	public GameWorld() {
		shapeRenderer = new ShapeRenderer();
		hero = new Hero(WIDTH / 2f, HEIGHT / 2f, 240f);
		setupStartingEquipment();
		heroController = new HeroController();
		platformerController = new PlatformerController();
		interactionController = new InteractionController();
		inventoryController = new InventoryController();
		combatController = new CombatController();
		lootGenerator = new LootGenerator();
		consumableController = new ConsumableController();
		questLogController = new QuestLogController();
		dialogueController = new DialogueController();
		dialogueInputController = new DialogueInputController(dialogueController);
		storyState = new StoryState();
		questController = new QuestController(hero.getQuestLog(),
			new QuestRewardService(hero.getWallet(), hero.getInventory(), storyState));
		dialogueController.setQuestController(questController);
		setupQuests();
		activeArea = AreaId.RETREAT;
		travelController = new TravelController(activeArea,
			TravelDestinationRepository.getDestinations(), this::requestAreaTransition);
		exteriorObstacles = new ArrayList<>();
		retreatRoads = new ArrayList<>();
		buildings = createBuildings();
		worldItems = createWorldItems();
		retreatStationTravelPoint = new TravelPoint(
			"retreat_station_travel", "RETREAT STATION", 1600f, 610f, AreaId.CPUT);
		exteriorInteractables = createInteractables();
		enemies = createEnemies();
		enemyAiControllers = createEnemyAiControllers();
		rosebankFriendAlly = new Ally("rosebank_friend");
		areaRegistry = createAreaRegistry();
		activeAreaDefinition = areaRegistry.get(activeArea);
		loadArea(activeAreaDefinition);
		exteriorHeroPosition = new Vector2(hero.getPosition());
		savedExteriorEnemies = new ArrayList<>();
		savedExteriorEnemyAiControllers = new ArrayList<>();
		activeBuilding = null;
		activeObstacles = exteriorObstacles;
		activeInteractables = exteriorInteractables;
		activeCollisionBounds = createActiveCollisionBounds();
		worldRenderer3D = new WorldRenderer3D(this);
		if (Gdx.app != null) {
			int npcCount = 0;
			for (Interactable i : activeInteractables) {
				if (i instanceof PrototypeNpc) {
					npcCount++;
				}
			}
			Gdx.app.log("NpcDebug", "GameWorld init: interactables=" + activeInteractables.size()
				+ " npcs=" + npcCount + " items=" + worldItems.size()
				+ " buildings=" + buildings.size());
		}
	}

	public Hero getHero() {
		return hero;
	}

	public StoryState getStoryState() {
		return storyState;
	}

	public List<Rectangle> getObstacles() {
		return activeObstacles;
	}

	public List<Interactable> getInteractables() {
		return activeInteractables;
	}

	public List<Building> getBuildings() {
		return buildings;
	}

	public List<Rectangle> getRetreatRoads() {
		return retreatRoads;
	}

	public List<Rectangle> getAreaGroundFeatures() {
		return retreatRoads;
	}

	public AreaId getActiveArea() {
		return activeArea;
	}

	public AreaId getRequestedArea() {
		return requestedArea;
	}

	public TravelPoint getRetreatStationTravelPoint() {
		return retreatStationTravelPoint;
	}

	public TravelController getTravelController() {
		return travelController;
	}

	public AreaRegistry getAreaRegistry() {
		return areaRegistry;
	}

	public Ally getRosebankFriendAlly() {
		return rosebankFriendAlly;
	}

	public TravelPoint getActiveTravelPoint() {
		for (Interactable interactable : exteriorInteractables) {
			if (interactable instanceof TravelPoint) return (TravelPoint) interactable;
		}
		return null;
	}

	public boolean isInBuilding() {
		return activeBuilding != null;
	}

	public CameraMode getCameraMode() {
		return activeBuilding == null ? CameraMode.ISOMETRIC : CameraMode.PLATFORMER;
	}

	public Interior getActiveInterior() {
		return activeBuilding == null ? null : activeBuilding.getInterior();
	}

	public float getCurrentWorldWidth() {
		return activeBuilding == null ? activeAreaDefinition.getWidth() : activeBuilding.getInterior().getWidth();
	}

	public float getCurrentWorldHeight() {
		return activeBuilding == null ? activeAreaDefinition.getHeight() : activeBuilding.getInterior().getHeight();
	}

	public String getCurrentPromptText() {
		return currentInteractable != null ? currentInteractable.getPromptText() : "";
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
		if (travelController.isMenuOpen()) {
			return;
		}
		if (inventoryController.isToggleRequested()) {
			inventoryVisible = !inventoryVisible;
		}
		if (questLogController.isToggleRequested()) {
			questLogVisible = !questLogVisible;
		}
		if (dialogueController.isActive()) {
			if (activeBuilding == null) {
				updateEnemyAi(delta);
			}
			dialogueInputController.update();
			return;
		}
		if (hero.isAlive() && activeBuilding != null) {
			platformerController.update(hero, activeBuilding.getInterior(),
				heroController.getPlatformerHorizontalInput(), heroController.isJumpRequested(), delta);
		} else if (hero.isAlive()) {
			Vector2 movement = heroController.getMovement(hero, delta);
			CollisionResolver.move(hero, movement.x, movement.y, activeCollisionBounds,
				getCurrentWorldWidth(), getCurrentWorldHeight());
		}
		if (activeBuilding == null || isCombatAllowedInActiveBuilding()) {
			updateEnemyAi(delta);
		}
		currentInteractable = findNearestInteractable();
		if (currentInteractable != previousInteractable) {
			if (Gdx.app != null) {
				if (currentInteractable instanceof BuildingEntrance) {
					BuildingEntrance entrance = (BuildingEntrance) currentInteractable;
					Gdx.app.log("InteractionDebug", "currentInteractable=BuildingEntrance"
						+ " position=" + entrance.getInteractionPosition()
						+ " distance=" + currentInteractableDistance
						+ " prompt=" + entrance.getPromptText());
				} else if (currentInteractable instanceof BuildingExit) {
					BuildingExit exit = (BuildingExit) currentInteractable;
					Gdx.app.log("InteractionDebug", "currentInteractable=BuildingExit"
						+ " position=" + exit.getInteractionPosition()
						+ " distance=" + currentInteractableDistance
						+ " prompt=" + exit.getPromptText());
				} else if (currentInteractable == null) {
					Gdx.app.log("InteractionDebug", "currentInteractable=null");
				}
			}
			previousInteractable = currentInteractable;
		}
		if (hero.isAlive() && currentInteractable != null && interactionController.isInteractionRequested()) {
			if (currentInteractable instanceof BuildingEntrance && Gdx.app != null) {
				Gdx.app.log("BuildingDebug", "E pressed on BuildingEntrance: "
					+ ((BuildingEntrance) currentInteractable).getBuilding().getId());
			} else if (currentInteractable instanceof BuildingExit && Gdx.app != null) {
				Gdx.app.log("BuildingDebug", "E pressed on BuildingExit: "
					+ ((BuildingExit) currentInteractable).getInterior().getId());
			}
			interactionMessage = currentInteractable.interact();
			interactionMessageTimer = MESSAGE_DURATION;
			if (currentInteractable instanceof PrototypeItem) {
				collectItem((PrototypeItem) currentInteractable);
			} else if (currentInteractable instanceof PrototypeNpc) {
				handleNpcInteraction((PrototypeNpc) currentInteractable);
			} else if (currentInteractable instanceof BuildingEntrance) {
				enterBuilding(((BuildingEntrance) currentInteractable).getBuilding());
			} else if (currentInteractable instanceof BuildingExit) {
				exitBuilding();
			} else if (currentInteractable instanceof TravelPoint) {
				travelController.openMenu();
			} else if (currentInteractable instanceof ChapelInteriorData.ChapelStorageCrate) {
				ChapelInteriorData.ChapelStorageCrate crate = (ChapelInteriorData.ChapelStorageCrate) currentInteractable;
				if (!crate.isLooted()) {
				hero.getInventory().add(crate.getContents().copy());
				crate.markLooted();
				}
				questController.handleEvent(new QuestEvent(QuestObjectiveType.INTERACT_WITH_OBJECT,
					currentInteractable.getId()));
			} else if (!currentInteractable.getId().isBlank()) {
				questController.handleEvent(new QuestEvent(QuestObjectiveType.INTERACT_WITH_OBJECT,
					currentInteractable.getId()));
			}
		}
		if ((activeBuilding == null || isCombatAllowedInActiveBuilding()) && hero.isAlive()
				&& combatController.update(delta, hero, enemies)
				&& combatController.getHitCount() > 0) {
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
		shapeRenderer.rect(0f, 0f, getCurrentWorldWidth(), getCurrentWorldHeight());
		shapeRenderer.setColor(OBSTACLE_COLOR);
		for (Rectangle obstacle : activeObstacles) {
			shapeRenderer.rect(obstacle.x, obstacle.y, obstacle.width, obstacle.height);
		}
		shapeRenderer.setColor(NPC_COLOR);
		for (Interactable interactable : activeInteractables) {
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
		float currentWidth = getCurrentWorldWidth();
		float currentHeight = getCurrentWorldHeight();
		for (float x = 0f; x <= currentWidth; x += GRID_SIZE) {
			shapeRenderer.line(x, 0f, x, currentHeight);
		}
		for (float y = 0f; y <= currentHeight; y += GRID_SIZE) {
			shapeRenderer.line(0f, y, currentWidth, y);
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

	private AreaRegistry createAreaRegistry() {
		AreaRegistry registry = new AreaRegistry();
		TiledAreaLoader loader = new TiledAreaLoader();
		AreaData retreatData = registry.loadArea(loader, AreaId.RETREAT, "retreat_location.tmx");
		registry.register(createRetreatArea(retreatData));
		registry.register(createCputArea());
		registry.register(createRosebankArea());
		return registry;
	}

	private AreaDefinition createRetreatArea(AreaData retreatData) {
		return RetreatAreaFactory.create(retreatData, buildings, exteriorInteractables,
			worldItems, enemies);
	}

	private AreaDefinition createCputArea() {
		List<Rectangle> features = List.of(new Rectangle(900f, 900f, 1400f, 180f));
		TravelPoint station = new TravelPoint("cput_station_travel", "CPUT STATION", 1600f, 820f, AreaId.RETREAT);
		PrototypeNpc guide = new PrototypeNpc("cput_placeholder_guide", "CPUT Guide", 1750f, 1120f,
			"The CPUT area will be developed in a later milestone.",
			DialogueRepository.createPlaceholderDialogue("cput-placeholder", "CPUT Guide",
				"The CPUT area will be developed in a later milestone."));
		return new AreaDefinition(AreaId.CPUT, "CPUT", WIDTH, HEIGHT, new Vector2(1600f, 900f),
			List.of(new Rectangle(900f, 1250f, 1400f, 80f)), features, List.of(),
			List.of(station, guide), List.of(), List.of());
	}

	private AreaDefinition createRosebankArea() {
		List<Rectangle> features = List.of(
			new Rectangle(850f, 1000f, 1500f, 180f),
			new Rectangle(1500f, 650f, 180f, 900f),
			new Rectangle(1050f, 1350f, 1100f, 220f));
		List<Rectangle> obstacles = List.of(
			new Rectangle(900f, 1250f, 300f, 220f),
			new Rectangle(2000f, 1250f, 300f, 220f),
			new Rectangle(1150f, 650f, 120f, 120f),
			new Rectangle(1930f, 650f, 120f, 120f));
		TravelPoint station = new TravelPoint("rosebank_station_travel", "ROSEBANK STATION", 1600f, 760f, AreaId.RETREAT);
		PrototypeNpc friend = new PrototypeNpc("rosebank_friend", "RosebankFriend", 1800f, 1220f,
			"You still have a friend in Rosebank.", DialogueRepository.createRosebankFriendDialogue());
		return new AreaDefinition(AreaId.ROSEBANK, "Rosebank", WIDTH, HEIGHT, new Vector2(1600f, 900f),
			obstacles, features, List.of(), List.of(station, friend), List.of(), List.of());
	}

	private List<Building> createBuildings() {
		List<Building> result = new ArrayList<>();
		Building tubbyHouse = new Building(
			"tubby_angel_house", "Tubby Angel's House", "tubby_angel_house_interior",
			1050f, 1400f, 300f, 240f, 140f,
			520f, 320f, 50f, 80f, 470f, 80f);
		Building olderSisterHouse = new Building(
			"older_sister_house", "Older Sister's House", "older_sister_house_interior",
			1900f, 1400f, 320f, 240f, 140f,
			620f, 320f, 50f, 80f, 570f, 80f);
		Building chapel = ChapelInteriorData.createChapelBuilding();
		addTubbyHousehold(tubbyHouse.getInterior());
		addOlderSisterHousehold(olderSisterHouse.getInterior());
		result.add(tubbyHouse);
		result.add(olderSisterHouse);
		result.add(chapel);
		if (Gdx.app != null) {
			for (Building building : result) {
				Gdx.app.log("BuildingDebug", "Registered building: " + building.getName()
					+ " entrance=" + building.getEntrance().getInteractionPosition());
			}
		}
		return result;
	}

	private List<Interactable> createInteractables() {
		List<Interactable> worldInteractables = new ArrayList<>();
		worldInteractables.add(new PrototypeNpc("retreat_local_guide", "Local Guide", 1420f, 1260f,
			"Welcome to Retreat.", DialogueRepository.createFirstStepsDialogue()));
		worldInteractables.add(new PrototypeNpc("retreat_neighbour", "Neighbour", 1810f, 1190f,
			"Howzit, neighbour.", DialogueRepository.createMeetTheNeighbourDialogue()));
		worldInteractables.addAll(worldItems);
		for (Building building : buildings) {
			worldInteractables.add(building.getEntrance());
		}
		worldInteractables.add(retreatStationTravelPoint);
		return worldInteractables;
	}

	private void addTubbyHousehold(Interior interior) {
		interior.addInteractable(createHouseholdNpc("retreat_tubby", "Tubby", 100f, interior,
			"Welcome home.", DialogueRepository.createTubbyAngelDialogue()));
		interior.addInteractable(createHouseholdNpc("retreat_tubby_father", "Tubby's Father", 180f, interior,
			"Make yourself comfortable.", null));
		interior.addInteractable(createHouseholdNpc("retreat_tubby_mother", "Tubby's Mother", 260f, interior,
			"There is always room at the table.", null));
		interior.addInteractable(createHouseholdNpc("retreat_tubby_police_sister", "Tubby's Policewoman Sister", 340f, interior,
			"Stay alert when you are out there.", null));
		interior.addInteractable(createHouseholdNpc("retreat_tubby_brother", "Tubby's Brother", 410f, interior,
			"Good to meet you.", null));
	}

	private void addOlderSisterHousehold(Interior interior) {
		String[] ids = {"retreat_older_sister", "retreat_older_sister_husband", "retreat_older_sister_son",
			"retreat_older_sister_daughter_1", "retreat_older_sister_daughter_2", "retreat_cousin_1", "retreat_cousin_2"};
		String[] names = {"Older Sister", "Her Husband", "Her Son", "Her Daughter 1", "Her Daughter 2", "Cousin 1", "Cousin 2"};
		for (int i = 0; i < ids.length; i++) {
			interior.addInteractable(createHouseholdNpc(ids[i], names[i], 90f + i * 70f, interior,
				"Welcome to the family home.", null));
		}
	}

	private PrototypeNpc createHouseholdNpc(String id, String name, float x, Interior interior, String text,
			com.weskaap.game.dialogue.Dialogue dialogue) {
		return new PrototypeNpc(id, name, x, interior.getPlayerSpawn().y, text,
			dialogue != null ? dialogue : DialogueRepository.createPlaceholderDialogue(id + "_dialogue", name, text));
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

		Quest wayForward = QuestRepository.getQuest("tubby_angel_001");
		if (wayForward != null) {
			hero.getQuestLog().add(wayForward);
		}

		QuestRepository.register(ChapelInteriorData.createChapelQuest());
		Quest chapelQuest = QuestRepository.getQuest("act1_l1_chapel");
		if (chapelQuest != null) {
			hero.getQuestLog().add(chapelQuest);
		}
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
		PrototypeEnemy firstEnemy = new PrototypeEnemy("retreat_enemy", 900f, 1000f, 100, EnemyAiController.ENEMY_SPEED);
		firstEnemy.setLootTable(basicLoot);
		PrototypeEnemy secondEnemy = new PrototypeEnemy("retreat_enemy", 2500f, 1800f, 100, EnemyAiController.ENEMY_SPEED);
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

	private List<Rectangle> createActiveCollisionBounds() {
		List<Rectangle> worldCollisionBounds = new ArrayList<>(activeObstacles);
		if (activeBuilding == null) {
			for (Building building : buildings) {
				worldCollisionBounds.addAll(building.getWallObstacles());
			}
		}
		for (Interactable interactable : activeInteractables) {
			if (interactable instanceof BuildingEntrance || interactable instanceof BuildingExit) {
				continue;
			}
			worldCollisionBounds.add(interactable.getBounds());
		}
		if (activeBuilding == null || isCombatAllowedInActiveBuilding()) {
			for (PrototypeEnemy enemy : enemies) {
				worldCollisionBounds.add(enemy.getCombatBounds());
			}
		}
		return worldCollisionBounds;
	}

	private void handleNpcInteraction(PrototypeNpc npc) {
		if (rosebankFriendAlly.getNpcId().equals(npc.getId())) {
			rosebankFriendAlly.establishAlliance();
		}
		questController.handleEvent(new QuestEvent(QuestObjectiveType.TALK_TO_NPC, npc.getId()));
		List<Quest> availableQuests = npc.getAvailableQuests(questController);
		if (!availableQuests.isEmpty()) {
			dialogueController.startDialogue(DialogueRepository.createQuestOfferDialogue(npc.getName(), availableQuests.get(0)));
			return;
		}
		List<Quest> activeQuests = npc.getActiveQuests(questController);
		if (!activeQuests.isEmpty()) {
			dialogueController.startDialogue(DialogueRepository.createQuestStatusDialogue(npc.getName(), activeQuests.get(0)));
			return;
		}
		List<Quest> completedQuests = npc.getCompletedQuests(questController);
		if (!completedQuests.isEmpty()) {
			dialogueController.startDialogue(DialogueRepository.createQuestStatusDialogue(npc.getName(), completedQuests.get(0)));
			return;
		}
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
		if (questController.isQuestCompleted(questId)) {
			questMessage = "Quest already completed: " + quest.getTitle();
			questMessageTimer = QUEST_MESSAGE_DURATION;
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

	private void enterBuilding(Building building) {
		questController.handleEvent(new QuestEvent(QuestObjectiveType.ENTER_BUILDING, building.getId()));
		if (Gdx.app != null) {
			Gdx.app.log("BuildingDebug", "Entering building: " + building.getId());
			Gdx.app.log("BuildingDebug", "Interior loaded: " + building.getInterior().getId());
		}
		exteriorHeroPosition.set(hero.getPosition());
		activeBuilding = building;
		activeObstacles = building.getInterior().getObstacles();
		activeInteractables = building.getInterior().getInteractables();
		if (building.getInterior().isCombatAllowed()) {
			swapToInteriorEnemies(building.getInterior());
		}
		Vector2 spawn = building.getInterior().getPlayerSpawn();
		hero.setPosition(spawn.x, spawn.y);
		platformerController.reset(hero, building.getInterior());
		activeCollisionBounds = createActiveCollisionBounds();
		currentInteractable = null;
		if (ChapelInteriorData.BUILDING_ID.equals(building.getId())) {
			dialogueController.startDialogue(ChapelInteriorData.createChapelIntroDialogue());
		}
		interactionMessage = "Entered " + building.getId();
		interactionMessageTimer = MESSAGE_DURATION;
		if (Gdx.app != null) {
			Gdx.app.log("BuildingDebug", "Interior spawn: " + hero.getPosition());
		}
	}

	private void exitBuilding() {
		if (activeBuilding == null) {
			return;
		}
		Building building = activeBuilding;
		questController.handleEvent(new QuestEvent(QuestObjectiveType.LEAVE_BUILDING, building.getId()));
		Vector2 returnPosition = building.getExteriorReturnPosition();
		if (Gdx.app != null) {
			Gdx.app.log("BuildingDebug", "Exiting building: " + building.getId());
			Gdx.app.log("BuildingDebug", "Returning exterior position: " + returnPosition);
		}
		if (building.getInterior().isCombatAllowed()) {
			restoreExteriorEnemies();
		}
		activeBuilding = null;
		activeObstacles = exteriorObstacles;
		activeInteractables = exteriorInteractables;
		hero.setPosition(returnPosition.x, returnPosition.y);
		activeCollisionBounds = createActiveCollisionBounds();
		currentInteractable = null;
		interactionMessage = "Exited " + building.getId();
		interactionMessageTimer = MESSAGE_DURATION;
	}

	private void requestAreaTransition(AreaId destination) {
		AreaDefinition area = areaRegistry.get(destination);
		if (area == null) return;
		TravelPoint departure = getActiveTravelPoint();
		if (departure != null) {
			questController.handleEvent(new QuestEvent(QuestObjectiveType.REACH_LOCATION, departure.getId()));
		}
		AreaId previousArea = activeArea;
		questController.handleEvent(new QuestEvent(QuestObjectiveType.LEAVE_AREA, previousArea.name()));
		requestedArea = destination;
		loadArea(area);
		questController.handleEvent(new QuestEvent(QuestObjectiveType.ENTER_AREA, destination.name()));
		interactionMessage = "Arrived in " + area.getDisplayName();
		interactionMessageTimer = MESSAGE_DURATION;
		if (Gdx.app != null) {
			Gdx.app.log("AreaDebug", "Transition requested: " + previousArea + " -> " + destination);
			Gdx.app.log("AreaDebug", "Area loaded: " + area.getDisplayName());
		}
	}

	private void loadArea(AreaDefinition area) {
		activeArea = area.getId();
		activeAreaDefinition = area;
		travelController.setCurrentArea(activeArea);
		activeBuilding = null;
		exteriorObstacles.clear();
		exteriorObstacles.addAll(area.getObstacles());
		retreatRoads.clear();
		retreatRoads.addAll(area.getGroundFeatures());
		buildings.clear();
		buildings.addAll(area.getBuildings());
		exteriorInteractables.clear();
		exteriorInteractables.addAll(area.getInteractables());
		worldItems.clear();
		worldItems.addAll(area.getItems());
		enemies.clear();
		enemies.addAll(area.getEnemies());
		enemyAiControllers.clear();
		enemyAiControllers.addAll(createEnemyAiControllers());
		activeObstacles = exteriorObstacles;
		activeInteractables = exteriorInteractables;
		Vector2 spawn = area.getSpawnPoint();
		hero.setPosition(spawn.x, spawn.y);
		exteriorHeroPosition.set(spawn);
		activeCollisionBounds = createActiveCollisionBounds();
		currentInteractable = null;
		previousInteractable = null;
	}

	private void collectItem(PrototypeItem prototypeItem) {
		if (hero.getInventory().add(prototypeItem.getItem())) {
			questController.handleEvent(new QuestEvent(QuestObjectiveType.COLLECT_ITEM, prototypeItem.getItem().getId()));
			worldItems.remove(prototypeItem);
			exteriorInteractables.remove(prototypeItem);
			activeCollisionBounds.remove(prototypeItem.getBounds());
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
		for (Interactable interactable : getInteractables()) {
			float distance = interactable.getInteractionPosition().dst2(hero.getPosition());
			if (interactable.canInteract(hero) && distance < nearestDistance) {
				nearest = interactable;
				nearestDistance = distance;
			}
		}
		currentInteractableDistance = (nearest != null) ? (float) Math.sqrt(nearestDistance) : -1f;
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
		List<Rectangle> otherCollisionBounds = new ArrayList<>(activeCollisionBounds);
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
				questController.handleEvent(new QuestEvent(QuestObjectiveType.DEFEAT_ENEMY, enemy.getId()));
				if (!enemy.hasDroppedLoot() && enemy.getLootTable() != null) {
					dropLoot(enemy);
				}
				activeCollisionBounds.remove(enemy.getCombatBounds());
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
			exteriorInteractables.add(prototypeItem);
			activeCollisionBounds.add(prototypeItem.getBounds());
		}
	}

	private boolean isCombatAllowedInActiveBuilding() {
		return activeBuilding != null && activeBuilding.getInterior().isCombatAllowed();
	}

	private void swapToInteriorEnemies(Interior interior) {
		savedExteriorEnemies.clear();
		savedExteriorEnemies.addAll(enemies);
		savedExteriorEnemyAiControllers.clear();
		savedExteriorEnemyAiControllers.addAll(enemyAiControllers);
		enemies.clear();
		enemyAiControllers.clear();
		for (PrototypeEnemy enemy : interior.getInteriorEnemies()) {
			enemies.add(enemy);
			enemyAiControllers.add(new EnemyAiController(enemy));
		}
	}

	private void restoreExteriorEnemies() {
		enemies.clear();
		enemyAiControllers.clear();
		enemies.addAll(savedExteriorEnemies);
		enemyAiControllers.addAll(savedExteriorEnemyAiControllers);
		savedExteriorEnemies.clear();
		savedExteriorEnemyAiControllers.clear();
	}

	public void dispose() {
		shapeRenderer.dispose();
		worldRenderer3D.dispose();
	}
}
