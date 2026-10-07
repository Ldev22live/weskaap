package com.weskaap.game.quest;

import java.util.Map;

public final class QuestProgressSnapshot {
    private final String questId;
    private final QuestState state;
    private final Map<String, Integer> objectiveProgress;
    private final boolean rewardsClaimed;

    public QuestProgressSnapshot(String questId, QuestState state, Map<String, Integer> objectiveProgress,
                                 boolean rewardsClaimed) {
        this.questId = questId;
        this.state = state;
        this.objectiveProgress = Map.copyOf(objectiveProgress);
        this.rewardsClaimed = rewardsClaimed;
    }

    public String getQuestId() { return questId; }
    public QuestState getState() { return state; }
    public Map<String, Integer> getObjectiveProgress() { return objectiveProgress; }
    public boolean areRewardsClaimed() { return rewardsClaimed; }
}
