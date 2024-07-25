import java.util.*;

// Generate Random values
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

class Graph {

    // Key is the index of fromVertex and Value is a key value pair <toVertex, weight>
    private HashMap<Integer, HashMap<Integer, Integer>> graph;

    private int vertexNumber;
    private int edgeNumber;

    private RandomUtil rndmWeight;
    private RandomUtil rndmVertex;

    final int maxWeightSetting = 5;

    // Generate a simple connected direct Graph with vertexNumber and edgeNumber
    Graph(int vertexNumber, int edgeNumber) {
        this.vertexNumber = vertexNumber;
        this.edgeNumber = edgeNumber;
        this.rndmWeight = new RandomUtil(maxWeightSetting);
        this.rndmVertex = new RandomUtil(this.vertexNumber);
        this.graph = new HashMap<>();

        for(int i = 1; i <= this.vertexNumber; i++) {
            this.graph.put(i, new HashMap<>());
        }

        // create a connected direct Graph
        createConnectedGraph();
        printGraph();
    }

    private void createConnectedGraph() {
        List<Integer> vertexList = generateShuffleVertex();

        // create a minimum connected direct Graph based on the shuffle vertex index
        int edgeNumberCreated = createMiniConnectedGraph(vertexList);
        // add more edges until the edge number meet the expectation
        addRestEdges(vertexList, this.edgeNumber - edgeNumberCreated);
    }

