package com.kpi.multithreading.bellman_ford_parallel.service;

import com.kpi.multithreading.bellman_ford_parallel.model.AdjacencyListGraph;
import com.kpi.multithreading.bellman_ford_parallel.model.Edge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class BellmanFordParallel {

    private final int nThread;

    public BellmanFordParallel(int nThreads) {
        this.nThread = nThreads;
    }

    public int[] solve(AdjacencyListGraph graph, int source) {
        final int vertices = graph.getVerticesNumber();
        final int edges = graph.getEdgesNumber();
        // Number of tasks
        final int parallelism = edges < 1000 ? 1 : edges / 1000;
        final int[] distances = new int[vertices];

        // Initialization
        for (int i = 0; i < vertices; i++) {
            distances[i] = Integer.MAX_VALUE;
        }
        distances[source] = 0;

        // Graph transformation
        final Map<Integer, List<Edge>> byDestination = new HashMap<>();
        for (int i = 0; i < edges; i++) {
            final Edge edge = graph.getEdge(i);
            byDestination.compute(edge.nodeB(), (k, v) -> {
                if (v == null) {
                    final List<Edge> l = new ArrayList<>();
                    l.add(edge);
                    return l;
                }
                v.add(edge);
                return v;
            });
        }

        final List<List<Edge>> transformedGraph = new ArrayList<>(byDestination.values());

        final int perThread = byDestination.size() / parallelism;
        final int extra = byDestination.size() % parallelism;

        final ExecutorService threadPool = Executors.newFixedThreadPool(nThread);
        try {
            // Main part
            final List<Future<?>> results  = new ArrayList<>(parallelism);
            for (int i = 1; i < vertices; i++) {
                int offset = 0;
                for (int j = 0; j < parallelism; j++) {
                    final int nVertices = (j < extra) ? perThread + 1 : perThread;
                    final Runnable task = new RecomputeDistanceTask(transformedGraph, distances, offset,
                            offset + nVertices);
                    results.add(threadPool.submit(task));
                    offset += nVertices;
                }

                for (Future<?> result : results) {
                    result.get();
                }
                results.clear();
            }

            // Negative cycle verification
            int offset = 0;
            for (int j = 0; j < parallelism; j++) {
                final int nVertices = (j < extra) ? perThread + 1 : perThread;
                final Runnable task = new VerifyDistanceTask(transformedGraph, distances, offset,
                        offset + nVertices);
                results.add(threadPool.submit(task));
                offset += nVertices;
            }

            for (Future<?> result : results) {
                result.get();
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            threadPool.shutdownNow();
        }

        return distances;
    }

    static class VerifyDistanceTask implements Runnable {

        private final List<List<Edge>> byDestination;

        private final int[] distances;

        private final int vertexBegin;

        private final int vertexEnd;

        public VerifyDistanceTask(List<List<Edge>> graph, int[] distances, int vertexBegin, int vertexEnd) {
            this.byDestination = graph;
            this.distances = distances;
            this.vertexBegin = vertexBegin;
            this.vertexEnd = vertexEnd;
        }

        @Override
        public void run() {
            for (int i = vertexBegin; i < vertexEnd; i++) {
                List<Edge> edges = byDestination.get(i);
                for (Edge edge : edges) {
                    int u = edge.nodeA();
                    int v = edge.nodeB();
                    int weight = edge.price();
                    if (distances[u] != Integer.MAX_VALUE && distances[u] + weight < distances[v]) {
                        throw new IllegalStateException("Graph contains negative weight cycle");
                    }
                }
            }
        }
    }

    static class RecomputeDistanceTask implements Runnable {

        private final List<List<Edge>> byDestination;

        private final int[] distances;

        private final int vertexBegin;

        private final int vertexEnd;

        public RecomputeDistanceTask(List<List<Edge>> graph, int[] distances, int vertexBegin, int vertexEnd) {
            this.byDestination = graph;
            this.distances = distances;
            this.vertexBegin = vertexBegin;
            this.vertexEnd = vertexEnd;
        }

        @Override
        public void run() {
            for (int i = vertexBegin; i < vertexEnd; i++) {
                List<Edge> edges = byDestination.get(i);
                for (Edge edge : edges) {
                    int u = edge.nodeA();
                    int v = edge.nodeB();
                    int weight = edge.price();
                    if (distances[u] != Integer.MAX_VALUE && distances[u] + weight < distances[v]) {
                        distances[v] = distances[u] + weight;
                    }
                }
            }
        }
    }
}
