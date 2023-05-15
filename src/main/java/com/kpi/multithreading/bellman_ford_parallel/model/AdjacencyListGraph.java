package com.kpi.multithreading.bellman_ford_parallel.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class AdjacencyListGraph {

    private final Edge[] adjacencyList;

    public AdjacencyListGraph(List<List<Integer>> adjacencyList) {
        if (adjacencyList.isEmpty() || adjacencyList.get(0).isEmpty()) {
            throw new IllegalArgumentException();
        }
        adjacencyList.sort(Comparator.comparingInt(entry -> entry.get(0)));
        final Edge[] edges = new Edge[adjacencyList.size()];
        for (int i = 0; i < adjacencyList.size(); i++) {
            edges[i] = new Edge(adjacencyList.get(i).get(0),
                    adjacencyList.get(i).get(1),
                    adjacencyList.get(i).get(2));
        }
        this.adjacencyList = edges;
    }

    public Edge getEdge(int i) {
        return adjacencyList[i];
    }

    public int getVerticesNumber() {
        return (int) Arrays.stream(adjacencyList)
                .flatMap(edge -> Stream.of(edge.nodeA(), edge.nodeB()))
                .distinct()
                .count();
    }

    public int getEdgesNumber() {
        return adjacencyList.length;
    }

    public List<Edge> getNeighbours(int source) {
        final Edge target = new Edge(source, -1, -1);
        int neighbour = Arrays.binarySearch(adjacencyList, target, Comparator.comparingInt(Edge::nodeA));
        if (neighbour < 0) {
            throw new IllegalArgumentException("Node " + source + " does not exist");
        }
        final List<Edge> neighbours = new ArrayList<>();
        Edge neightbourEdge;
        while ((neightbourEdge = adjacencyList[neighbour++]).nodeA() == source) {
            neighbours.add(neightbourEdge);
        }

        return neighbours;
    }
}
