# Milestone 12 — Tiled/Data Foundation

## Objective

Establish the data-driven foundation needed to migrate WesKaap away from hard-coded world/interior definitions while keeping all existing M11 functionality and the completed L1 Retreat Chapel intact.

This milestone is **not** the full GameWorld refactor. It introduces:

- a clear coordinate contract between Tiled and the game world,
- a lightweight data model for loaded areas,
- a standalone TMX loader that parses the existing `retreat_location.tmx`,
- unit tests proving the loader and coordinate conversion.

## TMX Structure Discovered

Source file: `assets/retreat_location.tmx`

| Attribute | Value |
| --- | --- |
| Orientation | orthogonal |
| Render order | right-down |
| Map size | 50 x 50 tiles |
| Tile size | 32 x 32 px |
| Pixel size | 1600 x 1600 px |
| Tileset | `retreat_placeholders.tsx` |

Layers:

| Layer | Type | Contents |
| --- | --- | --- |
| `Ground` | tile | mostly empty/0 |
| `Roads` | tile | a single horizontal road strip |
| `Buildings` | tile | building footprint tiles (gid 4) and door tiles (gid 7) |
| `Environment` | tile | decorative tiles (gid 6) |
| `Collision` | objectgroup | 4 rectangles: 3 building footprints, 1 fence |
| `Objects` | objectgroup | 3 entrances (with `interiorId` properties), 2 landmarks, 1 vehicle |
| `Spawns` | objectgroup | 1 player spawn |

### Parsed object details

**Collision**

| Name | Tiled X/Y | Tiled W/H | Purpose |
| --- | --- | --- | --- |
| `Building_House_1` | 192, 96 | 160 x 96 | house footprint |
| `Building_Shop_1` | 992, 96 | 160 x 96 | shop footprint |
| `Building_House_2` | 992, 864 | 160 x 96 | house footprint |
| `Fence_Left` | 192, 224 | 160 x 32 | fence obstacle |

**Objects**

| Name | Type | Tiled X/Y | Tiled W/H | Properties |
| --- | --- | --- | --- | --- |
| `House1_Entrance` | `entrance` | 256, 192 | 32 x 32 | `interiorId = retreat_house_1_interior` |
| `Shop1_Entrance` | `entrance` | 1056, 192 | 32 x 32 | `interiorId = retreat_shop_1_interior` |
| `Landmark_TableMountain` | `landmark` | 640, 0 | 320 x 64 | — |
| `Landmark_School` | `landmark` | 1200, 400 | 250 x 150 | — |
| `Vehicle_Minibus` | `vehicle` | 512, 800 | 96 x 48 | — |

**Spawns**

| Name | Tiled X/Y | Tiled W/H |
| --- | --- | --- |
| `PlayerSpawn` | 800, 864 | 32 x 32 |

## Coordinate Contract

Tiled conventions:

- Origin: top-left of the map.
- X increases to the right.
- Y increases downward.
- Object bounds are in pixels relative to the map origin.

WesKaap game-world conventions:

- Origin: bottom-left of the world.
- X increases to the right.
- Y increases upward.
- World units are larger than pixels.

Conversion (implemented in `TiledCoordinateConverter`):

- `worldX = tiledX * scale`
- `worldY = (mapPixelHeight - (tiledY + tiledHeight)) * scale`
- `worldWidth = tiledWidth * scale`
- `worldHeight = tiledHeight * scale`

M12 uses a uniform scale of **2.0 world units per pixel**. This maps the 1600 x 1600 pixel TMX to a 3200 x 3200 world-unit area. Note that this currently exceeds `GameWorld.HEIGHT` (2400), which is a known limitation described below.

## Loader Architecture

```text
retreat_location.tmx
        ↓
   TiledAreaLoader  (com.weskaap.game.tiled)
        ↓
   TiledCoordinateConverter
        ↓
     AreaData
        ↓
  future M13 systems
```

The loader is a pure data-loading layer. It does **not** handle:

- combat,
- quests,
- dialogue,
- inventory,
- AI,
- rendering,
- player control.

It parses the TMX XML directly using `javax.xml` so it can be unit-tested without a libGDX runtime context.

## Data Model

New classes in `core/src/main/java/com/weskaap/game/tiled/`:

