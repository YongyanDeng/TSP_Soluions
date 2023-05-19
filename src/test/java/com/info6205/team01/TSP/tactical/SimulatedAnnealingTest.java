package com.info6205.team01.TSP.tactical;

import com.info6205.team01.TSP.Graph.Node;
import com.info6205.team01.TSP.strategic.TwoOpt;
import com.info6205.team01.TSP.util.LoadData;
import com.info6205.team01.TSP.util.LoadDataImpl;
import com.info6205.team01.TSP.visualization.AlgorithmVisualization;
import com.info6205.team01.TSP.visualization.GraphOperation;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class SimulatedAnnealingTest {
    LoadData loadData;
    double[][] cityCoordinates;

    /**
     * Check node number in path
     */
    @Test
    public void pathTest1() throws Exception {
        LoadData loadData = new LoadDataImpl("src/test/java/com/info6205/team01/TSP/resources/testData15.csv");
        TSPNearestNeighbor tspNearestNeighbor = new TSPNearestNeighbor(loadData);
        tspNearestNeighbor.findShortestPath();
        SimulatedAnnealing simulatedAnnealing = new SimulatedAnnealing(tspNearestNeighbor.getTour(), loadData);
        simulatedAnnealing.optimize();
        assertTrue(simulatedAnnealing.getTour().size() == 15);
    }

    /**
     * Check all nodes are used
     */
    @Test
    public void pathTest2() throws Exception {
        LoadData loadData = new LoadDataImpl("src/test/java/com/info6205/team01/TSP/resources/testData15.csv");
        TSPNearestNeighbor tspNearestNeighbor = new TSPNearestNeighbor(loadData);
        tspNearestNeighbor.findShortestPath();
        SimulatedAnnealing simulatedAnnealing = new SimulatedAnnealing(tspNearestNeighbor.getTour(), loadData);
        simulatedAnnealing.optimize();
        for (int i = 0; i < loadData.length; i++) {
            assertTrue(simulatedAnnealing.getTour().contains(loadData.nodes.get(i)));
        }
    }

    /**
     * Check graph
     */
    @Test
    public void checkGraph() throws Exception {
        LoadData loadData = new LoadDataImpl("src/test/java/com/info6205/team01/TSP/resources/testData15.csv");
        GreedyHeuristic greedyHeuristic = new GreedyHeuristic(loadData.nodes);
        SimulatedAnnealing simulatedAnnealing = new SimulatedAnnealing(greedyHeuristic.getTour(), loadData, 1000, 0.995);
        simulatedAnnealing.optimize();
//        visualization(loadData.nodes, greedyHeuristic.getGos(), null, 5);
        visualization(loadData.nodes, simulatedAnnealing.getGos(), greedyHeuristic.getGos(), 300);
//        visualization(loadData.nodes, null, simulatedAnnealing.getShortGos(), 100);
        Thread.sleep(5000);
    }

    private void visualization(List<Node> nodes, List<GraphOperation> gos, List<GraphOperation> oldGos, int interval) {
        // Build av
        AlgorithmVisualization av = new AlgorithmVisualization(nodes, gos, oldGos);
        // You can set sleep time
        // default: 500
        av.setSleepTime(interval);
        av.showResult();
    }

}
