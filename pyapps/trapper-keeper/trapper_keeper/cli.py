"""Trapper Keeper cli interface."""
from trapper_keeper.stores.bolt_kvstore import BoltStore
from trapper_keeper.conf import TkSettings

class TrapperKeeper:
    """Trapper Keeper cli interface."""
    def __init__(self):
        self.tksettings = TkSettings.get_instance("trapper_keeper", xdg_config=True, auto_create=True)

    def settings(self):
        """Get the Trapper Keeper settings. Fire will interpret the TKSettings object and present the methods
        of TKSettings as commands in the CLI.

        Usage: __main__.py get_tk_settings <command|value>
          available commands:    get | get_attrs | get_instance | get_settings_folder |
                                 list_settings | load | save | set
          available values:      allow_missing_file | app_name | auto_create |
                                 chezmoi_db | db | flat_config | key | local_file |
                                 schema_version | settings_file_name |
                                 settings_folder | token | xdg_config

        """
        return self.settings

    def list_chez_buckets(self):
        """List the buckets in the Chezmoi store."""
        with BoltStore(self.tksettings.get("chezmoi_db"), readonly=True) as tx:
            for bucket in tx.bucket():
                print(bucket.first())
    # def get_chez_value(self, bucket: str, key: str):
    #     """Get a store based on the db_type.
    #
    #     Args:
    #         db_type (DbTypes): Type of store to open.
    #
    #     Returns:
    #         contextlib.AbstractContextManager: Store instance.
    #
    #     Raises:
    #         ValueError: Unsupported db_type
    #     """
    #     return get_store(db_type)
