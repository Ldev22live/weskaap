package com.weskaap.game.quest;

import java.util.Collections;
import java.util.List;

public class Quest {

    private final String id;
    private final String title;
    private final String description;
    private QuestState state;
    private final List<QuestObjective> objectives;

    public Quest(String id, String title, String description, List<QuestObjective> objectives) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Quest id cannot be blank");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Quest title cannot be blank");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Quest description cannot be blank");
        }
        if (objectives == null || objectives.isEmpty()) {
            throw new IllegalArgumentException("Quest must have at least one objective");
        }
        this.id = id;
        this.title = title;
        this.description = description;
        this.objectives = List.copyOf(objectives);
        this.state = QuestState.AVAILABLE;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public QuestState getState() {
        return state;
    }

    public List<QuestObjective> getObjectives() {
        return Collections.unmodifiableList(objectives);
    }

    public QuestObjective findObjective(String objectiveId) {
        for (QuestObjective objective : objectives) {
            if (objective.getId().equals(objectiveId)) {
                return objective;
            }
        }
        return null;
    }

    public boolean start() {
        if (state != QuestState.AVAILABLE) {
            return false;
        }
        state = QuestState.ACTIVE;
        return true;
    }

    public boolean complete() {
        if (state != QuestState.ACTIVE) {
            return false;
        }
        state = QuestState.COMPLETED;
        return true;
    }

    public boolean fail() {
        if (state != QuestState.ACTIVE) {
            return false;
        }
        state = QuestState.FAILED;
        return true;
    }

    public boolean isActive() {
        return state == QuestState.ACTIVE;
    }

    public boolean isCompleted() {
        return state == QuestState.COMPLETED;
    }

    public boolean isAvailable() {
        return state == QuestState.AVAILABLE;
    }

    public boolean isFailed() {
        return state == QuestState.FAILED;
    }

    public boolean areAllObjectivesComplete() {
        for (QuestObjective objective : objectives) {
            if (!objective.isComplete()) {
                return false;
            }
        }
        return true;
    }
}
