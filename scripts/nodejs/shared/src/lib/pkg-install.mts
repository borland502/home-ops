// https://github.com/lucax88x/configs/blob/main/scripts/utilities.mts

import type { PathLike } from "fs";
import type { Systeminformation } from "systeminformation";
import { getAllData } from "systeminformation";
import { $, fs, ProcessOutput, question, tmpfile, which } from "zx";
import { isEmpty } from "radash";

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
    macOS: "macOS",
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

export type SystemInformation = Systeminformation.StaticData & Systeminformation.DynamicData;

export async function getSystemData(): Promise<SystemInformation> {
    return await getAllData("*", "*");
}

export async function askConfirmation(quest: string): Promise<boolean> {
    const confirmation = await question(`${quest} (y)`);

    return confirmation.toLowerCase() === "y";
}

export async function hasCommand(command: string): Promise<boolean> {
    return !!(await which(command, { nothrow: true }));
}

export async function installByParu(pkg: string) {
    return $`paru -S --noconfirm ${pkg}`;
}

export async function installByApt(pkg: string) {
    return $`sudo apt install -y ${pkg}`;
}

export async function installByPacman(pkg: string) {
    return $`sudo pacman -S --noconfirm ${pkg}`;
}

export async function findByPacman(command: string) {
    return $`pacman -F ${command}`;
}

export async function installByYay(pkg: string) {
    return $`yay -S --noconfirm ${pkg}`;
}

export async function installByNala(pkg: string) {
    return $`sudo nala install -y ${pkg}`;
}

/**
 * Install a pkg for the given OS and distro.  Either pkg, command, or both values must be provided.
 * If pkg is provided, then a 1:1 mapping will be assumed:
 *
 *  e.g. pkg = "git" => apt install git
 *  not pkg = "fd" => apt install fd-find
 *
 * @param pkg
 */
export async function installPkg(pkg: string): Promise<ProcessOutput> {
    const sysinfo = await getSystemData();
    const os = sysinfo.os.platform as Os;
    const distro = sysinfo.os.distro as Distro;

    // always bet on brew (non-cask)
    if (await hasCommand(PKG_MGR.brew)) {
        const retVal = await findByBrew(pkg);
        if (retVal.exitCode === 0) {
            return installByBrew(pkg);
        }
    }

    switch (os) {
        case OS.windows:
            if (await hasCommand(PKG_MGR.scoop)) {
                const retVal = await findByScoop(pkg);
                if (retVal.exitCode === 0) {
                    return installByScoop(pkg);
                }
            }
            break;
        case OS.linux:
            switch (distro) {
                case DISTRO.arch:
                case DISTRO.endeavouros:
                    if (await hasCommand(PKG_MGR.pacman)) {
                        const retVal = await findByPacman(pkg);

                        if (retVal.exitCode === 0) {
                            return installByPacman(pkg);
                        }
                    }
                    if (await hasCommand(PKG_MGR.yay)) {
                        const retVal = await findByYay(pkg);
                        if (retVal.exitCode === 0) {
                            return installByYay(pkg);
                        }
                    }
                    break;
                case DISTRO.debian:
                case DISTRO.pengwin:
                case DISTRO.ubuntu:
                    if (await hasCommand(PKG_MGR.apt)) {
                        const retVal = await findByApt(pkg);
                        if (retVal.exitCode === 0) {
                            return installByApt(pkg);
                        }
                    }

                    if (await hasCommand(PKG_MGR.nala)) {
                        const retVal = await findByNala(pkg);
                        if (retVal.exitCode === 0) {
                            return installByNala(pkg);
                        }
                    }
                    break;
                default:
                    throw new Error(`Could not determine pkgMgr for OS: ${os}`);
            }
            break;
        default:
            throw new Error(`Could not determine pkgMgr for OS: ${os}`);
    }
    throw new Error(`Could not find package: ${pkg} on OS: ${os} and distro: ${distro}`);
}

async function findByApt(pkg: string) {
    return $`apt search ${pkg}`;
}

async function findByNala(pkg: string) {
    return $`nala search ${pkg}`;
}

async function findByScoop(pkg: string) {
    return $`scoop search ${pkg}`;
}

async function findByYay(pkg: string) {
    return $`yay -Ss ${pkg}`;
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
    return $`NONINTERACTIVE=1; ${file}`.verbose(true).run();
}

export async function ensureBrew() {
    if (await hasCommand("brew")) {
        return $`brew --version`;
    }

    const file = await downloadScript(
        new URL("https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh"),
    );

    if (isEmpty(file)) {
        throw new Error("Error downloading Homebrew install script");
    }

    return executeScript(file);
}

export async function installByFlatpak(pkg: string) {
    return $`flatpak install ${pkg}`;
}

export async function installByGh(pkg: string) {
    return $`gh release download ${pkg}`;
}

export async function findByBrew(pkg: string) {
    return $`brew search ${pkg}`;
}

export async function installByBrew(pkg: string, asCask = false) {
    if (asCask) {
        return $`brew install --cask ${pkg}`;
    } else {
        return $`brew install ${pkg}`;
    }
}

export async function installByScoop(pkg: string) {
    return $`scoop install ${pkg}`;
}
