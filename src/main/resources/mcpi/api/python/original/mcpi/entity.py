class Entity:
    """Minecraft PI entity description. Can be sent to Minecraft.setEntity/s"""
    def __init__(self, id, data=0):
        self.id = id
        self.data = data

    def __cmp__(self, rhs):
        return hash(self) - hash(rhs)

    def __hash__(self):
        return (self.id << 8) + self.data

    def withData(self, data):
        return Block(self.id, data)

    def __iter__(self):
        """Allows an Entity to be sent whenever id [and data] is needed"""
        return iter((self.id, self.data))
        
    def __repr__(self):
        return "Entity(%d, %d)"%(self.id, self.data)

AREA_EFFECT_CLOUD=Entity(0)
ARMOR_STAND=Entity(1)
ARROW=Entity(2)
BAT=Entity(3)
BLAZE=Entity(4)
BOAT=Entity(5)
CAVE_SPIDER=Entity(6)
CHICKEN=Entity(7)
COMPLEX_PART=Entity(8)
COW=Entity(9)
CREEPER=Entity(10)
DRAGON_FIREBALL=Entity(11)
DROPPED_ITEM=Entity(12)
EGG=Entity(13)
ENDER_CRYSTAL=Entity(14)
ENDER_DRAGON=Entity(15)
ENDER_PEARL=Entity(16)
ENDER_SIGNAL=Entity(17)
ENDERMAN=Entity(18)
ENDERMITE=Entity(19)
EXPERIENCE_ORB=Entity(20)
FALLING_BLOCK=Entity(21)
FIREBALL=Entity(22)
FIREWORK=Entity(23)
FISHING_HOOK=Entity(24)
GHAST=Entity(25)
GIANT=Entity(26)
GUARDIAN=Entity(27)
HORSE=Entity(28)
IRON_GOLEM=Entity(29)
ITEM_FRAME=Entity(30)
LEASH_HITCH=Entity(31)
LIGHTNING=Entity(32)
LINGERING_POTION=Entity(33)
MAGMA_CUBE=Entity(34)
MINECART=Entity(35)
MINECART_CHEST=Entity(36)
MINECART_COMMAND=Entity(37)
MINECART_FURNACE=Entity(38)
MINECART_HOPPER=Entity(39)
MINECART_MOB_SPAWNER=Entity(40)
MINECART_TNT=Entity(41)
MUSHROOM_COW=Entity(42)
OCELOT=Entity(43)
PAINTING=Entity(44)
PIG=Entity(45)
PIG_ZOMBIE=Entity(46)
PLAYER=Entity(47)
POLAR_BEAR=Entity(48)
PRIMED_TNT=Entity(49)
RABBIT=Entity(50)
SHEEP=Entity(51)
SHULKER=Entity(52)
SHULKER_BULLET=Entity(53)
SILVERFISH=Entity(54)
SKELETON=Entity(55)
SLIME=Entity(56)
SMALL_FIREBALL=Entity(57)
SNOWBALL=Entity(58)
SNOWMAN=Entity(59)
SPECTRAL_ARROW=Entity(60)
SPIDER=Entity(61)
SPLASH_POTION=Entity(62)
SQUID=Entity(63)
THROWN_EXP_BOTTLE=Entity(64)
TIPPED_ARROW=Entity(65)
UNKNOWN=Entity(66)
VILLAGER=Entity(67)
WEATHER=Entity(68)
WITCH=Entity(69)
WITHER=Entity(70)
WITHER_SKULL=Entity(71)
WOLF=Entity(72)
ZOMBIE=Entity(73)

