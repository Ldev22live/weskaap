package engine.scene;

/**
 * Marker interface for entity components.
 * <p>
 * Components are data containers attached to entities.
 * They should not contain logic; logic is handled by systems.
 * Components can implement Disposable for resource cleanup.
 * </p>
 * <p>
 * Component examples:
 * <ul>
 *   <li>TransformComponent - position, rotation, scale</li>
 *   <li>RenderComponent - mesh, material</li>
 *   <li>PhysicsComponent - collision shape, mass</li>
 *   <li>ScriptComponent - behavior script reference</li>
 * </ul>
 * </p>
 */
public interface Component {
    // Marker interface - no methods required
}