| Class | Responsibility |
| --- | --- |
| `TiledCoordinateConverter` | Converts Tiled pixel coordinates to world coordinates using the documented contract. |
| `AreaData` | Top-level loaded area: metadata, dimensions, collision bounds, buildings, objects, spawns. |
| `BuildingData` | Data-only building footprint, entrance position, interior id reference, and properties. |
| `MapObjectData` | Generic parsed object: id, name, type, bounds, position, and custom properties. |
| `SpawnData` | Spawn point: id, name, type, and world position. |

## Parsed Layers

| TMX Layer | Output | Notes |
| --- | --- | --- |
| `Ground` | not parsed yet | tile data is present but not consumed by M12 systems |
| `Roads` | not parsed yet | can be added in a later milestone |
| `Buildings` | not parsed yet | tile layer geometry is not currently consumed |
| `Environment` | not parsed yet | decorative layer |
| `Collision` | `AreaData.collisionBounds` | all rectangles converted to world units |
| `Objects` | `AreaData.objects` + `AreaData.buildings` | objects with `type="building"` become `BuildingData`; remainder are `MapObjectData` |
| `Spawns` | `AreaData.spawns` | converted to world-centre positions |

## L1 / M11 Compatibility

No existing gameplay code was changed for M12. The completed L1 Retreat Chapel remains fully functional:

- chapel entrance/exit,
- interior movement and jumping,
- tutorial rat combat,
- altar/storage/prayer/basement interactions,
- quest progression and rewards,
- exterior return position.

The new loader is standalone and is not yet wired into `GameWorld` or `WorldRenderer3D`. Hard-coded data continues to drive the runtime.

## Remaining Hard-Coded Data

The following are still hard-coded and will be addressed in M13:

- `GameWorld.WIDTH` / `GameWorld.HEIGHT`
- `GameWorld` area/building/enemy/item construction
- `ChapelInteriorData`
- all runtime entity creation and quest wiring
- renderer geometry generation

The TMX `retreat_location.tmx` does not yet contain data for the Retreat Chapel, so the chapel remains Java-defined for now.

## Deferred M13 Work

- Wire `AreaData` into `GameWorld` / `AreaRegistry`.
- Synthesize `Building` instances from `BuildingData` and connect them to existing `Interior` definitions.
- Load roads, ground, and environment tiles into the renderer.
- Resolve the TMX 1:1 aspect ratio vs. the game world's 4:3 aspect ratio.
- Migrate `ChapelInteriorData` into a data-driven format (Tiled objectgroup or JSON).
- Extract interior enemy/loot spawn logic into a dedicated data-driven system.

## Known Limitations

- The TMX is a structural sketch: most tile layers are empty or only partially decorated.
- The 1600 x 1600 pixel TMX at scale 2.0 produces a 3200 x 3200 world area, but `GameWorld.HEIGHT` is currently 2400. The extra vertical extent is a mismatch that must be resolved when the data is wired into gameplay.
- No `type="building"` objects exist in the TMX; `BuildingData` is produced only when such objects are present. Current entrances are `type="entrance"` and remain as `MapObjectData`.
- Tile layers (`Ground`, `Roads`, `Buildings`, `Environment`) are not parsed because M12 focuses on the object data contract.
- The loader is not yet used at runtime.

## Files Added/Changed

**New source**

- `core/src/main/java/com/weskaap/game/tiled/TiledCoordinateConverter.java`
- `core/src/main/java/com/weskaap/game/tiled/AreaData.java`
- `core/src/main/java/com/weskaap/game/tiled/BuildingData.java`
- `core/src/main/java/com/weskaap/game/tiled/MapObjectData.java`
- `core/src/main/java/com/weskaap/game/tiled/SpawnData.java`
- `core/src/main/java/com/weskaap/game/tiled/TiledAreaLoader.java`

**New tests**

- `core/src/test/java/com/weskaap/game/tiled/TiledCoordinateConverterTest.java`
- `core/src/test/java/com/weskaap/game/tiled/TiledAreaLoaderTest.java`
- `core/src/test/resources/retreat_location.tmx` (copy for unit tests)

**New docs**

- `docs/m12-tiled-data-foundation.md`
