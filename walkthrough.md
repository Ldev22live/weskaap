# Weskaap M2 Walkthrough

## Overview

M2 adds a lightweight collision foundation to the M1 prototype. The world now contains four deterministic static rectangular obstacles. The Hero keeps its existing controls, normalized diagonal speed, world boundaries, and camera following while gaining collision bounds and axis-separated obstacle resolution.

## Architecture

### Hero

`Hero` owns its center position, movement speed, visual size, and a reusable rectangular collision boundary. The collision rectangle is slightly smaller than the primitive visual square and is updated whenever the Hero position changes.

### HeroController

`HeroController` reads WASD and arrow-key input and returns a normalized, delta-scaled movement vector. It does not apply movement directly, allowing the world collision system to resolve the requested movement first.

### CollisionResolver

`CollisionResolver` applies movement one axis at a time:

1. Clamp the requested X position to the world width.
2. Check obstacles whose vertical range overlaps the Hero.
3. Stop the Hero at the nearest crossed obstacle edge.
4. Apply the resolved X position.
5. Repeat the process for Y using the updated X position.

Resolving X and Y independently allows the Hero to continue moving along one axis when the other axis is blocked, producing wall sliding. Edge-crossing checks also prevent large frame movements from skipping completely through an obstacle.

### GameWorld

`GameWorld` owns the Hero, controller, obstacle list, collision update flow, and primitive rendering. Obstacle creation is isolated in `createObstacles()` so a future world-data source can replace the hard-coded layout without changing collision resolution.

### MainGameScreen

`MainGameScreen` remains responsible for lifecycle and camera behavior. Its M1 camera follow and world clamping continue unchanged.

## Controls

- `W` or `Up`: move up
- `S` or `Down`: move down
- `A` or `Left`: move left
- `D` or `Right`: move right
- Two directional keys: normalized diagonal movement

## Manual Verification

1. Run `.\gradlew.bat lwjgl3:run` from the project root.
2. Confirm the gold Hero and multiple brown rectangular obstacles are visible.
3. Move with both WASD and arrow keys.
4. Approach each side of an obstacle and confirm the Hero stops at its edge.
5. Hold two movement keys while approaching a wall diagonally and confirm the Hero slides along it.
6. Travel toward each world edge and confirm the Hero remains inside the world.
7. Confirm the camera follows the Hero and stops at the world edges.
8. Close the game window and check the terminal for runtime exceptions.

## Automated Verification

The project was verified with:

```powershell
.\gradlew.bat build
git diff --check
```

The desktop target was also launched with `.\gradlew.bat lwjgl3:run` and remained running without runtime exceptions. No test framework is currently configured, so no new testing dependency was introduced solely for this milestone.

## Scope

M2 uses LibGDX `Rectangle` geometry and existing primitive rendering only. It does not add Box2D, Tiled, external assets, combat, enemies, items, NPCs, audio, persistence, networking, or later-milestone systems.
