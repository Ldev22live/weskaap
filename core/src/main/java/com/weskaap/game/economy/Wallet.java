package com.weskaap.game.economy;

public class Wallet {
    private int balance;

    public Wallet() {
        this(0);
    }

    public Wallet(int initialBalance) {
        if (initialBalance < 0) throw new IllegalArgumentException("Initial balance cannot be negative");
        balance = initialBalance;
    }

    public int getBalance() { return balance; }
    public boolean canAfford(int amount) { return amount >= 0 && balance >= amount; }

    public boolean earn(int amount) {
        if (amount <= 0) return false;
        balance += amount;
        return true;
    }

    public boolean spend(int amount) {
        if (amount <= 0 || !canAfford(amount)) return false;
        balance -= amount;
        return true;
    }

    public void restore(int balance) {
        if (balance < 0) throw new IllegalArgumentException("Balance cannot be negative");
        this.balance = balance;
    }
}
