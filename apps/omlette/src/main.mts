import { $, argv, cd, os, fs, ProcessOutput, syncProcessCwd } from "zx";
import config from "config";
import path from "node:path";
import { fsPromises } from "node:fs";
import {xdgConfig} from "../../../libs/shared/zx-utils/src/index.mjs";

const xdgConfigHome = xdgConfig || path.join(process.env.HOME || "", ".config");
const configFilePath = path.join(xdgConfigHome, "omlette", "gomplate.yaml");
const assetsConfigFilePath = path.join("assets", "gomplate.yaml");

async function ensureConfigFile() {
  try {
    await fsPromises.access(configFilePath);
    console.log(`Config file found at ${configFilePath}`);
  } catch (error) {
    console.log(`Config file not found at ${configFilePath}, copying from assets`);
    await fsPromises.mkdir(path.dirname(configFilePath), { recursive: true });
    await fsPromises.copyFile(assetsConfigFilePath, configFilePath);
    console.log(`Config file copied to ${configFilePath}`);
  }
}
