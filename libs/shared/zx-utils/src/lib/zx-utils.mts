import * as logger from "./log.mjs";
import { isString, set } from "radash";
import * as zx from "zx";
import config from "config";
import {getSystemData, type SystemInformation} from "./pkg-install.mjs";

const whichOptions = {
  path: "/bin:/usr/bin:/usr/local/bin",
  nothrow: true,
};

export const systemInfo: SystemInformation = await getSystemData();
export const homeopsConfig = config;

/**
 * Override the default zx options with custom options and functions
 */
// Create proxy manually rather than dynamically, and shallowly rather than deep
const argv_opts: object = homeopsConfig.get("zx.minimist.opts");
export const argv = zx.minimist(process.argv.slice(2), argv_opts)
export const fs = zx.fs;
export const which = zx.which
export const minimist = zx.minimist;

  // export function detectShell(): Promise<string | boolean>;
  // export function getPkgMgr(): string;
  // export function askConfirmation(quest: string): Promise<boolean>;
  // export function getSystemData(): Promise<SystemInformation>;
  // export function hasCommand(cmd: string): Promise<boolean>;

  // export const path: typeof zx.path;
  // export const os: typeof zx.os;
  // export const yaml: typeof zx.YAML;
  //
  // // zx functions and augments
  // export function syncProcessCwd(flag?: boolean): void;
  // export function cd(dir: string): Promise<void>;
  // export function fetch(url: string, opts?: RequestInit): Promise<Response>;
  // export function question(quest: string): Promise<string>;
  // export function tmpfile(): Promise<string>;
  // export function quote(str: string): string;
  // export function sleep(ms: number): Promise<void>;
  // export function echo(str: string): void;
  // export function exit(code?: number): void;
  // export function stdin(): Promise<string>;
  // export function within(dir: string, fn: () => Promise<void>): Promise<void>;
  // export function spinner(text: string): typeof spinner;
  // export function syncProcessCwd(flag?: boolean): void;
  // export function retry<T>(fn: () => Promise<T>, opts?: {retries: number, delay: number}): Promise<T>;
  // export function glob(pattern: string): Promise<string[]>;
  // export function ps(): Promise<typeof Process[]>;
  // export function kill(pid: number, signal?: NodeJS.Signals): Promise<void>;
  // export function tmpdir(): string;

// override defaults
set(zx.$, "argv", argv);
set(zx.$, "verbose", homeopsConfig.get("zx.verbose") || zx.$.verbose);
set(zx.$, "nothrow", homeopsConfig.get("zx.nothrow") || zx.$.nothrow);
set(zx.$, "log", logProcessor);
set(
  zx.$,
  "shell",
  homeopsConfig.get("zx.shell") || await detectShell()
);

// export augmented zx functions and options
export const $ = zx.$;

async function detectShell(): Promise<string | boolean> {
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
