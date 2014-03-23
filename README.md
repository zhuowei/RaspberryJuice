A Bukkit plugin implementing a subset and superset of the Minecraft Pi Socket API.

Features currently supported:
 - world.get/setBlock
 - world.getBlockWithData
 - world.setBlocks
 - world.getPlayerIds
 - chat.post
 - events.clear
 - events.block.hits
 - player.getTile
 - player.setTile
 - player.getPos
 - player.setPos
 - player.getHeight

Features that can't be supported:
 - Camera angles

Extra features:
 - getBlocks(x1,y1,z1,x2,y2,z2) has been implemented
 - multiplayer support
   - name added as an option parameter to player.# calls
   - modded minecraft.py in python api library so player "name" can be passed on Minecraft.create(ip, port, name)
   - this change does not stop standard python api library being used



