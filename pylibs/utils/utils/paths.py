"""Paths Module.

This module defines various path constants and structures for managing paths
in a system, including XDG base directories and project-specific paths.
"""

from __future__ import annotations

import dataclasses
import tempfile
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


@dataclasses.dataclass
class BasePaths:
    """Base paths for all systems.

    Attributes:
        HOME (str): The home directory path.
        TMP (str): The temporary directory path.
        SSH_HOME (str): The SSH directory path.
        SSH_KNOW_HOSTS (str): The SSH known hosts file path.
        GNUPG_HOME (str): The GnuPG directory path.
        KNOWN_HOSTS (str): The known hosts file path.
    """

    HOME = str(Path.home())
    TMP = tempfile.gettempdir()
    SSH_HOME = f"{HOME}/.ssh"
    SSH_KNOW_HOSTS = f"{SSH_HOME}/known_hosts"
    GNUPG_HOME = f"{HOME}/.gnupg"
    KNOWN_HOSTS = f"{HOME}/.ssh/known_hosts"


@dataclasses.dataclass()
class XdgPaths:
    """XDG Base Directories for system.

    Attributes:
        _base_paths (BasePaths): An instance of BasePaths.
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

    _base_paths: BasePaths = dataclasses.field(default_factory=BasePaths)

    def __post_init__(self):
        """Post-initialization to set attributes from BasePaths."""
        for key, value in vars(self._base_paths).items():
            setattr(self, key, value)

    XDG_DATA_HOME = str(xdg_data_home())
    XDG_CACHE_HOME = str(xdg_cache_home())
    XDG_CONFIG_HOME = str(xdg_config_home())
    XDG_STATE_HOME = str(xdg_state_home())
    XDG_RUNTIME_DIR = str(xdg_runtime_dir())
    XDG_CONFIG_DIRS = str(xdg_config_dirs())
    XDG_DATA_DIRS = str(xdg_data_dirs())
    XDG_BIN_HOME = f"{BasePaths.HOME}/.local/bin"
    XDG_LIB_HOME = f"{BasePaths.HOME}/.local/lib"


@dataclasses.dataclass
class HomeOpsPaths:
    """Path constants for the project.

    Attributes:
        _xdg_paths (XdgPaths): An instance of XdgPaths.
        _base_paths (BasePaths): An instance of BasePaths.
        AUTOMATION_HOME (str): The automation home directory path.
        PROOT (str): The project root directory path.
        PROJECT_ROOT (str): The project root directory path.
        HOME_OPS_HOME (str): The home operations home directory path.
    """

    _xdg_paths: XdgPaths = dataclasses.field(default_factory=XdgPaths)
    _base_paths: BasePaths = dataclasses.field(default_factory=BasePaths)

    def __post_init__(self):
        """Post-initialization to set attributes from XdgPaths and BasePaths."""
        for key, value in chain(
            vars(self._xdg_paths).items(), vars(self._base_paths).items()
        ):
            setattr(self, key, value)

    AUTOMATION_HOME = f"{XdgPaths.XDG_DATA_HOME}/automation"
    PROOT = f"{AUTOMATION_HOME}/home-ops"
    PROJECT_ROOT = PROOT
    HOME_OPS_HOME = PROOT


@dataclasses.dataclass
class AnsiblePaths:
    """Path constants for both the project and for the user level ansible installation at HOME/.ansible.

    Attributes:
        _xdg_paths (XdgPaths): An instance of XdgPaths.
        _base_paths (BasePaths): An instance of BasePaths.
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

    _xdg_paths: XdgPaths = dataclasses.field(default_factory=XdgPaths)
    _base_paths: BasePaths = dataclasses.field(default_factory=BasePaths)

    def __post_init__(self):
        """Post-initialization to set attributes from XdgPaths and BasePaths."""
        for key, value in chain(
            vars(self._xdg_paths).items(), vars(self._base_paths).items()
        ):
            setattr(self, key, value)

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
