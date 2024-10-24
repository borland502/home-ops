import {
  $,
  error,
  getSystemData,
  hasCommand,
  info,
  installByBrew,
  detectShell,
  echo
} from "@technohouser/zx-utils"
import { exit } from "process"

const sysinfo = await getSystemData()

$.shell = await detectShell()

async function installPipxPackages() {
  const pipxPackages = await $`chezmoi data --format json | jq -rce '.pipx.packages[]'`.lines()
    .catch((reason: unknown) => {
      error(`Error: ${reason}`)
      return [] as string[]
    })

  for (const pkg of pipxPackages) {
    hasCommand(pkg).then((hasPkg) => {
      if (hasPkg === null || !hasPkg) {
        $`pipx install ${pkg}`
      } else {
        info(`Package ${pkg} is already installed`)
      }
    }).catch((reason: unknown) => {
      error(`Error: ${reason}`)
    })
  }
}

async function installNpmPackages() {
  const npmPackages = await $`chezmoi data --format json | jq -rce '.npm.packages[]'`.lines()
    .catch((reason: unknown) => {
      error(`Error: ${reason}`)
      return [] as string[]
    })

  for (const pkg of npmPackages) {
    await $`npm install -g ${pkg}`
  }
}

async function installBrew(brewPackages: Promise<string[]>, brewCasks?: Promise<string[]>) {
  if (await hasCommand("chezmoi") === null) {
    $`brew install chezmoi`
  }

  const installedBrewCasks = await $`brew list --casks`.lines()
    .catch((reason: unknown) => {
      error(`Error: ${reason}`)
      return [] as string[]
    })

  if (brewCasks !== undefined) {
    for (const pkg of await brewCasks) {
      if (!installedBrewCasks.includes(pkg)) {
        await installByBrew(pkg, true)
      } else {
        info(`Cask ${pkg} is already installed`)
      }
    }
  }
  const installedBrewPackages = await $`brew list --formulae`.lines()

  for (const pkg of await brewPackages) {
    if (!installedBrewPackages.includes(pkg)) {
      await installByBrew(pkg)
    } else {
      info(`Package ${pkg} is already installed`)
    }
  }
}

await $`brew update`.then(() => {
  $`brew upgrade`
}).then(() => {
  $`brew doctor`
}).then(() => {
  $`brew missing`
}).then(() => {
  const brewPackages = $`chezmoi data --format json | jq -rce '.brew.packages[]'`.lines()

  let brewCasks: Promise<string[]> | undefined
  if (sysinfo.os.distro === "macOS") {
    brewCasks = $`chezmoi data --format json | jq -rce '.brew.casks[]'`.lines()
  }

  installBrew(brewPackages, brewCasks).then(() => {
    info("All brew packages installed")
  })

}).catch((reason: unknown) => {
  error(`Error: ${reason}`)
}).finally(() => {
  $`brew cleanup -s`
})

await hasCommand("npm").then((hasNPM) => {
  if (hasNPM === null || !hasNPM) {
    $`brew install npm`
  }

  $`npm update -g`.then(() => {
    $`npm install -g npm`
  }).then(() => {

    installNpmPackages().then(() => {
      info("All npm packages installed")
    })

  }).catch((reason: unknown) => {
    error(`Error: ${reason}`)
  })

})

await hasCommand("pipx").then((hasPipx) => {
  if (hasPipx === null || !hasPipx) {
    $`brew install pipx`
  }

  $`pipx upgrade-all`.then(() => {
    installPipxPackages().then(() => {
      info("All pipx packages installed")
    })

  }).catch((reason: unknown) => {
    error(`Error: ${reason}`)
  })
})

await hasCommand("tldr").then((hasTLDR) => {
  if (hasTLDR === null || !hasTLDR) {
    $`brew install tldr`
  }

  $`tldr --update`.catch((reason: unknown) => {
    error(`Error: ${reason}`)
  })
})

await hasCommand("softwareupdate").then((hasSoftwareUpdate) => {
  if(hasSoftwareUpdate !== null && sysinfo.os.distro === "macOS") {
    $`softwareupdate --install --all --force`.catch((reason: unknown) => {
      error(`Error: ${reason}`)
    })
  }
})
