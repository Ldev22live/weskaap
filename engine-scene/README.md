# Engine Scene

The `engine-scene` module provides scene graph, entity-component system, and scene lifecycle management on top of `engine-core`. It enables hierarchical transforms, scene switching, and component-based entity design.

## Purpose

Engine-scene provides:

- **Scene lifecycle management** - Creation, activation, deactivation, and disposal of scenes
- **Hierarchical scene graph** - Parent-child node relationships with transform composition
- **Transform system** - Local and world transforms using LibGDX math types
- **Entity-component system** - Lightweight component-based entity design
- **Scene switching** - Deterministic scene transitions with cleanup

## Public API

### Scene Interface

```java
public interface Scene extends Disposable {
    void activate();
    void update(float delta);
    void deactivate();
    void dispose();
    SceneNode getRoot();
    boolean isActive();
}
```

**Lifecycle states:**
- `CREATED` - Scene instantiated but not activated
- `ACTIVATED` - Scene is active and receiving updates
- `DEACTIVATED` - Scene deactivated but not disposed
- `DISPOSED` - Scene disposed and cannot be reused

**Lifecycle contract:**
- Scenes are created once and disposed when replaced
- Deactivated scenes are not reused (new instance created on switch)
- Scene disposal is idempotent
- Scene activation may throw; if it does, the scene is not activated

### BaseScene

Default implementation of Scene with standard lifecycle management:

```java
BaseScene scene = new BaseScene("MyScene");
SceneNode root = scene.getRoot();
```

### SceneManager

Manages scene lifecycle and switching:

```java
SceneManager manager = new SceneManager();
manager.switchScene(scene1);
manager.update(delta);
manager.dispose();
```

**Scene switching contract:**
- Current scene is deactivated then disposed
- New scene is activated
- If activation fails, old scene remains active
- Repeated switches to same scene are no-ops
- Null scenes are rejected

### SceneNode

Hierarchical node in the scene graph:

```java
SceneNode parent = new SceneNode("Parent");
parent.setPosition(5, 0, 0);
parent.setRotation(45, 0, 0);
parent.setScale(2, 2, 2);

SceneNode child = new SceneNode("Child");
child.setPosition(3, 0, 0);
parent.addChild(child);

Vector3 worldPos = child.getWorldPosition(); // Includes parent transform
```

**Transform conventions:**
- Position: Local translation (Vector3)
- Rotation: Euler angles in degrees (Vector3: pitch, yaw, roll)
- Scale: Local scale (Vector3)
- World transform: `parentWorld × localTransform`
- Transform multiplication order: Parent × Child (standard scene graph)

**Ownership rules:**
- Parent owns its children (disposed when parent is disposed)
- Detached nodes are not disposed (caller owns them)
- Root nodes are owned by the scene

**Cycle prevention:**
- Nodes cannot become their own ancestors
- Adding a child that would create a cycle throws IllegalStateException

### Entity

Component-based entity:

```java
Entity entity = new Entity("MyEntity");
entity.addComponent(new TransformComponent());
entity.addComponent(new RenderComponent());

TransformComponent transform = entity.getComponent(TransformComponent.class);
```

**Component rules:**
- Component types are unique per entity (one component per type)
- Adding a component of an existing type replaces the old one
- Replaced components are disposed if they implement Disposable
- Entities do not own scene resources (they only reference them)

**Lifecycle rules:**
- Entities can be created independently of scenes
- Disposed entities cannot accept new components
- Entity disposal disposes all components

### Component

Marker interface for entity components:

```java
public interface Component {
    // Marker interface - no methods required
}
```

Components are data containers. Logic is handled by systems (deferred to future phases).

## Dependencies

- engine-core (project dependency)
- JUnit 5 for testing

**No dependencies on:**
- Weskaap game code
- ECS frameworks (Fleks, Ashley)
- Rendering systems (deferred to engine-render)
- Physics systems (deferred to engine-physics)

## Running Tests

```bash
./gradlew.bat :engine-scene:test --no-daemon
```

All tests are headless and do not require an OpenGL context.

**Test coverage:**
- SceneNodeTest.java (31 tests) - Hierarchy, transforms, cycle rejection, non-uniform scale, disposal
- EntityTest.java (18 tests) - Component management, duplicate replacement, disposal, lifecycle
- SceneManagerTest.java (13 tests) - Scene switching, failure recovery, lifecycle, cleanup
- Total: 62 tests (100% passing)

## Running the Sample

The `engine-sample` module demonstrates engine-scene usage:

```bash
./gradlew.bat :engine-sample:run --no-daemon
```

The sample demonstrates:
- Scene creation and activation
- Hierarchical scene graph with transforms
- Entity with multiple components
- Scene switching and deterministic cleanup
- World position calculation

## Resource Ownership and Disposal

### Scene Ownership
- SceneManager owns the current scene
- Scenes are disposed when replaced or when manager is disposed
- Scene root owns all descendant nodes

### Node Ownership
- Parent owns its children
- Disposal cascades recursively to children
- Detached nodes are not disposed (caller owns them)
- Disposal removes node from parent

### Entity Ownership
- Entities are independent of scenes (can be created separately)
- Entities own their components
- Component disposal happens on entity disposal or component replacement
- Entities do not own scene resources (they reference them)

### Disposal Rules
- All disposal methods are idempotent
- Disposal exceptions don't prevent other resources from being disposed
- Disposal happens in reverse order (children before parent)
- Disposed objects cannot accept new operations

## Design Decisions

### Why not use Ashley ECS?

The current implementation uses a lightweight HashMap-based component registry. Ashley evaluation is deferred until ECS needs are clearer. This allows the engine to start with a simpler, more testable foundation.

### Why not force Entity to extend SceneNode?

Entities are kept independent of scene nodes to avoid forcing unnecessary inheritance. Entities can be associated with scene nodes but are not required to be. This provides flexibility for different use cases.

### Why dispose scenes on switch instead of reusing?

Disposing scenes on switch simplifies lifecycle management and prevents resource leaks. Scene reuse would require complex state management and is deferred until a clear use case emerges.

### Why use Euler angles for rotation?

Euler angles are simple and sufficient for the initial implementation. Quaternion-based rotation can be added later if needed for gimbal lock avoidance or interpolation.

### Why validate transform changes?

Transform changes invalidate the world transform cache. This ensures correct world position calculation without recomputing the entire scene graph every frame.

## Deferred Functionality

The following features are intentionally deferred to future phases:

- Rendering abstraction (Phase 3: engine-render)
- Asset management (future: engine-assets)
- Input abstraction (future: engine-input)
- Physics integration (future: engine-physics)
- Animation system (future: engine-animation)
- Audio playback (future: engine-audio)
- Serialization (future: engine-serialization)
- Debug tools (future: engine-debug)
- Systems for component processing (deferred until ECS needs are clearer)

## Module Dependency Rules

- engine-scene must depend on engine-core
- engine-scene must not depend on Weskaap game code
- engine-scene must not depend on engine-render or other future modules
- Games may depend on engine-scene and engine-core
- engine-render may depend on engine-scene

## Known Limitations

- Transform system uses Euler angles (gimbal lock possible)
- No quaternion-based rotation support
- No spatial partitioning or culling
- No prefab or template system
- No scene serialization
- No component systems (logic processing)
- HashMap-based component lookup (not strictly O(1) worst-case)

## License

Part of the Weskaap project.
