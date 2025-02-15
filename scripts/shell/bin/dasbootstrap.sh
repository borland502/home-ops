#!/usr/bin/env bash

set -e

PATH="/bin:/sbin:/usr/bin:/usr/sbin:/usr/local/bin:/usr/local/sbin"

# shellcheck disable=SC1090,SC1091
source "${HOME}/.local/share/automation/home-ops/scripts/shell/lib/install_doctor_functions.sh"

ensureRootPackageInstalled sudo
ensureRootPackageInstalled zip

# shellcheck disable=SC1090,SC1091
source "${HOME}/.local/share/automation/home-ops/scripts/shell/lib/logger.sh"


declare -rx GH_RAW="https://raw.githubusercontent.com/borland502"
declare -rx GH_PROJ="https://github.com/borland502/home-ops"

create_sudo_user

bootstrap_ansible_node

ensureLocalPath "${XDG_BIN_HOME:-$HOME/.local/bin}"
ensureLocalPath "${XDG_LIB_HOME:-$HOME/.local/lib}"
ensureLocalPath "${XDG_DATA_HOME:-$HOME/.local/share}"
ensureLocalPath "${XDG_CONFIG_HOME:-$HOME/.config}"
ensureLocalPath "${XDG_CACHE_HOME:-$HOME/.cache}"
ensureLocalPath "${XDG_STATE_HOME:-$HOME/.local/state}"

# Fallback action in case the install is interrupted after root phase is done
if [[ $USER == "root" ]] && [[ -f '/root/.rootfinished' ]]; then
    # presume sudo powers at this point
    _username="$(cat /root/.rootfinished)"
    exec su - "${_username}" "/home/${_username}/$(basename "$0")"
fi

ensurePackageInstalled build-essential
ensurePackageInstalled git
ensurePackageInstalled curl
ensurePackageInstalled rsync
ensurePackageInstalled npm
ensurePackageInstalled gcc
ensurePackageInstalled unison
ensurePackageInstalled jq


# if [[ $USER == "root" ]]; then
#     read -rp "Enter the username to create or default to jhettenh: " _user

#     if [ -z "$_user" ]; then
#         _user="jhettenh"
#     fi

#     # Create a quick and dirty service user then restart the script as that user
#     create_sudo_user "${_user}"
# fi

ensureLocalPath "${HOME}/.local/share/automation/home-ops"
if [[ ! -d "${HOME}/.local/share/automation/home-ops/.git" ]]; then
    # Only clone if the directory doesn't already exist and have a .git directory
    git clone --recurse-submodules https://github.com/borland502/home-ops.git "${HOME}/.local/share/automation/home-ops"
fi

eval "$(/home/linuxbrew/.linuxbrew/bin/brew shellenv)"

brew install dasel

if ! [[ $(command -v dasel) ]]; then
    logger error "Dasel is required to parse toml"
fi

logger info "Bootstrapping complete"
