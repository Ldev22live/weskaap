package engine.core;

/**
 * Configuration for the engine.
 * <p>
 * This class is immutable. Use the Builder to create instances.
 * </p>
 */
public class EngineConfig {

    private final String title;
    private final int initialWidth;
    private final int initialHeight;
    private final boolean useVSync;
    private final int targetFps;
    private final boolean enableLogging;

    private EngineConfig(Builder builder) {
        this.title = builder.title;
        this.initialWidth = builder.initialWidth;
        this.initialHeight = builder.initialHeight;
        this.useVSync = builder.useVSync;
        this.targetFps = builder.targetFps;
        this.enableLogging = builder.enableLogging;
    }

    public String getTitle() {
        return title;
    }

    public int getInitialWidth() {
        return initialWidth;
    }

    public int getInitialHeight() {
        return initialHeight;
    }

    public boolean isUseVSync() {
        return useVSync;
    }

    public int getTargetFps() {
        return targetFps;
    }

    public boolean isEnableLogging() {
        return enableLogging;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String title = "Engine Application";
        private int initialWidth = 1280;
        private int initialHeight = 720;
        private boolean useVSync = true;
        private int targetFps = 60;
        private boolean enableLogging = true;

        public Builder title(String title) {
            if (title == null || title.trim().isEmpty()) {
                throw new IllegalArgumentException("Title cannot be null or empty");
            }
            this.title = title;
            return this;
        }

        public Builder initialWidth(int width) {
            if (width <= 0) {
                throw new IllegalArgumentException("Width must be positive");
            }
            this.initialWidth = width;
            return this;
        }

        public Builder initialHeight(int height) {
            if (height <= 0) {
                throw new IllegalArgumentException("Height must be positive");
            }
            this.initialHeight = height;
            return this;
        }

        public Builder useVSync(boolean useVSync) {
            this.useVSync = useVSync;
            return this;
        }

        public Builder targetFps(int fps) {
            if (fps <= 0) {
                throw new IllegalArgumentException("Target FPS must be positive");
            }
            this.targetFps = fps;
            return this;
        }

        public Builder enableLogging(boolean enableLogging) {
            this.enableLogging = enableLogging;
            return this;
        }

        public EngineConfig build() {
            return new EngineConfig(this);
        }
    }
}
