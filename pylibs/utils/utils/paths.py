"""Paths Module.

This module defines various path constants and structures for managing paths
in a system, including XDG base directories and project-specific paths.
"""

from __future__ import annotations

import tempfile
from enum import StrEnum
from itertools import chain
from pathlib import Path

from xdg_base_dirs import (
    xdg_cache_home,
    xdg_config_dirs,
    xdg_config_home,
    xdg_data_dirs,
    xdg_data_home,
    xdg_runtime_dir,
    xdg_state_home,
)

SKIP_FILES = [
    ".project",
    ".classpath",
    ".launch",
    ".settings",
    "extensions.json",
    ".gitkeep",
    "ci.yml",
]

# List of directories to ignore
SKIP_DIRS = [
    ".cache",
    "collections",
    ".c9",
    ".devcontainer",
    ".DS_Store",
    ".idea",
    ".git",
    ".run",
    ".sass-cache",
    ".settings",
    ".sublime-workspace",
    ".task",
    ".vscode",
    ".venv",
    ".mypy_cache",
    ".nx",
    "__pycache__",
    "ansible_collections",
    "connect.lock",
    "coverage",
    "dist",
    "libpeerconnection.log",
    "node_modules",
    "npm-debug.log",
    "out-tsc",
    "reports",
    "tests",
    "test-results",
    "testem.log",
    "Thumbs.db",
    "tmp",
    "trapper_keeper",
    "typings",
    "venv",
    "automation",
]


class SkipPaths:
    """Utility class for filtering paths."""

    @staticmethod
    def filter_dirs(
        root: Path,
        exclude_dirs: list[str] | None = None,
        exclude_files: list[str] | None = None,
    ) -> chain:
        """Generate a chain of directories to be exported."""
        if exclude_dirs is None:
            exclude_dirs = SKIP_DIRS
        if exclude_files is None:
            exclude_files = SKIP_FILES
        for item in root.iterdir():
            if SkipPaths.is_ignored(item, exclude_dirs, exclude_files):
                continue
            yield item
            if item.is_dir():
                yield from SkipPaths.filter_dirs(item)

    @staticmethod
    def is_ignored(
        path: Path,
        exclude_dirs: list[str] | None = None,
        exclude_files: list[str] | None = None,
    ) -> bool:
        """Check if a path matches any of the ignore patterns."""
        if exclude_dirs is None:
            exclude_dirs = SKIP_DIRS
        if exclude_files is None:
            exclude_files = SKIP_FILES

        return path.name in exclude_dirs or path.name in exclude_files

    @staticmethod
    def get_files(*paths, exclude_dirs=None, exclude_files=None):
        """Flatten directories and filter out ignored paths."""
        for path in paths:
            if path is None or not path.exists():
                continue

            if path.is_file():
                yield path
            else:
                yield from SkipPaths.filter_dirs(path, exclude_dirs, exclude_files)



def export_paths() -> chain[str]:
    """Generate a chain of paths to be exported."""
    ansible_files = [str(path) for path in AnsiblePaths if Path(path.value).is_file()]
    return chain(ansible_files, SecretsPaths)


class BasePaths(StrEnum):
    """Base paths for the system.

    Attributes:
        HOME (str): The home directory path.
        TMP (str): The temporary directory path.
    """

    HOME = str(Path.home())
    TMP = tempfile.gettempdir()


class XdgPaths(StrEnum):
    """XDG Base Directories for the system.

    Attributes:
        XDG_DATA_HOME (str): The XDG data home directory path.
        XDG_CACHE_HOME (str): The XDG cache home directory path.
        XDG_CONFIG_HOME (str): The XDG config home directory path.
        XDG_STATE_HOME (str): The XDG state home directory path.
        XDG_RUNTIME_DIR (str): The XDG runtime directory path.
        XDG_CONFIG_DIRS (str): The XDG config directories path.
        XDG_DATA_DIRS (str): The XDG data directories path.
        XDG_BIN_HOME (str): The XDG bin home directory path.
        XDG_LIB_HOME (str): The XDG lib home directory path.
    """

    XDG_DATA_HOME = str(xdg_data_home())
    XDG_CACHE_HOME = str(xdg_cache_home())
    XDG_CONFIG_HOME = str(xdg_config_home())
    XDG_STATE_HOME = str(xdg_state_home())
    XDG_RUNTIME_DIR = str(xdg_runtime_dir())
    XDG_CONFIG_DIRS = str(xdg_config_dirs())
    XDG_DATA_DIRS = str(xdg_data_dirs())
    XDG_BIN_HOME = f"{BasePaths.HOME}/.local/bin"
    XDG_LIB_HOME = f"{BasePaths.HOME}/.local/lib"


