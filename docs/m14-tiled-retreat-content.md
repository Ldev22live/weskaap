# Milestone 14 — Tiled Retreat Content Integration

## M14 Objective

Make `assets/retreat_location.tmx` the authoritative source for Retreat overworld
geometry while preserving all M11/M12/M13 functionality and the L1 Chapel.

M14 is a content/runtime integration milestone, not a refactor.

## TMX Layers Used

| Layer | Type | M14 usage |
| --- | --- | --- |
| `Ground` | tile | Parsed into `TileLayerData`; currently all zeros. |
| `Roads` | tile | Parsed and converted to runtime ground-feature rectangles. |
| `Buildings` | tile | Parsed into `TileLayerData`; **not** consumed at runtime because building footprints are already covered by the collision layer and Java buildings. |
| `Environment` | tile | Parsed and converted to runtime ground-feature rectangles. |
| `Collision` | objectgroup | Converted to runtime obstacle bounds (M13). |
| `Objects` | objectgroup | Entrances become `TiledEntrance` markers; landmarks/vehicle remain raw `MapObjectData`. |
| `Spawns` | objectgroup | `PlayerSpawn` drives the Retreat spawn point (M13). |

## Coordinate Handling

Unchanged from M12/M13:

- Tiled origin top-left, Y-down; game origin bottom-left, Y-up.
- Uniform scale `2.0` world units per pixel.
- 1600 × 1600 px TMX → 3200 × 3200 world units.

Tile-layer conversion merges each horizontal run of equal non-zero GIDs into one
world rectangle via `TileLayerAdapter`, reusing `TiledCoordinateConverter` for
all coordinate math.

## World Dimensions

`GameWorld.HEIGHT` remains `3200f` (updated in M13) so the full converted TMX
extent is in bounds. `GameWorld.WIDTH` stays `3200f`. No magic numbers were
introduced; the Retreat `AreaDefinition` dimensions come from
`AreaData.getWorldWidth()/getWorldHeight()`.

## Player Spawn

`Spawns` → `PlayerSpawn` (800, 864, 32×32 px) → world centre **(1632, 1440)** via
`TiledCoordinateConverter`. `RetreatAreaFactory.resolvePlayerSpawn` uses it as
the Retreat `AreaDefinition.spawnPoint`; `GameWorld.loadArea` places the hero
there at startup. Fallback remains the world centre if the spawn is missing.

## Collision

`Collision` objectgroup → `AreaData.collisionBounds` →
`AreaDefinition.obstacles` → existing obstacle/collision path. The four TMX
rectangles are the only source; the old `GameWorld.createObstacles()` hard-coded
list was removed because `loadArea` always replaced it before use.

| TMX object | World bounds (x, y, w, h) |
| --- | --- |
| `Building_House_1` | (384, 2816, 320, 192) |
| `Building_Shop_1` | (1984, 2816, 320, 192) |
| `Building_House_2` | (1984, 1280, 320, 192) |
| `Fence_Left` | (384, 2688, 320, 64) |

## Building / Entrance Mapping

### Buildings

