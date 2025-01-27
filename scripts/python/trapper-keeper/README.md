# trapper-keeper

# Trapper Keeper
## Overview
Trapper Keeper aims to be a thin wrapper over KeePassXC using PyKeepass for the niche goal of packing various
environment variables, ssh keys, and other sensitive artifacts into the password manager to then unpack at a
destination vm/lxc/docker image.

### Chezmoi Adjacent
I love [chezmoi](https://www.chezmoi.io), which handles this goal with more encryption options and with better
instructions.  If you are really industrious, you can integrate with 1password or self-hosted bitwarden instances.
But simplicity in secrets management is key for me. Most TK operations are straight from the [PyKeepass](https://pykeepass.readthedocs.io/en/latest/)
library and the wrapped TK database binary will be accessible from the various KeePassXC [clients](https://github.com/lgg/awesome-keepass).
TK does not aim to alter any object it stores or the location where that object is expected


## Links
* [KeePassXC](https://keepassxc.org)
* [PyKeepass](https://pykeepass.readthedocs.io/en/latest/)
* [XDG Specification](https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html)
* [xdg-base-dirs library](https://github.com/srstevenson/xdg-base-dirs)
* [fire](https://google.github.io/python-fire/guide/)

## Commands

### Pack

This will create any files which are missing as well as the Keepass database itself.  The user is expected to supply
only a password token `~/.local/state/keepass_token` and a key `~/.config/trapper_keeper/key.txt`.  The key can be
anything at all so long as it never changes.

```shell
task pya:tk:run -- pack
```

or

```shell
poetry run python -m trapper_keeper pack
```

```console
NAME
    __main__.py - TrapperKeeper CLI.

SYNOPSIS
    __main__.py COMMAND | -

DESCRIPTION
    A command-line interface (CLI) for managing key/value pairs in the Trapper Keeper.

    Methods:
    -------
    __init__():
        Initialize the Trapper Keeper CLI.

    add(key: str, value: str):
        Add a key/value pair to the Trapper Keeper.

    get(key: str):
        Get a value from the Trapper Keeper.

    remove(key: str):
        Remove a key/value pair from the Trapper Keeper.

    update(key: str, value: str):
        Update a key/value pair in the Trapper Keeper.

    unpack():
        Unpack the Trapper Keeper.

    passphrase(length: int = 5):
        Generate a random passphrase.

    gen_key(file: str, length: int = 64):
        Generate a random key.

    pack():
        Pack the Trapper Keeper.

COMMANDS
    COMMAND is one of the following:

     passphrase
       Generate a random passphrase.

     save_credential
       Save a credential to a file.
```
