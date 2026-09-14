# Weskaap Walkthrough

## Overview

This prototype covers milestones M1 through M11. It demonstrates a lightweight LibGDX desktop game with keyboard-controlled Hero movement, a simple world, camera following, static obstacle collision, interaction, basic combat, enemy pursuit AI, Hero health/damage/death, an item/inventory/equipment foundation with item statistics, a lightweight loot/enemy-drops foundation, and a consumables/healing foundation. All visuals are primitive shapes; no external assets are used.

## Controls

- `W` or `Up`: move up
- `S` or `Down`: move down
- `A` or `Left`: move left
- `D` or `Right`: move right
- Two directional keys: normalized diagonal movement
- `E`: interact with a nearby NPC or item
- `Space`: basic attack in the direction the Hero is facing
- `I`: toggle inventory display
- `H`: use a Healing Potion from inventory

## Architecture

### Hero

`Hero` owns its center position, base movement speed, visual size, collision boundary, facing direction, health, an `Inventory`, and `Equipment`. It implements the `Combatant` abstraction so it can take damage and become dead. Calculated stats (attack, armour, max health, movement speed) combine base values with equipment item statistics. The facing direction is updated from the most recent non-zero movement input and is used by the combat system to place the attack area in front of the Hero.

### HeroController

`HeroController` reads WASD and arrow-key input and returns a normalized, delta-scaled movement vector. It updates the Hero's facing direction when movement occurs.

### CollisionResolver

`CollisionResolver` applies movement one axis at a time. It clamps positions to the world bounds and resolves collisions against static obstacles. A `Rectangle`-based overload allows both the Hero and enemies to reuse the same axis-separated resolution logic, including wall sliding and anti-tunneling.

### GameWorld

`GameWorld` owns the Hero, controller, obstacle list, NPC, enemies, enemy AI controllers, loot generator, consumable controller, and all primitive rendering. It coordinates the update order: movement, enemy AI (which may include enemy attacks), interaction, combat, death handling, loot generation, consumable use, and message timers.

### MainGameScreen

`MainGameScreen` owns the game camera, UI camera, `SpriteBatch`, `BitmapFont`, and screen lifecycle. It renders the world and draws UI messages at the top-left of the screen.

### Interaction System

- `Interactable` defines the contract for interactable world objects.
- `PrototypeNpc` implements the contract with a position, bounds, interaction range, and response text.
- `PrototypeItem` implements the contract for collectible world items and holds an `Item`.
- `InteractionController` detects the `E` key press.
- `InventoryController` detects the `I` key press to toggle inventory visibility.
- `GameWorld` selects the nearest valid interactable and triggers its response. If the target is a `PrototypeItem`, the item is added to the Hero's inventory and removed from the world.

### Combat System

- `Combatant` defines health, maximum health, taking damage, and alive state.
- `PrototypeEnemy` implements `Combatant` and owns position, bounds, health, and an optional `LootTable`.
- `CombatController` handles the Space key, attack cooldown, directional hit area, target detection, and damage application.
- When an enemy dies, `GameWorld` generates loot from the enemy's loot table once, spawns the resulting items as `PrototypeItem` instances near the enemy, and registers them in the world's interactable and collision collections.
- Dead enemies are removed from the world.

### Enemy AI System

- `EnemyAiController` manages per-enemy AI state (`IDLE` / `CHASING`), Hero detection, normalized pursuit, stopping distance, jitter prevention, and a simple melee attack with cooldown.
- Each enemy has its own controller and evaluates the Hero independently.
- Enemies will not chase or attack a dead Hero.
- Dead enemies do not run AI and are cleaned up by the combat system.

### Item, Inventory, and Equipment System

- `ItemType` is an enum with `WEAPON`, `ARMOUR`, `CONSUMABLE`, `QUEST`, and `MISC`.
- `Item` is a data-only class with an id, name, description, type, stackable flag, max stack size, quantity, and optional `ItemStats`.
- `ItemStats` holds optional gameplay statistics: damage, armour, health bonus, attack bonus, and movement modifier.
- `Inventory` manages items with a configurable capacity. It supports add, remove by id/quantity, `has`, `count`, `get`, `getItems`, `isFull`, `isEmpty`, `clear`, and `size`.
- Stackable items are consolidated into a single slot up to their max stack size. New stacks are created only when needed.
- `Inventory.add` and `Inventory.remove` return `false` when an operation cannot be completed atomically, ensuring items are not silently lost or partially mutated.
- `EquipmentSlot` defines `WEAPON` and `ARMOUR` slots, mapped from `ItemType`.
- `Equipment` owns the currently equipped items and supports atomic equip/unequip operations that interact with the inventory.
- `PrototypeItem` represents a collectible world item. It implements `Interactable`, so it uses the existing interaction system.
- `LootDrop` pairs an item template with a drop chance.
- `LootTable` defines a list of possible drops and a maximum number of drops per generation.
- `LootGenerator` uses a shared `Random` instance to evaluate a `LootTable` and return independent item instances.
- `GameWorld` creates world items, handles collection, and removes collected items from the world and collision bounds. The Hero starts with a `Basic Sword` and `Leather Armour` equipped.
- Enemies have a basic loot table with a chance to drop Healing Potions, equipment, and quest relics (up to two items per enemy).
- `ConsumableController` maps consumable item IDs to `ConsumableEffect` implementations. It handles `H` key input to use a Healing Potion, applies the effect, decrements the stack by one, and rolls back the inventory change if the effect cannot be applied.
- `HealingEffect` restores a fixed amount of HP, clamped to the Hero's calculated maximum health. It cannot be used while dead or at full health.
- If the inventory is full, a collected item stays in the world and an `Inventory full!` message is shown.

