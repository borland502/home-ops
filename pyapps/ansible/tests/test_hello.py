"""Hello unit test module."""

from ansible.hello import hello


def test_hello():
    """Test the hello function."""
    assert hello() == "Hello ansible"
