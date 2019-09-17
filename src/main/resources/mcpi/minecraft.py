from .connection import Connection
from .vec3 import Vec3
from .event import BlockEvent, ChatEvent
#from .entity import Entity
#from .block import Block
from .util import flatten
from warnings import warn

""" Minecraft PI low level api v0.1_1

    Note: many methods have the parameter *arg. This solution makes it
    simple to allow different types, and variable number of arguments.
    The actual magic is a mix of flatten_parameters() and __iter__. Example:
    A Cube class could implement __iter__ to work in Minecraft.setBlocks(c, id).

    (Because of this, it's possible to "erase" arguments. CmdPlayer removes
     entityId, by injecting [] that flattens to nothing)

    @author: Aron Nieminen, Mojang AB"""


def intFloor(*args):
    return [int(x) for x in flatten(args)]

class CmdPositioner:
    """Methods for setting and getting positions"""
    def __init__(self, connection, packagePrefix):
        self.conn = connection
        self.pkg = packagePrefix

    def getPos(self, ID) -> Vec3:
        """Get entity position (entityId:int) => Vec3"""
        s = self.conn.sendReceive(self.pkg + b".getPos", ID)
        return Vec3(*list(map(float, s.split(","))))

    def setPos(self, ID, x:float, y:float, z:float) -> None:
        """Set entity position (entityId:int, x,y,z)"""
        self.conn.send(self.pkg + b".setPos", ID, x, y, z)

    def getTilePos(self, ID) -> Vec3:
        """Get entity tile position (entityId:int) => Vec3"""
        s = self.conn.sendReceive(self.pkg + b".getTile", ID)
        return Vec3(*list(map(int, s.split(","))))

    def setTilePos(self, ID, x:int, y:int, z:int) -> None:
        """Set entity tile position (entityId:int) => Vec3"""
        self.conn.send(self.pkg + b".setTile", ID, x, y, z)
        
    def getDirection(self, ID) -> Vec3:
        """Get direction of the entity"""
        s = self.conn.sendReceive(self.pkg + b".getDirection", id)
        return Vec3(*list(s.split(",")))
        
    def setDirection(self, ID, x:float, y:float, z:float) -> None:
        """Set direction of the entity"""
        self.conn.send(self.pkg + b".setDirection", ID, x, y, z)
        
    def getRotation(self, ID) -> float:
        """Get rotation if the entity"""
        s = self.conn.sendReceive(self.pkg + b".getRotation", ID)
        return float(s)
    
    def setRotation(self, ID, yaw) -> float:
        """Set rotation if the entity"""
        self.conn.send(self.pkg + b".setRotation", ID, yaw)
        
    def getPitch(self, ID) -> float:
        """Get pitch if the entity"""
        s = self.conn.sendReceive(self.pkg + b".getPitch", ID)
        return float(s)
    
    def setPitch(self, ID, pitch) -> None:
        """Set pitch if the entity"""
        self.conn.send(self.pkg + b".setPitch", ID, pitch)
        
    def setting(self, setting, status):
        """Set a player setting (setting, status). keys: autojump"""
        self.conn.send(self.pkg + b".setting", setting, 1 if bool(status) else 0)


class CmdEntity(CmdPositioner):
    """Methods for entities"""
    def __init__(self, connection):
        CmdPositioner.__init__(self, connection, b"entity")
        
    def getName(self, ID):
        """Get the list name of the player with entity id => [name:str]
        
        Also can be used to find name of entity if entity is not a player."""
        return self.conn.sendReceive(b"entity.getName", ID)


class CmdPlayer(CmdPositioner):
    """Methods for the host (Raspberry Pi) player"""
    def __init__(self, connection):
        CmdPositioner.__init__(self, connection, b"player")
        self.conn = connection

    def getPos(self) -> Vec3:
        return CmdPositioner.getPos(self, [])
    def setPos(self, x:float, y:float, z:float) -> None:
        return CmdPositioner.setPos(self, [], x, y, z)
    def getTilePos(self) -> Vec3:
        return CmdPositioner.getTilePos(self, [])
    def setTilePos(self, x:int, y:int, z:int) -> None:
        return CmdPositioner.setTilePos(self, [], x, y, z)
    def getDirection(self) -> Vec3:
        return CmdPositioner.getDirection(self, [])
    def setDirection(self, x:float, y:float, z:float) -> None:
        return CmdPositioner.setDirection(self, [], x, y, z)
    def getRotation(self) -> float:
        return CmdPositioner.getRotation(self, [])
    def setRotation(self, yaw) -> None:
        return CmdPositioner.setRotation(self, [], yaw)
    def getPitch(self) -> float:
        return CmdPositioner.getPitch(self, [])
    def setPitch(self, pitch) -> None:
        return CmdPositioner.setPitch(self, [], pitch)
    
    def getFoodLevel(self) -> int:
        return self.conn.sendReceive(self.pkg + b".getFoodLevel", [])
    
    def setFoodLevel(self, foodLevel:int) -> None:
        self.conn.send(self.pkg + b".setFoodLevel", foodLevel)
        
    def getHealth(self) -> float:
        return self.conn.sendReceive(self.pkg + b".getHealth", [])
    
    def setHealth(self, health:float) -> None:
        self.conn.send(self.pkg + b".setHealth", [], health)
    
    def sendTitle(self, title:str, subTitle:str="", fadeIn:int=10, stay:int=70, fadeOut:int=20) -> None:
        self.conn.send(self.pkg + b".sendTitle", id, title, subTitle, fadeIn, stay, fadeOut)
        
