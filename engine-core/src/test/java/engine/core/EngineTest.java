package engine.core;

import engine.core.impl.DefaultEngine;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EngineTest {

    @Test
    void initialStateIsUninitialized() {
        Engine engine = new DefaultEngine();
        assertEquals(EngineState.UNINITIALIZED, engine.getState());
        assertNull(engine.getConfig());
    }

    @Test
    void initializeTransitionsToInitializedState() {
        Engine engine = new DefaultEngine();
        EngineConfig config = EngineConfig.builder()
            .title("Test Engine")
            .build();

        engine.initialize(config);

        assertEquals(EngineState.INITIALIZED, engine.getState());
        assertEquals("Test Engine", engine.getConfig().getTitle());
    }

    @Test
    void initializeWithNullConfigThrowsException() {
        Engine engine = new DefaultEngine();
        assertThrows(IllegalArgumentException.class, () -> engine.initialize(null));
        assertEquals(EngineState.UNINITIALIZED, engine.getState());
    }

    @Test
    void initializeTwiceThrowsException() {
        Engine engine = new DefaultEngine();
        EngineConfig config = EngineConfig.builder().build();

        engine.initialize(config);
        assertThrows(IllegalStateException.class, () -> engine.initialize(config));
    }

    @Test
    void createTransitionsToRunningState() {
        Engine engine = new DefaultEngine();
        EngineConfig config = EngineConfig.builder().build();

        engine.initialize(config);
        engine.create();

        assertEquals(EngineState.RUNNING, engine.getState());
    }

    @Test
    void createWithoutInitializeThrowsException() {
        Engine engine = new DefaultEngine();
        assertThrows(IllegalStateException.class, engine::create);
    }

    @Test
    void createTwiceThrowsException() {
        Engine engine = new DefaultEngine();
        EngineConfig config = EngineConfig.builder().build();

        engine.initialize(config);
        engine.create();
        assertThrows(IllegalStateException.class, engine::create);
    }

    @Test
    void updateWithValidDeltaSucceeds() {
        Engine engine = new DefaultEngine();
        EngineConfig config = EngineConfig.builder().build();

        engine.initialize(config);
        engine.create();
        assertDoesNotThrow(() -> engine.update(0.016f));
    }

    @Test
    void updateWithoutRunningStateThrowsException() {
        Engine engine = new DefaultEngine();
        EngineConfig config = EngineConfig.builder().build();

        engine.initialize(config);
        assertThrows(IllegalStateException.class, () -> engine.update(0.016f));
    }

    @Test
    void updateWithNegativeDeltaThrowsException() {
        Engine engine = new DefaultEngine();
        EngineConfig config = EngineConfig.builder().build();

        engine.initialize(config);
        engine.create();
        assertThrows(IllegalArgumentException.class, () -> engine.update(-0.016f));
    }

    @Test
    void updateWithNaNDeltaThrowsException() {
        Engine engine = new DefaultEngine();
        EngineConfig config = EngineConfig.builder().build();

        engine.initialize(config);
        engine.create();
        assertThrows(IllegalArgumentException.class, () -> engine.update(Float.NaN));
    }

    @Test
    void updateWithInfiniteDeltaThrowsException() {
        Engine engine = new DefaultEngine();
        EngineConfig config = EngineConfig.builder().build();

        engine.initialize(config);
        engine.create();
        assertThrows(IllegalArgumentException.class, () -> engine.update(Float.POSITIVE_INFINITY));
    }

    @Test
    void pauseTransitionsToPausedState() {
        Engine engine = new DefaultEngine();
        EngineConfig config = EngineConfig.builder().build();

        engine.initialize(config);
        engine.create();
        engine.pause();

        assertEquals(EngineState.PAUSED, engine.getState());
    }

    @Test
    void pauseWhenNotRunningIsNoOp() {
        Engine engine = new DefaultEngine();
        EngineConfig config = EngineConfig.builder().build();

        engine.initialize(config);
        assertDoesNotThrow(engine::pause);
        assertEquals(EngineState.INITIALIZED, engine.getState());
    }

    @Test
    void resumeTransitionsBackToRunning() {
        Engine engine = new DefaultEngine();
        EngineConfig config = EngineConfig.builder().build();

        engine.initialize(config);
        engine.create();
        engine.pause();
        engine.resume();

        assertEquals(EngineState.RUNNING, engine.getState());
    }

    @Test
    void resumeWhenNotPausedIsNoOp() {
        Engine engine = new DefaultEngine();
        EngineConfig config = EngineConfig.builder().build();

        engine.initialize(config);
        engine.create();
        assertDoesNotThrow(engine::resume);
        assertEquals(EngineState.RUNNING, engine.getState());
    }

    @Test
    void resizeWithValidDimensionsSucceeds() {
        Engine engine = new DefaultEngine();
        EngineConfig config = EngineConfig.builder().build();

        engine.initialize(config);
        assertDoesNotThrow(() -> engine.resize(800, 600));
    }

    @Test
    void resizeWithNegativeDimensionsThrowsException() {
        Engine engine = new DefaultEngine();
        EngineConfig config = EngineConfig.builder().build();

        engine.initialize(config);
        assertThrows(IllegalArgumentException.class, () -> engine.resize(-100, 600));
    }

    @Test
    void resizeWithoutInitializeThrowsException() {
        Engine engine = new DefaultEngine();
        assertThrows(IllegalStateException.class, () -> engine.resize(800, 600));
    }

    @Test
    void disposeTransitionsToDisposedState() {
        Engine engine = new DefaultEngine();
        EngineConfig config = EngineConfig.builder().build();

        engine.initialize(config);
        engine.dispose();

        assertEquals(EngineState.DISPOSED, engine.getState());
    }

    @Test
    void disposeIsIdempotent() {
        Engine engine = new DefaultEngine();
        EngineConfig config = EngineConfig.builder().build();

        engine.initialize(config);
        engine.dispose();
        assertDoesNotThrow(engine::dispose);
        assertEquals(EngineState.DISPOSED, engine.getState());
    }

    @Test
    void disposeWithoutInitializeIsSafe() {
        Engine engine = new DefaultEngine();
        assertDoesNotThrow(engine::dispose);
        assertEquals(EngineState.DISPOSED, engine.getState());
    }

    @Test
    void operationsAfterDisposeThrowException() {
        Engine engine = new DefaultEngine();
        EngineConfig config = EngineConfig.builder().build();

        engine.initialize(config);
        engine.dispose();

        assertThrows(IllegalStateException.class, () -> engine.create());
        assertThrows(IllegalStateException.class, () -> engine.update(0.016f));
        assertThrows(IllegalStateException.class, () -> engine.resize(800, 600));
    }
}
