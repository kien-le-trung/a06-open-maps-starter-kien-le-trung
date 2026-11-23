package comp210.a6;

/**
 * A vertex and a distance, the entries in Dijkstra's priority queue.
 *
 * Pairs are Comparable: their natural order is by distance, smallest first.
 * D has to be Comparable itself (Double is), so compareTo can hand the work
 * to the distances' own compareTo.
 */
class Pair<V, D extends Comparable<D>> implements Comparable<Pair<V, D>> {

    private final V vertex;
    private final D distance;

    Pair(V vertex, D distance) {
        this.vertex = vertex;
        this.distance = distance;
    }

    V getKey() {
        return vertex;
    }

    D getValue() {
        return distance;
    }

    /**
     * Orders pairs by distance: negative when this pair's distance is
     * smaller than other's, positive when it is larger, and 0 when they are
     * equal. The vertex plays no part.
     */
    @Override
    public int compareTo(Pair<V, D> other) {
        throw new UnsupportedOperationException("Part 2: write Pair.compareTo");
    }
}
