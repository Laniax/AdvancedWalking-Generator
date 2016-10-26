package scripts.advancedwalking.generator.tiles.collector;

import scripts.advancedwalking.generator.tiles.MeshTile;

import java.util.Set;

/**
 * @author Laniax
 */
public interface TileCollector {

    void collect();

    Set<MeshTile> getTiles();
}
