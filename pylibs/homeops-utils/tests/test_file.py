"""Test the file utility functions."""


import os
import shutil
import tempfile
import unittest
from pathlib import Path
from typing import ClassVar

from faker import Faker

from homeops_utils.file import pack, unpack


class TestFileUtil(unittest.TestCase):
    """Unit tests for file utility functions."""
    # "faker.providers"
    faker_provider_names: ClassVar[list[str]] = ["lorem.en_US", "file", "misc"]
    faker_providers: ClassVar[list[str]] = ["faker.providers." + p for p in faker_provider_names]

    def setUp(self):
        """Set up the test environment before each test.
        Creates a temporary source directory with 20 files.
        """
        self.fake: Faker = Faker(["en_US"], providers=self.faker_providers)

        with (tempfile.TemporaryDirectory(delete=False) as tmp_src_dir):
            self.src_dir = Path(tmp_src_dir)
            for _ in range(20):
                tmp_file: Path = Path.joinpath(self.src_dir, self.fake.file_path(absolute=False))
                tmp_file.parent.mkdir(parents=True, exist_ok=True)
                tmp_file.write_text(
                    self.fake.text(max_nb_chars=200), encoding="utf-8"
                )

    def test_pack_default(self):
        """Test the `pack` function with default parameters.
        Ensures the output file is created and has a non-zero size.
        """
        pack(src_dir=self.src_dir, out_file=f"{self.src_dir}.zst")
        self.assertTrue(Path(f"{self.src_dir}.zst").exists())
        self.assertFalse(Path(f"{self.src_dir}.tar").exists())
        self.assertGreater(Path(f"{self.src_dir}.zst").stat().st_size, 0)

    def test_unpack_default(self):
        """Test the `unpack` function with default parameters.
        Ensures the unpacked directory exists and contains the expected number of files.
        """
        self.test_pack_default()
        with (tempfile.TemporaryDirectory(delete=False) as tmp_src_dir):
            unpack(f"{self.src_dir}.zst", tmp_src_dir)
            out_dir = Path.joinpath(Path(tmp_src_dir), self.src_dir)
            self.assertTrue(out_dir.exists())
            self.assertTrue(out_dir.is_dir())
            self.assertEqual(20, len(os.listdir(out_dir)))

    def tearDown(self):
        """Clean up the test environment after each test.
        Removes the temporary source directory.
        """
        shutil.rmtree(path=self.src_dir)