The TMX contains no `type="building"` objects, so no `BuildingData` is produced
and no runtime `Building` is fabricated. Java buildings (Tubby Angel's House,
Older Sister's House, Retreat Chapel) remain the runtime buildings. A minimal
Tiled convention is established for future data: an object with
`type="building"` and an `interiorId` property becomes `BuildingData`; the
loader already honours this but the current TMX has none.

### Entrances

`House1_Entrance` and `Shop1_Entrance` (type `entrance`, each carrying an
`interiorId`) are converted to `TiledEntrance` interactable markers added to the
Retreat area. The markers:

- keep the Tiled name and `interiorId` for future wiring,
- render at the converted world position,
- return `"The door is locked."` on interaction — no transition and no invented
  interior content.

They will be replaced by real `BuildingEntrance` wiring once the referenced
interiors (`retreat_house_1_interior`, `retreat_shop_1_interior`) exist.

## Rendering Integration

No renderer changes. `WorldRenderer3D` already draws every rectangle in
`getAreaGroundFeatures()` as a flat road box. M14 feeds the TMX `Roads` and
`Environment` tile layers into that same list through `TileLayerAdapter`, so
Tiled content renders via the existing primitive pipeline.

| Layer | Resulting features |
| --- | --- |
| `Roads` | 2 merged strips: (0, 1536, 3200, 64) and (0, 1472, 3200, 64) |
| `Environment` | 8 small marker rectangles (sparse gid-6 tiles) |
| `Buildings` | not rendered as features (collision obstacles already show them) |
| `Ground` | empty — nothing to render |

The old `GameWorld.createRetreatRoads()` hard-coded rectangles were removed for
the same reason as `createObstacles()`: `loadArea` replaced them before use.

## Tests

New/updated tests:

- `TiledAreaLoaderTest.parsesTileLayers` — verifies 4 tile layers, dimensions,
  Roads gid-2 strip (rows 25–26), Buildings gid 4/7, Environment gid 6.
- `TileLayerAdapterTest` — run merging, empty layers, per-row rectangles, and
  the actual Roads strip → expected world rectangles.
- `RetreatAreaFactoryTest` — updated for the new signature; adds checks that
  Roads becomes a ground feature and that both TMX entrances become
  `TiledEntrance` markers with intact `interiorId` values.

Existing M12/M13/L1 tests unchanged and passing.

## Files Changed

- `core/src/main/java/com/weskaap/game/tiled/AreaData.java` — added
  `List<TileLayerData>` with getter and `getTileLayer(name)` lookup.
- `core/src/main/java/com/weskaap/game/tiled/TiledAreaLoader.java` — parses
  `<layer>` CSV data into `TileLayerData`.
- `core/src/main/java/com/weskaap/game/world/RetreatAreaFactory.java` — derives
  ground features from TMX layers and merges `TiledEntrance` markers.
- `core/src/main/java/com/weskaap/game/world/GameWorld.java` — removed dead
  hard-coded Retreat obstacle/road factory methods.
- `core/src/test/java/com/weskaap/game/world/RetreatAreaFactoryTest.java`
- `core/src/test/java/com/weskaap/game/tiled/TiledAreaLoaderTest.java`

## Files Added

- `core/src/main/java/com/weskaap/game/tiled/TileLayerData.java`
- `core/src/main/java/com/weskaap/game/tiled/TileLayerAdapter.java`
- `core/src/main/java/com/weskaap/game/interaction/TiledEntrance.java`
- `core/src/test/java/com/weskaap/game/tiled/TileLayerAdapterTest.java`
- `docs/m14-tiled-retreat-content.md`

## Known Limitations

- `Buildings` and `Ground` tile layers are parsed but not consumed at runtime.
- The TMX tile layer and object layer have minor sketch-level inconsistencies
  (e.g. `Building_Shop_1` collision at x=992 vs. tile footprint at x=1024; the
  `Shop1_Entrance` object at x=1056 vs. door tile at x=1120). These are data
  authoring issues to fix in the TMX later, not code defects.
- `TiledEntrance` markers do not transition anywhere; real interiors are still
  missing.
- Java building positions and TMX building footprints do not yet coincide.
- Environment markers render as generic flat features (no distinct visual).
- No tile-set texture rendering; all visuals remain primitive placeholders.

## Explicitly Deferred (M15+)

- `type="building"` objects in the TMX → runtime `Building` synthesis.
- Interiors for `retreat_house_1_interior` / `retreat_shop_1_interior` and
  `BuildingEntrance` wiring replacing `TiledEntrance`.
- `ChapelInteriorData` migration to data.
- Tile-set/texture-based rendering; per-layer render styles.
- Reconciling TMX footprints with Java building positions.
- `GameWorld` decomposition; CPUT/Rosebank data migration.
- L2 Abandoned House, new quests/enemies/art.
