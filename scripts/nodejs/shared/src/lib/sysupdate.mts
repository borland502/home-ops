import { $, which } from "zx";
import { ensureBrew, getSystemData, installByBrew, SystemInformation } from "./pkg-install.mts";

try {
    const sysinfo: SystemInformation = await getSystemData;

    // save sysinfo json to file
    await $`mkdir -p ~/.local/share/`
} catch (reason: unknown) {
    console.error(`Error: ${reason}`);
}

// try {
    // Brew update and related tasks
//     await $`brew update`;
//     await $`brew upgrade`;
//     await $`brew doctor`;
//     await $`brew missing`;
//     {
//         const brewPackages = $`chezmoi data --format json | jq -rce '.brew.packages[]'`.lines();
//         let brewCasks: Promise<string[]> | undefined;
//         if (sysinfo.os.distro === "macOS") {
//             brewCasks = $`chezmoi data --format json | jq -rce '.brew.casks[]'`.lines();
//         }
//         await installBrew(brewPackages, brewCasks);
//         console.info("All brew packages installed.");
//     }

//     // NPM packages
//     if (!(await which("npm"))) {
//         await $`brew install npm`;
//     }
//     await $`npm update -g`.quiet();
//     await $`npm install -g npm`;
//     await installNpmPackages().then(() => {
//         console.info("All npm packages installed.");
//     });

//     // Pipx packages
//     if ((await which("pipx")) === null || !(await which("pipx"))) {
//         await $`brew install pipx`;
//     }
//     await $`pipx upgrade-all`;
//     await installPipxPackages().then(() => {
//         console.info("All pipx packages installed.");
//     });

//     // TLDR update
//     if ((await which("tldr")) === null || !(await which("tldr"))) {
//         await $`brew install tldr`;
//     }
//     await $`tldr --update`;

//     // macOS software update
//     if ((await which("softwareupdate")) !== null && sysinfo.os.distro === "macOS") {
//         await $`softwareupdate --install --all --force`;
//     }

//     // Flatpak update
//     if (!(await which("flatpak"))) {
//         $`apt install flatpak`;
//     }
//     await $`flatpak update -y`;
// } catch (reason: unknown) {
//     console.error(`Error: ${reason}`);
// }

export async function installPipxPackages() {
    const pipxPackages = await $`chezmoi data --format json | jq -rce '.pipx.packages[]'`
        .lines()
        .catch((reason: unknown) => {
            console.error(`Error: ${reason}`);
            return [] as string[];
        });

    for (const pkg of pipxPackages) {
        await which(pkg)
            .then(async (hasPkg) => {
                if (!hasPkg) {
                    await $`pipx install ${pkg}`;
                }
            })
            .catch((reason) => {
                console.error(`Error: ${reason}`);
            });
    }
}

export async function installNpmPackages() {
    const npmPackages = await $`chezmoi data --format json | jq -rce '.npm.packages[]'`
        .lines()
        .catch((reason: unknown) => {
            console.error(`Error: ${reason}`);
            return [] as string[];
        });

    for (const pkg of npmPackages) {
        await $`npm install -g ${pkg}`;
    }
}

export async function installBrew(brewPackages: Promise<string[]>, brewCasks?: Promise<string[]>) {
    // const pkgResult = await ensureBrew();
    // if (pkgResult.exitCode !== 0) {
    //     console.error(pkgResult.text("utf-8"));
    // }

    if (!(await hasCommand("chezmoi"))) {
        await $`brew install chezmoi`;
    }
    const info = console.info;
    const installedBrewCasks = await $`brew list --casks`.lines().catch((reason: unknown) => {
        console.error(`Error: ${reason}`);
        return [] as string[];
    });

    if (brewCasks !== undefined) {
        for (const pkg of await brewCasks) {
            if (!installedBrewCasks.includes(pkg)) {
                await installByBrew(pkg, true);
            } else {
                console.info(`Cask ${pkg} is already installed.`);
            }
        }
    }
    const installedBrewPackages = await $`brew list --formulae`.lines();

    for (const pkg of await brewPackages) {
        if (!installedBrewPackages.includes(pkg)) {
            await installByBrew(pkg);
        } else {
            console.info(`Package ${pkg} is already installed.`);
        }
    }
}

async function hasCommand(command: string): Promise<boolean> {
    return !!(await which(command, { nothrow: true }));
}
