from mcpi.minecraft import Minecraft
mc = Minecraft.create()

import random

# Random position
x = random.randrange(-1000, 1000)
z = random.randrange(-1000, 1000)
y = mc.getHeight(x, z)

# Teleport player
mc.player.setTilePos(x, y, z)
