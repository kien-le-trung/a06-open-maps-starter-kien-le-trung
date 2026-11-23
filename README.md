# Final Project: Routes from Sitterson Hall

**COMP210, Fall 2026**

| | |
|---|---|
| Released | Tuesday, November 10 |
| Due | Tuesday, November 24, 5:00pm. |
| Points | 150, plus a 10% early bonus (12.5 points) |
| AI | Rung 3, AI as Collaborator. See "Using AI" below. |
| Collaboration | Solo. Follows the syllabus policy. Never share your API key. |

You walk out of Sitterson Hall and need to get across town. A map app answers
in a second. In this project you build the part of that app that does the
answering, on Chapel Hill's real streets: 4,806 intersections and the roads
between them using the OpenStreetMap API, and some light caching.

You find two routes to a destination you choose. Breadth-first search finds
the route through the fewest intersections, and Dijkstra's algorithm finds
the route with the least distance. They are not always the same route, and
your write-up explains why. Then you add a feature of your own design.

This project adapts BRIDGES assignment #9, "Shortest Path on Real Maps," from
UNC Charlotte.

We will be using the following BRIDGES Classes:

| Class | What it is | In the final project |
|---|---|---|
| `GraphAdjList<K, E1, E2>` | BRIDGES' graph, stored as adjacency lists: a map from each vertex ID to its `Element`, and from each vertex ID to a linked list of its outgoing edges. | The Chapel Hill road map, a `GraphAdjList<Integer, OsmVertex, Double>`. You call `getVertices()`, `getAdjacencyList(from)`, `addVertex`, `addEdge`, and `getEdgeData(from, to)` on it. |
| `Element<E>` | A BRIDGES wrapper around one piece of data, plus drawing information (color, size, location). It is the parent class of `SLelement`. | Each vertex is an `Element<OsmVertex>`. `graph.getVertices()` maps each vertex ID to its `Element`, and `getValue()` returns the `OsmVertex` with the latitude and longitude. `getVisualizer()` is the drawing side: `MyFeature` uses it to color vertices. |
| `OsmVertex` | The data for one OpenStreetMap intersection. | The value inside each vertex's `Element`. `getLatitude()` and `getLongitude()` give its location. `MapLoader` saves and rebuilds these, and `OpenStreetsMap` uses them to find the intersection closest to a point. |
| `SLelement<E>` | A node in a singly linked list. It extends `Element`, so it has everything an `Element` has, plus a `getNext()` pointer to the next node. | `graph.getAdjacencyList(from)` returns the head node of `from`'s edge list, an `SLelement<Edge<Integer, Double>>`. `getValue()` is the `Edge`, `getNext()` is the next node, and `null` means the list is over. |
| `Edge<K, E2>` | One directed edge: the vertex it goes to and its edge data. It is not an `Element`. | An `Edge<Integer, Double>`. `getTo()` is the ID of the neighboring intersection, and `getEdgeData()` is the road segment's length in meters. The edge doesn't store where it starts, because the adjacency list it's in already tells you that. |
| `ElementVisualizer` | The drawing settings attached to each `Element`. | It comes back from `getVisualizer()`. The code calls `setColor` on it to color vertices, and `MapLoader` reads `getLocationX()` and `getLocationY()` to save where each vertex is drawn. |
| `Color` | A BRIDGES RGB color. | `OpenStreetsMap` defines Carolina blue, orange, and red for drawing routes, and `MyFeature` uses colors for its picture. |
| `Bridges` | The connection to the BRIDGES server, made from the assignment number, username, and API key in `.env`. | `Main` creates it, `MapLoader` uses it to fetch the map, and `Main` uses it to post each picture (`setDataStructure`, `setTitle`, `setDescription`, `visualize`). |
| `DataSource` | The BRIDGES object that downloads real-world data sets. | `bridges.getDataSource().getOsmData(place, level)` downloads the OpenStreetMap data. Nobody imports it; the call chain returns it. |
| `OsmData` | The downloaded OpenStreetMap data set. | Only used for `.getGraph()`, which turns the download into the `GraphAdjList`. It also comes back from the call chain without an import. |