class CmdCamera:
    def __init__(self, connection):
        self.conn = connection

    def setNormal(self, *args) -> None:
        """Set camera mode to normal Minecraft view ([entityId])"""
        self.conn.send(b"camera.mode.setNormal", args)

    def setFixed(self) -> None:
        """Set camera mode to fixed view"""
        self.conn.send(b"camera.mode.setFixed")

    def setFollow(self, *args) -> None:
        """Set camera mode to follow an entity ([entityId])"""
        self.conn.send(b"camera.mode.setFollow", args)

    def setPos(self, x:float, y:float, z:float) -> None:
        """Set camera entity position (x,y,z)"""
        self.conn.send(b"camera.setPos", x, y, z)


class CmdEvents:
    """Events"""
    def __init__(self, connection):
        self.conn = connection

    def clearAll(self):
        """Clear all old events"""
        self.conn.send(b"events.clear")

    def pollBlockHits(self):
        """Only triggered by sword => [BlockEvent]"""
        s = self.conn.sendReceive(b"events.block.hits")
        events = [e for e in s.split("|") if e]
        return [BlockEvent.Hit(*list(map(int, e.split(",")))) for e in events]
    
    def pollChatPosts(self):
        """Triggered by posts to chat => [ChatEvent]"""
        s = self.conn.sendReceive(b"events.chat.posts")
        events = [e for e in s.split("|") if e]
        return [ChatEvent.Post(int(e[:e.find(",")]), e[e.find(",") + 1:]) for e in events]


class Minecraft:
    """The main class to interact with a running instance of Minecraft Pi."""
    def __init__(self, connection):
        self.conn = connection

        self.camera = CmdCamera(connection)
        self.entity = CmdEntity(connection)
        self.player = CmdPlayer(connection)
        self.events = CmdEvents(connection)

    def getBlock(self, x:int, y:int, z:int) -> str:
        """Get block (x,y,z) => id:int"""
        return self.conn.sendReceive(b"world.getBlock", x, y, z)

    def getBlocks(self, x1:int, y1:int, z1:int, x2:int, y2:int, z2:int) -> list:
        """Get a cuboid of blocks (x0,y0,z0,x1,y1,z1) => [id:int]"""
        blocks = self.conn.sendReceive(b"world.getBlocks", x1, y1, z1, x2, y2, z2)
        arr1d = blocks.split(',')
        
        xSize = abs(x1 - x2) + 1
        ySize = abs(y1 - y2) + 1
        zSize = abs(z1 - z2) + 1
        totalSize = xSize * ySize * zSize
        arr3d = []
        
        if len(arr1d) != totalSize:
            warn('Get number of blocks is incomplete')
        
        for i in range(0,totalSize,xSize*ySize):
            curArr = []
            for j in range(0,xSize*ySize,xSize):
                curArr.append(arr1d[i+j:i+j+xSize])
            arr3d.append(curArr)
        return arr3d

    def setBlock(self, x:int, y:int, z:int, block:str) -> None:
        """Set block (x,y,z,id,[data])"""
        self.conn.send(b"world.setBlock", x, y, z, block)

    def setBlocks(self, x1:int, y1:int, z1:int, x2:int, y2:int, z2:int, block) -> None: 
        """Set a cuboid of blocks (x1,y1,z1,x2,y2,z2,id,[data])"""
        self.conn.send(b"world.setBlocks", x1, y1, z1, x2, y2, z2, block)

    def getHeight(self, x:int, z:int) -> int:
        """Get the height of the world (x,z) => int"""
        return self.conn.sendReceive(b"world.getHeight", x, z)

    def getPlayerEntityIds(self) -> list:
        """Get the entity ids of the connected players => [id:int]"""
        ids = self.conn.sendReceive(b"world.getPlayerIds")
        return list(map(int, ids.split("|")))

