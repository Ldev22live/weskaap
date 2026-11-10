package engine.scene;

import engine.core.Disposable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * An entity in the scene with attached components.
 * <p>
 * Entities are lightweight containers for components. They do not
 * inherit from SceneNode to avoid forcing unnecessary coupling.
 * Entities can be associated with scene nodes but are not required to be.
 * </p>
 * <p>
 * Component rules:
 * <ul>
 *   <li>Component types are unique per entity (one component per type)</li>
 *   <li>Adding a component of an existing type replaces the old one</li>
 *   <li>Removed components are disposed if they implement Disposable</li>
 *   <li>Entities do not own scene resources (they only reference them)</li>
 * </ul>
 * </p>
 * <p>
 * Lifecycle rules:
 * <ul>
 *   <li>Entities can be created independently of scenes</li>
 *   <li>Disposed entities cannot accept new components</li>
 *   <li>Entity disposal disposes all components</li>
 * </ul>
 * </p>
 */
public class Entity implements Disposable {

    private final String name;
    private final Map<Class<?>, Component> components = new HashMap<>();
    private boolean disposed = false;

    /**
     * Creates a new entity with the given name.
     *
     * @param name The entity name (for debugging)
     */
    public Entity(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Entity name cannot be null or empty");
        }
        this.name = name;
    }

    /**
     * Creates a new entity with an auto-generated name.
     */
    public Entity() {
        this("Entity_" + UUID.randomUUID().toString().substring(0, 8));
    }

    // ===== Component management =====

    /**
     * Adds a component to this entity.
     * <p>
     * If a component of this type already exists, it is replaced.
     * The replaced component is disposed if it implements Disposable.
     * </p>
     *
     * @param component The component to add
     * @param <T> The component type
     * @throws IllegalStateException if entity is disposed
     * @throws IllegalArgumentException if component is null
     */
    public <T extends Component> void addComponent(T component) {
        if (disposed) {
            throw new IllegalStateException("Cannot add component to disposed entity");
        }
        if (component == null) {
            throw new IllegalArgumentException("Component cannot be null");
        }

        Class<?> componentClass = component.getClass();
        Component existing = components.get(componentClass);

        if (existing != null) {
            // Dispose the existing component if it's disposable
            if (existing instanceof Disposable) {
                ((Disposable) existing).dispose();
            }
        }

        components.put(componentClass, component);
    }

    /**
     * Returns a component of the given type, or null if not found.
     *
     * @param componentClass The component class
     * @param <T> The component type
     * @return The component, or null
     * @throws IllegalArgumentException if componentClass is null
     */
    @SuppressWarnings("unchecked")
    public <T extends Component> T getComponent(Class<T> componentClass) {
        if (componentClass == null) {
            throw new IllegalArgumentException("Component class cannot be null");
        }
        return (T) components.get(componentClass);
    }

    /**
     * Returns whether this entity has a component of the given type.
     *
     * @param componentClass The component class
     * @param <T> The component type
     * @return true if the component exists, false otherwise
     */
    public <T extends Component> boolean hasComponent(Class<T> componentClass) {
        return components.containsKey(componentClass);
    }

    /**
     * Removes a component of the given type.
     * <p>
     * The removed component is disposed if it implements Disposable.
     * </p>
     *
     * @param componentClass The component class
     * @param <T> The component type
     * @return The removed component, or null if not found
     * @throws IllegalArgumentException if componentClass is null
     */
    @SuppressWarnings("unchecked")
    public <T extends Component> T removeComponent(Class<T> componentClass) {
        if (componentClass == null) {
            throw new IllegalArgumentException("Component class cannot be null");
        }

        Component removed = components.remove(componentClass);
        if (removed != null && removed instanceof Disposable) {
            ((Disposable) removed).dispose();
        }
        return (T) removed;
    }

    /**
     * Returns the number of components attached to this entity.
     *
     * @return The component count
     */
    public int getComponentCount() {
        return components.size();
    }

    // ===== Disposal =====

    @Override
    public void dispose() {
        if (disposed) {
            return;
        }
        disposed = true;

        // Dispose all components
        for (Component component : components.values()) {
            if (component instanceof Disposable) {
                ((Disposable) component).dispose();
            }
        }
        components.clear();
    }

    /**
     * Returns whether this entity has been disposed.
     *
     * @return true if disposed, false otherwise
     */
    public boolean isDisposed() {
        return disposed;
    }

    /**
     * Returns the entity name.
     *
     * @return The entity name
     */
    public String getName() {
        return name;
    }
}
