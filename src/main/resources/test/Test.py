#Martin O'Hanlon
#www.stuffaboutcode.com
#RaspberryJuice Tests

import original.mcpi.minecraft as minecraft
import modded.mcpi.minecraft as minecraftmodded
import original.mcpi.block as block
import modded.mcpi.block as blockmodded
import time

def runBlockTests(mc):
    """runBlockTests - tests creation of all blocks for all data values known to RaspberryJuice
    
    A sign is placed next to the created block so user can view in Minecraft whether block created correctly or not
    Known issues:
    - CACTUS not creating correctly - may be a Minecraft constraint
    - MUSHROOM_RED not creating correctly - may be a Minecraft constraint
    - MUSHROOM_RED not creating correctly - may be a Minecraft constraint
    - id for NETHER_REACTOR_CORE wrong
    - some LEAVES missing but probably because they decay by the time user sees them
    - this test doesn't try activation of TNT
    
    Author: Tim Cummings https://www.triptera.com.au/wordpress/
    """

    solids=["STONE","DIRT","COBBLESTONE","BEDROCK","SAND","GRAVEL","GOLD_ORE","IRON_ORE","COAL_ORE","GLASS","LAPIS_LAZULI_ORE",
            "LAPIS_LAZULI_BLOCK","COBWEB","GOLD_BLOCK","IRON_BLOCK","BRICK_BLOCK","TNT","BOOKSHELF","MOSS_STONE","OBSIDIAN",
            "DIAMOND_ORE","DIAMOND_BLOCK","CRAFTING_TABLE","FARMLAND","REDSTONE_ORE","CLAY","PUMPKIN","NETHERRACK","SOUL_SAND",
            "GLOWSTONE_BLOCK","LIT_PUMPKIN","STAINED_GLASS","GLASS_PANE","END_STONE","EMERALD_ORE","GLOWING_OBSIDIAN","NETHER_REACTOR_CORE"]
    woods=["WOOD_PLANKS"]
    trees=["WOOD","LEAVES"]
    trees2=["LEAVES2"] #options are acacia and dark oak
    plants=["GRASS","DEAD_BUSH","FLOWER_CYAN","FLOWER_YELLOW","MUSHROOM_BROWN","MUSHROOM_RED","CACTUS","SUGAR_CANE","MELON"]
    liquids=["WATER","LAVA"]
    beds=["BED"]
    wools=["WOOL"]
    flats=["RAIL","RAIL_POWERED","RAIL_DETECTOR","RAIL_ACTIVATOR","SNOW","ICE","TRAPDOOR","TRAPDOOR_IRON"]
    slabs=["STONE_SLAB","STONE_SLAB_DOUBLE","WOODEN_SLAB"]
    torches=["TORCH","TORCH_REDSTONE"]
    gases=["AIR","FIRE"]
    stairs=["STAIRS_WOOD","STAIRS_COBBLESTONE","STAIRS_BRICK","STAIRS_STONE_BRICK","STAIRS_NETHER_BRICK","STAIRS_SANDSTONE"]
    signs=["SIGN_STANDING","SIGN_WALL"]
    doors=["DOOR_WOOD","DOOR_IRON","DOOR_SPRUCE","DOOR_BIRCH","DOOR_JUNGLE","DOOR_ACACIA","DOOR_DARK_OAK"]
    gates=["FENCE_GATE"]
    wallmounts=["LADDER","CHEST","FURNACE_INACTIVE","FURNACE_ACTIVE"]
    saplings=["SAPLING"]
    tallgrasses=["GRASS_TALL"]
    stonebricks=["STONE_BRICK"]
    snowblocks=["SNOW_BLOCK"]
    sandstones=["SANDSTONE"]
    
    xtest = 0
    ytest = 100
    ztest = 0
    mc.postToChat("runBlockTests(): Creating full suite of blocks at x=" + str(xtest) + " y=" + str(ytest) + " z=" + str(ztest))
    
    signmount=blockmodded.STONE
    signid=blockmodded.SIGN_STANDING.id
    
    x=xtest
    y=ytest-1
    z=ztest
    mc.setBlocks(x,y,z,x+100,y,z+100,blockmodded.STONE)
    y+=1
    mc.setBlocks(x,y,z,x+100,y+50,z+100,blockmodded.AIR)
    
    x=xtest+10
    z=ztest+10
    for key in solids + gases + flats:
        b = getattr(blockmodded,key)
        z += 1
        mc.setBlock(x-1,y,z,signmount)
        mc.setSign(x-1,y+1,z,signid,12,key,"id=" + str(b.id),"data=" + str(b.data))
        mc.setBlock(x,y,z,b)
    
    x=xtest+20
    z=ztest+10
    for key in trees:
        for data in range(16):
            b = getattr(blockmodded,key).withData(data)
            z += 1
            mc.setBlock(x-1,y,z,signmount)
            mc.setSign(x-1,y+1,z,signid,12,key,"id=" + str(b.id),"data=" + str(b.data))
            mc.setBlock(x,y,z,b)
        
    for key in trees2:
        for data in range(2):
            b = getattr(blockmodded,key).withData(data)
            z += 1
            mc.setBlock(x-1,y,z,signmount)
            mc.setSign(x-1,y+1,z,signid,12,key,"id=" + str(b.id),"data=" + str(b.data))
            mc.setBlock(x,y,z,b)
        
    for key in woods + stonebricks:
        b = getattr(blockmodded,key)
        for data in range(4):
            b = b.withData(data)
            z += 1
            mc.setBlock(x-1,y,z,signmount)
            mc.setSign(x-1,y+1,z,signid,12,key,"id=" + str(b.id),"data=" + str(b.data))
            mc.setBlock(x,y,z,b)
        
    for key in sandstones:
        b = getattr(blockmodded,key)
        for data in range(3):
            b = b.withData(data)
            z += 1
            mc.setBlock(x-1,y,z,signmount)
            mc.setSign(x-1,y+1,z,signid,12,key,"id=" + str(b.id),"data=" + str(b.data))
            mc.setBlock(x,y,z,b)
        
    for key in saplings + tallgrasses:
        b = getattr(blockmodded,key)
        for data in range(4):
            b = b.withData(data)
            z += 1
            mc.setBlock(x-1,y,z,signmount)
            mc.setSign(x-1,y+1,z,signid,12,key,"id=" + str(b.id),"data=" + str(b.data))
            mc.setBlock(x,y-1,z,blockmodded.DIRT)
            mc.setBlock(x,y,z,b)
        
    for key in plants:
        b = getattr(blockmodded,key)
        z += 1
        mc.setBlock(x-1,y,z,signmount)
        mc.setSign(x-1,y+1,z,signid,12,key,"id=" + str(b.id),"data=" + str(b.data))
        mc.setBlock(x,y-1,z,blockmodded.DIRT)
        mc.setBlock(x+1,y-2,z,blockmodded.DIRT)
        mc.setBlock(x+1,y-1,z,blockmodded.WATER)
        mc.setBlock(x,y,z,b)
    
    x=xtest+30
    z=ztest+10
    for key in wools:
        for data in range(16):
            b = getattr(blockmodded,key).withData(data)
            z += 1
            mc.setBlock(x-1,y,z,signmount)
            mc.setSign(x-1,y+1,z,signid,12,key,"id=" + str(b.id),"data=" + str(b.data))
            mc.setBlock(x,y,z,b)
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
            
    
    x=xtest+40
    z=ztest+10
    for key in liquids:
        z += 1
        mc.setBlocks(x-1,y,z,x+1,y,z+10,blockmodded.STONE)
        b = getattr(blockmodded,key + "_STATIONARY")
        z += 1
        mc.setSign(x-1,y+1,z,signid,12,key+"_STATIONARY","id=" + str(b.id),"data=" + str(b.data))
        mc.setBlock(x,y,z,b)
        for data in range(8):
            b = getattr(blockmodded,key).withData(data)
            z += 1
            mc.setSign(x-1,y+1,z,signid,12,key,"id=" + str(b.id),"data=" + str(b.data))
            mc.setBlock(x,y,z,b)
    
    for key in snowblocks:
        z += 1
        mc.setBlocks(x-1,y,z,x-1,y,z+8,blockmodded.STONE)
        for data in range(8):
            b = getattr(blockmodded,key).withData(data)
            z += 1
            mc.setSign(x-1,y+1,z,signid,12,key,"id=" + str(b.id),"data=" + str(b.data))
            mc.setBlock(x,y,z,b)
    
    x=xtest+50
    z=ztest+10
    for key in slabs:
        for data in range(8):
            b = getattr(blockmodded,key).withData(data)
            z += 1
            mc.setBlock(x-1,y,z,signmount)
            mc.setSign(x-1,y+1,z,signid,12,key,"id=" + str(b.id),"data=" + str(b.data))
            mc.setBlock(x,y,z,b)
    
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
    mc.postToChat("runBlockTests() completed")

