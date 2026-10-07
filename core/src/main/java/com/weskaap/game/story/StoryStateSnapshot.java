package com.weskaap.game.story;

import java.util.Set;

public final class StoryStateSnapshot {
    private final Set<StoryFlag> flags;

    public StoryStateSnapshot(Set<StoryFlag> flags) {
        this.flags = Set.copyOf(flags);
    }

    public Set<StoryFlag> getFlags() {
        return flags;
    }
}
