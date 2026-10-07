package com.weskaap.game.dialogue;

import com.weskaap.game.quest.QuestController;
import com.weskaap.game.quest.QuestState;
import com.weskaap.game.story.StoryFlag;
import com.weskaap.game.story.StoryState;

public final class DialogueContext {
    private final QuestController questController;
    private final StoryState storyState;

    public DialogueContext(QuestController questController, StoryState storyState) {
        this.questController = questController;
        this.storyState = storyState;
    }

    public boolean isQuestCompleted(String questId) {
        return questController.isQuestCompleted(questId);
    }

    public boolean hasQuestState(String questId, QuestState state) {
        return questController.getQuest(questId) != null && questController.getQuest(questId).getState() == state;
    }

    public boolean hasFlag(StoryFlag flag) {
        return storyState.isSet(flag);
    }
}
