package com.weskaap.game.dialogue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DialogueRepository {

    private DialogueRepository() {
    }

    public static Dialogue createTubbyAngelDialogue() {
        Map<String, DialogueNode> nodes = new HashMap<>();

        DialogueNode node1 = new DialogueNode(
            "node-1",
            "Tubby Angel",
            "What do you seek?",
            List.of(
                new DialogueOption("The way forward.", "node-2", "tubby_angel_001", "talk_to_tubby"),
                new DialogueOption("I need help.", "node-3")
            )
        );

        DialogueNode node2 = new DialogueNode(
            "node-2",
            "Tubby Angel",
            "Then you must continue your journey.",
            List.of(),
            "node-4"
        );

        DialogueNode node3 = new DialogueNode(
            "node-3",
            "Tubby Angel",
            "I can only guide you so far.",
            List.of(),
            "node-4"
        );

        DialogueNode node4 = new DialogueNode(
            "node-4",
            "Tubby Angel",
            "Go carefully.",
            List.of()
        );

        nodes.put(node1.getId(), node1);
        nodes.put(node2.getId(), node2);
        nodes.put(node3.getId(), node3);
        nodes.put(node4.getId(), node4);

        return new Dialogue("tubby-angel-introduction", "node-1", nodes);
    }
}
