import {Router} from "express";
import {getAll, Paths} from "../index.mjs";

// **** Variables **** //

export const apiRouter: Router = Router();

const hostRouter = Router();

/**
 * @swagger
 * /hosts:
 *  get:
 *  description: Get all hosts.
 *  responses:
 *  200:
 *  description: OK
 *  204:
 *  description: No Content
 *  500:
 *  description: Internal Server Error
 *
 */
hostRouter.get(Paths.default.Hosts.Get, (req, res, next) => {
  getAll(req, res).catch(next);
});

// Add HostRouter
apiRouter.use(Paths.default.Hosts.Base, hostRouter);
