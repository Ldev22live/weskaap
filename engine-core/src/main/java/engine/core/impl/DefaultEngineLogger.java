package engine.core.impl;

import engine.core.EngineLogger;

/**
 * Default implementation of EngineLogger using System.out/err.
 * <p>
 * This provides a simple logging mechanism without requiring
 * external dependencies. In production, this can be replaced
 * with LibGDX's Gdx.app.log or other logging frameworks.
 * </p>
 */
public class DefaultEngineLogger implements EngineLogger {

    @Override
    public void info(String tag, String message) {
        System.out.println("[INFO] " + tag + ": " + message);
    }

    @Override
    public void debug(String tag, String message) {
        System.out.println("[DEBUG] " + tag + ": " + message);
    }

    @Override
    public void warn(String tag, String message) {
        System.out.println("[WARN] " + tag + ": " + message);
    }

    @Override
    public void error(String tag, String message) {
        System.err.println("[ERROR] " + tag + ": " + message);
    }

    @Override
    public void error(String tag, String message, Throwable throwable) {
        System.err.println("[ERROR] " + tag + ": " + message);
        throwable.printStackTrace(System.err);
    }
}
