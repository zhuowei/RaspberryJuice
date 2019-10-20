import modded.mcpi.minecraft as minecraftmodded
import modded.mcpi.block as blockmodded
from time import sleep

mc = minecraftmodded.Minecraft.create()

entityid = mc.getPlayerEntityId("martinohanlon")
print(entityid)
# print(mc.entity.getEntities(entityid))
# print(mc.entity.getEntities(entityid, typeId=50))
# print(mc.entity.removeEntities(entityid, typeId=50))
# print(mc.entity.getEntities(entityid))
while True:
    # print(mc.entity.pollChatPosts(entityid))
    # print(mc.entity.pollBlockHits(entityid))
    # print(mc.entity.pollProjectileHits(entityid))
    # print(mc.events.pollChatPosts())
    # print(mc.events.pollBlockHits())
    # print(mc.events.pollProjectileHits())
    #mc.player.clearEvents()
    print(mc.player.pollChatPosts())
    print(mc.player.pollBlockHits())
    print(mc.player.pollProjectileHits())
    sleep(2)
    # print(mc.player.pollChatPosts())
    # print(mc.player.pollBlockHits())
    # print(mc.player.pollProjectileHits())

# print(mc.getEntities())
# print(mc.removeEntities(typeId=50))
# print(mc.getEntities(typeId=50))

#print(mc.player.getEntities(35))
#print(mc.player.removeEntities(10, typeId=50))
# print(mc.player.getEntities())