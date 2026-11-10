package engine.core;

/**
 * Core engine interface defining the lifecycle contract for game applications.
 * <p>
 * The engine integrates with LibGDX's Application lifecycle but provides
 * a clear, testable abstraction for initialization, updates, and shutdown.
 * </p>
 * <p>
 * Lifecycle states:
 * <ul>
 *   <li>UNINITIALIZED - Engine created but not initialized</li>
 *   <li>INITIALIZED - Engine initialized and ready to run</li>
 *   <li>RUNNING - Engine is actively processing updates</li>
 *   <li>PAUSED - Engine is paused (updates suspended)</li>
 *   <li>DISPOSED - Engine has been shut down and cannot be restarted</li>
 * </ul>
 * </p>
 */
public interface Engine {

    /**
     * Initializes the engine with the given configuration.
     * <p>
     * This method must be called once before any other engine methods.
     * Subsequent calls will throw IllegalStateException.
     * </p>
     *
     * @param config The engine configuration
     * @throws IllegalStateException if already initialized
     * @throws IllegalArgumentException if config is null or invalid
     */
    void initialize(EngineConfig config);

    /**
     * Called when the application is created.
     * <p>
     * This is the LibGDX Application.create() entry point.
     * The engine must be initialized before this is called.
     * </p>
     *
     * @throws IllegalStateException if engine not initialized
     */
    void create();

    /**
     * Called every frame with the time elapsed since the last frame.
     * <p>
     * Delta time is measured in seconds. Invalid values (negative, NaN, infinite)
     * will be rejected according to the engine's contract.
     * </p>
     *
     * @param delta Time in seconds since the last frame
     * @throws IllegalStateException if engine not running
     * @throws IllegalArgumentException if delta is invalid
     */
    void update(float delta);

    /**
     * Called when the application window is resized.
     *
     * @param width  New window width in pixels
     * @param height New window height in pixels
     * @throws IllegalStateException if engine not initialized
     */
    void resize(int width, int height);

    /**
     * Called when the application is paused (e.g., window loses focus).
     * <p>
     * Updates should be suspended but the engine remains initialized.
     * </p>
     */
    void pause();

    /**
     * Called when the application is resumed (e.g., window regains focus).
     * <p>
     * Updates should resume from the paused state.
     * </p>
     */
    void resume();

    /**
     * Called when the application is shutting down.
     * <p>
     * All resources owned by the engine must be released.
     * This method must be safe to call even if initialization failed.
     * After disposal, the engine cannot be restarted.
     * </p>
     */
    void dispose();

    /**
     * Returns the current lifecycle state of the engine.
     *
     * @return The current engine state
     */
    EngineState getState();

    /**
     * Returns the engine configuration.
     *
     * @return The configuration, or null if not initialized
     */
    EngineConfig getConfig();
}
