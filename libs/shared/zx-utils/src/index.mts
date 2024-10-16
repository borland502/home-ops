import { logProcessor, detectShell } from "./lib/zx-utils.mjs";
import { getSystemData } from "@technohouser/pkg-install";
import type { SystemInformation } from "@technohouser/pkg-install";
import { $ } from "zx";
import config from "config";
import { set } from "radash";
import minimist from "minimist";

export * from 'zx';

/**
 * Override the default zx options with custom options and functions
 */
// const zxConfig: Partial<Options> = config.get("zx");

export const homeopsConfig = config;

const argv_opts: object = homeopsConfig.get("zx.minimist.opts");

const argv = minimist(process.argv.slice(2), argv_opts)

// Create proxy manually rather than dynamically, and shallowly rather than deep
set($, "argv", argv);
set($, "verbose", homeopsConfig.get("zx.verbose") || $.verbose);
set($, "nothrow", homeopsConfig.get("zx.nothrow") || $.nothrow);
set($, "log", logProcessor);
set(
  $,
  "shell",
  homeopsConfig.get("zx.shell") || await detectShell()
);

export const systemInfo: SystemInformation = await getSystemData();

declare module "@technohouser/zx-utils" {}
