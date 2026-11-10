package engine.core;

/**
 * Interface for objects that own resources that must be explicitly released.
 * <p>
 * This is compatible with LibGDX's Disposable interface but defined
 * in engine-core to avoid direct LibGDX dependencies where not needed.
 * </p>
 * <p>
 * Resource ownership rules:
 * <ul>
 *   <li>Engine owns core resources (shaders, default materials)</li>
 *   <li>Scenes own their resources (models, textures)</li>
 *   <li>Entities reference scene resources (no ownership)</li>
 *   <li>Disposal cascades: Scene dispose → Entity dispose → Component dispose</li>
 * </ul>
 * </p>
 */
public interface Disposable {

    /**
     * Releases all resources owned by this object.
     * <p>
     * This method must be safe to call multiple times.
     * After disposal, the object should not be used.
     * </p>
     */
    void dispose();
}
