"""Hello unit test module."""

from resources.hello import hello


def test_hello():
    """Test the hello function."""
    assert hello() == "Hello resources"
