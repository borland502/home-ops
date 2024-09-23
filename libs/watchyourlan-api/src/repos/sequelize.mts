import { Sequelize } from "sequelize-typescript";

import { Host } from "@technohouser/watchyourlan";
import config from "config"
import { xdgState } from "xdg-basedir";

export const sequelize = new Sequelize({
	dialect: "sqlite",
  storage: `${xdgState.concat(config.get("database.dbFileName"))}`,
	models: [Host],
});
