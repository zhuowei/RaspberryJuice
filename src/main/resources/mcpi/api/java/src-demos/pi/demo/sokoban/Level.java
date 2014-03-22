package pi.demo.sokoban;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * A Sokoban level
 *
 * @author Daniel Frisk, twitter:danfrisk
 */
class Level {
    int number;
    String name;
    LevelTile[][] tiles;
    int width, height;

    Level(String data, int number) {
        this.number = number;
        Scanner s = new Scanner(data);
        this.name = s.nextLine();
        List<LevelTile[]> rows = new ArrayList<LevelTile[]>();
        while (s.hasNextLine()) {
            LevelTile[] row = parseRow(s.nextLine());
            this.width = Math.max(width, row.length);
            rows.add(row);
        }
        this.height = rows.size();
        tiles = rows.toArray(new LevelTile[height][]);
    }

    LevelTile get(Position p) {
        LevelTile[] row = tiles[p.j];
        return (p.i < row.length) ? row[p.i] : LevelTile.EMPTY;
    }

    private static LevelTile[] parseRow(String line) {
        List<LevelTile> row = new ArrayList<LevelTile>();
        for (char c : line.toCharArray()) {
            row.add(LevelTile.from(c));
        }
        return row.toArray(new LevelTile[row.size()]);
    }

    static List<Level> loadLevels() {
        List<Level> levels = new ArrayList<Level>();
        Scanner s = new Scanner(Level.class.getResourceAsStream("Level_Data"));
        s.useDelimiter("#").next();
        while (s.hasNext()) {
            levels.add(new Level(s.next(), levels.size() + 1));
        }
        return levels;
    }
}
