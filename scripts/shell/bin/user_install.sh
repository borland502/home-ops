#!/usr/bin/env bash

source "${HOME}/.local/share/automation/home-ops/scripts/shell/lib/xdg.sh"

# Python has no 'lts' equivalent
PYTHON_VERSION=3.13

# widen the path to include homebrew binaries
PATH="/bin:/sbin:/usr/bin:/usr/sbin:/usr/local/bin:/usr/local/sbin:/home/linuxbrew/.linuxbrew/bin:/opt/homebrew/bin"

# Check if running as root and switch to user if needed
if [[ $USER == root ]] && [[ -f '/root/.rootfinished' ]]; then
    _username=$(cat /root/.rootfinished)
    exec su - "${_username}" "/home/${_username}/$(basename "$0")"
fi

# Check if zsh is already the default shell
if [[ "$SHELL" == "/usr/bin/zsh" ]]; then
    echo "zsh is already the default shell."
else
    # Change the default shell to zsh
    chsh -s "$(which zsh)"
    echo "Default shell changed to zsh."
fi

# --- sdkman Installation ---
if ! command -v sdk > /dev/null; then  # Check if sdkman is already installed
    curl -s "https://get.sdkman.io" | bash # Install sdkman if not present
    
    # Add sdkman initialization to .zshrc (important!)
    echo 'source "$HOME/.sdkman/bin/sdkman-init.sh"' >> ~/.zshrc
    source "$HOME/.sdkman/bin/sdkman-init.sh" # Source for current shell
    
    sdk install java
fi

# --- pyenv Installation ---
brew install pyenv
pyenv install "${PYTHON_VERSION}"

# --- Taskfile.dev Installation ---
brew install go-task

# --- nvm Installation ---
brew install nvm
nvm install --lts

echo "Userspace installation complete."

