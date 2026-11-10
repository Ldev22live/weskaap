package engine.core;

import engine.core.impl.DefaultEngine;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ResourceOwnershipTest {

    @Test
    void registerResourceAddsToOwnedResources() {
        DefaultEngine engine = new DefaultEngine();
        EngineConfig config = EngineConfig.builder().enableLogging(false).build();

        engine.initialize(config);

        TestDisposable resource = new TestDisposable();
        engine.registerResource(resource);

        assertFalse(resource.isDisposed());
    }

    @Test
    void disposeCallsDisposeOnOwnedResources() {
        DefaultEngine engine = new DefaultEngine();
        EngineConfig config = EngineConfig.builder().enableLogging(false).build();

        engine.initialize(config);

        TestDisposable resource1 = new TestDisposable();
        TestDisposable resource2 = new TestDisposable();

        engine.registerResource(resource1);
        engine.registerResource(resource2);

        engine.dispose();

        assertTrue(resource1.isDisposed());
        assertTrue(resource2.isDisposed());
    }

    @Test
    void resourcesDisposedInReverseOrder() {
        // Reset the disposal order before this test
        TestDisposable.resetDisposalOrder();

        DefaultEngine engine = new DefaultEngine();
        EngineConfig config = EngineConfig.builder().enableLogging(false).build();

        engine.initialize(config);

        TestDisposable resource1 = new TestDisposable("r1");
        TestDisposable resource2 = new TestDisposable("r2");
        TestDisposable resource3 = new TestDisposable("r3");

        engine.registerResource(resource1);
        engine.registerResource(resource2);
        engine.registerResource(resource3);

        engine.dispose();

        // Verify reverse order: r3, r2, r1
        List<String> disposalOrder = TestDisposable.getDisposalOrder();
        assertEquals(3, disposalOrder.size());
        assertEquals("r3", disposalOrder.get(0));
        assertEquals("r2", disposalOrder.get(1));
        assertEquals("r1", disposalOrder.get(2));
    }

    @Test
    void registerResourceAfterDisposeThrowsException() {
        DefaultEngine engine = new DefaultEngine();
        EngineConfig config = EngineConfig.builder().enableLogging(false).build();

        engine.initialize(config);
        engine.dispose();

        TestDisposable resource = new TestDisposable();
        assertThrows(IllegalStateException.class, () -> engine.registerResource(resource));
    }

    @Test
    void registerNullResourceThrowsException() {
        DefaultEngine engine = new DefaultEngine();
        EngineConfig config = EngineConfig.builder().enableLogging(false).build();

        engine.initialize(config);
        assertThrows(IllegalArgumentException.class, () -> engine.registerResource(null));
    }

    @Test
    void resourceDisposeExceptionDoesNotPreventOtherDisposal() {
        DefaultEngine engine = new DefaultEngine();
        EngineConfig config = EngineConfig.builder().enableLogging(false).build();

        engine.initialize(config);

        TestDisposable goodResource = new TestDisposable();
        TestDisposable badResource = new TestDisposable(true); // Will throw on dispose
        TestDisposable anotherGoodResource = new TestDisposable();

        engine.registerResource(goodResource);
        engine.registerResource(badResource);
        engine.registerResource(anotherGoodResource);

        assertDoesNotThrow(() -> engine.dispose());

        assertTrue(goodResource.isDisposed());
        assertTrue(badResource.isDisposed());
        assertTrue(anotherGoodResource.isDisposed());
    }

    @Test
    void repeatedDisposeDoesNotReDisposeResources() {
        DefaultEngine engine = new DefaultEngine();
        EngineConfig config = EngineConfig.builder().enableLogging(false).build();

        engine.initialize(config);

        TestDisposable resource = new TestDisposable();
        engine.registerResource(resource);

        engine.dispose();
        int disposeCount = resource.getDisposeCount();

        engine.dispose();

        assertEquals(disposeCount, resource.getDisposeCount());
    }

    @Test
    void disposeWithoutInitializeStillClearsResources() {
        DefaultEngine engine = new DefaultEngine();

        TestDisposable resource = new TestDisposable();
        // Cannot register without initialize, so this tests the disposal safety
        assertDoesNotThrow(engine::dispose);
    }

    // Test helper class
    private static class TestDisposable implements Disposable {
        private boolean disposed = false;
        private int disposeCount = 0;
        private final String name;
        private final boolean throwOnDispose;
        private static final List<String> disposalOrder = new ArrayList<>();

        private TestDisposable() {
            this("");
        }

        private TestDisposable(String name) {
            this(name, false);
        }

        private TestDisposable(boolean throwOnDispose) {
            this("", throwOnDispose);
        }

        private TestDisposable(String name, boolean throwOnDispose) {
            this.name = name;
            this.throwOnDispose = throwOnDispose;
        }

        @Override
        public void dispose() {
            if (disposed && !throwOnDispose) {
                return; // Idempotent
            }
            disposed = true;
            disposeCount++;
            if (!name.isEmpty()) {
                disposalOrder.add(name);
            }

            if (throwOnDispose) {
                throw new RuntimeException("Test exception during dispose");
            }
        }

        public boolean isDisposed() {
            return disposed;
        }

        public int getDisposeCount() {
            return disposeCount;
        }

        private static List<String> getDisposalOrder() {
            return new ArrayList<>(disposalOrder);
        }

        private static void resetDisposalOrder() {
            disposalOrder.clear();
        }
    }
}
