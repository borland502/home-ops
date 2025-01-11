"""Utility functions for working with environment variables."""

from __future__ import annotations

import os
from pathlib import Path

from homeops_utils import file


def filter_env_vars(*paths: [str | Path]) -> dict[str, str]:
    """Filters out environment variables whose values are in the provided list of paths.

    :param paths: List of paths to filter out from environment variables.
    :return: Dictionary of filtered environment variables.
    """
    _paths = file.pathify(paths)
    filtered_env = {k: v for k, v in os.environ.items() if v not in _paths and v.isprintable()}
    return filtered_env
