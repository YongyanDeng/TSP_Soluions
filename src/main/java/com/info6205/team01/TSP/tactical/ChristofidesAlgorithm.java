package com.info6205.team01.TSP.tactical;

import java.util.*;

import com.info6205.team01.TSP.Graph.DirectedEdge;
import com.info6205.team01.TSP.Graph.Node;
import com.info6205.team01.TSP.Graph.UndirectedEdge;
import com.info6205.team01.TSP.util.*;
import com.info6205.team01.TSP.visualization.AlgorithmVisualization;
import com.info6205.team01.TSP.visualization.GraphOperation;

public class ChristofidesAlgorithm {
    double resultDistance;

    public List<GraphOperation> getGos() {
        return gos;
    }

    public static void main(String[] args) throws Exception {
        // Build original graph with "List<Node> nodes"
        LoadDataImpl ld = new LoadDataImpl();
        ChristofidesAlgorithm ca = new ChristofidesAlgorithm(ld.nodes);

        ca.setGenerateGos(true);
        ca.run();
        List<Node> hamiltonianCircuit = ca.getHamiltonianCircuit();
        AlgorithmVisualization av = new AlgorithmVisualization(ld.nodes, ca.gos);
        av.showResult();
        // Output graph of Hamiltonian Circuit
        if (hamiltonianCircuit != null) System.out.println("Finish");
    }

    public ChristofidesAlgorithm(List<Node> nodes) {
        this.N = nodes.size();
        this.nodearray = new Node[N];
        this.costMatrix = new double[N][N];
        this.nodeToIndex = new HashMap<>();
        int i = 0;
        for (Node node : nodes) {
            nodearray[i] = node;
            nodeToIndex.put(node, i++);
        }

        // Create Whole Graph with all nodes
        this.originalGraph = buildGraph();

        gos = new ArrayList<>();
    }

    private Map<Node, List<DirectedEdge>> buildGraph() {
        Map<Node, List<DirectedEdge>> graph = new HashMap<>();
        for (int i = 0; i < N; i++) {
            Node node1 = nodearray[i];
            for (int j = i + 1; j < N; j++) {
                Node node2 = nodearray[j];
                // node1 -> node2
                if (!graph.containsKey(node1)) graph.put(node1, new ArrayList<>());
                DirectedEdge de1 = new DirectedEdge(node1, node2);
                graph.get(node1).add(de1);
                costMatrix[i][j] = de1.getWeight();
                // node2 -> node1
                if (!graph.containsKey(node2)) graph.put(node2, new ArrayList<>());
                DirectedEdge de2 = new DirectedEdge(node2, node1);
                graph.get(node2).add(de2);
                costMatrix[j][i] = de2.getWeight();
            }
        }
        return graph;
    }

    public void run() {
        // Build MST with Prim Algorithm;
        Map<Node, List<DirectedEdge>> mst = Prim();

        // Visualization MST
        if (generateGos) {
            for (List<DirectedEdge> edges : mst.values()) {
                for (DirectedEdge edge : edges) {
                    GraphOperation go = GraphOperation.addEdge(edge.getFrom(), edge.getTo()).turnOffDelay();
                    if (!gos.contains(go)) {
                        gos.add(go);
                    }
                }
            }
        }

        // Construct subgraph with "odd-degree nodes" in MST
        Map<Node, List<DirectedEdge>> oddDegreeSubgraph = oddDegreeSubgraph(mst);

        // Minimum-Weighted Match for odd-degree subgraph with Blossom Algorithm
        Map<Node, Node> matches = minimumWeightedMatch(oddDegreeSubgraph);


        // Combine MST and minimum-weighted matching to a multigraph
        Map<Node, List<DirectedEdge>> multiGraph = combine(mst, matches);


        // Find Eulerian Circuit in multiGraph
        List<Node> eulerianCircuit = findEulerianCircuit(multiGraph);

        // Convert Eulerian Circuit to Hamiltonian Circuits
        this.hamiltonianCircuit = convertHamiltonianCircuit(eulerianCircuit);
        if (generateGos) {
            for (int i = 1; i < hamiltonianCircuit.size(); i++) {
                Node node1 = hamiltonianCircuit.get(i - 1);
                Node node2 = hamiltonianCircuit.get(i);
                GraphOperation go = GraphOperation.addEdge(node1, node2).setLayout(GraphOperation.Layout.HIGHLIGHT);
                if (gos.contains(go)) {
                    gos.add(GraphOperation.highlightEdge(node1, node2));
                } else {
                    gos.add(go);
                }
            }
        }
        // Path length of Hamiltonian Circuit;
        double sum = 0.0;
        if (hamiltonianCircuit != null) {
            for (int i = 1; i < hamiltonianCircuit.size(); i++) {
                Node from = hamiltonianCircuit.get(i - 1), to = hamiltonianCircuit.get(i);
                DirectedEdge e = new DirectedEdge(from, to);
                sum += e.getWeight();
            }
            this.resultDistance = sum;
        } else System.out.println("Hamiltonian Circult is NULL!");
    }

