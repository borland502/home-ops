"""Module for finding Ansible resources in the configured directories."""

from __future__ import annotations

import os

from utils.paths import AnsiblePaths


# def find_yaml_files_in_dir(path: str) -> list[str]:
#   """Finds all YAML files in the given directory."""
#   yield (os.path.join(path, file) for file in os.listdir(path) if file.endswith((".yaml", ".yml")))

from itertools import chain

def find_yaml_files(path) -> list[str]:
    """Finds all YAML files recursively under the given path.

    Args:
        path: The base path to search.

    Returns:
        A list of absolute paths to all YAML files found.
    """
    return chain.from_iterable(
        (os.path.join(root, file) for file in files if file.endswith((".yaml", ".yml")))
        for root, _, files in os.walk(path)
    )


def find_playbook(app_name: str) -> str | None:
  """Searches for a playbook file named after the given application name.

  Args:
      app_name (str): The name of the application to find the playbook for.

  Returns:
      str: The full path to the playbook file if found, otherwise None.

  This function iterates through all playbooks (obtained using `find_playbooks()`)
  and returns the first one whose filename starts with the provided `app_name`.
  """
  try:
    for playbook in find_playbooks():
      if str(playbook).startswith(app_name):
        return str(playbook)
  except FileNotFoundError:
    pass


def find_playbooks() -> list[str]:
  """Locates all playbook YAML files from the configured playbook directory.

  Returns:
      list[str]: A list of full paths to all playbook YAML files found.

  This function delegates the task of finding YAML files to `find_yaml_files`
  (implementation assumed to exist elsewhere), passing the directory containing
  playbooks (presumably defined in `Directories.PBROOT`). It returns the list
  of file paths obtained from `find_yaml_files`.
  """
  return find_yaml_files(AnsiblePaths.PBROOT)


def find_host(app_name: str) -> str:
  """Searches for a host file named after the given application name.

  Args:
      app_name (str): The name of the application to find the host file for.

  Returns:
      str: The full path to the host file if found, otherwise raises a `FileNotFoundError`.

  This function iterates through all hosts (obtained using `find_hosts()`)
  and returns the first one whose filename starts with the provided `app_name`.
  If no matching host file is found, it raises a `FileNotFoundError` to indicate
  the missing resource.
  """
  for host in find_hosts():
    if host.startswith(app_name):
      return host
  raise FileNotFoundError


def find_hosts() -> list[str]:
  """Locates all host YAML files from the configured host directory.

  Returns:
      list[str]: A list of full paths to all host YAML files found.

  This function operates similarly to `find_playbooks()`, delegating the
  task of finding YAML files to `find_yaml_files` using the directory containing
  hosts (presumably defined in `Directories.HVHOME`). It returns the list
  of file paths obtained from `find_yaml_files`.
  """
  return find_yaml_files(AnsiblePaths.HVHOME)
