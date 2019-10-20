#Martin O'Hanlon
#www.stuffaboutcode.com
#RaspberryJuice Tests

import original.mcpi.minecraft as minecraft
import modded.mcpi.minecraft as minecraftmodded
import original.mcpi.block as block
import modded.mcpi.block as blockmodded
import modded.mcpi.entity as entitymodded
import time
import math

def runBlockTests(mc):
    """runBlockTests - tests creation of all blocks for all data values known to RaspberryJuice
    
    A sign is placed next to the created block so user can view in Minecraft whether block created correctly or not
    Known issues:
    - id for NETHER_REACTOR_CORE and GLOWING_OBSIDIAN wrong
    - some LEAVES missing but because they decay by the time user sees them
    - this test doesn't try activation of TNT
    
    Author: Tim Cummings https://www.triptera.com.au/wordpress/
    """

    solids=["STONE","GRASS","DIRT","COBBLESTONE","BEDROCK","SAND","GRAVEL","GOLD_ORE","IRON_ORE","COAL_ORE","GLASS","LAPIS_LAZULI_ORE",
            "LAPIS_LAZULI_BLOCK","COBWEB","GOLD_BLOCK","IRON_BLOCK","BRICK_BLOCK","TNT","BOOKSHELF","MOSS_STONE","OBSIDIAN",
            "DIAMOND_ORE","DIAMOND_BLOCK","CRAFTING_TABLE","FARMLAND","REDSTONE_ORE","CLAY","PUMPKIN","MELON","NETHERRACK","SOUL_SAND",
            "GLOWSTONE_BLOCK","GLASS_PANE","LIT_PUMPKIN","END_STONE","EMERALD_ORE","GLOWING_OBSIDIAN","ICE",
            "SNOW_BLOCK","MYCELIUM","NETHER_BRICK","NETHER_REACTOR_CORE"]
    fences=["FENCE","FENCE_NETHER_BRICK","FENCE_SPRUCE","FENCE_BIRCH","FENCE_JUNGLE","FENCE_DARK_OAK","FENCE_ACACIA"]
    woods=["WOOD_PLANKS"]
    trees=["WOOD","LEAVES"]
    trees2=["LEAVES2"] #options are acacia and dark oak
    plants=["DEAD_BUSH","FLOWER_CYAN","FLOWER_YELLOW","SUGAR_CANE"]
    liquids=["WATER","LAVA"]
    beds=["BED"]
    coloureds=["WOOL","STAINED_GLASS"]
    flats=["RAIL","RAIL_POWERED","RAIL_DETECTOR","RAIL_ACTIVATOR","TRAPDOOR","TRAPDOOR_IRON"]
    slabs=["STONE_SLAB","STONE_SLAB_DOUBLE","WOODEN_SLAB"]
    torches=["TORCH","TORCH_REDSTONE"]
    gases=["AIR","FIRE"]
    stairs=["STAIRS_WOOD","STAIRS_COBBLESTONE","STAIRS_BRICK","STAIRS_STONE_BRICK","STAIRS_NETHER_BRICK","STAIRS_SANDSTONE"]
    signs=["SIGN_STANDING","SIGN_WALL"]
    doors=["DOOR_WOOD","DOOR_IRON","DOOR_SPRUCE","DOOR_BIRCH","DOOR_JUNGLE","DOOR_ACACIA","DOOR_DARK_OAK"]
    gates=["FENCE_GATE"]
    wallmounts=["SIGN_WALL","LADDER","CHEST","FURNACE_INACTIVE","FURNACE_ACTIVE"]
    saplings=["SAPLING"]
    tallgrasses=["GRASS_TALL"]
    stonebricks=["STONE_BRICK"]
    snowblocks=["SNOW"]
    sandstones=["SANDSTONE"]
    cacti=["CACTUS"]
    mushrooms=["MUSHROOM_BROWN","MUSHROOM_RED"]
    
    # location for platform showing all block types
    xtest = 0
    ytest = 50
    ztest = 0
    mc.postToChat("runBlockTests(): Creating test blocks at x=" + str(xtest) + " y=" + str(ytest) + " z=" + str(ztest))
    # create set of all block ids to ensure they all get tested
    # note some blocks have different names but same ids so they only have to be tested once per id
    # create a map of ids to names so can see which ones haven't been tested by name
    untested=set()
    blockmap={}
    for varname in dir(blockmodded):
        var=getattr(blockmodded,varname)
        try:
            # check var has data and id and add id to untested set
            var.data
            untested.add(var.id)
            try:
                names=blockmap[var.id]
                names.append(varname)
            except KeyError:
                blockmap[var.id]=[varname]
        except AttributeError:
            #only interested in objects with an id and data which behave like Blocks
            pass
    
    signmount=blockmodded.STONE
    sign=blockmodded.SIGN_STANDING.withData(12)
    signid=sign.id
    
    x=xtest
    y=ytest-1
    z=ztest
    mc.setBlocks(x,y,z,x+100,y,z+100,blockmodded.STONE)
    time.sleep(1)
    #clear the area in segments, otherwise it breaks the server
    #clearing area
    #mc.setBlocks(x,y+1,z,x+100,y+50,z+100,blockmodded.AIR)
    for y_inc in range(1, 10):
        mc.setBlocks(x,y+y_inc,z,x+100,y+y_inc,z+100,blockmodded.AIR)
        time.sleep(2)
    mc.player.setTilePos(xtest, ytest, ztest)
    time.sleep(1)
    x=xtest+10
    y=ytest
    z=ztest+10
    for key in solids + gases + flats + fences:
        b = getattr(blockmodded,key)
        z += 1
        mc.setBlock(x-1,y,z,signmount)
        mc.setSign(x-1,y+1,z,sign,key,"id=" + str(b.id),"data=" + str(b.data))
        mc.setBlock(x,y,z,b)
        untested.discard(b.id)
    
    time.sleep(1)
    x=xtest+20
    z=ztest+10
    for key in trees:
        for data in range(16):
            b = getattr(blockmodded,key).withData(data)
            z += 1
            mc.setBlock(x-1,y,z,signmount)
            mc.setSign(x-1,y+1,z,sign,key,"id=" + str(b.id),"data=" + str(b.data))
            mc.setBlock(x,y,z,b)
        untested.discard(b.id)        
    for key in trees2:
        for data in [0,1,4,5,8,9,12,13]:
            b = getattr(blockmodded,key).withData(data)
            z += 1
            mc.setBlock(x-1,y,z,signmount)
            mc.setSign(x-1,y+1,z,sign,key,"id=" + str(b.id),"data=" + str(b.data))
            mc.setBlock(x,y,z,b)
        untested.discard(b.id)    
    for key in woods + stonebricks:
        for data in range(4):
            b = getattr(blockmodded,key).withData(data)
            z += 1
            mc.setBlock(x-1,y,z,signmount)
            mc.setSign(x-1,y+1,z,sign,key,"id=" + str(b.id),"data=" + str(b.data))
            mc.setBlock(x,y,z,b)
        untested.discard(b.id)       
    for key in sandstones:
        for data in range(3):
            b = getattr(blockmodded,key).withData(data)
            z += 1
            mc.setBlock(x-1,y,z,signmount)
            mc.setSign(x-1,y+1,z,sign,key,"id=" + str(b.id),"data=" + str(b.data))
            mc.setBlock(x,y,z,b)
        untested.discard(b.id)
    for key in saplings + tallgrasses:
        for data in range(4):
            b = getattr(blockmodded,key).withData(data)
            z += 1
            mc.setBlock(x-1,y,z,signmount)
            mc.setSign(x-1,y+1,z,sign,key,"id=" + str(b.id),"data=" + str(b.data))
            mc.setBlock(x,y-1,z,blockmodded.DIRT)
            mc.setBlock(x,y,z,b)
        untested.discard(b.id)
    for key in plants:
        b = getattr(blockmodded,key)
        z += 1
        mc.setBlock(x-1,y,z,signmount)
        mc.setSign(x-1,y+1,z,sign,key,"id=" + str(b.id),"data=" + str(b.data))
        mc.setBlock(x,y-1,z,blockmodded.DIRT)
        mc.setBlock(x+1,y-2,z,blockmodded.DIRT)
        mc.setBlock(x+1,y-1,z,blockmodded.WATER)
        mc.setBlock(x,y,z,b)
        untested.discard(b.id)
    for key in cacti:
        b = getattr(blockmodded,key)
        z += 1
        mc.setBlock(x-1,y,z,signmount)
        mc.setSign(x-1,y+1,z,sign,key,"id=" + str(b.id),"data=" + str(b.data))
        # cactus has to be on sand and away from other blocks
        mc.setBlock(x+1,y-2,z,blockmodded.DIRT)
        mc.setBlock(x+1,y-1,z,blockmodded.SAND)
        mc.setBlock(x+1,y,z,b)
        untested.discard(b.id)
    for key in mushrooms:
        b = getattr(blockmodded,key)
        z += 1
        mc.setBlock(x-1,y,z,signmount)
        mc.setSign(x-1,y+1,z,sign,key,"id=" + str(b.id),"data=" + str(b.data))
        mc.setBlocks(x-3,y+3,z-3,x+3,y+3,z+3,blockmodded.STONE)
        mc.setBlock(x,y,z,b)
        untested.discard(b.id)
    
    time.sleep(1)
    x=xtest+30
    z=ztest+10
    for key in coloureds:
        for data in range(16):
            b = getattr(blockmodded,key).withData(data)
            z += 1
            mc.setBlock(x-1,y,z,signmount)
            mc.setSign(x-1,y+1,z,sign,key,"id=" + str(b.id),"data=" + str(b.data))
            mc.setBlock(x,y,z,b)
        untested.discard(b.id)
    for key in beds:
        #RaspberryJuice can't do coloured beds
        b = getattr(blockmodded,key)
        z += 10
        mc.setBlock(x-1,y,z,signmount)
        mc.setBlock(x+1,y,z,signmount)
        mc.setBlock(x,y,z-1,signmount)
        mc.setBlock(x,y,z+1,signmount)
        mc.setSign(x-1,y+1,z,signid,4,key,"id=" + str(b.id),"head=11","foot=3")
        mc.setSign(x+1,y+1,z,signid,12,key,"id=" + str(b.id),"head=9","foot=1")
        mc.setSign(x,y+1,z-1,signid,8,key,"id=" + str(b.id),"head=8","foot=0")
        mc.setSign(x,y+1,z+1,signid,0,key,"id=" + str(b.id),"head=10","foot=2")
        mc.setBlock(x+3,y,z,b.id,1)
        mc.setBlock(x+2,y,z,b.id,9)
        mc.setBlock(x-3,y,z,b.id,3)
        mc.setBlock(x-2,y,z,b.id,11)
        mc.setBlock(x,y,z-3,b.id,0)
        mc.setBlock(x,y,z-2,b.id,8)
        mc.setBlock(x,y,z+3,b.id,2)
        mc.setBlock(x,y,z+2,b.id,10)
        untested.discard(b.id)
        b=blockmodded.SIGN_STANDING
        mc.setSign (x-2,y,z-5,b.id,15,"SIGN_STANDING","id=" + str(b.id),"data=15","rotation")
        mc.setSign (x-4,y,z-4,b.id,14,"SIGN_STANDING","id=" + str(b.id),"data=14","rotation")
        mc.setSign (x-5,y,z-2,b.id,13,"SIGN_STANDING","id=" + str(b.id),"data=13","rotation")
        mc.setSign (x-6,y,z  ,b.id,12,"SIGN_STANDING","id=" + str(b.id),"data=12","rotation")
        mc.setSign (x-5,y,z+2,b.id,11,"SIGN_STANDING","id=" + str(b.id),"data=11","rotation")
        mc.setSign (x-4,y,z+4,b.id,10,"SIGN_STANDING","id=" + str(b.id),"data=10","rotation")
        mc.setSign (x-2,y,z+5,b.id, 9,"SIGN_STANDING","id=" + str(b.id),"data= 9","rotation")
        mc.setSign (x  ,y,z+6,b.id, 8,"SIGN_STANDING","id=" + str(b.id),"data= 8","rotation")
        mc.setSign (x+2,y,z+5,b.id, 7,"SIGN_STANDING","id=" + str(b.id),"data= 7","rotation")
        mc.setSign (x+4,y,z+4,b.id, 6,"SIGN_STANDING","id=" + str(b.id),"data= 6","rotation")
        mc.setSign (x+5,y,z+2,b.id, 5,"SIGN_STANDING","id=" + str(b.id),"data= 5","rotation")
        mc.setSign (x+6,y,z  ,b.id, 4,"SIGN_STANDING","id=" + str(b.id),"data= 4","rotation")
        mc.setSign (x+5,y,z-2,b.id, 3,"SIGN_STANDING","id=" + str(b.id),"data= 3","rotation")
        mc.setSign (x+4,y,z-4,b.id, 2,"SIGN_STANDING","id=" + str(b.id),"data= 2","rotation")
        mc.setSign (x+2,y,z-5,b.id, 1,"SIGN_STANDING","id=" + str(b.id),"data= 1","rotation")
        mc.setSign (x  ,y,z-6,b.id, 0,"SIGN_STANDING","id=" + str(b.id),"data= 0","rotation")
        untested.discard(b.id)
    
    time.sleep(1)
    x=xtest+40
    z=ztest+10
    for key in liquids:
        z += 1
        mc.setBlocks(x-1,y,z,x+1,y,z+10,blockmodded.STONE)
        b = getattr(blockmodded,key + "_STATIONARY")
        z += 1
        mc.setSign(x-1,y+1,z,sign,key+"_STATIONARY","id=" + str(b.id),"data=" + str(b.data))
        mc.setBlock(x,y,z,b)
        untested.discard(b.id)
        for data in range(8):
            b = getattr(blockmodded,key).withData(data)
            z += 1
            mc.setSign(x-1,y+1,z,sign,key,"id=" + str(b.id),"data=" + str(b.data))
            mc.setBlock(x,y,z,b)
        untested.discard(b.id)
    for key in snowblocks:
        z += 1
        mc.setBlocks(x-1,y,z,x-1,y,z+8,blockmodded.STONE)
        for data in range(8):
            b = getattr(blockmodded,key).withData(data)
            z += 1
            mc.setSign(x-1,y+1,z,sign,key,"id=" + str(b.id),"data=" + str(b.data))
            mc.setBlock(x,y,z,b)
        untested.discard(b.id)
    
    time.sleep(1)
    x=xtest+50
    z=ztest+10
    for key in slabs:
        for data in range(8):
            b = getattr(blockmodded,key).withData(data)
            z += 1
            mc.setBlock(x-1,y,z,signmount)
            mc.setSign(x-1,y+1,z,sign,key,"id=" + str(b.id),"data=" + str(b.data))
            mc.setBlock(x,y,z,b)
        untested.discard(b.id)
    
    time.sleep(1)
    x=xtest+60
    y=ytest
    z=ztest+10
    wallsignid=blockmodded.SIGN_WALL.id
    for key in wallmounts:
        b = getattr(blockmodded,key)
        mc.setBlock(x,y,z,signmount)
        mc.setBlock(x,y+1,z,signmount)
        #north
        mc.setBlock(x,y,z-1,b.id,2)
        mc.setSign(x,y+1,z-1,wallsignid,2,key,"id=" + str(b.id),"data=2","north")
        #south
        mc.setBlock(x,y,z+1,b.id,3)
        mc.setSign(x,y+1,z+1,wallsignid,3,key,"id=" + str(b.id),"data=3","south")
        #west
        mc.setBlock(x-1,y,z,b.id,4)
        mc.setSign(x-1,y+1,z,wallsignid,4,key,"id=" + str(b.id),"data=4","west")
        #east
        mc.setBlock(x+1,y,z,b.id,5)
        mc.setSign(x+1,y+1,z,wallsignid,5,key,"id=" + str(b.id),"data=5","east")
        y+=2
        untested.discard(b.id)
        #untested.discard(wallsignid)
        
    time.sleep(1)
    x=xtest+60
    y=ytest
    z=ztest+20
    for key in torches:
        b = getattr(blockmodded,key)
        mc.setBlocks(x,y,z,x,y+2,z,signmount)
        #north
        mc.setBlock(x,y,z-1,b.id,4)
        mc.setSign(x,y+1,z-1,wallsignid,2,key,"id=" + str(b.id),"data=4","north")
        #south
        mc.setBlock(x,y,z+1,b.id,3)
        mc.setSign(x,y+1,z+1,wallsignid,3,key,"id=" + str(b.id),"data=3","south")
        #west
        mc.setBlock(x-1,y,z,b.id,2)
        mc.setSign(x-1,y+1,z,wallsignid,4,key,"id=" + str(b.id),"data=2","west")
        #east
        mc.setBlock(x+1,y,z,b.id,1)
        mc.setSign(x+1,y+1,z,wallsignid,5,key,"id=" + str(b.id),"data=1","east")
        #up
        mc.setBlock(x,y+3,z,b.id,5)
        mc.setSign(x+1,y+2,z,wallsignid,5,key,"id=" + str(b.id),"data=5","up")
        z+=10
        untested.discard(b.id)
    
    time.sleep(1)
    x=xtest+70
    y=ytest
    z=ztest+10
    for key in doors:
        b = getattr(blockmodded,key)
        mc.setBlocks(x,y,z,x+3,y+2,z+3,signmount)
        mc.setBlocks(x+1,y,z+1,x+2,y+1,z+2,blockmodded.AIR)
        mc.setBlock(x+1,y  ,z  ,b.id,1)
        mc.setBlock(x+1,y+1,z  ,b.id,9)
        mc.setBlock(x+2,y  ,z  ,b.id,1)
        mc.setBlock(x+2,y+1,z  ,b.id,8)
        mc.setBlock(x  ,y  ,z+1,b.id,0)
        mc.setBlock(x  ,y+1,z+1,b.id,8)
        mc.setBlock(x  ,y  ,z+2,b.id,0)
        mc.setBlock(x  ,y+1,z+2,b.id,9)
        mc.setBlock(x+3,y  ,z+1,b.id,2)
        mc.setBlock(x+3,y+1,z+1,b.id,9)
        mc.setBlock(x+3,y  ,z+2,b.id,2)
        mc.setBlock(x+3,y+1,z+2,b.id,8)
        mc.setBlock(x+1,y  ,z+3,b.id,3)
        mc.setBlock(x+1,y+1,z+3,b.id,8)
        mc.setBlock(x+2,y  ,z+3,b.id,3)
        mc.setBlock(x+2,y+1,z+3,b.id,9)
        mc.setSign (x  ,y  ,z-1,wallsignid,2,key,"id=" + str(b.id),"data=1")
        mc.setSign (x  ,y+1,z-1,wallsignid,2,key,"id=" + str(b.id),"data=9")
        mc.setSign (x+3,y  ,z-1,wallsignid,2,key,"id=" + str(b.id),"data=1")
        mc.setSign (x+3,y+1,z-1,wallsignid,2,key,"id=" + str(b.id),"data=8")
        mc.setSign (x-1,y  ,z  ,wallsignid,4,key,"id=" + str(b.id),"data=0")
        mc.setSign (x-1,y+1,z  ,wallsignid,4,key,"id=" + str(b.id),"data=8")
        mc.setSign (x-1,y  ,z+3,wallsignid,4,key,"id=" + str(b.id),"data=0")
        mc.setSign (x-1,y+1,z+3,wallsignid,4,key,"id=" + str(b.id),"data=9")
        mc.setSign (x+4,y  ,z  ,wallsignid,5,key,"id=" + str(b.id),"data=2")
        mc.setSign (x+4,y+1,z  ,wallsignid,5,key,"id=" + str(b.id),"data=9")
        mc.setSign (x+4,y  ,z+3,wallsignid,5,key,"id=" + str(b.id),"data=2")
        mc.setSign (x+4,y+1,z+3,wallsignid,5,key,"id=" + str(b.id),"data=8")
        mc.setSign (x  ,y  ,z+4,wallsignid,3,key,"id=" + str(b.id),"data=3")
        mc.setSign (x  ,y+1,z+4,wallsignid,3,key,"id=" + str(b.id),"data=8")
        mc.setSign (x+3,y  ,z+4,wallsignid,3,key,"id=" + str(b.id),"data=3")
        mc.setSign (x+3,y+1,z+4,wallsignid,3,key,"id=" + str(b.id),"data=9")
        y+=3
        untested.discard(b.id)
    for key in gates:
        b = getattr(blockmodded,key)
        mc.setBlocks(x,y,z,x+3,y,z+3,blockmodded.AIR)
        mc.setBlock(x+1,y  ,z  ,b.id,0)
        mc.setBlock(x+2,y  ,z  ,b.id,0)
        mc.setBlock(x  ,y  ,z+1,b.id,1)
        mc.setBlock(x  ,y  ,z+2,b.id,1)
        mc.setBlock(x+3,y  ,z+1,b.id,1)
        mc.setBlock(x+3,y  ,z+2,b.id,1)
        mc.setBlock(x+1,y  ,z+3,b.id,0)
        mc.setBlock(x+2,y  ,z+3,b.id,0)
        mc.setBlock(x,y,z,signmount)
        mc.setBlock(x+3,y,z,signmount)
        mc.setBlock(x,y,z+3,signmount)
        mc.setBlock(x+3,y,z+3,signmount)
        mc.setSign (x  ,y  ,z-1,wallsignid,2,key,"id=" + str(b.id),"data=0")
        mc.setSign (x+3,y  ,z-1,wallsignid,2,key,"id=" + str(b.id),"data=0")
        mc.setSign (x-1,y  ,z  ,wallsignid,4,key,"id=" + str(b.id),"data=1")
        mc.setSign (x-1,y  ,z+3,wallsignid,4,key,"id=" + str(b.id),"data=1")
        mc.setSign (x+4,y  ,z  ,wallsignid,5,key,"id=" + str(b.id),"data=1")
        mc.setSign (x+4,y  ,z+3,wallsignid,5,key,"id=" + str(b.id),"data=1")
        mc.setSign (x  ,y  ,z+4,wallsignid,3,key,"id=" + str(b.id),"data=0")
        mc.setSign (x+3,y  ,z+4,wallsignid,3,key,"id=" + str(b.id),"data=0")
        y+=3
        untested.discard(b.id)
        
    time.sleep(1)
    x=xtest+70
    y=ytest
    z=ztest+20
    for key in stairs:
        b = getattr(blockmodded,key)
        mc.setBlocks(x+1,y,z-1,x+3,y+11,z-3,signmount)
        mc.setBlock(x+1,y  ,z  ,b.id,0)
        mc.setBlock(x+2,y+1,z  ,b.id,0)
        mc.setBlock(x+3,y+2,z  ,b.id,0)
        mc.setBlock(x+4,y+2,z  ,signmount)
        mc.setBlock(x+4,y+3,z-1,b.id,3)
        mc.setBlock(x+4,y+4,z-2,b.id,3)
        mc.setBlock(x+4,y+5,z-3,b.id,3)
        mc.setBlock(x+4,y+5,z-4,signmount)
        mc.setBlock(x+3,y+6,z-4,b.id,1)
        mc.setBlock(x+2,y+7,z-4,b.id,1)
        mc.setBlock(x+1,y+8,z-4,b.id,1)
        mc.setBlock(x  ,y+8,z-4,signmount)
        mc.setBlock(x  ,y+9,z-3,b.id,2)
        mc.setBlock(x  ,y+10,z-2,b.id,2)
        mc.setBlock(x  ,y+11,z-1,b.id,2)
        mc.setBlock(x  ,y+11,z  ,signmount)
        mc.setBlock(x+1,y-1,z  ,b.id,5)
        mc.setBlock(x+2,y  ,z  ,b.id,5)
        mc.setBlock(x+3,y+1,z  ,b.id,5)
        mc.setBlock(x+4,y+2,z-1,b.id,6)
        mc.setBlock(x+4,y+3,z-2,b.id,6)
        mc.setBlock(x+4,y+4,z-3,b.id,6)
        mc.setBlock(x+3,y+5,z-4,b.id,4)
        mc.setBlock(x+2,y+6,z-4,b.id,4)
        mc.setBlock(x+1,y+7,z-4,b.id,4)
        mc.setBlock(x  ,y+8,z-3,b.id,7)
        mc.setBlock(x  ,y+9,z-2,b.id,7)
        mc.setBlock(x  ,y+10,z-1,b.id,7)
        mc.setSign (x+2,y+2 ,z  ,wallsignid,3,key,"id=" + str(b.id),"data=0")
        mc.setSign (x+3,y   ,z  ,wallsignid,3,key,"id=" + str(b.id),"data=5")
        mc.setSign (x+4,y+5 ,z-2,wallsignid,5,key,"id=" + str(b.id),"data=3")
        mc.setSign (x+4,y+3 ,z-3,wallsignid,5,key,"id=" + str(b.id),"data=6")
        mc.setSign (x+2,y+8 ,z-4,wallsignid,2,key,"id=" + str(b.id),"data=1")
        mc.setSign (x+1,y+6 ,z-4,wallsignid,2,key,"id=" + str(b.id),"data=4")
        mc.setSign (x  ,y+11,z-2,wallsignid,4,key,"id=" + str(b.id),"data=2")
        mc.setSign (x  ,y+9 ,z-1,wallsignid,4,key,"id=" + str(b.id),"data=7")
        y+=12
        untested.discard(b.id)
        
    #Display list of all blocks which did not get tested
    for id in untested:
        untest="Untested block " + str(id)
        for varname in blockmap[id]:
            untest+=" " + varname
        mc.postToChat(untest)
    mc.postToChat("runBlockTests() complete")

