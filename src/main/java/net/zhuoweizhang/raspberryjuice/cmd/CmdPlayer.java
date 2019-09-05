package net.zhuoweizhang.raspberryjuice.cmd;

import net.zhuoweizhang.raspberryjuice.RemoteSession;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class CmdPlayer {
	private final String preFix = "player.";
	private RemoteSession session;

	public CmdPlayer(RemoteSession session) {
		this.session = session;
	}

	private boolean serverHasPlayer() {
		return !Bukkit.getOnlinePlayers().isEmpty();
	}

	private Player getCurrentPlayer() {
		if (!serverHasPlayer()) {
			session.send("Fail,There are no players in the server.");
			return null;
		} else {
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				return player;
			}
		}
		return null;
	}

	public void execute(String command, String[] args) {

		Player currentPlayer = getCurrentPlayer();
		if (currentPlayer == null) {
			session.send("Fail,There are no players in the server.");
			return;
		}

		// player.getTile
		if (command.equals("getTile")) {

			session.send(session.blockLocationToRelative(currentPlayer.getLocation()));

			// player.setTile
		} else if (command.equals("setTile")) {
			String x = args[0], y = args[1], z = args[2];

			//get players current location, so when they are moved we will use the same pitch and yaw (rotation)
			Location loc = currentPlayer.getLocation();
			currentPlayer.teleport(session.parseRelativeBlockLocation(x, y, z, loc.getPitch(), loc.getYaw()));

			// player.getAbsPos
		} else if (command.equals("getAbsPos")) {

			session.send(currentPlayer.getLocation());

			// player.setAbsPos
		} else if (command.equals("setAbsPos")) {
			String x = args[0], y = args[1], z = args[2];

			//get players current location, so when they are moved we will use the same pitch and yaw (rotation)
			Location loc = currentPlayer.getLocation();
			loc.setX(Double.parseDouble(x));
			loc.setY(Double.parseDouble(y));
			loc.setZ(Double.parseDouble(z));
			currentPlayer.teleport(loc);

			// player.getPos
		} else if (command.equals("getPos")) {

			session.send(session.locationToRelative(currentPlayer.getLocation()));

			// player.setPos
		} else if (command.equals("setPos")) {
			String x = args[0], y = args[1], z = args[2];

			//get players current location, so when they are moved we will use the same pitch and yaw (rotation)
			Location loc = currentPlayer.getLocation();
			currentPlayer.teleport(session.parseRelativeLocation(x, y, z, loc.getPitch(), loc.getYaw()));

			// player.setDirection
		} else if (command.equals("setDirection")) {
			Double x = Double.parseDouble(args[0]);
			Double y = Double.parseDouble(args[1]);
			Double z = Double.parseDouble(args[2]);

			Location loc = currentPlayer.getLocation();
			loc.setDirection(new Vector(x, y, z));
			currentPlayer.teleport(loc);

			// player.getDirection
		} else if (command.equals("getDirection")) {

			session.send(currentPlayer.getLocation().getDirection().toString());

			// player.setRotation
		} else if (command.equals("setRotation")) {
			Float yaw = Float.parseFloat(args[0]);

			Location loc = currentPlayer.getLocation();
			loc.setYaw(yaw);
			currentPlayer.teleport(loc);

			// player.getRotation
		} else if (command.equals("getRotation")) {

			float yaw = currentPlayer.getLocation().getYaw();
			// turn bukkit's 0 - -360 to positive numbers
			if (yaw < 0) yaw = yaw * -1;
			session.send(yaw);

			// player.setPitch
		} else if (command.equals("setPitch")) {
			Float pitch = Float.parseFloat(args[0]);

			Location loc = currentPlayer.getLocation();
			loc.setPitch(pitch);
			currentPlayer.teleport(loc);

			// player.getPitch
		} else if (command.equals("getPitch")) {

			session.send(currentPlayer.getLocation().getPitch());

			// player.sendTitle
		} else if (command.equals("sendTitle")) {

			String title = args[0];
			String subTitle = args[1];
			Integer fadeIn = Integer.parseInt(args[2]);
			Integer stay = Integer.parseInt(args[3]);
			Integer fadeOut = Integer.parseInt(args[4]);
			currentPlayer.sendTitle(title, subTitle, fadeIn, stay, fadeOut);

		} else {
			session.plugin.getLogger().warning(preFix + command + " is not supported.");
			session.send("Fail," + preFix + command + " is not supported.");
		}
	}

}
