package com.info6205.team01.TSP;

import com.info6205.team01.TSP.Graph.Node;
import com.info6205.team01.TSP.strategic.RandomSwapping;
import com.info6205.team01.TSP.strategic.ThreeOpt;
import com.info6205.team01.TSP.strategic.TwoOpt;
import com.info6205.team01.TSP.tactical.*;
import com.info6205.team01.TSP.util.LoadCSVData;
import com.info6205.team01.TSP.util.LoadDataImpl;
import com.info6205.team01.TSP.util.MST;
import com.info6205.team01.TSP.visualization.AlgorithmVisualization;
import com.info6205.team01.TSP.visualization.GraphOperation;
import com.info6205.team01.TSP.visualization.TestVis;

import java.util.ArrayList;
import java.util.List;

public class TSPSolver {

    LoadDataImpl loadCSVData;
    Boolean hasVisual = false;
    Boolean showFinalGraph = true;
    double minCost;

    private void NNAndOptimization() {
        // ========== NN ============
        TSPNearestNeighbor tspNearestNeighbor = new TSPNearestNeighbor(loadCSVData);
        tspNearestNeighbor.findShortestPath();
        if (hasVisual)
            visualization(loadCSVData.nodes, tspNearestNeighbor.getGos(), null, 5);
        System.out.printf("%s dis: %10.2f, ratio: %.2f%%\n", "Nearest Neighbor", tspNearestNeighbor.getMinDistance(), (tspNearestNeighbor.getMinDistance() - minCost) / minCost * 100);
//        System.out.printf("%s dis: %d, ratio: %f", );
        if(showFinalGraph) {
            TestVis tv = new TestVis("Nearest Neighbor");
            tv.showResult(tspNearestNeighbor.getTour());
        }
        // NN + Aco
        AntColonyOptimization nnAco = new AntColonyOptimization(loadCSVData.nodes, tspNearestNeighbor.getTour(), 10, 100, 0.7, 1, 5);
        nnAco.run();
        List<Node> nnacoTour = new ArrayList<>(nnAco.getTour());
        System.out.printf("%s dis: %10.2f, ratio: %.2f%%\n", "Nearest Neighbor + Aco ", nnAco.getMinDistance(), (nnAco.getMinDistance() - minCost) / minCost * 100);
        if(showFinalGraph) {
            TestVis tv = new TestVis("Nearest Neighbor + Aco");
            tv.showResult(nnacoTour);
        }
        // NN + opt2
        TwoOpt nnTwoOpt = optimizeWithTwoOpt(tspNearestNeighbor.getTour(), loadCSVData);
        System.out.printf("%s dis: %10.2f, ratio: %.2f%%\n", "Nearest Neighbor + opt2", nnTwoOpt.getMinDistance(), (nnTwoOpt.getMinDistance() - minCost) / minCost * 100);
        if (hasVisual)
            visualization(loadCSVData.nodes, nnTwoOpt.getGos(), tspNearestNeighbor.getGos(), 5);
        if(showFinalGraph) {
            TestVis tv = new TestVis("Nearest Neighbor + opt2");
            tv.showResult(nnTwoOpt.getTour());
        }
        // NN + 2opt + opt3
        ThreeOpt nnThreeOpt = optimizeWithThreeOpt(nnTwoOpt.getTour(), loadCSVData);
        System.out.printf("%s dis: %10.2f, ratio: %.2f%%\n", "Nearest Neighbor + 2opt + opt3", nnThreeOpt.getMinDistance(), (nnThreeOpt.getMinDistance() - minCost) / minCost * 100);
        if (hasVisual) {
            List<GraphOperation> origin = new ArrayList<>();
            origin.addAll(tspNearestNeighbor.getGos());
            origin.addAll(nnTwoOpt.getGos());
            visualization(loadCSVData.nodes, nnThreeOpt.getGos(), origin, 1);
        }
        if(showFinalGraph) {
            TestVis tv = new TestVis("Nearest Neighbor + 2opt + opt3");
            tv.showResult(nnThreeOpt.getTour());
        }
        // NN + SimulatedAnnealing
        SimulatedAnnealing nnSimulatedAnnealing = optimizeWithSimulatedAnnealing(tspNearestNeighbor.getTour(), loadCSVData);
        System.out.printf("%s dis: %10.2f, ratio: %.2f%%\n", "Nearest Neighbor + Simulated Annealing", nnSimulatedAnnealing.getMinDistance(), (nnSimulatedAnnealing.getMinDistance() - minCost) / minCost * 100);
        if (hasVisual) {
            // whole process take 6 hours
            /*List<GraphOperation> origin = new ArrayList<>();
            origin.addAll(tspNearestNeighbor.getGos());
            origin.addAll(nnTwoOpt.getGos());
            visualization(loadCSVData.nodes, nnSimulatedAnnealing.getGos(), origin, 1);*/
            // just result
            visualization(loadCSVData.nodes, null, nnSimulatedAnnealing.getShortGos(), 1);
        }
        if(showFinalGraph) {
            TestVis tv = new TestVis("Nearest Neighbor + Simulated Annealing");
            tv.showResult(nnSimulatedAnnealing.getTour());
        }
        // NN + aco + opt2 + opt3 + SimulatedAnnealing
        TwoOpt nnAcoTwoOpt = optimizeWithTwoOpt(nnacoTour, loadCSVData);
        System.out.printf("%s dis: %10.2f, ratio: %.2f%%\n", "Nearest Neighbor + aco + opt2", nnAcoTwoOpt.getMinDistance(), (nnAcoTwoOpt.getMinDistance() - minCost) / minCost * 100);
//        if (hasVisual)
//            visualization(loadCSVData.nodes, nnAcoTwoOpt.getGos(), tspNearestNeighbor.getGos(), 1);
        if(showFinalGraph) {
            TestVis tv = new TestVis("Nearest Neighbor + aco + opt2");
            tv.showResult(nnAcoTwoOpt.getTour());
        }
        ThreeOpt nnAcoTwoOptThreeOpt = optimizeWithThreeOpt(nnAcoTwoOpt.getTour(), loadCSVData);
        System.out.printf("%s dis: %10.2f, ratio: %.2f%%\n", "Nearest Neighbor + aco + opt2 + opt3", nnAcoTwoOptThreeOpt.getMinDistance(), (nnAcoTwoOptThreeOpt.getMinDistance() - minCost) / minCost * 100);
//        if (hasVisual) {
//            List<GraphOperation> origin = new ArrayList<>();
//            origin.addAll(tspNearestNeighbor.getGos());
//            origin.addAll(nnTwoOpt.getGos());
//            visualization(loadCSVData.nodes, nnThreeOpt.getGos(), origin, 1);
//        }
        if(showFinalGraph) {
            TestVis tv = new TestVis("Nearest Neighbor + aco + opt2 + opt3");
            tv.showResult(nnAcoTwoOptThreeOpt.getTour());
        }
        SimulatedAnnealing nnAcoTwoOptSimulatedThreeOptAnnealing = optimizeWithSimulatedAnnealing(nnAcoTwoOptThreeOpt.getTour(), loadCSVData);
        System.out.printf("%s dis: %10.2f, ratio: %.2f%%\n", "Nearest Neighbor + aco + opt2 + opt3 + Simulated Annealing", nnAcoTwoOptSimulatedThreeOptAnnealing.getMinDistance(), (nnAcoTwoOptSimulatedThreeOptAnnealing.getMinDistance() - minCost) / minCost * 100);
//        if (hasVisual) {
//            // whole process take 6 hours
//            /*List<GraphOperation> origin = new ArrayList<>();
//            origin.addAll(tspNearestNeighbor.getGos());
//            origin.addAll(nnTwoOpt.getGos());
//            visualization(loadCSVData.nodes, nnSimulatedAnnealing.getGos(), origin, 1);*/
//            // just result
//            visualization(loadCSVData.nodes, null, nnSimulatedAnnealing.getShortGos(), 1);
//        }
        if(showFinalGraph) {
            TestVis tv = new TestVis("Nearest Neighbor + aco + opt2 + opt3 + Simulated Annealing");
            tv.showResult(nnAcoTwoOptSimulatedThreeOptAnnealing.getTour());
        }
//        // NN + randomSwap
//        RandomSwapping nnRs = new RandomSwapping(tspNearestNeighbor.getTour());
//        System.out.printf("%s dis: %10.2f, ratio: %.2f%%\n", "Nearest Neighbor + random swap", nnRs.getMinDistance(), (nnRs.getMinDistance() - minCost) / minCost * 100);
//        if (hasVisual)
//            visualization(loadCSVData.nodes, nnRs.getGos(), tspNearestNeighbor.getGos(), 5);
    }

