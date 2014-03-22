package pi.demo.sokoban;

import java.util.*;
import static pi.Block.*;
import pi.*;
import pi.event.BlockHitEvent;

/**
 * Sokoban game for Minecraft
 *
 * @author Daniel Frisk, twitter:danfrisk
 */
public class Sokoban {
    Minecraft mc;
    //
    List<Level> levels = Level.loadLevels();
    Level level;
    //
    PositionSet walls = new PositionSet(), targets = new PositionSet(), stones = new PositionSet();

    public Sokoban(Minecraft minecraft) {
        this.mc = minecraft;
    }

    /**
     *
     */
    public static void main(String[] args) {
        new Sokoban(Minecraft.connect(args)).run();
    }

    public void run() {
        mc.postToChat("Sokoban!");
        mc.camera.setNormal();
        mc.setting("immutable", true);

        while (true) {
            if (stones.equals(targets)) {
                if (level != null) {
                    mc.postToChat("Level finished!");
                    sleep(2000);
                }
                int ni = level == null ? 0 : level.number >= levels.size() ? 0 : level.number;
                startLevel(levels.get(ni));
            }

            List<BlockHitEvent> hits = mc.events.pollBlockHits();
            if (!hits.isEmpty()) {
                BlockHitEvent ev = hits.iterator().next();
                onBlockHit(ev.position, ev.surfaceDirection.neg());
            }

            sleep(80);
        }
    }

    void startLevel(Level level) {
        this.level = level;

        walls.clear();
        targets.clear();
        stones.clear();

        // Clear the ground
        mc.setBlocks(Position.uv(0, 0).toWorld(0),
                Position.uv(level.width - 1, level.height - 1).toWorld(10),
                AIR);

        mc.setBlocks(Position.uv(0, 0).toWorld(-1),
                Position.uv(level.width - 1, level.height - 1).toWorld(-1),
                SANDSTONE);

        // Set level blocks and teleport the player
        for (int v = 0; v < level.height; v++) {
            for (int u = 0; u < level.width; u++) {
                Position pos = Position.uv(u, v);

                LevelTile t = level.get(pos);
                if (LevelTile.EMPTY == t) {
                    continue;
                }
                if (LevelTile.WALL == t) {
                    walls.add(pos);
                    mc.setBlocks(pos.toWorld(0), pos.toWorld(2), STONE);
                }
                if (LevelTile.PLAYER == t) {
                    mc.player.setPosition(pos.toWorld(2));
                }
                if (LevelTile.TARGET == t || LevelTile.TARGET_AND_STONE == t) {
                    targets.add(pos);
                    mc.setBlock(pos.toWorld(-1), wool(Color.LIGHT_BLUE));
                }
                if (LevelTile.STONE == t || LevelTile.TARGET_AND_STONE == t) {
                    stones.add(pos);
                    mc.setBlock(pos.toWorld(0), IRON_BLOCK);
                }
            }
        }

        mc.events.clearAll();
        mc.postToChat("Level " + level.number + " - " + level.name);
    }

    void onBlockHit(Vec position, Vec.Unit direction) {
        Position from = Position.fromWorld(position);
        Position to = Position.fromWorld(position.add(direction));
        if (!from.equals(to) && !walls.contains(to) && !stones.contains(to)) {
            if (stones.remove(from)) {
                stones.add(to);
                mc.setBlock(to.toWorld(0), targets.contains(to) ? GOLD_BLOCK : IRON_BLOCK);
                mc.setBlock(position, AIR);
            }
        }
    }

    void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException _) {
        }
    }

    /**
     *
     */
    private static class PositionSet extends HashSet<Position> {
    }
}
