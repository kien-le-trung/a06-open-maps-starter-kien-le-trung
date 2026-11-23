package comp210.a6;

import bridges.base.GraphAdjList;

import java.util.HashMap;
import java.util.List;

/**
 * Route finding on a road graph. Vertex keys are ints, and each edge holds
 * the length of a road segment in meters. The methods work on any graph with
 * those key and edge types, whatever the vertices hold, so you can test them
 * on small graphs you build by hand.
 *
 * Part 1: bfs and routeTo.
 * Part 2: dijkstra, with its two invariant checks, and negativeEdgeGraph.
 *
 * Walk a vertex's edges with graph.getAdjacencyList(vertex), the same
 * getValue() / getNext() loop as a linked list.
 */
public class RoutePlanner {

    // ------------------------------------------------------------------
    // Part 1: fewest intersections
    // ------------------------------------------------------------------

    /**
     * Breadth-first search from source. Fills steps with the number of edges
     * on the shortest route (by edge count) to each vertex BFS reaches, and
     * parent with the vertex just before each one on that route. A vertex BFS
     * never reaches gets no entry in steps. The source has no parent.
     */
    public static void bfs(GraphAdjList<Integer, ?, Double> graph, int source,
                           HashMap<Integer, Integer> steps, HashMap<Integer, Integer> parent) {
        throw new UnsupportedOperationException("Part 1: write RoutePlanner.bfs");
    }

    /**
     * Returns the route from source to destination as a list of vertices,
     * source first and destination last, by following parent links back from
     * destination. Returns an empty list when destination was not reached.
     */
    public static List<Integer> routeTo(HashMap<Integer, Integer> parent, int source, int destination) {
        throw new UnsupportedOperationException("Part 1: write RoutePlanner.routeTo");
    }

    // ------------------------------------------------------------------
    // Part 2: fewest meters
    // ------------------------------------------------------------------

    /**
     * Dijkstra's shortest path algorithm. Fills distance with the length of
     * the shortest route from source to each vertex, or Double.MAX_VALUE if
     * there is none, and parent with the vertex just before each one on its
     * route. The source has no parent.
     *
     * The loop keeps the settled-set invariant: once a vertex is settled, its
     * distance is final. Two checks enforce it as the loop runs:
     *
     *   Check 1. Vertices are settled in order of distance, so each settled
     *            distance is at least the one settled before it.
     *   Check 2. Relaxing an edge never lowers the distance of a vertex that
     *            is already settled.
     *
     * @throws IllegalStateException when either check fails, with a message
     *         that starts with "Check 1" or "Check 2" and names the vertex
     */
    public static void dijkstra(GraphAdjList<Integer, ?, Double> graph, int source,
                                HashMap<Integer, Double> distance, HashMap<Integer, Integer> parent) {
        throw new UnsupportedOperationException("Part 2: write RoutePlanner.dijkstra");
    }

    /**
     * Returns a small graph with one negative edge on which dijkstra fails
     * check 2. Running dijkstra from vertex 0 must settle some vertex and then
     * find a shorter route to it through the negative edge.
     */
    public static GraphAdjList<Integer, String, Double> negativeEdgeGraph() {
        throw new UnsupportedOperationException("Part 2: write RoutePlanner.negativeEdgeGraph");
    }
}
