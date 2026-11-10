# Weskaap Engine Architecture Proposal
**Phase 0: Engine-First Development**

> **Generated:** 2026-10-21  
> **Based on:** Full repository audit of Weskaap (commit 718de2c)  
> **Status:** Proposal awaiting approval

---

## 1. Executive Summary

This document proposes a modular, reusable Java game engine architecture to be extracted from the Weskaap codebase. The current Weskaap project contains a functional prototype with significant game-specific features, but lacks a clean separation between engine-level and game-level code. The proposed architecture will enable:

- **Multiple independent games** (2D, 2.5D, 3D) built on a shared engine foundation
- **Clear public APIs** with stable contracts for scene management, rendering, and resources
- **Explicit resource ownership** with predictable lifecycle rules
- **Versioned scene/resource formats** for tooling support
- **Testable engine components** independent of game logic

**Recommendation:** Start with a minimal 3-module foundation (engine-core, engine-scene, engine-render) and incrementally extract systems as needed. Do not create all 10 candidate modules upfront.

---

## 2. Verified Current Architecture

### 2.1 Repository State

| Aspect | Status |
|--------|--------|
| **Git branch** | `main` (up to date with origin/main) |
| **Working tree** | 1 modified file: `docs/act1-expanded.md` |
| **Recent commits** | Milestone 13, M11, 3D iso camera, Visual Bible |
| **Build status** | ✅ Passes (`gradlew build --no-daemon`) |
| **Test status** | ✅ Passes (17 test classes, JUnit 5) |

### 2.2 Technology Stack

| Component | Version | Notes |
|-----------|---------|-------|
| **Java** | 21 | Source/target compatibility |
| **Gradle** | (via wrapper) | Standard libGDX setup |
| **LibGDX** | 1.14.2 | Core game framework |
| **LWJGL3** | 3.4.1 | Desktop backend |
| **KTX** | 1.13.1-rc1 | Kotlin extensions (unused in Java code) |
| **Fleks** | 2.14 | ECS framework (declared but NOT used) |
| **JUnit** | 5.11.0 | Unit testing |

### 2.3 Module Structure

```
weskaap/
├── core/           # All game logic (35 Java classes in game/ package)
├── lwjgl3/         # Desktop launcher (2 Java files)
├── assets/         # TMX maps, placeholder tiles, concept art
└── docs/           # Design documentation
```

**Current dependency direction:** lwjgl3 → core → LibGDX

### 2.4 Application Entry Points & Lifecycle

**Entry point:** `Lwjgl3Launcher.main()` → `Lwjgl3Application(new WeskaapGame())`

**Lifecycle chain:**
```
Lwjgl3Launcher
  └─ WeskaapGame (extends Game)
       └─ MainGameScreen (implements Screen)
            ├─ show()
            ├─ render(float delta)
            ├─ resize(int width, int height)
            ├─ pause()
            ├─ resume()
            ├─ hide()
            └─ dispose()
```

**Game loop:** `MainGameScreen.render()` calls:
1. `world.update(delta)` - game logic
2. `worldCamera.update()` - camera tracking
3. `world.render3D(worldCamera)` - 3D rendering
4. UI rendering (SpriteBatch, Stage)

### 2.5 Rendering Implementation

**Current approach:** LibGDX 3D API with primitive shapes

- **WorldRenderer3D:** Uses `ModelBatch`, `ModelBuilder`, `ModelInstance`
- **Geometry:** All objects rendered as colored boxes (no textures, no sprites)
- **Lighting:** Simple directional light + ambient
- **Depth testing:** Enabled/disabled per render pass
- **Fallback:** Legacy 2D `ShapeRenderer.render()` still exists but unused

**Asset pipeline:** No `AssetManager` usage. TMX maps loaded via custom XML parser.

### 2.6 Camera System

**IsometricCamera** (game-specific):
- Wraps LibGDX `OrthographicCamera`
- Two modes: ISOMETRIC (overworld) and PLATFORMER (interiors)
- Follows hero position with configurable offset
- No camera shake, zoom tweening, or cinematic controls

### 2.7 Scene Management

**No scene graph or ECS.** Current architecture:

- **GameWorld:** God class (~1,070 lines) managing:
  - Hero, enemies, NPCs, items, buildings
  - Collision, AI, combat, dialogue, quests
  - Area transitions, building enter/exit
  - Rendering orchestration