    private void GHAndOptimization() {
        GreedyHeuristic gh = new GreedyHeuristic(loadCSVData.nodes);
        List<Node> tourOfGH = gh.getTour();
        System.out.printf("%s dis: %10.2f, ratio: %.2f%%\n", "Greedy Heuristic", gh.getMinDistance(), (gh.getMinDistance() - minCost) / minCost * 100);
        if (hasVisual)
            visualization(loadCSVData.nodes, gh.getGos(), null, 5);
        if(showFinalGraph) {
            TestVis tv = new TestVis("Greedy Heuristic");
            tv.showResult(gh.getTour());
        }
        // GH + Aco
        AntColonyOptimization ghAco = new AntColonyOptimization(loadCSVData.nodes, tourOfGH, 10, 100, 0.7, 1, 5);
        ghAco.run();
        List<Node> ghacoTour = new ArrayList<>(ghAco.getTour());
        System.out.printf("%s dis: %10.2f, ratio: %.2f%%\n", "Greedy Heuristic + Aco ", ghAco.getMinDistance(), (ghAco.getMinDistance() - minCost) / minCost * 100);
        if(showFinalGraph) {
            TestVis tv = new TestVis("Greedy Heuristic + Aco");
            tv.showResult(ghacoTour);
        }
        // GH + opt2
        TwoOpt ghTwoOpt = optimizeWithTwoOpt(new ArrayList<>(tourOfGH), loadCSVData);
        System.out.printf("%s dis: %10.2f, ratio: %.2f%%\n", "Greedy Heuristic + opt2", ghTwoOpt.getMinDistance(), (ghTwoOpt.getMinDistance() - minCost) / minCost * 100);
        if (hasVisual)
            visualization(loadCSVData.nodes, ghTwoOpt.getGos(), gh.getGos(), 5);
        if(showFinalGraph) {
            TestVis tv = new TestVis("Greedy Heuristic + opt2");
            tv.showResult(ghTwoOpt.getTour());
        }
        // GH + 2opt + opt3
        ThreeOpt ghThreeOpt = optimizeWithThreeOpt(ghTwoOpt.getTour(), loadCSVData);
        System.out.printf("%s dis: %10.2f, ratio: %.2f%%\n", "Greedy Heuristic + opt2 + opt3", ghThreeOpt.getMinDistance(), (ghThreeOpt.getMinDistance() - minCost) / minCost * 100);
        if (hasVisual) {
            List<GraphOperation> origin = new ArrayList<>();
            origin.addAll(gh.getGos());
            origin.addAll(ghTwoOpt.getGos());
            visualization(loadCSVData.nodes, ghThreeOpt.getGos(), origin, 1);
        }
        if(showFinalGraph) {
            TestVis tv = new TestVis("Greedy Heuristic + opt2 + opt3");
            tv.showResult(ghThreeOpt.getTour());
        }
        // GH + SimulatedAnnealing
        SimulatedAnnealing ghSimulatedAnnealing = optimizeWithSimulatedAnnealing(tourOfGH, loadCSVData);
        System.out.printf("%s dis: %10.2f, ratio: %.2f%%\n", "Greedy Heuristic + Simulated Annealing", ghSimulatedAnnealing.getMinDistance(), (ghSimulatedAnnealing.getMinDistance() - minCost) / minCost * 100);
        if (hasVisual) {
            // whole process take 6 hours
            /*List<GraphOperation> origin = new ArrayList<>();
            origin.addAll(tspNearestNeighbor.getGos());
            origin.addAll(nnTwoOpt.getGos());
            visualization(loadCSVData.nodes, nnSimulatedAnnealing.getGos(), origin, 1);*/
            // just result
            visualization(loadCSVData.nodes, null, ghSimulatedAnnealing.getShortGos(), 1);
        }
        if(showFinalGraph) {
            TestVis tv = new TestVis("Greedy Heuristic + Simulated Annealing");
            tv.showResult(ghSimulatedAnnealing.getTour());
        }

        // NN + aco + opt2 + opt3 + SimulatedAnnealing
        TwoOpt ghAcoTwoOpt = optimizeWithTwoOpt(ghacoTour, loadCSVData);
        System.out.printf("%s dis: %10.2f, ratio: %.2f%%\n", "Greedy Heuristic + aco + opt2", ghAcoTwoOpt.getMinDistance(), (ghAcoTwoOpt.getMinDistance() - minCost) / minCost * 100);
//        if (hasVisual)
//            visualization(loadCSVData.nodes, nnAcoTwoOpt.getGos(), tspNearestNeighbor.getGos(), 1);
        if(showFinalGraph) {
            TestVis tv = new TestVis("Greedy Heuristic + aco + opt2");
            tv.showResult(ghAcoTwoOpt.getTour());
        }
        ThreeOpt ghAcoTwoOptThreeOpt = optimizeWithThreeOpt(ghAcoTwoOpt.getTour(), loadCSVData);
        System.out.printf("%s dis: %10.2f, ratio: %.2f%%\n", "Greedy Heuristic + aco + opt2 + opt3", ghAcoTwoOptThreeOpt.getMinDistance(), (ghAcoTwoOptThreeOpt.getMinDistance() - minCost) / minCost * 100);
//        if (hasVisual) {
//            List<GraphOperation> origin = new ArrayList<>();
//            origin.addAll(tspNearestNeighbor.getGos());
//            origin.addAll(nnTwoOpt.getGos());
//            visualization(loadCSVData.nodes, nnThreeOpt.getGos(), origin, 1);
//        }
        if(showFinalGraph) {
            TestVis tv = new TestVis("Greedy Heuristic + aco + opt2 + opt3");
            tv.showResult(ghAcoTwoOptThreeOpt.getTour());
        }
        SimulatedAnnealing ghAcoTwoOptSimulatedThreeOptAnnealing = optimizeWithSimulatedAnnealing(ghAcoTwoOptThreeOpt.getTour(), loadCSVData);
        System.out.printf("%s dis: %10.2f, ratio: %.2f%%\n", "Greedy Heuristic + aco + opt2 + opt3 + Simulated Annealing", ghAcoTwoOptSimulatedThreeOptAnnealing.getMinDistance(), (ghAcoTwoOptSimulatedThreeOptAnnealing.getMinDistance() - minCost) / minCost * 100);
//        if (hasVisual) {
//            // whole process take 6 hours
//            /*List<GraphOperation> origin = new ArrayList<>();
//            origin.addAll(tspNearestNeighbor.getGos());
//            origin.addAll(nnTwoOpt.getGos());
//            visualization(loadCSVData.nodes, nnSimulatedAnnealing.getGos(), origin, 1);*/
//            // just result
//            visualization(loadCSVData.nodes, null, nnSimulatedAnnealing.getShortGos(), 1);
//        }
        if(showFinalGraph) {
            TestVis tv = new TestVis("Greedy Heuristic + aco + opt2 + opt3 + Simulated Annealing");
            tv.showResult(ghAcoTwoOptSimulatedThreeOptAnnealing.getTour());
        }

//        // GH + randomSwap
//        RandomSwapping ghRs = new RandomSwapping(new ArrayList<>(tourOfGH));
//        System.out.printf("%s dis: %10.2f, ratio: %.2f%%\n", "Greedy Heuristic + random swap", ghRs.getMinDistance(), (ghRs.getMinDistance() - minCost) / minCost * 100);
//        if (hasVisual)
//            visualization(loadCSVData.nodes, ghRs.getGos(), gh.getGos(), 5);
    }

