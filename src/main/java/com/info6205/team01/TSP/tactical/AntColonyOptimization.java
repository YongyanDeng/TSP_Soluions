package com.info6205.team01.TSP.tactical;

import java.util.*;

import com.info6205.team01.TSP.Graph.Node;
import com.info6205.team01.TSP.Graph.DirectedEdge;
import com.info6205.team01.TSP.Graph.UndirectedEdge;
import com.info6205.team01.TSP.util.Ant;
import com.info6205.team01.TSP.util.LoadDataImpl;
import com.info6205.team01.TSP.visualization.AlgorithmVisualization;
import com.info6205.team01.TSP.visualization.GraphOperation;

public class AntColonyOptimization {
    // ACO Example
    public static void main(String[] args) throws Exception {
        // Initialize variables for ACO
        LoadDataImpl ld = new LoadDataImpl();
        ChristofidesAlgorithm ca = new ChristofidesAlgorithm(ld.nodes.subList(0, 20));
        ca.run();
        List<Node> christofidesGraph = ca.getTour();
        AntColonyOptimization aco = new AntColonyOptimization(ld.nodes.subList(0, 20), christofidesGraph, 10, 100, 0.7, 1, 5);

        aco.run();
        aco.result();

        AlgorithmVisualization av = new AlgorithmVisualization(ld.nodes.subList(0, 20), aco.getGos());
        av.setSleepTime(10);
        av.showResult();
    }

    public double getMinDistance() {
        return this.bestAnt.getTourLength();
    }

    public AntColonyOptimization(List<Node> nodes, List<Node> initialSolution, int antsNum, int iterations, double evapRate, int alpha, int beta) {
        this.antsNum = antsNum;
        this.iterations = iterations;
        this.evapRate = evapRate;
        this.alpha = alpha;
        this.beta = beta;
        this.bestTourLength = Double.POSITIVE_INFINITY;
        this.ants = new ArrayList<>();
        this.bestAnt = null;

        this.N = nodes.size();
        int i = 0;
        this.nodearray = new Node[N];
        this.nodeToIndex = new HashMap<>();
        for (Node node : nodes) {
            nodearray[i] = node;
            nodeToIndex.put(node, i++);
        }

        // Initialize Pheromone Matrix;
        List<Integer> init = new ArrayList<>();
        for (Node node : initialSolution) init.add(nodeToIndex.get(node));
        initialPheromone(init);

        // Create Whole Graph & costMatrix with all nodes
        this.costMatrix = new double[N][N];
        this.originalGraph = buildGraph();

        this.gos = new ArrayList<>();
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

    private void initialPheromone(List<Integer> initialSolution) {
        this.pheromoneMatrix = new double[N][N];
        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {
                pheromoneMatrix[r][c] = 1.0 / (double) (N * N);
            }
        }

        for (int k = 1; k < initialSolution.size(); k++) {
            int from = initialSolution.get(k - 1), to = initialSolution.get(k);
            pheromoneMatrix[from][to] = 10 * (1.0 / (double) (N * N));
            pheromoneMatrix[to][from] = 10 * (1.0 / (double) (N * N));
        }
        pheromoneMatrix[initialSolution.get(0)][initialSolution.get(initialSolution.size() - 1)] = 10 * (1.0 / (double) (N * N));
        pheromoneMatrix[initialSolution.get(initialSolution.size() - 1)][initialSolution.get(0)] = 10 * (1.0 / (double) (N * N));
    }

    // Execute ACO
    public void run() {
        for (int i = 0; i < iterations; i++) {
            ants.clear();

            for (int j = 0; j < antsNum; j++) {
                Ant ant = new Ant(N, costMatrix, pheromoneMatrix, alpha, beta);
                ant.buildTour();
                ants.add(ant);
                if (bestAnt == null || ant.getTourLength() < bestAnt.getTourLength()) {
                    if (bestAnt != null) devisualize(bestAnt.getTour());
                    bestAnt = ant;
                    visualize(bestAnt.getTour());
                }
            }
            updatePheromoneMatrix();
        }
    }

    private void updatePheromoneMatrix() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                pheromoneMatrix[i][j] *= evapRate;
            }
        }

        for (Ant ant : ants) {
            List<Integer> tour = ant.getTour();
            double contribution = 10 / ant.getTourLength();

            for (int i = 0; i < tour.size() - 1; i++) {
                int city1 = tour.get(i);
                int city2 = tour.get(i + 1);
                pheromoneMatrix[city1][city2] += contribution;
                pheromoneMatrix[city2][city1] += contribution;
            }
        }
    }

    private void visualize(List<Integer> tour) {
        for (int i = 1; i < tour.size(); i++) {
            Node from = nodearray[tour.get(i - 1)];
            Node to = nodearray[tour.get(i)];
            UndirectedEdge ue = new UndirectedEdge(from, to);
            addEdge(ue);
        }
        Node from = nodearray[tour.get(tour.size() - 1)], to = nodearray[(tour.get(0))];
        addEdge(new UndirectedEdge(from, to));
    }

    private void devisualize(List<Integer> tour) {
        Node f = nodearray[tour.get(tour.size() - 1)], t = nodearray[tour.get(0)];
        removeEdge(new UndirectedEdge(f, t));

        for (int i = tour.size() - 1; i >= 1; i--) {
            Node from = nodearray[tour.get(i - 1)], to = nodearray[tour.get(i)];
            UndirectedEdge ue = new UndirectedEdge(from, to);
            removeEdge(ue);
        }
    }

    private void addEdge(UndirectedEdge ue) {
        gos.add(GraphOperation.addEdge(ue));
        gos.add(GraphOperation.highlightEdge(ue));
        gos.add(GraphOperation.unhighlightEdge(ue));
    }

    private void removeEdge(UndirectedEdge ue) {
        gos.add(GraphOperation.highlightEdge(ue));
        gos.add(GraphOperation.removeEdge(ue));
        gos.add(GraphOperation.unhighlightEdge(ue));
    }

    public List<GraphOperation> getGos() {
        return gos;
    }

    public void result() {
        System.out.println("ACO Algorithm's best tour length: " + bestAnt.getTourLength());
    }

    public List<Node> getNodes() {
        return Arrays.asList(nodearray);
    }

    public List<Node> getTour() {
        List<Node> tour = new ArrayList<>();
        for (Integer i : bestAnt.getTour()) tour.add(nodearray[i]);
        return tour;
    }

    private Map<Node, List<DirectedEdge>> originalGraph;
    private Map<Node, Integer> nodeToIndex;
    private Node[] nodearray;
    private int N;

    private int antsNum;      // Number of ants
    private int iterations;       // Number of iterations (t_max)
    private double evapRate;         // Pheromone evaporation rate
    private int alpha, beta; // Influence of pheromone trail (alpha) and heuristic information (beta)
    double[][] pheromoneMatrix;
    private double[][] costMatrix;
    private List<Ant> ants;
    private Ant bestAnt;

    double bestTourLength;

    List<GraphOperation> gos;
}