- Each **vertex** is an intersection. Its key is an `int` ID, and it holds an
  `OsmVertex` with the intersection's latitude and longitude.
- Each **edge** is one stretch of road between two intersections. It holds
  the stretch's length in meters.
- Edges are **directed**. A two-way street is two edges, one each way, and a
  one-way street is one edge.

## How the map is stored

The map is a BRIDGES `GraphAdjList<Integer, OsmVertex, Double>`, the
adjacency list:

| Slot | Type | What we fill in |
|---|---|---|
| `K` | `Integer` | The vertex's ID, one number per road intersection. Every map lookup uses it: `distance`, `parent` and `steps` are all keyed by this ID, and so is the adjacency list. |
| `E1` | `OsmVertex` | The intersection's location, its latitude and longitude. `OpenStreetsMap` uses it to find the intersection closest to a point, such as Sitterson Hall or your destination, and BRIDGES uses it to place the vertex when it draws the map. |
| `E2` | `Double` | The road segment's length in meters. BFS ignores it. Dijkstra adds these up, and `OpenStreetsMap` totals them to report a route's length. |

Here are the calls you need:

| Call | What it gives you |
|---|---|
| `graph.getVertices()` | a `HashMap` from each vertex ID to its `Element<OsmVertex>` |
| `graph.getVertexData(id)` | the `OsmVertex`: `getLatitude()`, `getLongitude()` |
| `graph.getVertex(id).getVisualizer()` | where BRIDGES draws the vertex: `getLocationX()`, `getLocationY()` |
| `graph.getAdjacencyList(id)` | the first node of that vertex's edge list, or `null` if it has no edges |
| `graph.addVertex(id, data)` and `graph.addEdge(from, to, length)` | build a graph |

`getAdjacencyList` hands you a singly linked list, so you walk it the same
way you walked your own linked lists in Module 2. `SLelement`, `Edge`, and
`Element` come from `bridges.base`, so add `import bridges.base.SLelement;`
and the others at the top of the files that use them (IntelliJ offers to add
them for you):

```java
SLelement<Edge<Integer, Double>> list = graph.getAdjacencyList(from);
while (list != null) {
    Edge<Integer, Double> edge = list.getValue();
    int to = edge.getTo();               // the vertex at the other end
    double length = edge.getEdgeData();  // meters
    list = list.getNext();
}
```

## Setup

Same setup as A5: a Maven project you open in IntelliJ, with BRIDGES pulled
from JitPack and your key read from `.env` by `BridgesConfig`.

1. **File > Open** the folder you cloned, the one holding `pom.xml`. Wait for
   the Maven import to finish.
2. Copy `.env.example` to `.env` in the same folder and fill in your BRIDGES
   username and API key. Leave `BRIDGES_ASSIGNMENT` at 6.
3. Run `Main` once with the green arrow. It says Part 0 is not written yet,
   loads the map from BRIDGES (a minute or two), and then stops with a message
   that Part 1 is not written yet. Those messages mean your setup works.

## Part 0: Save the map (20 points)

Every run of `Main` loads the map from the BRIDGES map server, and that takes
about a minute. Every student in the course shares that server. In Part 0
you make `Main` fetch the map once, save it to a file, and read the file on
every run after that. It is also your first chance to work with the adjacency
list. To save the graph you walk all of it, and to read it back you build a
graph from scratch.

Open `MapLoader.java`. `cacheFile` is written: it names the file, for
example `data/chapel-hill-north-carolina-tertiary.txt`. You write three
methods.

### The file format

Plain text, one space between fields:

