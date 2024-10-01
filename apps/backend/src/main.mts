/**
 * This is not a production server yet!
 * This is only a minimal backend to get started.
 */

import express from "express";
import { info } from "@technohouser/log";
import type {Request, Response} from "express";
import config from "config";
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

app.get("/api", (req: Request, res: Response) => {
  res.send({ message: "Welcome to backend!" });
});

const port = portNum || 3333;
export const server = app.listen(port, () => {
  info(`Listening at http://localhost:${port}/api`);
});
server.on("error", console.error);
