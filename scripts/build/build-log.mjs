#!/usr/bin/env -S npx tsx

import esbuild from "esbuild";
import { logConfig } from "./build-common.mjs";

console.log("esbuild config:", logConfig);
await esbuild.build(logConfig).catch(() => process.exit(1));

process.exit(0);
