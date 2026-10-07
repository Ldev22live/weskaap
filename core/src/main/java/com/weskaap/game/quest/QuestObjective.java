package com.weskaap.game.quest;

public class QuestObjective {
    private final String id;
    private final String description;
    private final QuestObjectiveType type;
    private final String targetId;
    private final int requiredAmount;
    private int currentAmount;
    private ObjectiveState state;

    public QuestObjective(String id, String description, QuestObjectiveType type, String targetId, int requiredAmount) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Objective id cannot be blank");
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Objective description cannot be blank");
        }
        if (type == null) throw new IllegalArgumentException("Objective type cannot be null");
        if (requiredAmount <= 0) throw new IllegalArgumentException("Required amount must be positive");
        this.id = id;
        this.description = description;
        this.type = type;
        this.targetId = targetId;
        this.requiredAmount = requiredAmount;
        state = ObjectiveState.PENDING;
    }

    public String getId() { return id; }
    public String getDescription() { return description; }
    public QuestObjectiveType getType() { return type; }
    public String getTargetId() { return targetId; }
    public int getRequiredAmount() { return requiredAmount; }
    public int getCurrentAmount() { return currentAmount; }
    public ObjectiveState getState() { return state; }
    public boolean isComplete() { return state == ObjectiveState.COMPLETED; }

    public void activate() {
        if (state == ObjectiveState.PENDING) state = ObjectiveState.IN_PROGRESS;
    }

    public boolean incrementProgress() {
        return incrementProgress(1);
    }

    public boolean incrementProgress(int amount) {
        if (amount <= 0 || isComplete()) return false;
        activate();
        currentAmount = Math.min(requiredAmount, currentAmount + amount);
        if (currentAmount >= requiredAmount) state = ObjectiveState.COMPLETED;
        return true;
    }

    void restoreProgress(int amount) {
        currentAmount = Math.max(0, Math.min(requiredAmount, amount));
        state = currentAmount >= requiredAmount ? ObjectiveState.COMPLETED
            : currentAmount > 0 ? ObjectiveState.IN_PROGRESS : ObjectiveState.PENDING;
    }
}
