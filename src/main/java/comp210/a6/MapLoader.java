package comp210.a6;

import bridges.base.GraphAdjList;
import bridges.connect.Bridges;
import bridges.data_src_dependent.OsmVertex;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import bridges.base.Edge;
import bridges.base.SLelement;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.util.ArrayList;

/**
 * Part 0: load the map from the BRIDGES server the first time, save it to a
 * file in the data folder, and read it from that file every time after.
 *
 * The BRIDGES map server is slow (about a minute for Chapel Hill) and every
 * student in the course shares it, so each of you fetches the map once.
 * Delete the file in data/ to fetch it again.
 *
 * The file is plain text, in this format:
 *
 *     place Chapel Hill, North Carolina
 *     level tertiary
 *     vertices 4806
 *     0 35.858983 -79.101819 977.29618 -5075.89499    id, latitude, longitude, map x, map y
 *     ...                                             one line per vertex
 *     edges 7068
 *     5 37 128.10505963057693                         from, to, length in meters
 *     ...                                             one line per edge
 *
 * Fields are separated by one space. The README walks through each method.
 */
public final class MapLoader {

    private MapLoader() {
    }

    /**
     * Returns the road graph for place at the given road level. If the file
     * from cacheFile exists and is not empty, read the graph from it. If
     * reading fails, or there is no file, fetch the graph from BRIDGES with
     *
     *     bridges.getDataSource().getOsmData(place, level).getGraph()
     *
     * then write it to the file and return it. Print one line saying where
     * the map came from: the file, or BRIDGES (and that you saved it).
     */
    public static GraphAdjList<Integer, OsmVertex, Double> load(Bridges bridges, String place, String level)
            throws IOException {
        throw new UnsupportedOperationException("Part 0: write MapLoader.load");
    }

    /**
     * The file for one place and level, in the data folder next to pom.xml.
     * "Chapel Hill, North Carolina" at "tertiary" becomes
     * data/chapel-hill-north-carolina-tertiary.txt. This one is written for
     * you.
     */
    static Path cacheFile(String place, String level) {
        // Same base folder as BridgesConfig uses for .env.
        String base = System.getProperty("dotenv.dir", "./");
        String name = (place + " " + level).toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
        return Paths.get(base, "data", name + ".txt");
    }

    /**
     * Writes graph to file in the format above. Create the data folder first
     * if it does not exist. Each vertex line holds the vertex's ID, the
     * latitude and longitude from its OsmVertex, and its map location from
     * its visualizer. Each edge line holds one edge from an adjacency list.
     */
    static void write(GraphAdjList<Integer, OsmVertex, Double> graph, String place, String level, Path file)
            throws IOException {
        Files.createDirectories(file.getParent());

        int edgeCount = 0;
        for (int from : graph.getVertices().keySet()) {
            SLelement<Edge<Integer, Double>> node =
                    graph.getAdjacencyList(from);

            while (node != null) {
                edgeCount++;
                node = node.getNext();
            }
        }

        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
            writer.write("place " + place);
            writer.newLine();
            writer.write("level " + level);
            writer.newLine();
            writer.write("vertices " + graph.getVertices().size());
            writer.newLine();

            for (int id : graph.getVertices().keySet()) {
                OsmVertex vertex = graph.getVertexData(id);
                double x = graph.getVertex(id).getVisualizer().getLocationX();
                double y = graph.getVertex(id).getVisualizer().getLocationY();

                writer.write(id + " "
                        + vertex.getLatitude() + " "
                        + vertex.getLongitude() + " "
                        + x + " " + y);
                writer.newLine();
            }

            writer.write("edges " + edgeCount);
            writer.newLine();

            for (int from : graph.getVertices().keySet()) {
                ArrayList<Edge<Integer, Double>> edges = new ArrayList<>();
                SLelement<Edge<Integer, Double>> node =
                        graph.getAdjacencyList(from);

                while (node != null) {
                    edges.add(node.getValue());
                    node = node.getNext();
                }

                for (int i = edges.size() - 1; i >= 0; i--) {
                    Edge<Integer, Double> edge = edges.get(i);
                    writer.write(from + " "
                            + edge.getTo() + " "
                            + edge.getEdgeData());
                    writer.newLine();
                }
            }
        }
        //throw new UnsupportedOperationException("Part 0: write MapLoader.write");
    }

    /**
     * Reads a file in the format above and rebuilds the graph: each vertex
     * holds a new OsmVertex(latitude, longitude) and sits at its map
     * location, and each edge holds its length.
     *
     * @throws IOException if the file ends before the vertex or edge count
     *         says it should, a vertex or edge line has the wrong number of
     *         fields, a count line does not start with "vertices" or "edges",
     *         or a number does not parse
     */
    static GraphAdjList<Integer, OsmVertex, Double> read(Path file) throws IOException {
        GraphAdjList<Integer, OsmVertex, Double> graph = new GraphAdjList<>();

        try (BufferedReader reader = Files.newBufferedReader(file)) {
            // Consume the place and level headers without interpreting them.
            if (reader.readLine() == null || reader.readLine() == null) {
                throw new IOException("Missing place or level header");
            }

            String line = reader.readLine();
            if (line == null) {
                throw new IOException("Missing vertices count");
            }

            String[] fields = line.split(" ");
            if (fields.length != 2 || !fields[0].equals("vertices")) {
                throw new IOException("Invalid vertices count line");
            }

            int vertexCount = Integer.parseInt(fields[1]);
            if (vertexCount < 0) {
                throw new IOException("Negative vertex count");
            }

            for (int i = 0; i < vertexCount; i++) {
                line = reader.readLine();
                if (line == null) {
                    throw new IOException("File ended before vertex record " + i);
                }

                fields = line.split(" ");
                if (fields.length != 5) {
                    throw new IOException("Vertex record " + i + " needs 5 fields");
                }

                int id = Integer.parseInt(fields[0]);
                double latitude = Double.parseDouble(fields[1]);
                double longitude = Double.parseDouble(fields[2]);
                double x = Double.parseDouble(fields[3]);
                double y = Double.parseDouble(fields[4]);

                graph.addVertex(id, new OsmVertex(latitude, longitude));
                graph.getVertex(id).setLocation(x, y);
            }

            line = reader.readLine();
            if (line == null) {
                throw new IOException("Missing edges count");
            }

            fields = line.split(" ");
            if (fields.length != 2 || !fields[0].equals("edges")) {
                throw new IOException("Invalid edges count line");
            }

            int edgeCount = Integer.parseInt(fields[1]);
            if (edgeCount < 0) {
                throw new IOException("Negative edge count");
            }

            for (int i = 0; i < edgeCount; i++) {
                line = reader.readLine();
                if (line == null) {
                    throw new IOException("File ended before edge record " + i);
                }

                fields = line.split(" ");
                if (fields.length != 3) {
                    throw new IOException("Edge record " + i + " needs 3 fields");
                }

                int from = Integer.parseInt(fields[0]);
                int to = Integer.parseInt(fields[1]);
                double length = Double.parseDouble(fields[2]);

                graph.addEdge(from, to, length);
            }

            return graph;
        } catch (NumberFormatException e) {
            throw new IOException("Invalid number in map file", e);
        }
        //throw new UnsupportedOperationException("Part 0: write MapLoader.read");
    }
}
