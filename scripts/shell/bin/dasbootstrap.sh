#!/usr/bin/env bash

# dasbootstrap.sh assumes no previously installed elements and will attempt to use more common commands
# to retrieve them

declare -rx GH_RAW="https://raw.githubusercontent.com/borland502"
declare -rx GH_PROJ="https://github.com/borland502/home-ops"
# shellcheck disable=SC2155
declare -rx HO_ROOT="$(mktemp -d)"

# Fallback action in case the install is interrupted after root phase is done
if [[ $USER == "root" ]] && [[ -f '/root/.rootfinished' ]]; then
  # presume sudo powers at this point
  _username="$(cat /root/.rootfinished)"
  exec su - "${_username}" "/home/${_username}/$(basename "$0")"
fi

# @description Helper function for ensurePackageInstalled for Debian installations
function ensureDebianPackageInstalled() {
  if type sudo &>/dev/null && [ "$CAN_USE_SUDO" != 'false' ]; then
    sudo apt-get update
    sudo apt-get install -y "$1"
  else
    apt-get update
    apt-get install -y "$1"
  fi
}

# @description Helper function for ensurePackageInstalled for ArchLinux installations
function ensureArchPackageInstalled() {
  if type sudo &>/dev/null && [ "$CAN_USE_SUDO" != 'false' ]; then
    sudo pacman update
    sudo pacman -S "$1"
  else
    pacman update
    pacman -S "$1"
  fi
}

# @description Ensures given package is installed on a system.
#
# @arg $1 string The name of the package that must be present
#
# @exitcode 0 The package(s) were successfully installed
# @exitcode 1+ If there was an error, the package needs to be installed manually, or if the OS is unsupported
function ensurePackageInstalled() {
  export CAN_USE_SUDO='true'
  # TODO: Restore other types later
  if ! [[ $(command -v "$1") ]]; then
    ensureDebianPackageInstalled "$1"
  elif [[ -f "/etc/arch-release" ]]; then
    ensureArchPackageInstalled "$1"
  fi
}

# @description If the user is running this script as root, then create a new user
# and restart the script with that user. This is required because Homebrew
# can only be invoked by non-root users.
function create_sudo_user() {
  local _username=${1:-'ansible'}

  ensurePackageInstalled "sudo"
  ensurePackageInstalled "zsh"
  ensurePackageInstalled "zip"

  if [ -z "$NO_INSTALL_HOMEBREW" ] && [ "$USER" == "root" ] && [ -z "$INIT_CWD" ] && type useradd &>/dev/null; then
    # shellcheck disable=SC2016
    logger info "Running as root - creating separate user named ${_username} to run script with"
    echo "${_username} ALL=(ALL) NOPASSWD: ALL" >>/etc/sudoers
    useradd --create-home --shell "$(which zsh)" "${_username}" >/dev/null || ROOT_EXIT_CODE=$?
    if [ -n "$ROOT_EXIT_CODE" ]; then
      # shellcheck disable=SC2016
      logger info "User ${_username} already exists"
    fi

    cp "$0" "/home/${_username}/$(basename "$0")"
    chown "${_username}:${_username}" "/home/${_username}/$(basename "$0")"

    mkdir -p /root/.ssh
    chmod 700 /root/.ssh
    read -rp "Enter the public key authorized for root access: " pubkey && echo "$pubkey" > \
      "/root/.ssh/authorized_keys"
    chmod 600 /root/.ssh/authorized_keys
    chown "${_username}:${_username}" /root/.ssh/authorized_keys

    chown -R "${_username}:${_username}" "/home/${_username}"

    # shellcheck disable=SC2016
    logger info "Reloading the script with the ${_username} user"
    echo "${_username}" >/root/.rootfinished
    exec su - "${_username}" "/home/${_username}/$(basename "$0")"
  fi
}

ensurePackageInstalled build-essential
ensurePackageInstalled git
ensurePackageInstalled curl
ensurePackageInstalled rsync
ensurePackageInstalled npm
ensurePackageInstalled gcc

if [[ $USER == "root" ]]; then
  read -rp "Enter the username to create or default to ansible: " _user

  if [ -z "$_user" ]; then
    _user="ansible"
  fi

  # Create a quick and dirty service user then restart the script as that user
  create_sudo_user "${_user}"
fi

mkdir -p "${HOME}/.local/share/automation"
git clone --recurse-submodules https://github.com/borland502/home-ops.git "${HOME}/.local/share/automation/home-ops"
