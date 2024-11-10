"""
Configuration self for Trapper Keeper

This module defines the configuration self for Trapper Keeper.
"""
from pathlib import Path

from pykeepass import create_database
from xdg_base_dirs import xdg_data_home, xdg_config_home, xdg_state_home

from simple_toml_settings import TOMLSettings
from trapper_keeper.keegen import gen_utf8, gen_passphrase


class TkSettings(TOMLSettings):
    __file__ = 'tkself.toml'
    __section__ = 'trapper_keeper'

    # Define the self that are required in the self file
    db: str = str(xdg_data_home() / 'trapper_keeper' / 'tk.kdbx')
    token: str = str(xdg_config_home() / 'trapper_keeper' / 'tk.token')
    key: str = str(xdg_state_home() / 'trapper_keeper' / 'tk.key')

    # Chezmoi parameters (BoltDB)
    chezmoi_db: str = str(xdg_data_home() / 'chezmoi' / 'chezmoistate.boltdb')

    # SQLite Databases

    # KV Store of env vars

    def __post_create_hook__(self):
        # Ensure that the directories exist
        Path(self.key).parent.mkdir(mode=0o700, exist_ok=True, parents=True)
        Path(self.token).parent.mkdir(mode=0o700, exist_ok=True, parents=True)
        Path(self.db).parent.mkdir(mode=0o700, exist_ok=True, parents=True)
        Path(self.chezmoi_db).parent.mkdir(mode=0o700, exist_ok=True, parents=True)

        """Get the Trapper Keeper settings."""
        pkey = Path(self.key)
        ptoken = Path(self.token)
        pdb = Path(self.db)

        if not Path.exists(pkey) or pkey.stat().st_size == 0:
          pkey.parent.mkdir(mode=0o700, exist_ok=True, parents=True)
          with open(pkey, "w") as f:
            f.write(gen_utf8())
        if not Path.exists(ptoken) or ptoken.stat().st_size == 0:
          ptoken.parent.mkdir(mode=0o700, exist_ok=True, parents=True)
          with open(ptoken, "w") as f:
            f.write(gen_passphrase())

        if not pdb.exists():
          tk_store = create_database(filename=pdb, password=ptoken.read_text(encoding="utf-8"), keyfile=pkey)
          tk_store.save()
          self.set("db", str(pdb))

        self.save()
