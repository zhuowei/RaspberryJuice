package net.zhuoweizhang.raspberryjuice;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
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

	private LocationType locationType;

	private HitClickType hitClickType;
	public LocationType getLocationType() {
		return locationType;
	}
	public HitClickType getHitClickType() {
		return hitClickType;
	}

	public void onEnable() {
		//save a copy of the default config.yml if one is not there
        this.saveDefaultConfig();
        //get host and port from config.yml
		String hostname = this.getConfig().getString("hostname");
		if (hostname == null || hostname.isEmpty()) hostname = "0.0.0.0";
		int port = this.getConfig().getInt("port");
		getLogger().info("Using host:port - " + hostname + ":" + Integer.toString(port));
		
		//get location type (ABSOLUTE or RELATIVE) from config.yml
		String location = this.getConfig().getString("location").toUpperCase();
		try {
			locationType = LocationType.valueOf(location);
		} catch(IllegalArgumentException e) {
			getLogger().warning("warning - location value in config.yml should be ABSOLUTE or RELATIVE - '" + location + "' found");
			locationType = LocationType.valueOf("RELATIVE");
		}
		getLogger().info("Using " + locationType.name() + " locations");

		//get hit click type (LEFT, RIGHT or BOTH) from config.yml
		String hitClick = this.getConfig().getString("hitclick").toUpperCase();
		try {
			hitClickType = HitClickType.valueOf(hitClick);
		} catch(IllegalArgumentException e) {
			getLogger().warning("warning - hitclick value in config.yml should be LEFT, RIGHT or BOTH - '" + hitClick + "' found");
			hitClickType = HitClickType.valueOf("RIGHT");
		}
		getLogger().info("Using " + hitClickType.name() + " clicks for hits");

		//setup session array
		sessions = new ArrayList<RemoteSession>();
		
		//create new tcp listener thread
		try {
			if (hostname.equals("0.0.0.0")) {
				serverThread = new ServerListenerThread(this, new InetSocketAddress(port));
			} else {
				serverThread = new ServerListenerThread(this, new InetSocketAddress(hostname, port));
			}
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
	
	@EventHandler
	public void PlayerJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		//p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 2, true, false));	// give night vision power
		Server server = getServer();
		server.broadcastMessage("Welcome " + p.getPlayerListName());
	}

	@EventHandler(ignoreCancelled=true)
	public void onPlayerInteract(PlayerInteractEvent event) {
		// only react to events which are of the correct type
		switch(hitClickType) {
			case BOTH:
				if ((event.getAction() != Action.RIGHT_CLICK_BLOCK) && (event.getAction() != Action.LEFT_CLICK_BLOCK)) return;
				break;
			case LEFT:
				if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;
				break;
			case RIGHT:
				if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
				break;
		}
		ItemStack currentTool = event.getItem();
		if (currentTool == null || !blockBreakDetectionTools.contains(currentTool.getType())) {
			return;
		}
		for (RemoteSession session: sessions) {
			session.queuePlayerInteractEvent(event);
		}
	}

	@EventHandler(ignoreCancelled=true)
	public void onChatPosted(AsyncPlayerChatEvent event) {
		//debug
		//getLogger().info("Chat event fired");
		for (RemoteSession session: sessions) {
			session.queueChatPostedEvent(event);
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	public void onProjectileHit(ProjectileHitEvent event) {
		
		for (RemoteSession session: sessions) {
			session.queueProjectileHitEvent(event);
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
		for(Player player : Bukkit.getOnlinePlayers()) {
			if (name.equals(player.getPlayerListName())) {
				return player;
			}
		}
		return null;
	}

	public Player getHostPlayer() {
		if (hostPlayer != null) return hostPlayer;
		for(Player player : Bukkit.getOnlinePlayers()) {
			return player;
		}
		return null;
	}

	//get entity by id - DONE to be compatible with the pi it should be changed to return an entity not a player...
	public Entity getEntity(int id) {
		for (Player p: getServer().getOnlinePlayers()) {
			if (p.getEntityId() == id) {
				return p;
			}
		}
		//check all entities in host player's world
		Player player = getHostPlayer();
		World w = player.getWorld();
		for (Entity e : w.getEntities()) {
			if (e.getEntityId() == id) {
				return e;
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
