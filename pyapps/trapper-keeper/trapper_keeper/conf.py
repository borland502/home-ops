"""Configuration self for Trapper Keeper.

This module defines the configuration self for Trapper Keeper.
"""

import hashlib
import os
from pathlib import Path
from typing import ClassVar
from uuid import UUID

from simple_toml_settings import TOMLSettings
from xdg_base_dirs import (
  xdg_cache_home,
  xdg_config_dirs,
  xdg_config_home,
  xdg_data_dirs,
  xdg_data_home,
  xdg_runtime_dir,
  xdg_state_home,
)


def file_hash(file: Path):
  """Calculate the SHA-256 hash of a file."""
  if file.exists():
    sha256 = hashlib.sha256()
    with open(file, "rb") as f:
      for block in iter(lambda: f.read(4096), b""):
        sha256.update(block)
    return sha256.hexdigest()
  return None

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


  ## XDG Base Directories for system
  xdg_data_home = str(xdg_data_home())
  xdg_config_home = str(xdg_config_home())
  xdg_state_home  = str(xdg_state_home())
  xdg_cache_home = str(xdg_cache_home())
  xdg_runtime_dir = str(xdg_runtime_dir())
  xdg_config_dirs = str(xdg_config_dirs())
  xdg_data_dirs = str(xdg_data_dirs())

  home = str(Path.home())
  ansible_home = str(Path.home() / ".ansible")
  ssh_home = str(Path.home() / ".ssh")
  sshconfig_home = f"{xdg_config_home}/sshconfig"

  ## KeeShare of BOOTSTRAP group
  bootstrap_db = f"{xdg_data_home}/{__section__}/bootstrap.kdbx"
  bootstrap_token = f"{xdg_config_home}/{__section__}/bootstrap.token"
  bootstrap_config = f"{xdg_state_home}/{__section__}/config.toml"

  src_db = f"{xdg_data_home}/keepass/secrets.kdbx"
  src_key = f"{xdg_state_home}/keepass/secrets.keyx"
  src_token = f"{xdg_config_home}/keepass/secrets.token"
  src_yubikey = f"{xdg_config_home}/keepass/yubikey.serial"

  # Resource UUIDs
  export_entry_pairs: ClassVar[dict[str,tuple[UUID,UUID]]] = {
    # The first entry is always the group that will contain the index of essential entries
    "BOOTSTRAP": (bootstrap_uuid, bootstrap_entry)
  }

  # Files to export from source.  The index will serve as the index for the target
  # keepass binary attachments index (target assumed to be new each time)
  src_files: ClassVar[dict[UUID, list[Path]]] = {
    bootstrap_entry: [
      f"{ssh_home}/config",
      f"{ssh_home}/id_ed25519",
      f"{ssh_home}/id_ed25519.pub",
      f"{home}/.npmrc",
      f"{xdg_config_home}/chezmoi/chezmoistate.boltdb"
    ]
  }

  src_dirs: ClassVar[dict[UUID, list[Path]]] = {
    bootstrap_entry: [
      f"{ansible_home}/inventory"
    ]
  }

  # TODO: trim these down to just the essentials
  src_env: ClassVar[dict[str, str]] = dict(os.environ)

  # Target Keepass database
  db = f"{xdg_data_home}/{__section__}/secrets.kdbx"
  key = f"{xdg_state_home}/{__section__}/secrets.keyx"
  token = f"{xdg_config_home}/{__section__}/secrets.token"

class TgtSettings(TkSettings):
  """Target Config file to create."""
