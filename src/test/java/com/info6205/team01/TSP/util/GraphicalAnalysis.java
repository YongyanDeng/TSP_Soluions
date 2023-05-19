package com.info6205.team01.TSP.util;

import java.util.*;

import com.info6205.team01.TSP.Graph.Node;
import com.info6205.team01.TSP.Graph.UndirectedEdge;
import com.info6205.team01.TSP.tactical.AntColonyAlgorithm;
import com.info6205.team01.TSP.tactical.ChristofidesAlgorithm;
import com.info6205.team01.TSP.visualization.AlgorithmVisualization;
import com.info6205.team01.TSP.visualization.GraphOperation;

public class GraphicalAnalysis {
    private List<Node> nodes;
    private ChristofidesAlgorithm solver;
    private List<GraphOperation> gos, newGos;

    public static void main(String args[]) throws Exception {
        LoadDataImpl ld = new LoadDataImpl();

        GraphicalAnalysis ga = new GraphicalAnalysis(ld.nodes);
        ga.visualize();

        AlgorithmVisualization av = new AlgorithmVisualization(ld.nodes, ga.getNewGos(), ga.getGos());
        av.setSleepTime(10);
        av.showResult();
    }

    public GraphicalAnalysis(List<Node> nodes) {
        this.nodes = nodes;
        gos = new ArrayList<>();
        newGos = new ArrayList<>();

        solver = new ChristofidesAlgorithm(this.nodes);
        solver.run();
//        solver.result();
    }

    public void visualize() {
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = i + 1; j < nodes.size(); j++) {
                Node a = nodes.get(i), b = nodes.get(j);
                gos.add(GraphOperation.addEdge(new UndirectedEdge(a, b)));
            }
        }

        List<Node> tour = solver.getTour();
        for (int i = 1; i < tour.size(); i++) {
            Node a = tour.get(i - 1), b = tour.get(i);
            newGos.add(GraphOperation.highlightEdge(new UndirectedEdge(a, b)));
        }
        newGos.add(GraphOperation.highlightEdge(new UndirectedEdge(tour.get(tour.size() - 1), tour.get(0))));
    }

    public List<GraphOperation> getGos() {
        return gos;
    }

    public List<GraphOperation> getNewGos() {
        return newGos;
    }
}
