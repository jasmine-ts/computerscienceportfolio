package bearmaps;

import org.junit.Test;

import java.util.List;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import edu.princeton.cs.algs4.Stopwatch;

/** @source cs61b project2 walkthrough videos by Josh Hug **/
public class KDTreeTest {
    private static Random r = new Random(7777);
    @Test
    public void testNearest() {
        Point A = new Point(2, 3); // constructs a Point with x = 1.1, y = 2.2
        Point Z = new Point(4, 2);
        Point B = new Point(4, 2);
        Point C = new Point(4, 5);
        Point D = new Point(3, 3);
        Point E = new Point(1, 5);
        Point F = new Point(4, 4);


        KDTree kd = new KDTree(List.of(A, Z, B, C, D, E, F));
        Point actual = kd.nearest(0, 7);
        Point expected = new Point(1, 5);
        assertEquals(expected, actual);
    }

    private Point randomPoint() {
        double x = r.nextDouble() * r.nextInt(1000);
        double y = r.nextDouble() * r.nextInt(1000);
        return new Point(x, y);
    }

    private List<Point> randomPoints(int N) {
        List<Point> points = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            points.add(randomPoint());
        }
        return points;
    }

    private void testPointsAndQueries(int N, int Q) {
        List<Point> points = randomPoints(N);
        NaivePointSet nps = new NaivePointSet(points);
        KDTree kdt = new KDTree(points);

        List<Point> queries = randomPoints(Q);
        for (Point p : queries) {
            Point npsNearest = nps.nearest(p.getX(), p.getY());
            double expectedDist = npsNearest.distance(npsNearest, p);
            Point kdtNearest = kdt.nearest(p.getX(), p.getY());
            double actualDist = kdtNearest.distance(kdtNearest, p);
            assertEquals(expectedDist, actualDist, 0.00001);
        }
    }

    @Test
    public void test20Pts20Queries() {
        int numPoints = 20;
        int numQueries = 20;
        testPointsAndQueries(numPoints, numQueries);
    }

    @Test
    public void test1000Pts200Queries() {
        int numPoints = 1000;
        int numQueries = 200;
        testPointsAndQueries(numPoints, numQueries);
    }

    @Test
    public void test10000Pts2000Queries() {
        int numPoints = 10000;
        int numQueries = 2000;
        testPointsAndQueries(numPoints, numQueries);
    }

    @Test
    public void test1000000Pts50000Queries() {
        int numPoints = 1000000;
        int numQueries = 50000;
        testPointsAndQueries(numPoints, numQueries);
    }

    /** @source cs61b Fall 2020 lab5 **/
    private void printTimingTable(List<Integer> Ns, List<Double> times, List<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    /** @source cs61b Fall 2020 lab5 **/
    public void timeKDTreeConstruction() {
        List<Integer> listOfNs = List.of(31250, 62500, 125000, 250000, 500000, 1000000, 2000000);
        List<Double> listOfTimes = new ArrayList<>();
        List<Integer> listOfopCounts = List.of(31250, 62500, 125000, 250000, 500000, 1000000, 2000000);

        for (int N : listOfNs) {
            List<Point> points = randomPoints(N);
            Stopwatch SLListSW = new Stopwatch();
            KDTree kdt = new KDTree(points);
            listOfTimes.add(SLListSW.elapsedTime());
        }
        printTimingTable(listOfNs, listOfTimes, listOfopCounts);
    }

    public void timeNaiveNearest() {
        List<Integer> listOfNs = List.of(125, 250, 500, 1000);
        List<Double> listOfTimes = new ArrayList<>();
        List<Integer> listOfopCounts = List.of(125, 250, 500, 1000);

        for (int N : listOfNs) {
            List<Point> points = randomPoints(N);
            List<Point> queries = randomPoints(1000000);
            NaivePointSet nps = new NaivePointSet(points);
            Stopwatch SLListSW = new Stopwatch();
            for (Point p : queries) {
                nps.nearest(p.getX(), p.getY());
            }
            listOfTimes.add(SLListSW.elapsedTime());
        }
        printTimingTable(listOfNs, listOfTimes, listOfopCounts);
    }

    public void timeKDTNearest() {
        List<Integer> listOfNs = List.of(31250, 62500, 125000, 250000, 500000, 1000000, 2000000);
        List<Double> listOfTimes = new ArrayList<>();
        List<Integer> listOfopCounts = List.of(31250, 62500, 125000, 250000, 500000, 1000000, 2000000);

        for (int N : listOfNs) {
            List<Point> points = randomPoints(N);
            List<Point> queries = randomPoints(1000000);
            KDTree kdt = new KDTree(points);
            Stopwatch SLListSW = new Stopwatch();
            for (Point p : queries) {
                kdt.nearest(p.getX(), p.getY());
            }
            listOfTimes.add(SLListSW.elapsedTime());
        }
        printTimingTable(listOfNs, listOfTimes, listOfopCounts);
    }

    @Test
    public void testKDTreeConstructionTiming() {
        timeKDTreeConstruction();
    }

    @Test
    public void testNaiveNearest() {
        timeNaiveNearest();
    }

    @Test
    public void testKDTNearest() {
        timeKDTNearest();
    }

}
