# Final Project Write-up

**Name:**
**Onyen:**
**BRIDGES URL:** https://bridges-cs.herokuapp.com/assignments/6/
**Destination (DESTINATION_NAME in Main.java):**

Four questions, 20 points, plus 5 points for a BRIDGES URL that shows all four pictures. A paragraph or two each. Use the numbers your program printed
for your destination, and where a question asks for a number, show where it
came from.

---

## Question 1: Fewest intersections or fewest meters (5 points)

Paste the two lines your program printed for the BFS route and the Dijkstra
route.

Picture 3 shows both routes: BFS in orange, Dijkstra in red, and shared
intersections in purple. Where do the two routes split, and where do they meet
again? Describe it in terms of the map. A road name from openstreetmap.org
helps.

Explain why BFS chose its route and why Dijkstra chose its own. Each route is
the best one by some measure. Name the measure for each.

If your two routes are the same, explain how that can happen. Then pick a
second destination where they differ, and report its numbers too.

```

```

---

## Question 2: Right answers, wrong algorithm (5 points)

This is the shortest path code from the original BRIDGES version of this
assignment, with its ordering written as a `compareTo` on `Pair`. Run it on
Chapel Hill and every distance matches yours.

```java
class Pair<K, V extends Comparable<V>> implements Comparable<Pair<K, V>> {
  // the key, the value, getKey(), and getValue(), the same as your Pair

  public int compareTo(Pair<K, V> other) {
    if (getValue().compareTo(other.getValue()) < 0)
      return 1;
    else if (getValue().compareTo(other.getValue()) > 0)
      return -1;
    return 0;
  }
}

  public static void dijkstra (GraphAdjList<Integer, OsmVertex, Double> gr,
                               int source,
                               HashMap<Integer, Double> distance,
                               HashMap<Integer, Integer> parent) {
    for (Map.Entry<Integer, Element<OsmVertex> > pai : gr.getVertices().entrySet())
      distance.put (pai.getKey(), Double.MAX_VALUE);
    distance.put(source, 0.);

    PriorityQueue<Pair<Integer, Double>> pq = new PriorityQueue<Pair<Integer, Double>> (100);
    pq.add(new Pair<Integer, Double>(source, distance.get(source)));

    while (!pq.isEmpty()) {
      int from = pq.peek().getKey();
      pq.remove();
      double d_from = distance.get(from);

      SLelement<Edge<Integer, Double>> list = gr.getAdjacencyList(from);
      while (list != null) {
        int  to = list.getValue().getTo();
        double edgelength = list.getValue().getEdgeData();
        double pathlength = d_from + edgelength;
        if (pathlength < distance.get(to)) {
          distance.put(to, pathlength);
          pq.add(new Pair<Integer, Double>(to, pathlength));
          parent.put(to, from);
        }
        list = list.getNext();
      }
    }
  }
```

Explain why this is not Dijkstra's algorithm. In what order does it take
vertices out of the queue, and what does it do that your `dijkstra` does not?

Suppose you added your two checks to this loop. Which one fails first, and
why?

Last, explain why a test that compares only the final distances cannot tell
this code apart from yours.

```

```

---

## Question 3: Settled means final (5 points)

Draw your `negativeEdgeGraph` (ASCII art is fine), with every edge weight.

Walk through your `dijkstra` on it from vertex 0. List the vertices in the
order they are settled, and stop at the moment check 2 fails. Paste the
message your exception printed.

What promise does the settled-set invariant make, and how does the negative
edge break it? Road lengths are never negative. Name one other kind of edge
weight where a negative value could show up.

```

```

---

## Question 4: Your feature (Part 3) (5 points)

What does your feature show? Explain why someone in Chapel Hill might want
to know it.

Which data structures did you use? For each one, name another structure
from the course you could have used instead, and explain why you chose the
one you did.

Describe one design decision you made and one approach you tried or
considered and rejected.

If you used AI for this part, say what you asked for and what you changed
in what it gave you.

```

```
