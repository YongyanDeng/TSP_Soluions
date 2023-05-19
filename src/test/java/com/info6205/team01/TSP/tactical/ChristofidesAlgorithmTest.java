package com.info6205.team01.TSP.tactical;

import com.info6205.team01.TSP.Graph.Node;
import com.info6205.team01.TSP.util.LoadDataImpl;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class ChristofidesAlgorithmTest {
    private List<Node> nodes;
    private List<Node> tour;
    private ChristofidesAlgorithm ca;

    public ChristofidesAlgorithmTest() throws Exception {
        LoadDataImpl ld = new LoadDataImpl();
        nodes = ld.nodes.subList(0, 15);

        ca = new ChristofidesAlgorithm(nodes);
        ca.run();
        tour = ca.getTour();
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
        System.out.println(ca.getMinDistance());
    }
}
