"""Offer a persistent dictionary with an API compatible with shelve and anydbm."""

import contextlib
import csv
import json
import os
import pickle
import shutil


class PersistentDict:
    """https://code.activestate.com/recipes/576642-persistent-dict-with-multiple-standard-file-format/.
    Persistent dictionary with an API compatible with shelve and anydbm.

    The dict is kept in memory, so the dictionary operations run as fast as
    a regular dictionary.

    Write to disk is delayed until close or sync (similar to gdbm's fast mode).

    Input file format is automatically discovered.
    Output file format is selectable between pickle, json, and csv.
    All three serialization formats are backed by fast C implementations.
    """

    def __init__(
        self, filename, flag="c", mode=None, file_format="pickle", *args, **kwds
    ):
        """Initialize a persistent dictionary."""
        self.flag = flag  # r=readonly, c=create, or n=new
        self.mode = mode  # None or an octal triple like 0644
        self.format = file_format  # 'csv', 'json', or 'pickle'
        self.filename = filename
        if flag != "n" and os.access(filename, os.R_OK):
            # ruff: noqa: SIM115
            fileobj = open(filename, "rb" if file_format == "pickle" else "r")
            with fileobj:
                self.load(fileobj)
        dict.__init__(self, *args, **kwds)

    def sync(self):
        """Write dict to disk."""
        if self.flag == "r":
            return
        filename = self.filename
        tempname = f"{filename}.tmp"
        fileobj = open(tempname, "wb" if self.format == "pickle" else "w")
        try:
            self.dump(fileobj)
        except Exception:
            os.remove(tempname)
            raise
        finally:
            fileobj.close()
        shutil.move(tempname, self.filename)  # atomic commit
        if self.mode is not None:
            os.chmod(self.filename, self.mode)

    def close(self):
        """Synchronize and close file."""
        self.sync()

    def __enter__(self):
        """Context manager enter."""
        return self

    def __exit__(self, *exc_info):
        """Context manager exit."""
        self.close()

    def dump(self, fileobj):
        """Pickle to file."""
        if self.format == "csv":
            csv.writer(fileobj).writerows(self.items())
        elif self.format == "json":
            json.dump(self, fileobj, separators=(",", ":"))
        elif self.format == "pickle":
            pickle.dump(dict(self), fileobj, 2)
        else:
            raise NotImplementedError(f"Unknown format: {self.format!r}")

    def load(self, fileobj):
        """Load from file."""
        # try formats from most restrictive to the least restrictive
        for loader in (pickle.load, json.load, csv.reader):
            fileobj.seek(0)
            # noinspection PyBroadException
            with contextlib.suppress(Exception):
                return self.update(loader(fileobj))
        raise ValueError("File not in a supported format")
