import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

class RandomUtil {

    private Random rndm = new Random();
    private int maxVal;

    RandomUtil(int maxVal) {
        this.maxVal = maxVal;
    }
    public int generateRandomNum() { return rndm.nextInt(this.maxVal - 1) + 1; }

    public int[] generateTwoRandomNums() {
        int[] rndm2 = new int[2];
        rndm2[0] = generateRandomNum();
        rndm2[1] = generateRandomNum();
        while(rndm2[0] == rndm2[1]) {
            rndm2[1] = generateRandomNum();
        }
        return rndm2;
    }
}


interface GraphInterface {
    // get the number of vertex
    int getVertexNumber();

    // retrive all To-Vertexs with the weight from the start vertex
    // Return the array of string ["toVertex, weight"]
    List<String> getNextVertexArray(int start);
 
    // Generate a simple connected direct Graph with the assigned vertexNumber and edgeNumber
    GraphInterface createConnectedGraph();
  
    // Add the algorithm to compute the graph
    GraphInterface setAlgorithm(AlgorithmInterface alg);

    // Computer Diameter, eccentricity and radius
    GraphInterface computeDiameter();

    // Retrieve the shortest path between vertex1 and vertex2
    GraphInterface computeShortestPath(int vertex1, int vertex2);
}


class GraphHashMap implements GraphInterface {
    // Key is the index of fromVertex and Value is a key value pair <toVertex, weight>
    private HashMap<Integer, HashMap<Integer, Integer>> graph;

    private int vertexNumber;
    private int edgeNumber;

    // The Algorithm to compute the graph
    private AlgorithmInterface alg = null;

    GraphHashMap(int vertexNumber, int edgeNumber) {
        this.vertexNumber = vertexNumber;
        this.edgeNumber = edgeNumber;
        this.graph = new HashMap<>();
        for (int i = 1; i <= this.vertexNumber; i++) {
            this.graph.put(i, new HashMap<>());
        }
    }

    @Override
    // Generate a simple connected direct Graph with vertexNumber and edgeNumber
    public GraphHashMap createConnectedGraph() {
        List<Integer> vertexList = generateShuffleVertex();

        // create a minimum connected direct Graph based on the shuffle vertex index
        int edgeNumberCreated = createMiniConnectedGraph(vertexList);
        // add more edges until the edge number meet the expectation
        addRestEdges(this.edgeNumber - edgeNumberCreated);
        // Print graph in an elegant way
        printGraph();

        return this;
    }

    @Override
    public int getVertexNumber() {
        return this.vertexNumber;
    }

    @Override
    // Get the next level of all the vertex with weight staring from vertex start
    public List<String> getNextVertexArray(int start) {
        List<String> res = new ArrayList<>();
        HashMap<Integer, Integer> next = graph.get(start);
        // return an empty list if no next vertex
        if (next == null || next.isEmpty())
            return res;

        for (Map.Entry<Integer, Integer> entry : next.entrySet()) {
            // add the string nextVertexIndex, weight
            res.add(entry.getKey().toString() + "," + entry.getValue().toString());
        }
        return res;
    }

    @Override
    // Set the Algorithm used to compute graph
    public GraphInterface setAlgorithm(AlgorithmInterface alg) {
        this.alg = alg;
        return this;
    }

    @Override
    public GraphInterface computeDiameter() {
        if(this.alg == null) {
            System.out.println("Algorithm is missing, please call setAlgorithm to set it");
            return this;
        }

        this.alg.computeDiameter(this);
        return this;
    }

    @Override
    // Get the shortest distance path between vertex1 and vertex2 using Dijkstra algorithm
    public GraphInterface computeShortestPath(int vertex1, int vertex2) {
        if(this.alg == null) {
            System.out.println("Algorithm is missing, please call setAlgorithm to set it");
            return this;
        }

        this.alg.getShortestPath(this, vertex1, vertex2);
        return this;
    }

    private List<Integer> generateShuffleVertex() {
        List<Integer> shuffleVertexList = new ArrayList<>();
        for (int i = 1; i <= this.vertexNumber; i++)
            shuffleVertexList.add(i);
        Collections.shuffle(shuffleVertexList);
        return shuffleVertexList;
    }

    private void addEdge(int from, int to) {
        // Generate the random value for weight from 1 to 5
        this.graph.get(from).putIfAbsent(to, (new RandomUtil(5)).generateRandomNum());
    }

