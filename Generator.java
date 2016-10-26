package scripts.advancedwalking.generator;

import org.tribot.api2007.types.RSTile;
import scripts.advancedwalking.core.logging.LogProxy;
import scripts.advancedwalking.generator.navmesh.AbstractShape;
import scripts.advancedwalking.generator.navmesh.ShapeFactory;
import scripts.advancedwalking.generator.navmesh.GeneratedNavMesh;
import scripts.advancedwalking.generator.tiles.Direction;
import scripts.advancedwalking.generator.tiles.MeshTile;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Laniax
 */
public class Generator {

    static LogProxy log = new LogProxy("Generator");

    public Set<MeshTile> validTiles;

    ShapeFactory factory;

    public Generator(ShapeFactory factory, Set<MeshTile> validTiles) {
        this.validTiles = validTiles;
        this.factory = factory;
    }

    /**
     * Builds the scanned tiles into a {@link GeneratedNavMesh} object.
     *
     * @return
     */
    public GeneratedNavMesh run() {

        log.info("Generating shapes..");
        long startTime = System.currentTimeMillis();
        long totalTime = System.currentTimeMillis();

        Set<AbstractShape> shapes = createShapes();

        log.info("Generated %d shapes in %dms.", shapes.size(), System.currentTimeMillis() - startTime);
        log.info("Calculating shape relations..");
        startTime = System.currentTimeMillis();

        calculateAdjacent(shapes);

        log.info("Calculated relations in %dms.", System.currentTimeMillis() - startTime);
        log.info("Building NavMesh object..");
        startTime = System.currentTimeMillis();

        GeneratedNavMesh mesh = new GeneratedNavMesh(shapes);

        log.info("Build NavMesh object in %dms. (total time: %dms)", System.currentTimeMillis() - startTime, System.currentTimeMillis() - totalTime);

        return mesh;
    }

    /**
     * Calculates which shapes are adjacent to eachother.
     *
     * @param shapes
     */
    private void calculateAdjacent(Set<AbstractShape> shapes) {

        //TODO: abstract this?

        for (AbstractShape shape : shapes) {

            for (MeshTile tile : shape.getAllTiles()) {

                for (Direction direction : Direction.getAllCardinal()) {

                    if (tile.isBlocked(direction))
                        continue;

                    final RSTile checkTile = tile.getAdjacentTile(direction);
                    final MeshTile chckTile = findTile(checkTile);

                    if (chckTile == null)
                        continue;

                    AbstractShape checkShape;
                    if ((checkShape = isInShape(shapes, chckTile)) != null && !checkShape.equals(shape)) {
                        shape.addAdjacent(checkShape);
                    }
                }
            }
        }
    }

    /**
     * Checks if the given tile is in any of the shapes of the given list.
     *
     * @param shapeList
     * @param tile
     * @return the shape it was in, or null if not found.
     */
    public static AbstractShape isInShape(Set<AbstractShape> shapeList, MeshTile tile) {
        for (AbstractShape shape : shapeList) {
            if (shape.contains(tile))
                return shape;
        }

        return null;
    }

    /**
     * Returns the {@link MeshTile} associated with the {@link RSTile}.
     *
     * @param tile
     * @return
     */
    public MeshTile findTile(RSTile tile) {
        for (MeshTile t : validTiles) {
            if (t.equals(tile))
                return t;
        }
        return null;
    }

    private Set<AbstractShape> createShapes() {

        Set<AbstractShape> shapeList = new HashSet<>();

        Iterator<MeshTile> iter = validTiles.iterator();

        while (iter.hasNext()) {

            MeshTile tile = iter.next();

            // Check if this tile is already in another shape
            if (isInShape(shapeList, tile) != null)
                continue;

            AbstractShape shape = factory.newShape(tile);

            shape.grow(this, shapeList);

            if (shape.accept()) {
                shapeList.add(shape);
            } else {
                iter.remove();
            }
        }

        log.info("Created %d shapes.", shapeList.size());
        log.info("Calculating polytopes..", shapeList.size());

        for (AbstractShape shape : shapeList) {
            shape.calculatePolygon(this);
        }

        return shapeList;
    }

}
