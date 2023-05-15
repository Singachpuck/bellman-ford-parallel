package com.kpi.multithreading.bellman_ford_parallel;

import com.kpi.multithreading.bellman_ford_parallel.model.AdjacencyListGraph;
import com.kpi.multithreading.bellman_ford_parallel.service.BellmanFordParallel;
import com.kpi.multithreading.bellman_ford_parallel.service.BellmanFordSequential;
import com.kpi.multithreading.bellman_ford_parallel.service.readers.GraphReader;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.util.Arrays;

public class BellmanFordApplication {

    /**
     * usage: Bellman-Ford parallel algorithm app -g <arg> [-h] -S <arg> [-s
     *        <arg>] [-t <arg>] [-w <arg>]
     * Argument description:
     *  -g,--graph <arg>      Path to the graph file.
     *  -h,--help             To ask to print the help.
     *  -S,--source <arg>     Defines the source node of the graph.
     *  -s,--weighted <arg>   Defines the number of lines to skip in the graph
     *                        file.
     *  -t,--type <arg>       Type of Bellman-Ford algorithm
     *                        (sequential/parallel). Default sequential.
     *  -w,--weighted <arg>   Defines if the graph file represents weighted
     *                        graph. 1 - graph is weighted, 0 - graph is not
     *                        weighted. 1 by default.
     */
    public static void main(String[] args) throws IOException, ParseException {
        final Option typeOption = Option.builder("t")
                .required(false)
                .hasArg(true)
                .desc("Type of Bellman-Ford algorithm (sequential/parallel). Default sequential.")
                .longOpt("type")
                .build();
        final Option graphOption = Option.builder("g")
                .required(true)
                .hasArg(true)
                .desc("Path to the graph file.")
                .longOpt("graph")
                .build();
        final Option sourceOption = Option.builder("S")
                .required(true)
                .hasArg(true)
                .desc("Defines the source node of the graph.")
                .longOpt("source")
                .build();
        final Option weightedOption = Option.builder("w")
                .required(false)
                .hasArg(true)
                .desc("Defines if the graph file represents weighted graph. 1 - graph is weighted, 0 - graph is not weighted. 1 by default.")
                .longOpt("weighted")
                .build();
        final Option linesSkipOption = Option.builder("s")
                .required(false)
                .hasArg(true)
                .desc("Defines the number of lines to skip in the graph file.")
                .longOpt("weighted")
                .build();
        final Option helpOption = Option.builder("h")
                .required(false)
                .hasArg(false)
                .desc("To ask to print the help.")
                .longOpt("help")
                .build();
        final Options options = new Options();
        final CommandLineParser parser = new DefaultParser();

        options.addOption(typeOption);
        options.addOption(graphOption);
        options.addOption(weightedOption);
        options.addOption(sourceOption);
        options.addOption(linesSkipOption);
        options.addOption(helpOption);

        final CommandLine commandLine = parser.parse(options, args);

        if (commandLine.hasOption("h")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Bellman-Ford parallel algorithm app", "Argument description:", options, "", true);
            return;
        }
        final String type = commandLine.getOptionValue("t", "sequential");
        final String graphPath = commandLine.getOptionValue("g");
        final int source = Integer.parseInt(commandLine.getOptionValue("S"));
        final int weighted = Integer.parseInt(commandLine.getOptionValue("w", "1"));
        final int skip = Integer.parseInt(commandLine.getOptionValue("s", "0"));

        final AdjacencyListGraph graph;
        final GraphReader graphReader = new GraphReader();
        if (weighted == 1) {
            graph = graphReader.readWeightedGraph(graphPath, skip);
        } else if (weighted == 0) {
            graph = graphReader.readUnweightedGraph(graphPath, skip);
        } else {
            throw new ParseException("Weighted indicator is not correct. Can be either 1 or 0.");
        }

        final int[] result;
        long before = System.nanoTime();
        switch (type) {
            case "sequential" -> {
                final BellmanFordSequential sequential = new BellmanFordSequential();
                result = sequential.solve(graph, source);
            }
            case "parallel" -> {
                final BellmanFordParallel parallel = new BellmanFordParallel(10);
                result = parallel.solve(graph, source);
            }
            default -> throw new ParseException("Type of Bellman-Ford algorithm is not correct");
        }
        long after = System.nanoTime();

        System.out.println(Arrays.toString(result));

        final String timeResult = "Elapsed time: " + (after - before) / 1_000_000_000D;
        System.out.println(timeResult);
    }
}