## Prototype Visuals

- Ground: dark green filled rectangle
- Grid: green lines
- Obstacles: brown rectangles
- NPC: blue rectangle
- Enemies: red rectangles
- Items: small green rectangles
- Hero: gold rectangle
- Attack area: orange rectangle briefly shown on attack

## Manual Verification

1. Run `.\gradlew.bat lwjgl3:run` from the project root.
2. Confirm the Hero, NPC, enemies, obstacles, and grid are visible.
3. Move with WASD and arrow keys; confirm diagonal movement is normalized.
4. Walk into obstacles and confirm wall sliding.
5. Travel to world edges and confirm the Hero stays inside.
6. Approach the NPC until `[E] Interact` appears, press `E`, and confirm `Hello, Hero!` displays.
7. Approach an enemy from outside its aggro range and confirm it stays idle.
8. Move within range and confirm the enemy changes to `CHASING` and moves toward the Hero.
9. Let the enemy approach and confirm it stops at the configured stopping distance.
10. Press `Space` while facing an enemy within attack range and confirm damage/HP feedback.
11. Hold `Space` and confirm attacks do not repeat until the cooldown expires.
12. Attack until the enemy is defeated and confirm it disappears and stops moving.
13. Allow an enemy to reach attack range and confirm it attacks periodically with a cooldown.
14. Confirm Hero HP decreases on each enemy attack and the Hero eventually dies if not moved away.
15. Confirm a dead Hero cannot move, attack, or interact.
16. Confirm enemies stop chasing and attacking a dead Hero.
17. Approach a green item rectangle until `[E] Interact` appears, press `E`, and confirm pickup feedback.
18. Confirm the item disappears from the world.
19. Press `I` to toggle the inventory display and confirm the collected item is listed.
20. Collect a second item and confirm the inventory count increases.
21. Confirm stackable items (e.g. Healing Potion) merge into a single slot with quantity shown.
22. Confirm a full inventory prevents pickup and shows `Inventory full!`.
23. Confirm a dead Hero cannot collect items.
24. Confirm the inventory display lists equipped `Basic Sword` and `Leather Armour`.
25. Confirm the stats line shows the weapon damage and armour contributions.
26. Confirm enemy melee damage is reduced by the equipped armour.
27. Confirm Hero attack damage is increased by the equipped weapon.
28. Confirm no runtime exceptions occur.
29. Defeat an enemy and confirm small green loot rectangles appear near its death position.
30. Confirm the `Enemy defeated! Loot dropped!` message appears when loot is generated.
31. Approach a dropped item and press `E` to collect it; confirm it appears in the inventory.
32. Confirm equipment loot does not auto-equip.
33. Confirm inventory-full behaviour preserves dropped loot in the world.
34. Allow an enemy to damage the Hero, then press `H`; confirm the Hero heals and the Healing Potion quantity decreases by one.
35. Confirm health does not exceed maximum when healing near full health.
36. Confirm `H` at full health shows `Health is already full.` and does not consume a potion.
37. Confirm a dead Hero cannot use potions.
38. Confirm the final potion in a stack removes the item from inventory.

## Automated Verification

The project is verified with:

```powershell
$env:GRADLE_OPTS="-Xmx512m -XX:MaxMetaspaceSize=256m"
.\gradlew.bat build --no-daemon
.\gradlew.bat lwjgl3:run --no-daemon
git diff --check
```

No test framework is currently configured, so no new testing dependency was introduced.

## Scope

The current implementation uses LibGDX primitive geometry only. It does not include Box2D, Tiled, external assets, combat XP/loot, skills, item rarity, item icons, advanced stat systems, temporary buffs/status effects, mana/energy, multiple consumable types, potion hotbar, merchants, shops, crafting, advanced AI behaviors (patrols, flocking, bosses), save/load, audio, networking, or a final UI/HUD.
