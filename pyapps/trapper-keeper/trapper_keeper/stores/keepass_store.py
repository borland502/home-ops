"""Keepass store for trapper-keeper."""

from __future__ import annotations

from contextlib import AbstractContextManager
from pathlib import Path
from typing import IO
from uuid import UUID

from pykeepass import PyKeePass, create_database
from pykeepass.entry import Entry
from pykeepass.group import Group
from utils.file import pathify, get_file_io, stringify_path, get_file_bytes

from trapper_keeper.conf import TkSettings, TgtSettings
from trapper_keeper.keegen import gen_passphrase, gen_utf8

BOOTSTRAP: str = "A9906776-8CEB-4044-8D65-97B5A93920DD"
BOOTSTRAP_ENTRY: str = "36EF1A2C-773E-4F9D-AE8A-07B21E21C317"
settings = TkSettings.get_instance("trapper_keeper", xdg_config=True, auto_create=True)

def _create_kp_db_bootstrap_group(kp_db: PyKeePass) -> None:
  """Create the top-level bootstrap groups."""

  # Create the bootstrap group in a new database
  kp_db.add_group(group_name=f"{BOOTSTRAP}", destination_group=kp_db.root_group)
  kp_db.save()

def _create_kp_db_bootstrap_entries(kp_db: PyKeePass) -> None:
  """Create the bootstrap entries in a new database."""

  src_db_path = settings.get("src_db")
  if src_db_path == Path.cwd():
      raise ValueError("The source database path cannot be the current working directory.")

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

  for idx, src_file in enumerate(src_files):
    kp_db.add_binary(data=get_file_bytes(src_file), compressed=True)
    entry.add_attachment(idx, src_file.name)

  src_env: dict[str, str] = {k: v for k, v in settings.get("src_env").items() if v and v.isprintable()}
  [entry.set_custom_property(k, v) for k, v in src_env.items()]

  kp_db.save()


def create_kp_db(kp_fp: Path, kp_token: Path, kp_key: Path | None = None) -> PyKeePass:
  """Create a new keepass vault in the KEEPASS_DB_PATH, with the KEEPASS_DB_KEY, and KEEPASS_DB_TOKEN."""
  print(f"Creating new Keepass database at {kp_fp}")
  if not kp_fp.is_file():
    # make the directory at least if the database does not exist
    kp_fp.parent.mkdir(mode=0o700, exist_ok=True, parents=True)
  if not kp_token.is_file():
    # make the directory at least if the database does not exist
    kp_token.parent.mkdir(mode=0o700, exist_ok=True, parents=True)
    kp_token.write_text(gen_passphrase(length=5), "utf-8")
    print(f"Token file created at {kp_token}")
  if kp_key is not None and not kp_key.is_file():
    # make the directory at least if the database does not exist
    kp_key.parent.mkdir(mode=0o700, exist_ok=True, parents=True)
    kp_key.write_text(gen_utf8(length=64), "utf-8")
  kp_db: PyKeePass = create_database(
    kp_fp,
    password=kp_token.read_text("utf-8"),
    keyfile=kp_key,
  )

  _create_kp_db_bootstrap_group(kp_db)
  _create_kp_db_bootstrap_entries(kp_db)
  kp_db.save()

  return kp_db


REF_PREFIX = "ref@"
REF_SEP = "/"
REF_SEP2 = ":"
ATTR_TITLE = "__title__"
ATTR_USERNAME = "__username__"
ATTR_PASSWORD = "__password__"
ATTR_URL = "__url__"


# def _validate_ref(ref: str) -> None:
#   """Validate the ref string."""
#   if not ref:
#     raise ValueError("Empty ref")
#   if not ref.startswith(REF_PREFIX):
#     raise ValueError(f"Invalid ref: {ref}, prefix {REF_PREFIX!r} expected")
#   if ref.removeprefix(REF_PREFIX).count(REF_SEP) < 1:
#     raise ValueError(
#       f"Invalid ref: {ref}, at least 1 separator {REF_SEP!r} expected",
#     )
#   if ref.removeprefix(REF_PREFIX).count(REF_SEP2) != 1:
#     raise ValueError(
#       f"Invalid ref: {ref}, exactly 1 separator {REF_SEP2!r} expected",
#     )


# def _parse_ref(ref: str) -> tuple[list[str], str]:
#   """Parse the ref string."""
#   _validate_ref(ref)
#   ref = ref.removeprefix(REF_PREFIX)
#   _path, attribute = ref.rsplit(REF_SEP2, maxsplit=1)
#   path = _path.split(REF_SEP)
#   return path, attribute


