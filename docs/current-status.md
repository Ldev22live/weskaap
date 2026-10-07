# WesKaap — Current Project Status

> Generated: 2026-10-07
> Based on repository state and a full `gradlew build` + `gradlew test` run.

## 1. Executive Summary

WesKaap is a story-driven 2.5D/isometric action RPG built with **Java + libGDX** and generated from `gdx-liftoff`. The codebase has moved well beyond the original gdx-liftoff template. The current prototype implements **milestones M1 through M11** (per `walkthrough.md`) and contains a working overworld, combat, inventory/equipment, loot, consumables, quests, dialogue, buildings/interiors, fast-travel between areas, an ally foundation, and a 3D isometric camera.

**Overall health: functional prototype with a large surface area, but architectural consolidation is needed before the next major milestone.**

## 2. Project Structure

| Module | Purpose | State |
| --- | --- | --- |
| `core` | All game logic, domain models, and tests | Active; majority of the code lives here |
| `lwjgl3` | Desktop launcher (LWJGL3) | Generated template, not heavily modified |
| `assets` | Graphics, maps, concept art | Mostly placeholders and concept pieces |
| `docs` | Project documentation | Empty except this file |

Key documents in the repo root:

- `README.md` — libGDX/Gradle quick-start.
- `walkthrough.md` — design and manual-verification guide for M1–M11.
- `world-visual-bible.md` — art direction, location list, and visual identity.

## 3. Build & Test Status

| Check | Result | Notes |
| --- | --- | --- |
| `gradlew.bat build --no-daemon` | PASS | Exits 0 |
| `gradlew.bat test --no-daemon` | PASS | 14 test classes run |
| Java source compatibility | 21 | Configured in root `build.gradle` |

The project currently uses:

- libGDX `1.14.2`
- LWJGL3 `3.4.1`
- KTX `1.13.1-rc1`
- Fleks `2.14`
- JUnit 5 for unit tests

## 4. What Is Implemented

### 4.1 Core Gameplay Loop
- **Hero movement** — WASD / arrow keys, normalized diagonal movement, facing direction derived from input.
- **Collision** — axis-separated resolver with world bounds clamping, wall sliding, anti-tunneling, and dynamic obstacle lists.
- **Combat** — basic melee attack with cooldown, directional hit area, damage application, and death handling.
- **Enemy AI** — per-enemy `IDLE`/`CHASING` state, Hero detection, pursuit, stopping distance, and melee attack cooldown.
- **Death handling** — dead Hero cannot move/attack/interact; enemies stop attacking dead Heroes.

### 4.2 Items, Inventory & Equipment
- Item types: `WEAPON`, `ARMOUR`, `CONSUMABLE`, `QUEST`, `MISC`.
- Stackable items with max stack sizes.
- Atomic add/remove; inventory-full feedback preserved.
- `EquipmentSlot.WEAPON` / `ARMOUR`, stat contribution to Hero attack, armour, max health, and movement speed.
- Hero starts with a `Basic Sword` and `Leather Armour`.

### 4.3 Loot & Consumables
- `LootTable` / `LootDrop` / `LootGenerator` foundation.
- Enemies drop loot on death (healing potions, equipment, quest relics).
- `ConsumableController` maps item IDs to effects; `H` uses a Healing Potion.
- Healing clamps to max health; full-health and dead-Hero cases are guarded.

### 4.4 Quest System
- Data-oriented quest model: `Quest`, `QuestObjective`, `QuestLog`, `QuestController`.
- Objective types include `TALK_TO_NPC`, `ENTER_BUILDING`, `LEAVE_BUILDING`, `COLLECT_ITEM`, `REACH_LOCATION`, `ENTER_AREA`, `LEAVE_AREA`, `DEFEAT_ENEMY`.
- Quest rewards foundation exists (`QuestReward`, `QuestRewardService`, `QuestRewardType`) wired to wallet/inventory/story state.
- Quest progress is reported back as events/results so UI/domain stay separated.
- Prototype quests: `First Steps`, `Meet the Neighbour`, plus `tubby_angel_001` from `QuestRepository`.

### 4.5 Dialogue & Interaction
- `Dialogue`, `DialogueNode`, `DialogueOption`, `DialogueController`, `DialogueInputController`.
- `DialogueRepository` provides NPC-specific dialogues, placeholder dialogues, quest-offer/status dialogues, and the Tubby Angel household dialogue.
- `PrototypeNpc` supports linked quests, available/active/completed quest queries, and optional dialogue.
- Building entrances/exits, travel points, and world items all implement `Interactable`.

### 4.6 Buildings & Interiors
- `Building`, `Interior`, `BuildingEntrance`, `BuildingExit`.
- Two starter buildings in Retreat: **Tubby Angel's House** and **Older Sister's House**.
- Interior platformer mode with `PlatformerController` (horizontal movement + jump).
- Enter/exit transitions preserve exterior position.

### 4.7 World & Travel
- `AreaId` + `AreaDefinition` + `AreaRegistry` for region definitions.
- Three areas defined: `RETREAT`, `CPUT`, `ROSEBANK`.
- `TravelPoint` + `TravelController` + `TravelMenuStage` for fast-travel UI.
- `GameWorld` loads area data (obstacles, buildings, interactables, items, enemies) on transition.

### 4.8 Camera & Rendering
- `IsometricCamera` for 3D isometric overworld presentation.
- `WorldRenderer3D` renders the world via the 3D camera.
- `MainGameScreen` also keeps a legacy 2D `ShapeRenderer` fallback rendering path.
- `WorldCoordinateConverter` projects world positions to screen space for UI prompts and labels.

