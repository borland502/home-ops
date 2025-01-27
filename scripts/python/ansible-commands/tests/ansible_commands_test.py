"""Unit tests for Ansible commands."""

import unittest
from pathlib import Path

from homeops_utils.paths import AnsiblePaths

from ansible_commands.ansible_commands import INVENTORY, VARS


class TestAnsibleCommands(unittest.TestCase):
    """Unit tests for Ansible commands."""

    def test_inventory_contains_correct_path(self):
        """Test that the inventory contains the correct path."""
        expected_inventory = ["-i", f"{Path.home()}/.ansible/inventory/hosts.yaml"]
        self.assertEqual(INVENTORY, expected_inventory)

    def test_vars_contains_correct_paths(self):
        """Test that the vars contain the correct paths."""
        expected_vars = [
            "-i",
            f"{AnsiblePaths.IHOME}/hosts.yaml",
            "-e",
            f"@{AnsiblePaths.GVHOME}/all.yaml",
            "-e",
            f"@{AnsiblePaths.GVHOME}/proxmox_all_lxc.yaml",
            "-e",
            f"@{AnsiblePaths.GVHOME}/proxmox_all_kvm.yaml",
        ]
        self.assertEqual(VARS, expected_vars)
