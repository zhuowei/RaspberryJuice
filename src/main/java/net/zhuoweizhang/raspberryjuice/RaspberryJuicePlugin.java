package net.zhuoweizhang.raspberryjuice;

import java.io.*;
import java.net.*;
import java.util.*;

import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class RaspberryJuicePlugin extends JavaPlugin implements Listener {

	public static final Set<Material> blockBreakDetectionTools = EnumSet.of(Material.DIAMOND_SWORD, 
		Material.GOLD_SWORD, Material.IRON_SWORD, Material.STONE_SWORD, Material.WOOD_SWORD);
	public static final int DEFAULT_PORT = 4711;

	public ServerListenerThread serverThread;

	public List<RemoteSession> sessions;

	private int tickTimerId = -1;

	public void onEnable() {
		sessions = new ArrayList<RemoteSession>();
		try {
			serverThread = new ServerListenerThread(this, new InetSocketAddress(DEFAULT_PORT));
			new Thread(serverThread).start();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		getServer().getPluginManager().registerEvents(this, this);
		tickTimerId = getServer().getScheduler().scheduleSyncRepeatingTask(this, new TickHandler(), 1, 1);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		ItemStack currentTool = event.getPlayer().getItemInHand();
		if (currentTool == null || !blockBreakDetectionTools.contains(currentTool.getType())) {
			return;
		}
		for (RemoteSession session: sessions) {
			session.queueBlockBreakEvent(event);
		}
	}

	/** called when a new session is established. */
	public void handleConnection(RemoteSession newSession) {
		synchronized(sessions) {
			sessions.add(newSession);
		}
	}


	public void onDisable() {
		serverThread.running = false;
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
					sI.remove();
				} else {
					s.tick();
				}
			}
		}
	}
}

