package com.weskaap.game.quest;

import java.util.List;

public final class QuestLogSnapshot {
    private final List<QuestProgressSnapshot> quests;

    public QuestLogSnapshot(List<QuestProgressSnapshot> quests) {
        this.quests = List.copyOf(quests);
    }

    public List<QuestProgressSnapshot> getQuests() {
        return quests;
    }
}