    private void CAAndOptimization() throws Exception {
        loadCSVData = new LoadDataImpl();
        ChristofidesAlgorithm ca = new ChristofidesAlgorithm(loadCSVData.nodes);
        ca.run();
        List<Node> caTour = new ArrayList<>(ca.getTour());
        System.out.printf("%s dis: %10.2f, ratio: %.2f%%\n", "Christofides", ca.getMinDistance(), (ca.getMinDistance() - minCost) / minCost * 100);
        if(showFinalGraph) {
            TestVis tv = new TestVis("Christofides");
            tv.showResult(caTour);
        }
        // Christofides + Aco
        AntColonyOptimization aco = new AntColonyOptimization(loadCSVData.nodes, caTour, 10, 100, 0.7, 1, 5);
        aco.run();
        List<Node> caacoTour = new ArrayList<>(aco.getTour());
        System.out.printf("%s dis: %10.2f, ratio: %.2f%%\n", "Christofides + Aco ", aco.getMinDistance(), (aco.getMinDistance() - minCost) / minCost * 100);
        if(showFinalGraph) {
            TestVis tv = new TestVis("Christofides + Aco");
            tv.showResult(caacoTour);
        }
        // Christofides + opt2
        TwoOpt caTwoOpt = optimizeWithTwoOpt(new ArrayList<>(caTour), loadCSVData);
        System.out.printf("%s dis: %10.2f, ratio: %.2f%%\n", "Christofides + opt2", caTwoOpt.getMinDistance(), (caTwoOpt.getMinDistance() - minCost) / minCost * 100);
        if(showFinalGraph) {
            TestVis tv = new TestVis("Christofides + opt2");
            tv.showResult(caTwoOpt.getTour());
        }
        // Christofides + opt2 + opt3
        ThreeOpt caThreeOpt = optimizeWithThreeOpt(caTwoOpt.getTour(), loadCSVData);
        System.out.printf("%s dis: %10.2f, ratio: %.2f%%\n", "Christofides + opt2 + opt3", caThreeOpt.getMinDistance(), (caThreeOpt.getMinDistance() - minCost) / minCost * 100);
        if(showFinalGraph) {
            TestVis tv = new TestVis("Christofides + opt2 + opt3");
            tv.showResult(caThreeOpt.getTour());
        }
        // Christofides + SimulatedAnnealing
        SimulatedAnnealing caSimulatedAnnealing = optimizeWithSimulatedAnnealing(caTour, loadCSVData);
        System.out.printf("%s dis: %10.2f, ratio: %.2f%%\n", "Christofides + Simulated Annealing", caSimulatedAnnealing.getMinDistance(), (caSimulatedAnnealing.getMinDistance() - minCost) / minCost * 100);
        if (hasVisual) {
            // whole process take 6 hours
        /*List<GraphOperation> origin = new ArrayList<>();
        origin.addAll(tspNearestNeighbor.getGos());
        origin.addAll(nnTwoOpt.getGos());
        visualization(loadCSVData.nodes, nnSimulatedAnnealing.getGos(), origin, 1);*/
            // just result
            visualization(loadCSVData.nodes, null, caSimulatedAnnealing.getShortGos(), 1);
        }
        if(showFinalGraph) {
            TestVis tv = new TestVis("Christofides + Simulated Annealing");
            tv.showResult(caSimulatedAnnealing.getTour());
        }
        // Christofides + aco + opt2 + opt3 + SimulatedAnnealing
        TwoOpt caAcoTwoOpt = optimizeWithTwoOpt(caacoTour, loadCSVData);
        System.out.printf("%s dis: %10.2f, ratio: %.2f%%\n", "Christofides + aco + opt2", caAcoTwoOpt.getMinDistance(), (caAcoTwoOpt.getMinDistance() - minCost) / minCost * 100);
//        if (hasVisual)
//            visualization(loadCSVData.nodes, nnAcoTwoOpt.getGos(), tspNearestNeighbor.getGos(), 1);
        if(showFinalGraph) {
            TestVis tv = new TestVis("Christofides + aco + opt2");
            tv.showResult(caAcoTwoOpt.getTour());
        }
        ThreeOpt caAcoTwoOptThreeOpt = optimizeWithThreeOpt(caAcoTwoOpt.getTour(), loadCSVData);
        System.out.printf("%s dis: %10.2f, ratio: %.2f%%\n", "Christofides + aco + opt2 + opt3", caAcoTwoOptThreeOpt.getMinDistance(), (caAcoTwoOptThreeOpt.getMinDistance() - minCost) / minCost * 100);
//        if (hasVisual) {
//            List<GraphOperation> origin = new ArrayList<>();
//            origin.addAll(tspNearestNeighbor.getGos());
//            origin.addAll(nnTwoOpt.getGos());
//            visualization(loadCSVData.nodes, nnThreeOpt.getGos(), origin, 1);
//        }
        if(showFinalGraph) {
            TestVis tv = new TestVis("Christofides + aco + opt2 + opt3");
            tv.showResult(caAcoTwoOptThreeOpt.getTour());
        }
        SimulatedAnnealing caAcoTwoOptSimulatedThreeOptAnnealing = optimizeWithSimulatedAnnealing(caAcoTwoOptThreeOpt.getTour(), loadCSVData);
        System.out.printf("%s dis: %10.2f, ratio: %.2f%%\n", "Christofides + aco + opt2 + opt3 + Simulated Annealing", caAcoTwoOptSimulatedThreeOptAnnealing.getMinDistance(), (caAcoTwoOptSimulatedThreeOptAnnealing.getMinDistance() - minCost) / minCost * 100);
//        if (hasVisual) {
//            // whole process take 6 hours
//            /*List<GraphOperation> origin = new ArrayList<>();
//            origin.addAll(tspNearestNeighbor.getGos());
//            origin.addAll(nnTwoOpt.getGos());
//            visualization(loadCSVData.nodes, nnSimulatedAnnealing.getGos(), origin, 1);*/
//            // just result
//            visualization(loadCSVData.nodes, null, nnSimulatedAnnealing.getShortGos(), 1);
//        }
        if(showFinalGraph) {
            TestVis tv = new TestVis("Christofides + aco + opt2 + opt3 + Simulated Annealing");
            tv.showResult(caAcoTwoOptSimulatedThreeOptAnnealing.getTour());
        }
        /*// Christofides + randomSwap
        RandomSwapping caRs = new RandomSwapping(new ArrayList<>(caTour));
        System.out.printf("%s dis: %10.2f, ratio: %.2f%%\n", "Christofides + random swap", caRs.getMinDistance(), (caRs.getMinDistance() - minCost) / minCost * 100);*/
    }

