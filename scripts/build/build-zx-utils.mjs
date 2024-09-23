#!/usr/bin/env -S npx tsx

import esbuild from "esbuild";
import { cjsConfig } from "./build-common.mjs";

/**
 * Override the cjsConfig to permit top level await and zx support
 *
 * @type {{entryPoints: string[], sourcemap: boolean, plugins: [{name: string, setup: function(*): void},Plugin,*,*], outfile: string, format: string, platform: string, target: string, minify: boolean, external: string[], tsconfig: string, sourcesContent: boolean, bundle: boolean, absWorkingDir: string}}
 */
export const zxUtilsConfig = {
  ...cjsConfig,
  target: "ESNext",
  format: "esm",
  entryPoints: ["libs/shared/zx-utils/src/index.mts"],
  outfile: "dist/libs/shared/zx-utils/index.mjs",
  tsconfig: "libs/shared/zx-utils/tsconfig.lib.json",
};

console.log("esbuild config:", zxUtilsConfig);
await esbuild.build(zxUtilsConfig).catch(() => process.exit(1));

process.exit(0);
