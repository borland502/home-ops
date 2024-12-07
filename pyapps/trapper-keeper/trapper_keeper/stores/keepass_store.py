"""Keepass store for trapper-keeper.  This store in particular serves as the focal point of
trapper-keeper and all other stores can (and should) be embedded within this store.

"""

from __future__ import annotations

import os
from contextlib import AbstractContextManager
from pathlib import Path
from uuid import UUID

from pykeepass import PyKeePass, create_database
from pykeepass.entry import Entry
from pykeepass.group import Group
from resources.configs.tk_conf import TkSettings
from utils.file import get_file_bytes, pathify

from trapper_keeper.keegen import gen_passphrase, gen_utf8

settings = TkSettings.get_instance("trapper_keeper", xdg_config=True, auto_create=True)

BOOTSTRAP = settings.get("bootstrap_uuid")
BOOTSTRAP_ENTRY = settings.get("bootstrap_entry")


def _create_kp_db_bootstrap_group(kp_db: PyKeePass) -> None:
    """Create the top-level bootstrap groups in the Keepass database.

    Args:
        kp_db (PyKeePass): The Keepass database instance.
    """
    kp_db.add_group(group_name=f"{BOOTSTRAP}", destination_group=kp_db.root_group)
    kp_db.save()


def _create_kp_db_bootstrap_entries(kp_db: PyKeePass) -> None:
    """Create the bootstrap entries in the Keepass database.

    Args:
        kp_db (PyKeePass): The Keepass database instance.

    Raises:
        ValueError: If the source database path is the current working directory.
        ValueError: If the bootstrap group is not found in the database.
    """
    src_db_path = settings.get("src_db")
    if src_db_path == Path.cwd():
        raise ValueError(
            "The source database path cannot be the current working directory."
        )

    if not kp_db.find_groups(name=f"{BOOTSTRAP}", first=True):
        raise ValueError(f"Bootstrap group {BOOTSTRAP} not found in the database.")

    group: Group = kp_db.find_groups(name=f"{BOOTSTRAP}", first=True)

    entry: Entry = kp_db.add_entry(
        destination_group=group,
        title=f"{BOOTSTRAP_ENTRY}",
        username="",
        password="",
    )

    src_files: list[Path] = pathify(*settings.get("src_files")[BOOTSTRAP_ENTRY])

    src_dirs: list[Path] = pathify(*settings.get("src_dirs")[BOOTSTRAP_ENTRY])

    seen_files = set()
    for dir_file in src_dirs:
        for root, _, files in os.walk(dir_file):
            for file in files:
                file_path = Path(root) / file
                if file_path not in seen_files:
                    seen_files.add(file_path)
                    src_files.append(file_path)

    store_attachments(entry, kp_db, src_files)


def store_attachments(entry, kp_db, src_files):
    """Store attachments in the Keepass database.

    Args:
        entry (_type_): _description_
        kp_db (_type_): _description_
        src_files (_type_): _description_
    """
    for idx, src_file in enumerate(src_files):
        # the file does not exist
        if not src_file.exists():
            print(f"Skipping {src_file} as it does not exist.")
            continue

        data: bytes | None = get_file_bytes(src_file)
        if data:
            kp_db.add_binary(data=get_file_bytes(src_file), compressed=False)
            entry.add_attachment(idx, src_file.name)
            entry.set_custom_property(f"file_{idx}", f"{src_file}")
        else:
            print(f"Skipping {src_file} as there is a problem.")

    src_env: dict[str, str] = {
        k: v for k, v in settings.get("src_env").items() if v and v.isprintable()
    }
    [entry.set_custom_property(k, v) for k, v in src_env.items()]
    kp_db.save()


def view_kp_db(fp_kp_db: Path, fp_token: Path, fp_key: Path | None = None) -> None:
    """View the contents of the Keepass database.

    Args:
        fp_kp_db (Path): The path to the Keepass database file.
        fp_token (Path): The path to the token file.
        fp_key (Path | None, optional): The path to the key file. Defaults to None.
    """
    kp_db: PyKeePass = PyKeePass(
        filename=fp_kp_db,
        password=fp_token.read_text(settings.get("encoding")),
        keyfile=fp_key,
    )
    for group in kp_db.groups:
        print(f"Group: {group.name}")
        for entry in group.entries:
            print(f"  Entry: {entry.title}")
            for k, v in entry.custom_properties.items():
                print(f"    {k}: {v}")
            for attachment in entry.attachments:
                print(f"    Attachment: {attachment.id}: {attachment.filename}")


