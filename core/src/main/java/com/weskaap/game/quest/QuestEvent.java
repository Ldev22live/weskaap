package com.weskaap.game.quest;

public final class QuestEvent {
    private final QuestObjectiveType type;
    private final String targetId;
    private final int amount;

    public QuestEvent(QuestObjectiveType type, String targetId) {
        this(type, targetId, 1);
    }

    public QuestEvent(QuestObjectiveType type, String targetId, int amount) {
        if (type == null) throw new IllegalArgumentException("Event type cannot be null");
        if (targetId == null || targetId.isBlank()) throw new IllegalArgumentException("Target id cannot be blank");
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        this.type = type;
        this.targetId = targetId;
        this.amount = amount;
    }

    public QuestObjectiveType getType() { return type; }
    public String getTargetId() { return targetId; }
    public int getAmount() { return amount; }
}
