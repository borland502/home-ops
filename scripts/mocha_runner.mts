import { detectShell, logProcessor } from './shared/zx-utils/src/index.mjs';
import { info, error } from '../dist/libs/shared/log';
import { $, globby, cd, syncProcessCwd } from 'zx';
import { defer } from 'radash';
import { register } from 'ts-node';
import mocha from 'mocha';

$.shell = await detectShell();
$.log = logProcessor;

// $.env.NODE_OPTIONS = '--loader ts-node/esm';

// globby(['apps/**/*.spec.ts', 'libs/**/*.spec.ts']).then((result) => {
//   result.forEach((fileName) => {
//     $`npx ts-mocha -p tsconfig.base.json --paths ${fileName} --require esm --require ts-mocha`;
//   });
// });

// for (const specFile of await globby(['*.spec.ts', '*.test.ts'])) {
//   info(`Running mocha on test ${specFile}`);
//   $`mocha --config ${specFile}`
//     .run()
//     .lines()
//     .then((line) => {
//       info(line);
//     })
//     .catch((err) => {
//       error(`Could not run mocha on test ${specFile}`);
//     });
// }
