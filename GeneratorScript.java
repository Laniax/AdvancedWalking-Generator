package scripts.advancedwalking.generator;

import org.tribot.api.General;
import org.tribot.api2007.Login;
import org.tribot.api2007.Player;
import org.tribot.api2007.Projection;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.MouseActions;
import org.tribot.script.interfaces.Painting;
import scripts.advancedwalking.core.io.IOExtensions;
import scripts.advancedwalking.core.logging.LogProxy;
import scripts.advancedwalking.game.path.Path;
import scripts.advancedwalking.game.path.PathStep;
import scripts.advancedwalking.generator.navmesh.AbstractShape;
import scripts.advancedwalking.generator.navmesh.factories.PolytopeFactory;
import scripts.advancedwalking.generator.navmesh.GeneratedNavMesh;
import scripts.advancedwalking.generator.tiles.collector.collectors.RegionCollector;
import scripts.advancedwalking.generator.tiles.collector.TileCollector;
import scripts.advancedwalking.generator.tiles.MeshTile;
import scripts.advancedwalking.network.CommonFiles;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

@ScriptManifest(authors = {"Laniax"}, category = "AdvancedWalking", name = "GeneratorScript")
public class GeneratorScript extends Script implements Painting, MouseActions {

    LogProxy log = new LogProxy("GeneratorScript");

    private boolean scanningTiles = true;

    TileCollector collector;

    GeneratedNavMesh mesh = null;
    Path path = null;

    HashMap<AbstractShape, Color> shapes = new HashMap<>();


    public void run() {

        while (Login.getLoginState() != Login.STATE.INGAME)
            General.sleep(500);

        // Use a different tile collector in order to gather tiles in your own way
        // This opens up the possibility to read from the RS cache etc and all you have to do is replace this collector.
        collector = new RegionCollector();

        while (scanningTiles) {

            collector.collect();
        }

        log.info("Collected %d tiles!", collector.getTiles().size());

        // Create a generator, defining the shape we want to generate the navmesh with
        // currently only the polytope (polygons) is written, but it could also become a rectangular/circled/rainbow mesh if you want :-)
        Generator generator = new Generator(new PolytopeFactory(), collector.getTiles());

        log.info("Initialized a polytope mesh generator.");

        log.info(CommonFiles.localMeshFile.getAbsolutePath());

        // Let the generator do its work and get a fully able NavMesh object in return!
        mesh = generator.run();

        // You can do whatever with the mesh object, use it directly to pathfind on, or serialize it to a file and use it later..
        log.info("Mesh contains %d shapes!", mesh.getShapeCount());
        if (IOExtensions.serialize(mesh, CommonFiles.localMeshFile)) {
            log.info("Mesh was successfully serialized! Path to file: %s", CommonFiles.localMeshFile.toPath());
        } else {
            log.error("Failed to serialize the mesh!");
        }

        // Give a color to every shape
        for (AbstractShape shape : mesh.getAllShapes()) {
            shapes.put(shape, new Color((int)(Math.random() * 0x1000000)));
        }

      //  path = mesh.findPath(Player.getPosition(), destination);

//        log.info("meshtile X:%d Y:%d is %d tiles away from  X:%d Y:%d", 2961, 3348, new MeshTile(new RSTile(2965, 3352, 0)).distanceTo(new RSTile(2961, 3348)), 2965, 3352);

        while(true) {
            // prevent script from stopping
        }
    }

    Color blackTransparent = new Color(0, 0, 0, 100);

    Rectangle stopButton = new Rectangle(380, 300, 130, 30);

