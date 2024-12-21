/**
 * Setup express server.
 */

import type {Express, Request, Response} from "express";
import express from "express"
import { homeopsConfig, HttpStatusCodes } from "@technohouser/shared";
import cors from "cors";
import { checkAndSyncTable } from "./init.mjs";
import { BaseRouter, Paths, RouteError } from "@technohouser/watchyourlan-api-lib";

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

const app: Express = express();

app.use(cors(corsOptions));

// **** Setup **** //
await checkAndSyncTable();

// Basic middleware
app.use("/", express.json());
app.use("/", express.urlencoded({ extended: true }));

// Add APIs, must be after middleware
app.use(Paths.default.Base, BaseRouter.apiRouter);

// Add error handler
const errorHandler = (err: RouteError, _: Request, res: Response, next: () => void) => {
    const status: HttpStatusCodes = err.status;
    next();
    return res.status(status).json({error: err.message});
}

app.use((err: RouteError, req: Request, res: Response, next: () => void) => {
    errorHandler(err, req, res, next);
});

// **** Export default **** //

export default app;
