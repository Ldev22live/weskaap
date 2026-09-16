package com.weskaap.game.dialogue;

import java.util.Collections;
import java.util.Map;

public class Dialogue {

    private final String id;
    private final String startingNodeId;
    private final Map<String, DialogueNode> nodes;

    public Dialogue(String id, String startingNodeId, Map<String, DialogueNode> nodes) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Dialogue id cannot be blank");
        }
        if (startingNodeId == null || startingNodeId.isBlank()) {
            throw new IllegalArgumentException("Starting node id cannot be blank");
        }
        if (nodes == null || nodes.isEmpty()) {
            throw new IllegalArgumentException("Dialogue must contain at least one node");
        }
        if (!nodes.containsKey(startingNodeId)) {
            throw new IllegalArgumentException("Starting node id must exist in nodes map");
        }
        this.id = id;
        this.startingNodeId = startingNodeId;
        this.nodes = Map.copyOf(nodes);
    }

    public String getId() {
        return id;
    }

    public String getStartingNodeId() {
        return startingNodeId;
    }

    public DialogueNode getStartingNode() {
        return nodes.get(startingNodeId);
    }

    public DialogueNode getNode(String nodeId) {
        return nodes.get(nodeId);
    }

    public Map<String, DialogueNode> getNodes() {
        return Collections.unmodifiableMap(nodes);
    }
}
