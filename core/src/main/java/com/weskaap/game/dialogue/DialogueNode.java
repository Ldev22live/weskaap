package com.weskaap.game.dialogue;

import java.util.Collections;
import java.util.List;

public class DialogueNode {

    private final String id;
    private final String speaker;
    private final String text;
    private final String nextNodeId;
    private final List<DialogueOption> options;

    public DialogueNode(String id, String speaker, String text, List<DialogueOption> options, String nextNodeId) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Node id cannot be blank");
        }
        if (speaker == null || speaker.isBlank()) {
            throw new IllegalArgumentException("Speaker cannot be blank");
        }
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Dialogue text cannot be blank");
        }
        if (options == null) {
            throw new IllegalArgumentException("Options list cannot be null");
        }
        this.id = id;
        this.speaker = speaker;
        this.text = text;
        this.nextNodeId = nextNodeId;
        this.options = List.copyOf(options);
    }

    public DialogueNode(String id, String speaker, String text, List<DialogueOption> options) {
        this(id, speaker, text, options, null);
    }

    public String getId() {
        return id;
    }

    public String getSpeaker() {
        return speaker;
    }

    public String getText() {
        return text;
    }

    public List<DialogueOption> getOptions() {
        return Collections.unmodifiableList(options);
    }

    public boolean hasOptions() {
        return !options.isEmpty();
    }

    public String getNextNodeId() {
        return nextNodeId;
    }

    public boolean isTerminal() {
        return nextNodeId == null && options.isEmpty();
    }
}
