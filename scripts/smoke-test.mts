import { detectShell, logProcessor } from "@technohouser/zx-utils";
import { info } from "../dist/libs/shared/log";
import { $ } from "zx";

$.shell = await detectShell();
$.log = logProcessor;

for (const project in await $`nx show projects`) {
  // lint (eslint)
  info(`Linting project ${project}`);
  $`nx lint ${project}`;
}
