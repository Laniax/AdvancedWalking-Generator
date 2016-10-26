package scripts.advancedwalking.generator.navmesh.factories;

import scripts.advancedwalking.generator.navmesh.AbstractShape;
import scripts.advancedwalking.generator.navmesh.ShapeFactory;
import scripts.advancedwalking.generator.navmesh.shapes.Polytope;
import scripts.advancedwalking.generator.tiles.MeshTile;

/**
 * @author Laniax
 */
public class PolytopeFactory implements ShapeFactory {

    @Override
    public AbstractShape newShape(MeshTile tile) {
        return new Polytope(tile);
    }
}
