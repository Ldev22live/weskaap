package engine.core;

/**
 * Represents the lifecycle state of the engine.
 */
public enum EngineState {
    /**
     * Engine has been created but not initialized.
     */
    UNINITIALIZED,

    /**
     * Engine has been initialized and is ready to run.
     */
    INITIALIZED,

    /**
     * Engine is actively processing updates.
     */
    RUNNING,

    /**
     * Engine is paused (updates suspended).
     */
    PAUSED,

    /**
     * Engine has been disposed and cannot be restarted.
     */
    DISPOSED
}
