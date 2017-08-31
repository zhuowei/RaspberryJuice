import collections

def flatten(l):
    for e in l:
        if isinstance(e, collections.Iterable) and not isinstance(e, str):
            for ee in flatten(e): yield ee
        else: yield e

def flatten_parameters_to_bytestring(l):
    return b",".join(map(_misc_to_bytes, flatten(l)))

def _misc_to_bytes(m):
    """
    Convert an arbitrary object into a string encoded as a CP437 series of bytes.

    See `Connection.send` for more details.
    """
    return str(m).encode("cp437")
    
def escape(s):
    """Escape content of strings which will break the api using html entity type escaping"""
    s = s.replace("&","&amp;")
    s = s.replace("\r\n","&#10;")
    s = s.replace("\n","&#10;")
    s = s.replace("\r","&#10;")
    s = s.replace("(","&#40;")
    s = s.replace(")","&#41;")
    s = s.replace(",","&#44;")
    s = s.replace("§","&sect;")
    return s
