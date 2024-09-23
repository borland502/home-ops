#!/usr/bin/env -S npx tsx

import esbuild from "esbuild";
import { cjsConfig } from "./build-common.mjs";

export const utilsConfig = {
  ...cjsConfig,
  entryPoints: ["libs/shared/utils/src/index.mts"],
  outfile: "dist/libs/shared/utils/index.cjs",
  tsconfig: "libs/shared/utils/tsconfig.lib.json",
};

console.log("esbuild config:", utilsConfig);
await esbuild.build(utilsConfig).catch(() => process.exit(1));

process.exit(0);
