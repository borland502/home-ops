import {Host} from "@technohouser/watchyourlan";
import {DataTypes, QueryTypes} from "sequelize";
import {Sequelize} from "sequelize-typescript";
import {homeopsConfig, path} from "@technohouser/zx-utils";
import {xdgState} from "@technohouser/utils";

const watchYourLanDb = path.join(
  xdgState,
  homeopsConfig.get("watchyourlan.db.dbFileName")
);

const sequelize = new Sequelize({
  dialect: "sqlite",
  storage: `${watchYourLanDb}`,
  models: [Host],
});


/**
 * Adapt the watchyourlan table layout to the format expected by the React Admin front end.
 */
Host.init(
  {
    id: {
      type: new DataTypes.INTEGER(),
      primaryKey: true,
      autoIncrement: false,
      field: "ID",
    },
    name: {
      type: new DataTypes.STRING(),
      allowNull: false,
      field: "NAME",
    },
    ip: {
      type: new DataTypes.STRING(),
      allowNull: true,
      field: "IP",
    },
    mac: {
      type: new DataTypes.STRING(),
      allowNull: true,
      field: "MAC",
    },
    hw: {
      type: new DataTypes.STRING(),
      allowNull: true,
      field: "HW",
    },
    date: {
      type: new DataTypes.STRING(),
      allowNull: true,
      field: "DATE",
    },
    known: {
      type: new DataTypes.INTEGER(),
      allowNull: true,
      field: "KNOWN",
    },
    now: {
      type: new DataTypes.INTEGER(),
      allowNull: true,
      field: "NOW",
    },
  },
  {
    sequelize: sequelize,
    tableName: "now",
    modelName: "Host",
    createdAt: false,
    updatedAt: false,
    deletedAt: false
  }
);

/**
 * Retrieves all records from the "now" table in the database.
 *
 * @returns A Promise that resolves to an array of objects, where each object represents a row from the "now" table.
 */
export async function findAllHosts(): Promise<Host[]> {
  return await sequelize.query("SELECT * FROM `now`", {
    type: QueryTypes.SELECT,
    logging: (msg) => console.log(msg),
  });
}
