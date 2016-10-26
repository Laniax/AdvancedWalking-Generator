package scripts.advancedwalking.generator.navmesh;


import scripts.advancedwalking.generator.tiles.MeshTile;

/**
 * @author Laniax
 */
public interface ShapeFactory<T> {

    /**
     * Creates a new shape object.
     * @param tile - Since everything is based on a 'growing' shape. this is the tile to start growing from.
     * @return
     */
    AbstractShape newShape(MeshTile tile);
}
