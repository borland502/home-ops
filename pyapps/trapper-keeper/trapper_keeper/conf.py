"""Configuration self for Trapper Keeper.

This module defines the configuration self for Trapper Keeper.
"""

import hashlib
import os
import pathlib
from pathlib import Path
from uuid import UUID

from simple_toml_settings import TOMLSettings
from utils.file import pathify, stringify_path
from xdg_base_dirs import xdg_config_home, xdg_data_home, xdg_state_home, xdg_cache_home, \
  xdg_runtime_dir, xdg_config_dirs, xdg_data_dirs


def file_hash(self):
  """Calculate the SHA-256 hash of a file."""
  if Path(self).exists():
    sha256 = hashlib.sha256()
    with open(self, "rb") as f:
      for block in iter(lambda: f.read(4096), b""):
        sha256.update(block)
    return sha256.hexdigest()
  return None

class TkSettings(TOMLSettings):
  """Common settings for Trapper Keeper."""
  __file__ = "config.toml"
  __section__ = "trapper_keeper"

  # XDG Base Directories for system
  xdg_data_home: str = str(xdg_data_home())
  xdg_config_home: str = str(xdg_config_home())
  xdg_state_home: str  = str(xdg_state_home())
  xdg_cache_home: str = str(xdg_cache_home())
  xdg_runtime_dir: str = str(xdg_runtime_dir())
  xdg_config_dirs: str = str(xdg_config_dirs())
  xdg_data_dirs: str = str(xdg_data_dirs())

  home = str(Path.home())
  ansible_home: str = str(Path.home() / ".ansible")
  ssh_home: str = str(Path.home() / ".ssh")
  sshconfig_home: str = f"{xdg_config_home}/sshconfig"

  # KeeShare of BOOTSTRAP group
  bootstrap_db: str = f"{xdg_data_home}/{__section__}/bootstrap.kdbx"
  bootstrap_token: str = f"{xdg_config_home}/{__section__}/bootstrap.token"

  src_db: str = f"{xdg_data_home}/keepass/secrets.kdbx"
  src_key: str = f"{xdg_state_home}/keepass/secrets.keyx"
  src_token: str = f"{xdg_config_home}/keepass/secrets.token"
  src_yubikey: str = f"{xdg_config_home}/keepass/yubikey.serial"

  # Resource UUIDs
  export_entry_pairs: dict[str,tuple[UUID,UUID]] = {
    # The first entry is always the group that will contain the index of essential entries
    "BOOTSTRAP": ("A9906776-8CEB-4044-8D65-97B5A93920DD", "36EF1A2C-773E-4F9D-AE8A-07B21E21C317")
  }

  # Files to export from source.  The index will serve as the index for the target
  # keepass binary attachments index (target assumed to be new each time)
  src_files: dict[UUID, list[Path]] = {
    "36EF1A2C-773E-4F9D-AE8A-07B21E21C317": [
      f"{ansible_home}/inventory/microsoft.ad.ldap.yaml",
      f"{ansible_home}/inventory/nmap.yaml",
      f"{ansible_home}/inventory/proxmox.yaml",
      f"{ansible_home}/inventory/sqlite.yaml",
      f"{ansible_home}/inventory/group_vars/all.yaml",
      f"{sshconfig_home}/hosts.conf",
      f"{sshconfig_home}/networks.conf",
      f"{sshconfig_home}/ssh.conf",
      f"{ssh_home}/config",
      f"{ssh_home}/id_ed25519",
      f"{ssh_home}/id_ed25519.pub",
      f"{home}/.npmrc",
    ]
  }

  # TODO: trim these down to just the essentials
  src_env: dict[str, str] = dict(os.environ)

  # Target Keepass database
  db: str = f"{xdg_data_home}/{__section__}/secrets.kdbx"
  key: str = f"{xdg_state_home}/{__section__}/secrets.keyx"
  token: str = f"{xdg_config_home}/{__section__}/secrets.token"



  # Properties to associate with an entry
  # properties: ClassVar[dict[UUID, dict[UUID, Path]]] = {
  #   "36EF1A2C-773E-4F9D-AE8A-07B21E21C317": [{
  #     "C80DC821-D9FE-49E0-869E-1AD72B769048": ".ssh/id_ed25519",
  #     "BED0E69E-96B1-4D66-8FB5-38358728ED2D": str(xdg_config_home() / "age/age.txt"),
  #   }]
  # }
  #
  # # Attachment UUIDs to associate with binaries
  # attachments: ClassVar[dict[UUID, dict[dict]]] = {
  #   "36EF1A2C-773E-4F9D-AE8A-07B21E21C317": [
  #   {
  #     "UUID": "C80DC821-D9FE-49E0-869E-1AD72B769048",
  #     "BIN_ID": 0,
  #     "NAME": "ssh_key",
  #   },
  #   {
  #     "UUID": "BED0E69E-96B1-4D66-8FB5-38358728ED2D",
  #     "BIN_ID": 1,
  #     "NAME": "age_key",
  #   },
  #   {
  #     "UUID": "4AFBCD17-D11F-407E-8F78-799BD8A3A58D",
  #     "BIN_ID": 2,
  #   }
  # ]}

