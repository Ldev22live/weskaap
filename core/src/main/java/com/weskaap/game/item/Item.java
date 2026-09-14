package com.weskaap.game.item;

public final class Item {
    public static final int DEFAULT_MAX_STACK = 99;
    public static final int DEFAULT_QUANTITY = 1;

    private final String id;
    private final String name;
    private final String description;
    private final ItemType type;
    private final boolean stackable;
    private final int maxStackSize;
    private final ItemStats stats;
    private int quantity;

    public Item(String id, String name, String description, ItemType type) {
        this(id, name, description, type, false, DEFAULT_MAX_STACK, DEFAULT_QUANTITY, new ItemStats());
    }

    public Item(String id, String name, String description, ItemType type, boolean stackable, int maxStackSize, int quantity) {
        this(id, name, description, type, stackable, maxStackSize, quantity, new ItemStats());
    }

    public Item(String id, String name, String description, ItemType type, boolean stackable, int maxStackSize, int quantity, ItemStats stats) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Item id must not be blank");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Item name must not be blank");
        }
        if (description == null) {
            throw new IllegalArgumentException("Item description must not be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("Item type must not be null");
        }
        if (maxStackSize <= 0) {
            throw new IllegalArgumentException("Item max stack size must be positive");
        }
        if (quantity <= 0 || quantity > maxStackSize) {
            throw new IllegalArgumentException("Item quantity must be between 1 and max stack size");
        }
        if (!stackable && quantity != DEFAULT_QUANTITY) {
            throw new IllegalArgumentException("Non-stackable items must have a quantity of 1");
        }
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.stackable = stackable;
        this.maxStackSize = maxStackSize;
        this.quantity = quantity;
        this.stats = stats != null ? stats : new ItemStats();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ItemType getType() {
        return type;
    }

    public boolean isStackable() {
        return stackable;
    }

    public ItemStats getStats() {
        return stats;
    }

    public int getMaxStackSize() {
        return maxStackSize;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean isValid() {
        return quantity > 0;
    }

    public boolean canStackWith(Item other) {
        if (other == null) {
            return false;
        }
        return stackable && other.stackable
            && id.equals(other.id)
            && type == other.type
            && maxStackSize == other.maxStackSize;
    }

    public int addQuantity(int amount) {
        if (amount <= 0) {
            return 0;
        }
        int available = maxStackSize - quantity;
        int toAdd = Math.min(amount, available);
        quantity += toAdd;
        return amount - toAdd;
    }

    public boolean removeQuantity(int amount) {
        if (amount <= 0) {
            return false;
        }
        if (amount > quantity) {
            return false;
        }
        quantity -= amount;
        return true;
    }

    public Item withQuantity(int newQuantity) {
        return new Item(id, name, description, type, stackable, maxStackSize, newQuantity, stats);
    }

    public Item copy() {
        return new Item(id, name, description, type, stackable, maxStackSize, quantity, stats);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Item)) {
            return false;
        }
        Item other = (Item) obj;
        return id.equals(other.id) && quantity == other.quantity;
    }

    @Override
    public int hashCode() {
        return 31 * id.hashCode() + quantity;
    }

    @Override
    public String toString() {
        return stackable && quantity > 1 ? name + " x" + quantity : name;
    }
}
