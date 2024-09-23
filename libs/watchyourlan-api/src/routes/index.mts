import { Router } from "express";
import { HostRoutes, Paths } from "@technohouser/watchyourlan";

// **** Variables **** //

const apiRouter = Router();

// ** Add UserRouter ** //

// Init router
const hostRouter = Router();

// Get all hosts
hostRouter.get(Paths.default.Hosts.Get, HostRoutes.getAll);

// Add HostRouter
apiRouter.use(Paths.default.Hosts.Base, hostRouter);

// **** Export default **** //

export default apiRouter;
