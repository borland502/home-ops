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