- **Direct object references:** Lists of `Rectangle`, `Interactable`, `PrototypeEnemy`, etc.
- **No hierarchical transforms:** Flat coordinate system
- **No entity-component system:** Fleks is in dependencies but unused

### 2.8 Input Handling

**Direct Gdx.input polling** (no input abstraction layer):

- `HeroController`: Polls WASD/arrow keys for movement
- `DialogueInputController`: Polls E and number keys for dialogue
- `InteractionController`: Polls E for interactions
- `PlatformerController`: Polls W/UP for jumping

**No input mapping, action binding, or gamepad support.**

### 2.9 Physics & Collision

**Custom AABB collision resolver:**

- `CollisionResolver`: Axis-separated collision with world bounds clamping
- No Box2D integration (dependency present but unused)
- No physics simulation (gravity, forces, impulses)
- Collision is purely kinematic (position correction only)

### 2.10 Audio & Animation

**Not implemented:**
- No audio playback (Sound/Music not used)
- No animation system (spritesheet playback, skeletal animation, tweening)
- Dependencies present (`gdx-freetype`) but unused

### 2.11 Asset Management

**Minimal asset pipeline:**
- No `AssetManager` usage
- TMX maps loaded via `TiledAreaLoader` (custom XML parser)
- No texture loading (all rendering uses primitive colors)
- No asset versioning or hot-reloading

### 2.12 Serialization

**TMX parsing only:**
- `TiledAreaLoader`: Parses TMX XML to `AreaData`
- No JSON serialization for game state
- No save/load system
- No binary serialization

### 2.13 Resource Disposal

**Proper disposal pattern:**
- `WorldRenderer3D` implements `Disposable`
- `MainGameScreen.dispose()` calls dispose on all resources
- ModelBatch, Models, SpriteBatch, ShapeRenderer, Stage all disposed
- No resource leaks detected

### 2.14 Error Handling

**Limited error handling:**
- Try-catch in `TiledAreaLoader` for XML parsing
- `IllegalArgumentException` for validation failures
- No global error handler
- No crash reporting
- JVM crash logs present in repo (indicates native issues)

### 2.15 Testing

**Good test coverage:**
- 17 test classes using JUnit 5
- Tests cover: dialogue, quests, travel, platformer physics, area loading, building logic
- Tests are headless (no LibGDX dependencies in test code)
- Build passes: `gradlew test --no-daemon`

### 2.16 Engine-Level vs Game-Specific Classification

**Engine-level candidates (generalizable):**

| Class/Package | Current Location | Engine Potential |
|--------------|-----------------|-----------------|
| `CollisionResolver` | `world/` | ✅ High - generic AABB collision |
| `TiledAreaLoader` | `tiled/` | ✅ High - TMX parsing (headless) |
| `TiledCoordinateConverter` | `tiled/` | ✅ High - coordinate transforms |
| `TileLayerAdapter` | `tiled/` | ✅ Medium - tile layer utilities |
| `AreaRegistry` | `world/` | ⚠️ Medium - needs generalization |
| `PlatformerController` | `world/` | ⚠️ Medium - platformer physics |
| `IsometricCamera` | `world3d/` | ⚠️ Low - game-specific camera modes |
| `WorldRenderer3D` | `world3d/` | ❌ Low - tightly coupled to game objects |
| `WorldCoordinateConverter` | `world3d/` | ⚠️ Medium - 3D projection utilities |

**Game-specific (Weskaap-only):**

- All domain packages: `player/`, `enemy/`, `ally/`, `building/`, `dialogue/`, `quest/`, `inventory/`, `equipment/`, `consumable/`, `loot/`, `travel/`, `story/`, `economy/`
- `GameWorld` (god class - needs decomposition)
- `HeroController`, `DialogueInputController`, `InteractionController` (input logic)
- All UI classes in `ui/` package
- All content repositories (`QuestRepository`, `DialogueRepository`)

---

## 3. Proposed Module Structure

### 3.1 Candidate Modules (Full Vision)

