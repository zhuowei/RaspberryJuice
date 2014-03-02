A Bukkit plugin implementing a subset of the Minecraft Pi Socket API.

Features currently supported:
 - world.get/setBlock
 - NEW: getBlocks - Returns a linear array of block IDs within the cube boundaries. Scan order is Y, X, Z. Y is the outer loop because it is height so you will get block information layer by layer.

Features that can't be supported:
 - Camera angles


