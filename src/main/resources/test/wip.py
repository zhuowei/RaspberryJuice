import modded.mcpi.minecraft as minecraftmodded
import modded.mcpi.block as blockmodded

mc = minecraftmodded.Minecraft.create()

# entityid = mc.getPlayerEntityId("martinohanlon")
# print(entityid)
# print(mc.entity.getEntities(entityid))
# print(mc.entity.getEntities(entityid, typeId=50))
# print(mc.entity.removeEntities(entityid, typeId=50))
# print(mc.entity.getEntities(entityid))

# print(mc.getEntities())
# print(mc.removeEntities(typeId=50))
# print(mc.getEntities(typeId=50))

print(mc.player.getEntities(100))
print(mc.player.removeEntities(100, typeId=50))
print(mc.player.getEntities(100))