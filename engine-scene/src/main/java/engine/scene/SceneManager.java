package engine.scene;

import engine.core.Disposable;

/**
 * Manages scene lifecycle and switching.
 * <p>
 * The SceneManager is responsible for:
 * - Activating and deactivating scenes
 * - Switching between scenes with cleanup
 * - Disposing the current scene on shutdown
 * </p>
 * <p>
 * Scene switching contract:
 * <ul>
 *   <li>When switching, the current scene is deactivated then disposed</li>
 *   <li>The new scene is activated</li>
 *   <li>If activation fails, the old scene remains active</li>
 *   <li>Repeated switches to the same scene are no-ops</li>
 *   <li>Null scenes are rejected</li>
 * </ul>
 * </p>
 * <p>
 * Ownership:
 * <ul>
 *   <li>SceneManager owns the current scene</li>
 *   <li>Scenes are disposed when replaced or when manager is disposed</li>
 * </ul>
 * </p>
 */
public class SceneManager implements Disposable {

    private Scene currentScene;
    private boolean disposed = false;

    /**
     * Creates a new scene manager.
     */
    public SceneManager() {
    }

    /**
     * Switches to the given scene.
     * <p>
     * The current scene (if any) is deactivated and disposed.
     * The new scene is activated. If activation fails, the old scene
     * remains active (if it existed).
     * </p>
     *
     * @param scene The scene to switch to
     * @throws IllegalStateException if manager is disposed
     * @throws IllegalArgumentException if scene is null
     */
    public void switchScene(Scene scene) {
        if (disposed) {
            throw new IllegalStateException("Cannot switch scene on disposed manager");
        }
        if (scene == null) {
            throw new IllegalArgumentException("Scene cannot be null");
        }

        // No-op if switching to the same scene
        if (scene == currentScene) {
            return;
        }

        Scene oldScene = currentScene;

        // Activate the new scene first
        scene.activate();

        // If activation succeeded, deactivate and dispose the old scene
        if (oldScene != null) {
            try {
                oldScene.deactivate();
            } finally {
                oldScene.dispose();
            }
        }

        currentScene = scene;
    }

    /**
     * Returns the current active scene, or null if no scene is active.
     *
     * @return The current scene, or null
     */
    public Scene getCurrentScene() {
        return currentScene;
    }

    /**
     * Updates the current scene.
     *
     * @param delta Time in seconds since the last frame
     * @throws IllegalStateException if no scene is active
     */
    public void update(float delta) {
        if (currentScene == null) {
            throw new IllegalStateException("No active scene to update");
        }
        currentScene.update(delta);
    }

    @Override
    public void dispose() {
        if (disposed) {
            return;
        }
        disposed = true;

        if (currentScene != null) {
            currentScene.deactivate();
            currentScene.dispose();
            currentScene = null;
        }
    }

    /**
     * Returns whether the manager has been disposed.
     *
     * @return true if disposed, false otherwise
     */
    public boolean isDisposed() {
        return disposed;
    }

    /**
     * Returns whether a scene is currently active.
     *
     * @return true if a scene is active, false otherwise
     */
    public boolean hasActiveScene() {
        return currentScene != null && currentScene.isActive();
    }
}
