package engine.scene;

import com.badlogic.gdx.math.Vector3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SceneNodeTest {

    @Test
    void rootNodeHasNoParent() {
        SceneNode node = new SceneNode("Root");
        assertNull(node.getParent());
    }

    @Test
    void rootNodeHasNoChildren() {
        SceneNode node = new SceneNode("Root");
        assertEquals(0, node.getChildCount());
    }

    @Test
    void addChildAddsToParent() {
        SceneNode parent = new SceneNode("Parent");
        SceneNode child = new SceneNode("Child");

        parent.addChild(child);

        assertEquals(1, parent.getChildCount());
        assertEquals(parent, child.getParent());
    }

    @Test
    void addChildRemovesFromOldParent() {
        SceneNode parent1 = new SceneNode("Parent1");
        SceneNode parent2 = new SceneNode("Parent2");
        SceneNode child = new SceneNode("Child");

        parent1.addChild(child);
        parent2.addChild(child);

        assertEquals(0, parent1.getChildCount());
        assertEquals(1, parent2.getChildCount());
        assertEquals(parent2, child.getParent());
    }

    @Test
    void addChildRejectsNull() {
        SceneNode parent = new SceneNode("Parent");
        assertThrows(IllegalArgumentException.class, () -> parent.addChild(null));
    }

    @Test
    void addChildRejectsSelf() {
        SceneNode node = new SceneNode("Node");
        assertThrows(IllegalArgumentException.class, () -> node.addChild(node));
    }

    @Test
    void addChildRejectsCycle() {
        SceneNode grandparent = new SceneNode("Grandparent");
        SceneNode parent = new SceneNode("Parent");
        SceneNode child = new SceneNode("Child");

        grandparent.addChild(parent);
        parent.addChild(child);

        assertThrows(IllegalStateException.class, () -> child.addChild(grandparent));
    }

    @Test
    void removeChildRemovesFromParent() {
        SceneNode parent = new SceneNode("Parent");
        SceneNode child = new SceneNode("Child");

        parent.addChild(child);
        assertTrue(parent.removeChild(child));

        assertEquals(0, parent.getChildCount());
        assertNull(child.getParent());
    }

    @Test
    void removeChildReturnsFalseForNonChild() {
        SceneNode parent = new SceneNode("Parent");
        SceneNode child = new SceneNode("Child");

        assertFalse(parent.removeChild(child));
    }

    @Test
    void removeChildRejectsNull() {
        SceneNode parent = new SceneNode("Parent");
        assertThrows(IllegalStateException.class, () -> parent.removeChild(null));
    }

    @Test
    void detachedNodeIsNotDisposed() {
        SceneNode parent = new SceneNode("Parent");
        SceneNode child = new SceneNode("Child");

        parent.addChild(child);
        parent.removeChild(child);

        assertFalse(child.isDisposed());
    }

    @Test
    void localPositionDefaultsToZero() {
        SceneNode node = new SceneNode("Node");
        Vector3 position = node.getPosition();
        assertEquals(0, position.x, 0.001f);
        assertEquals(0, position.y, 0.001f);
        assertEquals(0, position.z, 0.001f);
    }

    @Test
    void localRotationDefaultsToZero() {
        SceneNode node = new SceneNode("Node");
        Vector3 rotation = node.getRotation();
        assertEquals(0, rotation.x, 0.001f);
        assertEquals(0, rotation.y, 0.001f);
        assertEquals(0, rotation.z, 0.001f);
    }

    @Test
    void localScaleDefaultsToOne() {
        SceneNode node = new SceneNode("Node");
        Vector3 scale = node.getScale();
        assertEquals(1, scale.x, 0.001f);
        assertEquals(1, scale.y, 0.001f);
        assertEquals(1, scale.z, 0.001f);
    }

    @Test
    void setPositionUpdatesLocalPosition() {
        SceneNode node = new SceneNode("Node");
        node.setPosition(1, 2, 3);

        Vector3 position = node.getPosition();
        assertEquals(1, position.x, 0.001f);
        assertEquals(2, position.y, 0.001f);
        assertEquals(3, position.z, 0.001f);
    }

    @Test
    void setPositionRejectsNull() {
        SceneNode node = new SceneNode("Node");
        assertThrows(IllegalArgumentException.class, () -> node.setPosition(null));
    }

    @Test
    void setRotationUpdatesLocalRotation() {
        SceneNode node = new SceneNode("Node");
        node.setRotation(45, 90, 135);

        Vector3 rotation = node.getRotation();
        assertEquals(45, rotation.x, 0.001f);
        assertEquals(90, rotation.y, 0.001f);
        assertEquals(135, rotation.z, 0.001f);
    }

    @Test
    void setRotationRejectsNull() {
        SceneNode node = new SceneNode("Node");
        assertThrows(IllegalArgumentException.class, () -> node.setRotation(null));
    }

    @Test
    void setScaleUpdatesLocalScale() {
        SceneNode node = new SceneNode("Node");
        node.setScale(2, 3, 4);

        Vector3 scale = node.getScale();
        assertEquals(2, scale.x, 0.001f);
        assertEquals(3, scale.y, 0.001f);
        assertEquals(4, scale.z, 0.001f);
    }

    @Test
    void setScaleRejectsNull() {
        SceneNode node = new SceneNode("Node");
        assertThrows(IllegalArgumentException.class, () -> node.setScale(null));
    }

    @Test
    void worldPositionEqualsLocalForRoot() {
        SceneNode node = new SceneNode("Node");
        node.setPosition(5, 10, 15);

        Vector3 worldPos = node.getWorldPosition();
        assertEquals(5, worldPos.x, 0.001f);
        assertEquals(10, worldPos.y, 0.001f);
        assertEquals(15, worldPos.z, 0.001f);
    }

    @Test
    void worldPositionIncludesParentTransform() {
        SceneNode parent = new SceneNode("Parent");
        parent.setPosition(10, 20, 30);

        SceneNode child = new SceneNode("Child");
        child.setPosition(5, 10, 15);
        parent.addChild(child);

        Vector3 worldPos = child.getWorldPosition();
        assertEquals(15, worldPos.x, 0.001f);
        assertEquals(30, worldPos.y, 0.001f);
        assertEquals(45, worldPos.z, 0.001f);
    }

    @Test
    void nestedTransformsComposeCorrectly() {
        SceneNode grandparent = new SceneNode("Grandparent");
        grandparent.setPosition(10, 0, 0);

        SceneNode parent = new SceneNode("Parent");
        parent.setPosition(5, 0, 0);
        grandparent.addChild(parent);

        SceneNode child = new SceneNode("Child");
        child.setPosition(3, 0, 0);
        parent.addChild(child);

        Vector3 worldPos = child.getWorldPosition();
        assertEquals(18, worldPos.x, 0.001f);
        assertEquals(0, worldPos.y, 0.001f);
        assertEquals(0, worldPos.z, 0.001f);
    }

    @Test
    void transformChangeInvalidatesChildren() {
        SceneNode parent = new SceneNode("Parent");
        SceneNode child = new SceneNode("Child");

        parent.addChild(child);
        child.getWorldPosition(); // Cache world transform

        parent.setPosition(10, 0, 0);

        Vector3 worldPos = child.getWorldPosition();
        assertEquals(10, worldPos.x, 0.001f);
    }

    @Test
    void disposeDisposesChildrenRecursively() {
        SceneNode parent = new SceneNode("Parent");
        SceneNode child1 = new SceneNode("Child1");
        SceneNode child2 = new SceneNode("Child2");

        parent.addChild(child1);
        parent.addChild(child2);

        parent.dispose();

        assertTrue(parent.isDisposed());
        assertTrue(child1.isDisposed());
        assertTrue(child2.isDisposed());
    }

    @Test
    void disposeIsIdempotent() {
        SceneNode node = new SceneNode("Node");
        node.dispose();
        assertDoesNotThrow(node::dispose);
    }

    @Test
    void disposeRemovesFromParent() {
        SceneNode parent = new SceneNode("Parent");
        SceneNode child = new SceneNode("Child");

        parent.addChild(child);
        child.dispose();

        assertEquals(0, parent.getChildCount());
    }

    @Test
    void disposedNodeCannotAddChildren() {
        SceneNode parent = new SceneNode("Parent");
        SceneNode child = new SceneNode("Child");

        parent.dispose();
        assertThrows(IllegalStateException.class, () -> parent.addChild(child));
    }

    @Test
    void disposedNodeCannotRemoveChildren() {
        SceneNode parent = new SceneNode("Parent");
        SceneNode child = new SceneNode("Child");

        parent.addChild(child);
        parent.dispose();

        assertThrows(IllegalStateException.class, () -> parent.removeChild(child));
    }

    @Test
    void getChildrenReturnsUnmodifiableList() {
        SceneNode parent = new SceneNode("Parent");
        SceneNode child = new SceneNode("Child");

        parent.addChild(child);
        var children = parent.getChildren();

        assertThrows(UnsupportedOperationException.class, () -> children.add(new SceneNode("Another")));
    }

    @Test
    void nonUniformScaleIsSupported() {
        SceneNode node = new SceneNode("Node");
        node.setScale(2, 3, 0.5f);

        Vector3 scale = node.getScale();
        assertEquals(2, scale.x, 0.001f);
        assertEquals(3, scale.y, 0.001f);
        assertEquals(0.5f, scale.z, 0.001f);
    }
}
