#!/usr/bin/env -S npx tsx

import esbuild from "esbuild";
import { cjsConfig } from "./build-common.mjs";

export const pkgInstallConfig = {
  ...cjsConfig,
  entryPoints: ["libs/shared/pkg-install/src/index.mts"],
  outfile: "dist/libs/shared/pkg-install/index.cjs",
  tsconfig: "libs/shared/pkg-install/tsconfig.lib.json",
};

console.log("esbuild config:", pkgInstallConfig);
await esbuild.build(pkgInstallConfig).catch(() => process.exit(1));

process.exit(0);
