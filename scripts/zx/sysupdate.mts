import {
    $,
    ensureBrew,
    error,
    getSystemData,
    which,
    info,
    initShell,
    installByBrew
} from "@technohouser/shared";

async function main() {
    const sysinfo = await getSystemData();

    await initShell($);

    async function installAptPackages() {
        const aptPackages = await $`chezmoi data --format json | jq -rce '.apt.packages[]'`.lines()
            .catch((reason: unknown) => {
                error(`Error: ${reason}`);
                return [] as string[];
            });

        for (const pkg of aptPackages) {
            await $`sudo apt install -y ${pkg}`;
        }
    }

    async function installPipxPackages() {
        const pipxPackages = await $`chezmoi data --format json | jq -rce '.pipx.packages[]'`.lines()
            .catch((reason: unknown) => {
                error(`Error: ${reason}`);
                return [] as string[];
            });

        for (const pkg of pipxPackages) {
            which(pkg).then((hasPkg) => {
                if (hasPkg === null || !hasPkg) {
                    $`pipx install ${pkg}`;
                } else {
                    info(`Package ${pkg} is already installed`);
                }
            }).catch((reason: unknown) => {
                error(`Error: ${reason}`);
            });
        }
    }

    async function installNpmPackages() {
        const npmPackages = await $`chezmoi data --format json | jq -rce '.npm.packages[]'`.lines()
            .catch((reason: unknown) => {
                error(`Error: ${reason}`);
                return [] as string[];
            });

        for (const pkg of npmPackages) {
            await $`npm install -g ${pkg}`;
        }
    }

    async function installBrew(brewPackages: Promise<string[]>, brewCasks?: Promise<string[]>) {
        const pkgResult = await ensureBrew();
        if (pkgResult.exitCode !== 0) {
            error(pkgResult.text('utf-8'));
        }

        if (await hasCommand("chezmoi") === null) {
            $`brew install chezmoi`;
        }

        const installedBrewCasks = await $`brew list --casks`.lines()
            .catch((reason: unknown) => {
                error(`Error: ${reason}`);
                return [] as string[];
            });

        if (brewCasks !== undefined) {
            for (const pkg of await brewCasks) {
                if (!installedBrewCasks.includes(pkg)) {
                    await installByBrew(pkg, true);
                } else {
                    info(`Cask ${pkg} is already installed`);
                }
            }
        }
        const installedBrewPackages = await $`brew list --formulae`.lines();

        for (const pkg of await brewPackages) {
            if (!installedBrewPackages.includes(pkg)) {
                await installByBrew(pkg);
            } else {
                info(`Package ${pkg} is already installed`);
            }
        }
    }

    await which("apt").then(async () => {
        await installAptPackages();

        await $`sudo apt update && sudo apt dist-upgrade -y`.catch((reason: unknown) => {
            error(`Error: ${reason}`);
        });
    });

    await $`brew update`.then(() => {
        $`brew upgrade`;
    }).then(() => {
        $`brew doctor`;
    }).then(() => {
        $`brew missing`;
    }).then(async () => {
        const brewPackages = $`chezmoi data --format json | jq -rce '.brew.packages[]'`.lines();

        let brewCasks: Promise<string[]> | undefined;
        if (sysinfo.os.distro === "macOS") {
            brewCasks = $`chezmoi data --format json | jq -rce '.brew.casks[]'`.lines();
        }

        await installBrew(brewPackages, brewCasks).then(() => {
            info("All brew packages installed");
        });

    }).catch((reason: unknown) => {
        error(`Error: ${reason}`);
    }).finally(() => {
        $`brew cleanup -s`;
    });

    await which("npm").then(async (hasNPM) => {
        if (hasNPM === null || !hasNPM) {
            $`brew install npm`;
        }

        await $`npm update -g`.then(() => {
            $`npm install -g npm`;
        }).then(async () => {
            await installNpmPackages().then(() => {
                info("All npm packages installed");
            });

        }).catch((reason: unknown) => {
            error(`Error: ${reason}`);
        });

    });

    await which("pipx").then(async (hasPipx) => {
        if (hasPipx === null || !hasPipx) {
            $`brew install pipx`;
        }

        await $`pipx upgrade-all`.then(async () => {
            await installPipxPackages().then(() => {
                info("All pipx packages installed");
            });

        }).catch((reason: unknown) => {
            error(`Error: ${reason}`);
        });
    });

    await which("tldr").then((hasTLDR) => {
        if (hasTLDR === null || !hasTLDR) {
            $`brew install tldr`;
        }

        $`tldr --update`.catch((reason: unknown) => {
            error(`Error: ${reason}`);
        });
    });

    await which("softwareupdate").then((hasSoftwareUpdate) => {
        if (hasSoftwareUpdate !== null && sysinfo.os.distro === "macOS") {
            $`softwareupdate --install --all --force`.catch((reason: unknown) => {
                error(`Error: ${reason}`);
            });
        }
    });

    await which("flatpak").then((hasFlatpak: boolean) => {
        if (hasFlatpak === null || !hasFlatpak) {
            // TODO: Use generic package manager
            $`apt install flatpak`;
        }

        $`flatpak update -y`.catch((reason: unknown) => {
            error(`Error: ${reason}`);
        });
    });
}

main().catch((reason: unknown) => {
    error(`Error: ${reason}`);
});
