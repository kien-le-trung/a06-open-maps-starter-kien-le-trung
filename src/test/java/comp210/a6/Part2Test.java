package comp210.a6;

import bridges.base.GraphAdjList;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Your tests for Part 2. Write at least three, then right-click this file in
 * IntelliJ and choose Run 'Part2Test'. The README's "Writing your tests"
 * section has ideas.
 */
class Part2Test {

    /** Vertices 0 to n - 1 with the given edges, each {from, to, length}. */
    static GraphAdjList<Integer, String, Double> graph(int n, double[][] edges) {
        GraphAdjList<Integer, String, Double> graph = new GraphAdjList<>();
        for (int i = 0; i < n; i++) {
            graph.addVertex(i, "v" + i);
        }
        for (double[] e : edges) {
            graph.addEdge((int) e[0], (int) e[1], e[2]);
        }
        return graph;
    }

    // Write your tests here. Each one is a method marked @Test, for example:
    //
    // @Test
    // void dijkstraGivesTheSourceZero() {
    //     GraphAdjList<Integer, String, Double> g = graph(2, new double[][] {{0, 1, 5}});
    //     HashMap<Integer, Double> distance = new HashMap<>();
    //     RoutePlanner.dijkstra(g, 0, distance, new HashMap<>());
    //     assertEquals(0.0, distance.get(0));
    // }
}
