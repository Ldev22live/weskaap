package engine.scene;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

import java.util.UUID;

/**
 * A node in the hierarchical scene graph.
 * <p>
 * SceneNodes support parent-child relationships, local transforms,
 * and world transform calculation. Transform changes propagate
 * to descendants.
 * </p>
 * <p>
 * Transform conventions:
 * <ul>
 *   <li>Position: Local translation relative to parent (Vector3)</li>
 *   <li>Rotation: Local rotation as Euler angles in degrees (Vector3: pitch, yaw, roll)</li>
 *   <li>Scale: Local scale relative to parent (Vector3)</li>
 *   <li>World transform: Computed as parentWorld × localTransform</li>
 *   <li>Transform multiplication order: Parent × Child (standard scene graph)</li>
 * </ul>
 * </p>
 * <p>
 * Ownership rules:
 * <ul>
 *   <li>Parent owns its children (disposed when parent is disposed)</li>
 *   <li>Detached nodes are not disposed (caller owns them)</li>
 *   <li>Root nodes are owned by the scene</li>
 * </ul>
 * </p>
 */
public class SceneNode {

    private final String name;
    private SceneNode parent;
    private final java.util.List<SceneNode> children = new java.util.ArrayList<>();

    // Local transform
    private final Vector3 position = new Vector3();
    private final Vector3 rotation = new Vector3(); // Euler angles in degrees
    private final Vector3 scale = new Vector3(1, 1, 1);

    // Cached world transform (invalidated on transform change)
    private final Matrix4 worldTransform = new Matrix4();
    private boolean worldTransformDirty = true;

    private boolean disposed = false;

