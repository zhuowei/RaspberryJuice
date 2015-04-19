#Martin O'Hanlon
#www.stuffaboutcode.com
#RaspberryJuice Tests

import original.mcpi.minecraft as minecraft
import modded.mcpi.minecraft as minecraftmodded
import original.mcpi.block as block
import time

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
    
    mc.postToChat("Tests complete")

#Standard Library Tests
#Connect to minecraft
mc = minecraft.Minecraft.create()
mc.postToChat("Standard library")
runTests(mc)

time.sleep(3)

#Modded Library Tests
mc = minecraftmodded.Minecraft.create(name="martinohanlon")
mc.postToChat("Modded library")
runTests(mc, True)

