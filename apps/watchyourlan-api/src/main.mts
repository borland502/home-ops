import app from "./server.mjs";
import { createServer } from "http";

// **** Run **** //

const SERVER_START_MSG = "Express server started on port: 3000";
(async () => {
  // TODO: Detect read only fs access
  // await sequelize.sync();
  createServer(app).listen(3001, () => console.info(SERVER_START_MSG));
})();
