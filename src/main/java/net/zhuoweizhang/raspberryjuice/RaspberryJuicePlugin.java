package net.zhuoweizhang.raspberryjuice;

import java.net.*;
import java.util.*;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class RaspberryJuicePlugin extends JavaPlugin implements Listener {

	public static final Set<Material> blockBreakDetectionTools = EnumSet.of(
			Material.DIAMOND_SWORD,
			Material.GOLD_SWORD, 
			Material.IRON_SWORD, 
			Material.STONE_SWORD, 
			Material.WOOD_SWORD);

	public ServerListenerThread serverThread;

	public List<RemoteSession> sessions;

	public Player hostPlayer = null;

	public void onEnable() {
		//save a copy of the default config.yml if one is not there
        this.saveDefaultConfig();
        //get port from config.yml
		int port = this.getConfig().getInt("port");
        
        //setup session array
		sessions = new ArrayList<RemoteSession>();
		
		//create new tcp listener thread
		try {
			serverThread = new ServerListenerThread(this, new InetSocketAddress(port));
			new Thread(serverThread).start();
			getLogger().info("ThreadListener Started");
		} catch (Exception e) {
			e.printStackTrace();
			getLogger().warning("Failed to start ThreadListener");
			return;
		}
		//register the events
		getServer().getPluginManager().registerEvents(this, this);
		//setup the schedule to called the tick handler
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new TickHandler(), 1, 1);
	}

	@EventHandler(ignoreCancelled=true)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		ItemStack currentTool = event.getPlayer().getItemInHand();
		if (currentTool == null || !blockBreakDetectionTools.contains(currentTool.getType())) {
			return;
		}
		for (RemoteSession session: sessions) {
			session.queuePlayerInteractEvent(event);
		}
	}

	/** called when a new session is established. */
	public void handleConnection(RemoteSession newSession) {
		if (checkBanned(newSession)) {
			getLogger().warning("Kicking " + newSession.getSocket().getRemoteSocketAddress() + " because the IP address has been banned.");
			newSession.kick("You've been banned from this server!");
			return;
		}
		synchronized(sessions) {
			sessions.add(newSession);
		}
	}

	public Player getNamedPlayer(String name) {
		if (name == null) return null;
		Player[] allPlayers = getServer().getOnlinePlayers();
		for (int i = 0; i < allPlayers.length; ++i) {
			if (name.equals(allPlayers[i].getPlayerListName())) {
				return allPlayers[i];
			}
		}
		return null;
	}

	public Player getHostPlayer() {
		if (hostPlayer != null) return hostPlayer;
		Player[] allPlayers = getServer().getOnlinePlayers();
		if (allPlayers.length >= 1)
			return allPlayers[0];
		return null;
	}
	
	//get entity by id - TODO to be compatible with the pi it should be changed to return an entity not a player...
	public Player getEntity(int id) {
		for (Player p: getServer().getOnlinePlayers()) {
            if (p.getEntityId() == id) {
                return p;
            }
        }
		return null;
	}

	public boolean checkBanned(RemoteSession session) {
		Set<String> ipBans = getServer().getIPBans();
		String sessionIp = session.getSocket().getInetAddress().getHostAddress();
		return ipBans.contains(sessionIp);
	}


	public void onDisable() {
		getServer().getScheduler().cancelTasks(this);
		for (RemoteSession session: sessions) {
			try {
				session.close();
			} catch (Exception e) {
				getLogger().warning("Failed to close RemoteSession");
				e.printStackTrace();
			}
		}
		serverThread.running = false;
		try {
			serverThread.serverSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		sessions = null;
		serverThread = null;
		getLogger().info("Raspberry Juice Stopped");
	}

	private class TickHandler implements Runnable {
		public void run() {
			Iterator<RemoteSession> sI = sessions.iterator();
			while(sI.hasNext()) {
				RemoteSession s = sI.next();
				if (s.pendingRemoval) {
					s.close();
					sI.remove();
				} else {
					s.tick();
				}
			}
		}
	}
}