```
engine/
├── engine-core/          # Core abstractions, lifecycle, math
├── engine-scene/         # Scene graph, entity-component system
├── engine-render/        # Rendering abstraction (2D/3D)
├── engine-assets/        # Asset management, loading, versioning
├── engine-input/         # Input abstraction, mapping, gamepad
├── engine-physics/       # Physics integration (Box2D wrapper)
├── engine-animation/      # Animation system (sprites, tweening)
├── engine-audio/         # Audio playback (Sound/Music)
├── engine-serialization/ # Save/load, JSON/binary formats
├── engine-debug/         # Debug tools, profilers, visualizers
├── editor/               # Editor application (depends on engine)
└── samples/              # Sample games/demos
```

### 3.2 Recommended Initial Module Set

**Phase 1 (Milestone 1): Minimal Viable Engine**

```
engine/
├── engine-core/          # Core abstractions, lifecycle, math
├── engine-scene/         # Scene graph, entity-component system
└── engine-render/        # Rendering abstraction (2D/3D)
```

**Rationale:**
- These three modules provide the foundation for any game
- Rendering and scene management are the most coupled systems in Weskaap
- Starting small allows validation of the module approach before expanding
- Other systems (input, physics, audio) can be added incrementally as needed

**Phase 2 (Future):**
- `engine-assets` - when asset pipeline matures
- `engine-input` - when input abstraction is needed
- `engine-physics` - when Box2D integration is required
- `engine-serialization` - when save/load is implemented

### 3.3 Dependency Direction

```
editor ──> samples ──> weskaap-game
    │           │           │
    └───────────┴───────────┴──> engine-render
                                    │
                                    ├──> engine-scene
                                    │       │
                                    │       └──> engine-core
                                    │
                                    └──> engine-core
```

**Rules:**
- Engine modules never depend on game code
- Engine modules never depend on editor
- Game code depends only on engine modules it needs
- Editor depends on engine (not vice versa)

---

## 4. Core Public APIs & Lifecycle Contracts

### 4.1 Engine-Core API

**Purpose:** Core abstractions, lifecycle, math utilities

**Key interfaces:**

```java
// Lifecycle
public interface Engine {
    void create();
    void resize(int width, int height);
    void render(float delta);
    void pause();
    void resume();
    void dispose();
}

// Resource ownership
public interface Disposable {
    void dispose();
}

public interface ResourceManager {
    <T> T load(String path, Class<T> type);
    void unload(String path);
    void dispose();
}

// Math utilities (extract from LibGDX or wrap)
package engine.math;
public class Vector2 { /* ... */ }
public class Vector3 { /* ... */ }
public class Rectangle { /* ... */ }
```

**Lifecycle contract:**
- `create()` called once at startup
- `render(float delta)` called every frame (delta in seconds)
- `dispose()` called at shutdown, must release all resources
- Resources must track ownership for proper disposal

### 4.2 Engine-Scene API

**Purpose:** Scene graph, entity-component system

**Key interfaces:**

```java
// Scene graph
public interface Scene {
    String getId();
    void update(float delta);
    void render(Renderer renderer);
    void dispose();
}

public interface SceneNode {
    String getId();
    SceneNode getParent();
    List<SceneNode> getChildren();
    Transform getTransform();
    void update(float delta);
}

public class Transform {
    public Vector3 position;
    public Quaternion rotation;
    public Vector3 scale;
    public Matrix4 toMatrix4();
}

// Entity-Component System (hybrid approach)
public interface Entity {
    String getId();
    <T> T getComponent(Class<T> type);
    <T> void addComponent(T component);
    <T> void removeComponent(Class<T> type);
}

public interface Component {
    Entity getEntity();
}

// Scene management
public interface SceneManager {
    void registerScene(Scene scene);
    void loadScene(String sceneId);
    Scene getActiveScene();
    void update(float delta);
    void dispose();
}
```

**Design decision:** Hybrid scene graph + ECS
- Scene graph for hierarchical transforms (camera, UI, nested objects)
- ECS for game entities (hero, enemies, NPCs) with flexible composition
- Not using Ashley initially - evaluate after ECS needs are clearer

**Scene object identifiers:**
- Stable UUID-based IDs (not indices)
- IDs persist across serialization
- Lookup by ID is O(1) via HashMap

### 4.3 Engine-Render API

**Purpose:** Rendering abstraction supporting 2D, 2.5D, and 3D

**Key interfaces:**

