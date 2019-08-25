# RaspberryJuice

A Minecraft Bukkit plugin which connect to python program

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
    - Get highest position y of the block
    
    
 - world.getPlayerEntityIds() -> list:
    - Get the list of server players'id
    
    
 - world.postToChat(*msg) -> None:
    -Print message to minecraft chat
 
 
 - world.setSign(x:int, y:int, z:int, signType:str, signDir:int, line1:str="", line2:str="", line3:str="", line4:str="") -> None:
    - Set the stand sign of the input position
 
 
 - world.setWallSign(x:int, y:int, z:int, signType:str, signDir:int, line1="",line2="",line3="",line4="") -> None:
    - Set the wall sign of the input position
    
    
 - world.spawnEntity(x:int, y:int, z:int, entityID:int) -> int:
    - Spawn a entity of the input position
 
 
 - world.createExplosion(x:int, y:int, z:int, power:int=4) -> None:
    - Create a explosion of the input position
 
 
 - world.getPlayerEntityId(name:str) -> int:
    - Get the entity ID of input name
 
 
 - world.create(address = "localhost", port = 4711):
    - Connect your python program to Raspberryjuice
    

 ---
 
#### player
 - player.getPos() -> Vec3:
    - Get player's float position
    
    
 - player.setPos(x:float, y:float, z:float) -> None:
    - Set player's position with float
 
 
 - player.getTilePos() -> Vec3:
    - Get player's integer position
 
 - player.setTilePos(x:int, y:int, z:int) -> None:
    -Set player's integer position
 
 
 - player.getDirection() -> Vec3:
    - Get player's direction
 
 
 - player.setDirection(x:float, y:float, z:float) -> None:
    - Set player's direction
    
 
 - player.getRotation() -> float:
    - Get player's rotation
    
 
 - player.setRotation(yaw) -> None:
    - Set player's rotation
 
 
 - player.getPitch() -> float:
    - Get player's pitch
 
 
 - player.setPitch(pitch) -> None:
    - Set player's pitch
 
 
 - player.sendTitle(title:str, subTitle:str="", fadeIn:int=10, stay:int=70, fadeOut:int=20) -> None:
    - Send a title to player
 
 ---
 
#### entity
 - entity.getPos(ID) -> Vec3:
    - Get specific entity's float position
    
    
 - entity.setPos(ID, x:float, y:float, z:float) -> None:
    - Set specific entity's position with float
 
 
 - entity.getTilePos(ID) -> Vec3:
    - Get specific entity's integer position
 
 
 - entity.setTilePos(ID, x:int, y:int, z:int) -> None:
    - Set specific entity's position with integer
 
 
 - entity.getDirection(ID) -> Vec3:
    - Get specific entity's direction
 
 
 - entity.setDirection(ID, x:float, y:float, z:float) -> None:
    - Set specific entity's direction
 
 
 - entity.getRotation(ID) -> float:
    - Get specific entity's rotation
 
 
 - entity.setRotation(ID, yaw) -> float:
    - Set specific entity's rotation
    
 
 - entity.getPitch(ID) -> float:
    - Get specific entity's pitch
 
 
 - entity.setPitch(ID, pitch) -> None:
    - Set specific entity's pitch
 
 
 - entity.getName(ID):
    -  Get specific entity's name

### Commands that can't be supported

 - Camera angles
 
## Config

Modify config.yml:

 - port: 4711 - the default tcp port can be changed in config.yml
 - location: ABSOLUTE - determine whether locations are RELATIVE to the spawn point or ABSOLUTE
 - hitclick: RIGHT - determine whether hit events are triggered by LEFT clicks, RIGHT clicks or BOTH 

## Build

To build RaspberryJuice, [download and install Maven](https://maven.apache.org/install.html), clone the repository, run `mvn package':

```
git clone https://github.com/zhuowei/RaspberryJuice
cd RaspberryJuice
mvn package
```

## Version history

 - 1.14 - Modify the command, change the block number identity to block name.
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
