import {
    ensureBrew,
    getSystemData,
    installByBrew,
    hasCommand,
} from "./pkg-install.mjs";

import { $, ShellOutput, which } from "bun";

import { error, info } from "./log.mjs";



    const sysinfo = await getSystemData();


    // Brew update and subsequent commands
    // Brew update and subsequent commands
    export async function installBrewPackages() {
        if (!await hasCommand('chezmoi')) {
            error('chezmoi is not installed');
            return;
        }
        const brewPackages = $`chezmoi data --format json | jq -rce '.brew.packages[]'`;
        for await (const pkg of brewPackages.lines()) {
            await installByBrew(pkg);
        }
        
        if ((await getSystemData()).os.distro === "macOS") {
            const brewCasks = $`chezmoi data --format json | jq -rce '.brew.casks[]'`;
            for await(const cask of brewCasks.lines()) {
                await installByBrew(cask, true);
            }
        }
    }

    async function installNpmPackages() {
        try {
            const npmPackages: ShellOutput | undefined = await $`chezmoi data --format json | jq -rce '.npm.packages[]'`;
    
            for (const pkg of npmPackages) {
                await $`npm install -g ${pkg}`;
            }
        } catch (reason: unknown) {
            error(`Error: ${reason}`);
        }
    }

    async function installPipxPackages() {
        try {
            const pipxPackages = await $`chezmoi data --format json | jq -rce '.pipx.packages[]'`.lines();
    
            for (const pkg of pipxPackages) {
                try {
                    const hasPkg = await which(pkg);    

    async function installAptPackages() {
        const aptPackages = await $`chezmoi data --format json | jq -rce '.apt.packages[]'`
            .lines();
        };        

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

    // Software update on macOS
    // Flatpak update
    if ((await which("flatpak")) === null || (await which("flatpak")) === "") {
        await $`apt install flatpak`;
    }
    await $`flatpak update -y`;

    try {
        const installedBrewCasks = await $`brew list --casks`.lines();
        if (brewCasks !== undefined) {
            for (const pkg of await brewCasks) {
                if (!(await includesAsync(installedBrewCasks, pkg))) {
                    await installByBrew(pkg, true);
                } else {
                    info(`Cask ${pkg} is already installed`);
                }
            }
        }
    } catch (reason: unknown) {
        error(`Error: ${reason}`);
    }

