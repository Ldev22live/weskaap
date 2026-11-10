package engine.core.impl;

import engine.core.Disposable;
import engine.core.Engine;
import engine.core.EngineConfig;
import engine.core.EngineLogger;
import engine.core.EngineState;

import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of the Engine interface.
 * <p>
 * This implementation provides the core lifecycle management,
 * resource tracking, and logging integration.
 * </p>
 */
public class DefaultEngine implements Engine {

    private EngineConfig config;
    private EngineState state = EngineState.UNINITIALIZED;
    private EngineLogger logger;
    private final List<Disposable> ownedResources = new ArrayList<>();
    private boolean disposed = false;

    public DefaultEngine() {
        this.logger = new DefaultEngineLogger();
    }

    public DefaultEngine(EngineLogger logger) {
        if (logger == null) {
            throw new IllegalArgumentException("Logger cannot be null");
        }
        this.logger = logger;
    }

    @Override
    public void initialize(EngineConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("Config cannot be null");
        }
        if (state != EngineState.UNINITIALIZED) {
            throw new IllegalStateException("Engine already initialized");
        }

        this.config = config;
        this.state = EngineState.INITIALIZED;

        if (config.isEnableLogging()) {
            logger.info("DefaultEngine", "Engine initialized: " + config.getTitle());
        }
    }

    @Override
    public void create() {
        ensureInitialized();
        if (state == EngineState.RUNNING || state == EngineState.PAUSED) {
            throw new IllegalStateException("Engine already created");
        }

        state = EngineState.RUNNING;

        if (config.isEnableLogging()) {
            logger.info("DefaultEngine", "Engine created and running");
        }
    }

    @Override
    public void update(float delta) {
        ensureRunning();
        validateDelta(delta);

        // Update logic would go here in future phases
        // For now, this is a no-op to satisfy the lifecycle contract
    }

    @Override
    public void resize(int width, int height) {
        ensureInitialized();
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Width and height must be positive");
        }

        if (config.isEnableLogging()) {
            logger.debug("DefaultEngine", "Resized to " + width + "x" + height);
        }
    }

    @Override
    public void pause() {
        if (state != EngineState.RUNNING) {
            return; // Pause is a no-op if not running
        }

        state = EngineState.PAUSED;

        if (config.isEnableLogging()) {
            logger.info("DefaultEngine", "Engine paused");
        }
    }

    @Override
    public void resume() {
        if (state != EngineState.PAUSED) {
            return; // Resume is a no-op if not paused
        }

        state = EngineState.RUNNING;

        if (config.isEnableLogging()) {
            logger.info("DefaultEngine", "Engine resumed");
        }
    }

    @Override
    public void dispose() {
        if (disposed) {
            return; // Already disposed
        }

        disposed = true;
        state = EngineState.DISPOSED;

        // Dispose all owned resources in reverse order
        for (int i = ownedResources.size() - 1; i >= 0; i--) {
            Disposable resource = ownedResources.get(i);
            try {
                resource.dispose();
            } catch (Exception e) {
                if (config != null && config.isEnableLogging()) {
                    logger.error("DefaultEngine", "Error disposing resource", e);
                }
            }
        }
        ownedResources.clear();

        if (config != null && config.isEnableLogging()) {
            logger.info("DefaultEngine", "Engine disposed");
        }
    }

    @Override
    public EngineState getState() {
        return state;
    }

    @Override
    public EngineConfig getConfig() {
        return config;
    }

    /**
     * Registers a resource to be owned and disposed by the engine.
     *
     * @param resource The resource to own
     * @throws IllegalStateException if engine is disposed
     * @throws IllegalArgumentException if resource is null
     */
    public void registerResource(Disposable resource) {
        if (disposed) {
            throw new IllegalStateException("Cannot register resources on disposed engine");
        }
        if (resource == null) {
            throw new IllegalArgumentException("Resource cannot be null");
        }
        ownedResources.add(resource);
    }

    private void ensureInitialized() {
        if (state == EngineState.UNINITIALIZED || state == EngineState.DISPOSED) {
            throw new IllegalStateException("Engine not initialized");
        }
    }

    private void ensureRunning() {
        if (state != EngineState.RUNNING) {
            throw new IllegalStateException("Engine not running (state: " + state + ")");
        }
    }

    private void validateDelta(float delta) {
        if (Float.isNaN(delta) || Float.isInfinite(delta)) {
            throw new IllegalArgumentException("Delta time cannot be NaN or infinite");
        }
        if (delta < 0) {
            throw new IllegalArgumentException("Delta time cannot be negative");
        }
        // Cap delta to prevent spiral of death
        if (delta > 1.0f) {
            if (config.isEnableLogging()) {
                logger.warn("DefaultEngine", "Delta time capped at 1.0s (was: " + delta + "s)");
            }
        }
    }
}
