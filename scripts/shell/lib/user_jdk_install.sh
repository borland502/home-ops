#!/usr/bin/env bash

if [[ $USER == root ]] && [[ -f '/root/.rootfinished' ]]; then
  # presume sudo powers at this account
  _username="$(cat /root/.rootfinished)"
  exec su - "${_username}" "/home/${_username}/$(basename "$0")"
fi

# install sdkman
curl -s "https://get.sdkman.io" | bash

source "$HOME/.sdkman/bin/sdkman-init.sh"

sdk install java

# MYSELF=$(which "$0" 2>/dev/null)
# [ $? -gt 0 ] && [ -f "$0" ] && MYSELF="./$0"
# java=java
# if test -n "$JAVA_HOME"; then
#   java="$JAVA_HOME/bin/java"
# fi
# # shellcheck disable=SC2093
# exec "$java" -jar "$MYSELF" "$@"
