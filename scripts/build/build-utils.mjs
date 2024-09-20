#!/usr/bin/env -S npx tsx

import esbuild from 'esbuild';
import { utilsConfig } from './build-common.mjs';

console.log('esbuild config:', utilsConfig);
await esbuild.build(utilsConfig).catch(() => process.exit(1));

process.exit(0);
