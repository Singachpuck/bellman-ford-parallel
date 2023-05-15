package com.kpi.multithreading.bellman_ford_parallel.service.readers;

import com.kpi.multithreading.bellman_ford_parallel.model.AdjacencyListGraph;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GraphReader {

    public AdjacencyListGraph readWeightedGraph(String filePath, int linesSkip) throws IOException {
        final List<String> lines = Files.readAllLines(Path.of(filePath));

        final List<List<Integer>> graphRepresentation = new ArrayList<>();
        try {
            for (int i = linesSkip; i < lines.size(); i++) {
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

    public AdjacencyListGraph readUnweightedGraph(String filePath, int linesSkip) throws IOException {
        final List<String> lines = Files.readAllLines(Path.of(filePath));

        final List<List<Integer>> graphRepresentation = new ArrayList<>();
        try {
            for (int i = linesSkip; i < lines.size(); i++) {
                if (!lines.get(i).isBlank()) {
                    final List<Integer> edgeRepresentation = new ArrayList<>(Arrays
                            .stream(lines.get(i).split("\\s+"))
                            .map(Integer::valueOf)
                            .toList());
                    edgeRepresentation.add(1);
                    graphRepresentation.add(edgeRepresentation);
                }
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Graph file is not valid!");
        }

        return new AdjacencyListGraph(graphRepresentation);
    }
}
