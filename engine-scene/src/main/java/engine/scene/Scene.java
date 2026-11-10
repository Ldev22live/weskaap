package engine.scene;

import engine.core.Disposable;

/**
 * Scene lifecycle contract.
 * <p>
 * A scene represents a game state with its own hierarchy of nodes,
 * entities, and components. Scenes are managed by a SceneManager
 * and follow a defined lifecycle.
 * </p>
 * <p>
 * Lifecycle states:
 * <ul>
 *   <li>CREATED - Scene has been instantiated but not activated</li>
 *   <li>ACTIVATED - Scene is active and receiving updates</li>
 *   <li>DEACTIVATED - Scene has been deactivated but not disposed</li>
 *   <li>DISPOSED - Scene has been disposed and cannot be reused</li>
 * </ul>
 * </p>
 * <p>
 * Scene lifecycle contract:
 * <ul>
 *   <li>Scenes are created once and disposed when replaced</li>
 *   <li>Deactivated scenes are not reused (new instance created on switch)</li>
 *   <li>Scene disposal is idempotent</li>
 *   <li>Scene activation may throw; if it does, the scene is not activated</li>
 * </ul>
 * </p>
 */
public interface Scene extends Disposable {

    /**
     * Called when the scene is activated.
     * <p>
     * This is called once when the scene becomes the active scene.
     * Resources should be loaded and the scene initialized.
     * </p>
     *
     * @throws IllegalStateException if scene is already activated
     * @throws RuntimeException if activation fails (scene will not be activated)
     */
    void activate();

    /**
     * Called every frame when the scene is active.
     *
     * @param delta Time in seconds since the last frame
     * @throws IllegalStateException if scene is not activated
     */
    void update(float delta);

    /**
     * Called when the scene is deactivated.
     * <p>
     * This is called when switching to another scene.
     * The scene should pause or save state but not dispose resources.
     * </p>
     */
    void deactivate();

    /**
     * Called when the scene is being disposed.
     * <p>
     * All resources owned by the scene must be released.
     * This method must be safe to call multiple times.
     * </p>
     */
    @Override
    void dispose();

    /**
     * Returns the root node of the scene's scene graph.
     *
     * @return The root scene node
     */
    SceneNode getRoot();

    /**
     * Returns whether the scene is currently active.
     *
     * @return true if active, false otherwise
     */
    boolean isActive();
}
