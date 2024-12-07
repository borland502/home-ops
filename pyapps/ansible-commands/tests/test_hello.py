"""Hello unit test module."""

from ansible_commands.hello import hello


def test_hello():
    """Test the hello function."""
    assert hello() == "Hello ansible-commands"
