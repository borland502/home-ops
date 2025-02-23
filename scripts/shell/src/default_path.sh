# shellcheck disable=SC2148
# Python has no 'lts' equivalent
export PYTHON_VERSION=3.13
export XDG_DATA_HOME="${HOME}/.local/share"
export XDG_CONFIG_HOME="${HOME}/.config"
export XDG_CACHE_HOME="${HOME}/.cache"
export XDG_STATE_HOME="${HOME}/.local/state"
export XDG_RUNTIME_DIR="${HOME}/.local/run"
export XDG_CONFIG_DIRS="/etc/xdg:${XDG_CONFIG_HOME}"
export XDG_DATA_DIRS="/usr/local/share:/usr/share:${XDG_DATA_HOME}:/var/lib/flatpak/exports/share:${XDG_DATA_HOME}/flatpak/exports/share"
export NVM_DIR="${HOME}/.local/share/nvm"
export SDKMAN_DIR="${HOME}/.local/share/sdkman"
export PYENV_ROOT="${HOME}/.local/share/pyenv"
export DEFAULT_PATH="/bin:/sbin:/usr/bin:/usr/sbin:/usr/local/bin:/usr/local/sbin:/home/linuxbrew/.linuxbrew/bin:/opt/homebrew/bin"
export DEFAULT_PATH="${DEFAULT_PATH}:${HOME}/.local/share/sdkman/bin:${HOME}/.local/share/pyenv:${HOME}/.local/share/nvm"
export DEFAULT_PATH="${DEFAULT_PATH}:${HOME}/.local/bin"

GREEN=$(tput setaf 2)
YELLOW=$(tput setaf 3)
RED=$(tput setaf 1)
CYAN=$(tput setaf 6)
PURPLE=$(tput setaf 5)
LIGHT=$(tput bold)
RESET=$(tput sgr0)

# widen the path to include homebrew binaries -- export so that user is left with homebrew for the
# current session
declare -x PATH="${DEFAULT_PATH}"

# Catch the re-run of the script in non-root mode
source "${SDKMAN_DIR}/bin/sdkman-init.sh" 2>/dev/null
source "${NVM_DIR}/nvm.sh" 2>/dev/null
source "${PYENV_ROOT}/bin/pyenv" 2>/dev/null
source "${HOME}/.local/share/pyenv/versions/${PYTHON_VERSION}/bin/activate" 2>/dev/null

if [[ -d "/home/linuxbrew/.linuxbrew" ]]; then
    sudo chown "${USER}:${USER}" -R "/home/linuxbrew/.linuxbrew" 2>/dev/null
fi

echo "${GREEN}Default path set to ${DEFAULT_PATH}${RESET}"
