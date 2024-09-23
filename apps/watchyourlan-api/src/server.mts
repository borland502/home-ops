/**
 * Setup express server.
 */

// import helmet from 'helmet';
import express, { Request, Response } from "express";
import { IncomingHttpHeaders } from "node:http2"
import { homeopsConfig } from "@technohouser/zx-utils";
import cors from "cors";
import { HttpStatusCodes } from "@technohouser/utils";
import { checkAndSyncTable } from "./init.mts";
import { BaseRouter, Paths, RouteError } from "@technohouser/watchyourlan";

// **** Types **** //
export type RemoveIndexSignature<T> = {
  [K in keyof T as string extends K ? never : number extends K ? never : K]: T[K];
}

export type HttpDefaultHeaders = RemoveIndexSignature<IncomingHttpHeaders>;

export interface HttpReqHeaders extends HttpDefaultHeaders {

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

app.get('/', (req: Request, res: Response) => {
  res.send({"message": 'Hello World!'});
});

// TODO: Hook app side up to the library
// Add APIs, must be after middleware
// app.use(Paths.CTX.Base, BaseRouter.apiRouter);
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