    private List<Integer> generateShuffleVertex()
    {
        List<Integer> shuffleVertexList = new ArrayList<>();
        for(int i = 1; i <= this.vertexNumber; i++)
            shuffleVertexList.add(i);
        Collections.shuffle(shuffleVertexList);
        return shuffleVertexList;
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
            if(!dst.contains(neighbour)) {
                this.graph.get(cur).putIfAbsent(neighbour, this.rndmWeight.generateRandomNum());
                edgesNum++;
                src.remove(neighbour);
                dst.add(neighbour);
            }
            cur = neighbour;
            index++;
        }
        return edgesNum;
    }

    private void addRestEdges(List<Integer> vertexList, int restEdges) {
        // Add random edges if adding less than 60% of the max allowed edges
        if(this.edgeNumber <= this.vertexNumber * (this.vertexNumber - 1) * 0.6) {
            addRandomEdges(restEdges);
        } else {
            // add edges following the vertexList order
            addEdges(restEdges);
        }
    }

    private void addRandomEdges(int restEdges) {
        while(restEdges > 0) {
            int[] rndm2 = this.rndmVertex.generateTwoRandomNums();
            int cur = rndm2[0];
            int next = rndm2[1];
            if(cur == next)
                continue;
            if(!this.graph.get(cur).containsKey(next)) {
                this.graph.get(cur).putIfAbsent(next, this.rndmWeight.generateRandomNum());
                restEdges--;
            }
        }
    }

    private void addEdges(int restEdges) {
        for(int i = 1; i <= this.vertexNumber; i++) {
            for(int j = 1; j <= this.vertexNumber; j++) {
                if(i == j) continue;
                if(!this.graph.get(i).containsKey(j)) {
                    this.graph.get(i).putIfAbsent(j, this.rndmWeight.generateRandomNum());
                    restEdges--;
                    if(restEdges == 0)
                        return;
                }
            }
        }
    }

    private void printGraph() {
        System.out.println("Print out the randomly generated graph: ");
        System.out.println("{ ");
        for(int i = 1; i <= this.vertexNumber; i++) {
            System.out.print("   :" + i + "  [ ");
            if(this.graph.get(i).isEmpty()) {
                System.out.println("],");
            } else {
                for (Map.Entry<Integer, Integer> entry : this.graph.get(i).entrySet()) {
                    System.out.print("(:" + entry.getKey() + " " + entry.getValue() + ") ");
                }

                if(i == this.vertexNumber) {
                    System.out.println("]");
                } else {
                    System.out.println("],");
                }

            }
        }
        System.out.println("}");
        System.out.println("================================");
    }

    // record the max total weight for each vertex 
    private int maxWeight = 0;

    public Graph computeDiameter() {
        int radius = 0;
        int diameter = 0;
        System.out.println("The eccentricity of all the Vertex: ");
        for(int i = 1; i <= this.vertexNumber; i++) {
            LinkedList<Integer> currentPath = new LinkedList<>();
            currentPath.add(i);
            maxWeight = 0;
            int eccentricity = computeEcceBacktrack(i, currentPath, 0);
            if( i == 1) {
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

    private int computeEcceBacktrack(int start, LinkedList<Integer> currentPath, int dist) {
        HashMap<Integer, Integer> next = graph.get(start);
        // return the total max weight if no next vertex
        if(next ==null || next.isEmpty())
            return maxWeight;

        for (Map.Entry<Integer, Integer> entry : next.entrySet()) {
            int nextVer = entry.getKey();
            int weight = entry.getValue();

            // skip if the next vertex already visited
            if(currentPath.contains(nextVer)) continue;

            currentPath.add(nextVer);
            dist += weight;

            maxWeight = Math.max(maxWeight, dist);
            computeEcceBacktrack(nextVer, currentPath, dist);

            currentPath.removeLast();
            dist -= weight;
        }

        return maxWeight;
    }
    
    private String reversePath(int vertex1, int vertex2, int[] predecessor) {
        StringBuilder sb = new StringBuilder();
        while(vertex1 != vertex2) {
            sb.append(" ").append(vertex2);
            vertex2 = predecessor[vertex2];
        }

        String[] sbs = sb.toString().trim().split(" ");
        StringBuilder revsb = new StringBuilder();
        for(int i = sbs.length - 1; i >= 0; i--) {
            revsb.append(" -> " ).append(sbs[i]);
        }

         return revsb.toString();
    }

    private String generateShortestPath(int vertex1, int vertex2, int[] predecessor, int weight) {
        return weight + ", " + vertex1 + reversePath(vertex1, vertex2, predecessor);
    }

    private String dijkstra(int vertex1, int vertex2) {
        int v = graph.size() + 1;
        int[] dist = new int[v];
        boolean[] visited = new boolean[v];
        for (int i = 0; i < v; i++) {
            dist[i] = Integer.MAX_VALUE;
        }
        int[] predecessor = new int[v];
        Queue<Integer> queue = new LinkedList<>();
        queue.add(vertex1);
        dist[vertex1] = 0;
        while (!queue.isEmpty()) {
            Integer vertex = queue.poll();
            if (visited[vertex]) continue;
            visited[vertex] = true;

            HashMap<Integer, Integer> next = graph.get(vertex);
            if(next ==null || next.isEmpty())
                continue;

            for (Map.Entry<Integer, Integer> entry : next.entrySet()) {
                if (dist[vertex] < (dist[entry.getKey()] - entry.getValue())) {
                    dist[entry.getKey()] = dist[vertex] + entry.getValue();
                    predecessor[entry.getKey()] = vertex;
                }
                queue.add(entry.getKey());
            }
        }

        // Return the maximum Integer for the weight if the current searching path can not be reachable
        if(dist[vertex2] == Integer.MAX_VALUE)
            return Integer.MAX_VALUE + ", ";

        return generateShortestPath(vertex1, vertex2, predecessor, dist[vertex2]);
    }

    public void getShortestPath(int vertex1, int vertex2) {
        // Try to get the shortest path from vertex1 to vertex2 and from vertex2 to vertex1
        String[] resForward = dijkstra(vertex1, vertex2).split(",");
        String[] resBackward = dijkstra(vertex2, vertex1).split(",");

        int disForward = Integer.parseInt(resForward[0]);
        int disBackward = Integer.parseInt(resBackward[0]);

        System.out.println("The shortest distance path between vertex " + vertex1 + " and vertex " + vertex2 + " :");
        if(disForward < disBackward) {
            System.out.println(resForward[1]);
            System.out.println("Total Weight: " + disForward);
        } else {
            System.out.println(resBackward[1]);
            System.out.println("Total Weight: " + disBackward);
        }
    }
}
class Solution {
   public static void main(String argv) {
        String[] parameters = argv.split(" ");
        int vertexNum, edgeNum;

        try {
            vertexNum = Integer.parseInt(parameters[2]);
            edgeNum = Integer.parseInt(parameters[4]);
        } catch (NumberFormatException e) {
            System.out.println("Error: Please check whether you command is right format");
            System.out.println("Example: graph -N 8 -S 15");
            return;
        }

        if(edgeNum < vertexNum - 1 || edgeNum > vertexNum * (vertexNum - 1)) {
            System.out.println("Error: S should be in the range between N-1 (inclusive) to N(N-1) (inclusive)");
            return;
        }

        RandomUtil rndm = new RandomUtil(vertexNum);
        int[] vertexs = rndm.generateTwoRandomNums();

        new Graph(vertexNum, edgeNum).computeDiameter().getShortestPath(vertexs[0], vertexs[1]);

    }
}