    /**
     * Creates a new scene node with the given name.
     *
     * @param name The node name (for debugging)
     */
    public SceneNode(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Node name cannot be null or empty");
        }
        this.name = name;
    }

    /**
     * Creates a new scene node with an auto-generated name.
     */
    public SceneNode() {
        this("Node_" + UUID.randomUUID().toString().substring(0, 8));
    }

    // ===== Parent-child relationship =====

    /**
     * Adds a child node to this node.
     *
     * @param child The child node to add
     * @throws IllegalArgumentException if child is null or this node
     * @throws IllegalStateException if adding would create a cycle
     */
    public void addChild(SceneNode child) {
        if (disposed) {
            throw new IllegalStateException("Cannot add child to disposed node");
        }
        if (child == null) {
            throw new IllegalArgumentException("Child cannot be null");
        }
        if (child == this) {
            throw new IllegalArgumentException("Node cannot be its own child");
        }
        if (child.parent != null) {
            child.parent.removeChild(child);
        }

        // Check for cycles
        SceneNode ancestor = this.parent;
        while (ancestor != null) {
            if (ancestor == child) {
                throw new IllegalStateException("Adding child would create a cycle");
            }
            ancestor = ancestor.parent;
        }

        child.parent = this;
        children.add(child);
        child.invalidateWorldTransform();
    }

    /**
     * Removes a child node from this node.
     * <p>
     * The detached node is not disposed and becomes a root node.
     * </p>
     *
     * @param child The child node to remove
     * @return true if the child was removed, false otherwise
     */
    public boolean removeChild(SceneNode child) {
        if (disposed) {
            throw new IllegalStateException("Cannot remove child from disposed node");
        }
        if (child == null) {
            throw new IllegalStateException("Child cannot be null");
        }
        if (child.parent != this) {
            return false;
        }

        child.parent = null;
        children.remove(child);
        child.invalidateWorldTransform();
        return true;
    }

    /**
     * Returns the parent node, or null if this is a root node.
     *
     * @return The parent node, or null
     */
    public SceneNode getParent() {
        return parent;
    }

    /**
     * Returns an unmodifiable list of child nodes.
     *
     * @return The children list
     */
    public java.util.List<SceneNode> getChildren() {
        return java.util.Collections.unmodifiableList(children);
    }

    /**
     * Returns the number of children.
     *
     * @return The child count
     */
    public int getChildCount() {
        return children.size();
    }

    // ===== Transform accessors =====

    /**
     * Returns the local position (copy).
     *
     * @return A new Vector3 with the local position
     */
    public Vector3 getPosition() {
        return position.cpy();
    }

    /**
     * Sets the local position.
     *
     * @param position The new local position
     * @throws IllegalArgumentException if position is null
     */
    public void setPosition(Vector3 position) {
        if (position == null) {
            throw new IllegalArgumentException("Position cannot be null");
        }
        this.position.set(position);
        invalidateWorldTransform();
    }

    /**
     * Sets the local position from components.
     */
    public void setPosition(float x, float y, float z) {
        this.position.set(x, y, z);
        invalidateWorldTransform();
    }

    /**
     * Returns the local rotation as Euler angles in degrees (copy).
     *
     * @return A new Vector3 with the local rotation
     */
    public Vector3 getRotation() {
        return rotation.cpy();
    }

    /**
     * Sets the local rotation as Euler angles in degrees.
     *
     * @param rotation The new local rotation
     * @throws IllegalArgumentException if rotation is null
     */
    public void setRotation(Vector3 rotation) {
        if (rotation == null) {
            throw new IllegalArgumentException("Rotation cannot be null");
        }
        this.rotation.set(rotation);
        invalidateWorldTransform();
    }

    /**
     * Sets the local rotation from components (degrees).
     */
    public void setRotation(float pitch, float yaw, float roll) {
        this.rotation.set(pitch, yaw, roll);
        invalidateWorldTransform();
    }

    /**
     * Returns the local scale (copy).
     *
     * @return A new Vector3 with the local scale
     */
    public Vector3 getScale() {
        return scale.cpy();
    }

    /**
     * Sets the local scale.
     *
     * @param scale The new local scale
     * @throws IllegalArgumentException if scale is null
     */
    public void setScale(Vector3 scale) {
        if (scale == null) {
            throw new IllegalArgumentException("Scale cannot be null");
        }
        this.scale.set(scale);
        invalidateWorldTransform();
    }

    /**
     * Sets the local scale from components.
     */
    public void setScale(float x, float y, float z) {
        this.scale.set(x, y, z);
        invalidateWorldTransform();
    }

    /**
     * Returns the world transform matrix (copy).
     * <p>
     * The world transform is computed as parentWorld × localTransform.
     * </p>
     *
     * @return A new Matrix4 with the world transform
     */
    public Matrix4 getWorldTransform() {
        if (worldTransformDirty) {
            updateWorldTransform();
        }
        return worldTransform.cpy();
    }

    /**
     * Returns the world position (copy).
     *
     * @return A new Vector3 with the world position
     */
    public Vector3 getWorldPosition() {
        if (worldTransformDirty) {
            updateWorldTransform();
        }
        return new Vector3(worldTransform.getTranslation(new Vector3()));
    }

    // ===== Internal methods =====

    private void invalidateWorldTransform() {
        worldTransformDirty = true;
        for (SceneNode child : children) {
            child.invalidateWorldTransform();
        }
    }

    private void updateWorldTransform() {
        // Compute local transform matrix
        Matrix4 localMatrix = new Matrix4();
        localMatrix.translate(position);
        localMatrix.rotate(rotation.x, 1, 0, 0); // pitch
        localMatrix.rotate(rotation.y, 0, 1, 0); // yaw
        localMatrix.rotate(rotation.z, 0, 0, 1); // roll
        localMatrix.scale(scale.x, scale.y, scale.z);

        // If we have a parent, world = parent × local
        if (parent != null) {
            Matrix4 parentWorld = parent.getWorldTransform();
            worldTransform.set(parentWorld).mul(localMatrix);
        } else {
            worldTransform.set(localMatrix);
        }

        worldTransformDirty = false;
    }

    // ===== Disposal =====

    /**
     * Disposes this node and all its children recursively.
     * <p>
     * Disposal is idempotent. Detached nodes are not disposed.
     * </p>
     */
    public void dispose() {
        if (disposed) {
            return;
        }
        disposed = true;

        // Dispose children in reverse order
        for (int i = children.size() - 1; i >= 0; i--) {
            SceneNode child = children.get(i);
            child.parent = null;
            child.dispose();
        }
        children.clear();

        if (parent != null) {
            SceneNode p = parent;
            p.removeChild(this);
            parent = null;
        }
    }

    /**
     * Returns whether this node has been disposed.
     *
     * @return true if disposed, false otherwise
     */
    public boolean isDisposed() {
        return disposed;
    }

    /**
     * Returns the node name.
     *
     * @return The node name
     */
    public String getName() {
        return name;
    }
}
