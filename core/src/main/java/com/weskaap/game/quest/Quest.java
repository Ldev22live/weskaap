package com.weskaap.game.quest;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Quest {
    private final String id;
    private final String title;
    private final String description;
    private final String questGiverNpcId;
    private final List<QuestObjective> objectives;
    private final List<String> prerequisiteQuestIds;
    private final String followUpQuestId;
    private final List<QuestReward> rewards;
    private QuestState state;
    private boolean rewardsClaimed;

    public Quest(String id, String title, String description, List<QuestObjective> objectives) {
        this(id, title, description, null, objectives, List.of(), null, List.of());
    }

    public Quest(String id, String title, String description, String questGiverNpcId,
                 List<QuestObjective> objectives, List<String> prerequisiteQuestIds, String followUpQuestId) {
        this(id, title, description, questGiverNpcId, objectives, prerequisiteQuestIds, followUpQuestId, List.of());
    }

    public Quest(String id, String title, String description, String questGiverNpcId,
                 List<QuestObjective> objectives, List<String> prerequisiteQuestIds, String followUpQuestId,
                 List<QuestReward> rewards) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Quest id cannot be blank");
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Quest title cannot be blank");
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Quest description cannot be blank");
        }
        if (objectives == null || objectives.isEmpty()) {
            throw new IllegalArgumentException("Quest must have at least one objective");
        }
        this.id = id;
        this.title = title;
        this.description = description;
        this.questGiverNpcId = questGiverNpcId;
        this.objectives = List.copyOf(objectives);
        this.prerequisiteQuestIds = prerequisiteQuestIds == null ? List.of() : List.copyOf(prerequisiteQuestIds);
        this.followUpQuestId = followUpQuestId;
        this.rewards = rewards == null ? List.of() : List.copyOf(rewards);
        state = this.prerequisiteQuestIds.isEmpty() ? QuestState.AVAILABLE : QuestState.LOCKED;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getQuestGiverNpcId() { return questGiverNpcId; }
    public QuestState getState() { return state; }
    public List<QuestObjective> getObjectives() { return Collections.unmodifiableList(objectives); }
    public List<String> getPrerequisiteQuestIds() { return prerequisiteQuestIds; }
    public String getFollowUpQuestId() { return followUpQuestId; }
    public List<QuestReward> getRewards() { return rewards; }
    public boolean areRewardsClaimed() { return rewardsClaimed; }

    public QuestObjective findObjective(String objectiveId) {
        for (QuestObjective objective : objectives) {
            if (objective.getId().equals(objectiveId)) return objective;
        }
        return null;
    }

    public boolean unlock() {
        if (state != QuestState.LOCKED) return false;
        state = QuestState.AVAILABLE;
        return true;
    }

    public boolean accept() {
        if (state != QuestState.AVAILABLE) return false;
        state = QuestState.ACCEPTED;
        return true;
    }

    public boolean begin() {
        if (state != QuestState.ACCEPTED) return false;
        state = QuestState.IN_PROGRESS;
        for (QuestObjective objective : objectives) objective.activate();
        return true;
    }

    public boolean start() { return accept() && begin(); }

    public boolean complete() {
        if (state != QuestState.ACCEPTED && state != QuestState.IN_PROGRESS) return false;
        state = QuestState.COMPLETED;
        return true;
    }

    public boolean fail() {
        if (state != QuestState.ACCEPTED && state != QuestState.IN_PROGRESS) return false;
        state = QuestState.FAILED;
        return true;
    }

    public boolean isLocked() { return state == QuestState.LOCKED; }
    public boolean isAccepted() { return state == QuestState.ACCEPTED; }
    public boolean isActive() { return state == QuestState.ACCEPTED || state == QuestState.IN_PROGRESS; }
    public boolean isCompleted() { return state == QuestState.COMPLETED; }
    public boolean isAvailable() { return state == QuestState.AVAILABLE; }
    public boolean isFailed() { return state == QuestState.FAILED; }

    public boolean areAllObjectivesComplete() {
        for (QuestObjective objective : objectives) if (!objective.isComplete()) return false;
        return true;
    }

    public QuestProgressSnapshot snapshot() {
        Map<String, Integer> progress = new LinkedHashMap<>();
        for (QuestObjective objective : objectives) progress.put(objective.getId(), objective.getCurrentAmount());
        return new QuestProgressSnapshot(id, state, progress, rewardsClaimed);
    }

    public void restore(QuestProgressSnapshot snapshot) {
        if (snapshot == null || !id.equals(snapshot.getQuestId())) {
            throw new IllegalArgumentException("Snapshot does not match quest");
        }
        state = snapshot.getState();
        rewardsClaimed = snapshot.areRewardsClaimed();
        for (QuestObjective objective : objectives) {
            objective.restoreProgress(snapshot.getObjectiveProgress().getOrDefault(objective.getId(), 0));
            if (isActive() && !objective.isComplete()) objective.activate();
        }
    }

    void markRewardsClaimed() { rewardsClaimed = true; }
}
