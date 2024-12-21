import { findAllHosts, Host } from "../index.mjs";
import { error } from "@technohouser/shared";

function handleSuccess(res: Host[]) {
  // transform to json-api format
  return res;
}

function handleFailure(reason: Error) {
  error(reason.message);
  return reason;
}

/**
 * Get all users.
 */
export async function getAllHosts(): Promise<Host[] | Error> {
  return await findAllHosts().then(
    (res: Host[]) => handleSuccess(res),
    (reason: Error) => handleFailure(reason)
  );
}
