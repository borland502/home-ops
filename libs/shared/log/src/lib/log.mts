import * as zx from "zx";
import config from "config";

export const debug = (msg: string) =>
  console.debug(zx.chalk.hex(config.get("colors.blue"))(msg));
export const info = (msg: string) =>
  console.info(zx.chalk.hex(config.get("colors.purple"))(msg));
export const warn = (msg: string) =>
  console.warn(zx.chalk.hex(config.get("colors.orange"))(msg));
export const error = (msg: string) =>
  console.error(zx.chalk.hex(config.get("colors.red"))(msg));
export const trace = (msg: string) =>
  console.trace(zx.chalk.hex(config.get("colors.yellow"))(msg));
