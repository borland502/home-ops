"""Hello unit test module."""

from homeops_inventory.hello import hello


def test_hello():
    """Test the hello function."""
    assert hello() == "Hello homeops-inventory"