```java
// Renderer abstraction
public interface Renderer {
    void begin(Camera camera);
    void render(ModelInstance instance);
    void render(Sprite sprite);
    void end();
    void dispose();
}

// Camera abstraction
public interface Camera {
    void update();
    Matrix4 getViewMatrix();
    Matrix4 getProjectionMatrix();
    void viewport(int x, int y, int width, int height);
}

// 3D camera implementation
public class PerspectiveCamera implements Camera { /* ... */ }
public class OrthographicCamera implements Camera { /* ... */ }

// 2D camera implementation
public class 2DCamera implements Camera { /* ... */ }

// Renderable objects
public interface Renderable {
    void render(Renderer renderer);
}

// Material system
public class Material {
    public Color color;
    public Texture texture;
    // Shader parameters
}
```

**Rendering approach:**
- Abstract over LibGDX rendering (ModelBatch, SpriteBatch)
- Support multiple renderers (3D, 2D, custom)
- Materials separate from geometry
- Batch rendering for performance

---

## 5. Scene Graph & Component Design

### 5.1 Scene Graph Structure

```
Scene (root)
├─ CameraNode
│  └─ Transform (position, rotation, scale)
├─ LightingNode
│  ├─ DirectionalLight
│  └─ AmbientLight
├─ EntityRoot
│  ├─ Entity (hero)
│  │  ├─ TransformComponent
│  │  ├─ RenderComponent
│  │  ├─ PhysicsComponent
│  │  └─ ScriptComponent
│  ├─ Entity (enemy)
│  │  └─ ...
│  └─ Entity (npc)
│     └─ ...
└─ UIRoot
   ├─ UIElement (dialogue box)
   └─ UIElement (health bar)
```

**Hierarchy rules:**
- Scene is the root container
- Entities live under EntityRoot
- UI lives under UIRoot (separate transform space)
- Transforms concatenate down the hierarchy
- Scene graph is separate from ECS (entities can be in both)

### 5.2 Component Design

**Core components (engine-level):**

```java
// Transform
public class TransformComponent implements Component {
    public Vector3 position;
    public Quaternion rotation;
    public Vector3 scale;
}

// Rendering
public class RenderComponent implements Component {
    public Model model;
    public Material material;
    public boolean visible;
}

// Physics
public class PhysicsComponent implements Component {
    public Body body;  // Box2D body
    public Fixture fixture;
    public boolean isStatic;
}

// Scripting
public class ScriptComponent implements Component {
    public Script script;  // User-defined behavior
}
```

**Game-specific components (Weskaap-level):**

```java
// Weskaap-specific
public class HeroComponent implements Component {
    public int health;
    public int maxHealth;
    public float movementSpeed;
}

public class EnemyAiComponent implements Component {
    public AiState state;
    public float detectionRange;
}

public class QuestGiverComponent implements Component {
    public List<String> availableQuests;
}
```

**Component storage:**
- Each Entity maintains a `Map<Class<?>, Component>` for component lookup
- O(1) component access by type
- Type-safe via generic methods

### 5.3 ECS vs Scene Graph Trade-offs

**Why hybrid instead of pure ECS (Ashley)?**

| Aspect | Pure ECS (Ashley) | Hybrid (Proposed) |
|--------|-------------------|-------------------|
| **Hierarchical transforms** | Difficult (requires custom systems) | Natural (scene graph) |
| **UI layout** | Awkward (flat entity list) | Natural (UI hierarchy) |
| **Composition flexibility** | Excellent | Good (components still used) |
| **Performance** | Excellent (cache-friendly) | Good (acceptable for current scale) |
| **Learning curve** | Steep | Moderate |
| **LibGDX integration** | Ashley is LibGDX-standard | Custom but simpler |

**Recommendation:** Start with hybrid approach. Evaluate Ashley if:
- Entity count exceeds 1,000
- Performance profiling shows component iteration bottleneck
- Team needs standard ECS tooling

---

## 6. Rendering & Resource Management Approach

### 6.1 Rendering Pipeline

**Current Weskaap approach:**
- Single `WorldRenderer3D.render()` method
- Hard-coded render order (ground → roads → obstacles → buildings → entities)
- No render passes or layers

**Proposed engine approach:**

```java
public class RenderSystem {
    private List<RenderLayer> layers;
    
    public void render(Scene scene, Camera camera) {
        for (RenderLayer layer : layers) {
            layer.begin(camera);
            for (Renderable renderable : scene.getRenderables(layer)) {
                layer.render(renderable);
            }
            layer.end();
        }
    }
}

public enum RenderLayer {
    BACKGROUND,
    GROUND,
    WORLD_OBJECTS,
    ENTITIES,
    FOREGROUND,
    UI
}
```

