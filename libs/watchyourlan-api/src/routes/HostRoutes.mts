import { HostService, Host } from "@technohouser/watchyourlan";
import type { Request, Response } from "express";
import { HttpStatusCodes } from "@technohouser/zx-utils";
import { get } from "radash";
import * as jsonapi from "jsonapi-serializer";

// **** Functions **** //

/**
 * transform to json-api format
 * @param res
 * @param hosts
 */
export function toJsonApi(res: Response, hosts: Host[]) {
  res.setHeader("Content-Type", "application/vnd.api+json");
  const hostSerializer = new jsonapi.Serializer("hosts", {
    attributes: ["ID", "NAME", "IP", "MAC", "HW", "DATE", "KNOWN", "NOW"],
  });

  return res.status(HttpStatusCodes.OK).json(hostSerializer.serialize(hosts));
}

/**
 * Get all users.
 */
export async function getAll(_: Request, res: Response) {
  return await HostService.findAllHosts()
    .then((hosts: Host[] | Error) => {
      if (hosts !== undefined && get(hosts, "length", 0) <= 0) {
        console.log("No hosts found");
        return res.status(HttpStatusCodes.NO_CONTENT).json({});
      } else if (hosts instanceof Error) {
        return res.status(HttpStatusCodes.INTERNAL_SERVER_ERROR).json({});
      }

      return toJsonApi(res, hosts);
    })
    .catch((reason) => {
      console.error(`Could not recover from error: ${reason}`);
      return res.status(HttpStatusCodes.INTERNAL_SERVER_ERROR).json({});
    });
}
