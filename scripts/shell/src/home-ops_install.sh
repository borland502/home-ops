# shellcheck disable=SC2148

echo "Starting home-ops_install.sh with $(whoami) in $(pwd)"

# install packages for home-ops
PREV_DIR=$(pwd)

if ! [[ -f ~/.config/home-ops/default.toml ]]; then
    mkdir -p "${HOME}/.config/home-ops"
    cp ./config/default.toml ~/.config/home-ops/default.toml
fi

task homeops:init
