#!/usr/bin/env bash

declare -x XDG_LIB_HOME="${XDG_LIB_HOME:-$HOME/.local/lib}"
declare -x XDG_DATA_HOME="${XDG_DATA_HOME:-$HOME/.local/share}"
declare -x XDG_CONFIG_HOME="${XDG_CONFIG_HOME:-$HOME/.config}"
declare -x XDG_CACHE_HOME="${XDG_CACHE_HOME:-$HOME/.cache}"
declare -x XDG_STATE_HOME="${XDG_STATE_HOME:-$HOME/.local/state}"
declare -x XDG_BIN_HOME="${XDG_BIN_HOME:-$HOME/.local/bin}"
