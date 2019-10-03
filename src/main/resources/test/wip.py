import modded.mcpi.minecraft as minecraftmodded
import modded.mcpi.block as blockmodded

mc = minecraftmodded.Minecraft.create()

entityid = mc.getPlayerEntityId("martinohanlon")
print(mc.entity.getEntities(entityid))

print(entityid)
