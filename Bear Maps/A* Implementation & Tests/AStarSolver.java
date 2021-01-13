package bearmaps.proj2c;

import edu.princeton.cs.algs4.Stopwatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AStarSolver<Vertex> implements ShortestPathsSolver<Vertex> {
    private DoubleMapPQ<Vertex> vertexPQ;
    private HashMap<Vertex, Double> distTo; //hashmap of vertices and their distance from source
    private HashMap<Vertex, Vertex> edgeTo; //hashmap of vertices and their best from() vertex
    private AStarGraph<Vertex> graph;
    private Vertex source;
    private Vertex goal;
    private int outcomeVal;
    private int statesExplored;
    private Stopwatch sw;
    private List<Vertex> solutionList;

    /** Constructor finds the solution; computes everything necessary
     * for all other methods to return their results in constant time.
     * Timeout input variable is in seconds.*/
    public AStarSolver(AStarGraph<Vertex> input, Vertex start, Vertex end, double timeout) {
        sw = new Stopwatch();
        vertexPQ = new DoubleMapPQ<>();
        distTo = new HashMap<>();
        edgeTo = new HashMap<>();
        graph = input;
        source = start;
        goal = end;
        statesExplored = 0;

        vertexPQ.add(source, input.estimatedDistanceToGoal(start, end)); //add source vertex to PQ
        distTo.put(source, 0.0); //add source vertex to distTo with distance 0
        edgeTo.put(source, null); //add source vertex to edgeTo with null value

        while (vertexPQ.size() != 0) {
            Vertex v = vertexPQ.removeSmallest();
            if (v.equals(goal)) {
                outcomeVal = 1;
                createSolutionList();
                return;
            } else if (sw.elapsedTime() >= timeout) {
                outcomeVal = 7;
                createSolutionList();
                return;
            }
            statesExplored += 1;
            updatePQ(v);
        }
        outcomeVal = 0;
        createSolutionList();
    }

    private double calcPriority(Vertex v) {
        return distTo.get(v) + graph.estimatedDistanceToGoal(v, goal);
    }

    private void updatePQ(Vertex v) {
        List<WeightedEdge<Vertex>> vNeighbors = graph.neighbors(v);
        for (WeightedEdge<Vertex> n : vNeighbors) {
            Vertex neighborV = n.to();
            double distToNV = distTo.get(v) + n.weight();
            if (!distTo.containsKey(neighborV)) {
                distTo.put(neighborV, distToNV);
            }
            relax2(n);
        }
    }

    private void relax2(WeightedEdge<Vertex> e) {
        Vertex sourceV = e.from();
        Vertex destinationV = e.to();
        double weight = e.weight();

        double sourceDistPlusWeight = distTo.get(sourceV) + weight;
        if (sourceDistPlusWeight < distTo.get(destinationV)) {
            distTo.replace(destinationV, sourceDistPlusWeight);

            if (vertexPQ.contains(destinationV)) {
                vertexPQ.changePriority(destinationV, calcPriority(destinationV));
                edgeTo.replace(destinationV, sourceV);
            } else if (!edgeTo.containsKey(destinationV)) {
                vertexPQ.add(destinationV, calcPriority(destinationV));
                edgeTo.put(destinationV, sourceV);
            }
        } else {
            if (vertexPQ.contains(destinationV)) {
                vertexPQ.changePriority(destinationV, calcPriority(destinationV));
                edgeTo.replace(destinationV, sourceV);
            } else if (!edgeTo.containsKey(destinationV)) {
                vertexPQ.add(destinationV, calcPriority(destinationV));
                edgeTo.put(destinationV, sourceV);
            }
        }
    }


    private void createSolutionList() {
        solutionList = new ArrayList<>();
        if (outcomeVal != 1) {
            return;
        }
        DoubleMapPQ<Vertex> reverserPQ = new DoubleMapPQ<>();

        Vertex v = goal;
        int p = 10000;
        while (!v.equals(source)) {
            reverserPQ.add(v, p);
            v = edgeTo.get(v);
            p -= 1;
        }
        solutionList.add(source);
        while (reverserPQ.size() != 0) {
            solutionList.add(reverserPQ.removeSmallest());
        }
    }

    /** Returns one of the following:
     * 1) SolverOutcome.SOLVED if AStarSolver was able to complete all work in given time
     * 2) SolverOutcome.UNSOLVABLE if the priority queue became empty
     * 3) SolverOutcome.TIMEOUT if the solver ran out of time; check every time you dequeue */
    public SolverOutcome outcome() {
        if (outcomeVal == 1) {
            return SolverOutcome.SOLVED;
        } else if (outcomeVal == 0) {
            return SolverOutcome.UNSOLVABLE;
        } else {
            return SolverOutcome.TIMEOUT;
        }
    }

    /** Returns a list of vertices corresponding to a solution.
     * List should be empty if result was TIMEOUT or UNSOLVABLE. */
    public List<Vertex> solution() {
        return solutionList;
    }

    /** The total weight of the given solution by taking into account edge weights.
     * Should return 0 if result was TIMEOUT or UNSOLVABLE. */
    public double solutionWeight() {
        if (outcomeVal == 1) {
            return distTo.get(goal);
        } else {
            return 0;
        }
    }

    /** The total number of priority queue dequeue operations. */
    public int numStatesExplored() {
        return statesExplored;
    }

    /** The total time spent in seconds by the constructor. */
    public double explorationTime() {
        return sw.elapsedTime();
    }
}
