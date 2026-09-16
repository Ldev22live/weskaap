package com.weskaap.game.quest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class QuestRepository {

    private static final Map<String, Quest> QUEST_DATABASE = new HashMap<>();

    static {
        Quest tubbyAngel = new Quest(
            "tubby_angel_001",
            "The Way Forward",
            "Speak with Tubby Angel and learn what lies ahead.",
            List.of(new QuestObjective(
                "talk_to_tubby",
                "Speak with Tubby Angel.",
                QuestObjectiveType.TALK_TO_NPC,
                "tubby_angel",
                1
            ))
        );
        register(tubbyAngel);
    }

    private QuestRepository() {
    }

    public static void register(Quest quest) {
        if (quest != null && !quest.getId().isBlank()) {
            QUEST_DATABASE.put(quest.getId(), quest);
        }
    }

    public static Quest getQuest(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }
        Quest prototype = QUEST_DATABASE.get(id);
        if (prototype == null) {
            return null;
        }
        return copyOf(prototype);
    }

    public static List<Quest> getAllQuests() {
        List<Quest> copies = new ArrayList<>();
        for (Quest prototype : QUEST_DATABASE.values()) {
            copies.add(copyOf(prototype));
        }
        return List.copyOf(copies);
    }

    private static Quest copyOf(Quest prototype) {
        List<QuestObjective> copies = new ArrayList<>();
        for (QuestObjective objective : prototype.getObjectives()) {
            copies.add(new QuestObjective(
                objective.getId(),
                objective.getDescription(),
                objective.getType(),
                objective.getTargetId(),
                objective.getRequiredAmount()
            ));
        }
        return new Quest(prototype.getId(), prototype.getTitle(), prototype.getDescription(), copies);
    }
}
