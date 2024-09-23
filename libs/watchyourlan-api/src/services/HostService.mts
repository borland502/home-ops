import { getAll, Host } from "@technohouser/watchyourlan";
import { error } from "@technohouser/log";

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
  return await getAll().then(
    (res: Host[]) => handleSuccess(res),
    (reason: Error) => handleFailure(reason)
  );
}
