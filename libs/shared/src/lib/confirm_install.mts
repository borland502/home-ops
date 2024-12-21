import {argv, askConfirmation, error, info, installPkg} from "../index.mjs"
import {get} from "radash";
import {exit} from "node:process";
import type {ProcessOutput} from "zx";

if (get(argv, "help")) {
  info(`
Usage: confirm_install [options] [pkg ...]

Options:
  --help    Show this help message and exit

Arguments:
  pkg      One or more packages to install
  `);
  exit(0);
}

// eliminate the 0 arg case
if (argv._.length === 0) {
  error("No packages provided to install")
  exit(2)
}

if (await askConfirmation(`Do you want to install the following packages: ${argv._.toString()} ?`)) {
  for (const arg of argv._) {
    const pkgResult: ProcessOutput = await installPkg(arg)
    if (pkgResult.exitCode !== 0) {
      error(pkgResult.text('utf-8'))
    }

    info(pkgResult.text('utf-8'));
  }
}

