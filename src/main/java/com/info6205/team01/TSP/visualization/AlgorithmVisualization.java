package com.info6205.team01.TSP.visualization;

import com.info6205.team01.TSP.Graph.Node;
import com.info6205.team01.TSP.strategic.RandomSwapping;
import com.info6205.team01.TSP.tactical.AntColonyOptimization;
import com.info6205.team01.TSP.tactical.GreedyHeuristic;
import com.info6205.team01.TSP.util.LoadDataImpl;
import com.info6205.team01.TSP.util.Preprocessing;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

import java.util.ArrayList;
import java.util.List;

public class AlgorithmVisualization {

    private String title = "GraphStream";
    public AlgorithmVisualization(List<Node> nodes, List<GraphOperation> gos, List<GraphOperation> oldGos) {
        this.nodes = nodes;
        this.oldGos = oldGos;
        this.gos = gos;
    }

    public AlgorithmVisualization(List<Node> nodes, List<GraphOperation> gos, List<GraphOperation> oldGos, String title) {
        this.nodes = nodes;
        this.oldGos = oldGos;
        this.gos = gos;
        this.title = title;
    }

    public static void main(String[] args) throws Exception {
        // Example Usage
        LoadDataImpl ld = new LoadDataImpl();

        // Run your algorithn
        GreedyHeuristic gh = new GreedyHeuristic(ld.nodes);

        // Get gos
        gh.getGos();

        // Build av
        AlgorithmVisualization av = new AlgorithmVisualization(ld.nodes, gh.getGos());

        // You can set sleep time
        // default: 500
        av.setSleepTime(50);

        // Show the animation
        av.showResult();
    }

    private List<Node> nodes;
    private List<GraphOperation> gos;
    private List<GraphOperation> oldGos;
    private int sleepTime = 500;

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    public AlgorithmVisualization(List<Node> nodes, List<GraphOperation> gos) {
        this.nodes = nodes;
        this.gos = gos;
    }

    public void showResult() {
        System.setProperty("org.graphstream.ui", "swing");
        Graph graph = new SingleGraph("Tutorial 1");
        graph.setAttribute("ui.title", "Your Title");
        graph.setAttribute("ui.stylesheet", "node{\n" +
                "    size: 5px, 5px;\n" +
                "    fill-color: black;\n" +
                "    text-mode: normal; \n" +
                "}" +
                "edge.highlight{\n" +
                "   fill-color: red;\n" +
                "   size: 2.5px, 2.5px;" +
                "}"
        );

        // Add all the nodes
        for (Node node : nodes) {
            graph.addNode(node.getId()).setAttribute("ui.label", node.getId());
            graph.getNode((node.getId())).setAttribute("layout.frozen");
            graph.getNode((node.getId())).setAttribute("xy", node.getLatitude(), node.getLongitude());
        }
        if (oldGos != null) {
            for (GraphOperation go : oldGos) {
                changeEdge(graph, go);
            }
        }

        graph.display();
        if (gos != null) {
            for (GraphOperation go : gos) {
                changeEdge(graph, go);
                if (go.isDelay()){
                    sleep();
                }
            }
        }

    }

    private void changeEdge(Graph graph, GraphOperation go) {
        switch (go.getAction()) {
            case ADD:
                Edge e = graph.getEdge(go.node1.getId() + go.node2.getId()) == null ? graph.getEdge(go.node2.getId() + go.node1.getId()) : graph.getEdge(go.node1.getId() + go.node2.getId());
                if (e == null) {
                    graph.addEdge(go.node1.getId() + go.node2.getId(), go.node1.getId(), go.node2.getId());
                    if (go.getLayout() == GraphOperation.Layout.HIGHLIGHT) {
                        graph.getEdge(go.node1.getId() + go.node2.getId()).setAttribute("ui.class", "highlight");
                    }
                } else {
                    System.out.println("Edge dulplicate");
                }

                break;
            case REMOVE:
                Edge edge = graph.getEdge(go.node1.getId() + go.node2.getId()) == null ? graph.getEdge(go.node2.getId() + go.node1.getId()) : graph.getEdge(go.node1.getId() + go.node2.getId());
                if (edge != null) {
                    graph.removeEdge((org.graphstream.graph.Node) graph.getNode(go.node1.getId()), graph.getNode(go.node2.getId()));
                } else {
                    System.out.println("Edge not exist");
                }
//                if (graph.getEdge(go.node1.getId() + go.node2.getId()) != null) {
//                    edge = graph.getEdge(go.node1.getId() + go.node2.getId());
//                } else if (graph.getEdge(go.node2.getId() + go.node1.getId()) != null) {
//                    edge = graph.getEdge(go.node2.getId() + go.node1.getId());
//                } else {
//                    break;
//                }
//                graph.removeEdge(edge);
                break;
            case SETLAYOUT:
                Edge edge1;
                if (graph.getEdge(go.node1.getId() + go.node2.getId()) != null) {
                    edge1 = graph.getEdge(go.node1.getId() + go.node2.getId());
                } else if (graph.getEdge(go.node2.getId() + go.node1.getId()) != null) {
                    edge1 = graph.getEdge(go.node2.getId() + go.node1.getId());
                } else {
                    break;
                }
                if (go.getLayout() == GraphOperation.Layout.UNHIGHLIGHT) {
                    edge1.removeAttribute("ui.class");
                } else {
                    edge1.addAttribute("ui.class", "highlight");
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + go.getAction());
        }
    }

    protected void sleep() {
        try {
            Thread.sleep(sleepTime);
        } catch (Exception e) {
        }
    }
}
