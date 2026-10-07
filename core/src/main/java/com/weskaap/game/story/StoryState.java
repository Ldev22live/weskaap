package com.weskaap.game.story;

import java.util.EnumSet;

public class StoryState {
    private final EnumSet<StoryFlag> flags = EnumSet.noneOf(StoryFlag.class);

    public boolean set(StoryFlag flag) {
        return flag != null && flags.add(flag);
    }

    public boolean clear(StoryFlag flag) {
        return flag != null && flags.remove(flag);
    }

    public boolean isSet(StoryFlag flag) {
        return flag != null && flags.contains(flag);
    }

    public StoryStateSnapshot snapshot() {
        return new StoryStateSnapshot(flags);
    }

    public void restore(StoryStateSnapshot snapshot) {
        if (snapshot == null) throw new IllegalArgumentException("Snapshot cannot be null");
        flags.clear();
        flags.addAll(snapshot.getFlags());
    }
}
