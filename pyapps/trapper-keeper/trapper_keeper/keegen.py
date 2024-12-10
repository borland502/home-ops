"""Module to generate the keyfile and password from all printable characters.

Methods from [fauxfactory](https://github.com/omaciel/fauxfactory/tree/master)
"""

import secrets
import string
import unicodedata
from collections import namedtuple

from xkcdpass import xkcd_password as xp

# num words
TOKEN_SIZE: int = 5
KEY_SIZE: int = 190

UnicodePlane = namedtuple("UnicodePlane", ["min", "max"])
BMP = UnicodePlane(int("0x0000", 16), int("0xffff", 16))
SMP = UnicodePlane(int("0x10000", 16), int("0x1ffff", 16))

VALID_DELIMITERS: list[str] = [str(i) for i in range(10)]


def gen_passphrase(length=TOKEN_SIZE):
  """Generate a 1password style token that is assured to skate by anything with a complexity requirement other than length.

  :param length: size in words for the generated passphrase + 1
  :return: passphrase with an all caps word and symbol appended to the end
  """
  wordfile = xp.locate_wordfile()
  wordlist = xp.generate_wordlist(wordfile, min_length=TOKEN_SIZE, max_length=KEY_SIZE)
  pass_complexity_chk: str = "".join(
    [
      secrets.choice(string.digits),
      xp.generate_xkcdpassword(wordlist, numwords=1).upper(),
      secrets.choice("!@#$%^&*()_+{}|:<>?"),
    ]
  )
  passwd: str = xp.generate_xkcdpassword(
    wordlist, numwords=int(length), random_delimiters=True, valid_delimiters=VALID_DELIMITERS
  )
  return f"{passwd}{pass_complexity_chk}"


def gen_utf8(length=KEY_SIZE, smp=True, start=None, separator=""):
  """Return a random string made up of UTF-8 letters characters.

  Follows `RFC 3629`_.

  :param int length: Length for random data.
  :param str start: Random data start with.
  :param str separator: Separator character for start and random data.
  :param bool smp: Include Supplementary Multilingual Plane (SMP)
      characters
  :returns: A random string made up of ``UTF-8`` letters characters.
  :rtype: str

  . _`RFC 3629`: http://www.rfc-editor.org/rfc/rfc3629.txt

  """
  unicode_letters = list(_unicode_letters_generator(smp))
  output_string = "".join(secrets.choice(unicode_letters) for _ in range(length))

  if start:
    output_string = f"{start}{separator}{output_string}"[:length]
  return output_string

def generate_ed25519_key_pair() -> tuple[str, str]:
    """Generate a valid ed25519 SSH key pair."""
    from cryptography.hazmat.primitives import serialization
    from cryptography.hazmat.primitives.asymmetric import ed25519

    # Generate private key
    private_key = ed25519.Ed25519PrivateKey.generate()

    # Serialize private key
    private_key_bytes = private_key.private_bytes(
        encoding=serialization.Encoding.PEM,
        format=serialization.PrivateFormat.PKCS8,
        encryption_algorithm=serialization.NoEncryption()
    )
    private_key_str = private_key_bytes.decode("utf-8")

    # Generate public key
    public_key = private_key.public_key()

    # Serialize public key
    public_key_bytes = public_key.public_bytes(
        encoding=serialization.Encoding.OpenSSH,
        format=serialization.PublicFormat.OpenSSH
    )
    public_key_str = public_key_bytes.decode("utf-8")

    return private_key_str, public_key_str

def _unicode_letters_generator(smp=True):
  """Generate unicode characters in the letters category.

  :param bool smp: Include Supplementary Multilingual Plane (SMP)
      characters
  :return: a generator which will generates all unicode letters available

  """
  for i in range(BMP.min, SMP.max if smp else BMP.max):
    char = chr(i)
    if unicodedata.category(char).startswith("L"):
      yield char

