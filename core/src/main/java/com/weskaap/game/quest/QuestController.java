package com.weskaap.game.quest;

import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.List;

public class QuestController {
    private final QuestLog questLog;
    private final QuestRewardService rewardService;

    public QuestController(QuestLog questLog) {
        this(questLog, null);
    }

    public QuestController(QuestLog questLog, QuestRewardService rewardService) {
        if (questLog == null) throw new IllegalArgumentException("QuestLog cannot be null");
        this.questLog = questLog;
        this.rewardService = rewardService;
    }

    public boolean acceptQuest(String questId) {
        Quest quest = getOrCreateQuest(questId);
        if (quest == null || !arePrerequisitesComplete(quest)) return false;
        if (quest.isLocked()) quest.unlock();
        boolean accepted = quest.accept();
        if (accepted && Gdx.app != null) Gdx.app.log("Quest", "Quest accepted: " + quest.getTitle());
        return accepted;
    }

    public boolean beginQuest(String questId) {
        Quest quest = questLog.getQuest(questId);
        return quest != null && quest.begin();
    }

    public boolean startQuest(String questId) {
        return acceptQuest(questId) && beginQuest(questId);
    }

    public boolean declineQuest(String questId) {
        Quest quest = questLog.getQuest(questId);
        return quest == null || quest.isAvailable();
    }

    public boolean updateObjective(String objectiveId) {
        if (objectiveId == null || objectiveId.isBlank()) return false;
        for (Quest quest : questLog.getActiveQuests()) {
            QuestObjective objective = quest.findObjective(objectiveId);
            if (objective == null) continue;
            if (!objective.isComplete()) objective.incrementProgress();
            if (objective.isComplete() && quest.areAllObjectivesComplete()) completeQuest(quest.getId());
            return true;
        }
        return false;
    }

    public boolean updateObjective(QuestObjectiveType type, String targetId) {
        return handleEvent(new QuestEvent(type, targetId));
    }

    public boolean handleEvent(QuestEvent event) {
        if (event == null) return false;
        boolean updated = false;
        for (Quest quest : new ArrayList<>(questLog.getActiveQuests())) {
            for (QuestObjective objective : quest.getObjectives()) {
                if (objective.getType() == event.getType()
                    && event.getTargetId().equals(objective.getTargetId()) && !objective.isComplete()) {
                    objective.incrementProgress(event.getAmount());
                    updated = true;
                }
            }
            if (quest.areAllObjectivesComplete()) completeQuest(quest.getId());
        }
        return updated;
    }

    public boolean completeQuest(String questId) {
        Quest quest = questLog.getQuest(questId);
        if (quest == null) return false;
        boolean completed = quest.complete();
        if (completed && rewardService != null) rewardService.apply(quest);
        if (completed && Gdx.app != null) Gdx.app.log("Quest", "Quest completed: " + quest.getTitle());
        return completed;
    }

    public Quest getQuest(String questId) { return questLog.getQuest(questId); }
    public List<Quest> getActiveQuests() { return questLog.getActiveQuests(); }
    public boolean isQuestActive(String questId) {
        Quest quest = questLog.getQuest(questId);
        return quest != null && quest.isActive();
    }
    public boolean isQuestCompleted(String questId) {
        Quest quest = questLog.getQuest(questId);
        return quest != null && quest.isCompleted();
    }
    public boolean isQuestAvailable(String questId) {
        Quest existing = questLog.getQuest(questId);
        if (existing != null) {
            if (existing.isLocked() && arePrerequisitesComplete(existing)) existing.unlock();
            return existing.isAvailable();
        }
        Quest definition = QuestRepository.getQuest(questId);
        return definition != null && arePrerequisitesComplete(definition);
    }

    public List<Quest> getAvailableQuestsForNpc(String npcId) {
        List<Quest> available = new ArrayList<>();
        for (Quest quest : QuestRepository.getQuestsForNpc(npcId)) {
            if (isQuestAvailable(quest.getId())) available.add(quest);
        }
        return List.copyOf(available);
    }

    public boolean arePrerequisitesComplete(Quest quest) {
        for (String prerequisiteId : quest.getPrerequisiteQuestIds()) {
            if (!isQuestCompleted(prerequisiteId)) return false;
        }
        return true;
    }

    public QuestLog getQuestLog() { return questLog; }

    private Quest getOrCreateQuest(String questId) {
        Quest quest = questLog.getQuest(questId);
        if (quest != null) return quest;
        quest = QuestRepository.getQuest(questId);
        if (quest != null) questLog.add(quest);
        return quest;
    }
}
