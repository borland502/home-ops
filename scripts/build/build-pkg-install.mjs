#!/usr/bin/env -S npx tsx

import esbuild from "esbuild";
import { pkgInstallConfig } from "./build-common.mjs";

console.log("esbuild config:", pkgInstallConfig);
await esbuild.build(pkgInstallConfig).catch(() => process.exit(1));

process.exit(0);
