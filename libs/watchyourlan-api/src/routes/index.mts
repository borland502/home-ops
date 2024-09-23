import { Router } from "express";
import { HostRoutes, Paths } from "@technohouser/watchyourlan";

// **** Variables **** //

export const apiRouter = Router();

// ** Add UserRouter ** //

// Init router
const hostRouter = Router();

// Get all hosts
hostRouter.get(Paths.CTX.Hosts.Get, HostRoutes.getAll);

// Add HostRouter
apiRouter.use(Paths.CTX.Hosts.Base, hostRouter);
