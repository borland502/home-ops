/**
 * This is not a production server yet!
 * This is only a minimal backend to get started.
 */

import type { Request, Response } from "express";
import express from "express";
import { info } from "@technohouser/log";
import config from "config";
import { toInt } from "radash";

export const app = express();
const portNum = toInt(config.get("express.port"));

app.get("/", (_: Request, res: Response) => {
  res.send('Hello World!')
});

const port = portNum || 3333;
export const server = app.listen(port, () => {
  info(`Listening at http://localhost:${port}`);
});
server.on("error", console.error);
