from mcpi.minecraft import Minecraft
import hashlib
# Assignment 3 main file
# Feel free to modify, and/or to add other modules/classes in this or other files


import base64


st = "Ben test string"
stb = b"Ben test string"

li = []
for i in range(len(st)):
    li.append(chr( (ord(st[i]) + i) ^ 1417 ))

ha = hashlib.md5(stb).hexdigest()[:10]

out = ''.join(li)
out = out + ha
print(out)

mc = Minecraft.create("127.0.0.1", 4711)

mc.postToChat(out)
