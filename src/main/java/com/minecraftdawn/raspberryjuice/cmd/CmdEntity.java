package com.minecraftdawn.raspberryjuice.cmd;

import com.minecraftdawn.raspberryjuice.RaspberryJuicePlugin;
import com.minecraftdawn.raspberryjuice.RemoteSession;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class CmdEntity {
	private String preFix = "entity.";
	private String command;
	private String[] args;
	private RemoteSession session;
	private RaspberryJuicePlugin plugin;

	public CmdEntity(RemoteSession session, String cmd, String[] args) {
		this.session = session;
		this.command = cmd;
		this.args = args;

		this.plugin = session.plugin;
	}

	public void execute() {

		//get entity based on id
		Entity entity = plugin.getEntity(Integer.parseInt(args[0]));

		if (entity == null) {
			plugin.getLogger().info("Entity [" + args[0] + "] not found.");
			session.send("Fail,This entity identity not exist");
		}

		// entity.getTile
		if (command.equals("getTile")) {

			session.send(session.blockLocationToRelative(entity.getLocation()));

			// entity.setTile
		} else if (command.equals("setTile")) {
			String x = args[1], y = args[2], z = args[3];
			Location loc = entity.getLocation();

			entity.teleport(session.parseRelativeBlockLocation(x, y, z, loc.getPitch(), loc.getYaw()));

			// entity.getPos
		} else if (command.equals("getPos")) {

			session.send(session.locationToRelative(entity.getLocation()));

			// entity.setPos
		} else if (command.equals("setPos")) {
			String x = args[1], y = args[2], z = args[3];
			Location loc = entity.getLocation();

			entity.teleport(session.parseRelativeLocation(x, y, z, loc.getPitch(), loc.getYaw()));

			// entity.setDirection
		} else if (command.equals("setDirection")) {
			Double x = Double.parseDouble(args[1]);
			Double y = Double.parseDouble(args[2]);
			Double z = Double.parseDouble(args[3]);
			Location loc = entity.getLocation();

			loc.setDirection(new Vector(x, y, z));
			entity.teleport(loc);

			// entity.getDirection
		} else if (command.equals("getDirection")) {

			session.send(entity.getLocation().getDirection().toString());

			// entity.setRotation
		} else if (command.equals("setRotation")) {
			Float yaw = Float.parseFloat(args[1]);
			Location loc = entity.getLocation();

			loc.setYaw(yaw);
			entity.teleport(loc);

			// entity.getRotation
		} else if (command.equals("getRotation")) {

			session.send(entity.getLocation().getYaw());

			// entity.setPitch
		} else if (command.equals("setPitch")) {
			Float pitch = Float.parseFloat(args[1]);
			Location loc = entity.getLocation();

			loc.setPitch(pitch);
			entity.teleport(loc);

			// entity.getPitch
		} else if (command.equals("getPitch")) {
			session.send(entity.getLocation().getPitch());

			// entity.getListName
		} else if (command.equals("entity.getName")) {
			if (entity instanceof Player) {
				Player p = (Player) entity;
				//sending list name because plugin.getNamedPlayer() uses list name
				session.send(p.getPlayerListName());
			} else {
				session.send(entity.getName());
			}

		} else {
			session.plugin.getLogger().warning(preFix + command + " is not supported.");
			session.send("Fail," + preFix + command + " is not supported.");
		}

	}
}
