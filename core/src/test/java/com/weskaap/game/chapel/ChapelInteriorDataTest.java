package com.weskaap.game.chapel;

import com.badlogic.gdx.math.Rectangle;
import com.weskaap.game.building.Building;
import com.weskaap.game.building.BuildingEntrance;
import com.weskaap.game.building.Interior;
import com.weskaap.game.dialogue.Dialogue;
import com.weskaap.game.enemy.PrototypeEnemy;
import com.weskaap.game.interaction.Interactable;
import com.weskaap.game.quest.Quest;
import com.weskaap.game.quest.QuestController;
import com.weskaap.game.quest.QuestEvent;
import com.weskaap.game.quest.QuestLog;
import com.weskaap.game.quest.QuestObjectiveType;
import com.weskaap.game.quest.QuestRepository;
import com.weskaap.game.quest.QuestRewardService;
import com.weskaap.game.story.StoryState;
import com.weskaap.game.economy.Wallet;
import com.weskaap.game.inventory.Inventory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChapelInteriorDataTest {

    @Test
    void chapelBuildingIsCreatedWithExpectedFootprint() {
        Building chapel = ChapelInteriorData.createChapelBuilding();

        assertEquals(ChapelInteriorData.BUILDING_ID, chapel.getId());
        assertEquals("Retreat Chapel", chapel.getName());
        assertEquals(ChapelInteriorData.EXTERIOR_X, chapel.getPosition().x);
        assertEquals(ChapelInteriorData.EXTERIOR_Y, chapel.getPosition().y);
        assertEquals(ChapelInteriorData.EXTERIOR_WIDTH, chapel.getWidth());
        assertEquals(ChapelInteriorData.EXTERIOR_DEPTH, chapel.getDepth());
        assertNotNull(chapel.getEntrance());
        assertNotNull(chapel.getInterior());
    }

    @Test
    void chapelInteriorSupportsCombatAndHasEnemies() {
        Building chapel = ChapelInteriorData.createChapelBuilding();
        Interior interior = chapel.getInterior();

        assertTrue(interior.isCombatAllowed(), "Chapel must allow interior combat");
        assertEquals(1, interior.getInteriorEnemies().size(), "Chapel should contain one tutorial enemy");

        PrototypeEnemy rat = interior.getInteriorEnemies().get(0);
        assertEquals("chapel_rat", rat.getId());
        assertTrue(rat.isAlive());
    }

    @Test
    void chapelInteriorHasExpectedRoomsAndObstacles() {
        Building chapel = ChapelInteriorData.createChapelBuilding();
        Interior interior = chapel.getInterior();

        assertEquals(ChapelInteriorData.INTERIOR_WIDTH, interior.getWidth());
        assertEquals(ChapelInteriorData.INTERIOR_HEIGHT, interior.getHeight());

        // Walls (4) + added chapel obstacles
        assertTrue(interior.getObstacles().size() > 4, "Chapel should add internal collision obstacles");
        assertFalse(interior.getPlatforms().isEmpty(), "Chapel must have platforms");
    }

    @Test
    void chapelInteriorHasRequiredInteractables() {
        Building chapel = ChapelInteriorData.createChapelBuilding();
        Interior interior = chapel.getInterior();

        boolean hasAltar = false;
        boolean hasStorage = false;
        boolean hasPrayerClue = false;
        boolean hasBasementDiscovery = false;
        boolean hasExit = false;

        for (Interactable interactable : interior.getInteractables()) {
            String id = interactable.getId();
            if ("retreat_chapel_altar".equals(id)) hasAltar = true;
            if ("retreat_chapel_prayer_clue".equals(id)) hasPrayerClue = true;
            if ("retreat_chapel_basement_discovery".equals(id)) hasBasementDiscovery = true;
            if (interactable instanceof com.weskaap.game.building.BuildingExit) hasExit = true;
            if (interactable instanceof ChapelInteriorData.ChapelStorageCrate) hasStorage = true;
        }

        assertTrue(hasExit, "Chapel must have an exit");
        assertTrue(hasAltar, "Chapel must have an altar interactable");
        assertTrue(hasStorage, "Chapel must have a storage crate");
        assertTrue(hasPrayerClue, "Chapel must have a prayer room clue");
        assertTrue(hasBasementDiscovery, "Chapel must have a basement discovery interactable");
    }

    @Test
    void chapelInteractablesHaveBoundsAndPrompts() {
        Building chapel = ChapelInteriorData.createChapelBuilding();

        boolean foundAltar = false;
        for (Interactable interactable : chapel.getInterior().getInteractables()) {
            if ("retreat_chapel_altar".equals(interactable.getId())) {
                foundAltar = true;
                assertNotNull(interactable.getBounds());
                assertTrue(interactable.getBounds().width > 0);
                assertTrue(interactable.getBounds().height > 0);
                assertFalse(interactable.getPromptText().isBlank());
                assertFalse(interactable.interact().isBlank());
            }
        }
        assertTrue(foundAltar);
    }

    @Test
    void chapelQuestIsRegisteredAndProgressesWithEvents() {
        QuestRepository.register(ChapelInteriorData.createChapelQuest());
        Quest quest = QuestRepository.getQuest("act1_l1_chapel");
        assertNotNull(quest);
        assertEquals(5, quest.getObjectives().size());

        QuestLog log = new QuestLog();
        log.add(quest);
        QuestRewardService rewardService = new QuestRewardService(new Wallet(), new Inventory(), new StoryState());
        QuestController controller = new QuestController(log, rewardService);
        controller.startQuest("act1_l1_chapel");

        assertTrue(controller.handleEvent(new QuestEvent(QuestObjectiveType.ENTER_BUILDING, ChapelInteriorData.BUILDING_ID)));
        assertTrue(controller.isQuestActive("act1_l1_chapel"));

        assertTrue(controller.handleEvent(new QuestEvent(QuestObjectiveType.DEFEAT_ENEMY, "chapel_rat")));
        assertTrue(controller.handleEvent(new QuestEvent(QuestObjectiveType.INTERACT_WITH_OBJECT, "retreat_chapel_altar")));
        assertTrue(controller.handleEvent(new QuestEvent(QuestObjectiveType.INTERACT_WITH_OBJECT, "retreat_chapel_prayer_clue")));
        assertTrue(controller.handleEvent(new QuestEvent(QuestObjectiveType.INTERACT_WITH_OBJECT, "retreat_chapel_basement_discovery")));

        assertTrue(controller.isQuestCompleted("act1_l1_chapel"));
    }

    @Test
    void altarInteractionAdvancesChapelQuest() {
        QuestRepository.register(ChapelInteriorData.createChapelQuest());
        QuestLog log = new QuestLog();
        log.add(QuestRepository.getQuest("act1_l1_chapel"));
        QuestController controller = new QuestController(log);
        controller.startQuest("act1_l1_chapel");

        controller.handleEvent(new QuestEvent(QuestObjectiveType.ENTER_BUILDING, ChapelInteriorData.BUILDING_ID));
        controller.handleEvent(new QuestEvent(QuestObjectiveType.DEFEAT_ENEMY, "chapel_rat"));

        assertTrue(controller.handleEvent(
            new QuestEvent(QuestObjectiveType.INTERACT_WITH_OBJECT, "retreat_chapel_altar")));

        Quest quest = controller.getQuest("act1_l1_chapel");
        assertNotNull(quest);
        boolean altarComplete = quest.getObjectives().stream()
            .filter(o -> "inspect_altar".equals(o.getId()))
            .findFirst()
            .map(com.weskaap.game.quest.QuestObjective::isComplete)
            .orElse(false);
        assertTrue(altarComplete, "Inspecting the altar should complete the altar objective");
    }

    @Test
    void chapelIntroDialogueHasNarrativeText() {
        Dialogue intro = ChapelInteriorData.createChapelIntroDialogue();
        assertNotNull(intro);
        assertNotNull(intro.getStartingNode());
        assertFalse(intro.getStartingNode().getText().isBlank());
    }

    @Test
    void chapelQuestRewardsIncludeStoryFlagAndMoney() {
        Quest quest = ChapelInteriorData.createChapelQuest();
        assertEquals(2, quest.getRewards().size());
    }

    @Test
    void storageCrateGrantsPotionOnlyOnFirstInteraction() {
        Building chapel = ChapelInteriorData.createChapelBuilding();
        ChapelInteriorData.ChapelStorageCrate crate = findStorageCrate(chapel);

        assertFalse(crate.isLooted());
        assertEquals("A crate of supplies. You take a healing potion.", crate.interact());

        crate.markLooted();
        assertTrue(crate.isLooted());
        assertEquals("The crate is empty. Someone else has already taken the supplies.", crate.interact());
    }

    @Test
    void chapelQuestCompletionGrantsStoryFlagAndMoneyOnce() {
        QuestRepository.register(ChapelInteriorData.createChapelQuest());
        Quest quest = QuestRepository.getQuest("act1_l1_chapel");
        QuestLog log = new QuestLog();
        log.add(quest);

        Wallet wallet = new Wallet();
        StoryState storyState = new StoryState();
        QuestRewardService rewardService = new QuestRewardService(wallet, new Inventory(), storyState);
        QuestController controller = new QuestController(log, rewardService);
        controller.startQuest("act1_l1_chapel");

        controller.handleEvent(new QuestEvent(QuestObjectiveType.ENTER_BUILDING, ChapelInteriorData.BUILDING_ID));
        controller.handleEvent(new QuestEvent(QuestObjectiveType.DEFEAT_ENEMY, "chapel_rat"));
        controller.handleEvent(new QuestEvent(QuestObjectiveType.INTERACT_WITH_OBJECT, "retreat_chapel_altar"));
        controller.handleEvent(new QuestEvent(QuestObjectiveType.INTERACT_WITH_OBJECT, "retreat_chapel_prayer_clue"));
        controller.handleEvent(new QuestEvent(QuestObjectiveType.INTERACT_WITH_OBJECT, "retreat_chapel_basement_discovery"));

        assertTrue(controller.isQuestCompleted("act1_l1_chapel"));
        assertTrue(storyState.isSet(com.weskaap.game.story.StoryFlag.SUPERNATURAL_MYSTERY_INTRODUCED));
        assertEquals(15, wallet.getBalance());

        int balanceAfterFirstCompletion = wallet.getBalance();
        controller.handleEvent(new QuestEvent(QuestObjectiveType.ENTER_BUILDING, ChapelInteriorData.BUILDING_ID));
        assertEquals(balanceAfterFirstCompletion, wallet.getBalance(), "Rewards should not be granted repeatedly");
    }

    @Test
    void interiorCombatIsAllowedAndContainsOneRat() {
        Building chapel = ChapelInteriorData.createChapelBuilding();
        Interior interior = chapel.getInterior();

        assertTrue(interior.isCombatAllowed());
        assertEquals(1, interior.getInteriorEnemies().size());
        assertEquals("chapel_rat", interior.getInteriorEnemies().get(0).getId());
    }

    @Test
    void exteriorReturnPositionIsDerivedFromEntrance() {
        Building chapel = ChapelInteriorData.createChapelBuilding();
        BuildingEntrance entrance = chapel.getEntrance();

        assertNotNull(entrance);
        assertEquals(entrance.getInteractionPosition().x, chapel.getExteriorReturnPosition().x);
        assertEquals(entrance.getInteractionPosition().y - com.weskaap.game.building.Building.DOOR_RETURN_OFFSET,
            chapel.getExteriorReturnPosition().y);
    }

    private ChapelInteriorData.ChapelStorageCrate findStorageCrate(Building chapel) {
        for (com.weskaap.game.interaction.Interactable interactable : chapel.getInterior().getInteractables()) {
            if (interactable instanceof ChapelInteriorData.ChapelStorageCrate) {
                return (ChapelInteriorData.ChapelStorageCrate) interactable;
            }
        }
        fail("Storage crate not found in chapel interior");
        return null;
    }
}