    private int createMiniConnectedGraph(List<Integer> list) {
        HashSet<Integer> src = new HashSet<>(list);
        HashSet<Integer> dst = new HashSet<>();
        int cur = list.get(0);
        int edgesNum = 0;
        src.remove(cur);
        dst.add(cur);
        int index = 1;
        while (!src.isEmpty()) {
            int neighbour = list.get(index);
            if (!dst.contains(neighbour)) {
                addEdge(cur, neighbour);
                edgesNum++;
                src.remove(neighbour);
                dst.add(neighbour);
            }
            cur = neighbour;
            index++;
        }
        return edgesNum;
    }

    private void addRestEdges(int restEdges) {
        // Add random edges if adding less than 60% of the max allowed edges
        if (this.edgeNumber <= this.vertexNumber * (this.vertexNumber - 1) * 0.6) {
            addRandomEdges(restEdges);
        } else {
            // add edges following the vertexList order
            addEdges(restEdges);
        }
    }

    private void addRandomEdges(int restEdges) {
        while (restEdges > 0) {
            // Generate the random fromVertex and toVertex
            int[] rndm2 = (new RandomUtil(this.vertexNumber)).generateTwoRandomNums();
            int from = rndm2[0];
            int to = rndm2[1];
            if (from == to)
                continue;
            if (!this.graph.get(from).containsKey(to)) {
                addEdge(from, to);
                restEdges--;
            }
        }
    }

    private void addEdges(int restEdges) {
        for (int from = 1; from <= this.vertexNumber; from++) {
            for (int to = 1; to <= this.vertexNumber; to++) {
                if (from == to) continue;
                if (!this.graph.get(from).containsKey(to)) {
                    addEdge(from, to);
                    restEdges--;
                    if (restEdges == 0)
                        return;
                }
            }
        }
    }

    private void printGraph() {
        System.out.println("Print out the randomly generated graph: ");
        System.out.println("{ ");
        for (int i = 1; i <= this.vertexNumber; i++) {
            System.out.print("   :" + i + "  [ ");
            if (this.graph.get(i).isEmpty()) {
                System.out.println("],");
            } else {
                for (Map.Entry<Integer, Integer> entry : this.graph.get(i).entrySet()) {
                    System.out.print("(:" + entry.getKey() + " " + entry.getValue() + ") ");
                }

                if (i == this.vertexNumber) {
                    System.out.println("]");
                } else {
                    System.out.println("],");
                }

            }
        }
        System.out.println("}");
        System.out.println("================================");
    }
}

interface AlgorithmInterface {

    // Computer Diameter, eccentricity and radius
    AlgorithmInterface computeDiameter(GraphInterface graph);

    // Get the shortest distance path between vertex1 and vertex2 using Dijkstra algorithm
    void getShortestPath(GraphInterface graph, int vertex1, int vertex2);

}

class Algorithm implements AlgorithmInterface{

    // record the max total weight for each vertex
    private int maxWeight = 0;

    @Override
    // Computer Diameter, eccentricity and radius
    public Algorithm computeDiameter(GraphInterface graph) {
        int radius = 0;
        int diameter = 0;
        System.out.println("The eccentricity of all the Vertex: ");
        for (int i = 1; i <= graph.getVertexNumber(); i++) {
            LinkedList<Integer> currentPath = new LinkedList<>();
            currentPath.add(i);
            maxWeight = 0;
            int eccentricity = computeEcceBacktrack(graph, i, currentPath, 0);
            if (i == 1) {
                diameter = eccentricity;
                radius = eccentricity;
            }

            radius = Math.min(radius, eccentricity);
            diameter = Math.max(diameter, eccentricity);
            System.out.println("The eccentricity of Vertex " + i + ": " + eccentricity);
        }

        System.out.println("================================");
        System.out.println("The diameter of the graph: " + diameter);
        System.out.println("The radius of the graph:   " + radius);
        System.out.println("================================");

        return this;
    }

    @Override
    // Get the shortest distance path between vertex1 and vertex2 using Dijkstra algorithm
    public void getShortestPath(GraphInterface graph, int vertex1, int vertex2) {
        // Try to get the shortest path from vertex1 to vertex2 and from vertex2 to vertex1
        String[] resForward = dijkstra(graph, vertex1, vertex2).split(",");
        String[] resBackward = dijkstra(graph, vertex2, vertex1).split(",");

        int disForward = Integer.parseInt(resForward[0]);
        int disBackward = Integer.parseInt(resBackward[0]);

        System.out.println("The shortest distance path between vertex " + vertex1 + " and vertex " + vertex2 + " :");
        if (disForward < disBackward) {
            System.out.println(resForward[1]);
            System.out.println("Total Weight: " + disForward);
        } else {
            System.out.println(resBackward[1]);
            System.out.println("Total Weight: " + disBackward);
        }
    }

