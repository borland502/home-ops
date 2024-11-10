# """Tests for the trapper_keeper module."""
# import tempfile
# import unittest
# from pathlib import Path
#
# from faker import Faker
# from pykeepass.pykeepass import create_database
#
# from trapper_keeper.conf import TkSettings
# from trapper_keeper.tk import DbTypes, open_tk_store
#
#
# class TestTrapperKeeper(unittest.TestCase):
#   """Tests for the trapper_keeper module."""
#
#   def setUp(self):
#     """Sets up the test environment."""
#     self.fake = Faker()
#
#     with tempfile.TemporaryDirectory(delete=False) as tmpdir:
#       settings = TkSettings("trapper_keeper", auto_create=False)
#
#       kp_key = Path(tmpdir, "key")
#       kp_key.write_text(encoding="utf-8", data=self.fake.password())
#       kp_token = Path(tmpdir, "token")
#       kp_token.write_text(encoding="utf-8", data=self.fake.password())
#       kp_db = Path(tmpdir, "kp.kdbx")
#       prop_path = Path(tmpdir, "sqlite.sqlite")
#       bolt_path = Path(tmpdir, "bolt.db")
#
#       settings.set("key", kp_key)
#       settings.set("token", kp_token)
#       settings.set("db", kp_db)
#       settings.set("sqlite_db", prop_path)
#       settings.set("bolt_db", bolt_path)
#       settings.save()
#
#       self.settings = settings
#
#   def test_chezmoi_bolt_db_rw(self):
#     """Test that we can read and write to a bolt db."""
#     # Create boltdb file
#     self.bolt_path = self.settings.get("bolt_db")
#
#
#
#     self.bolt_path.touch()
#     with open_tk_store(db_type=DbTypes.BOLT, db_path=self.bolt_path, readonly=False) as tx:
#       config_state = tx.create_bucket(b"configState")
#       config_state.put(b"configState", b"1")
#       tx.entry_state = tx.create_bucket(b"entryState")
#       tx.git_external = tx.create_bucket(b"gitRepoExternalState")
#       tx.git_hub_keys = tx.create_bucket(b"gitHubKeysState")
#       tx.git_hub_latest_release = tx.create_bucket(b"gitHubLatestReleaseState")
#       tx.git_hub_tags = tx.create_bucket(b"gitHubTagsState")
#       tx.script_state = tx.create_bucket(b"scriptState")
#
#     with open_tk_store(DbTypes.BOLT, self.bolt_path, readonly=True) as tx:
#       self.assertIsNotNone(tx)
#       config_state = tx.bucket(b"configState")
#       self.assertIsNotNone(config_state)
#       config_state_val = config_state.get(b"configState")
#       self.assertIsNotNone(config_state_val)
#       entry_state = tx.bucket(b"entryState")
#       self.assertIsNotNone(entry_state)
#       github_keys = tx.bucket(b"gitHubKeysState")
#       self.assertIsNotNone(github_keys)
#       github_release = tx.bucket(b"gitHubLatestReleaseState")
#       self.assertIsNotNone(github_release)
#       github_tags = tx.bucket(b"gitHubTagsState")
#       self.assertIsNotNone(github_tags)
#       git_external = tx.bucket(b"gitRepoExternalState")
#       self.assertIsNotNone(git_external)
#       script_state = tx.bucket(b"scriptState")
#       self.assertIsNotNone(script_state)
#
#   def test_keepass_basic_db_rw(self):
#     """Test that we can read and write to a keepass db."""
#     create_database(self.kp_db, self.kp_token.read_text(encoding="utf-8"), keyfile=self.kp_key)
#     with open_tk_store(db_type=DbTypes.KP, db_path=self.kp_db, token=self.kp_token, key=self.kp_key) as tk_db:
#       self.assertTrue(self.kp_token.exists())
#       self.assertTrue(self.kp_key.exists())
#       for _ in range(19):
#         username = self.fake.user_name()
#         password = self.fake.password()
#         url = self.fake.url()
#         title = self.fake.user_name()
#         tk_db.add_entry(tk_db.root_group, title, username, password, url)
#       tk_db.save()
#
#     with open_tk_store(db_type=DbTypes.KP, db_path=self.kp_db, token=self.kp_token, key=self.kp_key) as tk_db:
#       self.assertTrue(self.kp_db.exists())
#       self.assertEqual(19, len(tk_db.entries))
#
#   def tearDown(self):
#     """Tears down the test environment."""
#     dir_parent: Path = self.kp_key.parent
#     self.kp_key.unlink(missing_ok=True)
#     self.kp_token.unlink(missing_ok=True)
#     self.prop_path.unlink(missing_ok=True)
#     self.bolt_path.unlink(missing_ok=True)
#     self.kp_db.unlink(missing_ok=True)
#     dir_parent.rmdir()
#
#
# if __name__ == "__main__":
#   unittest.main()
