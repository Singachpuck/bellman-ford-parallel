package com.kpi.multithreading.bellman_ford_parallel.service;

import com.kpi.multithreading.bellman_ford_parallel.model.AdjacencyListGraph;

public class BellmanFordSequential {

    public int[] solve(AdjacencyListGraph graph, int source) {
        final int vertices = graph.getVerticesNumber();
        final int edges = graph.getEdgesNumber();
        final int[] distances = new int[vertices];
        for (int i = 0; i < vertices; i++) {
            distances[i] = Integer.MAX_VALUE;
        }
        distances[source] = 0;

        for (int i = 1; i < vertices; i++) {
            for (int j = 0; j < edges; j++) {
                int u = graph.getEdge(j).nodeA();
                int v = graph.getEdge(j).nodeB();
                int weight = graph.getEdge(j).price();
                if (distances[u] != Integer.MAX_VALUE && distances[u] + weight < distances[v]) {
                    distances[v] = distances[u] + weight;
                }
            }
        }

        for (int j = 0; j < edges; j++) {
            int u = graph.getEdge(j).nodeA();
            int v = graph.getEdge(j).nodeB();
            int weight = graph.getEdge(j).price();
            if (distances[u] != Integer.MAX_VALUE && distances[u] + weight < distances[v]) {
                throw new IllegalStateException("Graph contains negative weight cycle");
            }
        }

        return distances;
    }
}
