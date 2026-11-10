# Engine Core

The `engine-core` module provides the foundational abstractions for the Weskaap game engine. It defines the core lifecycle contracts, resource ownership rules, and logging infrastructure that all other engine modules depend on.

## Purpose

Engine-core is the base layer of the modular engine architecture. It provides:

- **Engine lifecycle management** - Initialization, updates, pause/resume, and disposal
- **Resource ownership contracts** - Clear rules for who owns and disposes resources
- **Configuration** - Immutable engine configuration via builder pattern
- **Logging abstraction** - Simple logging interface without external dependencies
- **State management** - Observable engine state transitions

## Public API

### Engine Interface

The `Engine` interface defines the core lifecycle contract:

```java
public interface Engine {
    void initialize(EngineConfig config);
    void create();
    void update(float delta);
    void resize(int width, int height);
    void pause();
    void resume();
    void dispose();
    EngineState getState();
    EngineConfig getConfig();
}
```

**Lifecycle states:**
- `UNINITIALIZED` - Engine created but not initialized
- `INITIALIZED` - Engine initialized and ready to run
- `RUNNING` - Engine is actively processing updates
- `PAUSED` - Engine is paused (updates suspended)
- `DISPOSED` - Engine has been shut down

### EngineConfig

Immutable configuration created via builder pattern:

```java
EngineConfig config = EngineConfig.builder()
    .title("My Game")
    .initialWidth(1280)
    .initialHeight(720)
    .useVSync(true)
    .targetFps(60)
    .enableLogging(true)
    .build();
```

### Disposable

Interface for objects that own resources requiring explicit disposal:

```java
public interface Disposable {
    void dispose();
}
```

**Resource ownership rules:**
- Engine owns core resources (shaders, default materials)
- Scenes own their resources (models, textures)
- Entities reference scene resources (no ownership)
- Disposal cascades: Scene dispose → Entity dispose → Component dispose

### EngineLogger

Simple logging interface:

```java
public interface EngineLogger {
    void info(String tag, String message);
    void debug(String tag, String message);
    void warn(String tag, String message);
    void error(String tag, String message);
    void error(String tag, String message, Throwable throwable);
}
```

## Dependencies

- LibGDX core (`gdx:$gdxVersion`)
- JUnit 5 for testing

**No dependencies on:**
- Weskaap game-specific code
- ECS frameworks (Fleks is in project but not used by engine-core)
- Custom math libraries (uses LibGDX math where needed)

## Running Tests

```bash
./gradlew.bat :engine-core:test --no-daemon
```

All tests are headless and do not require an OpenGL context.

## Running the Sample

The `engine-sample` module demonstrates engine-core usage:

```bash
./gradlew.bat :engine-sample:run --no-daemon
```

The sample demonstrates:
- Engine initialization with configuration
- Engine lifecycle integration with LibGDX
- Update ticks
- Resize handling
- Pause/resume handling
- Clean shutdown

## Deferred Functionality

The following features are intentionally deferred to future phases:

- Scene graph and entity-component system (Phase 2: engine-scene)
- Rendering abstraction (Phase 3: engine-render)
- Asset management (future: engine-assets)
- Input abstraction (future: engine-input)
- Physics integration (future: engine-physics)
- Animation system (future: engine-animation)
- Audio playback (future: engine-audio)
- Serialization (future: engine-serialization)
- Debug tools (future: engine-debug)

## Module Dependency Rules

- engine-core must not depend on Weskaap game code
- engine-core must not depend on engine-scene, engine-render, or other engine modules
- engine-core may depend on LibGDX where required
- Other engine modules may depend on engine-core
- Games may depend on engine-core and other engine modules

## Design Decisions

### Why not use Ashley ECS?

The current implementation does not include an ECS. The architecture proposal recommends a hybrid scene graph + ECS approach, with Ashley evaluation deferred until ECS needs are clearer. This allows the engine to start with a simpler, more testable foundation.

### Why not duplicate LibGDX math?

Engine-core uses LibGDX's math classes (Vector2, Vector3, Matrix4) where needed rather than duplicating them. This reduces maintenance burden and ensures compatibility with LibGDX.

### Why a simple logger instead of SLF4J/Log4j?

The `EngineLogger` interface provides a minimal abstraction without requiring external logging dependencies. Implementations can route to System.out, LibGDX's Gdx.app.log, or other frameworks as needed. This keeps the module lightweight and testable.

### Why immutable config?

`EngineConfig` is immutable to prevent configuration changes after initialization, which could lead to undefined behavior. The builder pattern provides a fluent API for construction.

## License

Part of the Weskaap project.
