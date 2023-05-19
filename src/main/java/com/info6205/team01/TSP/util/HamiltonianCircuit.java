package com.info6205.team01.TSP.util;

import java.util.*;

import com.info6205.team01.TSP.Graph.DirectedEdge;
import com.info6205.team01.TSP.Graph.Node;
import com.info6205.team01.TSP.visualization.GraphOperation;

public class HamiltonianCircuit {
    private List<Node> eulerianCircuit;
    private List<GraphOperation> gos;
    public HamiltonianCircuit(List<Node> eulerianCircuit) {
        this.eulerianCircuit = eulerianCircuit;
    }

    public List<Node> convertEulerianToHamiltonian() {
        double minLength = Double.MAX_VALUE;
        List<Node> bestHamiltonianCircuit = new ArrayList<>();
        for (int i = 0; i < eulerianCircuit.size(); i++) {
            int start = i;
            Set<Node> currVisited = new HashSet<>();
            List<Node> currHamiltonianCircuit = new ArrayList<>();
            for (int j = 0; j < eulerianCircuit.size(); j++) {
                int currNodeIndex = (start+j) % eulerianCircuit.size();
                Node currNode= eulerianCircuit.get(currNodeIndex);
                if (!currVisited.contains(currNode)) {
                    currHamiltonianCircuit.add(currNode);
                    currVisited.add(currNode);
                }
            }
            double currLength = 0;
            try {
                currLength = calculateDistance(currHamiltonianCircuit);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (currLength < minLength) {
                minLength = currLength;
                bestHamiltonianCircuit = currHamiltonianCircuit;
            }
        }

        // Check if the Hamiltonian circuit is valid
        if(!bestHamiltonianCircuit.isEmpty()) bestHamiltonianCircuit.add(bestHamiltonianCircuit.get(0));

        // Return the Hamiltonian circuit
        return bestHamiltonianCircuit;
    }

    private double calculateDistance(List<Node> tour) throws Exception {
        LoadData loadData = new LoadDataImpl();
        int length = tour.size();
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
}
