package com.aman.abandonedhouse.lwjgl3;

/**
 * Minimal startup helper. The game can run directly on the current JVM.
 */
public final class StartupHelper {
    private StartupHelper() {}

    public static boolean startNewJvmIfRequired() {
        return false;
    }
}
