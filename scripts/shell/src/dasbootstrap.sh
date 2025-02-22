#!/usr/bin/env bash

# dasbootstrap.sh assumes no previously installed elements and will attempt to use more common commands
# to retrieve them

declare -rx GH_RAW="https://raw.githubusercontent.com/borland502"
declare -rx GH_PROJ="https://github.com/borland502/home-ops"

ensureRootPackageInstalled git
ensureRootPackageInstalled zip
ensureRootPackageInstalled curl

create_sudo_user
installTask
bootstrap_ansible_node

ensurePackageInstalled build-essential
ensurePackageInstalled git
ensurePackageInstalled curl
ensurePackageInstalled rsync
ensurePackageInstalled npm
ensurePackageInstalled gcc

mkdir -p "${HOME}/.local/share/automation"
if [ ! -d "${HOME}/.local/share/automation/home-ops" ]; then
  git clone --recurse-submodules https://github.com/borland502/home-ops.git "${HOME}/.local/share/automation/home-ops"
fi
