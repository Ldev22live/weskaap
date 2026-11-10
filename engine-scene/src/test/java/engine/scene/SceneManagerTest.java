package engine.scene;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SceneManagerTest {

    @Test
    void managerHasNoSceneInitially() {
        SceneManager manager = new SceneManager();
        assertNull(manager.getCurrentScene());
        assertFalse(manager.hasActiveScene());
    }

    @Test
    void switchSceneActivatesScene() {
        SceneManager manager = new SceneManager();
        BaseScene scene = new BaseScene("TestScene");

        manager.switchScene(scene);

        assertEquals(scene, manager.getCurrentScene());
        assertTrue(scene.isActive());
        assertTrue(manager.hasActiveScene());
    }

    @Test
    void switchSceneRejectsNull() {
        SceneManager manager = new SceneManager();
        assertThrows(IllegalArgumentException.class, () -> manager.switchScene(null));
    }

    @Test
    void switchSceneRejectsAfterDisposal() {
        SceneManager manager = new SceneManager();
        manager.dispose();

        assertThrows(IllegalStateException.class, () -> manager.switchScene(new BaseScene("Test")));
    }

    @Test
    void switchSceneDeactivatesOldScene() {
        SceneManager manager = new SceneManager();
        BaseScene scene1 = new BaseScene("Scene1");
        BaseScene scene2 = new BaseScene("Scene2");

        manager.switchScene(scene1);
        manager.switchScene(scene2);

        assertFalse(scene1.isActive());
        assertTrue(scene2.isActive());
    }

    @Test
    void switchSceneDisposesOldScene() {
        SceneManager manager = new SceneManager();
        BaseScene scene1 = new BaseScene("Scene1");
        BaseScene scene2 = new BaseScene("Scene2");

        manager.switchScene(scene1);
        manager.switchScene(scene2);

        assertTrue(scene1.isDisposed());
        assertFalse(scene2.isDisposed());
    }

    @Test
    void switchSceneToSameSceneIsNoOp() {
        SceneManager manager = new SceneManager();
        BaseScene scene = new BaseScene("TestScene");

        manager.switchScene(scene);
        manager.switchScene(scene);

        assertTrue(scene.isActive());
        assertFalse(scene.isDisposed());
    }

    @Test
    void switchSceneOnActivationFailureRestoresOldScene() {
        SceneManager manager = new SceneManager();
        BaseScene scene1 = new BaseScene("Scene1");
        FailingScene scene2 = new FailingScene();

        manager.switchScene(scene1);

        assertThrows(RuntimeException.class, () -> manager.switchScene(scene2));

        assertTrue(scene1.isActive());
        assertEquals(scene1, manager.getCurrentScene());
    }

    @Test
    void updateRoutesToCurrentScene() {
        SceneManager manager = new SceneManager();
        CountingScene scene = new CountingScene();

        manager.switchScene(scene);
        manager.update(0.016f);

        assertEquals(1, scene.getUpdateCount());
    }

    @Test
    void updateThrowsWhenNoSceneActive() {
        SceneManager manager = new SceneManager();
        assertThrows(IllegalStateException.class, () -> manager.update(0.016f));
    }

    @Test
    void disposeDisposesCurrentScene() {
        SceneManager manager = new SceneManager();
        BaseScene scene = new BaseScene("TestScene");

        manager.switchScene(scene);
        manager.dispose();

        assertTrue(scene.isDisposed());
        assertFalse(scene.isActive());
        assertNull(manager.getCurrentScene());
    }

    @Test
    void disposeIsIdempotent() {
        SceneManager manager = new SceneManager();
        manager.dispose();
        assertDoesNotThrow(manager::dispose);
    }

    @Test
    void disposeWithNoSceneIsSafe() {
        SceneManager manager = new SceneManager();
        assertDoesNotThrow(manager::dispose);
    }

    // Test helper classes
    private static class FailingScene extends BaseScene {
        @Override
        public void activate() {
            throw new RuntimeException("Activation failed");
        }
    }

    private static class CountingScene extends BaseScene {
        private int updateCount = 0;

        @Override
        public void update(float delta) {
            updateCount++;
        }

        public int getUpdateCount() {
            return updateCount;
        }
    }
}