    public void ANTAndOptimization() throws Exception {
        loadCSVData = new LoadDataImpl();
        AntColonyAlgorithm aca = new AntColonyAlgorithm(loadCSVData.nodes, 10, 100, 0.7, 1, 5);
        // AntColonyOptimization aco = new AntColonyOptimization(loadCSVData.nodes, null, 10, 100, 0.7, 1, 5);
        aca.run();
        List<Node> acaTour = new ArrayList<>(aca.getTour());
        System.out.printf("%s dis: %10.2f, ratio: %.2f%%\n", "Aca", aca.getMinDistance(), (aca.getMinDistance() - minCost) / minCost * 100);
        if(showFinalGraph) {
            TestVis tv = new TestVis("Aca");
            tv.showResult(acaTour);
        }
        // Ant + opt2
        TwoOpt antTwoOpt = optimizeWithTwoOpt(new ArrayList<>(acaTour), loadCSVData);
        System.out.printf("%s dis: %10.2f, ratio: %.2f%%\n", "Aca + opt2", antTwoOpt.getMinDistance(), (antTwoOpt.getMinDistance() - minCost) / minCost * 100);
        if(showFinalGraph) {
            TestVis tv = new TestVis("Aca + opt2");
            tv.showResult(antTwoOpt.getTour());
        }
        // Ant + opt2 + opt3
        ThreeOpt antThreeOpt = optimizeWithThreeOpt(antTwoOpt.getTour(), loadCSVData);
        System.out.printf("%s dis: %10.2f, ratio: %.2f%%\n", "Aca + opt2 + opt3", antThreeOpt.getMinDistance(), (antThreeOpt.getMinDistance() - minCost) / minCost * 100);
        if(showFinalGraph) {
            TestVis tv = new TestVis("Aca + opt2 + opt3");
            tv.showResult(antThreeOpt.getTour());
        }
        // NN + SimulatedAnnealing
        SimulatedAnnealing antSimulatedAnnealing = optimizeWithSimulatedAnnealing(acaTour, loadCSVData);
        System.out.printf("%s dis: %10.2f, ratio: %.2f%%\n", "Aca + Simulated Annealing", antSimulatedAnnealing.getMinDistance(), (antSimulatedAnnealing.getMinDistance() - minCost) / minCost * 100);
        if (hasVisual) {
            // whole process take 6 hours
        /*List<GraphOperation> origin = new ArrayList<>();
        origin.addAll(tspNearestNeighbor.getGos());
        origin.addAll(nnTwoOpt.getGos());
        visualization(loadCSVData.nodes, nnSimulatedAnnealing.getGos(), origin, 1);*/
            // just result
            visualization(loadCSVData.nodes, null, antSimulatedAnnealing.getShortGos(), 1);
        }
        if(showFinalGraph) {
            TestVis tv = new TestVis("Aca + Simulated Annealing");
            tv.showResult(antSimulatedAnnealing.getTour());
        }
        // NN + aco + opt2 + opt3 + SimulatedAnnealing
        SimulatedAnnealing antTwoOptSimulatedThreeOptAnnealing = optimizeWithSimulatedAnnealing(antThreeOpt.getTour(), loadCSVData);
        System.out.printf("%s dis: %10.2f, ratio: %.2f%%\n", "Aca + opt2 + opt3 + Simulated Annealing", antTwoOptSimulatedThreeOptAnnealing.getMinDistance(), (antTwoOptSimulatedThreeOptAnnealing.getMinDistance() - minCost) / minCost * 100);
//        if (hasVisual) {
//            // whole process take 6 hours
//            /*List<GraphOperation> origin = new ArrayList<>();
//            origin.addAll(tspNearestNeighbor.getGos());
//            origin.addAll(nnTwoOpt.getGos());
//            visualization(loadCSVData.nodes, nnSimulatedAnnealing.getGos(), origin, 1);*/
//            // just result
//            visualization(loadCSVData.nodes, null, nnSimulatedAnnealing.getShortGos(), 1);
//        }
        if(showFinalGraph) {
            TestVis tv = new TestVis("Aca + opt2 + opt3 + Simulated Annealing");
            tv.showResult(antTwoOptSimulatedThreeOptAnnealing.getTour());
        }
        // Ant + randomSwap
        /*RandomSwapping antRs = new RandomSwapping(new ArrayList<>(acaTour));
        System.out.printf("%s dis: %10.2f, ratio: %.2f%%\n", "Aca + random swap", antRs.getMinDistance(), (antRs.getMinDistance() - minCost) / minCost * 100);*/
    }


