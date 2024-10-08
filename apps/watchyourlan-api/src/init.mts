#!/usr/bin/env -S npx tsx --tsconfig ./scripts/tsconfig.app.json

import {
  fs, homeopsConfig,
  path,
} from "@technohouser/zx-utils";
import { error } from "@technohouser/log";
import {xdgState} from "@technohouser/utils";
import {Sequelize} from "sequelize-typescript";
import {Host} from "@technohouser/watchyourlan";
import process from "process"

const watchYourLanDb = path.join(
  xdgState,
  homeopsConfig.get("watchyourlan.db.dbFileName")
);

const sequelize = new Sequelize({
	dialect: "sqlite",
  storage: `${watchYourLanDb}`,
	models: [Host],
});

export async function checkAndSyncTable() {
  // if the db doesn't already exist, then create it
  fs.ensureFileSync(watchYourLanDb);
  // Check if the table exists
  const tableExists = await sequelize
    .getQueryInterface()
    .showAllTables()
    .then((tables) => tables.includes("now"));
  if (!tableExists) {
    error(`Table 'now' does not exist at path ${watchYourLanDb}.  Exiting.`);
    process.exit(1);
  }
}
