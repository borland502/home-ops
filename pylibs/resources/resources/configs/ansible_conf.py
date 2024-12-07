"""Configuration self for Trapper Keeper.

This module defines the configuration self for Trapper Keeper.
"""

from simple_toml_settings import TOMLSettings

from resources.configs.baseconf import BaseConfig


class AnsibleCommandSettings(BaseConfig):
  """Common settings for Trapper Keeper."""

  __file__ = "config.toml"
  __section__ = "ansible_commands"


class Directories(TOMLSettings):
  """Path constants for both the project and for the user level ansible installation at HOME/.ansible."""

  __file__ = "config.toml"
  __section__ = "directories"

  proot: str = f"{AnsibleCommandSettings.xdg_data_home}/automation/dasbootstrap"
  project_root: str = proot
  xdg_bin_home: str = f"{AnsibleCommandSettings.home}/.local/bin"
  xdg_lib_home: str = f"{proot}/.local/lib"
  pbroot: str = f"{proot}/ansible/playbooks"
  playbook_root: str = pbroot
  croot: str = f"{proot}/ansible"
  ahome: str = f"{AnsibleCommandSettings.home}/.ansible"
  ansible_home: str = ahome
  chome: str = f"{ahome}/collections"
  rhome: str = f"{ahome}/roles"
  ihome: str = f"{ahome}/inventory"
  gvhome: str = f"{ihome}/group_vars"
  hvhome: str = f"{ihome}/host_vars"
