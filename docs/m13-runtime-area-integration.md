# Milestone 13 — Runtime Area Integration

## 1. Implementation Summary

M13 wires the M12 Tiled data foundation into the live WesKaap runtime for the
Retreat exterior. The Retreat area now loads from `assets/retreat_location.tmx`
instead of relying entirely on hard-coded geometry. Existing M11/L1 gameplay
(buildings, interiors, NPCs, combat, items, quests) is preserved by layering the
hard-coded entities on top of the TMX-derived base.

Key changes:

- `GameWorld.HEIGHT` updated to `3200f` so the converted 3200 × 3200 TMX area fits
  the world bounds without silent cropping or distortion.
- `AreaRegistry` can now load and retrieve raw `AreaData`.
- `RetreatAreaFactory` converts `AreaData` into the existing runtime
  `AreaDefinition`.
- `GameWorld` loads Retreat through the new pipeline and applies it at startup.

## 2. Runtime Architecture

```text
retreat_location.tmx
        ↓
   TiledAreaLoader
        ↓
     AreaData
        ↓
   AreaRegistry.loadArea(...)
        ↓
  RetreatAreaFactory
        ↓
  AreaDefinition
        ↓
   GameWorld.loadArea(...)
        ↓
  existing gameplay systems
```

## 3. Retreat Integration

`GameWorld.createAreaRegistry()` now:

1. Creates an `AreaRegistry`.
2. Loads `AreaData` for `AreaId.RETREAT` from `retreat_location.tmx`.
3. Builds an `AreaDefinition` via `RetreatAreaFactory`.
4. Registers the runtime Retreat area plus the existing CPUT and Rosebank areas.

At the end of the `GameWorld` constructor, `loadArea(activeAreaDefinition)` is
called so the active area data is applied to runtime state.

## 4. Collision Integration

`AreaData.getCollisionBounds()` is fed directly into `AreaDefinition.obstacles`.
The four TMX collision rectangles now act as world obstacles for Retreat:

- `Building_House_1` → (384, 2816, 320, 192)
- `Building_Shop_1` → (1984, 2816, 320, 192)
- `Building_House_2` → (1984, 1280, 320, 192)
- `Fence_Left` → (384, 2688, 320, 64)

The existing collision system consumes these rectangles without modification.

## 5. Spawn Integration

The TMX `PlayerSpawn` object at (800, 864) with size 32 × 32 is converted to a
world-centre position of (1632, 1440). `RetreatAreaFactory` uses it as the
Retreat `AreaDefinition.spawnPoint`, and `GameWorld.loadArea(...)` places the
hero there at startup.

If the TMX ever lacks a `PlayerSpawn`, the factory falls back to the world centre.

## 6. Entrance Integration

The TMX `Objects` layer entrance markers (`House1_Entrance`, `Shop1_Entrance`)
are parsed and preserved in `AreaData`. Their `interiorId` properties are
intact.

They are **not** yet turned into live `BuildingEntrance` instances because the
referenced interiors (`retreat_house_1_interior`, `retreat_shop_1_interior`) do
not exist in the current project. Converting them would require inventing missing
interior data, which is out of M13 scope.

The existing Java-defined buildings (Tubby Angel's House, Older Sister's House,
Retreat Chapel) continue to provide their own entrances and interiors.

## 7. Building Integration

The TMX does not contain `type="building"` objects, so `AreaData.buildings` is
empty and no `BuildingData` is synthesised. The runtime Retreat area reuses the
existing Java buildings.

This means there is currently a content-level gap: the TMX building footprints
and the Java building positions do not match. This is documented as an M13
limitation and will be resolved when the TMX is extended with proper building
objects and the buildings are migrated to data.

## 8. Coordinate / World Size Handling

M12 established a uniform scale of 2.0, converting the 1600 × 1600 pixel TMX to
a 3200 × 3200 world-unit area. The previous `GameWorld.HEIGHT` was 2400, which
would have silently cropped the northern part of the converted TMX.

