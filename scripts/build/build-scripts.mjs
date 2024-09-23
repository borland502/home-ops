#!/usr/bin/env -S npx tsx

import {$, globby} from "zx";
import esbuild from "esbuild";
import wildcardImports from "esbuild-plugin-wildcard-imports";

const entryPoints = (await globby(["scripts/**/*.mts", "scripts/**/*.mjs", "!scripts/node_modules/**/*"]));

const plugins = [wildcardImports()]

// permit the output to be a pure typescript esm file (mts)
export const scriptConfig = {
  external: ["deno","pg-hstore","node:*","fsevents","zx","config"],
  bundle: true,
  minify: false,
  sourcemap: false,
  sourcesContent: false,
  platform: "node",
  target: "ESNext",
  format: "esm",
  entryPoints: entryPoints,
  outdir: "node_modules/.bin",
  outExtension: { ".js": ".mts" },
  // https://github.com/evanw/esbuild/issues/1921#issuecomment-1152991694
  banner: {
    js: "import { createRequire } from 'module';const require = createRequire(import.meta.url);",
  },
  tsconfig: "scripts/tsconfig.app.json",
  plugins: plugins
};

console.log("esbuild config:", scriptConfig);
await esbuild.build(scriptConfig).catch(() => process.exit(1));

process.exit(0);
