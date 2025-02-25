#!/usr/bin/env bash

echo "Starting Home-Ops Installation Script${RESET} with $(whoami) in $(pwd)"
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
# Catch the re-run of the script in non-root mode
source "${SDKMAN_DIR}/bin/sdkman-init.sh"
source "${NVM_DIR}/nvm.sh"
eval "$(pyenv init -)"

if [[ -d "/home/linuxbrew/.linuxbrew" ]]; then
    sudo chown "${USER}:${USER}" -R "/home/linuxbrew/.linuxbrew" 2>/dev/null
fi

echo "${GREEN}Default path set to ${DEFAULT_PATH}${RESET}"
# shellcheck disable=SC2148

echo "Running install_doctor_functions.sh"

function ensure_permissions() {
    if [ -n "$CI" ]; then
        if type sudo &>/dev/null; then
            sudo chown -R "${USER}:${USER}" "${HOME}"
        fi
    fi
}

# @description Acquire unique ID for this script
function set_script_id() {
    if [ -z "$CI" ]; then
        if type md5sum &>/dev/null; then
            FILE_HASH="$(md5sum "$0" | sed 's/\s.*$//')"
        else
            FILE_HASH="$(date -r "$0" +%s)"
        fi
    else
        FILE_HASH="none"
    fi
}

# @description Caches values from commands
function cache() {
    local DIR="${CACHE_DIR:-.cache}"
    if ! test -d "$DIR"; then
        mkdir -p "$DIR"
    fi
    local FN="$DIR/${LINENO}-${FILE_HASH}"
    if ! test -f "$FN"; then
        "$@" >"$FN"
    fi
    cat "$FN"
}

# @description Formats log statements
function format() {
    # shellcheck disable=SC2001,SC2016
    ANSI_STR="$(echo "$1" | sed 's/^\([^`]*\)`\([^`]*\)`/\1\\e[100;1m \2 \\e[0;39m/')"
    if [[ $ANSI_STR == *'`'*'`'* ]]; then
        ANSI_STR="$(format "$ANSI_STR")"
    fi
    echo -e "$ANSI_STR"
}

# @description Proxy function for handling logs in this script
function logger() {
    local LOG_LEVEL="$1:='info'"
    if [ "$LOG_LEVEL" == 'error' ]; then
        echo -e "\e[1;41m  ERROR   \e[0m $(format "$2")\e[0;39m"
        elif [ "$LOG_LEVEL" == 'info' ]; then
        echo -e "\e[1;46m   INFO   \e[0m $(format "$2")\e[0;39m"
        elif [ "$LOG_LEVEL" == 'success' ]; then
        echo -e "\e[1;42m SUCCESS  \e[0m $(format "$2")\e[0;39m"
        elif [ "$LOG_LEVEL" == 'warn' ]; then
        echo -e "\e[1;43m WARNING  \e[0m $(format "$2")\e[0;39m"
    else
        echo "$*" >&2
    fi
}

