/**
 * Setup express server.
 */

import cookieParser from "cookie-parser";
import morgan from "morgan";
// import helmet from 'helmet';
import express, { Request, Response } from "express";
import { homeopsConfig } from "@technohouser/zx-utils"
import cors from "cors";
import { Paths, BaseRouter, RouteError } from "@technohouser/watchyourlan";
import { HttpStatusCodes } from "@technohouser/utils";
import { checkAndSyncTable } from "./init.mts";

// **** Variables **** //
const expressConfig = homeopsConfig.get("express");
const expressHeaders = expressConfig["headers"];

const corsOptions = {
  origin: expressHeaders.origin,
  optionsSuccessStatus: 200,
  methods: "GET",
  allowedHeaders: ["Content-Range"],
  preflightContinue: true,
};

const app = express();

app.use(cors(corsOptions));

// **** Setup **** //
await checkAndSyncTable()

// Basic middleware
app.use("/", express.json());
app.use("/", express.urlencoded({ extended: true }));
app.use("/", morgan("dev"));
app.use("/", cookieParser("xxxxxxxxxxxxxx"));

// // Add APIs, must be after middleware
// app.use(Paths.default.Base, BaseRouter.default);
//
// // Add error handler
// app.use((err: RouteError, _: Request, res: Response) => {
//   let status = HttpStatusCodes.BAD_REQUEST;
//   if (err instanceof RouteError) {
//     status = err.status;
//   }
//   return res.status(status).json({ error: err.message });
// });

// **** Export default **** //

export default app;
