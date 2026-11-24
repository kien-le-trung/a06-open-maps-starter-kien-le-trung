package comp210.a6;

import bridges.base.GraphAdjList;
import bridges.data_src_dependent.OsmVertex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Part 3: your own feature. See the README for what it needs to do and some
 * ideas to start from. The nearest dining hall is the README's worked
 * example, so pick a different question.
 *
 * draw colors the vertices of graph to show your feature, and returns the
 * description for its BRIDGES picture. Main posts it as picture 4. source is
 * Sitterson Hall's vertex and destination is your destination's vertex, the
 * same two Parts 1 and 2 route between. You can add methods, fields, and
 * nested classes here.
 */
public class MyFeature {
    private double SPEED = 1.35; // m/s
    private List<CoffeeShop> coffeeShops = new ArrayList<>();
    private RoutePlanner routePlanner =  new RoutePlanner();

    private static class CoffeeShop {
        private final double lat;
        private final double lon;
        private String name;
        public CoffeeShop(String name, double lat, double lon) {
            this.lat = lat;
            this.lon = lon;
            this.name = name;
        }
        public int retrieveVertex(GraphAdjList<Integer, OsmVertex, Double> graph, Set<Integer> candidates, double lat, double lon) {
            return OpenStreetsMap.getClosestVertex(graph, candidates, lat, lon);
        }
    }

    private List<Integer> shortestDistance(GraphAdjList<Integer, OsmVertex, Double> graph, int source, int destination) {
        HashMap<Integer, Double> distance = new HashMap<>();
        HashMap<Integer, Integer> parent = new HashMap<>();
        RoutePlanner.dijkstra(graph, source, distance, parent);
        List<Integer> shortestRoute = routePlanner.routeTo(parent, source, destination);
        return shortestRoute;
    }


    public static String draw(GraphAdjList<Integer, OsmVertex, Double> graph, int source, int destination) {
        // coffee shops + populate vertices list
        MyFeature myFeature = new MyFeature();
        // temporarily turn off to demonstrate feature
        // myFeature.coffeeShops.add(new CoffeeShop("Starbucks", 35.9130553, -79.0554276));
        // myFeature.coffeeShops.add(new CoffeeShop("1922", 35.913622, -79.05438));
        myFeature.coffeeShops.add(new CoffeeShop("Meantime", 35.911435, -79.051216));

        // compute distance without coffee
        List<Integer> noCoffeeRoute = myFeature.shortestDistance(graph, source, destination);

        // compute distance for all coffee + update the shortest
        double minDist = Double.MAX_VALUE;
        CoffeeShop closestCoffee = myFeature.coffeeShops.get(0);
        List<Integer> closestCoffeeRoute = new ArrayList<>();
        for (CoffeeShop shop : myFeature.coffeeShops) {
            int coffee = shop.retrieveVertex(graph, graph.getVertices().keySet(), shop.lat, shop.lon);
            List<Integer> toCoffee = myFeature.shortestDistance(graph, source, coffee);
            List<Integer> toDestination = myFeature.shortestDistance(graph, coffee, destination);
            double routeLength = OpenStreetsMap.routeLength(graph, toCoffee) + OpenStreetsMap.routeLength(graph, toDestination);
            if (routeLength < minDist) {
                minDist = routeLength;
                closestCoffee = shop;
                closestCoffeeRoute = new ArrayList<>(toCoffee);
                closestCoffeeRoute.addAll(toDestination.subList(1,toDestination.size()));
            }
        }

        // drawing
        OpenStreetsMap.fadeAll(graph);
        OpenStreetsMap.colorRoute(
                graph, noCoffeeRoute, OpenStreetsMap.ORANGE
        );
        OpenStreetsMap.colorRoute(
                graph, closestCoffeeRoute, OpenStreetsMap.CAROLINA_BLUE
        );

        int selectedCoffeeVertex = closestCoffee.retrieveVertex(
                graph,
                graph.getVertices().keySet(),
                closestCoffee.lat,
                closestCoffee.lon
        );
        graph.getVertex(selectedCoffeeVertex)
                .getVisualizer()
                .setColor(OpenStreetsMap.RED);

        // reporting
        double estimatedExtraTime = (OpenStreetsMap.routeLength(graph, closestCoffeeRoute) - OpenStreetsMap.routeLength(graph, noCoffeeRoute))/myFeature.SPEED;
        String retMessage = "The closest coffee stop is " + closestCoffee.name + ". " + "Estimated extra time: " +  estimatedExtraTime/60 + "minutes.";
        return retMessage;
        // throw new UnsupportedOperationException("Part 3: write MyFeature.draw");
    }
}
