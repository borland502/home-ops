import {$, which, argv, fs, error, info} from "@technohouser/zx-utils"
import {isString, get} from "radash"
import {exit} from "node:process";

/**
 * This script checks if Visual Studio Code (VSCode) is installed on the system
 * and attempts to open files provided as command-line arguments in VSCode.
 * If VSCode is not found or a file does not exist, it logs an error and exits.
 * CLI Options:
 * --help    Show this help message and exit
 */

if (get(argv, "help")) {
  info(`
Usage: vscode.mts [options] [file ...]

Options:
  --help    Show this help message and exit

Arguments:
  file      One or more files to open in VSCode
  `);
  exit(0);
}

const vscode: string | boolean = await which("code")
if (!vscode) {
  error("VSCode not found in your system")
  exit(2)
}

if (argv._.length === 0) {
  // No arguments provided, open VSCode in the current directory
  await $`${vscode} .`
} else {
  for (const arg of argv._) {
    if(await fs.pathExists(arg)) {
      await $`${vscode} ${arg}`
    } else {
      error(`Directory ${arg} does not exist`)
      exit(2)
    }
  }
}