class TgtSettings(TkSettings):
  """Target Config file to create."""

# class SrcSettings(BaseSettings):
#   """Settings for the source Keepass database. TODO: Read only?"""
#
#
# class TgtSettings(BaseSettings):
#   """Settings for Trapper Keeper."""
#   __file__ = "tk.toml"
#   __section__ = "trapper_keeper"
#
#   db: str = str(xdg_data_home() / __section__ / "secrets.kdbx")
#   key: str = str(xdg_state_home() / __section__ / "secrets.keyx")
#   token: str = str(xdg_config_home() / __section__ / "secrets.token")

# class TkSettings(TgtSettings):
#
#   __file__ = "tk.toml"
#   __section__ = "trapper_keeper"


  # {
  #   "AGE_KEY": "683A891C-3D4F-4711-96CB-6DE1322C29B3",
  #   "INVENTORY_TOKEN": "6EEF202A-4A5A-4B57-BF8D-25C588D1505A",
  #   "CLOUD_TOKEN": "6EEF202A-4A5A-4B57-BF8D-25C588D1505",
  #   "HOMEOPS_TOKEN": "D460C4F4-5447-4B0F-A373-58B519E6A480",
  # }

  # Source Keepass parameters -- always assumed to be a workstation with at least console access
  # db: str = str(xdg_data_home() / "secrets.kdbx")
  # key: str = str(xdg_state_home() / "secrets.keyx")
  # key_hash: str = file_hash(key)
  # token: str = str(xdg_config_home() / "secrets.token")
  # token_hash: str = file_hash(token)
  # yubikey: tuple[int, int] = (2, 19116593)

  # Chezmoi parameters (BoltDB)
  # src_chezmoi_db: str = str(xdg_config_home() / "chezmoi" / "chezmoistate.boltdb")

  # src_kp_share: ClassVar[dict[str, dict[str, str]]] = {
  #   "Inventory": {
  #     "PATH": ".ansible/secrets.kdbx",
  #     "TOKEN": str(xdg_config_home() / "ansible/inventory.token"),
  #     "UUID": "i/C/v7PUQjK1ckcQ/S/YTQ==",
  #   },
  #   "CloudServices": {
  #     "PATH": str(xdg_data_home() / "inventory/secrets.kdbx"),
  #     "TOKEN": str(xdg_config_home() / "cloud/cloud.token"),
  #     "UUID": "cSjEMT8KTcyZtn+fwAYI1Q==",
  #   },
  #   "HomeOps": {
  #     "PATH": str(xdg_data_home() / "automation/secrets.kdbx"),
  #     "TOKEN": str(xdg_config_home() / "automation/automation.token"),
  #     "UUID": "9DZooqWUTzK4iq80uSHcNQ==",
  #   },
  # }

  # tgt_db: str = str(xdg_data_home() / __section__ / "tk.kdbx")
  # tgt_db_hash: str = file_hash(tgt_db)
  # tgt_token: str = str(xdg_config_home() / __section__ / "tk.token")
  # tgt_token_hash: str = file_hash(tgt_token)
  # tgt_key: str = str(xdg_state_home() / __section__ / "tk.key")
  # tgt_key_hash: str = file_hash(tgt_key)

  # groups: ClassVar[dict[str, list[str]]] = {
  #   "CloudServices": ["AWS", "GCP", "CF"],
  #   "Encryption": ["SSH", "GPG", "AGE", "YubiKeys"],
  #   "Inventory": ["Ansible"],
  #   "ShareTokens": [],
  #   "JunkDrawer": [],
  #   "HomeOps": ["Dotfiles"],
  # }



  # SQLite Databases

  # KV Store of env vars
  #
  # def __post_create_hook__(self):
  #   """Post-create hook for TkSettings."""
  #   if not Path(self.get("src_db")).is_file():
  #     print(f"Source Keepass database not found at {self.get('src_db')}")
  #
  #   if not Path(self.get("src_key")).is_file():
  #     print(f"Source Keepass key not found at {self.get('src_key')}")
  #
  #   if not self.get("src_yubikey"):
  #     print("Source YubiKey not found")
