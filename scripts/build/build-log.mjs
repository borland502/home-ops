#!/usr/bin/env -S npx tsx

import esbuild from "esbuild";
import { cjsConfig } from "./build-common.mjs";

export const logConfig = {
  ...cjsConfig,
  entryPoints: ["libs/shared/log/src/index.mts"],
  outfile: "dist/libs/shared/log/index.cjs",
  tsconfig: "libs/shared/log/tsconfig.lib.json",
};

console.log("esbuild config:", logConfig);
await esbuild.build(logConfig).catch(() => process.exit(1));

process.exit(0);
