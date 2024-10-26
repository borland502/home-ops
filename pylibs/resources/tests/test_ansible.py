"""Unit tests for Ansible resources module."""
import unittest

from resources.ansible import find_yaml_files

class TestAnsibleResources(unittest.TestCase):
    """Test cases for Ansible resources module."""

    def test_find_yaml_files(self):
        """Test that we can find all yaml files in a directory."""
        yaml_files = find_yaml_files("./")
        self.assertGreater(len(yaml_files), 0)