#    def saveCheckpoint(self):
#        """Save a checkpoint that can be used for restoring the world"""
#        self.conn.send(b"world.checkpoint.save")

#    def restoreCheckpoint(self):
#        """Restore the world state to the checkpoint"""
#        self.conn.send(b"world.checkpoint.restore")

    def postToChat(self, *msg) -> None:
        """Post a message to the game chat"""
        self.conn.send(b"chat.post", msg)
        
    # TODO：修改成一個py檔處理Sign
    def setSign(self, x:int, y:int, z:int, signType:str, signDir:int, line1:str="", line2:str="", line3:str="", line4:str="") -> None:
        minecraftSignsType = ["SPRUCE_SIGN","ACACIA_SIGN","BIRCH_SIGN","DARK_OAK_SIGN","JUNGLE_SIGN","OAK_SIGN"]
        
        # ["SPRUCE_WALL_SIGN","ACACIA_WALL_SIGN","BIRCH_WALL_SIGN","DARK_OAK_WALL_SIGN","JUNGLE_WALL_SIGN","OAK_WALL_SIGN"]
        minecraftSignsDir = {0:'SOUTH',
                             1:'SOUTH_SOUTH_WEST',
                             2:'SOUTH_WEST',
                             3:'WEST_SOUTH_WEST',
                             4:'WEST',
                             5:'WEST_NORTH_WEST',
                             6:'NORTH_WEST',
                             7:'NORTH_NORTH_WEST',
                             8:'NORTH',
                             9:'NORTH_NORTH_EAST',
                             10:'NORTH_EAST',
                             11:'EAST_NORTH_EAST',
                             12:'EAST',
                             13:'EAST_SOUTH_EAST',
                             14:'SOUTH_EAST',
                             15:'SOUTH_SOUTH_EAST'
                             }
        
        if type(signDir) == int:
            if 0 <= signDir < 16:
                signDir = minecraftSignsDir.get(signDir)
        elif type(signDir) == str:
            for k,v in minecraftSignsDir.items():
                if signDir == v:
                    break
            else:
                signDir = minecraftSignsDir.get(0)
            
        signType = signType.upper()
        if signType not in minecraftSignsType: raise Exception("Sign name error")
        self.conn.send(b"world.setSign", x, y, z , signType, signDir, line1 ,line2 ,line3 ,line4)
        
    def setWallSign(self, x:int, y:int, z:int, signType:str, signDir:int, line1="",line2="",line3="",line4="") -> None:
        minecraftSignsType = ["SPRUCE_WALL_SIGN","ACACIA_WALL_SIGN","BIRCH_WALL_SIGN","DARK_OAK_WALL_SIGN","JUNGLE_WALL_SIGN","OAK_WALL_SIGN"]
        
        minecraftSignsDir = {0:'SOUTH',
                             1:'WEST',
                             2:'NORTH',
                             3:'EAST'}
        
        if type(signDir) == int:
            if 0 <= signDir < 4:
                signDir = minecraftSignsDir.get(signDir)
        elif type(signDir) == str:
            for k,v in minecraftSignsDir.items():
                if signDir == v:
                    break
            else:
                signDir = minecraftSignsDir.get(0)
            
        signType = signType.upper()
        if signType not in minecraftSignsType: raise Exception("Sign name error")
        self.conn.send(b"world.setWallSign", x, y, z , signType, signDir, line1 ,line2 ,line3 ,line4)
        
    def spawnEntity(self, x:int, y:int, z:int, entityID:int) -> int:
        """Spawn entity (x,y,z,id,[data])"""
        return int(self.conn.sendReceive(b"world.spawnEntity", x, y, z, entityID))
    
    def createExplosion(self, x:int, y:int, z:int, power:int=4) -> None:
        self.conn.send(b"world.createExplosion", x, y, z, power)

    def getPlayerEntityId(self, name:str) -> int:
        """Get the entity id of the named player => [id:int]"""
        return int(self.conn.sendReceive(b"world.getPlayerId", name))

    def setting(self, setting, status):
        """Set a world setting (setting, status). keys: world_immutable, nametags_visible"""
        self.conn.send(b"world.setting", setting, 1 if bool(status) else 0)

    @staticmethod
    def create(address = "localhost", port = 4711):
        return Minecraft(Connection(address, port))


if __name__ == "__main__":
    mc = Minecraft.create()
    mc.postToChat("Hello, Minecraft!")