    private Map<Node, List<DirectedEdge>> Prim() {
        double[] dist = new double[N];
        Arrays.fill(dist, Double.POSITIVE_INFINITY);

        boolean[] st = new boolean[N];
        int[] parent = new int[N];

        PriorityQueue<DirectedEdge> heap = new PriorityQueue<>((a, b) -> Double.compare(a.getWeight(), b.getWeight()));
        heap.offer(new DirectedEdge(new Node("-1", 0, 0), nodearray[0], 0));
        dist[0] = 0.0;

        while (!heap.isEmpty()) {
            DirectedEdge e = heap.poll();
            int u = nodeToIndex.get(e.getTo());

            if (st[u]) continue;

            st[u] = true;
            parent[u] = nodeToIndex.get(e.getFrom()) == null ? -1 : nodeToIndex.get(e.getFrom());

            for (int v = 0; v < N; v++) {
                if (costMatrix[u][v] != 0 && !st[v] && costMatrix[u][v] < dist[v]) {
                    dist[v] = costMatrix[u][v];
                    heap.add(new DirectedEdge(nodearray[u], nodearray[v], dist[v]));
                }
            }
        }

        Map<Node, List<DirectedEdge>> mst = new HashMap<>();
        for (Node node : nodearray) mst.put(node, new ArrayList<>());

        for (int i = 1; i < N; i++) {
            mst.get(nodearray[i]).add(
                    new DirectedEdge(nodearray[i], nodearray[parent[i]], costMatrix[i][parent[i]])
            );
            mst.get(nodearray[parent[i]]).add(
                    new DirectedEdge(nodearray[parent[i]], nodearray[i], costMatrix[parent[i]][i])
            );
        }
        return mst;
    }

    // Construct Subgraph with "odd-degrees nodes" in MST
    private Map<Node, List<DirectedEdge>> oddDegreeSubgraph(Map<Node, List<DirectedEdge>> MST) {
        // find all "odd-degree nodes" in MST
        Set<Node> oddNodes = new HashSet<>();
        for (Node node : MST.keySet()) {
            if (MST.get(node).size() % 2 == 1) oddNodes.add(node);
        }

        // Construct subgraph with original graph
        Map<Node, List<DirectedEdge>> subgraph = new HashMap<>();
        for (Node node : oddNodes)
            subgraph.put(node, new ArrayList<>());

        for (Node from : oddNodes) {
            for (DirectedEdge e : originalGraph.get(from)) {
                Node to = e.getTo();
                if (oddNodes.contains(to)) {
                    subgraph.get(from).add(e);
                }
                ;
            }
        }

        return subgraph;
    }

    // Construct Minimum Weighted Perfect Matching;
    private Map<Node, Node> minimumWeightedMatch(Map<Node, List<DirectedEdge>> graph) {
        MinimumWeightMatch mwm = new MinimumWeightMatch(graph);
        Map<Node, Node> matches = mwm.getMatches();
        return matches;
    }

    // Combine matches and MST to build a multigraph
    private Map<Node, List<DirectedEdge>> combine(Map<Node, List<DirectedEdge>> MST, Map<Node, Node> matches) {
        Map<Node, List<DirectedEdge>> multiGraph = new HashMap<>();

        // Must use deep copy
        for (Node node : MST.keySet()) {
            multiGraph.put(node, new ArrayList<>());
            for (DirectedEdge e : MST.get(node)) {
                multiGraph.get(node).add(new DirectedEdge(e));
            }
        }
        Integer gosLen = null;
        if (generateGos) {
            gosLen = gos.size();
        }
        matches.forEach((k, v) -> {
            DirectedEdge edge = new DirectedEdge(k, v);
            multiGraph.get(k).add(edge);
            if (generateGos) {
                if (!gos.contains(GraphOperation.addEdge(edge.getFrom(), edge.getTo()).setLayout(GraphOperation.Layout.HIGHLIGHT)))
                    gos.add(GraphOperation.addEdge(edge.getFrom(), edge.getTo()).setLayout(GraphOperation.Layout.HIGHLIGHT));
                gos.add(GraphOperation.highlightEdge(edge.getFrom(), edge.getTo()));
            }

        });
        if (generateGos) {
            int end = gos.size();
            for (int i = gosLen - 1; i < end; i++) {
                GraphOperation go = gos.get(i);
                UndirectedEdge edge = go.getEdge();
                gos.add(GraphOperation.unhighlightEdge(edge).turnOffDelay());
            }
        }
        return multiGraph;
    }

    // Form Eulerian Circuit
    private List<Node> findEulerianCircuit(Map<Node, List<DirectedEdge>> multiGraph) {
        Map<Node, List<DirectedEdge>> graph = new HashMap<>();

        // Must use deep copy
        for (Node node : multiGraph.keySet()) {
            graph.put(node, new ArrayList<>());
            for (DirectedEdge e : multiGraph.get(node)) {
                graph.get(node).add(new DirectedEdge(e));
            }
        }

        EulerianCircuit ec = new EulerianCircuit(graph, nodeToIndex, nodearray);
        List<Node> circuit = ec.getEulerianCircuit();
        return circuit;
    }

    // Convert Eulerian Circuit to Hamiltonian Circuit
    private List<Node> convertHamiltonianCircuit(List<Node> eulerianCircuit) {
        if (eulerianCircuit == null) return null;

        HamiltonianCircuit hc = new HamiltonianCircuit(eulerianCircuit);
        List<Node> hamiltonianCircuit = hc.convertEulerianToHamiltonian();
        return hamiltonianCircuit;
    }


    public List<Node> getTour() {
        int n = this.hamiltonianCircuit.size();
        return this.hamiltonianCircuit.subList(0, n - 1);
    }


    public double getMinDistance() {
        return resultDistance;
    }

    public List<Node> getHamiltonianCircuit() {
        return this.hamiltonianCircuit;
    }


    private Map<Node, List<DirectedEdge>> originalGraph;
    private Map<Node, Integer> nodeToIndex;
    private Node[] nodearray;
    private double[][] costMatrix;
    private int N;
    private List<Node> hamiltonianCircuit;

    private List<GraphOperation> gos;
    private boolean generateGos = false;

    public void setGenerateGos(boolean generateGos) {
        this.generateGos = generateGos;
    }
}
