package com.kpi.multithreading.bellman_ford_parallel.model;

import java.util.Objects;

/**
 * Class that represents edge of the graph.
 * @param nodeA source node of the edge.
 * @param nodeB destination node of the edge.
 * @param price weight of the edge.
 */
public record Edge(int nodeA, int nodeB, int price) {

    /**
     * Is used to compare source node for Java Arrays.binarySearch().
     * @param o the reference object with which to compare.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return nodeA == edge.nodeA;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeA);
    }
}
