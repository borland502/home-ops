import { Chalk } from 'chalk';
import config from 'config';

const chalk = new Chalk();

export const debug = (msg: string) =>
  console.debug(chalk.hex(config.get('colors.blue'))(msg));
export const info = (msg: string) =>
  console.info(chalk.hex(config.get('colors.purple'))(msg));
export const warn = (msg: string) =>
  console.warn(chalk.hex(config.get('colors.orange'))(msg));
export const error = (msg: string) =>
  console.error(chalk.hex(config.get('colors.red'))(msg));
export const trace = (msg: string) =>
  console.trace(chalk.hex(config.get('colors.yellow'))(msg));
