"""Main module for ansible-commands."""
from typer import Typer, Option

from utils.paths import BasePaths
from utils.ssh import HostKeysUtils

from .ansible_commands import Actions, Plays

app = Typer()


@app.command()
def create_kvm(
  app_name: str = Option("kvm", help="Virtual machine to manage (defaults to kvm)"),
):
  """Create and set up a new KVM using debian by default."""
  print(f"Creating KVM {app_name}...")
  actions = Actions(app_name)
  actions.create_kvm()
  # preemptively delete the host key
  HostKeysUtils(filename=BasePaths.KNOWN_HOSTS).remove(app_name)
  Actions.dump_inventory()
  actions.bootstrap_container(app_name)
  actions.ansible_container_user(app_name)


@app.command()
def create_lxc(
  app_name: str = Option("lxc", help="Application to manage (defaults to lxc)"),
):
  """Create and set up a new LXC container, installing favorites, and creating a service user."""
  print(f"Creating LXC container {app_name}...")
  actions = Actions(app_name)
  actions.create_lxc()
  # preemptively delete the host key
  HostKeysUtils(filename=BasePaths.KNOWN_HOSTS).remove(app_name)
  Actions.dump_inventory()
  actions.bootstrap_container(app_name)
  actions.ansible_container_user(app_name)
  # if a playbook exists with the app_name then run it
  actions.setup_playbook(app_name)


@app.command()
def destroy(
  app_name: str = Option("lxc", help="Application to manage (defaults to lxc)"),
):
  """Destroy an existing LXC container."""
  actions = Actions(app_name)
  actions.destroy_lxc()


@app.command()
def update_facts():
  """Update facts for all managed hosts."""
  Actions.update_facts()


@app.command()
def update_containers(
  user: str = Option("user", help="Ansible user to run playbook under"),
):
  """Update Ansible containers from requirements."""
  Plays.update_containers(user=user)


@app.command()
def update_collections():
  """Update Ansible collections from requirements."""
  Actions.update_collections()


@app.command()
def dump_inventory():
  """Dump the inventory to hosts.yaml."""
  Actions.dump_inventory()


@app.command()
def update_roles():
  """Update Ansible roles from requirements."""
  Actions.update_roles()


if __name__ == "__main__":
  app()
