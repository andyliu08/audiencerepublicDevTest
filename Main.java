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
    public Graph(int vertexNumber, int edgeNumber) {
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
        // add more random edges until the edge number meet the expectation
        addRandomEdges(vertexList, this.edgeNumber - edgeNumberCreated);
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

    private void addRandomEdges(List<Integer> list, int restEdges) {
        while(restEdges > 0) {
            int cur = list.get(this.rndmVertex.generateRandomNum());
            int next = list.get(this.rndmVertex.generateRandomNum());
            if(cur == next)
                continue;
            if(!this.graph.get(cur).containsKey(next)) {
                this.graph.get(cur).putIfAbsent(next, this.rndmWeight.generateRandomNum());
                restEdges--;
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

    int maxWeight = 0;
    public Graph computeDiameter() {
        int radius = 0;
        int diameter = 0;
        System.out.println("The eccentricity of all the Vertex: ");
        for(int i = 1; i <= this.vertexNumber; i++) {
            LinkedList<Integer> currentPath = new LinkedList<>();
            currentPath.add(i);
            maxWeight = 0;
            int eccentricity = computeEcce(i, currentPath, 0);
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

    private int computeEcce(int start, LinkedList<Integer> currentPath, int dist) {
        HashMap<Integer, Integer> next = graph.get(start);
        if(next ==null || next.isEmpty())
            return maxWeight;

        for (Map.Entry<Integer, Integer> entry : next.entrySet()) {
            int nextVer = entry.getKey();
            int weight = entry.getValue();

            if(currentPath.contains(nextVer)) continue;

            currentPath.add(nextVer);
            dist += weight;
            maxWeight = Math.max(maxWeight, dist);
            computeEcce(nextVer, currentPath, dist);
            currentPath.removeLast();
            dist -= weight;
        }

        return maxWeight;
    }
    
        private String reversePath(int s, int t, int[] predecessor) {
        StringBuilder sb = new StringBuilder();
        while(s != t) {
            sb.append(" ").append(t);
            t = predecessor[t];
        }

        String[] sbs = sb.toString().trim().split(" ");
        StringBuilder revsb = new StringBuilder();
        for(int i = sbs.length - 1; i >= 0; i--) {
            revsb.append(" -> " ).append(sbs[i]);
        }

         return revsb.toString();
    }

    private String generateShortestPath(int s, int t, int[] predecessor, int weight) {
        return weight + ", " + s + reversePath(s, t, predecessor);
    }

    private String dijkstra(int s, int t) {
        int v = graph.size() + 1;
        int[] dist = new int[v];
        boolean[] visited = new boolean[v];
        for (int i = 0; i < v; i++) {
            dist[i] = Integer.MAX_VALUE;
        }
        int[] predecessor = new int[v];
        Queue<Integer> queue = new LinkedList<>();
        queue.add(s);
        dist[s] = 0;
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

        if(dist[t] == Integer.MAX_VALUE)
            return Integer.MAX_VALUE + ", ";

        return generateShortestPath(s, t, predecessor, dist[t]);
    }

    public void getShortestPath(int s, int t) {
        String[] resForward = dijkstra(s, t).split(",");
        String[] resBackward = dijkstra(t, s).split(",");

        int disForward = Integer.parseInt(resForward[0]);
        int disBackward = Integer.parseInt(resBackward[0]);

        System.out.println("The shortest distance path between vertex " + s + " and vertex " + t + " :");
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
   
        final int vertexNum = Integer.parseInt(parameters[2]);
        final int edgeNum = Integer.parseInt(parameters[4]);
        RandomUtil rndm = new RandomUtil(vertexNum);
        int[] nodes = rndm.generateTwoRandomNums();

        new Graph(vertexNum, edgeNum).computeDiameter().getShortestPath(nodes[0], nodes[1]);

    }
}

