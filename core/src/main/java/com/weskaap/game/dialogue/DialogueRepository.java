package com.weskaap.game.dialogue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.weskaap.game.quest.Quest;

public final class DialogueRepository {

    private DialogueRepository() {
    }

    public static Dialogue createRosebankFriendDialogue() {
        DialogueNode greeting = new DialogueNode("greeting", "RosebankFriend",
            "I know that face. It has been a long time.", List.of(), "allies");
        DialogueNode allies = new DialogueNode("allies", "RosebankFriend",
            "You still have a friend in Rosebank. Something is changing here, and we should face it together.", List.of());
        return new Dialogue("rosebank-friend-introduction", greeting.getId(), Map.of(
            greeting.getId(), greeting, allies.getId(), allies));
    }

    public static Dialogue createQuestOfferDialogue(String speaker, Quest quest) {
        DialogueNode offer = new DialogueNode("offer", speaker,
            quest.getTitle() + ": " + quest.getDescription(), List.of(
                new DialogueOption("Accept", "accepted", quest.getId()),
                new DialogueOption("Not now", "declined")));
        DialogueNode accepted = new DialogueNode("accepted", speaker, "I will mark it in your quest log.", List.of());
        DialogueNode declined = new DialogueNode("declined", speaker, "Come back if you change your mind.", List.of());
        return new Dialogue(quest.getId() + "_offer", offer.getId(), Map.of(
            offer.getId(), offer, accepted.getId(), accepted, declined.getId(), declined));
    }

    public static Dialogue createQuestStatusDialogue(String speaker, Quest quest) {
        String text = quest.isCompleted() ? "Thank you. " + quest.getTitle() + " is complete."
            : "You are still working on " + quest.getTitle() + ".";
        return createPlaceholderDialogue(quest.getId() + "_status", speaker, text);
    }

    public static Dialogue createPlaceholderDialogue(String id, String speaker, String text) {
        DialogueNode node = new DialogueNode("node-1", speaker, text, List.of());
        return new Dialogue(id, node.getId(), Map.of(node.getId(), node));
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

    public static Dialogue createFirstStepsDialogue() {
        Map<String, DialogueNode> nodes = new HashMap<>();

        DialogueNode node1 = new DialogueNode(
            "node-1",
            "Local Guide",
            "Hello, Hero! I have a task for you.",
            List.of(
                new DialogueOption("I will help.", "node-2", "quest_first_steps", "speak-local-guide"),
                new DialogueOption("Maybe later.", "node-3")
            )
        );

        DialogueNode node2 = new DialogueNode(
            "node-2",
            "Local Guide",
            "Speak with the locals and learn the way.",
            List.of(),
            "node-4"
        );

        DialogueNode node3 = new DialogueNode(
            "node-3",
            "Local Guide",
            "Return when you are ready.",
            List.of()
        );

        DialogueNode node4 = new DialogueNode(
            "node-4",
            "Local Guide",
            "Go carefully.",
            List.of()
        );

        nodes.put(node1.getId(), node1);
        nodes.put(node2.getId(), node2);
        nodes.put(node3.getId(), node3);
        nodes.put(node4.getId(), node4);

        return new Dialogue("first-steps-introduction", "node-1", nodes);
    }

    public static Dialogue createMeetTheNeighbourDialogue() {
        Map<String, DialogueNode> nodes = new HashMap<>();

        DialogueNode node1 = new DialogueNode(
            "node-1",
            "Neighbour",
            "Howzit, neighbour. Will you say hello?",
            List.of(
                new DialogueOption("Howzit!", "node-2", "quest_meet_the_neighbour", "speak-neighbour"),
                new DialogueOption("Not now.", "node-3")
            )
        );

        DialogueNode node2 = new DialogueNode(
            "node-2",
            "Neighbour",
            "Good to meet you.",
            List.of(),
            "node-4"
        );

        DialogueNode node3 = new DialogueNode(
            "node-3",
            "Neighbour",
            "Maybe next time.",
            List.of()
        );

        DialogueNode node4 = new DialogueNode(
            "node-4",
            "Neighbour",
            "Take care.",
            List.of()
        );

        nodes.put(node1.getId(), node1);
        nodes.put(node2.getId(), node2);
        nodes.put(node3.getId(), node3);
        nodes.put(node4.getId(), node4);

        return new Dialogue("meet-the-neighbour-introduction", "node-1", nodes);
    }
}
