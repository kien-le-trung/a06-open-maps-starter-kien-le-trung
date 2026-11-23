package comp210.a6;

import bridges.base.GraphAdjList;
import bridges.connect.Bridges;
import bridges.data_src_dependent.OsmVertex;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        throw new UnsupportedOperationException("Part 0: write MapLoader.write");
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
        throw new UnsupportedOperationException("Part 0: write MapLoader.read");
    }
}
