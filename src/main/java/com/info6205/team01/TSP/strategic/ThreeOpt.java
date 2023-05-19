package com.info6205.team01.TSP.strategic;

import com.info6205.team01.TSP.Graph.Node;
import com.info6205.team01.TSP.util.LoadCSVData;
import com.info6205.team01.TSP.util.LoadData;
import com.info6205.team01.TSP.visualization.GraphOperation;

import java.util.*;

public class ThreeOpt {

    List<Node> tour;
    int length;
    List<GraphOperation> gos = new ArrayList<>();
    double minDistance = 0;
    LoadData loadData;
    int maxOpt = Integer.MAX_VALUE;
    Map<String, Integer> IDToIndex;
    double[][] distances;

    public ThreeOpt(List<Node> nodes, LoadData loadData) {
        this.loadData = loadData;
        tour = new ArrayList<>(nodes);
        length = tour.size();
        this.IDToIndex = loadData.IDToIndex;
        this.distances = loadData.adjacencyMatrix;
        minDistance = calculateDistance(tour);
    }

    public ThreeOpt(List<Node> nodes, LoadData loadData, int maxOpt) {
        this.loadData = loadData;
        tour = new ArrayList<>(nodes);
        length = tour.size();
        minDistance = calculateDistance(tour);
        this.IDToIndex = loadData.IDToIndex;
        this.distances = loadData.adjacencyMatrix;
        this.maxOpt = maxOpt;
    }

    public double getMinDistance() {
        return minDistance;
    }

    public void optimize() {
        int currOpt = 0;
        List<Node> newTour;
        int n = tour.size();
        boolean improvement = true;

        while (improvement) {
            improvement = false;

            for (int i = 0; i < n-1; i++) {
                int point11 = i;
                int point12 = i+1;
                for (int j = i+2; j < n-1; j++) {
                    int point21 = j;;
                    int point22 = j+1;
                    for (int k = j + 2; k < n-1; k++) {
                        // evaluate all possible 3-opt swaps
                        currOpt++;
                        if (currOpt >= maxOpt) {
                            return;
                        }
                        int point31 = k;
                        int point32 = k+1;
                        newTour = threeOptSwap(tour, point11, point12, point21, point22, point31, point32);
                        if(newTour.size() == 0) {
                            continue;
                        }
                        // calculate new distance
                        double newDist = calculateDistance(newTour);
                        tour = newTour;
                        improvement = true;
                        minDistance = newDist;
                    }
                }
            }
        }
    }

