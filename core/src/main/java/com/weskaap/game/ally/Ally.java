package com.weskaap.game.ally;

public class Ally {
    private final String npcId;
    private AllyRelationshipState relationshipState;

    public Ally(String npcId) {
        if (npcId == null || npcId.isBlank()) throw new IllegalArgumentException("NPC id cannot be blank");
        this.npcId = npcId;
        relationshipState = AllyRelationshipState.RECOGNIZED;
    }

    public String getNpcId() { return npcId; }
    public AllyRelationshipState getRelationshipState() { return relationshipState; }
    public boolean isAlly() { return relationshipState == AllyRelationshipState.ALLY; }

    public void establishAlliance() {
        relationshipState = AllyRelationshipState.ALLY;
    }
}
