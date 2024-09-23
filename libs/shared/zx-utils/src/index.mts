import { logProcessor, detectShell } from "./lib/zx-utils.mts";
import { getSystemData } from "@technohouser/pkg-install";
import type { SystemInformation } from "@technohouser/pkg-install";
import { $ as _$, Options } from "zx";
import { DeepProxy } from "@qiwi/deep-proxy";
import config from "config";
import { set } from "radash";

export * from "zx";

/**
 * Override the default zx options with custom options and functions
 */
const zxConfig: Partial<Options> = config.get("zx");
export const homeopsConfig = config;

export const $ = new DeepProxy(
  _$,
  ({ name, DEFAULT, target: t, trapName, args }) => {
    return DEFAULT;
  }
);

// set the zx variable to values in zxConfig, or framework defaults
set($, "verbose", zxConfig.verbose || $.verbose);
set($, "nothrow", zxConfig.nothrow || $.nothrow);
set(
  $,
  "shell",
  zxConfig.shell ? zxConfig.shell : (await detectShell()) || $.shell
);
set($, "log", logProcessor);

const systemInfoData: SystemInformation = await getSystemData();

export const systemInfo = systemInfoData;
