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
export PATH="/bin:/sbin:/usr/bin:/usr/sbin:/usr/local/bin:/usr/local/sbin:/home/linuxbrew/.linuxbrew/bin:/opt/homebrew/bin"
export PATH="${PATH}:${HOME}/.linuxbrew/bin:${HOME}/.linuxbrew/sbin"
export PATH="${PATH}:${HOME}/.local/share/sdkman/bin:${HOME}/.local/share/pyenv:${HOME}/.local/share/nvm"
export PATH="${PATH}:${HOME}/.local/bin"

if ! [[ $(command -v sdk) ]]; then
  curl -s "https://get.sdkman.io" | bash
fi

source "${HOME}/.local/share/sdkman/bin/sdkman-init.sh"

if ! [[ $(command -v pyenv) ]]; then
  curl -fsSL https://pyenv.run | bash
fi

eval "$(pyenv init --path)"

if ! [[ -d "${NVM_DIR}" ]]; then
  mkdir -p "${NVM_DIR}"
  curl -o- "https://raw.githubusercontent.com/nvm-sh/nvm/v0.40.1/install.sh" | bash
fi

[ -s "$NVM_DIR/nvm.sh" ] && \. "$NVM_DIR/nvm.sh"  # This loads nvm
[ -s "$NVM_DIR/bash_completion" ] && \. "$NVM_DIR/bash_completion"  # This loads nvm bash_completion
