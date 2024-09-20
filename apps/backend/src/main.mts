/**
 * This is not a production server yet!
 * This is only a minimal backend to get started.
 */

import express from "express";
import { info } from "@technohouser/log";
import config from "config";
import * as core from "express-serve-static-core";
import { toInt } from "radash";
import { fileURLToPath } from "url";
import path from "path";

let fileName: string;
let dirName: string;

if (typeof fileName === "undefined" || typeof dirName === "undefined") {
  fileName = fileURLToPath(import.meta.url);
  dirName = path.dirname(fileName);
}

export { fileName, dirName };

export const app = express();
const portNum = toInt(config.get("express.port"));
const assetsFolder: core.PathParams = config.get("express.assetsFolder");
const apiBase: core.PathParams = config.get("express.apiBase");

// @ts-expect-error The types do not align and yet this is the supported syntax
app.use(assetsFolder, express.static(path.join(dirName, "assets")));

app.get(apiBase, (req, res) => {
  res.send({ message: "Welcome to backend!" });
});

const port = portNum || 3333;
export const server = app.listen(port, () => {
  info(`Listening at http://localhost:${port}/api`);
});
server.on("error", console.error);
