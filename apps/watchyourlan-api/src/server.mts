/**
 * Setup express server.
 */


import type { Request, Response } from "express";
import express from "express"
import { homeopsConfig } from "@technohouser/zx-utils";
import cors from "cors";
import { HttpStatusCodes } from "@technohouser/utils";
import { checkAndSyncTable } from "./init.mjs";
import { BaseRouter, Paths, RouteError } from "@technohouser/watchyourlan";

// **** Types **** //
export type RemoveIndexSignature<T> = {
  [K in keyof T as string extends K ? never : number extends K ? never : K]: T[K];
}

const origin: string = homeopsConfig.get("express.headers.origin");

// **** Variables **** //
const corsOptions = {
  origin: origin,
  optionsSuccessStatus: 200,
  methods: "GET",
  allowedHeaders: ["Content-Range"],
  preflightContinue: true,
};

const app = express();

app.use(cors(corsOptions));

// **** Setup **** //
await checkAndSyncTable();

// Basic middleware
app.use("/", express.json());
app.use("/", express.urlencoded({ extended: true }));

// Add APIs, must be after middleware
app.use(Paths.default.Base, BaseRouter.apiRouter);

// Add error handler
app.use((err: RouteError, _: Request, res: Response, next: () => void) => {
  let status = HttpStatusCodes.BAD_REQUEST;
  status = err.status;
  next();
  return res.status(status).json({ error: err.message });
});

// **** Export default **** //

export default app;
