import { Table, Column, Model, DataType } from "sequelize-typescript";

@Table
export class Host extends Model {
  @Column(DataType.INTEGER)
  ID!: number;

  @Column(DataType.STRING)
  NAME!: string;

  @Column(DataType.STRING)
  IP?: string;

  @Column(DataType.STRING)
  MAC?: string;

  @Column(DataType.STRING)
  HW?: string;

  @Column(DataType.STRING)
  DATE?: string;

  @Column(DataType.INTEGER)
  KNOWN?: number;

  @Column(DataType.INTEGER)
  NOW?: number;
}

export default {
  Host,
} as const;
