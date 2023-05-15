package com.kpi.multithreading.bellman_ford_parallel.service;

import com.kpi.multithreading.bellman_ford_parallel.model.AdjacencyListGraph;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class BellmanFordTest {

    static Collection<Arguments> graphs() {
        List<Integer> edge1 = new ArrayList<>();
        edge1.add(0);
        edge1.add(1);
        edge1.add(-1);
        List<Integer> edge2 = new ArrayList<>();
        edge2.add(0);
        edge2.add(2);
        edge2.add(4);
        List<Integer> edge3 = new ArrayList<>();
        edge3.add(1);
        edge3.add(2);
        edge3.add(3);
        List<Integer> edge4 = new ArrayList<>();
        edge4.add(1);
        edge4.add(3);
        edge4.add(2);
        List<Integer> edge5 = new ArrayList<>();
        edge5.add(1);
        edge5.add(4);
        edge5.add(2);
        List<Integer> edge6 = new ArrayList<>();
        edge6.add(3);
        edge6.add(2);
        edge6.add(5);
        List<Integer> edge7 = new ArrayList<>();
        edge7.add(3);
        edge7.add(1);
        edge7.add(1);
        List<Integer> edge8 = new ArrayList<>();
        edge8.add(4);
        edge8.add(3);
        edge8.add(-3);
        List<Integer> edge9 = new ArrayList<>();
        edge9.add(0);
        edge9.add(0);
        edge9.add(-1);

        List<List<Integer>> graph1 = new ArrayList<>();
        graph1.add(edge1);
        graph1.add(edge2);
        graph1.add(edge3);
        graph1.add(edge4);
        graph1.add(edge5);
        graph1.add(edge6);
        graph1.add(edge7);
        graph1.add(edge8);
        // negative cycle
//        graph1.add(edge9);

        return List.of(
            Arguments.of(new AdjacencyListGraph(graph1))
        );
    }

    private final BellmanFordSequential bellmanFordSequential = new BellmanFordSequential();

    private final BellmanFordParallel bellmanFordParallel = new BellmanFordParallel(10);

    @ParameterizedTest
    @MethodSource("graphs")
    void solveTest(AdjacencyListGraph graph) {
        // [0, -1, 2, -2, 1]
        assertArrayEquals(new int[] {0, -1, 2, -2, 1}, bellmanFordSequential.solve(graph, 0));
    }

    @ParameterizedTest
    @MethodSource("graphs")
    void solveParallel(AdjacencyListGraph graph) {
        assertArrayEquals(new int[] {0, -1, 2, -2, 1}, bellmanFordParallel.solve(graph, 0));
    }

    @Test
    void verifyParallel() throws IOException, URISyntaxException {
        AdjacencyListGraph graph = readGraph("small.txt", 0);
        Assertions.assertArrayEquals(bellmanFordParallel.solve(graph, 1),
                bellmanFordSequential.solve(graph, 1));
    }

    AdjacencyListGraph readGraph(String name, int skip) throws URISyntaxException, IOException {
        final List<String> lines = Files.readAllLines(Path.of(getClass().getClassLoader().getResource(name).toURI()));

        final List<List<Integer>> graphRepresentation = new ArrayList<>();
        try {
            for (int i = skip; i < lines.size(); i++) {
                if (!lines.get(i).isBlank()) {
                    graphRepresentation.add(Arrays
                            .stream(lines.get(i).split("\\s+"))
                            .map(Integer::valueOf)
                            .toList());
                }
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Graph file is not valid!");
        }

        return new AdjacencyListGraph(graphRepresentation);
    }
}