# @description Helper function for ensurePackageInstalled for Alpine installations
function ensureAlpinePackageInstalled() {
    if type sudo &>/dev/null && [ "$CAN_USE_SUDO" != 'false' ]; then
        sudo apk --no-cache add "$1"
    else
        apk --no-cache add "$1"
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

# @description Helper function for ensurePackageInstalled for RedHat installations
function ensureRedHatPackageInstalled() {
    if type sudo &>/dev/null && [ "$CAN_USE_SUDO" != 'false' ]; then
        if type dnf &>/dev/null; then
            sudo dnf install -y "$1"
        else
            sudo yum install -y "$1"
        fi
    else
        if type dnf &>/dev/null; then
            dnf install -y "$1"
        else
            yum install -y "$1"
        fi
    fi
}

# @description Installs package when user is root on Linux
#
# @arg $1 string The name of the package that must be present
#
# @exitcode 0 The package was successfully installed
# @exitcode 1+ If there was an error, the package needs to be installed manually, or if the OS is unsupported
function ensureRootPackageInstalled() {
    export CAN_USE_SUDO='true'
    if ! type "$1" &>/dev/null; then
        if [[ "$OSTYPE" == 'linux'* ]]; then
            if [ -f "/etc/redhat-release" ]; then
                ensureRedHatPackageInstalled "$1"
                elif [ -f "/etc/debian_version" ]; then
                ensureDebianPackageInstalled "$1"
                elif [ -f "/etc/arch-release" ]; then
                ensureArchPackageInstalled "$1"
                elif [ -f "/etc/alpine-release" ]; then
                ensureAlpinePackageInstalled "$1"
            fi
        fi
    fi
}

# @description If the user is running this script as root, then create a new user
# and restart the script with that user. This is required because Homebrew
# can only be invoked by non-root users.
function create_sudo_user() {
    local _username=${1:-'ansible'}
    
    # shellcheck disable=SC2016
    if [ "$USER" != 'root' ]; then
        logger info "Not running as root, so not creating a new user"
    else
        echo "${_username} ALL=(ALL) NOPASSWD: ALL" >>/etc/sudoers
        useradd --create-home --shell "$(which zsh)" "${_username}" || ROOT_EXIT_CODE=$?
        chown -R "${_username}:${_username}" /home/"${_username}"
        if [ -n "$ROOT_EXIT_CODE" ]; then
            # shellcheck disable=SC2016
            logger info "User ${_username} already exists"
        fi
        
        # shellcheck disable=SC2016
        logger info "Reloading the script with the ${_username} user"
    fi
    
    # shellcheck disable=SC2016
    mkdir -p "/home/${_username}/.local/bin"
    cp "$0" "/home/${_username}/.local/bin"
    chown "${_username}:${_username}" "/home/${_username}/.local/bin/$(basename "$0")"
    exec su - "${_username}" "/home/${_username}/.local/bin/$(basename "$0")" -- "$@"
}

# @description Ensures ~/.local/bin is in the PATH variable on *nix machines and
# exits with an error on unsupported OS types
#
# @set PATH string The updated PATH with a reference to ~/.local/bin
#
# @noarg
#
# @exitcode 0 If the PATH was appropriately updated or did not need updating
# @exitcode 1+ If the OS is unsupported
function ensureLocalPath() {
    if [[ "$OSTYPE" == 'darwin'* ]] || [[ "$OSTYPE" == 'linux'* ]]; then
        # shellcheck disable=SC2016
        PATH_STRING='export PATH="$HOME/.local/bin:$PATH"'
        mkdir -p "$HOME/.local/bin"
        mkdir -p "${HOME}/.local/lib"
        
        if ! grep "$PATH_STRING" <"$HOME/.profile" >/dev/null; then
            echo -e "${PATH_STRING}\n" >>"$HOME/.profile"
            logger info "Updated the PATH variable to include ~/.local/bin in $HOME/.profile"
        fi
        elif [[ "$OSTYPE" == 'cygwin' ]] || [[ "$OSTYPE" == 'msys' ]] || [[ "$OSTYPE" == 'win32' ]]; then
        logger error "Windows is not directly supported. Use WSL or Docker." && exit 1
        elif [[ "$OSTYPE" == 'freebsd'* ]]; then
        logger error "FreeBSD support not added yet" && exit 1
    else
        logger warn "System type not recognized"
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
    if ! type "$1" &>/dev/null; then
        if [[ "$OSTYPE" == 'darwin'* ]]; then
            brew install "$1"
            elif [[ "$OSTYPE" == 'linux'* ]]; then
            if [ -f "/etc/redhat-release" ]; then
                ensureRedHatPackageInstalled "$1"
                elif [ -f "/etc/debian_version" ]; then
                ensureDebianPackageInstalled "$1"
                elif [ -f "/etc/arch-release" ]; then
                ensureArchPackageInstalled "$1"
                elif [ -f "/etc/alpine-release" ]; then
                ensureAlpinePackageInstalled "$1"
                elif type dnf &>/dev/null || type yum &>/dev/null; then
                ensureRedHatPackageInstalled "$1"
                elif type apt-get &>/dev/null; then
                ensureDebianPackageInstalled "$1"
                elif type pacman &>/dev/null; then
                ensureArchPackageInstalled "$1"
                elif type apk &>/dev/null; then
                ensureAlpinePackageInstalled "$1"
            else
                logger error "$1 is missing. Please install $1 to continue." && exit 1
            fi
            elif [[ "$OSTYPE" == 'cygwin' ]] || [[ "$OSTYPE" == 'msys' ]] || [[ "$OSTYPE" == 'win32' ]]; then
            logger error "Windows is not directly supported. Use WSL or Docker." && exit 1
            elif [[ "$OSTYPE" == 'freebsd'* ]]; then
            logger error "FreeBSD support not added yet" && exit 1
        else
            logger error "System type not recognized"
        fi
    fi
}

# @description Helper function for ensureTaskInstalled that performs the installation of Task.
#
# @see ensureTaskInstalled
#
# @noarg
#
# @exitcode 0 If Task installs/updates properly
# @exitcode 1+ If the installation fails
function installTask() {
    # @description Release URL to use when downloading [Task](https://github.com/go-task/task)
    TASK_RELEASE_URL="https://github.com/go-task/task/releases/latest"
    CHECKSUM_DESTINATION=/tmp/megabytelabs/task_checksums.txt
    CHECKSUMS_URL="$TASK_RELEASE_URL/download/task_checksums.txt"
    DOWNLOAD_DESTINATION=/tmp/megabytelabs/task.tar.gz
    TMP_DIR=/tmp/megabytelabs
    logger info "Checking if install target is macOS or Linux"
    if [[ "$OSTYPE" == 'darwin'* ]]; then
        DOWNLOAD_URL="$TASK_RELEASE_URL/download/task_darwin_amd64.tar.gz"
    else
        DOWNLOAD_URL="$TASK_RELEASE_URL/download/task_linux_amd64.tar.gz"
    fi
    logger info "Creating folder for Task download"
    mkdir -p "$(dirname "$DOWNLOAD_DESTINATION")"
    logger info "Downloading latest version of Task"
    curl -sSL "$DOWNLOAD_URL" -o "$DOWNLOAD_DESTINATION"
    curl -sSL "$CHECKSUMS_URL" -o "$CHECKSUM_DESTINATION"
    DOWNLOAD_BASENAME="$(basename "$DOWNLOAD_URL")"
    DOWNLOAD_SHA256="$(grep "$DOWNLOAD_BASENAME" <"$CHECKSUM_DESTINATION" | cut -d ' ' -f 1)"
    sha256 "$DOWNLOAD_DESTINATION" "$DOWNLOAD_SHA256" >/dev/null
    logger success "Validated checksum"
    mkdir -p "$TMP_DIR/task"
    tar -xzf "$DOWNLOAD_DESTINATION" -C "$TMP_DIR/task" >/dev/null
    if type task &>/dev/null && [ -w "$(which task)" ]; then
        TARGET_BIN_DIR="."
        TARGET_DEST="$(which task)"
    else
        if [ "$USER" == "root" ] || (type sudo &>/dev/null && sudo -n true); then
            TARGET_BIN_DIR='/usr/local/bin'
        else
            TARGET_BIN_DIR="$HOME/.local/bin"
        fi
        TARGET_DEST="$TARGET_BIN_DIR/task"
    fi
    if [ "$USER" == "root" ]; then
        mkdir -p "$TARGET_BIN_DIR"
        mv "$TMP_DIR/task/task" "$TARGET_DEST"
        elif type sudo &>/dev/null && sudo -n true; then
        sudo mkdir -p "$TARGET_BIN_DIR"
        sudo mv "$TMP_DIR/task/task" "$TARGET_DEST"
    else
        mkdir -p "$TARGET_BIN_DIR"
        mv "$TMP_DIR/task/task" "$TARGET_DEST"
    fi
    logger success "Installed Task to $TARGET_DEST"
    rm "$CHECKSUM_DESTINATION"
    rm "$DOWNLOAD_DESTINATION"
}

# @description Verifies the SHA256 checksum of a file
#
# @arg $1 string Path to the file
# @arg $2 string The SHA256 signature
#
# @exitcode 0 The checksum is valid or the system is unrecognized
# @exitcode 1+ The OS is unsupported or if the checksum is invalid
function sha256() {
    if [[ "$OSTYPE" == 'darwin'* ]]; then
        if type brew &>/dev/null && ! type sha256sum &>/dev/null; then
            brew install coreutils
        else
            logger warn "Brew is not installed - this may cause issues"
        fi
        if type brew &>/dev/null; then
            PATH="$(brew --prefix)/opt/coreutils/libexec/gnubin:$PATH"
        fi
        if type sha256sum &>/dev/null; then
            echo "$2 $1" | sha256sum -c
        else
            logger warn "Checksum validation is being skipped for $1 because the sha256sum program is not available"
        fi
        elif [[ "$OSTYPE" == 'linux-gnu'* ]]; then
        if ! type shasum &>/dev/null; then
            logger warn "Checksum validation is being skipped for $1 because the shasum program is not installed"
        else
            echo "$2  $1" | shasum -s -a 256 -c
        fi
        elif [[ "$OSTYPE" == 'linux-musl' ]]; then
        if ! type sha256sum &>/dev/null; then
            logger warn "Checksum validation is being skipped for $1 because the sha256sum program is not available"
        else
            echo "$2  $1" | sha256sum -c
        fi
        elif [[ "$OSTYPE" == 'cygwin' ]] || [[ "$OSTYPE" == 'msys' ]] || [[ "$OSTYPE" == 'win32' ]]; then
        logger error "Windows is not directly supported. Use WSL or Docker." && exit 1
        elif [[ "$OSTYPE" == 'freebsd'* ]]; then
        logger error "FreeBSD support not added yet" && exit 1
    else
        logger warn "System type not recognized. Skipping checksum validation."
    fi
}

##### Main Logic #####
function bootstrap_ansible_node() {
    local _username=${1:-'ansible'}
    
    logger info "Running as $(whoami)"
    
    if [ ! -f "$HOME/.profile" ]; then
        touch "$HOME/.profile"
    fi
    
    # @description Ensure git hosts are all in ~/.ssh/known_hosts
    mkdir -p ~/.ssh
    chmod 700 ~/.ssh
    if [ ! -f ~/.ssh/known_hosts ]; then
        touch ~/.ssh/known_hosts
        chmod 600 ~/.ssh/known_hosts
    fi
    
    if ! grep -q "^gitlab.com " ~/.ssh/known_hosts; then
        ssh-keyscan gitlab.com >>~/.ssh/known_hosts 2>/dev/null
    fi
    
    if ! grep -q "^github.com " ~/.ssh/known_hosts; then
        ssh-keyscan github.com >>~/.ssh/known_hosts 2>/dev/null
    fi
    
    if ! grep -q "^bitbucket.org " ~/.ssh/known_hosts; then
        ssh-keyscan bitbucket.org >>~/.ssh/known_hosts 2>/dev/null
    fi
    
    # @description Ensures ~/.local/bin is in PATH
    ensureLocalPath
    
    # @description Ensures base dependencies are installed
    if [[ "$OSTYPE" == 'darwin'* ]]; then
        if ! type curl &>/dev/null && type brew &>/dev/null; then
            brew install curl
        fi
        if ! type git &>/dev/null; then
            # shellcheck disable=SC2016
            logger info 'Git is not present. A password may be required to run sudo xcode-select --install'
            sudo xcode-select --install
        fi
        elif [[ "$OSTYPE" == 'linux-gnu'* ]] || [[ "$OSTYPE" == 'linux-musl'* ]]; then
        if ! type curl &>/dev/null || ! type git &>/dev/null || ! type gzip &>/dev/null || ! type sudo &>/dev/null || ! type jq &>/dev/null; then
            ensurePackageInstalled "curl"
            ensurePackageInstalled "file"
            ensurePackageInstalled "git"
            ensurePackageInstalled "gzip"
            ensurePackageInstalled "sudo"
            ensurePackageInstalled "jq"
            ensurePackageInstalled "rsync"
        fi
    fi
    
    # @description Ensures Homebrew, Poetry, and Volta are installed
    if [ -z "$NO_INSTALL_HOMEBREW" ]; then
        if [[ "$OSTYPE" == 'darwin'* ]] || [[ "$OSTYPE" == 'linux-gnu'* ]] || [[ "$OSTYPE" == 'linux-musl'* ]]; then
            if [ -z "$INIT_CWD" ]; then
                if ! type brew &>/dev/null; then
                    if type sudo &>/dev/null && sudo -n true; then
                        
                        echo | /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
                    else
                        logger warn "Homebrew is not installed. The script will attempt to install Homebrew and you might be prompted for your password."
                        /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)" || BREW_EXIT_CODE="$?"
                        
                        if [ -n "$BREW_EXIT_CODE" ]; then
                            if command -v brew >/dev/null; then
                                echo "Homebrew was installed but part of the installation failed. Retrying again after changing a few things.."
                                
                                BREW_DIRS="share/man share/doc share/zsh/site-functions etc/bash_completion.d"
                                for BREW_DIR in $BREW_DIRS; do
                                    if [ -d "$(brew --prefix)/$BREW_DIR" ]; then
                                        sudo chown -R "${USER}" "$(brew --prefix)/$BREW_DIR"
                                        
                                    fi
                                done
                                brew update --force --quiet
                            fi
                        fi
                    fi
                fi
                if ! (grep "/bin/brew shellenv" <"$HOME/.profile" &>/dev/null) && [[ "$OSTYPE" != 'darwin'* ]]; then
                    
                    # shellcheck disable=SC2016
                    logger info 'Adding linuxbrew source command to ~/.profile'
                    
                    if [ -d "/home/linuxbrew/.linuxbrew" ]; then
                        echo 'eval "$(/home/linuxbrew/.linuxbrew/bin/brew shellenv)"' >>"$HOME/.profile"
                        sudo chown -R $USER:$USER /home/linuxbrew/.linuxbrew/Cellar
                        elif [ -d "$HOME/.linuxbrew" ]; then
                        echo 'eval "$($HOME/.linuxbrew/bin/brew shellenv)"' >>"$HOME/.profile"
                        elif [ -d "/opt/homebrew" ]; then
                        echo 'eval "$(/opt/homebrew/bin/brew shellenv)"' >>"$HOME/.profile"
                    fi
                    
                fi
                
                if [ -f "$HOME/.profile" ]; then
                    # shellcheck disable=SC1091
                    . "$HOME/.profile" &>/dev/null || true
                fi
                if ! type poetry &>/dev/null; then
                    # shellcheck disable=SC2016
                    brew install poetry || logger info 'There may have been an issue installing poetry with brew'
                fi
                if ! type jq &>/dev/null; then
                    # shellcheck disable=SC2016
                    brew install jq || logger info 'There may have been an issue installiny jq with brew'
                fi
                if ! type yq &>/dev/null; then
                    # shellcheck disable=SC2016
                    brew install yq || logger info 'There may have been an issue installing yq with brew'
                fi
            fi
        fi
    fi
    
}
# shellcheck disable=SC2148

