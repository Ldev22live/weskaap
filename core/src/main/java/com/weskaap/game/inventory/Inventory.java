package com.weskaap.game.inventory;

import com.weskaap.game.item.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Inventory {
    public static final int DEFAULT_CAPACITY = 20;

    private final int capacity;
    private final List<Item> items;

    public Inventory() {
        this(DEFAULT_CAPACITY);
    }

    public Inventory(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Inventory capacity must be positive");
        }
        this.capacity = capacity;
        this.items = new ArrayList<>(capacity);
    }

    public int getCapacity() {
        return capacity;
    }

    public int size() {
        return items.size();
    }

    public boolean isFull() {
        return size() >= capacity;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public boolean add(Item item) {
        if (item == null || !item.isValid()) {
            return false;
        }

        if (item.isStackable()) {
            int availableQuantity = (capacity - size()) * item.getMaxStackSize();
            for (Item existing : items) {
                if (existing.canStackWith(item)) {
                    availableQuantity += existing.getMaxStackSize() - existing.getQuantity();
                }
            }
            if (availableQuantity < item.getQuantity()) {
                return false;
            }

            int remaining = item.getQuantity();
            for (Item existing : items) {
                if (existing.canStackWith(item)) {
                    remaining = existing.addQuantity(remaining);
                    if (remaining == 0) {
                        return true;
                    }
                }
            }
            while (remaining > 0) {
                int stackSize = Math.min(remaining, item.getMaxStackSize());
                items.add(item.withQuantity(stackSize));
                remaining -= stackSize;
            }
            return true;
        }

        if (isFull()) {
            return false;
        }
        items.add(item.copy());
        return true;
    }

    public boolean remove(Item item) {
        if (item == null) {
            return false;
        }
        return remove(item.getId(), item.getQuantity());
    }

    public boolean remove(String itemId, int quantity) {
        if (itemId == null || itemId.isBlank() || quantity <= 0 || count(itemId) < quantity) {
            return false;
        }

        int remaining = quantity;
        Iterator<Item> iterator = items.iterator();
        while (iterator.hasNext() && remaining > 0) {
            Item existing = iterator.next();
            if (existing.getId().equals(itemId)) {
                int available = existing.getQuantity();
                if (remaining >= available) {
                    remaining -= available;
                    iterator.remove();
                } else {
                    existing.removeQuantity(remaining);
                    remaining = 0;
                }
            }
        }
        return remaining == 0;
    }

    public boolean has(String itemId) {
        if (itemId == null || itemId.isBlank()) {
            return false;
        }
        for (Item item : items) {
            if (item.getId().equals(itemId)) {
                return true;
            }
        }
        return false;
    }

    public int count(String itemId) {
        if (itemId == null || itemId.isBlank()) {
            return 0;
        }
        int total = 0;
        for (Item item : items) {
            if (item.getId().equals(itemId)) {
                total += item.getQuantity();
            }
        }
        return total;
    }

    public Item get(String itemId) {
        if (itemId == null || itemId.isBlank()) {
            return null;
        }
        for (Item item : items) {
            if (item.getId().equals(itemId)) {
                return item.copy();
            }
        }
        return null;
    }

    public List<Item> getItems() {
        List<Item> copies = new ArrayList<>(items.size());
        for (Item item : items) {
            copies.add(item.copy());
        }
        return Collections.unmodifiableList(copies);
    }

    public void clear() {
        items.clear();
    }
}
