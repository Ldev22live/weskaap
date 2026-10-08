package com.weskaap.game.chapel;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.weskaap.game.building.Building;
import com.weskaap.game.building.Interior;
import com.weskaap.game.building.InteriorPlatform;
import com.weskaap.game.dialogue.Dialogue;
import com.weskaap.game.dialogue.DialogueNode;
import com.weskaap.game.dialogue.DialogueOption;
import com.weskaap.game.enemy.EnemyAiController;
import com.weskaap.game.enemy.PrototypeEnemy;
import com.weskaap.game.interaction.Interactable;
import com.weskaap.game.item.Item;
import com.weskaap.game.item.ItemType;
import com.weskaap.game.quest.QuestReward;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ChapelInteriorData {

    private ChapelInteriorData() {
    }

    public static final String BUILDING_ID = "retreat_chapel";
    public static final String INTERIOR_ID = "retreat_chapel_interior";

    // Exterior footprint
    public static final float EXTERIOR_X = 1550f;
    public static final float EXTERIOR_Y = 950f;
    public static final float EXTERIOR_WIDTH = 260f;
    public static final float EXTERIOR_DEPTH = 200f;
    public static final float EXTERIOR_HEIGHT = 150f;

    // Interior world units
    public static final float INTERIOR_WIDTH = 680f;
    public static final float INTERIOR_HEIGHT = 480f;
    public static final float SPAWN_X = 80f;
    public static final float SPAWN_Y = 80f;
    public static final float EXIT_X = 340f;
    public static final float EXIT_Y = 40f;

    public static Building createChapelBuilding() {
        Building chapel = new Building(
            BUILDING_ID,
            "Retreat Chapel",
            INTERIOR_ID,
            EXTERIOR_X, EXTERIOR_Y,
            EXTERIOR_WIDTH, EXTERIOR_DEPTH, EXTERIOR_HEIGHT,
            INTERIOR_WIDTH, INTERIOR_HEIGHT,
            SPAWN_X, SPAWN_Y,
            EXIT_X, EXIT_Y);
        configureInterior(chapel.getInterior());
        return chapel;
    }

    private static void configureInterior(Interior interior) {
        interior.setCombatAllowed(true);
        addPlatforms(interior);
        addObstacles(interior);
        addInteractables(interior);
        addEnemy(interior);
    }

    private static void addPlatforms(Interior interior) {
        // Main chapel floor
        interior.addPlatform(new InteriorPlatform(0f, 0f, 680f, 24f));
        // Raised altar platform
        interior.addPlatform(new InteriorPlatform(540f, 60f, 120f, 20f));
        // Side hall step
        interior.addPlatform(new InteriorPlatform(0f, 240f, 160f, 20f));
        // Prayer room floor
        interior.addPlatform(new InteriorPlatform(0f, 320f, 240f, 24f));
        // Basement steps
        interior.addPlatform(new InteriorPlatform(280f, 280f, 120f, 16f));
        // Basement floor
        interior.addPlatform(new InteriorPlatform(360f, 320f, 280f, 24f));
    }

    public static void addObstacles(Interior interior) {
        // Altar rail
        interior.addObstacle(new Rectangle(500f, 50f, 20f, 100f));
        // Central pew block
        interior.addObstacle(new Rectangle(180f, 100f, 80f, 220f));
        // Storage shelves
        interior.addObstacle(new Rectangle(20f, 260f, 60f, 80f));
        // Prayer room screen
        interior.addObstacle(new Rectangle(180f, 360f, 20f, 80f));
        // Basement support pillar
        interior.addObstacle(new Rectangle(460f, 380f, 40f, 40f));
    }

    private static void addInteractables(Interior interior) {
        // Altar interaction
        interior.addInteractable(new ChapelAltar());
        // Storage interactable
        interior.addInteractable(new ChapelStorageCrate());
        // Prayer room clue
        interior.addInteractable(new ChapelPrayerClue());
        // Basement discovery
        interior.addInteractable(new ChapelBasementDiscovery());
    }

    private static void addEnemy(Interior interior) {
        PrototypeEnemy rat = new PrototypeEnemy("chapel_rat", 320f, 160f, 40, EnemyAiController.ENEMY_SPEED);
        interior.addInteriorEnemy(rat);
    }

    public static Dialogue createChapelIntroDialogue() {
        DialogueNode node = new DialogueNode(
            "intro",
            "Chapel",
            "The chapel is quiet. Dust hangs in the light from the broken window. Something feels wrong here.",
            List.of());
        return new Dialogue("retreat-chapel-intro", node.getId(), Map.of(node.getId(), node));
    }

    public static Dialogue createAltarDialogue() {
        Map<String, DialogueNode> nodes = new HashMap<>();
        DialogueNode node1 = new DialogueNode(
            "altar-1",
            "Altar",
            "The altar cloth is torn. A faint scratch mark leads toward the side hall.",
            List.of(new DialogueOption("Inspect the scratch.", "altar-2")));
        DialogueNode node2 = new DialogueNode(
            "altar-2",
            "Altar",
            "Something was dragged from the altar toward the storage hall.",
            List.of());
        nodes.put(node1.getId(), node1);
        nodes.put(node2.getId(), node2);
        return new Dialogue("retreat-chapel-altar", node1.getId(), nodes);
    }

    public static Dialogue createPrayerClueDialogue() {
        DialogueNode node = new DialogueNode(
            "prayer-clue",
            "Prayer Room",
            "A journal page reads: 'The noises come from below the chapel floor. They started after the visitor came.'",
            List.of());
        return new Dialogue("retreat-chapel-prayer", node.getId(), Map.of(node.getId(), node));
    }

    public static Dialogue createBasementDiscoveryDialogue() {
        DialogueNode node = new DialogueNode(
            "basement-discovery",
            "Basement",
            "Old crates and a fresh footprint in the dust. Someone has been here recently. The trail leads back out of the chapel.",
            List.of());
        return new Dialogue("retreat-chapel-basement", node.getId(), Map.of(node.getId(), node));
    }

    public static com.weskaap.game.quest.Quest createChapelQuest() {
        return new com.weskaap.game.quest.Quest(
            "act1_l1_chapel",
            "The Chapel",
            "Investigate the Retreat Chapel and discover what has disturbed it.",
            "retreat_chapel",
            List.of(
                new com.weskaap.game.quest.QuestObjective("enter_chapel", "Enter the Retreat Chapel.",
                    com.weskaap.game.quest.QuestObjectiveType.ENTER_BUILDING, BUILDING_ID, 1),
                new com.weskaap.game.quest.QuestObjective("defeat_chapel_rat", "Defeat the creature in the chapel.",
                    com.weskaap.game.quest.QuestObjectiveType.DEFEAT_ENEMY, "chapel_rat", 1),
                new com.weskaap.game.quest.QuestObjective("inspect_altar", "Inspect the altar.",
                    com.weskaap.game.quest.QuestObjectiveType.INTERACT_WITH_OBJECT, "retreat_chapel_altar", 1),
                new com.weskaap.game.quest.QuestObjective("find_prayer_clue", "Find the prayer room clue.",
                    com.weskaap.game.quest.QuestObjectiveType.INTERACT_WITH_OBJECT, "retreat_chapel_prayer_clue", 1),
                new com.weskaap.game.quest.QuestObjective("discover_basement", "Discover the basement trail.",
                    com.weskaap.game.quest.QuestObjectiveType.INTERACT_WITH_OBJECT, "retreat_chapel_basement_discovery", 1)
            ),
            List.of(),
            null,
            List.of(
                QuestReward.storyFlag(com.weskaap.game.story.StoryFlag.SUPERNATURAL_MYSTERY_INTRODUCED),
                QuestReward.money(15)
            ));
    }

    // Chapel-specific interactables

    public static class ChapelAltar implements Interactable {
        private static final float SIZE = 40f;
        private static final float RANGE = 72f;
        private final Rectangle bounds;

        public ChapelAltar() {
            this.bounds = new Rectangle(560f, 70f, SIZE, SIZE);
        }

        @Override
        public Vector2 getInteractionPosition() {
            return new Vector2(bounds.x + bounds.width / 2f, bounds.y + bounds.height / 2f);
        }

        @Override
        public Rectangle getBounds() {
            return bounds;
        }

        @Override
        public float getInteractionRange() {
            return RANGE;
        }

        @Override
        public String interact() {
            return "The altar has been disturbed.";
        }

        @Override
        public String getPromptText() {
            return "Inspect Altar";
        }

        @Override
        public String getId() {
            return "retreat_chapel_altar";
        }
    }

    public static class ChapelStorageCrate implements Interactable {
        private static final float SIZE = 32f;
        private static final float RANGE = 64f;
        private final Rectangle bounds;
        private final Item contents;
        private boolean looted;

        public ChapelStorageCrate() {
            this.bounds = new Rectangle(90f, 270f, SIZE, SIZE);
            this.contents = new Item("healing-potion", "Healing Potion", "A simple restorative tonic.",
                ItemType.CONSUMABLE, true, 20, 1);
            this.looted = false;
        }

        @Override
        public Vector2 getInteractionPosition() {
            return new Vector2(bounds.x + bounds.width / 2f, bounds.y + bounds.height / 2f);
        }

        @Override
        public Rectangle getBounds() {
            return bounds;
        }

        @Override
        public float getInteractionRange() {
            return RANGE;
        }

        @Override
        public String interact() {
            if (looted) {
                return "The crate is empty. Someone else has already taken the supplies.";
            }
            return "A crate of supplies. You take a healing potion.";
        }

        @Override
        public String getPromptText() {
            return looted ? "Search Crate" : "Search Crate";
        }

        public boolean isLooted() {
            return looted;
        }

        public void markLooted() {
            this.looted = true;
        }

        public Item getContents() {
            return contents;
        }
    }

    public static class ChapelPrayerClue implements Interactable {
        private static final float SIZE = 32f;
        private static final float RANGE = 64f;
        private final Rectangle bounds;

        public ChapelPrayerClue() {
            this.bounds = new Rectangle(60f, 400f, SIZE, SIZE);
        }

        @Override
        public Vector2 getInteractionPosition() {
            return new Vector2(bounds.x + bounds.width / 2f, bounds.y + bounds.height / 2f);
        }

        @Override
        public Rectangle getBounds() {
            return bounds;
        }

        @Override
        public float getInteractionRange() {
            return RANGE;
        }

        @Override
        public String interact() {
            return "A torn journal page mentions noises from below.";
        }

        @Override
        public String getPromptText() {
            return "Read Journal";
        }

        @Override
        public String getId() {
            return "retreat_chapel_prayer_clue";
        }
    }

    public static class ChapelBasementDiscovery implements Interactable {
        private static final float SIZE = 40f;
        private static final float RANGE = 72f;
        private final Rectangle bounds;

        public ChapelBasementDiscovery() {
            this.bounds = new Rectangle(480f, 400f, SIZE, SIZE);
        }

        @Override
        public Vector2 getInteractionPosition() {
            return new Vector2(bounds.x + bounds.width / 2f, bounds.y + bounds.height / 2f);
        }

        @Override
        public Rectangle getBounds() {
            return bounds;
        }

        @Override
        public float getInteractionRange() {
            return RANGE;
        }

        @Override
        public String interact() {
            return "Fresh footprints in the dust. Someone came through the basement.";
        }

        @Override
        public String getPromptText() {
            return "Inspect Tracks";
        }

        @Override
        public String getId() {
            return "retreat_chapel_basement_discovery";
        }
    }
}
