"""Unit tests for path-related classes."""

import tempfile
import unittest
from pathlib import Path
from xdg_base_dirs import (
    xdg_data_home,
    xdg_cache_home,
    xdg_config_home,
    xdg_state_home,
    xdg_runtime_dir,
    xdg_config_dirs,
    xdg_data_dirs,
)

from utils.paths import BasePaths, XdgPaths, HomeOpsPaths, AnsiblePaths


class TestPaths(unittest.TestCase):
    """Unit tests for path-related classes."""

    def test_initializes_base_paths_correctly(self):
        """Test that BasePaths initializes correctly."""
        base_paths = BasePaths()
        self.assertEqual(base_paths.HOME, str(Path.home()))
        self.assertEqual(base_paths.TMP, tempfile.gettempdir())
        self.assertEqual(base_paths.SSH_HOME, f"{base_paths.HOME}/.ssh")
        self.assertEqual(base_paths.GNUPG_HOME, f"{base_paths.HOME}/.gnupg")
        self.assertEqual(base_paths.KNOWN_HOSTS, f"{base_paths.HOME}/.ssh/known_hosts")

    def test_initializes_xdg_paths_correctly(self):
        """Test that XdgPaths initializes correctly."""
        xdg_paths = XdgPaths()
        self.assertEqual(xdg_paths.XDG_DATA_HOME, str(xdg_data_home()))
        self.assertEqual(xdg_paths.XDG_CACHE_HOME, str(xdg_cache_home()))
        self.assertEqual(xdg_paths.XDG_CONFIG_HOME, str(xdg_config_home()))
        self.assertEqual(xdg_paths.XDG_STATE_HOME, str(xdg_state_home()))
        self.assertEqual(xdg_paths.XDG_RUNTIME_DIR, str(xdg_runtime_dir()))
        self.assertEqual(xdg_paths.XDG_CONFIG_DIRS, str(xdg_config_dirs()))
        self.assertEqual(xdg_paths.XDG_DATA_DIRS, str(xdg_data_dirs()))
        self.assertEqual(xdg_paths.XDG_BIN_HOME, f"{BasePaths.HOME}/.local/bin")
        self.assertEqual(xdg_paths.XDG_LIB_HOME, f"{BasePaths.HOME}/.local/lib")

    def test_initializes_home_ops_paths_correctly(self):
        """Test that HomeOpsPaths initializes correctly."""
        home_ops_paths = HomeOpsPaths()
        self.assertEqual(
            home_ops_paths.AUTOMATION_HOME, f"{XdgPaths.XDG_DATA_HOME}/automation"
        )
        self.assertEqual(
            home_ops_paths.PROOT, f"{home_ops_paths.AUTOMATION_HOME}/home-ops"
        )
        self.assertEqual(home_ops_paths.PROJECT_ROOT, home_ops_paths.PROOT)
        self.assertEqual(home_ops_paths.HOME_OPS_HOME, home_ops_paths.PROOT)

    def test_initializes_ansible_paths_correctly(self):
        """Test that AnsiblePaths initializes correctly."""
        ansible_paths = AnsiblePaths()
        self.assertEqual(
            ansible_paths.PBROOT, f"{HomeOpsPaths.PROOT}/ansible/playbooks"
        )
        self.assertEqual(ansible_paths.PLAYBOOK_ROOT, ansible_paths.PBROOT)
        self.assertEqual(ansible_paths.AHOME, f"{BasePaths.HOME}/.ansible")
        self.assertEqual(ansible_paths.ANSIBLE_HOME, ansible_paths.AHOME)
        self.assertEqual(ansible_paths.CHOME, f"{ansible_paths.AHOME}/collections")
        self.assertEqual(ansible_paths.RHOME, f"{ansible_paths.AHOME}/roles")
        self.assertEqual(ansible_paths.IHOME, f"{ansible_paths.AHOME}/inventory")
        self.assertEqual(ansible_paths.GVHOME, f"{ansible_paths.IHOME}/group_vars")
        self.assertEqual(ansible_paths.HVHOME, f"{ansible_paths.IHOME}/host_vars")
        self.assertEqual(
            ansible_paths.COLLECTIONS_REQS, f"{ansible_paths.CHOME}/requirements.yml"
        )
        self.assertEqual(
            ansible_paths.ROLES_REQS, f"{ansible_paths.RHOME}/requirements.yml"
        )
        self.assertEqual(
            ansible_paths.ALL_KVM_VARS, f"{ansible_paths.GVHOME}/proxmox_all_kvm.yaml"
        )
        self.assertEqual(ansible_paths.ALL_VARS, f"{ansible_paths.GVHOME}/all.yaml")
        self.assertEqual(
            ansible_paths.ALL_LXC_VARS, f"{ansible_paths.GVHOME}/proxmox_all_lxc.yaml"
        )
        self.assertEqual(
            ansible_paths.CHEZMOI_DATA, f"{ansible_paths.GVHOME}/chezmoi_data.yaml"
        )
        self.assertEqual(
            ansible_paths.STATIC_HOSTS, f"{ansible_paths.IHOME}/hosts.yaml"
        )
        self.assertEqual(ansible_paths.STATIC_HOSTS_YAML, ansible_paths.STATIC_HOSTS)
        self.assertEqual(
            ansible_paths.STATIC_HOSTS_TOML, f"{ansible_paths.IHOME}/hosts.toml"
        )
        self.assertEqual(ansible_paths.DYNAMIC_NMAP, f"{ansible_paths.IHOME}/nmap.yaml")
        self.assertEqual(
            ansible_paths.DYNAMIC_PROXMOX, f"{ansible_paths.IHOME}/proxmox.yaml"
        )
        self.assertEqual(
            ansible_paths.DYNAMIC_LDAP, f"{ansible_paths.IHOME}/microsoft.ad.ldap.yaml"
        )
        self.assertEqual(
            ansible_paths.DYNAMIC_SQLITE, f"{ansible_paths.IHOME}/sqlite.yaml"
        )
        self.assertEqual(
            ansible_paths.DBS_SQLITE,
            f"{XdgPaths.XDG_STATE_HOME}/sqlite/dasbootstrap.db",
        )
        self.assertEqual(
            ansible_paths.INVENTORY_ALL, f"{ansible_paths.IHOME}/inventory/"
        )


if __name__ == "__main__":
    """Run the unit tests."""
    unittest.main()
