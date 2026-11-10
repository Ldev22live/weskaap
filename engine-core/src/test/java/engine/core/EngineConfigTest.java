package engine.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EngineConfigTest {

    @Test
    void defaultBuilderValues() {
        EngineConfig config = EngineConfig.builder().build();

        assertEquals("Engine Application", config.getTitle());
        assertEquals(1280, config.getInitialWidth());
        assertEquals(720, config.getInitialHeight());
        assertTrue(config.isUseVSync());
        assertEquals(60, config.getTargetFps());
        assertTrue(config.isEnableLogging());
    }

    @Test
    void customTitle() {
        EngineConfig config = EngineConfig.builder()
            .title("Custom Title")
            .build();

        assertEquals("Custom Title", config.getTitle());
    }

    @Test
    void customDimensions() {
        EngineConfig config = EngineConfig.builder()
            .initialWidth(1920)
            .initialHeight(1080)
            .build();

        assertEquals(1920, config.getInitialWidth());
        assertEquals(1080, config.getInitialHeight());
    }

    @Test
    void vSyncDisabled() {
        EngineConfig config = EngineConfig.builder()
            .useVSync(false)
            .build();

        assertFalse(config.isUseVSync());
    }

    @Test
    void customTargetFps() {
        EngineConfig config = EngineConfig.builder()
            .targetFps(144)
            .build();

        assertEquals(144, config.getTargetFps());
    }

    @Test
    void loggingDisabled() {
        EngineConfig config = EngineConfig.builder()
            .enableLogging(false)
            .build();

        assertFalse(config.isEnableLogging());
    }

    @Test
    void nullTitleThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
            EngineConfig.builder().title(null)
        );
    }

    @Test
    void emptyTitleThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
            EngineConfig.builder().title("")
        );
    }

    @Test
    void blankTitleThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
            EngineConfig.builder().title("   ")
        );
    }

    @Test
    void negativeWidthThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
            EngineConfig.builder().initialWidth(-100)
        );
    }

    @Test
    void zeroWidthThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
            EngineConfig.builder().initialWidth(0)
        );
    }

    @Test
    void negativeHeightThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
            EngineConfig.builder().initialHeight(-100)
        );
    }

    @Test
    void zeroHeightThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
            EngineConfig.builder().initialHeight(0)
        );
    }

    @Test
    void negativeTargetFpsThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
            EngineConfig.builder().targetFps(-60)
        );
    }

    @Test
    void zeroTargetFpsThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
            EngineConfig.builder().targetFps(0)
        );
    }

    @Test
    void builderCreatesImmutableConfig() {
        EngineConfig.Builder builder = EngineConfig.builder()
            .title("Original");

        EngineConfig config1 = builder.build();
        EngineConfig config2 = builder.title("Modified").build();

        assertEquals("Original", config1.getTitle());
        assertEquals("Modified", config2.getTitle());
    }

    @Test
    void fullCustomConfig() {
        EngineConfig config = EngineConfig.builder()
            .title("Full Config Test")
            .initialWidth(2560)
            .initialHeight(1440)
            .useVSync(false)
            .targetFps(120)
            .enableLogging(false)
            .build();

        assertEquals("Full Config Test", config.getTitle());
        assertEquals(2560, config.getInitialWidth());
        assertEquals(1440, config.getInitialHeight());
        assertFalse(config.isUseVSync());
        assertEquals(120, config.getTargetFps());
        assertFalse(config.isEnableLogging());
    }
}
