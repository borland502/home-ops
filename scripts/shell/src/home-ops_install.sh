# shellcheck disable=SC2148

echo "Starting home-ops_install.sh with $(whoami) in $(pwd)"

# install packages for home-ops
PREV_DIR=$(pwd)

if ! [[ -f ~/.config/home-ops/default.toml ]]; then
    mkdir -p "${HOME}/.config/home-ops"
    cp ./config/default.toml ~/.config/home-ops/default.toml
fi

# spring shell
cd ./scripts/spring-cli || exit 2
sdk env install
./gradlew clean bootJar
cd "${PREV_DIR}" || exit 2


cd ./scripts/zx || exit 2
brew install oven-sh/bun/bun
nvm use
brew install bun
bun install
cd "${PREV_DIR}" || exit 2
