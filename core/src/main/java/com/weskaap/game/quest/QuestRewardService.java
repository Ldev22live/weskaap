package com.weskaap.game.quest;

import com.weskaap.game.economy.Wallet;
import com.weskaap.game.inventory.Inventory;
import com.weskaap.game.story.StoryState;

public class QuestRewardService {
    private final Wallet wallet;
    private final Inventory inventory;
    private final StoryState storyState;

    public QuestRewardService(Wallet wallet, Inventory inventory, StoryState storyState) {
        this.wallet = wallet;
        this.inventory = inventory;
        this.storyState = storyState;
    }

    public boolean apply(Quest quest) {
        if (quest == null || !quest.isCompleted() || quest.areRewardsClaimed()) return false;
        for (QuestReward reward : quest.getRewards()) {
            if (reward.getType() == QuestRewardType.ITEM && !inventory.add(reward.getItem())) return false;
        }
        for (QuestReward reward : quest.getRewards()) {
            switch (reward.getType()) {
                case MONEY:
                    wallet.earn(reward.getAmount());
                    break;
                case STORY_FLAG:
                    storyState.set(reward.getStoryFlag());
                    break;
                case ITEM:
                    break;
            }
        }
        quest.markRewardsClaimed();
        return true;
    }
}
