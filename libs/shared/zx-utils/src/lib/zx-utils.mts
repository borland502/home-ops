import * as logger from "@technohouser/log";
import { isString } from "radash";
import * as zx from "zx";

// type ZxCommonOptions = Pick<Options, 'shell' | 'verbose' | 'nothrow' | 'log'>;

const whichOptions = {
  path: "/bin:/usr/bin:/usr/local/bin",
  nothrow: true,
};

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
