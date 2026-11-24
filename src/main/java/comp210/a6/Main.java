package comp210.a6;

import bridges.base.GraphAdjList;
import bridges.connect.Bridges;
import bridges.data_src_dependent.OsmVertex;

import java.util.HashMap;
import java.util.List;

/**
 * Plans a route from Sitterson Hall to a destination you choose on Chapel
 * Hill's roads, and draws up to four pictures in BRIDGES:
 *
 * Part 0 saves the map to a file after the first run, so later runs skip
 * the slow BRIDGES map server. The pictures:
 *
 *   1. the BFS route, fewest intersections           (Part 1)
 *   2. every intersection shaded by Dijkstra distance (Part 2)
 *   3. the BFS and Dijkstra routes together           (Part 2)
 *   4. your own feature                               (Part 3)
 *
 * A picture whose part you have not written yet is skipped, so you can run
 * this as soon as Part 1 works.
 */
public class Main {

    // ------------------------------------------------------------------
    // YOUR CHOICE: where to go. Find a place on openstreetmap.org,
    // right-click it, choose "Show address", and copy the latitude and
    // longitude. Your write-up numbers come from this destination.
    // ------------------------------------------------------------------
    static final String DESTINATION_NAME = "Trader Joe's";
    static final double DESTINATION_LATITUDE = 35.9358;
    static final double DESTINATION_LONGITUDE = -79.0242;

    /** Sitterson Hall, home of UNC Computer Science, from OpenStreetMap. */
    static final double SITTERSON_LATITUDE = 35.9099702;
    static final double SITTERSON_LONGITUDE = -79.0532690;

    /** Chapel Hill's roads down to the "tertiary" level, about 4,800 intersections. */
    static final String PLACE = "Chapel Hill, North Carolina";
    static final String ROAD_LEVEL = "tertiary";

    public static void main(String[] args) throws Exception {
        BridgesConfig config = BridgesConfig.load();
        System.out.println("Loaded BRIDGES credentials: " + config.describe());

        Bridges bridges = new Bridges(config.getAssignmentNumber(), config.getUsername(), config.getApiKey());
        bridges.setTitle("Final Project: Sitterson Hall to " + DESTINATION_NAME);

        GraphAdjList<Integer, OsmVertex, Double> graph;
        try {
            graph = MapLoader.load(bridges, PLACE, ROAD_LEVEL);
        } catch (UnsupportedOperationException e) {
            System.out.println("Part 0 is not written yet, so the map comes from BRIDGES every run.");
            graph = bridges.getDataSource().getOsmData(PLACE, ROAD_LEVEL).getGraph();
        }
        bridges.setDataStructure(graph);

        int source = OpenStreetsMap.getClosestVertex(graph, graph.getVertices().keySet(),
                SITTERSON_LATITUDE, SITTERSON_LONGITUDE);
        System.out.println("Map: " + graph.getVertices().size() + " intersections. Sitterson Hall is vertex " + source + ".");

        // Part 1: fewest intersections
        HashMap<Integer, Integer> steps = new HashMap<>();
        HashMap<Integer, Integer> bfsParent = new HashMap<>();
        try {
            RoutePlanner.bfs(graph, source, steps, bfsParent);
        } catch (UnsupportedOperationException e) {
            System.out.println("Part 1 is not written yet, so there are no pictures. Start with bfs in RoutePlanner.");
            return;
        }

        // Snap to the nearest intersection Sitterson can reach, so the route
        // never ends on a dead-end scrap of road.
        int destination = OpenStreetsMap.getClosestVertex(graph, steps.keySet(),
                DESTINATION_LATITUDE, DESTINATION_LONGITUDE);
        List<Integer> bfsRoute = RoutePlanner.routeTo(bfsParent, source, destination);
        double bfsMeters = OpenStreetsMap.routeLength(graph, bfsRoute);
        System.out.printf("BFS route:      %d steps, %.0f m (%d intersections reachable)%n",
                bfsRoute.size() - 1, bfsMeters, steps.size());

        OpenStreetsMap.stylePath(graph, bfsRoute, OpenStreetsMap.ORANGE);
        OpenStreetsMap.styleRoot(graph, source);
        OpenStreetsMap.styleRoot(graph, destination);
        bridges.setDescription(String.format("Picture 1 (Part 1): the BFS route (orange) from Sitterson Hall to %s,"
                + " %d steps and %.0f m.", DESTINATION_NAME, bfsRoute.size() - 1, bfsMeters));
        bridges.visualize();

        // Part 2: fewest meters
        HashMap<Integer, Double> distance = new HashMap<>();
        HashMap<Integer, Integer> parent = new HashMap<>();
        try {
            RoutePlanner.dijkstra(graph, source, distance, parent);
        } catch (UnsupportedOperationException e) {
            System.out.println("Part 2 is not written yet, so pictures 2 and 3 are skipped.");
            distance = null;
        }

        if (distance != null) {
            List<Integer> dijkstraRoute = RoutePlanner.routeTo(parent, source, destination);
            double dijkstraMeters = OpenStreetsMap.routeLength(graph, dijkstraRoute);
            System.out.printf("Dijkstra route: %d steps, %.0f m%n", dijkstraRoute.size() - 1, dijkstraMeters);

            OpenStreetsMap.styleDistance(graph, distance);
            OpenStreetsMap.styleRoot(graph, source);
            bridges.setDescription("Picture 2 (Part 2): every intersection shaded by its distance from Sitterson Hall"
                    + " (navy), white (near) to black (far). Gray dots have no route from Sitterson.");
            bridges.visualize();

            OpenStreetsMap.styleRoutes(graph, bfsRoute, dijkstraRoute);
            OpenStreetsMap.styleRoot(graph, source);
            OpenStreetsMap.styleRoot(graph, destination);
            bridges.setDescription(String.format("Picture 3 (Part 2): to %s, the BFS route (orange) is %d steps and"
                    + " %.0f m; the Dijkstra route (red) is %d steps and %.0f m. Purple is shared.",
                    DESTINATION_NAME, bfsRoute.size() - 1, bfsMeters, dijkstraRoute.size() - 1, dijkstraMeters));
            bridges.visualize();
        }

        // Part 3: your feature
        try {
            String description = MyFeature.draw(graph, source, destination);
            bridges.setDescription(description);
            bridges.visualize();
        } catch (UnsupportedOperationException e) {
            System.out.println("Part 3 is not written yet, so picture 4 is skipped.");
        }
    }
}