**Benefits:**
- Layer-based rendering (configurable order)
- Multiple cameras per scene (split-screen, picture-in-picture)
- Post-processing support (future)
- Clear separation of rendering concerns

### 6.2 Resource Management

**Current Weskaap approach:**
- No `AssetManager`
- Models created directly via `ModelBuilder`
- No asset versioning or hot-reloading

**Proposed engine approach:**

```java
public class EngineAssetManager implements ResourceManager {
    private AssetManager gdxAssetManager;
    private Map<String, AssetMetadata> metadata;
    
    public <T> T load(String path, Class<T> type) {
        // Check metadata for version
        // Load via LibGDX AssetManager
        // Cache reference
    }
    
    public void unload(String path) {
        // Release reference
        // Unload from LibGDX AssetManager if refcount == 0
    }
}

public class AssetMetadata {
    public String version;
    public String checksum;
    public List<String> dependencies;
}
```

**Resource ownership rules:**
- Engine owns core resources (shaders, default materials)
- Scenes own their resources (models, textures)
- Entities reference scene resources (no ownership)
- Disposal cascades: Scene dispose → Entity dispose → Component dispose

### 6.3 Asset Pipeline

**Phase 1:** Keep current approach (primitive shapes, TMX loading)

**Phase 2:** Integrate LibGDX AssetManager
- Load textures from assets/
- Load models from Blender exports
- Load TMX maps via engine-assets module

**Phase 3:** Versioned asset format
- Asset manifest file (JSON)
- Checksum-based validation
- Hot-reloading support (editor only)

---

## 7. Serialization & Asset Pipeline Approach

### 7.1 Scene Serialization

**Current Weskaap approach:**
- No scene serialization
- All world data hard-coded in `GameWorld`
- TMX parsed but not fully integrated

**Proposed engine approach:**

```java
// Scene format (JSON)
{
  "version": "1.0",
  "id": "retreat_exterior",
  "camera": {
    "type": "isometric",
    "position": [1600, 1100, -1400],
    "target": [1600, 0, 1600]
  },
  "entities": [
    {
      "id": "hero_spawn",
      "components": {
        "Transform": { "position": [1600, 1440, 0] }
      }
    },
    {
      "id": "enemy_1",
      "components": {
        "Transform": { "position": [900, 1000, 0] },
        "Render": { "model": "enemy_box" },
        "EnemyAi": { "state": "IDLE" }
      }
    }
  ]
}
```

**Serialization strategy:**
- JSON for human-editable scenes (editor support)
- Binary for runtime efficiency (optional)
- Version field for format migration
- Component-based serialization (extensible)

### 7.2 Asset Pipeline Integration

**Phase 1:** Extract `TiledAreaLoader` to engine-assets
- Keep TMX parsing as-is
- Generalize to support any TMX file
- Add unit tests

**Phase 2:** Add JSON scene loader
- Load scene JSON files
- Instantiate entities from scene data
- Wire components to engine systems

**Phase 3:** Asset versioning
- Asset manifest with checksums
- Validation on load
- Migration tools for format changes

---

## 8. Testing Strategy

### 8.1 Current Test Coverage

**Strengths:**
- 17 test classes using JUnit 5
- Headless tests (no LibGDX dependencies)
- Good coverage of domain logic

**Gaps:**
- No engine-level tests (everything is game-specific)
- No integration tests
- No rendering tests (requires LibGDX context)

### 8.2 Proposed Engine Test Strategy

**Unit tests (headless):**
- Math utilities (Vector2, Vector3, Rectangle)
- Scene graph operations (add/remove child, transform concatenation)
- Component operations (add/remove/lookup)
- Serialization (scene JSON parse/write)
- TMX parsing (already exists)

**Integration tests (with LibGDX):**
- Rendering pipeline (requires OpenGL context)
- Asset loading (requires file system)
- Lifecycle (create/render/dispose)

**Test organization:**
```
engine-core/src/test/     # Headless unit tests
engine-scene/src/test/    # Headless unit tests
engine-render/src/test/   # Integration tests (require LibGDX)
```

**Test utilities:**
- LibGDX test runner for headless rendering tests
- Mock asset manager for unit tests
- Test scene fixtures

---

## 9. Phased Implementation Plan

