# Engine Sample

The `engine-sample` module demonstrates how to use `engine-core` in a minimal desktop application. It shows the integration between the engine lifecycle and LibGDX's application model.

## Purpose

This sample demonstrates:
- Engine initialization with configuration
- Engine lifecycle integration with LibGDX
- At least one update tick
- Resize handling
- Pause/resume handling
- Clean shutdown

## Running the Sample

```bash
./gradlew.bat :engine-sample:run --no-daemon
```

## Code Structure

### EngineSampleLauncher

The main entry point that:
1. Creates an `EngineConfig` with sample settings
2. Initializes a `DefaultEngine` instance
3. Configures LibGDX's `Lwjgl3ApplicationConfiguration`
4. Launches the application with `EngineSampleApplication`

### EngineSampleApplication

A LibGDX `ApplicationListener` that adapts LibGDX lifecycle callbacks to the engine:
- `create()` → calls `engine.create()`
- `resize()` → calls `engine.resize()`
- `render()` → calls `engine.update()` with delta time
- `pause()` → calls `engine.pause()`
- `resume()` → calls `engine.resume()`
- `dispose()` → calls `engine.dispose()`

## Expected Output

When running the sample, you should see console output similar to:

```
[INFO] DefaultEngine: Engine initialized: Engine Core Sample
[Sample] LibGDX create() called
[INFO] DefaultEngine: Engine created and running
[Sample] Engine state after create: RUNNING
[Sample] Update tick #1, engine state: RUNNING
[Sample] Update tick #2, engine state: RUNNING
[Sample] Update tick #3, engine state: RUNNING
[Sample] Update tick #4, engine state: RUNNING
[Sample] Update tick #5, engine state: RUNNING
```

Pause/resume events will also be logged when the window loses/regains focus.

## Dependencies

- engine-core (project dependency)
- LibGDX LWJGL3 backend
- LibGDX desktop natives

## Notes

- The sample does not render anything visual (rendering is deferred to engine-render in Phase 3)
- The sample uses a fixed delta time (0.016s) for demonstration
- The sample window can be resized to test the resize handling
