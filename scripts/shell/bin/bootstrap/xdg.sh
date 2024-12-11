#!/bin/bash

# Define XDG directories
XDG_DIRS=(
  "$HOME/.local/{share,state,bin,lib}"
  "$HOME/.config"
  "$HOME/.cache"
)

# Create directories and set permissions
for dir in "${XDG_DIRS[@]}"; do
  mkdir -p "$dir"
  chmod 0700 "$dir"
done

echo "XDG directories created and permissions set to 0700."
