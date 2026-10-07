package com.weskaap.game.dialogue;

import java.util.List;

public class DialogueResolver {
    public Dialogue resolve(Dialogue fallback, DialogueContext context, List<DialogueVariant> variants) {
        if (fallback == null || context == null) throw new IllegalArgumentException("Fallback and context are required");
        if (variants != null) {
            for (DialogueVariant variant : variants) {
                if (variant.matches(context)) return variant.getDialogue();
            }
        }
        return fallback;
    }
}
