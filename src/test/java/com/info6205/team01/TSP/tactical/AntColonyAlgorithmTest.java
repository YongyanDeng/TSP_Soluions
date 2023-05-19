package com.info6205.team01.TSP.tactical;

import java.util.*;

import com.info6205.team01.TSP.Graph.Node;
import com.info6205.team01.TSP.util.LoadDataImpl;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class AntColonyAlgorithmTest {
    private List<Node> nodes;
    private List<Node> tour;
    private AntColonyAlgorithm aca;

    public AntColonyAlgorithmTest() throws Exception {
        LoadDataImpl ld = new LoadDataImpl();
        nodes = ld.nodes.subList(0, 15);

        aca = new AntColonyAlgorithm(this.nodes, 10, 100, 0.7, 1, 5);

        aca.run();
        aca.result();

        this.tour = aca.getTour();
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
        System.out.println(aca.getMinDistance());
    }
}