```
place Chapel Hill, North Carolina
level tertiary
vertices 4806
0 35.858983 -79.101819 977.2961869168789 -5075.894994299386
...
edges 7068
5 37 128.10505963057693
...
```

| Line | Holds |
|---|---|
| `place ...` and `level ...` | what `load` was asked for |
| `vertices N` | how many vertex lines follow |
| a vertex line | ID, latitude, longitude, map x, map y |
| `edges M` | how many edge lines follow |
| an edge line | from, to, length in meters |

The two counts let `read` check that the file is complete.

### `write(graph, place, level, file)`

Write the header, then one line per vertex, then the `edges` line, then one
line per edge. The `edges` line comes before any edge, so count the edges
first: walk every adjacency list once to count, or collect the edge lines in a
list and write them after the count. Make the folder first with
`Files.createDirectories(file.getParent())`, then open the file with
`Files.newBufferedWriter(file)` inside a try-with-resources block so it gets
closed.

Build each line with `+`, as in `id + " " + latitude + " " + ...`. Java then
writes every digit of each `double`, and the numbers come back unchanged when
you read them.

Watch the edge order. BRIDGES adds each new edge to the **front** of its
vertex's list, so a list holds its edges newest first. If you write a list
front to back, then read the file and add the edges in that order, every list
comes back reversed. That changes which route BFS picks when two routes tie.
Write each vertex's list back to front. A test in `Part0Test` that writes a
graph, reads it back, and compares each vertex's list will catch a reversed
list.

### `read(file)`

Read the file with `Files.newBufferedReader(file)` and `readLine()`, split each
line with `split(" ")`, and convert with `Integer.parseInt` and
`Double.parseDouble`. For each vertex line:

```java
graph.addVertex(id, new OsmVertex(latitude, longitude));
graph.getVertex(id).setLocation(x, y);
```

For each edge line, `graph.addEdge(from, to, length)`.

Throw an `IOException` when the file is not a complete map:

- the file ends before the counts say it should (`readLine()` returns `null`
  at the end of the file),
- a vertex line does not have 5 fields or an edge line does not have 3,
- the count lines do not start with `vertices` and `edges`, or
- a number does not parse. `Integer.parseInt` and `Double.parseDouble` throw
  `NumberFormatException`, which is not an `IOException`, so catch it and
  throw an `IOException` instead.

The `place` and `level` lines hold names with spaces in them, so skip those
two lines without checking their fields.

### `load(bridges, place, level)`

If `cacheFile(place, level)` exists and is not empty (`Files.exists`,
`Files.size`), return `read` of it. If there is no file, or `read` throws an
`IOException`, fetch the map from BRIDGES, `write` it, and return it:

```java
bridges.getDataSource().getOsmData(place, level).getGraph()
```

Print one line saying where the map came from, such as `Loaded the map from
data/chapel-hill-north-carolina-tertiary.txt.` or `Fetched the map from
BRIDGES and saved it to data/...`. The check below uses those lines.

