"""This module contains the SqliteStore class."""
from pathlib import Path

from sqlite_utils import Database


class SqliteStore(Database):
  """Basic context manager for accessing Sqlite databases."""
  def __init__(self, sp_fp: Path):
    """Initializes the SqliteStore context manager."""
    super().__init__()

  def __enter__(self) -> Database:
    """Enters the SqliteStore context manager."""
    return self

  def __exit__(self, __exc_type, __exc_value, __traceback):
    """Exits the SqliteStore context manager."""
    self.close()
