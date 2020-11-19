import log from "loglevel";

const merge = require('deepmerge');

export interface Configuration {
    apiUrl: string
}

const CONFIG_FILE = "/config.json";
const DEFAULT_CONFIGURATION: Configuration = {
    apiUrl: window.location.origin + "/api"
};

export class ConfigUtils {
    static async readConfiguration(): Promise<Configuration> {
        try {
            const response = await fetch(CONFIG_FILE);
            const configuration: Configuration = await response.json()

            log.debug("[Configuration] Configuration File Loaded");

            return merge(DEFAULT_CONFIGURATION, configuration);
        } catch (e) {
            log.debug("[Configuration] Default Configuration Loaded");

            return DEFAULT_CONFIGURATION;
        }
    }
}
