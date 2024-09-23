#!/usr/bin/env -S npx tsx

import esbuild from "esbuild";
import { cjsConfig } from "./build-common.mjs";

export const backendConfig = {
  ...cjsConfig,
  entryPoints: ["apps/backend/src/main.mts"],
  outfile: "dist/apps/backend/main.cjs",
  tsconfig: "apps/backend/tsconfig.app.json",
};


console.log("esbuild config:", backendConfig);
await esbuild.build(backendConfig).catch(() => process.exit(1));

process.exit(0);