def runEntityTests(mc):
    """runEntityTests - tests creation of all entities known to RaspberryJuice
    
    A sign is placed next to the created entity so user can view in Minecraft whether block created correctly or not
    Known issues:
    - Some entities untested yet ["LEASH_HITCH","SNOWBALL","FIREBALL","SMALL_FIREBALL","ENDER_SIGNAL","PRIMED_TNT","DRAGON_FIREBALL","WITHER_SKULL","HUSK"]
    - Some entities don't spawn on one test but will spawn on a subsequent test
    
    Author: Tim Cummings https://www.triptera.com.au/wordpress/
    """
    all=["EXPERIENCE_ORB","AREA_EFFECT_CLOUD","ELDER_GUARDIAN","WITHER_SKELETON","STRAY","EGG","LEASH_HITCH","PAINTING","ARROW","SNOWBALL","FIREBALL","SMALL_FIREBALL","ENDER_PEARL","ENDER_SIGNAL","THROWN_EXP_BOTTLE","ITEM_FRAME","WITHER_SKULL","PRIMED_TNT","HUSK","SPECTRAL_ARROW","SHULKER_BULLET","DRAGON_FIREBALL","ZOMBIE_VILLAGER","SKELETON_HORSE","ZOMBIE_HORSE","ARMOR_STAND","DONKEY","MULE","EVOKER_FANGS","EVOKER","VEX","VINDICATOR","ILLUSIONER","MINECART_COMMAND","BOAT","MINECART","MINECART_CHEST","MINECART_FURNACE","MINECART_TNT","MINECART_HOPPER","MINECART_MOB_SPAWNER","CREEPER","SKELETON","SPIDER","GIANT","ZOMBIE","SLIME","GHAST","PIG_ZOMBIE","ENDERMAN","CAVE_SPIDER","SILVERFISH","BLAZE","MAGMA_CUBE","ENDER_DRAGON","WITHER","BAT","WITCH","ENDERMITE","GUARDIAN","SHULKER","PIG","SHEEP","COW","CHICKEN","SQUID","WOLF","MUSHROOM_COW","SNOWMAN","OCELOT","IRON_GOLEM","HORSE","RABBIT","POLAR_BEAR","LLAMA","LLAMA_SPIT","PARROT","VILLAGER","ENDER_CRYSTAL"]
    livers=["VILLAGER","WITHER_SKELETON","EGG","ZOMBIE_VILLAGER","SKELETON_HORSE","ZOMBIE_HORSE","DONKEY","MULE",
        "WITCH","SHULKER","PIG","SHEEP","COW","WOLF","MUSHROOM_COW","CREEPER",
        "OCELOT","IRON_GOLEM","HORSE","RABBIT","POLAR_BEAR","LLAMA","EVOKER","VINDICATOR"]
    items=["EXPERIENCE_ORB","AREA_EFFECT_CLOUD","ARROW","ENDER_PEARL","THROWN_EXP_BOTTLE","SPECTRAL_ARROW","SHULKER_BULLET","ARMOR_STAND","EVOKER_FANGS","VEX","BLAZE",
        "LLAMA_SPIT","ENDER_CRYSTAL"]
    minecarts=["MINECART_COMMAND","MINECART","MINECART_CHEST","MINECART_FURNACE","MINECART_TNT","MINECART_HOPPER","MINECART_MOB_SPAWNER"]
    floats=["BOAT"]
    sinks=["SQUID",]
    hangers=["PAINTING","ITEM_FRAME",]
    todo=["LEASH_HITCH","SNOWBALL","FIREBALL","SMALL_FIREBALL","ENDER_SIGNAL","PRIMED_TNT","DRAGON_FIREBALL","WITHER_SKULL","HUSK"]
    giants=["ELDER_GUARDIAN","GUARDIAN","GIANT","ENDER_DRAGON","GHAST"]
    cavers=["MAGMA_CUBE","BAT","PARROT","CHICKEN","STRAY","SKELETON","SPIDER","ZOMBIE","SLIME","CAVE_SPIDER","PIG_ZOMBIE","ENDERMAN","SNOWMAN","SILVERFISH","ILLUSIONER"]
    bosses=["WITHER"]
    # location for platform showing all block types
    xtest = 50
    ytest = 50
    ztest = 50
    air=blockmodded.AIR
    wall=blockmodded.GLASS
    roof=blockmodded.STONE
    floor=blockmodded.STONE
    fence=blockmodded.FENCE
    signmount=blockmodded.STONE
    sign=blockmodded.SIGN_STANDING.withData(4)
    signid=sign.id
    torch=blockmodded.TORCH.withData(5)
    rail=blockmodded.RAIL
    wallsignid=blockmodded.SIGN_WALL.id
    mc.postToChat("runEntityTests(): Creating test entities at x=" + str(xtest) + " y=" + str(ytest) + " z=" + str(ztest))
    #mc.setBlocks(xtest,ytest-1,ztest,xtest+100,ytest+50,ztest+100,air)
    
    #clear the area in segments, otherwise it breaks the server
    #clearing area
    for y_inc in range(0, 10):
        mc.setBlocks(xtest,ytest+y_inc,ztest,xtest+100,ytest+y_inc,ztest+100,air)
        time.sleep(2)

    mc.setBlocks(xtest,ytest-1,ztest-1,xtest+100,ytest-1,ztest+100,floor)
    mc.player.setTilePos(xtest, ytest, ztest)

    mc.postToChat("Dancing villager")
    r = 10
    x=xtest
    y=ytest
    z=ztest + r
    id=mc.spawnEntity(x,y,z,entitymodded.VILLAGER)
    theta = 0
    while theta <= 2 * math.pi:
        time.sleep(1)
        theta += 0.1
        x = xtest + math.sin(theta) * r
        z = ztest + math.cos(theta) * r
        mc.entity.setPos(id,x,y,z)
    

    # create set of all block ids to ensure they all get tested
    # note some blocks have different names but same ids so they only have to be tested once per id
    # create a map of ids to names so can see which ones haven't been tested by name
    untested=set()
    entitymap={}
    for varname in dir(entitymodded):
        var=getattr(entitymodded,varname)
        try:
            # check var has data and id and add id to untested set
            if varname[0] != '_': 
                untested.add(var.id)
                try:
                    names=entitymap[var.id]
                    names.append(varname)
                except KeyError:
                    entitymap[var.id]=[varname]
        except AttributeError:
            #only interested in objects with an id which behave like entities
            pass
    
    
    time.sleep(1)    
    x=xtest
    y=ytest
    z=ztest
    for key in items:
        z += 2
        if z > 98:
            z = ztest
            x += 10
        e = getattr(entitymodded,key)
        mc.setBlock(x+2,y,z,signmount)
        mc.setSign(x+2,y+1,z,sign,key,"id=" + str(e.id))
        mc.spawnEntity(x,y,z,e)
        untested.discard(e.id)
    for key in hangers:
        z += 3
        if z > 97:
            z = ztest
            x += 10
        e = getattr(entitymodded,key)
        mc.setBlocks(x+2,y,z-1,x+2,y+2,z+1,signmount)
        mc.setSign(x+1,y,z,wallsignid,4,key,"id=" + str(e.id))
        mc.spawnEntity(x+1,y+2,z,e)
        untested.discard(e.id)
    z = ztest - 4
    x += 10
    time.sleep(1)
    for key in livers:
        z += 4
        if z > 96:
            z = ztest
            x += 10
        e = getattr(entitymodded,key)
        mc.setBlocks(x-2,y,z-2,x+2,y,z+2,fence)
        mc.setBlocks(x-1,y,z-1,x+1,y,z+1,air)
        mc.setBlock(x-3,y,z-1,torch)
        mc.setSign(x-3,y,z,wallsignid,4,key,"id=" + str(e.id))
        mc.spawnEntity(x,y,z,e)
        untested.discard(e.id)
    x+=10
    z=ztest - 3
    time.sleep(1)
    for key in minecarts:
        z += 3
        if z > 97:
            z = ztest
            x += 10
        e = getattr(entitymodded,key)
        mc.setBlock(x+2,y,z,signmount)
        mc.setSign(x+2,y+1,z,sign,key,"id=" + str(e.id))
        mc.setBlock(x+2,y,z-1,torch.id,4)
        mc.setBlocks(x,y,z-1,x,y,z+1,rail)
        mc.spawnEntity(x,y,z,e)
        untested.discard(e.id)
    time.sleep(1)
    for key in floats:
        z += 5
        if z > 95:
            z = ztest
            x += 10
        e = getattr(entitymodded,key)
        mc.setBlock(x+2,y,z,signmount)
        mc.setSign(x+2,y+1,z,sign,key,"id=" + str(e.id))
        mc.setBlock(x+2,y,z-1,torch.id,2)
        mc.setBlocks(x-2,y-2,z-3,x+2,y-1,z+2,floor)
        mc.setBlocks(x-1,y-1,z-2,x+1,y-1,z+1,blockmodded.WATER_STATIONARY)
        mc.spawnEntity(x,y,z,e)
        untested.discard(e.id)
            
    x+=10
    z=ztest - 4
    time.sleep(1)
    for key in cavers:
        z += 4
        if z > 96:
            z = ztest
            x += 10
        e = getattr(entitymodded,key)
        mc.setBlocks(x,y,z,x+4,y+3,z+4,wall)
        mc.setBlocks(x,y+4,z,x+4,y+4,z+4,roof)
        mc.setBlocks(x-2,y-1,z,x+6,y-1,z+4,floor)
        mc.setBlocks(x+1,y,z+1,x+3,y+3,z+3,air)
        mc.setBlock(x-1,y,z+1,torch)
        mc.setSign(x-1,y,z+2,sign,key,"id=" + str(e.id))
        mc.spawnEntity(x+2,y,z+2,e)
        untested.discard(e.id)

    x=xtest
    y=ytest+10
    z=ztest
    time.sleep(1)
    for key in giants:
        e = getattr(entitymodded,key)
        mc.setBlocks(x,y,z,x+20,y+20,z+20,wall)
        mc.setBlocks(x,y+21,z,x+20,y+21,z+20,roof)
        mc.setBlocks(x-5,y-1,z-1,x+20,y-1,z+21,floor)
        mc.setBlocks(x+1,y,z+1,x+19,y+20,z+19,air)
        mc.setSign(x-1,y,z+2,sign,key,"id=" + str(e.id))
        mc.spawnEntity(x+10,y+5,z+10,e)
        untested.discard(e.id)
        z += 20
        if z > 80:
            z = ztest
            x += 25
    time.sleep(1)
    for key in bosses:
        e = getattr(entitymodded,key)
        mc.setBlocks(x,y,z,x+20,y+20,z+20,blockmodded.BEDROCK)
        mc.setBlocks(x,y+21,z,x+20,y+21,z+20,blockmodded.BEDROCK)
        mc.setBlocks(x-5,y-1,z-1,x+20,y-1,z+21,blockmodded.BEDROCK)
        mc.setBlocks(x+1,y,z+1,x+19,y+20,z+19,air)
        mc.setBlocks(x+1,y,z+8,x+19,y,z+12,torch)
        mc.setBlocks(x+1,y+10,z+2,x+1,y+15,z+18,torch.id,1)
        mc.setBlocks(x+19,y+10,z+2,x+19,y+15,z+18,torch.id,2)
        mc.setBlocks(x+1,y+10,z+19,x+19,y+15,z+19,torch.id,4)
        mc.setBlocks(x+1,y+10,z+1,x+19,y+15,z+1,torch.id,3)
        mc.setBlocks(x,y,z+9,x+3,y+3,z+11,blockmodded.BEDROCK)
        mc.setBlocks(x+4,y,z+9,x+19,y+3,z+11,wall)
        mc.setBlocks(x,y,z+10,x+19,y+2,z+10,air)
        mc.setSign(x-1,y,z+2,sign,key,"id=" + str(e.id))
        mc.spawnEntity(x+10,y+5,z+10,e)
        untested.discard(e.id)
        z += 20
        if z > 80:
            z = ztest
            x += 25
    time.sleep(1)
    for key in sinks:
        e = getattr(entitymodded,key)
        mc.setBlocks(x,y,z,x+20,y+20,z+20,wall)
        mc.setBlocks(x,y+21,z,x+20,y+21,z+20,roof)
        mc.setBlocks(x-5,y-1,z-1,x+20,y-1,z+21,floor)
        mc.setBlocks(x+1,y,z+1,x+19,y+20,z+19,blockmodded.WATER_STATIONARY)
        mc.setSign(x-1,y,z+2,sign,key,"id=" + str(e.id))
        mc.spawnEntity(x+10,y,z+10,e)
        untested.discard(e.id)
        z += 20
        if z > 80:
            z = ztest
            x += 25
    
    #Display list of all entities which did not get tested
    for id in untested:
        untest="Untested entity " + str(id)
        for varname in entitymap[id]:
            untest+=" " + varname
        mc.postToChat(untest)
    mc.postToChat("runEntityTests() completed. Use command")
    mc.postToChat("/kill @e[type=!player]")
    mc.postToChat("to remove test entities")