    public void solve() throws Exception {
        // data transform
        loadCSVData = new LoadDataImpl();
        double[][] adjacencyMatrix = loadCSVData.adjacencyMatrix;
        double[][] cityCoordinates = loadCSVData.coordination;
        // get lower boundary
        MST mst = new MST();
        minCost = mst.minCostConnectPoints(cityCoordinates);
        System.out.println("Minimum cost: " + minCost);

        // ============ NN ===========
        NNAndOptimization();
       // ============ GH ===========
        GHAndOptimization();

        // ========== Christofides ============
        CAAndOptimization();

        // ========== Ant ==========
        ANTAndOptimization(); 
    }


    private TwoOpt optimizeWithTwoOpt(List<Node> tour, LoadDataImpl ld) {
        TwoOpt twoOpt = new TwoOpt(tour, ld);
        twoOpt.optimize();
        return twoOpt;
    }

    private SimulatedAnnealing optimizeWithSimulatedAnnealing(List<Node> tour, LoadDataImpl ld) {
        SimulatedAnnealing simulatedAnnealing = new SimulatedAnnealing(tour, ld);
        simulatedAnnealing.optimize();
        return simulatedAnnealing;
    }

    private ThreeOpt optimizeWithThreeOpt(List<Node> tour, LoadDataImpl ld) {
        ThreeOpt threeOpt = new ThreeOpt(tour, ld);
        threeOpt.optimize();
        return threeOpt;
    }

    private void visualization(List<Node> nodes, List<GraphOperation> gos, List<GraphOperation> oldGos, int interval) {
        AlgorithmVisualization av;
        // Build av
        if (oldGos != null)
            av = new AlgorithmVisualization(nodes, gos, oldGos);
        else
            av = new AlgorithmVisualization(nodes, gos);
        // You can set sleep time
        // default: 500
        av.setSleepTime(interval);
        av.showResult();
    }

    public static void main(String[] args) throws Exception {
        TSPSolver tspSolver = new TSPSolver();
        tspSolver.solve();
    }

}
