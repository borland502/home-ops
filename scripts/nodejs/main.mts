import { parseArgs } from "util";
import { installPkg } from "./shared/src/index.mjs";
import { $ } from "bun";

type UpdateType = "brew" | "npm" | "pipx" | "flatpak" | "tldr" | "macos";

class CLI {
    constructor() {
        this.setup();
    }

    setup() {
        const { values } = parseArgs({
            options: {
                install: { type: "string" },
                update: { type: "string" },
                help: { type: "boolean" },
            },
        });

        if (values.help) {
            this.showHelp();
            return;
        }

        if (values.install) {
            this.handleInstall(values.install as string);
        }

        if (values.update) {
            this.handleUpdate(values.update as UpdateType);
        }
    }

    async handleInstall(...pkgs: string[]) {
        try {
            for (const pkg of pkgs) {
                await installPkg(pkg);
                console.log(`Successfully installed package: ${pkg}`);
            }
        } catch (error) {
            console.error(`Failed to install package: ${error}`);
        }
    }

    async handleUpdate(type: UpdateType) {
        try {
            switch (type) {
                case "brew":
                    await $`brew update && brew upgrade`;
                    break;
                case "npm":
                    await $`npm update -g`;
                    break;
                case "pipx":
                    await $`pipx upgrade-all`;
                    break;
                case "flatpak":
                    await $`flatpak update`;
                    break;
                case "tldr":
                    await $`tldr --update`;
                    break;
                case "macos":
                    await $`softwareupdate -i -a`;
                    break;
                default:
                    console.error(`Unknown update type: ${type}`);
            }
            console.log(`Successfully updated: ${type}`);
        } catch (error) {
            console.error(`Failed to update: ${error}`);
        }
    }

    showHelp() {
        console.log(`
            CLI Help:
            --install <package>  Install a package
            --update <type>      Update a specific type (brew, npm, pipx, flatpak, tldr, macos)
            --help               Show this help message
        `);
    }
}

new CLI();
