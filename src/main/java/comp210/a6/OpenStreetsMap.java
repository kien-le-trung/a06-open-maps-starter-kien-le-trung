package comp210.a6;

import bridges.base.Color;
import bridges.base.Element;
import bridges.base.GraphAdjList;
import bridges.data_src_dependent.OsmVertex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Map helpers and drawing code that Main calls: snapping a latitude and
 * longitude to a vertex, measuring a route, and coloring the pictures.
 * Nothing here needs changing.
 *
 * The graph is a BRIDGES GraphAdjList. Each vertex key is an int ID, each
 * vertex holds an OsmVertex (a latitude and longitude), and each edge holds
 * the length of a road segment in meters. Edges are directed, so a two-way
 * street is two edges and a one-way street is one.
 *
 * A graph this size goes to BRIDGES' large graph viewer, which draws vertex
 * colors but draws every edge as a thin blue line. The pictures show
 * routes by coloring their vertices instead.
 */
public class OpenStreetsMap {

    static final Color CAROLINA_BLUE = new Color(75, 156, 211);
    static final Color ORANGE = new Color(230, 126, 34);
    static final Color RED = new Color(200, 30, 45);
    static final Color PURPLE = new Color(128, 90, 180);
    static final Color NAVY = new Color(19, 41, 75);
    static final Color FADED = new Color(200, 200, 200, 0.4f);

    /**
     * Returns the vertex among candidates closest to (latitude, longitude).
     * Pass graph.getVertices().keySet() to search every vertex, or the
     * vertices BFS reached to snap only to places the source can get to.
     *
     * @throws IllegalArgumentException if candidates is empty
     */
    public static int getClosestVertex(GraphAdjList<Integer, OsmVertex, Double> graph, Set<Integer> candidates,
                                       double latitude, double longitude) {
        Integer closest = null;
        double minDistance = Double.MAX_VALUE;

        for (Integer key : candidates) {
            OsmVertex vertex = graph.getVertexData(key);
            double latitudeDifference = vertex.getLatitude() - latitude;
            double longitudeDifference = vertex.getLongitude() - longitude;
            // Comparing squared distances finds the same vertex without a square root.
            double squaredDistance = latitudeDifference * latitudeDifference
                    + longitudeDifference * longitudeDifference;
            if (squaredDistance < minDistance) {
                closest = key;
                minDistance = squaredDistance;
            }
        }

        if (closest == null) {
            throw new IllegalArgumentException("no candidate vertices to search");
        }
        return closest;
    }

    /** Returns the total length in meters of the edges along route. */
    public static double routeLength(GraphAdjList<Integer, ?, Double> graph, List<Integer> route) {
        double length = 0.0;
        for (int i = 0; i + 1 < route.size(); i++) {
            length += graph.getEdgeData(route.get(i), route.get(i + 1));
        }
        return length;
    }

    /** Colors every vertex in the faded gray. */
    public static void fadeAll(GraphAdjList<Integer, OsmVertex, Double> graph) {
        for (Element<OsmVertex> element : graph.getVertices().values()) {
            element.getVisualizer().setColor(FADED);
        }
    }

    /**
     * Shades every reachable vertex from white at the source to black at the
     * farthest one. Vertices with no route from the source stay faded.
     */
    public static void styleDistance(GraphAdjList<Integer, OsmVertex, Double> graph,
                                     HashMap<Integer, Double> distance) {
        fadeAll(graph);

        double maxDistance = 0.0;
        for (double vertexDistance : distance.values()) {
            if (vertexDistance < Double.MAX_VALUE && vertexDistance > maxDistance) {
                maxDistance = vertexDistance;
            }
        }

        for (Map.Entry<Integer, Element<OsmVertex>> entry : graph.getVertices().entrySet()) {
            double vertexDistance = distance.get(entry.getKey());
            if (vertexDistance == Double.MAX_VALUE) {
                continue;
            }
            // When only the source is reachable, maxDistance is 0, and the
            // source is shaded white.
            int shade = 255;
            if (maxDistance > 0) {
                shade = (int) (255 * (maxDistance - vertexDistance) / maxDistance);
            }
            entry.getValue().getVisualizer().setColor(new Color(shade, shade, shade));
        }
    }

    /** Fades the map, then draws one route in color. */
    public static void stylePath(GraphAdjList<Integer, OsmVertex, Double> graph, List<Integer> route, Color color) {
        fadeAll(graph);
        colorRoute(graph, route, color);
    }

    /**
     * Fades the map, then draws two routes: the BFS route in orange, the
     * Dijkstra route in red, and the intersections they share in purple.
     */
    public static void styleRoutes(GraphAdjList<Integer, OsmVertex, Double> graph,
                                   List<Integer> bfsRoute, List<Integer> dijkstraRoute) {
        fadeAll(graph);
        colorRoute(graph, bfsRoute, ORANGE);
        colorRoute(graph, dijkstraRoute, RED);

        Set<Integer> shared = new HashSet<>(bfsRoute);
        shared.retainAll(dijkstraRoute);
        colorRoute(graph, new ArrayList<>(shared), PURPLE);
    }

    /** Colors the vertices along route. */
    public static void colorRoute(GraphAdjList<Integer, OsmVertex, Double> graph, List<Integer> route, Color color) {
        for (Integer vertex : route) {
            graph.getVertex(vertex).getVisualizer().setColor(color);
        }
    }

    /** Marks a vertex, such as the source or a destination, in navy. */
    public static void styleRoot(GraphAdjList<Integer, OsmVertex, Double> graph, int root) {
        graph.getVertex(root).getVisualizer().setColor(NAVY);
    }
}
