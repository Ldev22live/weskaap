# Act 1 — L1: Retreat Church / Chapel

## Overview

The Retreat Church / Chapel is the first playable interior level in Act 1. It serves as a compact tutorial for interior movement, basic combat, interaction, and the beginning of the supernatural mystery in Retreat.

## Purpose

- Introduce players to interior exploration.
- Provide the first deliberate combat encounter.
- Teach interaction with environmental objects.
- Establish the first story clue that something is wrong in Retreat.
- Reuse all existing M11 systems without architectural redesign.

## Location

- **Area:** Retreat (exterior)
- **Building ID:** `retreat_chapel`
- **Exterior footprint:** 260 x 200 world units
- **Interior size:** 680 x 480 world units
- **Position in Retreat world:** (1550, 950)

## Interior Layout

The chapel interior is divided into connected sections along a single horizontal plane. It uses the existing platformer movement system.

```
+------------------------------------------------------------+
| Prayer Room |           Main Chapel          |  Altar   |
|  (clue)     |   (pew obstacle + first enemy)   | (object) |
+------------ +----------------------------------+----------+
|             | Storage / Side Hall (loot)       |          |
|             +----------------------------------+          |
|                             |                             |
|                          Entrance                        |
|                          / Exit                           |
+------------------------------------------------------------+
```

### Sections

1. **Entrance / Exit**
   - Player spawns near the front door.
   - Building exit interactable returns the player to the exterior at the chapel entrance.

2. **Main Chapel**
   - Central open floor.
   - One pew obstacle blocks direct movement.
   - Altar platform on the far right.
   - The first combat encounter (one tutorial rat) is placed here.

3. **Side Hall / Storage**
   - Small area off the main chapel.
   - Contains a storage crate with a one-time healing potion.

4. **Prayer Room**
   - Quiet side room.
   - Contains a journal page with the first supernatural clue.

5. **Basement**
   - Represented as a lower connected section using existing platform/elevation geometry.
   - Contains a discovery interactable (fresh footprint in dust).
   - No vertical level transition is implemented; the basement is a contiguous part of the same interior.

## Gameplay Flow

1. Player enters the chapel.
2. A short intro dialogue plays automatically.
3. Player explores the main chapel.
4. Player defeats the chapel rat (tutorial combat).
5. Player inspects the altar.
6. Player optionally searches the storage crate for a one-time healing potion.
7. Player reads the journal clue in the prayer room.
8. Player inspects the basement discovery.
9. Player exits the chapel.

## Entrance / Exit Behaviour

- Reuses `BuildingEntrance` / `BuildingExit` from the existing building system.
- The exterior return position is derived from the entrance using `Building.DOOR_RETURN_OFFSET`.
- When the player enters, the intro dialogue is started automatically.
- Exterior enemies are paused while inside; interior enemies become active only inside the chapel.

## Combat Encounter

- **Enemy:** one tutorial rat (`chapel_rat`).
- **Health:** 40 HP.
- **Behaviour:** uses the existing `EnemyAiController` (chase + attack).
- **Difficulty:** deliberately easy; the player starts with a sword and armour.
- **Reward:** none from the rat directly; the quest grants money and a story flag.

## Interactables

| ID | Type | Purpose | Location |
| --- | --- | --- | --- |
| `retreat_chapel_altar` | Altar | Main objective; confirms the altar was disturbed. | Main chapel, raised platform |
| `retreat_chapel_storage` | Storage Crate | Optional one-time loot: healing potion. | Side hall |
| `retreat_chapel_prayer_clue` | Journal Page | Story clue about noises from below. | Prayer room |
| `retreat_chapel_basement_discovery` | Footprints | Discovery that someone recently used the basement. | Basement area |
| chapel exit | `BuildingExit` | Returns player to Retreat exterior. | Near entrance |

## Quest / Story Integration

- **Quest ID:** `act1_l1_chapel`
- **Title:** *The Chapel*
- **Objectives:**
  1. Enter the Retreat Chapel.
  2. Defeat the creature in the chapel.
  3. Inspect the altar.
  4. Find the prayer room clue.
  5. Discover the basement trail.
- **Rewards:**
  - `StoryFlag.SUPERNATURAL_MYSTERY_INTRODUCED`
  - 15 money
- The quest is added to the player's log at game start.

## Systems Reused

| System | Usage |
| --- | --- |
| `Building` / `Interior` / `BuildingEntrance` / `BuildingExit` | Chapel shell and transition |
| `PlatformerController` | Interior movement and jumping |
| `HeroController` | Movement input (jump key changed to W/UP to avoid SPACE conflict with attack) |
| `InteractionController` | E-key interaction with objects |
| `CombatController` | Player attack inside the chapel |
| `EnemyAiController` / `PrototypeEnemy` | Chapel rat behaviour |
| `QuestController` / `QuestEvent` | Objective tracking |
| `DialogueController` / `DialogueRepository` | Chapel intro dialogue |
| `WorldRenderer3D` | 3D box rendering of interior, platforms, obstacles, enemy, and attack |

## Files Changed / Added

- `core/src/main/java/com/weskaap/game/chapel/ChapelInteriorData.java`
- `core/src/main/java/com/weskaap/game/building/Interior.java`
- `core/src/main/java/com/weskaap/game/enemy/PrototypeEnemy.java`
- `core/src/main/java/com/weskaap/game/interaction/Interactable.java`
- `core/src/main/java/com/weskaap/game/player/HeroController.java`
- `core/src/main/java/com/weskaap/game/world/GameWorld.java`
- `core/src/main/java/com/weskaap/game/world3d/WorldRenderer3D.java`
- `core/src/test/java/com/weskaap/game/chapel/ChapelInteriorDataTest.java`
- `docs/act1-l1-chapel.md`

## Known Limitations

- The chapel is constructed with hard-coded data in `ChapelInteriorData.java`. It is intentionally isolated so it can be migrated to the M12 Tiled/data-driven loader.
- The basement is implemented as a contiguous section of the same interior, not a separate level or vertical transition.
- Interior enemies and loot are not yet loaded from external data.
- The 3D renderer still uses primitive placeholder boxes.

## Future Migration (M12 / M13)

- Move `ChapelInteriorData` constants and object positions into a Tiled objectgroup or JSON data file.
- Replace hard-coded `GameWorld` chapel integration with an `AreaLoader` that reads the data file.
- Extract interior enemy spawn logic into a dedicated system as part of the larger `GameWorld` refactor.
