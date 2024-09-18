import * as logger from '../../../log/src/index.mjs';
import { isString } from 'radash';
import { which, LogEntry } from 'zx';

// type ZxCommonOptions = Pick<Options, 'shell' | 'verbose' | 'nothrow' | 'log'>;

const whichOptions = {
  path: '/bin:/usr/bin:/usr/local/bin',
  nothrow: true,
};

export async function detectShell(): Promise<string | boolean> {
  const shells = [
    which('zsh', whichOptions),
    which('bash', whichOptions),
    which('sh', whichOptions),
  ];

  // set the shell in order of preference [zsh -> bash -> sh]
  for (const shell of shells) {
    const isShell = await shell;
    if (isString(isShell)) {
      return isShell;
    }
  }

  return false;
}

/**
 * Zx script to add color based on output type
 * @param entry one of the following:
 *
 * stdout
 * stderr
 * cmd
 * fetch
 * cd
 * custom
 * retry
 */
export function logProcessor(entry: LogEntry) {
  switch (entry.kind) {
    case 'stdout':
      logger.info(entry.data.toString());
      break;
    case 'stderr':
      logger.error(entry.data.toString());
      break;
    case 'cmd':
      logger.info(`Running command: ${entry.cmd}`);
      break;
    case 'fetch':
      logger.info(`Fetching ${entry.url} through ${entry.init?.method}`);
      break;
    case 'cd':
      logger.info(`Changing directory to ${entry.dir}`);
      break;
    case 'custom':
      logger.warn(`executing custom zx function: ${entry.data.toString()}`);
      break;
    case 'retry':
      logger.info(`retrying due to ${entry.error}`);
  }
}