declare -rx GH_RAW="https://raw.githubusercontent.com/borland502"
declare -rx GH_PROJ="https://github.com/borland502/home-ops"


if [[ "${USER}" == "root" ]]; then
    echo "Running as root, creating a non-root user to run home-ops"
    NEWUSER=${1:-"ansible"}
    
    ensureRootPackageInstalled git
    ensureRootPackageInstalled zip
    ensureRootPackageInstalled curl
    ensureRootPackageInstalled zsh
    ensureRootPackageInstalled sudo
    
    create_sudo_user "${NEWUSER}"
    installTask
    if ! command -v task &> /dev/null; then
        echo "Task could not be found, please install it."
        exit 1
    fi
    # Bootstrap this host for ansible
    bootstrap_ansible_node "${NEWUSER}"
    
fi

ensurePackageInstalled git
ensurePackageInstalled curl
ensurePackageInstalled rsync
ensurePackageInstalled npm
ensurePackageInstalled gcc
ensurePackageInstalled zip
ensurePackageInstalled keepassxc
ensurePackageInstalled rclone

mkdir -p "${HOME}/.local/share/automation"
if [ ! -d "${HOME}/.local/share/automation/home-ops" ]; then
    git clone --recurse-submodules https://github.com/borland502/home-ops.git "${HOME}/.local/share/automation/home-ops"
