#!/usr/bin/env -S npx tsx --tsconfig ./scripts/tsconfig.app.json

import {$, globby, systemInfo, homeopsConfig, path, fs} from "@technohouser/zx-utils"
import {warn, error} from "@technohouser/log"
import {xdgData} from "xdg-basedir";
import sqlite3 from "sqlite3";


// **** Variables **** //
const entryPoints = (await globby(["scripts/**/*.mts", "scripts/**/*.mjs", "!scripts/node_modules/**/*"]));
const watchYourLanDb = path.join(xdgData, homeopsConfig.get("watchyourlan.db.dbFileName"));

fs.ensureFileSync(watchYourLanDb);

const db = new sqlite3.Database(watchYourLanDb, (err) => {
  if (err) {
    error(`Error opening database ${err}`)
  }
});

db.all("select * from now", (err, rows) => {
  if (err) {
    error(`Error querying database ${err}`)
  }
  for (const row in rows) {
    warn(`${row}`)
  }
})


// TODO: Demo code, remove and create entry point for cli

for (const entryPoint of entryPoints) {
  warn(`${entryPoint}`)
}

warn(`${watchYourLanDb}`)

// **** Functions **** //


