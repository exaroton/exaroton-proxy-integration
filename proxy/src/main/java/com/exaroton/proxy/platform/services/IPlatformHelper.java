package com.exaroton.proxy.platform.services;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.file.GenericBuilder;

/**
 * A helper fetching information about the Platform.
 */
public interface IPlatformHelper {
    /**
     * Gets the name of the current platform
     *
     * @return The name of the current platform.
     */
    String getPlatformName();

    /**
     * Get the version of the exaroton plugin.
     *
     * @return The version of the mod.
     */
    String getPluginVersion();

    /**
     * Gets the configuration file for the plugin/mod.
     *
     * @return The configuration file.
     */
    GenericBuilder<Config, FileConfig> getConfig();
}