class SecretsPaths(StrEnum):
    """Paths for secrets files.

    Attributes:
        BOOTSTRAP_DB (str): The bootstrap database file path.
        BOOTSTRAP_TOKEN (str): The bootstrap token file path.
        BOOTSTRAP_CONFIG (str): The bootstrap config file path.
        DB (str): The secrets database file path.
        KEY (str): The secrets key file path.
        TOKEN (str): The secrets token file path.
        SRC_DB (str): The source database file path.
        SRC_KEY (str): The source key file path.
        SRC_TOKEN (str): The source token file path.
        SRC_YUBIKEY (str): The source YubiKey serial file path.
        SSH_HOME (str): The SSH home directory path.
        SSH_KNOW_HOSTS (str): The SSH known hosts file path.
        GNUPG_HOME (str): The GnuPG home directory path.
        KNOWN_HOSTS (str): The known hosts file path.
    """

    BOOTSTRAP_DB = f"{XdgPaths.XDG_DATA_HOME}/trapper_keeper/bootstrap.kdbx"
    BOOTSTRAP_TOKEN = f"{XdgPaths.XDG_CONFIG_HOME}/trapper_keeper/bootstrap.token"
    BOOTSTRAP_CONFIG = f"{XdgPaths.XDG_STATE_HOME}/trapper_keeper/config.toml"
    DB = f"{XdgPaths.XDG_DATA_HOME}/trapper_keeper/secrets.kdbx"

    KEY = f"{XdgPaths.XDG_STATE_HOME}/trapper_keeper/secrets.keyx"
    TOKEN = f"{XdgPaths.XDG_CONFIG_HOME}/trapper_keeper/secrets.token"

    # Temporary location on the host system for the target's password.  The key will be stored in the
    # archive
    TMP_TOKEN = f"{XdgPaths.XDG_CACHE_HOME}/trapper_keeper/secrets.token"

    SRC_DB = f"{XdgPaths.XDG_DATA_HOME}/keepass/secrets.kdbx"
    SRC_KEY = f"{XdgPaths.XDG_STATE_HOME}/keepass/secrets.keyx"
    SRC_TOKEN = f"{XdgPaths.XDG_CONFIG_HOME}/keepass/secrets.token"
    SRC_YUBIKEY = f"{XdgPaths.XDG_CONFIG_HOME}/keepass/yubikey.serial"
    SSH_HOME = f"{BasePaths.HOME}/.ssh"
    SSH_KNOW_HOSTS = f"{SSH_HOME}/known_hosts"
    GNUPG_HOME = f"{BasePaths.HOME}/.gnupg"
    KNOWN_HOSTS = f"{BasePaths.HOME}/.ssh/known_hosts"


class HomeOpsPaths(StrEnum):
    """Path constants for the project.

    Attributes:
        AUTOMATION_HOME (str): The automation home directory path.
        PROOT (str): The project root directory path.
        PROJECT_ROOT (str): The project root directory path.
        HOME_OPS_HOME (str): The home operations home directory path.
    """

    AUTOMATION_HOME = f"{XdgPaths.XDG_DATA_HOME}/automation"
    PROOT = f"{AUTOMATION_HOME}/home-ops"
    PROJECT_ROOT = PROOT
    HOME_OPS_HOME = PROOT


class AnsiblePaths(StrEnum):
    """Path constants for both the project and for the user level ansible installation at HOME/.ansible.

    Attributes:
        PBROOT (str): The playbook root directory path.
        PLAYBOOK_ROOT (str): The playbook root directory path.
        AHOME (str): The ansible home directory path.
        ANSIBLE_HOME (str): The ansible home directory path.
        CHOME (str): The collections home directory path.
        RHOME (str): The roles home directory path.
        IHOME (str): The inventory home directory path.
        GVHOME (str): The group vars home directory path.
        HVHOME (str): The host vars home directory path.
        COLLECTIONS_REQS (str): The collections requirements file path.
        ROLES_REQS (str): The roles requirements file path.
        ALL_KVM_VARS (str): The all KVM vars file path.
        ALL_VARS (str): The all vars file path.
        ALL_LXC_VARS (str): The all LXC vars file path.
        CHEZMOI_DATA (str): The chezmoi data file path.
        STATIC_HOSTS (str): The static hosts file path.
        STATIC_HOSTS_YAML (str): The static hosts YAML file path.
        STATIC_HOSTS_TOML (str): The static hosts TOML file path.
        DYNAMIC_NMAP (str): The dynamic NMAP file path.
        DYNAMIC_PROXMOX (str): The dynamic Proxmox file path.
        DYNAMIC_LDAP (str): The dynamic LDAP file path.
        DYNAMIC_SQLITE (str): The dynamic SQLite file path.
        DBS_SQLITE (str): The SQLite database file path.
        INVENTORY_ALL (str): The inventory all directory path.
    """

    PBROOT = f"{HomeOpsPaths.PROOT}/ansible/playbooks"
    PLAYBOOK_ROOT = PBROOT
    AHOME = f"{BasePaths.HOME}/.ansible"
    ANSIBLE_HOME = AHOME
    CHOME = f"{AHOME}/collections"
    RHOME = f"{AHOME}/roles"
    IHOME = f"{AHOME}/inventory"
    GVHOME = f"{IHOME}/group_vars"
    HVHOME = f"{IHOME}/host_vars"
    COLLECTIONS_REQS = f"{CHOME}/requirements.yml"
    ROLES_REQS = f"{RHOME}/requirements.yml"
    ALL_KVM_VARS = f"{GVHOME}/proxmox_all_kvm.yaml"
    ALL_VARS = f"{GVHOME}/all.yaml"
    ALL_LXC_VARS = f"{GVHOME}/proxmox_all_lxc.yaml"
    CHEZMOI_DATA = f"{GVHOME}/chezmoi_data.yaml"
    STATIC_HOSTS = f"{IHOME}/hosts.yaml"
    STATIC_HOSTS_YAML = STATIC_HOSTS
    STATIC_HOSTS_TOML = f"{IHOME}/hosts.toml"
    DYNAMIC_NMAP = f"{IHOME}/nmap.yaml"
    DYNAMIC_PROXMOX = f"{IHOME}/proxmox.yaml"
    DYNAMIC_LDAP = f"{IHOME}/microsoft.ad.ldap.yaml"
    DYNAMIC_SQLITE = f"{IHOME}/sqlite.yaml"
    DBS_SQLITE = f"{XdgPaths.XDG_STATE_HOME}/sqlite/dasbootstrap.db"
    INVENTORY_ALL = f"{IHOME}/inventory/"

