"""Ansible Commands module.

This module provides classes and methods to interact with LXC and KVM containers using Ansible.
It includes functionalities to create, destroy, bootstrap, configure user access, and run playbooks for containers.
"""

import os

import ansible_runner
from resources.ansible import find_playbook
from utils.paths import AnsiblePaths

# Inventory and variable definitions for Ansible commands
INVENTORY: list[str] = ["-i", str(AnsiblePaths.IHOME + "/hosts.yaml")]
VARS: list[str] = [
    *INVENTORY,
    *[
        "-e",
        "@" + str(AnsiblePaths.ALL_VARS),
        "-e",
        "@" + str(AnsiblePaths.ALL_LXC_VARS),
        "-e",
        "@" + str(AnsiblePaths.ALL_KVM_VARS),
    ],
]


class Plays:
    """Class for running playbooks."""

    @classmethod
    def update_containers(cls, user: str):
        """Update all LXC & KVM containers.

        Args:
            user (str): The user to run the playbook as.
        """
        ansible_runner.run_command(
            executable_cmd="ansible-playbook",
            project_dir=AnsiblePaths.PBROOT,
            cmdline_args=[
                "ansible/playbooks/maintainence/pkg-update.yaml",
                *VARS,
                "--user",
                user,
            ],
        )


class Actions:
    """Class for interacting with LXC containers using Ansible.

    Provide methods to create, destroy, bootstrap, configure user access,
    and run playbooks for LXC containers.
    """

    def __init__(self, app="lxc"):
        """Initialize the Actions class with the specified application name.

        Args:
            app (str, optional): The application name for the LXC container.
                Defaults to "lxc".
        """
        self.app = app
        self.app_path = f"@{AnsiblePaths.HVHOME}/{app}.yaml"

        ansible_runner.run_command(
            executable_cmd="ansible-inventory",
            cmdline_args=[
                "all",
                "--export",
                "--list",
                "--yaml",
                *VARS,
                "--output",
                str(AnsiblePaths.STATIC_HOSTS),
            ],
        )

    def create_kvm(self):
        """Create a new KVM using the specified application role."""
        ansible_runner.run_command(
            executable_cmd="ansible",
            cmdline_args=[
                "localhost",
                "-m",
                "import_role",
                "-a",
                "name=cielito.proxmox.create_kvm",
                "-e",
                self.app_path,
                *VARS,
                "--user",
                "root",
            ],
        )

    def create_lxc(self):
        """Create a new LXC container using the specified application role.

        Uses the `cielito.proxmox.create_lxc` Ansible role to create a new
        LXC container based on the configuration defined in the application's
        playbook.
        """
        print(f"Creating LXC container {self.app}...")
        ansible_runner.run_command(
            executable_cmd="ansible",
            cmdline_args=[
                "localhost",
                "-m",
                "import_role",
                "-a",
                "name=maxhoesel.proxmox.lxc_container",
                "-e",
                self.app_path,
                *VARS,
                "--user",
                "root",
            ],
        )

    def destroy_lxc(self):
        """Destroy the specified LXC container using the application role.

        Uses the `technohouser.destroy_xc` Ansible role to destroy the LXC
        container associated with the current application.
        """
        ansible_runner.run_command(
            executable_cmd="ansible",
            cmdline_args=[
                "localhost",
                "-m",
                "import_role",
                "-a",
                "name=technohouser.destroy_lxc",
                "-e",
                self.app_path,
                *VARS,
                "--user",
                "root",
            ],
        )

    def bootstrap_container(self, app):
        """Bootstrap the specified LXC container using the application role.

        Uses the `technohouser.bootstrap` Ansible role to bootstrap the
        given LXC container, presumably by installing the required software
        and configuration.

        Args:
            app (str): The application name for the LXC container.
        """
        ansible_runner.run_command(
            executable_cmd="ansible",
            cmdline_args=[
                app,
                "-m",
                "import_role",
                "-a",
                "name=technohouser.dasbootstrap.bootstrap",
                *VARS,
                "-e",
                self.app_path,
                "--user",
                "root",
            ],
        )

    def ansible_container_user(self, app):
        """Configure the specified LXC container for Ansible access.

        Uses the `technohouser.ansible` Ansible role to configure the
        given LXC container to allow SSH access by the 'ansible' user, likely
        for further automation.

        Args:
            app (str): The application name for the LXC container.
        """
        ansible_runner.run_command(
            executable_cmd="ansible",
            cmdline_args=[
                app,
                "-m",
                "import_role",
                "-a",
                "name=technohouser.dasbootstrap.ansible",
                "-e",
                self.app_path,
                *VARS,
                "--user",
                "ansible",
            ],
        )

    @classmethod
    def dump_inventory(cls):
        """Convert dynamic inventory sources into a single static host file."""
        ansible_runner.run_command(
            executable_cmd="ansible-inventory",
            cmdline_args=[
                "all",
                "--export",
                "--list",
                "--yaml",
                *VARS,
                "--output",
                str(AnsiblePaths.STATIC_HOSTS),
            ],
        )

    @classmethod
    def update_collections(cls):
        """Update galaxy collections for Dasbootstrap."""
        ansible_runner.run_command(
            executable_cmd="ansible-galaxy",
            cmdline_args=[
                "collection",
                "install",
                "-r",
                str(AnsiblePaths.COLLECTIONS_REQS),
                "--force",
            ],
        )

    @classmethod
    def update_facts(cls):
        """Run the ansible.builtin.setup module against all hosts with the service user."""
        ansible_runner.run_command(
            executable_cmd="ansible",
            cmdline_args=["all", "-m", "setup", *VARS, "--user", "ansible"],
        )

    @classmethod
    def update_roles(cls):
        """Update galaxy roles for Dasbootstrap."""
        ansible_runner.run_command(
            executable_cmd="ansible-galaxy",
            cmdline_args=[
                "role",
                "install",
                "-r",
                str(AnsiblePaths.ROLES_REQS),
                "--force",
            ],
        )

    @classmethod
    def setup_playbook(cls, app) -> None:
        """Find and execute an ansible playbook using the service user.

        Args:
            app (str): The application name for the playbook.
        """
        playbook = find_playbook(app)

        if playbook is None:
            return

        ansible_runner.run_command(
            executable_cmd="ansible-playbook",
            cmdline_args=[playbook, *VARS, "--user", "ansible"],
        )

    @classmethod
    def purge_cache(cls):
        """Erases all files within the ~/.cache/ansible directory."""
        if os.path.exists(AnsiblePaths.CHOME):
            for filename in os.listdir(AnsiblePaths.CHOME):
                file_path = os.path.join(AnsiblePaths.CHOME, filename)
                try:
                    os.remove(file_path)
                except OSError as e:
                    print(f"Error deleting file {file_path}: {e}")
            print(
                f"Contents of Ansible cache directory '{AnsiblePaths.CHOME}' erased successfully."
            )
        else:
            print("Ansible cache directory not found.")
