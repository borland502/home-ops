"""This module is the entry point for the CLI. It uses the Fire library to create a CLI for Trapper Keeper."""

import fire

from trapper_keeper.cli import TrapperKeeper

fire.Fire(TrapperKeeper)
