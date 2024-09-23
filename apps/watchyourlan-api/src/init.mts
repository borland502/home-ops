#!/usr/bin/env -S npx tsx --tsconfig ./scripts/tsconfig.app.json

import {$, globby, systemInfo, homeopsConfig, path, fs} from "@technohouser/zx-utils"
import {info, warn, error} from "@technohouser/log"
import {xdgData} from "xdg-basedir";
import { Sequelize, Model, DataTypes } from "sequelize";
import config from "config";
import {PathLike} from "fs";


// **** Variables **** //
export const entryPoints = (await globby(["scripts/**/*.mts", "scripts/**/*.mjs", "!scripts/node_modules/**/*"]));
export const watchYourLanDb = path.join(xdgData, homeopsConfig.get("watchyourlan.db.dbFileName"));

// Initialize Sequelize
export const sequelize = new Sequelize({
  dialect: "sqlite",
  storage: watchYourLanDb,
});

export async function checkAndSyncTable() {
  try {
    // if the db doesn't already exist, then create it
    fs.ensureFileSync(watchYourLanDb);

    // Check if the table exists
    const tableExists = await sequelize.getQueryInterface().showAllTables().then(tables => tables.includes("now"));

    if (!tableExists && config.get("env") === "dev") {
      // Sync the model if in development mode
      await sequelize.sync();
      info("Table 'now' has been created.");
    } else {
      info("Table 'now' already exists or not in development mode.");
    }
  } catch (err) {
    error(`Error: ${err.message}`);
  }
}







