import { nxE2EPreset } from "@nx/playwright/preset";
import { PlaywrightTestConfig } from '@playwright/test';
import { URL } from "url";

const __filename = new URL(import.meta.url).pathname;

const config: PlaywrightTestConfig = {
  ...nxE2EPreset(__filename, { testDir: "./e2e" }),
  testDir: './tests',
  testMatch: '**/*.ts',
};

export default config;