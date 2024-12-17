import {access, mkdir} from "node:fs/promises";
import {$, error, info, installPkg, warn, which, xdgConfig} from "@technohouser/shared";

import path from "path";
import {exit} from "node:process";

const xdgConfigHome = xdgConfig || path.join(process.env.HOME || "", ".config");
const configFilePath = path.join(xdgConfigHome, "omelette", "gomplate.yaml");
const assetsConfigFilePath = path.join("assets", "gomplate.yaml");

export async function ensureGomplate() {
    try {
        which("gomplate").then(async (hasGoplate) => {
            if (hasGoplate === null || !hasGoplate) {
                info("Gomplate not found, installing");

                await installPkg("gomplate").then((res) => {
                    if (res.exitCode === 0) {
                        info("Gomplate installed");
                    } else {
                        error("Gomplate installation failed");
                        exit(1)
                    }
                })
            }
        });

        await access(configFilePath);
        info(`Config file found at ${configFilePath}`);
    } catch (err) {
        warn(`Config file not found at ${configFilePath}, copying from assets`);
        await mkdir(path.dirname(configFilePath), {recursive: true});

        $`gomplate --file ${assetsConfigFilePath} --out ${configFilePath}`;
        info(`Config file copied to ${configFilePath}`);
    }
}

export async function ensureTemplatesDir() {
    try {
        await access(path.join(xdgConfigHome, "omelette", "templates"));
        info(`Templates directory found at ${path.join(xdgConfigHome, "omlette", "templates")}`);
    } catch (err) {
        warn(`Templates directory not found at ${path.join(xdgConfigHome, "omlette", "templates")}, creating`);
        await mkdir(path.join(xdgConfigHome, "omelette", "templates"), {recursive: true});
        info(`Templates directory created at ${path.join(xdgConfigHome, "omlette", "templates")}`);
    }
}
