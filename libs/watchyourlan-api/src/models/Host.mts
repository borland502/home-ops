import { Table, Column, Model, DataType } from "sequelize-typescript";

@Table
export class Host extends Model {
  @Column(DataType.INTEGER)
  declare ID: number;

  @Column(DataType.STRING)
  declare NAME: string;

  @Column(DataType.STRING)
  declare IP?: string;

  @Column(DataType.STRING)
  declare MAC?: string;

  @Column(DataType.STRING)
  declare HW?: string;

  @Column(DataType.STRING)
  declare DATE?: string;

  @Column(DataType.INTEGER)
  declare KNOWN?: number;

  @Column(DataType.INTEGER)
  declare NOW?: number;
}

