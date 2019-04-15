import collections
import sys
import locale

def flatten(l):
    for e in l:
        if isinstance(e, collections.Iterable) and not isinstance(e, str):
            for ee in flatten(e): yield ee
        else: yield e

def flatten_parameters_to_bytestring(l):
    return b",".join(map(_misc_to_bytes, flatten(l)))

def _misc_to_bytes(m):
    """
    Convert an arbitrary object into a string encoded using default OS encoding series of bytes.

    See `Connection.send` for more details.
    """

    code, encoding = locale.getdefaultlocale()
    return str(m).encode(encoding)
    #return str(m).encode("cp437")