class KeepassStore(PyKeePass):
  """Keepass store for trapper-keeper."""

  def __init__(self, kp_fp: Path, kp_token: Path, kp_key: Path | None = None):
    """Initialize the KeepassStore."""
    super().__init__(
      filename=kp_fp,
      password=kp_token.read_text("utf-8"),
      keyfile=kp_key,
    )

    if not self.find_groups(name=BOOTSTRAP, first=True):
      _create_kp_db_bootstrap_group(self)
      _create_kp_db_bootstrap_entries(self)

  def __enter__(self) -> AbstractContextManager:
    """Context manager enter."""
    super().__enter__()
    return self

  def get_bootstrap_entry(self, uuid: UUID):
    """Get the value by UUID in the bootstrap group."""
    return self.find_entries(group=self.root_group, name=uuid, first=True)

  # def load_ref(self, ref: str) -> str:
  #   """Load a reference."""
  #   path, attribute = _parse_ref(ref)
  #   entry = self.find_entries(path=path)
  #   if entry is None:
  #     raise KeyError(f"Entry {path!r} not found")
  #   if attribute == ATTR_TITLE:
  #     out: str = entry.title
  #   elif attribute == ATTR_USERNAME:
  #     out = entry.username
  #   elif attribute == ATTR_PASSWORD:
  #     out = entry.password
  #   elif attribute == ATTR_URL:
  #     out = entry.url
  #   else:
  #     out = entry.custom_properties[attribute]
  #   if out.startswith(REF_PREFIX):
  #     if attribute == ATTR_TITLE:
  #       raise ValueError(f"Invalid ref: {ref}, title cannot be a ref")
  #     return self.load_ref(out)
  #   return out

  # def load_env(self, entry_path: Sequence[str]) -> None:
  #   """Load the environment."""
  #   env = self.env(entry_path=entry_path)
  #   for k, v in env.items():
  #     os.environ[k] = v

  # def env(self, entry_path: Sequence[str]) -> dict[str, str]:
  #   """:param self:
  #   :param entry_path:
  #   :return:
  #   """
  #   entry = self.find_entries(path=entry_path)
  #   if entry is None:
  #     raise KeyError(f"Entry {entry_path!r} not found")
  #   kv = entry.custom_properties
  #   return {k: self.load_ref(v) if v.startswith(REF_PREFIX) else v for k, v in kv.items()}

  # def write_env(
  #   self,
  #   entry_path: Sequence[str],
  #   env: dict[str, str],
  #   create_if_not_exists: bool = True,
  # ) -> None:
  #   """Write the environment."""
  #   entry = self.find_entries(path=entry_path)
  #   if entry is None:
  #     if create_if_not_exists:
  #       entry = self.add_entry(
  #         group=self.root_group,
  #         title=entry_path[-1],
  #         username="",
  #         password="",
  #         url="",
  #       )
  #     else:
  #       raise KeyError(f"Entry {entry_path!r} not found and create_if_not_exists is False")

  #     raise KeyError(f"Entry {entry_path!r} not found")
  #   for k, v in env.items():
  #     entry.set_custom_property(k, v)
  #   self.save()

  # def dump_env(self, entry_path: Sequence[str], output_format: str) -> StringIO:
  #   """Dump the environment."""
  #   with StringIO() as sink:
  #     for k, v in self.env(entry_path):
  #       match output_format:
  #         case "env":
  #           if output_format == "env":
  #             sink.write(f"{k}={v}")
  #           elif output_format == "docker":
  #             sink.write(f"-e {k}={v}")
  #           elif output_format == "shell":
  #             sink.write(f"bootstrap {k}={v}")
  #           else:
  #             raise ValueError(f"Invalid format: {output_format!r}")
  #     return sink

  def __exit__(self, __exc_type, __exc_value, __traceback):
    """Context manager exit."""
    super().__exit__(__exc_type, __exc_value, __traceback)

  def copy_group(self, src: KeepassStore, src_group: Group, dest_group: Group):
    """Copy a group from the source database to the destination database."""
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
        force_creation=True)

  def copy_bootstrap_entries(self, src: KeepassStore):
    """Copy the bootstrap entries from the source database."""
    for entry in src.find_entries(group=src.get_bootstrap_group()):
      self.add_entry(
        destination_group=self.get_bootstrap_group(),
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

  def get_bootstrap_group(self) -> Group | None:
    return self.find_groups(name=f"{BOOTSTRAP}", first=True)
