package com.info6205.team01.TSP.tactical;

import com.info6205.team01.TSP.Graph.Node;
import com.info6205.team01.TSP.util.LoadData;
import com.info6205.team01.TSP.visualization.GraphOperation;

import java.util.*;

public class SimulatedAnnealing {

    private double temperature = 10000;
    private double coolingFactor = 0.999999;

    List<Node> tour;
    int length;
    List<GraphOperation> gos = new ArrayList<>();
    List<GraphOperation> shortGos = new ArrayList<>();
    double minDistance = 0;
    LoadData loadData;

    public SimulatedAnnealing(List<Node> nodes, LoadData loadData) {
        this.loadData = loadData;
        tour = new ArrayList<>(nodes);
        length = tour.size();
        minDistance = calculateDistance(tour);
    }

    public SimulatedAnnealing(List<Node> nodes, LoadData loadData, double temperature, double coolingFactor) {
        this.loadData = loadData;
        tour = new ArrayList<>(nodes);
        length = tour.size();
        minDistance = calculateDistance(tour);
        this.temperature = temperature;
        this.coolingFactor = coolingFactor;
    }

    public void optimize() {
        List<Node> newTour;
        List<Node> best = tour;
        int optCount = 0;

        Random random = new Random();

        for (double t = temperature; t > 1; t *= coolingFactor) {
            int i = random.nextInt(tour.size() - 2) + 1;
            int j = random.nextInt(tour.size() - i - 1) + i + 1;
            // 2-opt swap
            newTour = twoOptSwap(tour, i, j);
            // check new distance
            double currDist = calculateDistance(tour);
            double newDist = calculateDistance(newTour);

            if (Math.random() < probability(currDist, newDist, t)) {
                int secondTo = j == length - 1 ? 0 : j + 1;
                gos.add(GraphOperation.removeEdge(tour.get(i - 1), tour.get(i)));
                gos.add(GraphOperation.removeEdge(tour.get(j), tour.get(secondTo)));
                gos.add(GraphOperation.addEdge(tour.get(i - 1), tour.get(j)));
                gos.add(GraphOperation.addEdge(tour.get(i), tour.get(secondTo)));
                tour = newTour;
            }

            if (newDist < minDistance) {
                optCount++;
                best = tour;
                minDistance = newDist;
            }
        }
//        System.out.println("SimulatedAnnealing optCount: " + optCount);
        tour = best;

        // generate short gos
        shortGos = new ArrayList<>();
        for (int i = 0; i < tour.size()-1;i++) {
            shortGos.add(GraphOperation.addEdge(tour.get(i),tour.get(i+1)));
        }
        shortGos.add(GraphOperation.addEdge(tour.get(length-1),tour.get(0)));
    }

    public double getMinDistance() {
        return minDistance;
    }

    // helper method for performing a 2-opt swap
    private List<Node> twoOptSwap(List<Node> tour, int i, int j) {
        Node[] newTour = new Node[length];
        int index = 0;
        // take all the cities from beginning to i-1
        for (int k = 0; k < i; k++) {
            newTour[index++] = tour.get(k);
        }
        // take cities from i to j in reverse order
        for (int k = j; k >= i; k--) {
            newTour[index++] = tour.get(k);
        }
        // take cities from j+1 to end
        for (int k = j + 1; k < length; k++) {
            newTour[index++] = tour.get(k);
        }
        List<Node> nList = new ArrayList<>(Arrays.asList(newTour));
        return nList;
    }

    private double calculateDistance(List<Node> tour) {
        Map<String, Integer> IDToIndex = loadData.IDToIndex;
        double[][] distances = loadData.adjacencyMatrix;

        double distance = 0;
        for (int i = 0; i < length - 1; i++) {
            int index1 = IDToIndex.get(tour.get(i).getId());
            int index2 = IDToIndex.get(tour.get(i + 1).getId());
            distance += distances[index1][index2];
        }
        int lastIndex = IDToIndex.get(tour.get(length - 1).getId());
        int firstIndex = IDToIndex.get(tour.get(0).getId());
        distance += distances[lastIndex][firstIndex];

        return distance;
    }

    public List<GraphOperation> getGos() {
        return gos;
    }

    public List<Node> getTour() {
        return tour;
    }

    public static double probability(double f1, double f2, double temp) {
        if (f2 < f1) return 1;
        return Math.exp((f1 - f2) / temp);
    }

    public List<GraphOperation> getShortGos() {
        return shortGos;
    }
}