def runTests(mc, extended=False):

    #Hello World
    mc.postToChat("Hello Minecraft World, testing starts")

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
        mc.postToChat(direction)
        rotation = mc.player.getRotation()
        mc.postToChat("player.getRotation()=" + str(rotation))
        pitch = mc.player.getPitch()
        mc.postToChat("player.getPitch()=" + str(pitch))

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
    if extended:
        playerid = mc.getPlayerEntityId("martinohanlon")
        mc.postToChat("playerId(martinohanlon)="+str(playerid))

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

    #block hit events
    mc.postToChat("hit a block")
    blockHit = False
    while not blockHit:
        time.sleep(0.1)
        blockEvents = mc.events.pollBlockHits()
        for blockEvent in blockEvents:
            mc.postToChat("You hit block - x:" + str(blockEvent.pos.x) + " y:" + str(blockEvent.pos.y) + " z:" + str(blockEvent.pos.z))
            blockHit = True

    if extended:
        mc.postToChat("Post To Chat")
        chatPosted = False
        while not chatPosted:
            time.sleep(0.1)
            chatPosts = mc.events.pollChatPosts()
            for chatPost in chatPosts:
                mc.postToChat("Echo " + chatPost.message)
                chatPosted = True
                
    if extended:
        runBlockTests(mc)
    
    mc.postToChat("Tests complete")

#Standard Library Tests
#Connect to minecraft
mc = minecraft.Minecraft.create()
mc.postToChat("Standard library")
runTests(mc)

time.sleep(3)

#Modded Library Tests
mc = minecraftmodded.Minecraft.create()
mc.postToChat("Modded library")
runTests(mc, True)

