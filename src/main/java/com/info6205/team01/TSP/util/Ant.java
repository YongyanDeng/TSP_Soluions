package com.info6205.team01.TSP.util;

import java.util.*;

public class Ant {
    private int numCities;
    private double[][] distances;
    private double[][] pheromones;
    private double alpha;
    private double beta;
    private List<Integer> tour;
    private double tourLength;
    private boolean[] visited;
    private Random random;

    public Ant(int numCities, double[][] distances, double[][] pheromones, double alpha, double beta) {
        this.numCities = numCities;
        this.distances = distances;
        this.pheromones = pheromones;
        this.alpha = alpha;
        this.beta = beta;
        this.tour = new ArrayList<>();
        this.visited = new boolean[numCities];
        this.random = new Random();

        int initialCity = random.nextInt(numCities);
        visitCity(initialCity);
    }

    public void visitCity(int city) {
        tour.add(city);
        visited[city] = true;
        if (tour.size() == numCities) {
            tour.add(tour.get(0)); // Close the tour
            calculateTourLength();
        }
    }

    public int chooseNextCity() {
        int currentCity = tour.get(tour.size() - 1);
        double[] probabilities = new double[numCities];
        double sumProbabilities = 0.0;

        for (int i = 0; i < numCities; i++) {
            if (!visited[i]) {
                double probability = Math.pow(pheromones[currentCity][i], alpha) * Math.pow(1 / distances[currentCity][i], beta);
                probabilities[i] = probability;
                sumProbabilities += probability;
            } else {
                probabilities[i] = 0;
            }
        }

        double randomValue = random.nextDouble() * sumProbabilities;
        double cumulativeProbability = 0.0;

        for (int i = 0; i < numCities; i++) {
            if (!visited[i]) {
                cumulativeProbability += probabilities[i];
                if (cumulativeProbability >= randomValue) {
                    return i;
                }
            }
        }

        return -1;
    }

    public void buildTour() {
        while (tour.size() < numCities) {
            int nextCity = chooseNextCity();
            visitCity(nextCity);
        }
    }

    private void calculateTourLength() {
        tourLength = 0.0;
        for (int i = 0; i < tour.size() - 1; i++) {
            tourLength += distances[tour.get(i)][tour.get(i + 1)];
        }
    }

    public List<Integer> getTour() {
        return tour.subList(0, tour.size() - 1);
    }

    public double getTourLength() {
        return tourLength;
    }
}
