import { Router } from "express";
import { getAll, Paths} from "@technohouser/watchyourlan";

// **** Variables **** //

export const apiRouter = Router();

// ** Add UserRouter ** //

// Init router
const hostRouter = Router();

// Get all hosts
hostRouter.get(Paths.default.Hosts.Get, getAll);

// Add HostRouter
apiRouter.use(Paths.default.Hosts.Base, hostRouter);
