"""Trapper Keeper interface to various DB Stores noted by class DbTypes."""

from __future__ import annotations

import contextlib
from enum import StrEnum, auto
from pathlib import Path

from trapper_keeper.stores.bolt_kvstore import BoltStore
from trapper_keeper.stores.dict_store import PersistentDict
from trapper_keeper.stores.keepass_store import KeepassStore, create_kp_db
from trapper_keeper.stores.sqlite_store import SqliteStore


class DbTypes(StrEnum):
  """Types of datastores that can be used.


  Args:
      StrEnum (_type_): String enum name of supported datastores.
  """

  BOLT: str = auto()
  KP: str = auto()
  SQLITE: str = auto()
  KV: str = auto()


def _get_tk_store(fp_kp_db: Path, fp_token: Path, fp_key: Path | None = None) -> contextlib.AbstractContextManager:
  """Open a Trapper Keeper store based on the db_type."""
  return KeepassStore(fp_kp_db, fp_token, fp_key)

def _get_kv_store(db_fp: Path) -> contextlib.AbstractContextManager:
  """Open a key/value store."""
  return PersistentDict(filename=db_fp, flag="c", format="json", mode=0o600, encoding="utf-8", errors="strict", indent=2)

def _get_sqlite_store(db_fp: Path) -> contextlib.AbstractContextManager:
  """Open a sqlite store."""
  return SqliteStore(db_fp)

def _get_bolt_store(db_fp: Path, readonly: bool = True) -> contextlib.AbstractContextManager:
  """Open the boltdb store."""
  return BoltStore(db_fp, readonly)


# noinspection PyCompatibility
def get_store(db_type: DbTypes, **kwargs) -> contextlib.AbstractContextManager:
  """Get a store based on the db_type.

  Args:
      db_type (DbTypes): Type of store to open.
      **kwargs: Additional arguments required for the specific store type.

  Returns:
      contextlib.AbstractContextManager: Store instance.

  Raises:
      ValueError: If the db_type is unsupported.
  """
  match db_type:
    case DbTypes.BOLT:
      db_fp: Path = kwargs["db_fp"]
      readonly: bool = kwargs["readonly"]
      return _get_bolt_store(db_fp=db_fp, readonly=readonly)
    case DbTypes.KP:
      fp_kp_db: Path = kwargs["fp_kp_db"]
      fp_token: Path = kwargs["fp_token"]
      fp_key: Path | None = kwargs.get("fp_key")
      if fp_kp_db is None or fp_token is None:
        raise ValueError("fp_kp_db and fp_token are required for KeepassStore.")
      if not fp_kp_db.exists():
        create_kp_db(fp_kp_db=fp_kp_db, fp_token=fp_token, fp_key=fp_key)
      return _get_tk_store(fp_kp_db=fp_kp_db, fp_token=fp_token, fp_key=fp_key)
    case DbTypes.SQLITE:
      db_fp: Path = kwargs["db_fp"]
      return _get_sqlite_store(db_fp=db_fp)
    case DbTypes.KV:
      db_fp: Path = kwargs["db_fp"]
      readonly: bool = kwargs["readonly"]
      return _get_kv_store(db_fp=db_fp, readonly=readonly)
    case _:
      raise ValueError(f"Unsupported db_type: {db_type}")
