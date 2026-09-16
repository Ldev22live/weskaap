package com.weskaap.game.quest;

public class QuestObjective {

    private final String id;
    private final String description;
    private final QuestObjectiveType type;
    private final String targetId;
    private final int requiredAmount;
    private int currentAmount;
    private boolean completed;

    public QuestObjective(String id, String description, QuestObjectiveType type, String targetId, int requiredAmount) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Objective id cannot be blank");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Objective description cannot be blank");
        }
        if (type == null) {
            throw new IllegalArgumentException("Objective type cannot be null");
        }
        if (requiredAmount <= 0) {
            throw new IllegalArgumentException("Required amount must be positive");
        }
        this.id = id;
        this.description = description;
        this.type = type;
        this.targetId = targetId;
        this.requiredAmount = requiredAmount;
        this.currentAmount = 0;
        this.completed = false;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public QuestObjectiveType getType() {
        return type;
    }

    public String getTargetId() {
        return targetId;
    }

    public int getRequiredAmount() {
        return requiredAmount;
    }

    public int getCurrentAmount() {
        return currentAmount;
    }

    public boolean isComplete() {
        return completed;
    }

    public boolean incrementProgress() {
        if (completed) {
            return false;
        }
        currentAmount = Math.min(requiredAmount, currentAmount + 1);
        if (currentAmount >= requiredAmount) {
            completed = true;
        }
        return true;
    }
}
