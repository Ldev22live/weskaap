package engine.core;

/**
 * Simple logging interface for the engine.
 * <p>
 * This provides a minimal logging abstraction without requiring
 * external logging libraries. Implementations can route to
 * System.out, LibGDX's Gdx.app.log, or other logging frameworks.
 * </p>
 */
public interface EngineLogger {

    /**
     * Logs an informational message.
     *
     * @param tag   The log tag (e.g., class name)
     * @param message The message to log
     */
    void info(String tag, String message);

    /**
     * Logs a debug message.
     *
     * @param tag   The log tag
     * @param message The message to log
     */
    void debug(String tag, String message);

    /**
     * Logs a warning message.
     *
     * @param tag   The log tag
     * @param message The message to log
     */
    void warn(String tag, String message);

    /**
     * Logs an error message.
     *
     * @param tag   The log tag
     * @param message The message to log
     */
    void error(String tag, String message);

    /**
     * Logs an error message with a throwable.
     *
     * @param tag   The log tag
     * @param message The message to log
     * @param throwable The exception or error
     */
    void error(String tag, String message, Throwable throwable);
}
