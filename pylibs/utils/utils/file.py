from __future__ import annotations

import os
import tarfile
import tempfile
import zipfile
from enum import StrEnum, auto
from io import BytesIO, StringIO
from pathlib import Path
from typing import IO

import zstandard as zstd

def stringify_path(*pathlike: Path | str) -> list[str]:
    return [str(pathlike) for pathlike in pathlike]


def pathify(*pathlike: str | Path) -> list[Path]:
    return [Path(path) for path in pathlike]


class CompressionTypes(StrEnum):
    ZIP = auto()
    ZSTD = auto()


def pack(src_dir: Path, out_file: Path | str, compression_type: CompressionTypes = CompressionTypes.ZSTD):
    """Use tar to create an archive and then a designated compression format to compress the archive

    Args:
      src_dir: The path to the folder to compress.
      out_file: The path to the target archive file.
      compression_type: The type of compression to use
    """
    src_dir_pth, out_file_pth = pathify(src_dir, out_file)

    with tarfile.open(f"{src_dir_pth}.tar", "x") as tar_file:
        tar_file.add(src_dir_pth, arcname=src_dir_pth)

    match compression_type:
        case CompressionTypes.ZSTD:
            cctx = zstd.ZstdCompressor(level=3, write_checksum=True, write_content_size=True, threads=-1)
            with open(f"{src_dir_pth}.tar", "rb") as tar_file, open(out_file_pth, "xb") as zstd_file:
                zstd_file.write(cctx.compress(tar_file.read()))
            os.remove(f"{src_dir_pth}.tar")
        case CompressionTypes.ZIP:
            raise NotImplementedError
        case _:
            raise TypeError(f"{compression_type} is not implemented")


def unpack(in_file: Path | str, out_dir: Path | str, compression_type: CompressionTypes = CompressionTypes.ZSTD):
    """:param out_dir:
    :param in_file:
    :param compression_type:
    :return:
    """
    in_file_pth, out_dir_pth = pathify(in_file, out_dir)

    match compression_type:
        case CompressionTypes.ZSTD:
            temp_in_stream: bytes = in_file_pth.read_bytes()
            dctx = zstd.ZstdDecompressor()
            temp_out_stream: BytesIO = BytesIO(dctx.decompress(data=temp_in_stream))

            # assume tar output, but verify
            if not tarfile.is_tarfile(temp_out_stream):
                raise OSError("This array of bytes does not seem to be a tarfile")

            if not out_dir_pth.exists():
                out_dir_pth.mkdir(parents=True, exist_ok=True)

            with tarfile.open(mode="r", fileobj=temp_out_stream) as tar_file:
                tar_file.extractall(path=out_dir_pth)
        case _:
            raise TypeError(f"{compression_type} is not implemented")


def zip_files(*files: Path) -> Path:
    with (
        tempfile.NamedTemporaryFile(delete=False, delete_on_close=False) as temp_file,
        zipfile.ZipFile(temp_file.name, mode="w") as zip_file,
    ):
        for file in files:
            zip_file.write(file)

        return Path(temp_file.name)


def ensure_path(*files: Path) -> bool:
    all_exist: bool = True
    for _, path in enumerate([path for path in files if not path.exists()]):
        path.parent.mkdir(parents=True, exist_ok=True, mode=0o700)
        path.is_file()
        if all_exist:
            all_exist = False
    return all_exist


def get_file_bytes(path: Path) -> bytes:
    """Read a file and return its contents as bytes."""
    with open(path, 'rb') as file:
        return file.read()

def get_file_io(path: Path) -> BytesIO:
    """Determine if a given path is a file, and if so, return a StringIO or BytesIO object based on the file type.

    Args:
        path: The path to the file.

    Returns:
        A StringIO object if the file is a text file, or a BytesIO object if the file is a binary file.

    Raises:
        FileNotFoundError: If the path does not exist.
        IsADirectoryError: If the path is a directory.
        ValueError: If the file type cannot be determined.
    """
    if not path.exists():
        raise FileNotFoundError(f"The path {path} does not exist.")
    if path.is_dir():
        raise IsADirectoryError(f"The path {path} is a directory.")

    with open(path, 'rb') as file:
      return BytesIO(file.read())

def backup_and_generate(*files):
    pass


def delete_files(*files: Path):
    for file in files:
        file.unlink(missing_ok=True)
