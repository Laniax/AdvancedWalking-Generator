package scripts.advancedwalking.generator.tiles.collector.collectors;

import org.tribot.api2007.Game;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSTile;
import scripts.advancedwalking.generator.tiles.CollisionFlags;
import scripts.advancedwalking.generator.tiles.collector.TileCollector;
import scripts.advancedwalking.generator.tiles.MeshTile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Laniax
 */
public class RegionCollector implements TileCollector {

    private ArrayList<RSTile> scannedList = new ArrayList<>();

    private Set<MeshTile> result = new HashSet<>();

    /**
     * Removes all the tiles from the closed list so they can be scanned again.
     */
    public void clear() {
        scannedList.clear();
    }

    @Override
    public void collect() {
        final RSTile base = new RSTile(Game.getBaseX(), Game.getBaseY());
        final RSArea chunk = new RSArea(base, new RSTile(base.getX() + 102, base.getY() + 102));

        for (RSTile scanTile : chunk.getAllTiles()) {

            if (scannedList.contains(scanTile)) {
                // We have already scanned this tile before.
                continue;
            }

            MeshTile meshTile = new MeshTile(scanTile);
            if (!meshTile.isBlocked(CollisionFlags.BLOCKED)) {
                result.add(meshTile);
            }

            scannedList.add(scanTile);
        }
    }

    @Override
    public Set<MeshTile> getTiles() {

        return result;

    }
}
