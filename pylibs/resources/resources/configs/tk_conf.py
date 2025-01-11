"""Configuration self for Trapper Keeper.

This module defines the configuration self for Trapper Keeper.
"""

import os
from pathlib import Path
from typing import ClassVar
from uuid import UUID

from simple_toml_settings import TOMLSettings

from homeops_utils.paths import (
    SecretsPaths,
    export_paths,
)


class TkSettings(TOMLSettings):
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

    # ## KeeShare of BOOTSTRAP group
    bootstrap_db = str(SecretsPaths.BOOTSTRAP_DB)
    bootstrap_token = str(SecretsPaths.BOOTSTRAP_TOKEN)
    bootstrap_config = str(SecretsPaths.BOOTSTRAP_CONFIG)

    src_db = str(SecretsPaths.SRC_DB)
    src_key = str(SecretsPaths.SRC_KEY)
    src_token = str(SecretsPaths.SRC_TOKEN)
    src_yubikey = str(SecretsPaths.SRC_YUBIKEY)

    # Resource UUIDs
    export_entry_pairs: ClassVar[dict[str, tuple[UUID, UUID]]] = {
        # The first entry is always the group that will contain the index of essential entries
        "BOOTSTRAP": (bootstrap_uuid, bootstrap_entry)
    }

    # Files to export from source.  The index will serve as the index for the target
    # keepass binary attachments index (target assumed to be new each time)
    src_files: ClassVar[dict[UUID, list[Path]]] = {
        bootstrap_entry: [str(pathlike) for pathlike in export_paths()]
    }

    # TODO: trim these down to just the essentials
    src_env: ClassVar[dict[str, str]] = dict(os.environ)


class TgtSettings(TkSettings):
    """Target Config file to create."""
