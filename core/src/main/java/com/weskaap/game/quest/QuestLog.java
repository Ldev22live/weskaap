package com.weskaap.game.quest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestLog {

    private final List<Quest> quests;

    public QuestLog() {
        this.quests = new ArrayList<>();
    }

    public boolean add(Quest quest) {
        if (quest == null || hasQuest(quest.getId())) {
            return false;
        }
        quests.add(quest);
        return true;
    }

    public Quest getQuest(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }
        for (Quest quest : quests) {
            if (quest.getId().equals(id)) {
                return quest;
            }
        }
        return null;
    }

    public boolean hasQuest(String id) {
        return getQuest(id) != null;
    }

    public List<Quest> getQuests() {
        return Collections.unmodifiableList(new ArrayList<>(quests));
    }

    public List<Quest> getActiveQuests() {
        List<Quest> active = new ArrayList<>();
        for (Quest quest : quests) {
            if (quest.isActive()) {
                active.add(quest);
            }
        }
        return Collections.unmodifiableList(active);
    }

    public List<Quest> getCompletedQuests() {
        List<Quest> completed = new ArrayList<>();
        for (Quest quest : quests) {
            if (quest.isCompleted()) {
                completed.add(quest);
            }
        }
        return Collections.unmodifiableList(completed);
    }
}
