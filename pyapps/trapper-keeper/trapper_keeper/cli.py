"""Command line interface for trapper-keeper."""

from __future__ import annotations

import contextlib
import getpass
import os
import shutil
import subprocess
import tempfile
from pathlib import Path

from pykeepass import PyKeePass
from utils.file import unpack, pack
from xdg_base_dirs import xdg_cache_home, xdg_config_home, xdg_data_home, xdg_state_home

from .conf import TkSettings, file_hash, TgtSettings
from .keegen import gen_passphrase, gen_utf8
from .stores.keepass_store import create_kp_db, KeepassStore
from .tk import DbTypes, get_store



class TrapperKeeper:
  """TrapperKeeper CLI.
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
  """

  def __init__(self):
    """Initialize the Trapper Keeper CLI.

    Settings will either be loaded or generated if they do not exist.
    """
    self.settings: TkSettings = TkSettings.get_instance("trapper_keeper", xdg_config=True, auto_create=True)
    self.tgt_settings: TgtSettings = TgtSettings.get_instance("trapper_keeper", local_file=True, auto_create=True)

  def backup(self):
    """Backup Trapper Keeper."""
    pass

  def unpack(self):
    """Unpack the Trapper Keeper."""
    pass

  def pack(self):
    """Pack the Trapper Keeper."""
    # Create a random folder in the system temp folder
    temp_dir = tempfile.mkdtemp()
    os.chdir(temp_dir)

    # Define the paths for the new store and credentials
    self.tgt_settings.set("db", f"{temp_dir}/secrets.kdbx", autosave=True)
    self.tgt_settings.set("key", f"{temp_dir}/secrets.key", autosave=True)
    self.tgt_settings.set("token", f"{temp_dir}/secrets.token", autosave=True)

    self.gen_key(length=128, key=Path(self.tgt_settings.get("key")))
    self.passphrase(length=7, token=Path(self.tgt_settings.get("token")))
    create_kp_db(kp_fp=Path(self.tgt_settings.get("db")), kp_token=Path(self.tgt_settings.get("token")), kp_key=Path(self.tgt_settings.get("key")))

    pack_dir = Path(temp_dir)
    pack(src_dir=pack_dir, out_file=pack_dir / "trapper_keeper.zst")


  # def _validate_or_create_export_binaries(self, kpdb: KeepassStore):
  #   kpdb.

  @staticmethod
  def gen_key(length: int = 128, key: Path | None = None):
    """Generate a random key."""
    generated_key: str = gen_utf8(length)
    if key:
      key.parent.mkdir(parents=True, exist_ok=True)
      key.parent.chmod(0o700)
      if key.is_file() and key.stat().st_size > 0:
        print(f"Key file already exists at {key}")
      else:
        key.write_text(generated_key, "utf-8")
        key.chmod(0o600)
        print(f"Key file created at {key}")
    else:
      print(generated_key)

  @staticmethod
  def passphrase(length: int = 7, token: Path | None = None):
    """Generate a random passphrase."""
    passphrase: str = gen_passphrase(length)
    if token:
      token.parent.mkdir(parents=True, exist_ok=True)
      token.parent.chmod(0o700)
      if token.is_file() and token.stat().st_size > 0:
        print(f"Token file already exists at {token}")
      else:
        token.write_text(passphrase, "utf-8")
        token.chmod(0o600)
        print(f"Token file created at {token}")
    else:
      print(passphrase)

  def export_attachment_from_origin(self, entry_path: str, attachment_name: str, export_file: Path):
    """Get a binary from Trapper Keeper."""
    # Prompt for a password
    password = Path(self.settings.get("src_token")).read_text("utf-8")

    # Define the command to open the Keepass database
    command = [
      "keepassxc-cli",
      "attachment-export",
      "--key-file",
      self.settings.get("src_key"),
      "--yubikey",
      f"{self.settings.get('src_yubikey_slot')}:{self.settings.get('src_yubikey_serial')}",
      self.settings.get("src_db"),
      entry_path,
      attachment_name,
      export_file,
    ]

    # Run the command and pipe the password into stdin
    process = subprocess.Popen(command, stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    stdout, stderr = process.communicate(input=password.encode())

  def add_export_entry(self, attachment_name: str, export_file: Path):
    pass

  def export_bootstrap_kpdb(self, tmp_dir: Path) -> Path:
    # Open the new keepass db and print the version
    # Prompt for a password
    password = Path(self.settings.get("bootstrap_token")).read_text("utf-8")

    # Define the command to open the Keepass database
    command = [
      "keepassxc-cli",
      "export",
      "--format",
      "xml",
      self.settings.get("bootstrap_db"),
    ]

    # Run the command and pipe the password into stdin
    process = subprocess.Popen(command, stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    stdout, stderr = process.communicate(input=password.encode())

    # Check for errors
    if process.returncode != 0:
      print(f"Error opening Keepass database: {stderr.decode()}")
    else:
      # Define the output file path
      tmp_dir.mkdir(parents=True, exist_ok=True)
      output_file = tmp_dir / "bootstrap.xml"

      # Save stdout to the output file
      with open(output_file, "wb") as f:
        f.write(stdout)

      # Set file permissions to 0600
      output_file.chmod(0o600)

      return output_file

  # def import_bootstrap_kpdb(self, bootstrap_export: Path):
  #   password = Path(self.tgt_settings.get("token")).read_text("utf-8")
  #   print(password)
  #   key = Path(self.tgt_settings.get("key"))
  #   db = Path(self.tgt_settings.get("db"))
  #   print(key)
  #   print(db)
  #
  #   # Define the command to open the Keepass database
  #   command = [
  #     "keepassxc-cli",
  #     "import",
  #     '--set-key-file',
  #     f"{key}",
  #     "--set-password",
  #     f"{password}",
  #     bootstrap_export,
  #     db,
  #   ]
  #
  #   # Run the command and pipe the password into stdin
  #   process = subprocess.Popen(command, stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
  #
  #   # Check for errors
  #   if process.returncode != 0:
  #     print(process.args)
  #     print(f"Error opening Keepass database: {process.stderr.read()}")
  #   else:
  #     print(process.stdout.read())