def create_kp_db(
    fp_kp_db: Path, fp_token: Path, fp_key: Path | None = None
) -> PyKeePass:
    """Create a new Keepass database.

    Args:
        fp_kp_db (Path): The path to the Keepass database file.
        fp_token (Path): The path to the token file.
        fp_key (Path | None, optional): The path to the key file. Defaults to None.

    Returns:
        KeepassStore: The wrapped Keepass database instance.
    """
    print(f"Creating new Keepass database at {fp_kp_db}")
    if not fp_kp_db.is_file():
        fp_kp_db.parent.mkdir(
            mode=settings.get("user_dir_mode"), exist_ok=True, parents=True
        )
    if not fp_token.is_file():
        fp_token.parent.mkdir(
            mode=settings.get("user_dir_mode"), exist_ok=True, parents=True
        )
        fp_token.write_text(
            gen_passphrase(length=settings.get("passphrase_length")),
            settings.get("encoding"),
        )
        print(f"Token file created at {fp_token}")
    if fp_key is not None and not fp_key.is_file():
        fp_key.parent.mkdir(
            mode=settings.get("user_dir_mode"), exist_ok=True, parents=True
        )
        fp_key.write_text(
            gen_utf8(length=settings.get("key_length")), settings.get("encoding")
        )
    kp_db: PyKeePass = create_database(
        fp_kp_db,
        password=fp_token.read_text(settings.get("encoding")),
        keyfile=fp_key,
    )

    _create_kp_db_bootstrap_group(kp_db)
    _create_kp_db_bootstrap_entries(kp_db)
    kp_db.save()

    return kp_db


class KeepassStore(PyKeePass):
    """Keepass store for trapper-keeper."""

    def __init__(self, fp_kp_db: Path, fp_token: Path, fp_key: Path | None = None):
        """Initialize the KeepassStore.

        Args:
            fp_kp_db (Path): The path to the Keepass database file.
            fp_token (Path): The path to the token file.
            fp_key (Path | None, optional): The path to the key file. Defaults to None.
        """
        if not fp_kp_db:
            super().__init__(fp_kp_db, fp_token.read_text(settings.get("encoding")))
        else:
            super().__init__(
                fp_kp_db,
                fp_token.read_text(settings.get("encoding")),
                keyfile=fp_key,
            )

        if not self.find_groups(name=BOOTSTRAP, first=True):
            _create_kp_db_bootstrap_group(self)
            _create_kp_db_bootstrap_entries(self)

    def __enter__(self) -> AbstractContextManager:
        """Context manager enter.

        Returns:
            AbstractContextManager: The context manager instance.
        """
        super().__enter__()
        return self

    def get_bootstrap_entry(self, uuid: UUID):
        """Get the value by UUID in the bootstrap group.

        Args:
            uuid (UUID): The UUID of the entry.

        Returns:
            Entry: The entry found by UUID.
        """
        return self.find_entries(group=self.root_group, name=uuid, first=True)

    def __exit__(self, __exc_type, __exc_value, __traceback):
        """Context manager exit.

        Args:
            __exc_type: The exception type.
            __exc_value: The exception value.
            __traceback: The traceback object.
        """
        super().__exit__(__exc_type, __exc_value, __traceback)

    def copy_group(self, src: KeepassStore, src_group: Group, dest_group: Group):
        """Copy a group from the source database to the destination database.

        Args:
            src (KeepassStore): The source Keepass store.
            src_group (Group): The source group.
            dest_group (Group): The destination group.
        """
        for entry in src.find_entries(group=src_group):
            self.add_entry(
                destination_group=dest_group,
                title=entry.title,
                username=entry.username,
                password=entry.password,
                url=entry.url,
                notes=entry.notes,
                expiry_time=entry.expiry_time,
                tags=entry.tags,
                otp=entry.otp,
                icon=entry.icon,
                force_creation=True,
            )

    def copy_bootstrap_entries(self, src: KeepassStore):
        """Copy additional entries from the source database bootstrap group.

        Args:
            src (KeepassStore): The source Keepass store.
        """
        tgt_group: Group = self.get_bootstrap_group()
        src_group: Group = src.get_bootstrap_group()
        additional_entries = [
            entry
            for entry in src.find_entries(group=src_group)
            if entry.title != BOOTSTRAP_ENTRY
        ]
        tgt_group.append(additional_entries)
        self.save()

    def get_bootstrap_group(self) -> Group | None:
        """Get the bootstrap group.  The bootstrap group contains entries with key/values and
        attachments for a new system.

        Returns:
            Group | None: The bootstrap group if found, otherwise None.
        """
        return self.find_groups(name=f"{BOOTSTRAP}", first=True)
