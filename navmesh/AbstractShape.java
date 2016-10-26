package scripts.advancedwalking.generator.navmesh;

import org.tribot.api.interfaces.Positionable;
import scripts.advancedwalking.game.teleports.Teleport;
import scripts.advancedwalking.generator.Generator;
import scripts.advancedwalking.generator.tiles.MeshTile;

import java.io.Serializable;
import java.util.*;

/**
 * Represents a fragment of the {@link GeneratedNavMesh}, this could be a polygon, rectangle or anything you can set your mind to :-)
 *
 * @author Laniax
 */
public abstract class AbstractShape implements Serializable {

    private List<MeshTile> boundaryTiles = new ArrayList<>();
    private Set<AbstractShape> adjacentShapes = new LinkedHashSet<>();
    private Set<Teleport> teleports = new HashSet<>();

//    private transient RSPolygon polygon;
    private transient List<MeshTile> shapeTiles = new ArrayList<>();

    public void setBoundaryTiles(List<MeshTile> boundaryTiles) {
        this.boundaryTiles = boundaryTiles;
    }

    public List<MeshTile> getBoundaryTiles() {
        return this.boundaryTiles;
    }

//    /**
//     * Gets the polygon.
//     * Make sure it has been calculated first.
//     * @return
//     */
//    public RSPolygon getPolygon() {
//        return polygon;
//    }

//    /**
//     * Sets the polygon.
//     */
//    public void setPolygon(RSPolygon polygon) {
//        this.polygon = polygon;
//    }

    /**
     * Returns how many tiles are in this shape.
     * @return
     */
    public int getTileCount() {
        return shapeTiles.size();
    }

    /**
     * Get all the {@link MeshTile}'s that are inside this shape.
     * @return
     */
    public List<MeshTile> getAllTiles() {
        return shapeTiles;
    }

    /**
     * Gets all the teleports inside this shape, this will usually be a agility shortcut or stair.
     * But if the player starts on this shape, it might also be a spell/item teleport.
     * @return
     */
    public Set<Teleport> getTeleports() {
        return teleports;
    }

    /**
     * Adds a tile to this shape.
     * @return
     */
    public boolean addTile(MeshTile tile) {
        return shapeTiles.add(tile);
    }

    /**
     * Removes a tile from this shape.
     * @return
     */
    public boolean removeTile(MeshTile tile) {
        return shapeTiles.remove(tile);
    }

    /**
     * Add another shape to indicate that this shape lies adjacent to it.
     * ie. the player is able to navigate between these two shapes.
     * @param shape
     * @return
     */
    public boolean addAdjacent(AbstractShape shape) {
        return adjacentShapes.add(shape);
    }

    /**
     * Returns if this shape is allowed to be added to the navmesh.
     * @return
     */
    public abstract boolean accept();

    public abstract int getSize();

    public abstract void grow(Generator generator, Set<AbstractShape> shapeList);

    public abstract boolean contains(Positionable tile);

    public abstract void calculatePolygon(Generator generator);

}
