"""
Gets entity types from RaspberryJuice and creates the constants for inclusion in mcpi/entity.py 
"""
from mcpi.minecraft import Minecraft
mc = Minecraft.create()
entity_types = mc.getEntityTypes()
for entity_type in entity_types:
    print("""{} = Entity({}, "{}")""".format(entity_type.name, entity_type.id, entity_type.name))
