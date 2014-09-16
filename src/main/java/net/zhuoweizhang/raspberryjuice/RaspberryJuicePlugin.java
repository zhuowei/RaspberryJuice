package net.zhuoweizhang.raspberryjuice;

import java.io.*;
import java.net.*;
import java.util.*;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class RaspberryJuicePlugin extends JavaPlugin implements Listener {

	public static final Set<Material> blockBreakDetectionTools = EnumSet.of(Material.DIAMOND_SWORD,
		Material.GOLD_SWORD, Material.IRON_SWORD, Material.STONE_SWORD, Material.WOOD_SWORD);

	public ServerListenerThread serverThread;

	public List<RemoteSession> sessions;

	public Player hostPlayer = null;

	private int tickTimerId = -1;

	public void onEnable() {
		//save a copy of the default config.yml if one is not there
        this.saveDefaultConfig();
        
		sessions = new ArrayList<RemoteSession>();
		try {
			//get port frrom config.yml
			int port = this.getConfig().getInt("port");
			serverThread = new ServerListenerThread(this, new InetSocketAddress(port));
			new Thread(serverThread).start();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		getServer().getPluginManager().registerEvents(this, this);
		tickTimerId = getServer().getScheduler().scheduleSyncRepeatingTask(this, new TickHandler(), 1, 1);
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
			System.out.println("Kicking " + newSession.getSocket().getRemoteSocketAddress() + " because the IP address has been banned.");
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

	public boolean checkBanned(RemoteSession session) {
		Set<String> ipBans = getServer().getIPBans();
		String sessionIp = session.getSocket().getInetAddress().getHostAddress();
		return ipBans.contains(sessionIp);
	}


	public void onDisable() {
		serverThread.running = false;
		try {
			serverThread.serverSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		getServer().getScheduler().cancelTasks(this);
		for (RemoteSession session: sessions) {
			try {
				session.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		sessions = null;
		serverThread = null;
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

