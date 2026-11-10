package engine.scene;

import engine.core.Disposable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EntityTest {

    @Test
    void entityHasNoComponentsInitially() {
        Entity entity = new Entity("TestEntity");
        assertEquals(0, entity.getComponentCount());
    }

    @Test
    void addComponentAddsComponent() {
        Entity entity = new Entity("TestEntity");
        TestComponent component = new TestComponent();

        entity.addComponent(component);

        assertEquals(1, entity.getComponentCount());
        assertEquals(component, entity.getComponent(TestComponent.class));
    }

    @Test
    void addComponentRejectsNull() {
        Entity entity = new Entity("TestEntity");
        assertThrows(IllegalArgumentException.class, () -> entity.addComponent(null));
    }

    @Test
    void addComponentRejectsAfterDisposal() {
        Entity entity = new Entity("TestEntity");
        entity.dispose();

        assertThrows(IllegalStateException.class, () -> entity.addComponent(new TestComponent()));
    }

    @Test
    void addComponentReplacesExistingComponent() {
        Entity entity = new Entity("TestEntity");
        TestComponent component1 = new TestComponent();
        TestComponent component2 = new TestComponent();

        entity.addComponent(component1);
        entity.addComponent(component2);

        assertEquals(1, entity.getComponentCount());
        assertEquals(component2, entity.getComponent(TestComponent.class));
    }

    @Test
    void addComponentDisposesReplacedComponent() {
        Entity entity = new Entity("TestEntity");
        DisposableComponent component1 = new DisposableComponent();
        DisposableComponent component2 = new DisposableComponent();

        entity.addComponent(component1);
        entity.addComponent(component2);

        assertTrue(component1.isDisposed());
        assertFalse(component2.isDisposed());
    }

    @Test
    void getComponentReturnsNullForMissing() {
        Entity entity = new Entity("TestEntity");
        assertNull(entity.getComponent(TestComponent.class));
    }

    @Test
    void getComponentRejectsNullClass() {
        Entity entity = new Entity("TestEntity");
        assertThrows(IllegalArgumentException.class, () -> entity.getComponent(null));
    }

    @Test
    void hasComponentReturnsTrueForExisting() {
        Entity entity = new Entity("TestEntity");
        entity.addComponent(new TestComponent());

        assertTrue(entity.hasComponent(TestComponent.class));
    }

    @Test
    void hasComponentReturnsFalseForMissing() {
        Entity entity = new Entity("TestEntity");
        assertFalse(entity.hasComponent(TestComponent.class));
    }

    @Test
    void removeComponentRemovesComponent() {
        Entity entity = new Entity("TestEntity");
        TestComponent component = new TestComponent();

        entity.addComponent(component);
        TestComponent removed = entity.removeComponent(TestComponent.class);

        assertEquals(component, removed);
        assertEquals(0, entity.getComponentCount());
        assertNull(entity.getComponent(TestComponent.class));
    }

    @Test
    void removeComponentReturnsNullForMissing() {
        Entity entity = new Entity("TestEntity");
        assertNull(entity.removeComponent(TestComponent.class));
    }

    @Test
    void removeComponentRejectsNullClass() {
        Entity entity = new Entity("TestEntity");
        assertThrows(IllegalArgumentException.class, () -> entity.removeComponent(null));
    }

    @Test
    void removeComponentDisposesRemovedComponent() {
        Entity entity = new Entity("TestEntity");
        DisposableComponent component = new DisposableComponent();

        entity.addComponent(component);
        entity.removeComponent(DisposableComponent.class);

        assertTrue(component.isDisposed());
    }

    @Test
    void disposeDisposesAllComponents() {
        Entity entity = new Entity("TestEntity");
        DisposableComponent component1 = new DisposableComponent();
        DisposableComponent component2 = new DisposableComponent();

        entity.addComponent(component1);
        entity.addComponent(component2);
        entity.dispose();

        assertTrue(component1.isDisposed());
        assertTrue(component2.isDisposed());
    }

    @Test
    void disposeIsIdempotent() {
        Entity entity = new Entity("TestEntity");
        entity.dispose();
        assertDoesNotThrow(entity::dispose);
    }

    @Test
    void disposedEntityCannotAddComponents() {
        Entity entity = new Entity("TestEntity");
        entity.dispose();

        assertThrows(IllegalStateException.class, () -> entity.addComponent(new TestComponent()));
    }

    @Test
    void multipleComponentTypesCoexist() {
        Entity entity = new Entity("TestEntity");
        TestComponent component1 = new TestComponent();
        AnotherComponent component2 = new AnotherComponent();

        entity.addComponent(component1);
        entity.addComponent(component2);

        assertEquals(2, entity.getComponentCount());
        assertEquals(component1, entity.getComponent(TestComponent.class));
        assertEquals(component2, entity.getComponent(AnotherComponent.class));
    }

    // Test helper classes
    private static class TestComponent implements Component {
    }

    private static class AnotherComponent implements Component {
    }

    private static class DisposableComponent implements Component, Disposable {
        private boolean disposed = false;

        @Override
        public void dispose() {
            disposed = true;
        }

        public boolean isDisposed() {
            return disposed;
        }
    }
}
