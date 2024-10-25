"""Trapper Keeper core functionality."""

# ruff: noqa: I001
from sqlalchemy import Column, Integer, String, Unicode, create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.future import engine

from resources.paths import Directories
Base = declarative_base(bind=engine)
hosts_db = Directories.ANSIBLE_HOME

engine = create_engine()


class AnsibleHost(Base):
  """Ansible Hosts table."""
  __tablename__ = "hosts"
  # ruff: noqa: A003
  id = Column(Integer, primary_key=True)
  name = Column(Unicode(63))
  address = Column(String(63))
