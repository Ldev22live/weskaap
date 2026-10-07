package com.weskaap.game.dialogue;

import java.util.function.Predicate;

public final class DialogueVariant {
    private final Predicate<DialogueContext> condition;
    private final Dialogue dialogue;

    public DialogueVariant(Predicate<DialogueContext> condition, Dialogue dialogue) {
        if (condition == null || dialogue == null) throw new IllegalArgumentException("Condition and dialogue are required");
        this.condition = condition;
        this.dialogue = dialogue;
    }

    public boolean matches(DialogueContext context) { return condition.test(context); }
    public Dialogue getDialogue() { return dialogue; }
}
