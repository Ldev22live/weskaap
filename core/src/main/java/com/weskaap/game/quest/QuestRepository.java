package com.weskaap.game.quest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class QuestRepository {
    private static final Map<String, Quest> QUEST_DATABASE = new HashMap<>();

    static {
        register(new Quest("tubby_angel_001", "The Way Forward",
            "Speak with Tubby Angel and learn what lies ahead.",
            List.of(objective("talk_to_tubby", "Speak with Tubby Angel.", QuestObjectiveType.TALK_TO_NPC, "tubby_angel"))));
        register(retreatQuest("retreat_tubby_welcome", "A Word with Father",
            "Speak with Tubby's father.", "retreat_tubby", "talk_tubby_father", "Speak with Tubby's father.",
            QuestObjectiveType.TALK_TO_NPC, "retreat_tubby_father", List.of(), "retreat_station_route"));
        register(retreatQuest("retreat_station_route", "The Route Ahead",
            "Visit Retreat Train Station.", "retreat_tubby", "visit_retreat_station", "Visit Retreat Train Station.",
            QuestObjectiveType.REACH_LOCATION, "retreat_station_travel", List.of("retreat_tubby_welcome"), null));
        register(retreatQuest("retreat_father_errand", "Check on the Family",
            "Speak with Tubby's mother.", "retreat_tubby_father", "talk_tubby_mother", "Speak with Tubby's mother.",
            QuestObjectiveType.TALK_TO_NPC, "retreat_tubby_mother", List.of(), null));
        register(retreatQuest("retreat_mother_supplies", "Household Supplies",
            "Collect a healing potion.", "retreat_tubby_mother", "collect_healing_potion", "Collect a healing potion.",
            QuestObjectiveType.COLLECT_ITEM, "healing-potion", List.of(), null));
        register(retreatQuest("retreat_police_sister_patrol", "A Safer Street",
            "Defeat one hostile creature outside.", "retreat_tubby_police_sister", "defeat_retreat_enemy",
            "Defeat one hostile creature.", QuestObjectiveType.DEFEAT_ENEMY, "retreat_enemy", List.of(), null));
        register(retreatQuest("retreat_brother_station", "Find the Station",
            "Visit Retreat Train Station.", "retreat_tubby_brother", "brother_visit_station", "Visit Retreat Train Station.",
            QuestObjectiveType.REACH_LOCATION, "retreat_station_travel", List.of(), null));
        register(retreatQuest("retreat_older_sister_family", "Meet the Cousins",
            "Speak with Cousin 1.", "retreat_older_sister", "talk_cousin_1", "Speak with Cousin 1.",
            QuestObjectiveType.TALK_TO_NPC, "retreat_cousin_1", List.of(), null));
    }

    private QuestRepository() {
    }

    public static void register(Quest quest) {
        if (quest != null && !quest.getId().isBlank()) QUEST_DATABASE.put(quest.getId(), quest);
    }

    public static Quest getQuest(String id) {
        if (id == null || id.isBlank()) return null;
        Quest prototype = QUEST_DATABASE.get(id);
        return prototype == null ? null : copyOf(prototype);
    }

    public static List<Quest> getAllQuests() {
        List<Quest> copies = new ArrayList<>();
        for (Quest prototype : QUEST_DATABASE.values()) copies.add(copyOf(prototype));
        return List.copyOf(copies);
    }

    public static List<Quest> getQuestsForNpc(String npcId) {
        List<Quest> quests = new ArrayList<>();
        for (Quest prototype : QUEST_DATABASE.values()) {
            if (npcId != null && npcId.equals(prototype.getQuestGiverNpcId())) quests.add(copyOf(prototype));
        }
        return List.copyOf(quests);
    }

    private static Quest retreatQuest(String id, String title, String description, String giverId,
                                      String objectiveId, String objectiveDescription, QuestObjectiveType type,
                                      String targetId, List<String> prerequisites, String followUpId) {
        return new Quest(id, title, description, giverId,
            List.of(objective(objectiveId, objectiveDescription, type, targetId)), prerequisites, followUpId);
    }

    private static QuestObjective objective(String id, String description, QuestObjectiveType type, String targetId) {
        return new QuestObjective(id, description, type, targetId, 1);
    }

    private static Quest copyOf(Quest prototype) {
        List<QuestObjective> copies = new ArrayList<>();
        for (QuestObjective objective : prototype.getObjectives()) {
            copies.add(new QuestObjective(objective.getId(), objective.getDescription(), objective.getType(),
                objective.getTargetId(), objective.getRequiredAmount()));
        }
        return new Quest(prototype.getId(), prototype.getTitle(), prototype.getDescription(),
            prototype.getQuestGiverNpcId(), copies, prototype.getPrerequisiteQuestIds(), prototype.getFollowUpQuestId(),
            prototype.getRewards());
    }
}
