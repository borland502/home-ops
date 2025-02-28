import * as logger from "./log.mts"; // ensure this line uses .mts, not .mjs
import { isString } from "radash";
import * as zx from "zx";
import config from "config";
import * as process from "node:process";
import { exit } from "node:process";
import { $ } from "bun";

const whichOptions = {
    path: "/bin:/usr/bin:/usr/local/bin",
    nothrow: true,
};

export const homeopsConfig = config;

export async function initShell($$: zx.Shell & zx.Options) {
    // override defaults
    $$.shell = homeopsConfig.get("zx.shell") || (await detectShell());
    $$.verbose = homeopsConfig.get("zx.verbose") || zx.$.verbose;
    $$.nothrow = homeopsConfig.get("zx.nothrow") || zx.$.nothrow;
    $$.log = logProcessor;
}

export * from "zx";

export async function detectShell(): Promise<string | true> {
    const shells = await Promise.all([
        zx.which("zsh", whichOptions),
        zx.which("bash", whichOptions),
        zx.which("sh", whichOptions),
    ]);

    // set the shell in order of preference [zsh -> bash -> sh]
    for (const shell of shells) {
        const isShell = shell;
        if (isString(isShell)) {
            return isShell;
        }
    }

    exit(2);
}

/**
 * Zx script to add color based on output type
 * @param entry one of the following:
 *
 * stdout
 * stderr
 * cmd
 * fetch
 * cd
 * custom
 * retry
 */
export function logProcessor(entry: zx.LogEntry) {
    switch (entry.kind) {
        case "stdout":
            logger.info(entry.data.toString());
            break;
        case "stderr":
            logger.error(entry.data.toString());
            break;
        case "cmd":
            logger.info(`Running command: ${entry.cmd}`);
            break;
        case "fetch":
            logger.info(`Fetching ${String(entry.url)} through ${entry.init?.method}`);
            break;
        case "cd":
            logger.info(`Changing directory to ${entry.dir}`);
            break;
        case "custom":
            logger.warn(
                `executing custom zx function: ${isString(entry.data) ? entry.data : String(entry.data)}`,
            );
            break;
        case "retry":
            logger.info(`retrying due to ${entry.error}`);
    }
}
