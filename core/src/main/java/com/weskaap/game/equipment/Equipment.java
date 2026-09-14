package com.weskaap.game.equipment;

import com.weskaap.game.inventory.Inventory;
import com.weskaap.game.item.Item;
import com.weskaap.game.item.ItemStats;
import com.weskaap.game.item.ItemType;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class Equipment {
    private final EnumMap<EquipmentSlot, Item> equipped = new EnumMap<>(EquipmentSlot.class);

    public boolean equip(Inventory inventory, Item item) {
        if (item == null) {
            return false;
        }
        EquipmentSlot slot = EquipmentSlot.fromItemType(item.getType());
        if (slot == null) {
            return false;
        }
        if (!inventory.remove(item)) {
            return false;
        }

        Item current = equipped.put(slot, item.copy());
        if (current != null && !inventory.add(current)) {
            equipped.put(slot, current);
            inventory.add(item);
            return false;
        }
        return true;
    }

    public boolean unequip(EquipmentSlot slot, Inventory inventory) {
        if (slot == null) {
            return false;
        }
        Item current = equipped.remove(slot);
        if (current == null) {
            return false;
        }
        if (!inventory.add(current)) {
            equipped.put(slot, current);
            return false;
        }
        return true;
    }

    public Item getEquipped(EquipmentSlot slot) {
        if (slot == null) {
            return null;
        }
        Item item = equipped.get(slot);
        return item != null ? item.copy() : null;
    }

    public Map<EquipmentSlot, Item> getEquippedItems() {
        Map<EquipmentSlot, Item> copy = new EnumMap<>(EquipmentSlot.class);
        for (Map.Entry<EquipmentSlot, Item> entry : equipped.entrySet()) {
            copy.put(entry.getKey(), entry.getValue().copy());
        }
        return Collections.unmodifiableMap(copy);
    }

    public boolean isEquippable(Item item) {
        if (item == null) {
            return false;
        }
        return EquipmentSlot.fromItemType(item.getType()) != null;
    }

    public boolean hasEquipment(ItemType type) {
        EquipmentSlot slot = EquipmentSlot.fromItemType(type);
        return slot != null && equipped.containsKey(slot);
    }

    public ItemStats getTotalStats() {
        ItemStats total = ItemStats.ZERO;
        for (Item item : equipped.values()) {
            total = total.add(item.getStats());
        }
        return total;
    }
}
