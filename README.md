RaspberryJuice - A Bukkit plugin which implements the Minecraft Pi Socket API.

# ADD entities to Minecraft using python!!

Feature added to the Bukkit plugin
 - world.showWarning : simple log for server
 - world.setEntity : adds a new entity in given coordinates

Usage example: 

```
import mcpi.minecraft as minecraft
import mcpi.entity as Entity

mc = minecraft.Minecraft.create()

mc.showWarning("My first warning")

pos = mc.player.getPos()
x = y = z = 100
mc.showWarning("Howly crap, creating entities")

for i in range(0,20):
    mc.setEntity(pos.x, pos.y, pos.z, Entity.SHEEP)
```

Check Entity.py in mcpi in resources to learn about available entities list

Features currently supported:
 - world.get/setBlock
 - world.getBlockWithData
 - world.setBlocks
 - world.getPlayerIds
 - world.getBlocks
 - chat.post
 - events.clear
 - events.block.hits
 - player.getTile
 - player.setTile
 - player.getPos
 - player.setPos
 - world.getHeight
 - entity.getTile
 - entity.setTile
 - entity.getPos
 - entity.setPos

Features that can't be supported:
 - Camera angles

Extra features(**):
 - getBlocks(x1,y1,z1,x2,y2,z2) has been implemented
 - getDirection, getRotation, getPitch functions - get the 'direction' players and entities are facing
 - getPlayerId(playerName) - get the entity of a player by name
 - pollChatPosts() - get events back for posts to the chat
 - multiplayer support
   - name added as an option parameter to player calls
   - modded minecraft.py in python api library so player "name" can be passed on Minecraft.create(ip, port, name)
   - this change does not stop standard python api library being used
 - the default tcp port can be changed in config.yml

** to use the extra features an modded version of the java and python libraries that were originally supplied by Mojang with the Pi is required, https://github.com/zhuowei/RaspberryJuice/tree/master/src/main/resources/mcpi.  You only need the modded libraries to use the extra features, the original libraries still work, you just wont be able to use the extra features

** please note extra features are NOT guaranteed to be maintained in future releases, particularly if updates are made to the original Pi API which replace the functionality


Version history:
 - 1.1 - Initial release
 - 1.1.1 - block hit events
 - 1.2 - added world.getBlockWithData
 - 1.3 - getHeight, multiplayer, getBlocks
 - 1.4 - bug fixes, port specified in config.yml
 - 1.4.2 - bug fixes
 - 1.5 - entity functions
 - 1.6 - added getPlayerId(playerName), getDirection, getRotation, getPitch
 - 1.7 - added pollChatPosts() & block update performance improvements
 - 1.8 - minecraft version 1.9.2 compatibility
 