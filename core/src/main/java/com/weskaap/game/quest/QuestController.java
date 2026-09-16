package com.weskaap.game.quest;

import com.badlogic.gdx.Gdx;

import java.util.List;

public class QuestController {

    private final QuestLog questLog;

    public QuestController(QuestLog questLog) {
        if (questLog == null) {
            throw new IllegalArgumentException("QuestLog cannot be null");
        }
        this.questLog = questLog;
    }

    public boolean startQuest(String questId) {
        Quest quest = questLog.getQuest(questId);
        if (quest == null) {
            quest = QuestRepository.getQuest(questId);
            if (quest == null) {
                return false;
            }
            questLog.add(quest);
        }
        boolean started = quest.start();
        if (started && Gdx.app != null) {
            Gdx.app.log("Quest", "Quest started: " + quest.getTitle());
        }
        return started;
    }

    public boolean updateObjective(String objectiveId) {
        if (objectiveId == null || objectiveId.isBlank()) {
            return false;
        }
        for (Quest quest : questLog.getActiveQuests()) {
            QuestObjective objective = quest.findObjective(objectiveId);
            if (objective == null) {
                continue;
            }
            if (!objective.isComplete()) {
                objective.incrementProgress();
            }
            if (objective.isComplete() && quest.areAllObjectivesComplete()) {
                boolean completed = quest.complete();
                if (completed && Gdx.app != null) {
                    Gdx.app.log("Quest", "Quest completed: " + quest.getTitle());
                }
            }
            return true;
        }
        return false;
    }

    public boolean completeQuest(String questId) {
        Quest quest = questLog.getQuest(questId);
        if (quest == null) {
            return false;
        }
        boolean completed = quest.complete();
        if (completed && Gdx.app != null) {
            Gdx.app.log("Quest", "Quest completed: " + quest.getTitle());
        }
        return completed;
    }

    public Quest getQuest(String questId) {
        return questLog.getQuest(questId);
    }

    public List<Quest> getActiveQuests() {
        return questLog.getActiveQuests();
    }

    public boolean isQuestActive(String questId) {
        Quest quest = questLog.getQuest(questId);
        return quest != null && quest.isActive();
    }

    public boolean isQuestCompleted(String questId) {
        Quest quest = questLog.getQuest(questId);
        return quest != null && quest.isCompleted();
    }

    public boolean isQuestAvailable(String questId) {
        Quest quest = questLog.getQuest(questId);
        return quest != null && quest.isAvailable();
    }

    public QuestLog getQuestLog() {
        return questLog;
    }
}