fi
cd "${HOME}/.local/share/automation/home-ops" || exit 2
#!/usr/bin/env bash

#########
# This script carries the installation forward into the userspace with an assumption
# of homebrew availability (Homebrew/Linuxbrew) before chezmoi is configured

# Check if running as root and switch to user if needed
if [[ $USER == root ]]; then
    mkdir -p "/home/${_username}/.local/bin"
    cp "$0" "/home/${_username}/.local/bin"
    chown "${_username}:${_username}" "/home/${_username}/.local/bin/$(basename "$0")"
    exec su - "${_username}" "/home/${_username}/.local/bin/$(basename "$0")" -- "$@"
fi

if ! [[ -f ~/.zshrc ]]; then
    cp ../../../config/default.zshrc ~/.zshrc
fi

if ! [[ $(command -v age) ]]; then
    brew install age
fi

# Check if zsh is already the default shell
if ! [[ $(command -v zsh) ]]; then
    # Change the default shell to zsh
    brew install zsh
    sudo echo '/home/linuxbrew/.linuxbrew/bin/zsh' | sudo tee -a /etc/shells
    chsh -s '/home/linuxbrew/.linuxbrew/bin/zsh'
    echo "Default shell changed to zsh."
    
    # export the path again for the new shell
    declare -x PATH="${DEFAULT_PATH}"
