package com.weskaap.game.equipment;

import com.weskaap.game.item.ItemType;

public enum EquipmentSlot {
    WEAPON,
    ARMOUR;

    public static EquipmentSlot fromItemType(ItemType type) {
        if (type == null) {
            return null;
        }
        return switch (type) {
            case WEAPON -> WEAPON;
            case ARMOUR -> ARMOUR;
            default -> null;
        };
    }
}
