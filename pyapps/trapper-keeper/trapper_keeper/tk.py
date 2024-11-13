"""Trapper Keeper interface to various DB Stores noted by class DbTypes."""

from __future__ import annotations

import contextlib
from dataclasses import dataclass
from enum import StrEnum, auto

from trapper_keeper.conf import TkSettings
from trapper_keeper.stores.bolt_kvstore import BoltStore
from trapper_keeper.stores.keepass_store import KeepassStore


@dataclass
class KeeAuth:
  """KeePass authentication information.

  Args:
      kp_key
      kp_token
  """


class DbTypes(StrEnum):
  """Types of datastores that can be used.


  Args:
      StrEnum (_type_): String enum name of supported datastores.
  """

  BOLT: str = auto()
  KP: str = auto()
  SQLITE: str = auto()


def get_tk_settings() -> TkSettings:
  """Get the Trapper Keeper settings.

  Returns:
      TkSettings: _description_
  """
  return TkSettings.get_instance("trapper_keeper", xdg_config=True, auto_create=True)


def _get_tk_store(settings: TkSettings) -> contextlib.AbstractContextManager:
  """Open a Trapper Keeper store based on the db_type."""
  return KeepassStore(kp_fp=settings.get("db"), kp_key=settings.get("key"), kp_token=settings.get("token"))


def _get_chezmoi_store(settings: TkSettings, readonly: bool = True) -> contextlib.AbstractContextManager:
  """Open the Chezmoi (bolt) store."""
  return BoltStore(settings.get("chezmoi_db"), readonly)


def get_store(settings: TkSettings, db_type: DbTypes) -> contextlib.AbstractContextManager:
  """Get a store based on the db_type.

  Args:
      settings (TkSettings): TkSettings instance.
      db_type (DbTypes): Type of store to open.

  Returns:
      contextlib.AbstractContextManager: Store instance.

  Raises:
      ValueError: Unsupported db_type
  """
  if db_type == DbTypes.BOLT:
    return _get_chezmoi_store(settings)
  if db_type == DbTypes.KP:
    return _get_tk_store(settings)
  raise ValueError(f"Unsupported db_type: {db_type}")


# TODO: Pack, Unpack stores to/from tk store
