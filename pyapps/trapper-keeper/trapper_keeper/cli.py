"""Command line interface for trapper-keeper."""

from __future__ import annotations

import os
import subprocess
import tempfile
from pathlib import Path

from utils import file

from .conf import TgtSettings, TkSettings
from .keegen import gen_passphrase, gen_utf8
from .stores.keepass_store import create_kp_db, view_kp_db


def gen_key(length: int = 128, fp_key: Path | None = None):
    """Generate a random key.

    Args:
        length (int): The length of the key to generate. Defaults to 128.
        fp_key (Path | None): The file path to save the key. Defaults to None.
    """
    generated_key: str = gen_utf8(length)
    TrapperKeeper.save_credential(generated_key, fp_key)


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

    def unpack(self):
        """Unpack Trapper Keeper."""

    def pack(self):
        """Pack the Trapper Keeper.

        This method creates a temporary directory, generates necessary keys and tokens,
        creates a KeePass database, and packs the directory into a compressed file.
        """
        # Create a random folder in the system temp folder
        temp_dir = tempfile.mkdtemp()
        os.chdir(temp_dir)

        # Define the paths for the new store and credentials
        self.tgt_settings.set("db", f"{temp_dir}/secrets.kdbx", autosave=True)
        self.tgt_settings.set("key", f"{temp_dir}/secrets.key", autosave=True)
        self.tgt_settings.set("token", f"{temp_dir}/secrets.token", autosave=True)

        gen_key(length=self.tgt_settings.get("passphrase_length"), fp_key=Path(self.tgt_settings.get("key")))
        self.passphrase(length=7, fp_token=Path(self.tgt_settings.get("token")))
        create_kp_db(fp_kp_db=Path(self.tgt_settings.get("db")), fp_token=Path(self.tgt_settings.get("token")), fp_key=Path(self.tgt_settings.get("key")))

        pack_dir = Path(temp_dir)
        file.pack(src_dir=pack_dir, out_file=pack_dir / "trapper_keeper.zst")
        view_kp_db(fp_kp_db=Path(self.tgt_settings.get("db")), fp_token=Path(self.tgt_settings.get("token")), fp_key=Path(self.tgt_settings.get("key")))

    @staticmethod
    def passphrase(length: int = 7, fp_token: Path | None = None):
        """Generate a random passphrase.

        Args:
            length (int): The length of the passphrase to generate. Defaults to 7.
            fp_token (Path | None): The file path to save the passphrase. Defaults to None.
        """
        passphrase: str = gen_passphrase(length)
        TrapperKeeper.save_credential(passphrase, fp_token)

    @staticmethod
    def save_credential(sec_credential: str, fp_token: str | Path):
        """Save a credential to a file.

        Args:
            sec_credential (str): The credential to save.
            fp_token (str | Path): The file path to save the credential.
        """
        _fp_token = Path(fp_token)

        if _fp_token:
            _fp_token.parent.mkdir(parents=True, exist_ok=True)
            _fp_token.parent.chmod(0o700)
            if _fp_token.is_file() and _fp_token.stat().st_size > 0:
                print(f"Token file already exists at {_fp_token}")
            else:
                _fp_token.write_text(sec_credential, "utf-8")
                _fp_token.chmod(0o600)
                print(f"Token file created at {_fp_token}")
        else:
            print(_fp_token)

    def export_attachment_from_origin(self, entry_path: str, attachment_name: str, export_file: Path):
        """Get a binary from Trapper Keeper.

        Args:
            entry_path (str): The path to the entry in the KeePass database.
            attachment_name (str): The name of the attachment to export.
            export_file (Path): The file path to save the exported attachment.
        """
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

    def refresh_config(self) -> None:
        """Refresh the configuration settings.

        This method deletes existing configuration files and reloads the settings.
        """
        if self.settings or file.ensure_path(self.settings.get_settings_folder()):
            config_path = self.settings.get_settings_folder()
            file.delete_files(config_path)

        # Doesn't really matter if the file doesn't exist as the method's goal is accomplished either way
        self.settings = TkSettings.get_instance("trapper_keeper", xdg_config=True, auto_create=True)

    def add_export_entry(self, attachment_name: str, export_file: Path):
        """Add an export entry.

        Args:
            attachment_name (str): The name of the attachment.
            export_file (Path): The file path to save the exported attachment.
        """

    def export_bootstrap_kpdb(self, tmp_dir: Path) -> Path:
        """Export the bootstrap KeePass database.

        Args:
            tmp_dir (Path): The temporary directory to save the exported database.

        Returns:
            Path: The path to the exported database file.
        """
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
            raise Exception(f"Error opening Keepass database: {stderr.decode()}")

        # Define the output file path
        tmp_dir.mkdir(parents=True, exist_ok=True)
        output_file = tmp_dir / "bootstrap.xml"

        # Save stdout to the output file
        with open(output_file, "wb") as f:
            f.write(stdout)

        # Set file permissions to 0600
        output_file.chmod(0o600)
        return output_file
