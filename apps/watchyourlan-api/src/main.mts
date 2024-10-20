import { info } from "@technohouser/zx-utils";
import app from "./server.mjs";
import { createServer } from "http";

// **** Run **** //

const SERVER_START_MSG = "Express server started on port: 3001";
void (() => {
   createServer(app).listen(3001, () => info(SERVER_START_MSG));
})();
