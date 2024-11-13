"""Tests for the trapper_keeper module."""
import shutil
import tempfile
import unittest
from pathlib import Path
from unittest.mock import mock_open, patch

from faker import Faker

from trapper_keeper.conf import TkSettings
from trapper_keeper.stores.keepass_store import create_kp_db
from trapper_keeper.tk import _get_chezmoi_store


class TestTrapperKeeper(unittest.TestCase):
  """Tests for the trapper_keeper module."""

  def setUp(self):
    """Sets up the test environment."""
    self.fake = Faker()

    with tempfile.TemporaryDirectory(delete=False) as tmpdir:
      self.parent_dir = Path(tmpdir)
      settings = TkSettings("trapper_keeper", auto_create=False, local_file=True)

      kp_key = f"{tmpdir}/key"
      kp_token = f"{tmpdir}/token"
      kp_db = f"{tmpdir}/kp.kdbx"

      Path(kp_key).write_text(self.fake.password())
      Path(kp_token).write_text(self.fake.password())

      # Create keepass db file
      # TODO: Migrate to Pathlike rather than Path
      fresh_db = create_kp_db(Path(kp_db), Path(kp_token),
                 Path(kp_key))
      fresh_db.save()

      prop_path = f"{tmpdir}/sqlite.sqlite"
      bolt_path = f"{tmpdir}/bolt.db"

      settings.set("key", kp_key)
      settings.set("token", kp_token)
      settings.set("db", kp_db)
      settings.set("sqlite_db", prop_path)
      settings.set("bolt_db", bolt_path)
      settings.save()

      self.settings = settings

  def test_chezmoi_bolt_db_rw(self):
    """Test that we can read and write to a bolt db."""
    # Create boltdb file
    self.bolt_path = self.settings.get("bolt_db")

    Path(self.bolt_path).touch()
    with _get_chezmoi_store(self.settings, readonly=False) as tx:
      config_state = tx.create_bucket(b"configState")
      config_state.put(b"configState", b"1")
      tx.entry_state = tx.create_bucket(b"entryState")
      tx.git_external = tx.create_bucket(b"gitRepoExternalState")
      tx.git_hub_keys = tx.create_bucket(b"gitHubKeysState")
      tx.git_hub_latest_release = tx.create_bucket(b"gitHubLatestReleaseState")
      tx.git_hub_tags = tx.create_bucket(b"gitHubTagsState")
      tx.script_state = tx.create_bucket(b"scriptState")

    with _get_chezmoi_store(self.settings) as tx:
      self.assertIsNotNone(tx)
      config_state = tx.bucket(b"configState")
      self.assertIsNotNone(config_state)
      config_state_val = config_state.get(b"configState")
      self.assertIsNotNone(config_state_val)
      entry_state = tx.bucket(b"entryState")
      self.assertIsNotNone(entry_state)
      github_keys = tx.bucket(b"gitHubKeysState")
      self.assertIsNotNone(github_keys)
      github_release = tx.bucket(b"gitHubLatestReleaseState")
      self.assertIsNotNone(github_release)
      github_tags = tx.bucket(b"gitHubTagsState")
      self.assertIsNotNone(github_tags)
      git_external = tx.bucket(b"gitRepoExternalState")
      self.assertIsNotNone(git_external)
      script_state = tx.bucket(b"scriptState")
      self.assertIsNotNone(script_state)

  @patch("trapper_keeper.conf.Path.mkdir")
  @patch("trapper_keeper.conf.Path.exists", return_value=False)
  @patch("trapper_keeper.conf.Path.stat")
  @patch("trapper_keeper.conf.open", new_callable=mock_open)
  @patch("trapper_keeper.conf.gen_utf8", return_value="mocked_utf8")
  @patch("trapper_keeper.conf.gen_passphrase", return_value="mocked_passphrase")
  @patch("trapper_keeper.conf.create_database")
  def creates_directories_and_files_if_not_exist(self, mock_create_db, mock_open, mock_mkdir):
    """Test that we can create directories and files if they do not exist."""
    settings = TkSettings("tk", auto_create=True, local_file=True)
    settings.__post_create_hook__()

    mock_mkdir.assert_any_call(mode=0o700, exist_ok=True, parents=True)
    mock_open.assert_any_call(Path(settings.key), "w")
    mock_open.assert_any_call(Path(settings.token), "w")
    mock_create_db.assert_called_once()

  @patch("trapper_keeper.conf.Path.exists", return_value=True)
  @patch("trapper_keeper.conf.Path.stat")
  @patch("trapper_keeper.conf.open", new_callable=mock_open)
  def does_not_create_files_if_exist(self, mock_open, mock_stat):
    """Test that we do not create files if they exist."""
    mock_stat.return_value.st_size = 1
    settings = TkSettings("tk", auto_create=True, local_file=True)
    settings.__post_create_hook__()

    mock_open.assert_not_called()

  @patch("trapper_keeper.conf.Path.exists", return_value=False)
  @patch("trapper_keeper.conf.Path.stat")
  @patch("trapper_keeper.conf.open", new_callable=mock_open)
  @patch("trapper_keeper.conf.gen_utf8", return_value="mocked_utf8")
  @patch("trapper_keeper.conf.gen_passphrase", return_value="mocked_passphrase")
  def creates_key_and_token_if_not_exist(self, mock_open):
    """Test that we can create a key and token file."""
    settings = TkSettings("tk", auto_create=True, local_file=True)
    settings.__post_create_hook__()

    mock_open.assert_any_call(Path(settings.key), "w")
    mock_open.assert_any_call(Path(settings.token), "w")
    mock_open().write.assert_any_call("mocked_utf8")
    mock_open().write.assert_any_call("mocked_passphrase")

  @patch("trapper_keeper.conf.Path.exists", return_value=False)
  @patch("trapper_keeper.conf.create_database")
  @patch("trapper_keeper.conf.Path.read_text", return_value="mocked_passphrase")
  def creates_database_if_not_exist(self, mock_read_text, mock_create_db):
    """Test that we can create a keepass db."""
    settings = TkSettings("tk", auto_create=True, local_file=True)
    settings.__post_create_hook__()

    mock_create_db.assert_called_once()
    mock_read_text.assert_called_once()

  def tearDown(self):
    """Tears down the test environment."""
    Path(self.settings.key).unlink(missing_ok=True)
    Path(self.settings.token).unlink(missing_ok=True)
    Path(self.settings.chezmoi_db).unlink(missing_ok=True)
    shutil.rmtree(self.parent_dir)

if __name__ == "__main__":
  unittest.main()
