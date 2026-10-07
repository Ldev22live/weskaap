package com.weskaap.game.building;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BuildingTest {

    private Building createTestHouse() {
        return new Building(
            "test_house", "test_house_interior",
            800f, 500f, 200f, 200f, 120f,
            400f, 300f,
            200f, 80f,
            200f, 240f
        );
    }

    @Test
    void buildingHasIdAndInteriorAndEntrance() {
        Building building = createTestHouse();
        assertEquals("test_house", building.getId());
        assertNotNull(building.getInterior());
        assertNotNull(building.getEntrance());
        assertEquals("test_house_interior", building.getInterior().getId());
        assertEquals(200f, building.getWidth());
        assertEquals(200f, building.getDepth());
        assertEquals(120f, building.getHeight());
    }

    @Test
    void entranceIsDerivedFromFootprintAndInFrontOfWall() {
        Building building = createTestHouse();
        BuildingEntrance entrance = building.getEntrance();
        float expectedX = building.getPosition().x + building.getWidth() / 2f;
        float expectedY = building.getPosition().y - Building.WALL_THICKNESS;
        assertEquals(expectedX, entrance.getInteractionPosition().x);
        assertEquals(expectedY, entrance.getInteractionPosition().y);
        assertEquals("Enter", entrance.getPromptText());
    }

    @Test
    void wallsContainDoorwayOpening() {
        Building building = createTestHouse();
        float doorLeft = building.getEntrance().getInteractionPosition().x - BuildingEntrance.SIZE / 2f;
        float doorRight = building.getEntrance().getInteractionPosition().x + BuildingEntrance.SIZE / 2f;
        boolean hasFrontWallLeft = false;
        boolean hasFrontWallRight = false;
        for (Rectangle wall : building.getWallObstacles()) {
            if (wall.y == building.getPosition().y && wall.x == building.getPosition().x
                && wall.width == doorLeft - building.getPosition().x) {
                hasFrontWallLeft = true;
            }
            if (wall.y == building.getPosition().y && wall.x == doorRight
                && wall.width == building.getPosition().x + building.getWidth() - doorRight) {
                hasFrontWallRight = true;
            }
        }
        assertTrue(hasFrontWallLeft);
        assertTrue(hasFrontWallRight);
    }

    @Test
    void interiorContainsWallsAndExit() {
        Building building = createTestHouse();
        Interior interior = building.getInterior();
        assertEquals(4, interior.getObstacles().size());
        assertEquals(1, interior.getInteractables().size());
        assertNotNull(interior.getExit());
        assertEquals("Exit", interior.getExit().getPromptText());
    }

    @Test
    void entrancePromptAndRange() {
        Building building = createTestHouse();
        BuildingEntrance entrance = building.getEntrance();
        assertEquals(72f, entrance.getInteractionRange());
    }

    @Test
    void exteriorReturnPositionIsDerivedFromEntrance() {
        Building building = createTestHouse();
        BuildingEntrance entrance = building.getEntrance();
        Vector2 expected = new Vector2(entrance.getInteractionPosition().x,
            entrance.getInteractionPosition().y - Building.DOOR_RETURN_OFFSET);
        assertEquals(expected, building.getExteriorReturnPosition());
    }
}
