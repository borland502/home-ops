#!/usr/bin/env -S npx tsx

import esbuild from "esbuild";
import { zxUtilsConfig } from "./build-common.mjs";

console.log("esbuild config:", zxUtilsConfig);
await esbuild.build(zxUtilsConfig).catch(() => process.exit(1));

process.exit(0);
