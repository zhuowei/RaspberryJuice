# RaspberryJuice

一個Minecraft Bukkit插件，用來實現與Python連接，使用Python控制Minecraft Server

## Commands

### Commands supported

#### world
 - world.getBlock(x:int, y:int, z:int) -> str
   - Get the block of the input position
 
 - world.getBlocks(x1:int, y1:int, z1:int, x2:int, y2:int, z2:int) -> list
   - Get the blocks of the input position range
 
 - world.setBlock(x:int, y:int, z:int, block:str) -> None:
   - Set the block of the input position
 
 - world.setBlocks(x1:int, y1:int, z1:int, x2:int, y2:int, z2:int, block) -> None:
    - Set the blocks of the input position range
 
 - world.getHeight(x:int, z:int) -> int:
 
 - world.getPlayerEntityIds() -> list:
 
 - world.postToChat(*msg) -> None:
 
 - world.setSign(x:int, y:int, z:int, signType:str, signDir:int, line1:str="", line2:str="", line3:str="", line4:str="") -> None:
 
 - world.setWallSign(x:int, y:int, z:int, signType:str, signDir:int, line1="",line2="",line3="",line4="") -> None:
 
 - world.spawnEntity(x:int, y:int, z:int, entityID:int) -> int:
 
 - world.createExplosion(x:int, y:int, z:int, power:int=4) -> None:
 
 - world.getPlayerEntityId(name:str) -> int:
 
 - world.create(address = "localhost", port = 4711):
 
 ---
 
#### player
 - player.getPos() -> Vec3:
 
 - player.setPos(x:float, y:float, z:float) -> None:
 
 - player.getTilePos() -> Vec3:
 
 - player.setTilePos(x:int, y:int, z:int) -> None:
 
 - player.getDirection() -> Vec3:
 
 - player.setDirection(x:float, y:float, z:float) -> None:
 
 - player.getRotation() -> float:
 
 - player.setRotation(yaw) -> None:
 
 - player.getPitch() -> float:
 
 - player.setPitch(pitch) -> None:
 
 - player.sendTitle(title:str, subTitle:str="", fadeIn:int=10, stay:int=70, fadeOut:int=20) -> None:
 
 ---
 
#### entity
 - entity.getPos(ID) -> Vec3:
   
 - entity.setPos(ID, x:float, y:float, z:float) -> None:
 
 - entity.getTilePos(ID) -> Vec3:
 
 - entity.setTilePos(ID, x:int, y:int, z:int) -> None:
 
 - entity.getDirection(ID) -> Vec3:
 
 - entity.setDirection(ID, x:float, y:float, z:float) -> None:
 
 - entity.getRotation(ID) -> float:
 
 - entity.setRotation(ID, yaw) -> float:
 
 - entity.getPitch(ID) -> float:
 
 - entity.setPitch(ID, pitch) -> None:
 
 - entity.getName(ID):
 

### Commands that can't be supported

 - Camera angles

### Extra commands

 - getBlocks(x1,y1,z1,x2,y2,z2) has been implemented
 - getDirection, getRotation, getPitch functions - get the 'direction' players and entities are facing
 - setDirection, setRotation, setPitch functions - set the 'direction' players and entities are facing
 - getPlayerId(playerName) - get the entity of a player by name
 - pollChatPosts() - get events back for posts to the chat
 - setSign(x,y,z,block type id,data,line1,line2,line3,line4)
   - Wall signs (id=68 or block.SIGN_WALL.id) require data for facing direction 2=north, 3=south, 4=west, 5=east
   - Standing signs (id=63 or block.SIGN_STANDING.id) require data for facing rotation (0-15) 0=south, 4=west, 8=north, 12=east
 - spawnEntity(x,y,z,entity) - creates an entity and returns its entity id. see entity.py for list.
 - getEntityTypes - returns all the entities supported by the server.
 - entity.getName(id) - get a player name for entity id. Reverse of getPlayerId(playerName)

Note - extra features are NOT guaranteed to be maintained in future releases, particularly if updates are made to the original Pi API which replace the functionality

## Config

Modify config.yml:

 - port: 4711 - the default tcp port can be changed in config.yml
 - location: RELATIVE - determine whether locations are RELATIVE to the spawn point (default like pi) or ABSOLUTE
 - hitclick: RIGHT - determine whether hit events are triggered by LEFT clicks, RIGHT clicks or BOTH 

## Build

To build RaspberryJuice, [download and install Maven](https://maven.apache.org/install.html), clone the repository, run `mvn package':

```
https://github.com/MinecraftDawn/RaspberryJuice.git
cd RaspberryJuice
mvn package
```

## Version history

 - 1.14 修改指令方式，從數字方塊ID改為方塊名稱。
 - 1.11 - spawnEntity, setDirection, setRotation, setPitch
 - 1.10.1 - bug fixes
 - 1.10 - left, right, both hit clicks added to config.yml & fixed minor hit events bug
 - 1.9.1 - minor change to improve connection reset
 - 1.9 - relative and absolute positions added to config.yml
 - 1.8 - minecraft version 1.9.2 compatibility
 - 1.7 - added pollChatPosts() & block update performance improvements
 - 1.6 - added getPlayerId(playerName), getDirection, getRotation, getPitch
 - 1.5 - entity functions
 - 1.4.2 - bug fixes
 - 1.4 - bug fixes, port specified in config.yml
 - 1.3 - getHeight, multiplayer, getBlocks
 - 1.2 - added world.getBlockWithData
 - 1.1.1 - block hit events
 - 1.1 - Initial release

## Contributors

 - [zhuowei](https://github.com/zhuowei)
 - [martinohanlon](https://github.com/martinohanlon)
 - [jclaggett](https://github.com/jclaggett)
 - [opticyclic](https://github.com/opticyclic)
 - [timcu](https://www.triptera.com.au/wordpress/)
 - [pxai](https://github.com/pxai)