### 9.1 Phase 1: Engine-Core (Week 1-2)

**Goal:** Establish core abstractions and lifecycle

**Tasks:**
1. Create `engine-core` module
2. Define `Engine` interface with lifecycle methods
3. Extract math utilities (or wrap LibGDX math)
4. Define `Disposable` and `ResourceManager` interfaces
5. Create basic `Engine` implementation
6. Write unit tests for math and lifecycle

**Acceptance criteria:**
- Engine can initialize and shut down cleanly
- Math utilities tested and documented
- Lifecycle contract verified with tests
- Module builds independently

**Migration impact:** Low (new module, no changes to Weskaap)

### 9.2 Phase 2: Engine-Scene (Week 3-4)

**Goal:** Implement scene graph and ECS foundation

**Tasks:**
1. Create `engine-scene` module (depends on engine-core)
2. Implement `Scene`, `SceneNode`, `Transform`
3. Implement `Entity`, `Component`, component storage
4. Implement `SceneManager`
5. Write unit tests for scene graph operations
6. Write unit tests for component operations

**Acceptance criteria:**
- Scene graph supports hierarchical transforms
- Component add/remove/lookup works correctly
- SceneManager can load/switch scenes
- All operations tested

**Migration impact:** Low (new module, no changes to Weskaap)

### 9.3 Phase 3: Engine-Render (Week 5-6)

**Goal:** Abstract rendering over LibGDX

**Tasks:**
1. Create `engine-render` module (depends on engine-core, engine-scene)
2. Define `Renderer`, `Camera` interfaces
3. Implement `OrthographicCamera`, `PerspectiveCamera`
4. Implement basic `ModelRenderer` (wraps LibGDX ModelBatch)
5. Implement render layer system
6. Write integration tests (require LibGDX context)

**Acceptance criteria:**
- Can render a simple scene with camera
- Render layers work correctly
- Integration tests pass
- Module builds independently

**Migration impact:** Low (new module, no changes to Weskaap)

### 9.4 Phase 4: Minimal Engine Demo (Week 7)

**Goal:** Prove engine can run a standalone application

**Tasks:**
1. Create `samples/minimal-demo` module
2. Implement simple game using engine APIs
3. Demo: Initialize engine, create scene, render box, update position, shut down
4. Verify demo runs on desktop (lwjgl3)
5. Document getting-started guide

**Acceptance criteria:**
- Demo application runs without Weskaap code
- Scene renders correctly
- Entity moves via update loop
- Clean shutdown verified
- Documentation complete

**Migration impact:** None (standalone demo)

### 9.5 Phase 5: Weskaap Migration Planning (Week 8)

**Goal:** Plan migration of Weskaap to engine

**Tasks:**
1. Identify which Weskaap systems to migrate first
2. Design migration strategy (big bang vs incremental)
3. Plan rollback strategy
4. Document migration steps
5. Get approval for migration

**Acceptance criteria:**
- Migration plan documented
- Rollback strategy defined
- Risks identified and mitigated
- Stakeholder approval obtained

**Migration impact:** High (changes to Weskaap code)

### 9.6 Phase 6: Weskaap Migration (Week 9+)

**Goal:** Migrate Weskaap to use engine

**Tasks:**
1. Update Weskaap build to depend on engine modules
2. Migrate rendering to engine-render
3. Migrate scene management to engine-scene
4. Migrate collision/physics to engine-core (or engine-physics)
5. Refactor GameWorld to use engine APIs
6. Update tests
7. Verify gameplay unchanged

**Acceptance criteria:**
- Weskaap builds and runs with engine
- All gameplay features work as before
- Tests pass
- Performance acceptable
- Code complexity reduced

**Migration impact:** High (significant Weskaap refactoring)

---

## 10. Migration & Rollback Strategy

### 10.1 Migration Strategy

**Recommended approach: Incremental migration**

1. **Add engine as dependency** (keep existing code)
   - Add engine modules to Weskaap build
   - Existing code continues to work
   - No gameplay changes

2. **Migrate rendering first**
   - Replace `WorldRenderer3D` with engine-render
   - Keep existing scene structure
   - Verify rendering unchanged

3. **Migrate scene management**
   - Replace GameWorld lists with engine-scene
   - Convert entities to ECS entities
   - Verify gameplay unchanged

