// https://github.com/lucax88x/configs/blob/main/scripts/utilities.mts

import type { PathLike } from "fs";
import type { Systeminformation } from "systeminformation";
import { getAllData } from "systeminformation";
import { xdgCache } from "xdg-basedir";
import { $, fs, path, question, tmpfile, which } from "zx";
import { error, warn } from "@technohouser/log";
import { isNil } from "@technohouser/utils";

const DISTRO = {
  arch: "arch",
  pengwin: "pengwin",
  endeavouros: "endeavouros",
  manjaro: "manjaro",
  ubuntu: "ubuntu",
  debian: "debian",
} as const;

type Distro = keyof typeof DISTRO;

const OS = {
  linux: "linux",
  windows: "windows",
  darwin: "darwin",
} as const;

type Os = keyof typeof OS;

const PKG_MGR = {
  apt: "apt",
  brew: "brew",
  pacman: "pacman",
  nala: "nala",
  scoop: "scoop",
  yay: "yay",
} as const;

// type Pkgmgr = keyof typeof PKG_MGR;

export type SystemInformation = Systeminformation.StaticData &
  Systeminformation.DynamicData;

export async function getSystemData(): Promise<SystemInformation> {
  const cacheDir = isNil(xdgCache)
    ? "${os.homedir()}/.cache/sysinfo"
    : path.join(xdgCache, "/sysinfo");
  const sysInfoCacheFile = path.join(cacheDir, "sysinfo.json");

  fs.ensureDirSync(cacheDir, {
    mode: 0o700,
  });

  try {
    return fs.readJSONSync(sysInfoCacheFile) as SystemInformation;
  } catch (err) {
    warn(`Error reading system information cache: ${err}`);
    const data = await getAllData("*", "*");
    fs.outputJsonSync(sysInfoCacheFile, JSON.stringify(data, null, 2));
    return data;
  }
}

const systemData = await getSystemData();

export async function askConfirmation(quest: string): Promise<boolean> {
  const confirmation = await question(`${quest} (y)`);

  return confirmation.toLowerCase() === "y";
}

export async function hasCommand(command: string): Promise<boolean> {
  return !!(await which(command, { nothrow: true }));
}

// export async function existsApplicationInOsx(app: string) {
//   return async (): Promise<Condition> => {
//     try {
//       return toCondition(await fs.exists(`/Applications/${app}.app`));
//     } catch (error) {
//       return 'not exists';
//     }
//   };
// }

// export async function existsByPwsh(command: string) {
//   return async (): Promise<Condition> => {
//     try {
//       return toCondition(!!(await $`command -v ${command}`));
//     } catch (error) {
//       return 'not exists';
//     }
//   };
// }

// export async function existsFontInUnix(font: string) {
//   return async (): Promise<Condition> => {
//     try {
//       return toCondition(!!(await $`fc-list | grep -i ${font}`));
//     } catch (error) {
//       return 'not exists';
//     }
//   };
// }

export async function installByParu(pkg: string) {
  $.verbose = true;
  await $`paru -S --noconfirm ${pkg}`;

  return true;
}

export async function installByApt(pkg: string) {
  $.verbose = true;
  await $`sudo apt install -y ${pkg}`;

  return true;
}

// export async function installByNala(pkg: string) {
//   $.verbose = true;
//   // $`sudo nala install -y ${pkg}`.then(
//   //   (output) => {},
//   //   (err) => {}
//   // );

//   return true;
// }

export async function getPackageManager(os: Os, distro: Distro) {
  const pkgMgrs = [];

  if (os === OS.linux) {
    switch (distro) {
      case DISTRO.arch:
        if (await hasCommand(PKG_MGR.pacman)) {
          pkgMgrs.push(PKG_MGR.pacman);
        } else {
          throw new Error("pacman cannot be missing for an arch install");
        }
        if (await hasCommand(PKG_MGR.yay)) {
          pkgMgrs.push(PKG_MGR.yay);
        }
        break;
      case DISTRO.debian:
      case DISTRO.pengwin:
      case DISTRO.ubuntu:
        if (await hasCommand(PKG_MGR.apt)) {
          pkgMgrs.push(PKG_MGR.apt);
        } else {
          throw new Error("apt cannot be missing for a debian install");
        }
        if (await hasCommand(PKG_MGR.nala)) {
          pkgMgrs.push(PKG_MGR.nala);
        }
        break;
      default:
        throw new Error(`Could not determine pkgMgr for OS: ${os}`);
    }
  }
}

export async function downloadScript(url: URL) {
  const res = await fetch(url);
  if (res.ok) {
    return tmpfile(url.hash, await res.text());
  }

  throw new Error(`Error downloading script: ${res.status}`);
}

// for potentially long-running scripts it is better to execute this way with zx as prompts to the user
// and output are displayed closer to realtime
export async function executeScript(file: PathLike) {
  fs.chmodSync(file, "0500");
  await $`NONINTERACTIVE=1; ${file}`.verbose(true).run();
}

export async function ensureBrew() {
  return downloadScript(
    new URL(
      "https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh"
    )
  ).then(
    async (file) => {
      return await executeScript(file);
    },
    (err: Error) => {
      error(err.message);
    }
  );
}

export async function installByBrew(pkg: string, asCask = false) {
  if (isNil(await hasCommand(PKG_MGR.brew))) {
    warn("Homebrew not installed.  Installing now...");
    ensureBrew().catch((result) => {
      error(`There was a problem installing brew: ${result}`);
    });
  }

  $.verbose = true;
  if (asCask) {
    await $`brew install --cask ${pkg}`;
  } else {
    await $`brew install ${pkg}`;
  }
  return true;
}

export async function installByScoop(pkg: string) {
  $.verbose = true;
  await $`scoop install ${pkg}`;

  return true;
}
