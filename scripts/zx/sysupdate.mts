import {
    $,
    ensureBrew,
    error,
    getSystemData,
    which,
    info,
    initShell,
    installByBrew,
} from "@technohouser/shared";

try {
    const sysinfo = await getSystemData();
    await initShell($);

    // Brew update and related tasks
    await $`brew update`;
    await $`brew upgrade`;
    await $`brew doctor`;
    await $`brew missing`;
    {
        const brewPackages = $`chezmoi data --format json | jq -rce '.brew.packages[]'`.lines();
        let brewCasks: Promise<string[]> | undefined;
        if (sysinfo.os.distro === "macOS") {
            brewCasks = $`chezmoi data --format json | jq -rce '.brew.casks[]'`.lines();
        }
        await installBrew(brewPackages, brewCasks);
        info("All brew packages installed");
    }

    // NPM packages
    if ((await which("npm")) === null || !(await which("npm"))) {
        $`brew install npm`;
    }
    await $`npm update -g`;
    await $`npm install -g npm`;
    await installNpmPackages().then(() => {
        info("All npm packages installed");
    });

    // Pipx packages
    if ((await which("pipx")) === null || !(await which("pipx"))) {
        $`brew install pipx`;
    }
    await $`pipx upgrade-all`;
    await installPipxPackages().then(() => {
        info("All pipx packages installed");
    });

    // TLDR update
    if ((await which("tldr")) === null || !(await which("tldr"))) {
        $`brew install tldr`;
    }
    await $`tldr --update`;

    // macOS software update
    if ((await which("softwareupdate")) !== null && sysinfo.os.distro === "macOS") {
        await $`softwareupdate --install --all --force`;
    }

    // Flatpak update
    if ((await which("flatpak")) === null || (await which("flatpak")) === "") {
        $`apt install flatpak`;
    }
    await $`flatpak update -y`;
} catch (reason: unknown) {
    error(`Error: ${reason}`);
}

async function installPipxPackages() {
    const pipxPackages = await $`chezmoi data --format json | jq -rce '.pipx.packages[]'`
        .lines()
        .catch((reason: unknown) => {
            error(`Error: ${reason}`);
            return [] as string[];
        });

    for (const pkg of pipxPackages) {
        which(pkg)
            .then((hasPkg) => {
                if (hasPkg === null || !hasPkg) {
                    $`pipx install ${pkg}`;
                } else {
                    info(`Package ${pkg} is already installed`);
                }
            })
            .catch((reason: unknown) => {
                error(`Error: ${reason}`);
            });
    }
}

async function installNpmPackages() {
    const npmPackages = await $`chezmoi data --format json | jq -rce '.npm.packages[]'`
        .lines()
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
        error(pkgResult.text("utf-8"));
    }

    if ((await hasCommand("chezmoi")) === null) {
        $`brew install chezmoi`;
    }

    const installedBrewCasks = await $`brew list --casks`.lines().catch((reason: unknown) => {
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
