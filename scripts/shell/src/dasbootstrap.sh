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
