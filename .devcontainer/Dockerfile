# Note: You can use any Debian/Ubuntu based image you want.
FROM --platform=linux/amd64 mcr.microsoft.com/devcontainers/base:bookworm

ENV USERNAME=vscode

# Create the vscode user if it does not already exist
RUN id -u vscode &>/dev/null || sudo useradd -m -s /bin/zsh ${USERNAME} \
  && echo "${USERNAME} ALL=(ALL) NOPASSWD: ALL" > /etc/sudoers.d/${USERNAME} \
  && chmod 0440 /etc/sudoers.d/${USERNAME}

# Set the user to 'vscode'
USER vscode

# Install pyenv dependencies
RUN sudo apt-get update && export DEBIAN_FRONTEND=noninteractive && sudo apt-get -y install --no-install-recommends \
  build-essential \
  libssl-dev \
  zlib1g-dev \
  libbz2-dev \
  libreadline-dev \
  libsqlite3-dev \
  wget \
  curl \
  llvm \
  libncurses5-dev \
  libncursesw5-dev \
  xz-utils \
  tk-dev \
  libffi-dev \
  liblzma-dev \
  git \
  sudo \
  zsh \
  && sudo apt-get clean && sudo rm -rf /var/lib/apt/lists/* \
  && /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)" \
  && ([ -d "/home/linuxbrew/.linuxbrew" ] && echo 'eval "$(/home/linuxbrew/.linuxbrew/bin/brew shellenv)"' >> /home/${USERNAME}/.profile \
  || [ -d "$HOME/.linuxbrew" ] && echo 'eval "$($HOME/.linuxbrew/bin/brew shellenv)"' >> /home/${USERNAME}/.profile \
  || [ -d "/opt/homebrew" ] && echo 'eval "$(/opt/homebrew/bin/brew shellenv)"' >> /home/${USERNAME}/.profile) \
  && eval "$(/home/linuxbrew/.linuxbrew/bin/brew shellenv)" \
  && brew install gcc chezmoi curl

CMD ["sleep", "infinity"]
