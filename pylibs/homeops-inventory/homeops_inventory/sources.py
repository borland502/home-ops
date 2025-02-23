"""Inventory sources module for HomeOps Inventory."""
from __future__ import annotations

import datetime
from pathlib import Path

import xdg_base_dirs
from ansible.inventory.host import Host
from ansible.inventory.manager import InventoryManager
from ansible.parsing.dataloader import DataLoader
from ansible.plugins.loader import init_plugin_loader
from ansible.vars.manager import VariableManager
from cachier import cachier

from ansible_commands.ansible_commands import Actions
from homeops_utils.paths import Directories
from homeops_utils.paths import Inventory


class InventorySource:
    """InventorySource is a simple abstraction over ansible's complicated inventory plugin system."""

    default_basedir: Path = Path(Directories.IHOME)

    def __init__(self, basedir: Path = default_basedir, source=None):
        """To gain absolute control over merge we accept only one source rather than a directory."""
        self.basedir = basedir
        self.sources = source

    def _get_dataloader(self):
        """Initializes the Ansible plugin loader and creates a DataLoader instance.

        Returns:
            DataLoader: An instance of Ansible's DataLoader with the base directory set.
        """
        init_plugin_loader()
        loader = DataLoader()
        loader.set_basedir(str(self.basedir))
        return loader

    def get_inventory_manager(self):
        """Creates and returns an InventoryManager instance.

        Returns:
            InventoryManager: An instance of Ansible's InventoryManager.
        """
        return InventoryManager(self._get_dataloader(), self.sources)

    def get_variable_manager(self):
        """Creates and returns a VariableManager instance.

        Returns:
            VariableManager: An instance of Ansible's VariableManager.
        """
        return VariableManager(self._get_dataloader(), self.get_inventory_manager())

    def get_dataloader(self):
        """Returns a DataLoader instance.

        Returns:
            DataLoader: An instance of Ansible's DataLoader.
        """
        return self._get_dataloader()

    @property
    def source(self):
        """Returns the source or sources for the inventory.

        Returns:
            The source for the inventory.
        """
        return self.sources


class KitchenSinkInventory:
    """KitchenSinkInventory is a class that manages and caches various sources of inventory data.

    Attributes:
        cache_dir (Path): Directory path for caching inventory data.
        ignore_args (tuple): Arguments to ignore in caching.

    Methods:
        __init__():
            Initializes the KitchenSinkInventory instance and gathers initial facts.

        _gather_facts():
            Gathers and dumps inventory facts. Cached for 1 day.

        _proxmox_source() -> list[Host]:
            Retrieves inventory data from a Proxmox source. Cached for 1 day.

        _static_source() -> list[Host]:
            Retrieves inventory data from a static hosts TOML file. Cached for 1 day.

        _ldap_source() -> list[Host]:
            Retrieves inventory data from an LDAP source. Cached for 1 day.

        _nmap_source() -> list[Host]:
            Retrieves inventory data from an NMAP source. Cached for 1 day.

        _load_hosts(inv_mgr: InventoryManager, var_mgr: VariableManager) -> list[Host]:
            Loads hosts from the given inventory manager and variable manager.

        static_hosts() -> list[Host]:
            Property that returns the static hosts.

        proxmox_hosts() -> list[Host]:
            Property that returns the Proxmox hosts.

        nmap_hosts() -> list[Host]:
            Property that returns the NMAP hosts.

        ldap_hosts() -> list[Host]:
            Property that returns the LDAP hosts.

        merged_inventory() -> list[Host]:
            Property that returns the merged inventory from Proxmox, LDAP, and static hosts.
    """

    cache_dir = xdg_base_dirs.xdg_cache_home() / "dasbootstrap"
    ignore_args = tuple("self")

    def __init__(self):
        """Initializes the KitchenSinkInventory instance and gathers initial facts."""
        self._gather_facts()

    @cachier(
        stale_after=datetime.timedelta(days=1), cache_dir=cache_dir, allow_none=True
    )
    def _gather_facts(self):
        Actions.dump_inventory()

    @cachier(stale_after=datetime.timedelta(days=1), cache_dir=cache_dir)
    def _proxmox_source(self) -> list[Host]:
        inv_source = InventorySource(source=Inventory.DYNAMIC_PROXMOX)
        return self._load_hosts(
            inv_source.get_inventory_manager(), inv_source.get_variable_manager()
        )

    @cachier(stale_after=datetime.timedelta(days=1), cache_dir=cache_dir)
    def _static_source(self) -> list[Host]:
        inv_source = InventorySource(source=Inventory.STATIC_HOSTS_TOML)
        return self._load_hosts(
            inv_source.get_inventory_manager(), inv_source.get_variable_manager()
        )

    @cachier(stale_after=datetime.timedelta(days=1), cache_dir=cache_dir)
    def _ldap_source(self) -> list[Host]:
        inv_source = InventorySource(source=Inventory.DYNAMIC_LDAP)
        return self._load_hosts(
            inv_source.get_inventory_manager(), inv_source.get_variable_manager()
        )

    @cachier(stale_after=datetime.timedelta(days=1), cache_dir=cache_dir)
    def _nmap_source(self) -> list[Host]:
        inv_source = InventorySource(source=Inventory.DYNAMIC_NMAP)
        return self._load_hosts(
            inv_source.get_inventory_manager(), inv_source.get_variable_manager()
        )

    @classmethod
    def _load_hosts(
        cls, inv_mgr: InventoryManager, var_mgr: VariableManager
    ) -> list[Host]:
        hosts: list[Host] = inv_mgr.get_hosts()
        for host in hosts:
            host.vars = var_mgr.get_vars(host=host)

        return hosts

    @property
    def static_hosts(self) -> list[Host]:
        """Property that returns the static hosts.

        Returns:
            list[Host]: A list of static hosts.
        """
        return self._static_source()

    @property
    def proxmox_hosts(self) -> list[Host]:
        """Property that returns the Proxmox hosts.

        Returns:
            list[Host]: A list of Proxmox hosts.
        """
        return self._proxmox_source()

    @property
    def nmap_hosts(self) -> list[Host]:
        """Property that returns the NMAP hosts.

        Returns:
            list[Host]: A list of NMAP hosts.
        """
        return self._nmap_source()

    @property
    def ldap_hosts(self) -> list[Host]:
        """Property that returns the LDAP hosts.

        Returns:
            list[Host]: A list of LDAP hosts.
        """
        return self._ldap_source()

    @property
    def merged_inventory(self) -> list[Host]:
        """Property that returns the merged inventory from Proxmox, LDAP, and static hosts.

        Returns:
            list[Host]: A list of merged inventory hosts.
        """
        merged_inventory: list[Host] = self.proxmox_hosts
        merged_inventory.extend(self.ldap_hosts)
        merged_inventory.extend(self.static_hosts)
        return merged_inventory
