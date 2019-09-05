package net.zhuoweizhang.raspberryjuice.cmd;

import net.zhuoweizhang.raspberryjuice.RemoteSession;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class CmdEvent {
    private final String preFix = "events.";
    private RemoteSession session;

    public CmdEvent(RemoteSession session) {
        this.session = session;
    }

    public void execute(String command, String[] args) {
        // events.clear
        if (command.equals("clear")) {
            session.interactEventQueue.clear();
            session.chatPostedQueue.clear();

            // events.block.hits
        } else if (command.equals("block.hits")) {
            StringBuilder b = new StringBuilder();
            PlayerInteractEvent event;
            while ((event = session.interactEventQueue.poll()) != null) {
                Block block = event.getClickedBlock();
                Location loc = block.getLocation();
                b.append(session.blockLocationToRelative(loc));
                b.append(",");
                b.append(session.blockFaceToNotch(event.getBlockFace()));
                b.append(",");
                b.append(event.getPlayer().getEntityId());
                if (session.interactEventQueue.size() > 0) {
                    b.append("|");
                }
            }
            session.send(b.toString());

            // events.chat.posts
        } else if (command.equals("chat.posts")) {
            StringBuilder b = new StringBuilder();
            AsyncPlayerChatEvent event;
            while ((event = session.chatPostedQueue.poll()) != null) {
                b.append(event.getPlayer().getEntityId());
                b.append(",");
                b.append(event.getMessage());
                if (session.chatPostedQueue.size() > 0) {
                    b.append("|");
                }
            }
            session.send(b.toString());

        } else {
            session.plugin.getLogger().warning(preFix + command + " is not supported.");
            session.send("Fail," + preFix + command + " is not supported.");
        }
    }
}
