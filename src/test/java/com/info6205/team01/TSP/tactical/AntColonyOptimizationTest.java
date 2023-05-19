package com.info6205.team01.TSP.tactical;

import java.util.*;

import org.junit.Test;

import com.info6205.team01.TSP.Graph.Node;
import com.info6205.team01.TSP.util.LoadDataImpl;

import static org.junit.Assert.assertTrue;


public class AntColonyOptimizationTest {
    private List<Node> nodes;
    private List<Node> tour;
    private AntColonyOptimization aco;
    private ChristofidesAlgorithm ca;

    public AntColonyOptimizationTest() throws Exception {
        LoadDataImpl ld = new LoadDataImpl();
        nodes = ld.nodes.subList(0, 15);

        ca = new ChristofidesAlgorithm(nodes);
        ca.run();
        List<Node> christofidesGraph = ca.getTour();
        aco = new AntColonyOptimization(this.nodes, christofidesGraph, 10, 100, 0.7, 1, 5);

        aco.run();
        aco.result();

        this.tour = aco.getTour();
    }

    @Test
    // Check number of node in tour
    public void testNodeNumber() throws Exception {
        assertTrue(tour.size() == nodes.size());
    }

    @Test
    // Check all nodes are used
    public void testUsedNodes() throws Exception {
        for (int i = 0; i < nodes.size(); i++) {
            assertTrue(tour.contains(nodes.get(i)));
        }
    }

    @Test
    public void testShortestPath() throws Exception {
        System.out.println(aco.getMinDistance());
    }
}