    // helper method for performing a 3-opt swap
    private List<Node> threeOptSwap(List<Node> tour, int point11, int point12, int point21, int point22, int point31, int point32) {
        List<Node> newTour = new ArrayList<>();

        Node node11 = tour.get(point11);
        Node node12 = tour.get(point12);
        Node node21 = tour.get(point21);
        Node node22 = tour.get(point22);
        Node node31 = tour.get(point31);
        Node node32 = tour.get(point32);
        int op11 = IDToIndex.get(node11.getId());
        int op12 = IDToIndex.get(node12.getId());
        int op21 = IDToIndex.get(node21.getId());
        int op22 = IDToIndex.get(node22.getId());
        int op31 = IDToIndex.get(node31.getId());
        int op32 = IDToIndex.get(node32.getId());
        double deletedEdges = distances[op11][op12] + distances[op21][op22] + distances[op31][op32];

        double[] gain = new double[4];
        double minGain = Double.MAX_VALUE;
        int minGainIndex = -1;

        gain[0] = distances[op11][op21] + distances[op12][op31] + distances[op22][op32] - deletedEdges;
        gain[1] = distances[op11][op31] + distances[op22][op12] + distances[op21][op32] - deletedEdges;
        gain[2] = distances[op11][op22] + distances[op31][op21] + distances[op12][op32] - deletedEdges;
        gain[3] = distances[op11][op22] + distances[op31][op12] + distances[op21][op32] - deletedEdges;

        for (int i = 0; i < 4; i++) {
            if (gain[i] < minGain) {
                minGain = gain[i];
                minGainIndex = i;
            }
        }

        if (minGain < 0) {
            gos.add(GraphOperation.removeEdge(node11, node12));
            gos.add(GraphOperation.removeEdge(node21, node22));
            gos.add(GraphOperation.removeEdge(node31, node32));
            switch (minGainIndex) {
                case 0:
                    newTour.addAll(tour.subList(0, point11+1));
                    List<Node> seg1 = tour.subList(point12, point21+1);
                    Collections.reverse(seg1);
                    newTour.addAll(seg1);
                    List<Node> seg2 = tour.subList(point22, point31+1);
                    Collections.reverse(seg2);
                    newTour.addAll(seg2);
                    newTour.addAll(tour.subList(point32, tour.size()));
                    gos.add(GraphOperation.addEdge(node11, node21));
                    gos.add(GraphOperation.addEdge(node12, node31));
                    gos.add(GraphOperation.addEdge(node22, node32));
                    break;
                case 1:
                    newTour.addAll(tour.subList(0, point11+1));
                    List<Node> seg22 = tour.subList(point22, point31+1);
                    Collections.reverse(seg22);
                    newTour.addAll(seg22);
                    newTour.addAll(tour.subList(point12, point21+1));
                    newTour.addAll(tour.subList(point32, tour.size()));
                    gos.add(GraphOperation.addEdge(node11, node31));
                    gos.add(GraphOperation.addEdge(node22, node12));
                    gos.add(GraphOperation.addEdge(node21, node32));
                    break;
                case 2:
                    newTour.addAll(tour.subList(0, point11+1));
                    newTour.addAll(tour.subList(point22, point31+1));
                    List<Node> seg31 = tour.subList(point12, point21+1);
                    Collections.reverse(seg31);
                    newTour.addAll(seg31);
                    newTour.addAll(tour.subList(point32, tour.size()));
                    gos.add(GraphOperation.addEdge(node11, node22));
                    gos.add(GraphOperation.addEdge(node31, node21));
                    gos.add(GraphOperation.addEdge(node12, node32));
                    break;
                default:
                    newTour.addAll(tour.subList(0, point11+1));
                    newTour.addAll(tour.subList(point22, point31+1));
                    newTour.addAll(tour.subList(point12, point21+1));
                    newTour.addAll(tour.subList(point32, tour.size()));
                    gos.add(GraphOperation.addEdge(node11, node22));
                    gos.add(GraphOperation.addEdge(node31, node12));
                    gos.add(GraphOperation.addEdge(node21, node32));
                    break;
            }
        }

        /*// case 1: ABC -> ACB
        newTour.addAll(tour.subList(0, point11+1));
        List<Node> seg1 = tour.subList(point12, point21+1);
        Collections.reverse(seg1);
        newTour.addAll(seg1);
        List<Node> seg2 = tour.subList(point22, point31+1);
        Collections.reverse(seg2);
        newTour.addAll(seg2);
        newTour.addAll(tour.subList(point32, tour.size()));
        double newDist = calculateDistance(newTour);

        // case 2: ABC -> BAC
        List<Node> newTour2 = new ArrayList<>();
        newTour2.addAll(tour.subList(0, point11+1));
        newTour2.addAll(seg2);
        newTour2.addAll(tour.subList(point12, point21+1));
        newTour2.addAll(tour.subList(point32, tour.size()));
        double newDist2 = calculateDistance(newTour2);

        if (newDist2 < newDist) {
            newTour = newTour2;
            newDist = newDist2;
        }

        // case 3: ABC -> BCA
        List<Node> newTour3 = new ArrayList<>();
        newTour3.addAll(tour.subList(0, point11+1));
        newTour3.addAll(tour.subList(point22, point31+1));
        newTour3.addAll(seg1);
        newTour3.addAll(tour.subList(point32, tour.size()));
        double newDist3 = calculateDistance(newTour3);

        if (newDist3 < newDist) {
            newTour = newTour3;
            newDist = newDist3;
        }

        // case 4: ABC -> CAB
        List<Node> newTour4 = new ArrayList<>();
        newTour4.addAll(tour.subList(0, point11+1));
        newTour4.addAll(tour.subList(point22, point31+1));
        newTour4.addAll(tour.subList(point12, point21+1));
        newTour4.addAll(tour.subList(point32, tour.size()));
        double newDist4 = calculateDistance(newTour4);

        if (newDist4 < newDist) {
            newTour = newTour4;
            newDist = newDist4;
        }
        if (newTour.size() > 155) {
            System.out.println("stop");
        }*/
//        System.out.println("newTour length: " + newTour.size());
//        System.out.println("newDist: " + newDist);

        return newTour;
    }


    // helper method for calculating the distance of a tour
    private double calculateDistance(List<Node> tour) {

        double distance = 0;
        for (int i = 0; i < length - 1; i++) {
            int index1 = IDToIndex.get(tour.get(i).getId());
            int index2 = IDToIndex.get(tour.get(i + 1).getId());
            distance += distances[index1][index2];
        }
        int lastIndex = IDToIndex.get(tour.get(length - 1).getId());
        int firstIndex = IDToIndex.get(tour.get(0).getId());
        ;
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
