"""Tests for the paths module."""

import unittest
from pathlib import Path
from unittest.mock import patch

from faker import Faker

from homeops_utils.paths import SKIP_DIRS, SKIP_FILES, SkipPaths, export_paths


class TestPathsIntegration(unittest.TestCase):
    """Integration tests for the paths module."""

    def setUp(self):
        """Set up the test case with a Faker instance and ignore lists."""
        self.faker = Faker()
        self.ignore_dirs = SKIP_DIRS
        self.ignore_files = SKIP_FILES

    def test_is_ignored(self):
        """Test that paths are correctly identified as ignored or not."""
        fake_path = self.faker.file_path(depth=3, extension=[])
        ignored_path = f"{self.faker.file_path(depth=2, extension=[])}/{self.faker.random_element(self.ignore_dirs)}/subdir"
        self.assertFalse(SkipPaths.is_ignored(Path(fake_path), self.ignore_dirs))
        self.assertTrue(SkipPaths.is_ignored(Path(ignored_path), self.ignore_dirs))

    def test_export_paths(self):
        """Test that exported paths are directories and not ignored."""
        for path in export_paths():
            self.assertTrue(Path(path).is_dir())
            self.assertFalse(SkipPaths.is_ignored(Path(path), self.ignore_dirs))

    @patch("utils.paths.Path.is_dir", return_value=True)
    @patch("utils.paths.Path.is_file", return_value=False)
    def test_export_dirs_integration(self, mock_is_dir, mock_is_file):
        """Test that export_paths returns sorted directories, excluding ignored ones."""
        fake_dirs = sorted(
            [self.faker.file_path(depth=3, extension=[]) for _ in range(10)], key=len
        )
        expected_dirs = [
            d
            for d in fake_dirs
            if not any(ignored in d for ignored in self.ignore_dirs)
        ]
        with patch("utils.paths._export_paths", return_value=fake_dirs):
            paths = list(export_paths())
            paths = [
                p
                for p in paths
                if not any(ignored in p for ignored in self.ignore_dirs)
            ]
            self.assertTrue(all(Path(path).is_dir() for path in paths))
            self.assertEqual(paths, expected_dirs)

    @patch("utils.paths.Path.is_file", return_value=True)
    @patch("utils.paths.Path.is_dir", return_value=False)
    def test_export_files_integration(self, mock_is_file, mock_is_dir):
        """Test that export_paths returns sorted files."""
        fake_files = sorted(
            [
                self.faker.file_path(depth=3, category="text", extension=["txt"])
                for _ in range(10)
            ],
            key=len,
        )
        with patch("utils.paths._export_paths", return_value=fake_files):
            paths = list(export_paths())
            self.assertTrue(all(Path(path).is_file() for path in paths))
            self.assertEqual(paths, fake_files)

    def test_is_ignored_integration_dirs(self):
        """Test that directories are correctly identified as ignored or not."""
        fake_dir = self.faker.file_path(depth=3, extension=[])
        ignored_dir = f"{self.faker.file_path(depth=2, extension=[])}/{self.faker.random_element(self.ignore_dirs)}/subdir"
        self.assertFalse(SkipPaths.is_ignored(Path(fake_dir), self.ignore_dirs))
        self.assertTrue(SkipPaths.is_ignored(Path(ignored_dir), self.ignore_dirs))

    def test_is_ignored_integration_files(self):
        """Test that files are correctly identified as ignored or not."""
        for ext in self.ignore_files:
            fake_file = self.faker.file_path(depth=3, category="file", extension=[ext])
            self.assertTrue(SkipPaths.is_ignored(Path(fake_file), self.ignore_files))


if __name__ == "__main__":
    unittest.main()
