#!/usr/bin/env -S npx tsx

import esbuild, { build } from 'esbuild';
import { backendConfig } from './build-common.mjs';

console.log('esbuild config:', backendConfig);
await esbuild.build(backendConfig).catch(() => process.exit(1));

process.exit(0);
