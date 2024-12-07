"""Configuration self for Trapper Keeper.

This module defines the configuration self for Trapper Keeper.
"""

import hashlib
import os
from pathlib import Path
from typing import ClassVar
from uuid import UUID

from resources.configs.baseconf import BaseConfig
from utils.paths import XdgPaths, AnsiblePaths, BasePaths


def file_hash(file: Path):
    """Calculate the SHA-256 hash of a file."""
    if file.exists():
        sha256 = hashlib.sha256()
        with open(file, "rb") as f:
            for block in iter(lambda: f.read(4096), b""):
                sha256.update(block)
        return sha256.hexdigest()
    return None


class TkSettings(BaseConfig):
    """Common settings for Trapper Keeper."""

    __file__ = "config.toml"
    __section__ = "trapper_keeper"

    # Constants
    passphrase_length = 7
    key_length = 128
    encoding = "utf-8"
    user_dir_mode = 0o700
    user_file_mode = 0o600
    default_file_mode = 0o644
    bootstrap_uuid = "A9906776-8CEB-4044-8D65-97B5A93920DD"
    bootstrap_entry = "36EF1A2C-773E-4F9D-AE8A-07B21E21C317"
    default_username = "ansible"

    home = BasePaths.HOME
    ansible_home = AnsiblePaths.ANSIBLE_HOME
    ssh_home = BasePaths.SSH_HOME
    gnupg_home = BasePaths.GNUPG_HOME

    ## KeeShare of BOOTSTRAP group
    bootstrap_db = f"{XdgPaths.XDG_DATA_HOME}/{__section__}/bootstrap.kdbx"
    bootstrap_token = f"{XdgPaths.XDG_CONFIG_HOME}/{__section__}/bootstrap.token"
    bootstrap_config = f"{XdgPaths.XDG_STATE_HOME}/{__section__}/config.toml"

    src_db = f"{XdgPaths.XDG_DATA_HOME}/keepass/secrets.kdbx"
    src_key = f"{XdgPaths.XDG_STATE_HOME}/keepass/secrets.keyx"
    src_token = f"{XdgPaths.XDG_CONFIG_HOME}/keepass/secrets.token"
    src_yubikey = f"{XdgPaths.XDG_CONFIG_HOME}/keepass/yubikey.serial"

    # Resource UUIDs
    export_entry_pairs: ClassVar[dict[str, tuple[UUID, UUID]]] = {
        # The first entry is always the group that will contain the index of essential entries
        "BOOTSTRAP": (bootstrap_uuid, bootstrap_entry)
    }

    # Files to export from source.  The index will serve as the index for the target
    # keepass binary attachments index (target assumed to be new each time)
    src_files: ClassVar[dict[UUID, list[Path]]] = {
        bootstrap_entry: [f"{home}/.npmrc", f"{ansible_home}/ansible.cfg"]
    }

    # TODO: Track file paths for directory files
    src_dirs: ClassVar[dict[UUID, list[Path]]] = {
        bootstrap_entry: [
            f"{ansible_home}/inventory",
            f"{ssh_home}",
            f"{XdgPaths.XDG_CONFIG_HOME}",
        ]
    }

    # TODO: trim these down to just the essentials
    src_env: ClassVar[dict[str, str]] = dict(os.environ)

    # Target Keepass database
    db = f"{XdgPaths.XDG_DATA_HOME}/{__section__}/secrets.kdbx"
    key = f"{XdgPaths.XDG_STATE_HOME}/{__section__}/secrets.keyx"
    token = f"{XdgPaths.XDG_CONFIG_HOME}/{__section__}/secrets.token"


class TgtSettings(TkSettings):
    """Target Config file to create."""
