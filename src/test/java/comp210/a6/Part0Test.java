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
}
