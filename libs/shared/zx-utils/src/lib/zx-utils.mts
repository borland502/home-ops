import * as logger from "./log.mjs";
import { isString } from "radash";
import * as zx from "zx";
import config from "config";

const whichOptions = {
  path: "/bin:/usr/bin:/usr/local/bin",
  nothrow: true,
};

export const homeopsConfig = config;

/**
 * Override the default zx options with custom options and functions
 */
// Create proxy manually rather than dynamically, and shallowly rather than deep
// const argv_opts: object = homeopsConfig.get("zx.minimist.opts");
// export const argv = zx.minimist(process.argv.slice(2), argv_opts)
// export const fs = zx.fs;
//

// // override defaults
// zx.$.arguments = argv;
// zx.$.verbose = homeopsConfig.get("zx.verbose") || zx.$.verbose;
// zx.$.nothrow = homeopsConfig.get("zx.nothrow") || zx.$.nothrow;
// zx.$.log = logProcessor;
//
// // export augmented zx functions and options
//
// export const $ = zx.$;

export * from "zx";

export async function detectShell(): Promise<string | boolean> {
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

  return false;
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
function logProcessor(entry: zx.LogEntry) {
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
      logger.info(
        `Fetching ${String(entry.url)} through ${entry.init?.method}`
      );
      break;
    case "cd":
      logger.info(`Changing directory to ${entry.dir}`);
      break;
    case "custom":
      logger.warn(
        `executing custom zx function: ${isString(entry.data) ? entry.data : String(entry.data)}`
      );
      break;
    case "retry":
      logger.info(`retrying due to ${entry.error}`);
  }
}
