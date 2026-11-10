package engine.sample;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import engine.core.Engine;
import engine.scene.BaseScene;
import engine.scene.Component;
import engine.scene.Entity;
import engine.scene.SceneManager;
import engine.scene.SceneNode;

/**
 * LibGDX ApplicationListener that adapts LibGDX lifecycle to engine-core and engine-scene.
 * <p>
 * This class demonstrates:
 * - Engine lifecycle integration with LibGDX
 * - Scene creation and activation
 * - Scene graph hierarchy with transforms
 * - Entity-component system
 * - Scene switching and cleanup
 * </p>
 */
public class EngineSampleApplication implements ApplicationListener {

    private final Engine engine;
    private SceneManager sceneManager;
    private int updateCount = 0;

    public EngineSampleApplication(Engine engine) {
        this.engine = engine;
    }

    @Override
    public void create() {
        System.out.println("[Sample] LibGDX create() called");
        engine.create();
        System.out.println("[Sample] Engine state after create: " + engine.getState());

        // Initialize scene manager
        sceneManager = new SceneManager();

        // Create first scene with scene graph
        BaseScene scene1 = createScene1();
        sceneManager.switchScene(scene1);

        System.out.println("[Sample] Scene 1 activated with " + scene1.getRoot().getChildCount() + " root children");
    }

    private BaseScene createScene1() {
        BaseScene scene = new BaseScene("Scene1");
        SceneNode root = scene.getRoot();

        // Create a hierarchical scene graph
        SceneNode parent = new SceneNode("ParentNode");
        parent.setPosition(5, 0, 0);
        root.addChild(parent);

        SceneNode child1 = new SceneNode("Child1");
        child1.setPosition(3, 2, 0);
        parent.addChild(child1);

        SceneNode child2 = new SceneNode("Child2");
        child2.setPosition(-2, 1, 0);
        parent.addChild(child2);

        SceneNode grandchild = new SceneNode("Grandchild");
        grandchild.setPosition(1, 0, 0);
        child1.addChild(grandchild);

        // Create an entity with components
        Entity entity = new Entity("TestEntity");
        entity.addComponent(new TestComponent("value1"));
        entity.addComponent(new AnotherComponent(42));

        System.out.println("[Sample] Created entity with " + entity.getComponentCount() + " components");
        System.out.println("[Sample] Grandchild world position: " + grandchild.getWorldPosition());

        return scene;
    }

    private BaseScene createScene2() {
        BaseScene scene = new BaseScene("Scene2");
        SceneNode root = scene.getRoot();

        SceneNode node = new SceneNode("Scene2Node");
        node.setPosition(10, 10, 10);
        root.addChild(node);

        return scene;
    }

    @Override
    public void resize(int width, int height) {
        System.out.println("[Sample] LibGDX resize() called: " + width + "x" + height);
        engine.resize(width, height);
    }

    @Override
    public void render() {
        float delta = 0.016f; // ~60 FPS

        engine.update(delta);

        if (sceneManager != null && sceneManager.hasActiveScene()) {
            sceneManager.update(delta);
        }

        updateCount++;

        // Switch scenes after 10 updates to demonstrate scene switching
        if (updateCount == 10) {
            System.out.println("[Sample] Switching to Scene 2...");
            BaseScene scene2 = createScene2();
            sceneManager.switchScene(scene2);
            System.out.println("[Sample] Scene 2 activated: " + scene2.isActive() + " with " + scene2.getRoot().getChildCount() + " child");
        }

        if (updateCount <= 5) {
            System.out.println("[Sample] Update tick #" + updateCount + ", engine state: " + engine.getState());
        }
    }

    @Override
    public void pause() {
        System.out.println("[Sample] LibGDX pause() called");
        engine.pause();
        System.out.println("[Sample] Engine state after pause: " + engine.getState());
    }

    @Override
    public void resume() {
        System.out.println("[Sample] LibGDX resume() called");
        engine.resume();
        System.out.println("[Sample] Engine state after resume: " + engine.getState());
    }

    @Override
    public void dispose() {
        System.out.println("[Sample] LibGDX dispose() called");

        if (sceneManager != null) {
            sceneManager.dispose();
        }

        engine.dispose();
        System.out.println("[Sample] Engine state after dispose: " + engine.getState());
        System.out.println("[Sample] Total updates: " + updateCount);
    }

    // Test component classes
    private static class TestComponent implements Component {
        private final String value;

        TestComponent(String value) {
            this.value = value;
        }

        String getValue() {
            return value;
        }
    }

    private static class AnotherComponent implements Component {
        private final int number;

        AnotherComponent(int number) {
            this.number = number;
        }

        int getNumber() {
            return number;
        }
    }
}
