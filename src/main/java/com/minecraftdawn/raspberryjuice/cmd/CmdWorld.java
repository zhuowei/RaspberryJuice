package com.minecraftdawn.raspberryjuice.cmd;

import com.minecraftdawn.raspberryjuice.RaspberryJuicePlugin;
import com.minecraftdawn.raspberryjuice.RemoteSession;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Collection;

public class CmdWorld {
	private String preFix = "world.";
	private String command;
	private String[] args;
	private RemoteSession session;
	private RaspberryJuicePlugin plugin;
	private World world;

	public CmdWorld(RemoteSession session, World world, String cmd, String[] args) {
		this.session = session;
		this.command = cmd;
		this.args = args;
		this.world = world;

		this.plugin = session.plugin;
	}

	public void execute() {

		// world.getBlock
		if (command.equals("getBlock")) {
			Location loc = session.parseRelativeBlockLocation(args[0], args[1], args[2]);

			session.send(world.getBlockAt(loc).getType().name());

			// world.getBlocks
		} else if (command.equals("getBlocks")) {
			Location loc1 = session.parseRelativeBlockLocation(args[0], args[1], args[2]);
			Location loc2 = session.parseRelativeBlockLocation(args[3], args[4], args[5]);

			session.send(getBlocks(loc1, loc2));

			// world.setBlock
		} else if (command.equals("setBlock")) {
			Location loc = session.parseRelativeBlockLocation(args[0], args[1], args[2]);

			updateBlock(world, loc, args[3]);

			// world.setBlocks
		} else if (command.equals("setBlocks")) {
			Location loc1 = session.parseRelativeBlockLocation(args[0], args[1], args[2]);
			Location loc2 = session.parseRelativeBlockLocation(args[3], args[4], args[5]);
			String blockType = args[6];

			setCuboid(loc1, loc2, blockType);

			// world.getPlayerIds
		} else if (command.equals("getPlayerIds")) {
			StringBuilder bdr = new StringBuilder();
			Collection<? extends Player> players = Bukkit.getOnlinePlayers();
			if (players.size() > 0) {
				for (Player p : players) {
					bdr.append(p.getEntityId());
					bdr.append("|");
				}
				bdr.deleteCharAt(bdr.length() - 1);
				session.send(bdr.toString());
			} else {
				session.send("Fail," + "There are no players in the server.");
			}

			// world.getPlayerId
		} else if (command.equals("getPlayerId")) {
			Player p = plugin.getNamedPlayer(args[0]);
			if (p != null) {
				session.send(p.getEntityId());
			} else {
				plugin.getLogger().info("Player [" + args[0] + "] not found.");
				session.send("Fail," + "T	he player not exist");
			}

			// world.getHeight
		} else if (command.equals("getHeight")) {
			session.send(world.getHighestBlockYAt(session.parseRelativeBlockLocation(args[0], "0", args[1])));

		}
		// world.setSign
		else if (command.equals("setSign")) {
			Location loc = session.parseRelativeBlockLocation(args[0], args[1], args[2]);
			Block thisBlock = world.getBlockAt(loc);

			thisBlock.setType(Material.valueOf(args[3]));

			org.bukkit.block.data.type.Sign s = (org.bukkit.block.data.type.Sign) thisBlock.getBlockData();
			s.setRotation(BlockFace.valueOf(args[4]));
			thisBlock.setBlockData(s);

			BlockState signState = thisBlock.getState();

			if (signState instanceof Sign) {
				Sign sign = (Sign) signState;

				for (int i = 5; i - 5 < 4 && i < args.length; i++) {
					sign.setLine(i - 5, args[i]);
				}
				sign.update();
			}


		} else if (command.equals("setWallSign")) {
			Location loc = session.parseRelativeBlockLocation(args[0], args[1], args[2]);
			Block thisBlock = world.getBlockAt(loc);
			thisBlock.setType(Material.valueOf(args[3]));

			WallSign s = (WallSign) thisBlock.getBlockData();
			s.setFacing(BlockFace.valueOf(args[4]));
			thisBlock.setBlockData(s);

			BlockState signState = thisBlock.getState();

			if (signState instanceof Sign) {
				Sign sign = (Sign) signState;

				for (int i = 5; i - 5 < 4 && i < args.length; i++) {
					sign.setLine(i - 5, args[i]);
				}
				sign.update();
			}

			// world.spawnEntity
		} else if (command.equals("spawnEntity")) {
			Location loc = session.parseRelativeBlockLocation(args[0], args[1], args[2]);
			Entity entity = world.spawnEntity(loc, EntityType.fromId(Integer.parseInt(args[3])));
			session.send(entity.getEntityId());

			// world.explode
		} else if (command.equals("createExplosion")) {
			Location loc = session.parseRelativeBlockLocation(args[0], args[1], args[2]);
			Float power = Float.parseFloat(args[3]);

			world.createExplosion(loc, power);

			// world.getEntityTypes
		} else if (command.equals("getEntityTypes")) {
			StringBuilder bdr = new StringBuilder();
			for (EntityType entityType : EntityType.values()) {
				if (entityType.isSpawnable() && entityType.getTypeId() >= 0) {
					bdr.append(entityType.getTypeId());
					bdr.append(",");
					bdr.append(entityType.toString());
					bdr.append("|");
				}
			}
			session.send(bdr.toString());

		} else {
			session.plugin.getLogger().warning(preFix + command + " is not supported.");
			session.send("Fail," + preFix + command + " is not supported.");
		}
	}