    @Override
    public void onPaint(Graphics g) {

        if (scanningTiles && collector != null) {
            g.setColor(blackTransparent);
            g.fillRect(stopButton.x, stopButton.y, stopButton.width, stopButton.height);
            g.setColor(Color.BLACK);
            g.drawRect(stopButton.x, stopButton.y, stopButton.width, stopButton.height);
            g.setColor(Color.WHITE);
            g.drawString("Generate", (stopButton.width / 3) + stopButton.x, (stopButton.height / 2) + stopButton.y + 5);
            g.drawString("Number of tiles: " + collector.getTiles().size(), stopButton.x, stopButton.y - 10);
        }

//
//        MeshTile playerPos = new MeshTile(Player.getPosition());
//
//        g.setColor(Color.cyan);
//        g.drawString("Blocked N: "+playerPos.isBlocked(Direction.NORTH), 10,20);
//        g.drawString("Blocked E: "+playerPos.isBlocked(Direction.EAST), 10,35);
//        g.drawString("Blocked S: "+playerPos.isBlocked(Direction.SOUTH), 10,50);
//        g.drawString("Blocked W: "+playerPos.isBlocked(Direction.WEST), 10,65);

        if (path != null) {

            for (PathStep step : path.getAll()) {

                RSTile t = step.getDestination();

                Point tilePoint = Projection.tileToScreen(t, 0);

                if (Projection.isInViewport(tilePoint)) {

                    Polygon drawPoly = Projection.getTileBoundsPoly(t, 0);
                    g.fillPolygon(drawPoly);
                }
            }
        }

        if (mesh != null) {

            for (Map.Entry<AbstractShape, Color> set : shapes.entrySet()) {

                AbstractShape shape = set.getKey();

//                if (!shape.contains(Player.getPosition()))
//                    continue;

                if (shape.contains(Player.getPosition())) {
                    for (MeshTile t : shape.getAllTiles()) {
                        Point tilePoint = Projection.tileToScreen(t, 0);

                        if (Projection.isInViewport(tilePoint)) {

//                            g.setColor(Color.yellow);
//                            g.drawString(t.getCollisionData() + "", tilePoint.x, tilePoint.y);
                            g.setColor(blackTransparent);
                            Polygon drawPoly = Projection.getTileBoundsPoly(t, 0);
                            g.fillPolygon(drawPoly);
                        }
                    }
                }


                g.setColor(set.getValue());

                Point previous = null, first = null;
                Point previousMinimap = null, firstMinimap = null;

                int i = 0;
                for (MeshTile t : shape.getBoundaryTiles()) {

                    Point tilePoint = Projection.tileToScreen(t,0);

                    if (Projection.isInViewport(tilePoint)) {
                        Polygon drawPoly = Projection.getTileBoundsPoly(t, 0);
                        g.fillPolygon(drawPoly);
                        g.setColor(Color.WHITE);
                        g.drawString(i+"", tilePoint.x, tilePoint.y);
                    }
                    g.setColor(set.getValue());

                    Point minimap = Projection.tileToMinimap(t);
                    if (minimap != null && Projection.isInMinimap(minimap)) {
                        g.fillOval(minimap.x, minimap.y, 3,3);
                    }
                    i++;

                    if (previous != null && previousMinimap != null) {
                        if (Projection.isInViewport(tilePoint)&& Projection.isInViewport(previous))
                            g.drawLine(previous.x, previous.y, tilePoint.x, tilePoint.y);

                        if (Projection.isInMinimap(minimap)&& Projection.isInMinimap(previousMinimap))
                            g.drawLine(previousMinimap.x, previousMinimap.y, minimap.x, minimap.y);
                    }
                    if (first == null) {
                        first = tilePoint;
                        firstMinimap = minimap;
                    }
//
                    previous = tilePoint;
                    previousMinimap = minimap;
                }
//
                if (Projection.isInViewport(previous) && Projection.isInViewport(first))
                    g.drawLine(previous.x, previous.y, first.x, first.y);

                if ( previousMinimap != null && firstMinimap != null && Projection.isInMinimap(previousMinimap) && Projection.isInMinimap(firstMinimap))
                    g.drawLine(previousMinimap.x, previousMinimap.y, firstMinimap.x, firstMinimap.y);

//                break;
            }
        }
    }

    @Override
    public void mouseClicked(Point point, int button, boolean isBot) {

        if (stopButton.contains(point.x, point.y)) {
            scanningTiles = false;
        }

    }

    public void mouseMoved(Point point, boolean b) {
    }

    public void mouseDragged(Point point, int i, boolean b) {
    }

    public void mouseReleased(Point point, int i, boolean b) {
    }
}