def runTests(mc, library="Standard library", extended=False):

    #Hello World
    mc.postToChat("Hello Minecraft World, testing starts for " + library)

    #get/setPos
    #get/setTilePos
    pos = mc.player.getPos()
    tilePos = mc.player.getTilePos()
    mc.postToChat("player.getPos()=" + str(pos.x) + ":" + str(pos.y) + ":" + str(pos.z))
    height = mc.getHeight(pos.x,pos.z)
    mc.postToChat("getHeight()=" + str(height))
    mc.player.setPos(pos.x,pos.y + 10,pos.z)
    mc.postToChat("player.getTilePos()=" + str(tilePos.x) + ":" + str(tilePos.y) + ":" + str(tilePos.z))
    mc.player.setTilePos(tilePos.x, tilePos.y, tilePos.z)

    if extended:
        direction = mc.player.getDirection()
        mc.postToChat("player.getDirection()=" + str(direction))
        rotation = mc.player.getRotation()
        mc.postToChat("player.getRotation()=" + str(rotation))
        pitch = mc.player.getPitch()
        mc.postToChat("player.getPitch()=" + str(pitch))
        mc.player.setDirection(0,0,1)
        mc.player.setRotation(180)
        mc.player.setPitch(-45)

    #getBlock
    below = mc.getBlock(pos.x,pos.y-1,pos.z)

    mc.postToChat("block below is - " + str(below))

    #getBlockWithData
    blockBelow = mc.getBlockWithData(pos.x, pos.y-1, pos.z)
    mc.postToChat("block data below is = " + str(blockBelow.data))


    #setBlock no data
    mc.setBlock(pos.x,pos.y+2,pos.z, block.GOLD_BLOCK.id)
    #setBlock with data
    mc.setBlock(pos.x,pos.y+3,pos.z, block.WOOL.id, 1)

    #setBlocks
    mc.setBlocks(pos.x,pos.y + 10,pos.z,
                 pos.x + 5, pos.y + 15, pos.z + 5,
                 block.WOOL.id, 5)

    #getBlocks
    if extended:
        listOfBlocks = mc.getBlocks(pos.x,pos.y + 10,pos.z,
                                    pos.x + 5, pos.y + 15, pos.z + 5)
        print(listOfBlocks)

    #getPlayerEntityIds
    playerids = mc.getPlayerEntityIds()
    mc.postToChat("playerIds()=" + str(playerids))
    if extended and len(playerids) > 0:
        playername = mc.entity.getName(playerids[0])
        mc.postToChat("player with id " + str(playerids[0]) + " has name " + playername)
        playerid = mc.getPlayerEntityId(playername)
        mc.postToChat("player with name " + playername + " has id " + str(playerid))

    #entity commands
    pos = mc.entity.getPos(playerids[0])
    tilePos = mc.entity.getTilePos(playerids[0])
    mc.postToChat("entity.getPos()=" + str(pos.x) + ":" + str(pos.y) + ":" + str(pos.z))
    mc.entity.setPos(playerids[0],pos.x,pos.y + 10,pos.z)
    mc.postToChat("entity.getTilePos()=" + str(tilePos.x) + ":" + str(tilePos.y) + ":" + str(tilePos.z))
    mc.entity.setTilePos(playerids[0],tilePos.x, tilePos.y, tilePos.z)
    if extended:
        direction = mc.entity.getDirection(playerids[0])
        mc.postToChat("entity.getDirection()=" + str(direction))
        rotation = mc.entity.getRotation(playerids[0])
        mc.postToChat("entity.getRotation()=" + str(rotation))
        pitch = mc.entity.getPitch(playerids[0])
        mc.postToChat("entity.getPitch()=" + str(pitch))
        mc.entity.setDirection(playerids[0],0,0,1)
        mc.entity.setRotation(playerids[0],180)
        mc.entity.setPitch(playerids[0],-45)

    #block hit events
    mc.postToChat("hit a block with sword")
    blockHit = False
    while not blockHit:
        time.sleep(1)
        blockEvents = mc.events.pollBlockHits()
        for blockEvent in blockEvents:
            mc.postToChat("You hit block - x:" + str(blockEvent.pos.x) + " y:" + str(blockEvent.pos.y) + " z:" + str(blockEvent.pos.z))
            blockHit = True

    if extended:
        entity_types = mc.getEntityTypes()
        mc.postToChat("The last found was entity: id=" + str(entity_types[-1].id) + " name=" + entity_types[-1].name)
        mc.spawnEntity(tilePos.x + 2, tilePos.y + 2, tilePos.x + 2, entitymodded.CREEPER)
        mc.postToChat("Creeper spawned")

        mc.postToChat("Fire Arrow")
        arrowFired = False
        while not arrowFired:
            time.sleep(1)
            projectileHits = mc.events.pollProjectileHits()
            for projectileHit in projectileHits:
                mc.postToChat("Arrow hit - x:" + str(projectileHit.pos.x) + " y:" + str(projectileHit.pos.y) + " z:" + str(projectileHit.pos.z))
                arrowFired = True
                
        mc.postToChat("Post To Chat - Run full block and entity test Y/N?")
        chatPosted = False
        fullTests = False
        while not chatPosted:
            time.sleep(1)
            chatPosts = mc.events.pollChatPosts()
            for chatPost in chatPosts:
                mc.postToChat("Echo " + chatPost.message)
                chatPosted = True
                if chatPost.message == "Y":
                    fullTests = True

        if fullTests:
            runBlockTests(mc)
            runEntityTests(mc)
    
    mc.postToChat("Tests complete for " + library)

#Standard Library Tests
#Connect to minecraft
mc = minecraft.Minecraft.create()
runTests(mc)

time.sleep(3)

#Modded Library Tests
mc = minecraftmodded.Minecraft.create()
runTests(mc, "Modded library", True)

mc.postToChat("ALL TESTS COMPLETE")