4. **Migrate other systems incrementally**
   - Input → engine-input (when created)
   - Physics → engine-physics (when created)
   - Assets → engine-assets (when created)

5. **Remove old code**
   - Once all systems migrated
   - Remove deprecated code
   - Clean up imports

**Benefits:**
- Can test each migration step independently
- Can rollback if step fails
- Reduces risk of big-bang failure

### 10.2 Rollback Strategy

**Per-step rollback:**
- Each migration step committed separately
- Git tags before each step
- Can revert to previous commit if step fails

**Full rollback:**
- Keep Weskaap working copy on separate branch
- If migration fails, revert to working branch
- Document failure and adjust plan

**Rollback triggers:**
- Gameplay regression
- Performance degradation > 20%
- Build instability
- Test failures

### 10.3 Compatibility Risks

**LibGDX version compatibility:**
- Engine must use same LibGDX version as Weskaap (1.14.2)
- Future LibGDX upgrades must be coordinated
- Risk: Medium

**Java version compatibility:**
- Engine and Weskaap both use Java 21
- No compatibility issues expected
- Risk: Low

**Gradle configuration:**
- Engine modules need proper Gradle setup
- Dependency management must be consistent
- Risk: Low

**Runtime behavior:**
- Engine abstractions may introduce performance overhead
- Need profiling to verify acceptable performance
- Risk: Medium

---

## 11. Assumptions & Unresolved Decisions

### 11.1 Assumptions

1. **LibGDX remains the underlying framework** - Engine will wrap LibGDX, not replace it
2. **Java 21 is the target platform** - No need to support older Java versions
3. **Desktop (LWJGL3) is the primary target** - Mobile/web support deferred
4. **Team size is small** - Architecture should be simple enough for 1-3 developers
5. **Weskaap is the first game** - Engine design validated by Weskaap migration

### 11.2 Unresolved Decisions

**ECS vs Scene Graph:**
- **Decision:** Hybrid approach (proposed)
- **Alternative:** Pure ECS with Ashley
- **Resolution:** Re-evaluate after Phase 2 if ECS needs become clearer

**Math library:**
- **Decision:** Wrap LibGDX math initially
- **Alternative:** Use JOML or custom math
- **Resolution:** Evaluate if LibGDX math is insufficient

**Asset format:**
- **Decision:** JSON for scenes (human-readable)
- **Alternative:** Binary only (faster), YAML (alternative)
- **Resolution:** JSON chosen for editor support

**Module granularity:**
- **Decision:** Start with 3 modules (core, scene, render)
- **Alternative:** Create all 10 modules upfront
- **Resolution:** Incremental approach reduces risk

**Fleks ECS:**
- **Decision:** Not using initially (despite dependency)
- **Alternative:** Use Fleks from start
- **Resolution:** Evaluate after ECS needs are clear

---

## 12. Completion Criteria

The audit and proposal are complete when:

- ✅ Repository structure, Git state, and build configuration documented
- ✅ Entry points, lifecycle, and game loop documented
- ✅ Core systems (rendering, camera, scene, ECS) documented
- ✅ I/O systems (input, assets, audio, animation, physics) documented
- ✅ Infrastructure (serialization, disposal, error handling, tests) documented
- ✅ Engine-level vs game-specific code classified
- ✅ Compatibility risks and dependencies evaluated
- ✅ Proposed module structure designed
- ✅ Core public APIs and lifecycle contracts defined
- ✅ Scene graph and component design specified
- ✅ Rendering and resource management approach defined
- ✅ Serialization and asset pipeline approach defined
- ✅ Testing strategy outlined
- ✅ Phased implementation plan with acceptance criteria
- ✅ Migration and rollback strategy documented
- ✅ Assumptions and unresolved decisions explicitly stated
- ✅ First implementation milestone clearly defined

**First implementation milestone (Phase 1-4):**
> Prove a minimal engine can initialize, create or load a scene, render it, update it, and shut down cleanly in a standalone application.

**Status:** ✅ Complete - awaiting approval to proceed with implementation

---

## 13. Next Steps (Awaiting Approval)

1. **Review this proposal** with stakeholders
2. **Approve or revise** the architecture
3. **Begin Phase 1** (engine-core module creation)
4. **Execute phased implementation** (Phases 1-4)
5. **Plan Weskaap migration** (Phase 5)
6. **Execute migration** (Phase 6)

**Do not proceed with implementation until this proposal is approved.**
