"""CLI for managing Ansible containers."""
from __future__ import annotations

import ipaddress
from functools import cached_property

import fire
from ansible.inventory.host import Host
from dns import resolver, reversename
from dns.name import Name
from dns.resolver import NXDOMAIN
from homeops_inventory.sources import KitchenSinkInventory
from homeops_utils.paths import SecretsPaths
from homeops_utils.ssh import HostKeysUtils

from .ansible_commands import Actions, Plays


class AnsibleCommands:
    """A class to manage Ansible containers.

    This class provides methods to create, destroy, and update Ansible containers,
    as well as manage Ansible facts, collections, roles, and inventory.
    """
    def create_kvm(self, app_name: str):
        """Create and set up a new KVM using debian by default."""
        print(f"Creating KVM {app_name}...")
        actions = Actions(app_name)
        actions.create_kvm()
        # preemptively delete the host key
        HostKeysUtils(filename=SecretsPaths.KNOWN_HOSTS).remove(app_name)
        Actions.dump_inventory()
        actions.bootstrap_container(app_name)
        actions.ansible_container_user(app_name)

    def create_lxc(self, app_name: str):
        """Create and set up a new LXC container, installing favorites, and creating a service user."""
        print(f"Creating LXC container {app_name}...")
        actions = Actions(app_name)
        actions.create_lxc()
        # preemptively delete the host key
        HostKeysUtils(filename=SecretsPaths.KNOWN_HOSTS).remove(app_name)
        Actions.dump_inventory()
        actions.bootstrap_container(app_name)
        actions.ansible_container_user(app_name)
        # if a playbook exists with the app_name then run it
        actions.setup_playbook(app_name)

    def destroy(self, app_name: str = "lxc"):
        """Destroy an existing LXC container."""
        actions = Actions(app_name)
        actions.destroy_lxc()

    def update_facts(self):
        """Update facts for all managed hosts."""
        Actions.update_facts()

    def update_containers(self, user: str):
        """Update Ansible containers from requirements."""
        Plays.update_containers(user=user)

    def update_collections(self):
        """Update Ansible collections from requirements."""
        Actions.update_collections()

    def dump_inventory(self):
        """Dump the inventory to hosts.yaml."""
        Actions.dump_inventory()

    def update_roles(self):
        """Update Ansible roles from requirements."""
        Actions.update_roles()


class ActiveInventory:
    """A class to manage and preprocess the active inventory of Ansible hosts.

    This class provides methods to initialize the inventory, normalize hostnames,
    filter duplicates, and preprocess the inventory to remove duplicates and
    normalize hostnames.
    """

    def __init__(self):
        """Initialize the ActiveInventory class.

        This method initializes the ActiveInventory instance by creating a
        KitchenSinkInventory object, retrieving the merged inventory, and
        preprocessing it to remove duplicates and normalize hostnames.
        """
        kitchen_sink_inventory = KitchenSinkInventory()
        inventory: list[Host] = kitchen_sink_inventory.merged_inventory
        self._active_unique_inventory = self._preprocess_inventory(inventory)

    @classmethod
    def _normalize_hostname(cls, hostname: str) -> str | None:
        ip: None = None
        try:
            ip: str = str(ipaddress.ip_address(hostname)).strip()
            name: Name = reversename.from_address(ip)
            resolved_name: str = str(resolver.resolve(name, "PTR")[0])

            if resolved_name.endswith("."):
                hostname = resolved_name[:-1]

        except NXDOMAIN:
            return ip

        except ValueError:
            pass

        if hostname.startswith("*"):
            return None

        hostname = hostname.lower()
        hostname = hostname.split(".")[0]
        return hostname

    @classmethod
    def _filter_duplicates(cls, filtered_inventory, host: Host):
        # We are only interested in sources that provide Host objects
        if host is None or not isinstance(host, Host):
            return

        # hostname is not from nmap, so normalize and store host
        host.name = cls._normalize_hostname(host.name)
        if host.name is None:
            return

        if filtered_inventory.get(f"{host.name}") is None:
            filtered_inventory[f"{host.name}"] = host
        for k in host.vars:
            if k not in filtered_inventory[f"{host.name}"].vars:
                filtered_inventory[f"{host.name}"].vars[k] = host.vars[k]

    @classmethod
    def _host_generator(cls, inventory: list[Host]) -> Host:
        yield inventory.pop()

    def _preprocess_inventory(self, inventory: list[Host]) -> dict[str, Host]:
        filtered_inventory: dict[str, Host] = {}

        for host in inventory:
            self._filter_duplicates(filtered_inventory, host)

        return filtered_inventory

    @cached_property
    def inventory(self) -> dict[str, Host]:
        """Return the active unique inventory as a dictionary of hosts."""
        return self._active_unique_inventory

    def run(self) -> None:
        """Print the active unique inventory."""
        print(self.inventory)


if __name__ == "__main__":
    fire.Fire()