fi

# --- sdkman Installation ---
if ! command -v sdk > /dev/null; then  # Check if sdkman is already installed
    brew install zip
    export SDKMAN_DIR="${HOME}/.local/share/sdkman" && curl -s "https://get.sdkman.io" | bash
    
    if [[ -d "${HOME}/.sdkman" ]] && ! [[ -d "${SDKMAN_DIR}" ]]; then
        mv "${HOME}/.sdkman/" "${HOME}/.local/share/sdkman/"
    fi
    
    # Add sdkman initialization to .zshrc (important!)
    # shellcheck disable=SC2016
    echo 'source "${SDKMAN_DIR}/bin/sdkman-init.sh"' >> ~/.zshrc
    source "${SDKMAN_DIR}/bin/sdkman-init.sh" # Source for current shell
    
    sdk install java
fi

# --- pyenv Installation ---
brew install pyenv
pyenv install "${PYTHON_VERSION}"

# --- Taskfile.dev Installation ---
brew install go-task

# --- nvm Installation ---
source "${HOME}/.local/share/nvm/nvm.sh" 2>/dev/null
brew install nvm
nvm install --lts

# activate envs for home-ops
pyenv global "$PYTHON_VERSION"

# --- chezmoi Installation ---
brew install chezmoi
chezmoi init --source "${HOME}/.local/share/automation/home-ops/scripts/dotfiles"
chezmoi apply --source "${HOME}/.local/share/automation/home-ops/scripts/dotfiles"

echo "Userspace installation complete."

# shellcheck disable=SC2148

echo "Starting home-ops_install.sh with $(whoami) in $(pwd)"

# install packages for home-ops
PREV_DIR=$(pwd)

if ! [[ -f ~/.config/home-ops/default.toml ]]; then
    mkdir -p "${HOME}/.config/home-ops"
    cp ./config/default.toml ~/.config/home-ops/default.toml
fi

task homeops:init
