#!/usr/bin/env -S npx tsx

import esbuild from "esbuild";
import { cjsConfig } from "./build-common.mjs";
import { console, process } from "node:global";

export const backendConfig = {
  ...cjsConfig,
  external: ["deno", "zx", "pg-hstore", "morgan", "express", "express-async-errors", "express-jwt", "jsonwebtoken",
    "bcryptjs", "cors", "helmet", "cookie-parser", "cookie-signature","node:*","fsevents", "config"],
  format: "esm",
  // https://github.com/evanw/esbuild/issues/1921#issuecomment-1152991694
  banner: {
    js: "import { createRequire } from 'module';const require = createRequire(import.meta.url);",
  },
  entryPoints: ["apps/watchyourlan-api/src/main.mts"],
  outfile: "dist/apps/watchyourlan-api/main.mjs",
  tsconfig: "apps/watchyourlan-api/tsconfig.app.json",
};

console.log("esbuild config:", backendConfig);
await esbuild.build(backendConfig).catch(() => process.exit(1));

process.exit(0);
