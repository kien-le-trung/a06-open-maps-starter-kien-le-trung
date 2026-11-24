package comp210.a6;

import bridges.base.Edge;
import bridges.base.GraphAdjList;
import bridges.base.SLelement;
import bridges.data_src_dependent.OsmVertex;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Your tests for Part 0. Write at least three, then right-click this file in
 * IntelliJ and choose Run 'Part0Test'. The README's "Writing your tests"
 * section has ideas.
 *
 * Keep your tests off the network: write files into dir, a temporary folder
 * JUnit creates for each test and deletes afterward, and call write and read
 * directly instead of load.
 */
class Part0Test {

    @TempDir
    Path dir;

    /** Three intersections, with two-way streets from 0 to 1 and from 0 to 2. */
    static GraphAdjList<Integer, OsmVertex, Double> threeCorners() {
        GraphAdjList<Integer, OsmVertex, Double> graph = new GraphAdjList<>();
        graph.addVertex(0, new OsmVertex(35.9099702, -79.053269));
        graph.getVertex(0).setLocation(977.2961869168789, -5075.894994299386);
        graph.addVertex(1, new OsmVertex(35.91058, -79.0487785));
        graph.getVertex(1).setLocation(977.7104, -5075.3001);
        graph.addVertex(2, new OsmVertex(35.9059451, -79.0456232));
        graph.getVertex(2).setLocation(978.5, -5076.25);
        graph.addEdge(0, 1, 84.2);
        graph.addEdge(1, 0, 84.2);
        graph.addEdge(0, 2, 12.5);
        graph.addEdge(2, 0, 12.5);
        return graph;
    }

    /** Each vertex's adjacency list as "to/length" strings, in list order. */
    static List<String> listOf(GraphAdjList<Integer, OsmVertex, Double> graph, int vertex) {
        List<String> edges = new ArrayList<>();
        SLelement<Edge<Integer, Double>> list = graph.getAdjacencyList(vertex);
        while (list != null) {
            edges.add(list.getValue().getTo() + "/" + list.getValue().getEdgeData());
            list = list.getNext();
        }
        return edges;
    }

    // Write your tests here. Each one is a method marked @Test, for example:
    //
    // @Test
    // void writeStartsWithThePlace() throws IOException {
    //     Path file = dir.resolve("map.txt");
    //     MapLoader.write(threeCorners(), "Chapel Hill, North Carolina", "tertiary", file);
    //     assertEquals("place Chapel Hill, North Carolina", Files.readAllLines(file).get(0));
    // }
    @Test
    void writeTest() throws IOException {
        GraphAdjList<Integer, OsmVertex, Double> graph = new GraphAdjList<>();

        graph.addVertex(0, new OsmVertex(1.0, 2.0));
        graph.getVertex(0).setLocation(3.0, 4.0);

        graph.addVertex(1, new OsmVertex(5.0, 6.0));
        graph.getVertex(1).setLocation(7.0, 8.0);

        graph.addVertex(2, new OsmVertex(9.0, 10.0));
        graph.getVertex(2).setLocation(11.0, 12.0);

        graph.addEdge(0, 1, 2.0);
        graph.addEdge(0, 2, 3.0);

        Path file = dir.resolve("map.txt");
        MapLoader.write(graph, "test", "tertiary", file);

        List<String> expected = List.of(
                "place test",
                "level tertiary",
                "vertices 3",
                "0 1.0 2.0 3.0 4.0",
                "1 5.0 6.0 7.0 8.0",
                "2 9.0 10.0 11.0 12.0",
                "edges 2",
                "0 1 2.0",
                "0 2 3.0"
        );

        assertEquals(expected, Files.readAllLines(file));
    }

    @Test
    void readTest() throws IOException {
        Path file = dir.resolve("map.txt");
        Files.write(file, List.of(
                "place test",
                "level tertiary",
                "vertices 3",
                "0 1.0 2.0 3.0 4.0",
                "1 5.0 6.0 7.0 8.0",
                "2 9.0 10.0 11.0 12.0",
                "edges 2",
                "0 1 2.0",
                "0 2 3.0"
        ));

        GraphAdjList<Integer, OsmVertex, Double> graph = MapLoader.read(file);

        assertEquals(3, graph.getVertices().size());

        // Each row contains latitude, longitude, drawing x, and drawing y.
        double[][] expected = {
                {1.0, 2.0, 3.0, 4.0},
                {5.0, 6.0, 7.0, 8.0},
                {9.0, 10.0, 11.0, 12.0}
        };

        for (int id = 0; id < expected.length; id++) {
            assertNotNull(graph.getVertex(id));

            OsmVertex vertex = graph.getVertexData(id);
            assertEquals(expected[id][0], vertex.getLatitude(), 1e-9);
            assertEquals(expected[id][1], vertex.getLongitude(), 1e-9);
            assertEquals(expected[id][2],
                    graph.getVertex(id).getVisualizer().getLocationX(), 1e-9);
            assertEquals(expected[id][3],
                    graph.getVertex(id).getVisualizer().getLocationY(), 1e-9);
        }

        assertEquals(List.of("2/3.0", "1/2.0"), listOf(graph, 0));
        assertEquals(List.of(), listOf(graph, 1));
        assertEquals(List.of(), listOf(graph, 2));
    }

    @Test
    void readRejectsMissingVertexCount() throws IOException {
        Path file = dir.resolve("missing-vertex-count.txt");
        Files.write(file, List.of(
                "place test",
                "level tertiary",
                "vertices",
                "0 1.0 2.0 3.0 4.0",
                "1 5.0 6.0 7.0 8.0",
                "2 9.0 10.0 11.0 12.0",
                "edges 2",
                "0 1 2.0",
                "0 2 3.0"
        ));

        IOException exception = org.junit.jupiter.api.Assertions.assertThrows(
                IOException.class,
                () -> MapLoader.read(file)
        );

        assertEquals("Invalid vertices count line", exception.getMessage());
    }
}