**Check your work:** write at least three tests in `Part0Test` (see "Writing
your tests" below) and run them. Then run `Main` twice. The first run says it
saved the map, and the second should load it from the file in well under a
second. Open the file in `data/` to see the whole graph as plain text.

## Part 1: Fewest intersections (30 points)

Open `RoutePlanner.java`. You write two methods.

### `bfs(graph, source, steps, parent)`

Breadth-first search from `source`, with a queue (`ArrayDeque`). Fill in two
maps:

- `steps` maps each vertex BFS reaches to the number of edges on the shortest
  route to it. The source gets 0. A vertex BFS never reaches gets no entry.
- `parent` maps each reached vertex to the vertex just before it on that
  route. The source gets no entry.

The keys of `steps` work as your visited set. Checking `steps.containsKey(to)`
is O(1), and that is what keeps BFS at O(V + E). The autograder runs your
`bfs` on 50,000 vertices.

### `routeTo(parent, source, destination)`

Return the route as a list, `source` first and `destination` last. Start at
`destination` and follow `parent` back until there is no parent. You collect
the route backward, so reverse it at the end (`Collections.reverse`).

If `destination` is not the source and has no parent, it was never reached,
so return an empty list. The route from the source to itself is
`[source]`.

**Check your work:** write at least three tests in `Part1Test` and run them.

### Choose your destination

At the top of `Main.java`, set `DESTINATION_NAME`, `DESTINATION_LATITUDE`,
and `DESTINATION_LONGITUDE` to somewhere in Chapel Hill you go. To find the numbers, right-click a spot on
[openstreetmap.org](https://www.openstreetmap.org) and choose "Show address".
The starting value is the Friday Center. Pick your own, because your
write-up's numbers come from it.

`Main` snaps your destination to the nearest intersection Sitterson can
reach. Run `Main`: it prints the BFS route's steps and meters and posts
**picture 1**, the BFS route in orange.

## Part 2: Fewest meters (50 points)

BFS treats a 10-meter road segment the same as a 1,000-meter one. Dijkstra's
algorithm adds up the length of each segment, so it finds the route with the
fewest meters.

### `Pair.compareTo`

Dijkstra's priority queue holds `Pair<Integer, Double>` entries, a vertex and
a distance, and it needs to know which pair comes first. `Pair` answers that
with `compareTo(other)`, which sets the class's **natural order**: the order
Java uses when you give it nothing else. `a.compareTo(b)` returns a negative
number when `a` comes first, a positive number when `b` comes first, and 0
for a tie.

Open `Pair.java`. A pair's vertex comes from `getKey()` and its distance
from `getValue()`. `Pair` is declared `Comparable<Pair<V, D>>`, and its
distance type `D` has to be `Comparable` too. `Double` is. Write
`compareTo(other)` so pairs are ordered by distance, smallest first. The
vertex plays no part. You do not need to subtract anything: `D` already has
a `compareTo` of its own. Subtracting would go wrong anyway, since
`(int) (0.25 - 0.75)` is 0 and turns two different distances into a tie.

A `java.util.PriorityQueue` built with no arguments orders its entries by
`compareTo`, so once yours works, the queue is a min-heap on distance and
`poll()` hands you the closest vertex. Create it in `dijkstra` with
`new PriorityQueue<>()`.

### `dijkstra(graph, source, distance, parent)`

Set every vertex's distance to `Double.MAX_VALUE` and the source's to 0. Put
the source in the queue. Then, while the queue is not empty:

1. `poll()` the closest pair. Its vertex is the next one to **settle**.
2. Relax each of its edges: if going through this vertex gives a shorter
   distance to the vertex at the other end, update `distance` and `parent`,
   and add a new pair to the queue.

`java.util.PriorityQueue` cannot change the distance of a pair already in it.
So when a vertex gets a shorter distance, you add a second pair and the old
one stays behind. When an old pair comes out of the queue later, its distance
is larger than the vertex's current distance. Skip it with `continue`.

#### The invariant, in code

Dijkstra's algorithm keeps one promise: **once a vertex is settled, its
distance is final.** You make your code check that promise as it runs. Keep
a `HashSet` of settled vertices and the distance of the vertex settled last,
and add two checks:

| Check | What it enforces | When it fails |
|---|---|---|
| Check 1 | Vertices settle in order of distance. | You settle a vertex whose distance is smaller than the distance of the vertex settled before it. |
| Check 2 | A settled vertex never gets a shorter distance. | Relaxing an edge would lower the distance of a vertex that is already settled. |

When a check fails, throw an `IllegalStateException` whose message starts
with `"Check 1"` or `"Check 2"` and names the vertex, for example
`"Check 2 failed: vertex 7 was settled at distance 40.0, but the edge from 3
gives it 35.0"`.

On a correct Dijkstra with real road lengths, neither check ever fails. They
fail when the algorithm is wrong or when a graph breaks the algorithm's rules.

### `negativeEdgeGraph()`

Build a small graph with one negative edge on which your `dijkstra`, run from
vertex 0, fails **check 2**. Three or four vertices are enough. Work it out on
paper first: which vertex settles too early, and which edge makes its
distance go down afterward? Question 3 of the write-up asks you to walk
through it.

**Check your work:** write at least three tests in `Part2Test` and run them.
Then run `Main`. It prints the Dijkstra route and posts two more pictures.
Picture 2 shades every intersection by its distance from Sitterson, white
nearby to black far away. Picture 3 shows both routes: BFS in orange, Dijkstra
in red, and the intersections they share in purple.

## Part 3: Your feature (25 points)

Open `MyFeature.java`. `draw(graph, source, destination)` colors the map's
vertices to show something you built, and returns a one-sentence
description. `source` is Sitterson Hall's vertex and `destination` is your
destination's, the same two Parts 1 and 2 route between. `Main` posts the
result as **picture 4**. You decide what the feature does, and it needs to:

- answer a question about getting around Chapel Hill that Parts 1 and 2 do
  not already answer,
- use at least one data structure from the course beyond the `HashMap`s and
  the priority queue you already used, or use one of them in a new way, and
- show its answer in picture 4.

Here is an example of the size we have in mind. Its question is off limits
for your own feature, because it is the one in the course's sample solution.
Its techniques are yours to use in a different feature:

> **Nearest dining hall.** (Don't use this one) Color each intersection by whether Lenoir or
> Chase is the shorter walk. Dijkstra measures outward from its source, but
> this question is about walking toward a hall, so the sample turns every
> edge around first. It also adds one extra vertex with a zero-length edge to
> each hall, so a single Dijkstra run answers for every intersection at once.

Pick a different idea. Some places to start, which you can take, change, or
replace with your own:

Use `OpenStreetsMap.fadeAll`, `colorRoute`, and `styleRoot` to color the
picture, and `OpenStreetsMap.getClosestVertex` to turn a latitude and
longitude into a vertex. You can add methods, fields, and nested classes to
`MyFeature.java`. Keep it all in that one file so it uploads with the rest.

Part 3 is graded by hand, from your code and picture 4. Write-up Question 4,
graded with the write-up, asks you to explain your design. A feature that
answers the nearest-dining-hall question earns no Part 3 points.

## Part 4: Write-up (25 points)

Answer the four questions in `writeup.md` with the numbers from your
destination. Put your BRIDGES URL at the top: your pictures live at
`https://bridges-cs.herokuapp.com/assignments/6/yourusername`.

## Early bonus (+12.5)

Submit your code to **Final Project** at least 72 hours before the deadline,
by 5:00pm on Saturday, November 21, and the autograder adds 12.5 points, 10%
of the 125-point code assignment. The bonus goes by the time of each code
submission, checked on its own, so a resubmission after that time does not
earn it, even if an earlier one did.

## Looking at the pictures

The map is big enough that BRIDGES draws it in its large-graph viewer:

- Scroll to zoom and drag to pan. The routes are small at full size, so zoom
  in on them.
- Every road is drawn as a thin blue line whatever its color in your code, so
  the pictures show routes and regions by coloring the intersections.
- Use the arrows under the picture to move between pictures 1 to 4.

## Using AI

The final project is at Rung 3, AI as Collaborator. You can hand LearnWithAI a
clearly specified piece, such as "write `read` for this file format," and
audit what comes back. You still own every design decision, and Part 3 and
the write-up ask you to explain yours. Submit your AI conversations with your
write-up, as on every assignment. The course treats undisclosed AI use as
falsifying the submission.

## Writing your tests

`src/test/java/comp210/a6` has one test file per part: `Part0Test`,
`Part1Test`, and `Part2Test`. Each starts with a few helpers that build small
graphs and no tests. Write at least three JUnit tests in each file as you
finish its part, then right-click the file and choose **Run**.

Each file is worth 3 points of its part. The autograder counts the tests
JUnit finds in each file, and three or more earns the 3 points. It also runs
your tests on your code and lists which ones pass. That list is not scored,
because a bug in your code already costs points in the part's other tests.

The best tests use graphs small enough to draw on paper, so you know the right
answer before you run the code. Some ideas:

| File | Ideas |
|---|---|
| `Part0Test` | the header lines `write` puts first; `read` on a short file you type yourself; a file that ends early; writing a graph and reading it back, list order included |
| `Part1Test` | `bfs` on a line of vertices; a graph where the route with fewer steps is not the first one found; a one-way edge; `routeTo` for a vertex `bfs` never reached |
| `Part2Test` | `compareTo` on two close distances such as 0.25 and 0.75; a graph where the cheapest route has more steps; a vertex `dijkstra` cannot reach; your `negativeEdgeGraph` failing check 2 |

Gradescope also runs its own tests. It checks your code against random
graphs, times `bfs` on 50,000 vertices, and counts how many times `dijkstra`
walks each vertex's edges.

## What to submit

The final project has two Gradescope assignments.

Upload your code to **Final Project**:

1. `MapLoader.java`
2. `RoutePlanner.java`
3. `Pair.java`
4. `MyFeature.java`
5. `Part0Test.java`
6. `Part1Test.java`
7. `Part2Test.java`

The autograder grades Parts 0 to 2 as soon as you submit, and you can
resubmit as many times as you like before the deadline. If `MyFeature.java`
does not compile, the autograder sets it aside so it cannot cost you Parts 0
to 2, and your instructor grades Part 3 from the file you uploaded. A test
file that does not compile is set aside too, and it costs you only that
file's 3 points.

Upload `writeup.md` (or a PDF of it) and your AI conversations to **Final
Project Write-up**, with your BRIDGES URL at the top.

Leave `.env` and the `data/` folder out of both. Same rule as A5: an API key
anywhere in either submission is an automatic zero, and the autograder scans
every file you upload for one.

## Grading

| Part | Points | Gradescope assignment | Graded by |
|---|---|---|---|
| Part 0: `MapLoader`, including 3 for your `Part0Test` | 20 | Final Project | autograder |
| Part 1: `bfs` and `routeTo`, including 3 for your `Part1Test` | 30 | Final Project | autograder |
| Part 2: `compareTo`, `dijkstra`, and `negativeEdgeGraph`, including 3 for your `Part2Test` | 50 | Final Project | autograder |
| Part 3: your feature | 25 | Final Project | instructor |
| Early bonus: submit at least 72 hours before the deadline | +12.5 | Final Project | autograder |
| BRIDGES URL shows all four pictures under your account | 5 | Final Project Write-up | instructor |
| Write-up questions | 20 | Final Project Write-up | instructor |

## Files

```
your cloned folder/          <- open THIS folder in IntelliJ
  README.md                   this file
  writeup.md                  Part 4
  pom.xml                     Maven build, pulls BRIDGES, dotenv-java, JUnit
  .env.example                template for your credentials
  .gitignore                  keeps .env and data/ out of git
  data/                       appears after Part 0; delete it to fetch the map again
  src/main/java/comp210/a6/
    MapLoader.java            Part 0, yours
    RoutePlanner.java         Parts 1 and 2, yours
    Pair.java                 Part 2, yours: compareTo
    MyFeature.java            Part 3, yours
    Main.java                 runs it all; set your destination at the top
    OpenStreetsMap.java       map helpers and drawing code
    BridgesConfig.java        reads .env, same as A5
  src/test/java/comp210/a6/
    Part0Test.java            Part 0, yours: at least three tests
    Part1Test.java            Part 1, yours: at least three tests
    Part2Test.java            Part 2, yours: at least three tests
```
