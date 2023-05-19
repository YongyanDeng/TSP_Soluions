package com.info6205.team01.TSP.strategic;

import com.info6205.team01.TSP.Graph.Node;
import com.info6205.team01.TSP.util.LoadCSVData;
import com.info6205.team01.TSP.util.LoadData;
import com.info6205.team01.TSP.visualization.GraphOperation;

import java.util.*;

public class TwoOpt {

    List<Node> tour;
    int length;
    List<GraphOperation> gos = new ArrayList<>();
    double minDistance = 0;
    LoadData loadData;

    public TwoOpt(List<Node> nodes, LoadData loadData) {
        this.loadData = loadData;
        tour = new ArrayList<>(nodes);
        length = tour.size();
        minDistance = calculateDistance(tour);
    }

    public void optimize() {
        List<Node> newTour;
        boolean improvement = true;
        int optCount = 0;

        while (improvement) {
            improvement = false;

            for (int i = 1; i < length - 1; i++) {
                for (int j = i + 1; j < length; j++) {
                    // 2-opt swap
                    newTour = twoOptSwap(tour, i, j);
                    // check new distance
                    double newDist = calculateDistance(newTour);

                    if (newDist < minDistance) {
                        optCount++;
                        // new tour is better, keep it
                        // add graph change info
                        int secondTo = j == length - 1 ? 0 : j + 1;
                        gos.add(GraphOperation.removeEdge(tour.get(i - 1), tour.get(i)));
                        gos.add(GraphOperation.removeEdge(tour.get(j), tour.get(secondTo)));
                        gos.add(GraphOperation.addEdge(tour.get(i - 1), tour.get(j)));
                        gos.add(GraphOperation.addEdge(tour.get(i), tour.get(secondTo)));
                        tour = newTour;
                        minDistance = newDist;
                        improvement = true;
                    }
                }
            }
        }
//        System.out.println("TwoOpt optCount: " + optCount);
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

    // helper method for calculating the distance of a tour
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
}