    // Compute eccentricity of Vertex start using backtracking
    private int computeEcceBacktrack(GraphInterface graph, int start, LinkedList<Integer> currentPath, int dist) {
        List<String> next = graph.getNextVertexArray(start);
        // return the total max weight if no next vertex
        if (next.isEmpty())
            return maxWeight;

        for (String n : next) {
            int nextVer = Integer.parseInt(n.split(",")[0]);
            int weight = Integer.parseInt(n.split(",")[1]);

            // skip if the next vertex already visited
            if (currentPath.contains(nextVer)) continue;

            currentPath.add(nextVer);
            dist += weight;

            maxWeight = Math.max(maxWeight, dist);
            computeEcceBacktrack(graph, nextVer, currentPath, dist);

            currentPath.removeLast();
            dist -= weight;
        }

        return maxWeight;
    }

    private String reversePath(int vertex1, int vertex2, int[] predecessor) {
        StringBuilder sb = new StringBuilder();
        while (vertex1 != vertex2) {
            sb.append(" ").append(vertex2);
            vertex2 = predecessor[vertex2];
        }

        String[] sbs = sb.toString().trim().split(" ");
        StringBuilder revsb = new StringBuilder();
        for (int i = sbs.length - 1; i >= 0; i--) {
            revsb.append(" -> ").append(sbs[i]);
        }

        return revsb.toString();
    }

    private String generateShortestPath(int vertex1, int vertex2, int[] predecessor, int weight) {
        return weight + ", " + vertex1 + reversePath(vertex1, vertex2, predecessor);
    }

    private String dijkstra(GraphInterface graph, int vertex1, int vertex2) {
        int vertexCnt = graph.getVertexNumber() + 1;
        int[] dist = new int[vertexCnt];
        boolean[] visited = new boolean[vertexCnt];
        for (int i = 0; i < vertexCnt; i++) {
            dist[i] = Integer.MAX_VALUE;
        }
        int[] predecessor = new int[vertexCnt];
        Queue<Integer> queue = new LinkedList<>();
        queue.add(vertex1);
        dist[vertex1] = 0;
        while (!queue.isEmpty()) {
            Integer vertex = queue.poll();
            if (visited[vertex]) continue;
            visited[vertex] = true;

            List<String> next = graph.getNextVertexArray(vertex);
            // skip if no next vertex
            if (next.isEmpty())
                continue;

            for (String n : next) {
                int nextVer = Integer.parseInt(n.split(",")[0]);
                int weight = Integer.parseInt(n.split(",")[1]);

                if (dist[vertex] < (dist[nextVer] - weight)) {
                    dist[nextVer] = dist[vertex] + weight;
                    predecessor[nextVer] = vertex;
                }
                queue.add(nextVer);
            }
        }

        // Return the maximum Integer for the weight if the current searching path can not be reachable
        if (dist[vertex2] == Integer.MAX_VALUE)
            return Integer.MAX_VALUE + ", ";

        return generateShortestPath(vertex1, vertex2, predecessor, dist[vertex2]);
    }
}

public class Solution {
    public static void main(String argv) {
        String[] parameters = argv.split(" ");
        int vertexNum, edgeNum;

        try {
            vertexNum = Integer.parseInt(parameters[2]);
            edgeNum = Integer.parseInt(parameters[4]);
        } catch (Exception e) {
            System.out.println("Error: Please check whether the command follows the right format");
            System.out.println("Example: graph -N 8 -S 15");
            return;
        }

        if(edgeNum < vertexNum - 1 || edgeNum > vertexNum * (vertexNum - 1)) {
            System.out.println("Error: S should be in the range between " + (vertexNum-1) + " (inclusive) to " + (vertexNum*(vertexNum-1)) + " (inclusive)");
            return;
        }

        int[] vertex = (new RandomUtil(vertexNum)).generateTwoRandomNums();
        (new GraphHashMap(vertexNum, edgeNum)).createConnectedGraph().setAlgorithm(new Algorithm()).computeDiameter().computeShortestPath(vertex[0], vertex[1]);
    }
}
