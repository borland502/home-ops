import type {SystemInformation} from "./lib/pkg-install.mjs";
import type * as zx from "zx";
import * as Process from "node:process";
export * from "./lib/utils.mjs";
export { HttpStatusCodes } from "./lib/common/HttpStatusCodes.mjs";
export * from "./lib/common/XdgPaths.mjs";
export * from "./lib/log.mjs";
export * from "./lib/zx-utils.mjs"


declare module "@technohouser/zx-utils" {}
