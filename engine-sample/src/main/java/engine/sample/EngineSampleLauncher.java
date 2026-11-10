package engine.sample;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import engine.core.Engine;
import engine.core.EngineConfig;
import engine.core.impl.DefaultEngine;

/**
 * Desktop sample demonstrating engine-core and engine-scene usage.
 * <p>
 * This sample shows:
 * - Engine initialization with configuration
 * - Engine lifecycle integration with LibGDX
 * - Scene creation and activation
 * - Scene graph hierarchy with transforms
 * - Entity-component system
 * - Scene switching and cleanup
 * - At least one update tick
 * - Resize handling
 * - Pause/resume handling
 * - Clean shutdown
 * </p>
 */
public class EngineSampleLauncher {

    public static void main(String[] args) {
        // Create engine configuration
        EngineConfig config = EngineConfig.builder()
            .title("Engine Core Sample")
            .initialWidth(800)
            .initialHeight(600)
            .useVSync(true)
            .targetFps(60)
            .enableLogging(true)
            .build();

        // Create and initialize the engine
        Engine engine = new DefaultEngine();
        engine.initialize(config);

        // Create LibGDX application configuration
        Lwjgl3ApplicationConfiguration lwjglConfig = new Lwjgl3ApplicationConfiguration();
        lwjglConfig.setTitle(config.getTitle());
        lwjglConfig.setWindowedMode(config.getInitialWidth(), config.getInitialHeight());
        lwjglConfig.useVsync(config.isUseVSync());
        lwjglConfig.setForegroundFPS(config.getTargetFps());
        lwjglConfig.setIdleFPS(config.getTargetFps());
        lwjglConfig.setResizable(true);

        // Create LibGDX application with engine lifecycle adapter
        new Lwjgl3Application(new EngineSampleApplication(engine), lwjglConfig);
    }
}
