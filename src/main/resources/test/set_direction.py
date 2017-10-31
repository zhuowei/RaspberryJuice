from modded.mcpi.minecraft import Minecraft
from time import sleep
mc = Minecraft.create()
#while True:
#    d = mc.player.getDirection()
#    mc.postToChat(d)
#    sleep(1)
#    mc.player.setDirection(0, 0, 1)
#    sleep(1)

#while True:
#    r = mc.player.getRotation()
#    mc.postToChat(r)
#    sleep(1)
#    mc.player.setRotation(90)
#    sleep(1)

while True:
    r = mc.player.getPitch()
    mc.postToChat(r)
    sleep(1)
    mc.player.setPitch(-45)
    sleep(1)