M13 resolves this by updating `GameWorld.HEIGHT` to `3200f`. `GameWorld.WIDTH`
was already `3200f`, so the world is now a consistent 3200 × 3200 square that
matches the scaled TMX. CPUT and Rosebank areas remain within these bounds.

No per-area coordinate system or clipping was introduced; the global world
bounds were expanded to match the authoritative Tiled data.

## 9. Renderer Integration

`WorldRenderer3D` was not modified. It already derives ground size and obstacle
rendering from `GameWorld.WIDTH` / `GameWorld.HEIGHT` and the active obstacle list,
so the expanded world and TMX obstacles are rendered automatically using the
existing primitive placeholder geometry.

Tile layers (`Ground`, `Roads`, `Buildings`, `Environment`) remain unrendered;
road rendering still uses the existing hard-coded `retreatRoads` list.

## 10. L1 Compatibility

The completed L1 Retreat Chapel remains fully Java-defined and functional:

- chapel entrance/exit,
- interior movement and jumping,
- tutorial rat combat,
- altar, storage crate, prayer clue, basement discovery,
- quest progression and rewards,
- exterior return position.

No L1 code was changed.

## 11. M11 Regression Results

All M11 systems remain intact:

- building/interior entry and exit,
- travel between areas,
- dialogue and dialogue-quest integration,
- quest progression and rewards,
- combat and enemy AI,
- inventory/equipment,
- economy/wallet,
- story/ally state,
- 3D isometric camera and rendering.

The automated test suite passes with no failures.

## 12. Tests

New and updated tests:

- `AreaRegistryTest` — verifies `AreaRegistry.loadArea(...)` stores `AreaData`
  and that the loaded Retreat data contains expected collision, spawn, and
  entrance data.
- `RetreatAreaFactoryTest` — verifies conversion from `AreaData` to runtime
  `AreaDefinition`, including spawn position, collision integration, and
  preservation of hard-coded buildings.

All existing tests continue to pass.

## 13. Remaining Hard-Coded Data

- Java-defined buildings: Tubby Angel's House, Older Sister's House, Retreat Chapel.
- Java-defined NPCs, enemies, items, and roads for Retreat.
- CPUT and Rosebank areas remain fully hard-coded.
- Interiors and interior entities remain Java-defined.
- Quest and dialogue content remain Java-defined.

## 14. Deferred Work

- Full migration of all Retreat entities to Tiled/data-driven definitions.
- Creation of interiors for TMX entrances (`retreat_house_1_interior`,
  `retreat_shop_1_interior`).
- Conversion of TMX entrance objects into live `BuildingEntrance` instances once
  interiors exist.
- Parsing and rendering of tile layers (`Ground`, `Roads`, `Buildings`,
  `Environment`).
- Migration of CPUT and Rosebank to the data-driven pipeline.
- Larger `GameWorld` decomposition/refactor beyond the small M13 integration
  boundary.

## 15. Files Added/Changed

**Modified**

- `core/src/main/java/com/weskaap/game/world/AreaRegistry.java`
  - added `AreaData` loading and retrieval.
- `core/src/main/java/com/weskaap/game/world/GameWorld.java`
  - updated `HEIGHT` to `3200f`;
  - loads Retreat `AreaData` and builds `AreaDefinition` via `RetreatAreaFactory`;
  - applies the active area at startup.

**New**

- `core/src/main/java/com/weskaap/game/world/RetreatAreaFactory.java`
  - converts Retreat `AreaData` into a runtime `AreaDefinition`.
- `core/src/test/java/com/weskaap/game/world/RetreatAreaFactoryTest.java`
- `docs/m13-runtime-area-integration.md`

**Updated tests**

- `core/src/test/java/com/weskaap/game/world/AreaRegistryTest.java`

## 16. Git Status

See the final report for the current working-tree status.