### 4.9 Story & Ally Foundations
- `StoryState` / `StoryStateSnapshot` for global narrative flags.
- `Ally` class; Rosebank friend is marked as an ally.
- `Wallet` exists but is not heavily exercised yet.

## 5. Current Code Quality & Architecture Observations

### Strengths
- **Clear package boundaries** — domain packages (`player`, `enemy`, `quest`, `dialogue`, `inventory`, `equipment`, `travel`, `world`, etc.) are well separated.
- **Test coverage exists** — 14 test classes covering ally, building, dialogue, interaction, quest, travel, world, and platformer behavior.
- **Build is green** — both `build` and `test` pass.
- **Design documentation is present** — `walkthrough.md` and `world-visual-bible.md` give strong context.

### Risks / Technical Debt
1. **`GameWorld` is a God class** — `GameWorld.java` is ~1,020 lines and coordinates rendering, collision, AI, interaction, combat, loot, consumables, quests, dialogue, area transitions, and building enter/exit. This is the highest-risk file for future changes.
2. **Mixed rendering paths** — `GameWorld` still contains a legacy 2D `ShapeRenderer` `render()` method alongside `render3D()`. Keeping both increases maintenance cost.
3. **Asset pipeline is immature** — the game still uses primitive shapes. `assets/` contains concept art and an early Tiled map (`retreat_location.tmx`) but no production sprite/model pipeline.
4. **State mutation is centralized** — many sub-systems (`QuestController`, `DialogueController`, `TravelController`, `ConsumableController`) are updated inside `GameWorld.update()`, making isolation harder.
5. **Hard-coded world data** — obstacles, buildings, NPC positions, quests, and area definitions are created in `GameWorld` methods. As the world grows, this should move to data files (Tiled/JSON) loaded by the `AreaRegistry`.
6. **JVM crash artifacts present** — several `hs_err_pid*.log` and `replay_pid*.log` files are in the repo root. These indicate recent native/JVM crashes during local runs and should be investigated/removed. They are currently ignored by `.gitignore`, but they clutter the working directory and may signal GPU/driver/memory issues.
7. **Large uncommitted change set** — many modified files and new files are not committed. The current working tree appears to contain the M11 work in progress or freshly completed. Committing and documenting this milestone would reduce risk.

## 6. Asset & Visual Status

| Asset | State |
| --- | --- |
| In-game visuals | Primitive rectangles only (colored shapes) |
| `assets/weskaap_tiles.png` | Early tileset (used by `retreat_location.tmx`) |
| `assets/retreat_location.tmx` | Started Tiled overworld map for Retreat |
| `assets/retreat_placeholders.tsx` | Placeholder tileset definition |
| `assets/concept-art/characters/hero_3D_concept_idea.blend` | Hero 3D concept |
| `assets/concept-art/characters/hero_2D_concept_idea.png` | Hero 2D concept |
| `assets/concept-art/locations/retreat_concept_game.png` | Retreat environment concept |

No production 3D models, character sprites, animations, audio, or final UI artwork are integrated yet.

## 7. Git Status (Working Tree)

A significant amount of work is uncommitted:

- **Modified files**: `Hero.java`, `HeroController.java`, `GameWorld.java`, `MainGameScreen.java`, `QuestController.java`, `QuestLog.java`, `Quest.java`, `QuestObjective.java`, `QuestObjectiveType.java`, `QuestRepository.java`, `QuestState.java`, `DialogueController.java`, `DialogueRepository.java`, `Interactable.java`, `PrototypeNpc.java`, `DialogueStage.java`, `GameHud.java`, `InteractionPrompt.java`, `IsometricCamera.java`, `WorldRenderer3D.java`.
- **New files (untracked)**: entire new packages `ally`, `building`, `economy`, `story`, `travel`, `world3d`, plus new UI and quest classes, Tiled assets, and Blender/Python concept files.

Recommendation: stage and commit this milestone with a clear message such as `Milestone 11: isometric camera, buildings, interiors, travel, quest rewards, and dialogue foundation`.

## 8. Known Limitations

- Only `RETREAT` is functionally populated; `CPUT` and `ROSEBANK` are placeholder areas.
- Only two prototype buildings exist.
- Dialogue is mostly linear; branching choice support exists (`DialogueOption`) but is not heavily used.
- No save/load, no persistence.
- No audio.
- No final UI/UX; HUD is text-based.
- No real 3D assets; 3D camera is rendering primitives or placeholder geometry.
- `Wallet`/`StoryState` are wired but not central to gameplay yet.

## 9. Next Logical Steps

1. **Commit the current milestone** to capture M11 work.
2. **Clean up JVM crash logs** from the working directory and investigate their cause (likely GPU/driver related given the 3D camera work).
3. **Refactor `GameWorld`** — extract area loading, rendering, and update-phase orchestration into smaller services/managers.
4. **Pick a single art pipeline** — decide whether to keep `ShapeRenderer` fallback or commit fully to `WorldRenderer3D`, and load the Tiled map for Retreat instead of hard-coded obstacles.
5. **Flesh out one complete vertical slice** — e.g., finish Retreat with Tiled map, proper NPCs, one full quest chain, and a building interior.
6. **Add save/load foundation** before the world grows much larger.
7. **Integrate real assets** (Blender → Tiled → LibGDX) once the pipeline is proven.

## 10. Verification Commands

```powershell
# Build everything
.\gradlew.bat build --no-daemon

# Run unit tests
.\gradlew.bat test --no-daemon

# Run the desktop game
.\gradlew.bat lwjgl3:run --no-daemon
```

Both build and test pass as of this status report.
