package engine.scene;

import engine.core.Disposable;

import java.util.UUID;

/**
 * Base implementation of Scene with standard lifecycle management.
 * <p>
 * This implementation provides:
 * - Activation/deactivation state tracking
 * - Root scene node management
 * - Standard disposal behavior
 * </p>
 */
public class BaseScene implements Scene {

    private final String name;
    private final SceneNode root;
    private boolean active = false;
    private boolean disposed = false;

    /**
     * Creates a new base scene with the given name.
     *
     * @param name The scene name
     */
    public BaseScene(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Scene name cannot be null or empty");
        }
        this.name = name;
        this.root = new SceneNode(name + "_Root");
    }

    /**
     * Creates a new base scene with an auto-generated name.
     */
    public BaseScene() {
        this("Scene_" + UUID.randomUUID().toString().substring(0, 8));
    }

    @Override
    public void activate() {
        if (disposed) {
            throw new IllegalStateException("Cannot activate disposed scene");
        }
        if (active) {
            throw new IllegalStateException("Scene is already active");
        }
        active = true;
    }

    @Override
    public void update(float delta) {
        if (!active) {
            throw new IllegalStateException("Scene is not active");
        }
        if (disposed) {
            throw new IllegalStateException("Scene is disposed");
        }
        // Update logic would be added here in future phases
    }

    @Override
    public void deactivate() {
        if (!active) {
            return; // Already deactivated
        }
        active = false;
    }

    @Override
    public void dispose() {
        if (disposed) {
            return;
        }
        disposed = true;

        if (active) {
            deactivate();
        }

        root.dispose();
    }

    @Override
    public SceneNode getRoot() {
        return root;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * Returns the scene name.
     *
     * @return The scene name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns whether the scene has been disposed.
     *
     * @return true if disposed, false otherwise
     */
    public boolean isDisposed() {
        return disposed;
    }
}
