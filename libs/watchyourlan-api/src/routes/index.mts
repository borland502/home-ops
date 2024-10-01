import { Router } from "express";
import { findAllHosts, Paths } from "@technohouser/watchyourlan";

// **** Variables **** //

export const apiRouter = Router();

// ** Add UserRouter ** //

// Init router
const hostRouter = Router();

// Get all hosts
hostRouter.get(Paths.default.Hosts.Get, findAllHosts);

// Add HostRouter
apiRouter.use(Paths.default.Hosts.Base, hostRouter);
