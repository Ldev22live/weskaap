package com.weskaap.game.quest;

import com.weskaap.game.item.Item;
import com.weskaap.game.story.StoryFlag;

public final class QuestReward {
    private final QuestRewardType type;
    private final int amount;
    private final Item item;
    private final StoryFlag storyFlag;

    private QuestReward(QuestRewardType type, int amount, Item item, StoryFlag storyFlag) {
        this.type = type;
        this.amount = amount;
        this.item = item;
        this.storyFlag = storyFlag;
    }

    public static QuestReward money(int amount) {
        if (amount <= 0) throw new IllegalArgumentException("Money reward must be positive");
        return new QuestReward(QuestRewardType.MONEY, amount, null, null);
    }

    public static QuestReward item(Item item) {
        if (item == null) throw new IllegalArgumentException("Item reward cannot be null");
        return new QuestReward(QuestRewardType.ITEM, 0, item.copy(), null);
    }

    public static QuestReward storyFlag(StoryFlag flag) {
        if (flag == null) throw new IllegalArgumentException("Story flag cannot be null");
        return new QuestReward(QuestRewardType.STORY_FLAG, 0, null, flag);
    }

    public QuestRewardType getType() { return type; }
    public int getAmount() { return amount; }
    public Item getItem() { return item == null ? null : item.copy(); }
    public StoryFlag getStoryFlag() { return storyFlag; }
}