	// create a cuboid of lots of blocks
	private void setCuboid(Location pos1, Location pos2, String blockType) {
		int minX, maxX, minY, maxY, minZ, maxZ;
		World world = pos1.getWorld();
		minX = pos1.getBlockX() < pos2.getBlockX() ? pos1.getBlockX() : pos2.getBlockX();
		maxX = pos1.getBlockX() >= pos2.getBlockX() ? pos1.getBlockX() : pos2.getBlockX();
		minY = pos1.getBlockY() < pos2.getBlockY() ? pos1.getBlockY() : pos2.getBlockY();
		maxY = pos1.getBlockY() >= pos2.getBlockY() ? pos1.getBlockY() : pos2.getBlockY();
		minZ = pos1.getBlockZ() < pos2.getBlockZ() ? pos1.getBlockZ() : pos2.getBlockZ();
		maxZ = pos1.getBlockZ() >= pos2.getBlockZ() ? pos1.getBlockZ() : pos2.getBlockZ();

		for (int x = minX; x <= maxX; ++x) {
			for (int z = minZ; z <= maxZ; ++z) {
				for (int y = minY; y <= maxY; ++y) {
					updateBlock(world, x, y, z, blockType);
				}
			}
		}
	}

	// get a cuboid of lots of blocks
	private String getBlocks(Location pos1, Location pos2) {
		StringBuilder blockData = new StringBuilder();

		int minX, maxX, minY, maxY, minZ, maxZ;
		World world = pos1.getWorld();
		minX = pos1.getBlockX() < pos2.getBlockX() ? pos1.getBlockX() : pos2.getBlockX();
		maxX = pos1.getBlockX() >= pos2.getBlockX() ? pos1.getBlockX() : pos2.getBlockX();
		minY = pos1.getBlockY() < pos2.getBlockY() ? pos1.getBlockY() : pos2.getBlockY();
		maxY = pos1.getBlockY() >= pos2.getBlockY() ? pos1.getBlockY() : pos2.getBlockY();
		minZ = pos1.getBlockZ() < pos2.getBlockZ() ? pos1.getBlockZ() : pos2.getBlockZ();
		maxZ = pos1.getBlockZ() >= pos2.getBlockZ() ? pos1.getBlockZ() : pos2.getBlockZ();

		for (int y = minY; y <= maxY; ++y) {
			for (int x = minX; x <= maxX; ++x) {
				for (int z = minZ; z <= maxZ; ++z) {
					blockData.append(world.getBlockAt(x, y, z).getType().name() + ",");
				}
			}
		}

		return blockData.substring(0, blockData.length() > 0 ? blockData.length() - 1 : 0);    // We don't want last comma
	}

	// updates a block
	private void updateBlock(World world, Location loc, String blockType) {
		Block thisBlock = world.getBlockAt(loc);
		updateBlock(thisBlock, blockType);
	}

	private void updateBlock(World world, int x, int y, int z, String blockType) {
		Block thisBlock = world.getBlockAt(x, y, z);
		updateBlock(thisBlock, blockType);
	}

	private void updateBlock(Block thisBlock, String blockType) {
		// check to see if the block is different - otherwise leave it
		blockType = blockType.toUpperCase();
		if ((thisBlock.getType() != Material.valueOf(blockType))) {
			thisBlock.setType(Material.valueOf(blockType.toUpperCase()));
		}
	}
}
