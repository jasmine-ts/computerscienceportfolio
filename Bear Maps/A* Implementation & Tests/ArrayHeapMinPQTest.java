package bearmaps;

import edu.princeton.cs.algs4.Stopwatch;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

public class ArrayHeapMinPQTest {

    private static Random p = new Random(7777);
    private static Random i = new Random(8888);

    public void printSimpleHeapDrawing(Object[] heap) {
        int depth = ((int) (Math.log(heap.length) / Math.log(2)));
        int level = 0;
        int itemsUntilNext = (int) Math.pow(2, level);
        for (int j = 0; j < depth; j++) {
            System.out.print(" ");
        }

        for (int i = 1; i < heap.length; i++) {
            ArrayHeapMinPQ.priorityNode pNode = (ArrayHeapMinPQ.priorityNode) heap[i];
            System.out.printf("%d ", pNode.item);
            if (i == itemsUntilNext) {
                System.out.println();
                level++;
                itemsUntilNext += Math.pow(2, level);
                depth--;
                for (int j = 0; j < depth; j++) {
                    System.out.print(" ");
                }
            }
        }
    }

    public Double randomPriority() {
        return p.nextDouble();
    }

    public Double randomItem() {
        return i.nextDouble() * i.nextInt(1000);
    }

    public Integer randomInt(int N) {
        return i.nextInt(N - 1);
    }


    public ArrayHeapMinPQ createRandomAHMPQ(int N) {
        ArrayHeapMinPQ returnPQ = new ArrayHeapMinPQ();
        HashSet itemsSet = new HashSet<Integer>();
        for (int i = 0; i < N; i++) {
            double item = randomItem();
            if (itemsSet.contains(item)) {
                continue;
            } else {
                itemsSet.add(item);
            }
            double priority = randomPriority();
            returnPQ.add(item, priority);
        }
        return returnPQ;
    }

    public NaiveMinPQ createRandomNaiveMPQ(int N) {
        NaiveMinPQ returnPQ = new NaiveMinPQ();
        HashSet itemsSet = new HashSet<Double>();
        for (int i = 0; i < N; i++) {
            double item = randomItem();
            if (itemsSet.contains(item)) {
                continue;
            } else {
                itemsSet.add(item);
            }
            double priority = randomPriority();
            returnPQ.add(item, priority);
        }
        return returnPQ;
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

    public void timeAHMPQaddOp() {
        List<Integer> listOfNs = List.of(31250, 62500, 125000, 250000, 500000, 1000000);
        List<Double> listOfTimes = new ArrayList<>();
        List<Integer> listOfopCounts = List.of(31250, 62500, 125000, 250000, 500000, 1000000);

        for (int N : listOfNs) {
            Stopwatch SW = new Stopwatch();
            createRandomAHMPQ(N);
            listOfTimes.add(SW.elapsedTime());
        }
        printTimingTable(listOfNs, listOfTimes, listOfopCounts);
    }

    public void timeAHMPQremoveSmallest() {
        List<Integer> listOfNs = List.of(31250, 62500, 125000, 250000, 500000, 1000000);
        List<Double> listOfTimes = new ArrayList<>();
        List<Integer> listOfopCounts = List.of(31250, 62500, 125000, 250000, 500000, 1000000);

        for (int N : listOfNs) {
            ArrayHeapMinPQ AHMPQ = createRandomAHMPQ(N);
            Stopwatch SW = new Stopwatch();
            for (int i = 0; i < AHMPQ.size; i++) {
                AHMPQ.removeSmallest();
            }
            listOfTimes.add(SW.elapsedTime());
        }
        printTimingTable(listOfNs, listOfTimes, listOfopCounts);
    }


    public void timeNaiveMPQremoveSmallest() {
        List<Integer> listOfNs = List.of(31250, 62500, 125000, 250000);
        List<Double> listOfTimes = new ArrayList<>();
        List<Integer> listOfopCounts = List.of(31250, 62500, 125000, 250000);

        for (int N : listOfNs) {
            NaiveMinPQ NMPQ = createRandomNaiveMPQ(N);
            Stopwatch SW = new Stopwatch();
            for (int i = 0; i < NMPQ.size(); i++) {
                NMPQ.removeSmallest();
            }
            listOfTimes.add(SW.elapsedTime());
        }
        printTimingTable(listOfNs, listOfTimes, listOfopCounts);
    }

    public void timeAHMPQchangePriority() {
        List<Integer> listOfNs = List.of(31250, 62500, 125000, 250000, 500000, 1000000);
        List<Double> listOfTimes = new ArrayList<>();
        List<Integer> listOfopCounts = List.of(31250, 62500, 125000, 250000, 500000, 1000000);

        for (int N : listOfNs) {
            ArrayHeapMinPQ AHMPQ = createRandomAHMPQ(N);
            Stopwatch SW = new Stopwatch();
            for (int i = 1; i < 30000; i++) {
                ArrayHeapMinPQ.priorityNode node = (ArrayHeapMinPQ.priorityNode) AHMPQ.minHeap.get(i);
                Object item = node.item;
                AHMPQ.changePriority(item, randomPriority());
            }
            listOfTimes.add(SW.elapsedTime());
        }
        printTimingTable(listOfNs, listOfTimes, listOfopCounts);
    }

    @Test
    public void testTimeAHMPQaddOp() {
        timeAHMPQaddOp();
    }

    @Test
    public void testTimeAHMPQremoveSmallest() {
        timeAHMPQremoveSmallest();
    }

    @Test
    public void testTimeNaiveMPQremoveSmallest() {
        timeNaiveMPQremoveSmallest();
    }

    @Test
    public void testTimeAHMPQchangePriority() {
        timeAHMPQchangePriority();
    }

    @Test
    public void testRemoveSmallestRandom() {
        ArrayHeapMinPQ AHMPQ = createRandomAHMPQ(20);
        while (AHMPQ.size() > 0) {
            AHMPQ.removeSmallest();
        }
    }

    @Test
    public void testChangePriorityRandom() {
        ArrayHeapMinPQ AHMPQ = createRandomAHMPQ(5);
        for (int i = 0; i < 10; i++) {
            int randomIndex = randomInt(5);
            if (randomIndex != 0) {
                ArrayHeapMinPQ.priorityNode node = (ArrayHeapMinPQ.priorityNode) AHMPQ.minHeap.get(randomIndex);
                Object item = node.item;
                AHMPQ.changePriority(item, randomPriority());
            }
        }
    }

    public ArrayHeapMinPQ makePQ() {
        ArrayHeapMinPQ testPQ = new ArrayHeapMinPQ<>();
        testPQ.add(1, 1);
        testPQ.add(2, 2);
        testPQ.add(3, 3);
        testPQ.add(4, 4);
        testPQ.add(5, 5);
        return testPQ;
    }

    @Test
    public void testPQAdd1() {
        ArrayHeapMinPQ testPQ = makePQ();
        Object[] testPQArray = testPQ.minHeap.toArray();
        printSimpleHeapDrawing(testPQArray);
    }

    @Test
    public void testPQAdd2() {
        ArrayHeapMinPQ testPQ = new ArrayHeapMinPQ<>();
        testPQ.add(40, 4);
        testPQ.add(10, 1);
        testPQ.add(30, 3);
        testPQ.add(20, 2);
        testPQ.add(50, 5);
        Object[] testPQArray = testPQ.minHeap.toArray();
        printSimpleHeapDrawing(testPQArray);
    }

    @Test
    public void testPQRemoveSmallestDupPriorities() {
        ArrayHeapMinPQ testPQ = new ArrayHeapMinPQ<>();
        testPQ.add(40, 1);
        testPQ.add(10, 1);
        testPQ.add(30, 1);
        testPQ.add(20, 2);
        testPQ.add(50, 2);
        while (testPQ.size > 0) {
            testPQ.removeSmallest();
        }
    }

    @Test
    public void testPQContains() {
        ArrayHeapMinPQ testPQ = makePQ();
        assertTrue(testPQ.contains(1));
        assertFalse(testPQ.contains(6));
    }

    @Test
    public void testPQgetSmallest() {
        ArrayHeapMinPQ testPQ = makePQ();
        Object expected = 1;
        Object actual = testPQ.getSmallest();
        assertEquals(expected, actual);
    }

    @Test
    public void testPQremoveSmallest() {
        ArrayHeapMinPQ testPQ = makePQ();
        Object expected = 1;
        Object actual = testPQ.removeSmallest();
        assertEquals(expected, actual);
        Object[] testPQArray = testPQ.minHeap.toArray();
        printSimpleHeapDrawing(testPQArray);
    }

    @Test
    public void testPQchangePriority() {
        ArrayHeapMinPQ testPQ = makePQ();
        testPQ.changePriority(4, 1);
        Object[] testPQArray = testPQ.minHeap.toArray();
        printSimpleHeapDrawing(testPQArray);
    }

    @Test
    public void randomPriorityTest() {
        System.out.println(randomItem());
    }
    
    public ArrayList randPriorityList(int N) {
        ArrayList returnList = new ArrayList();
        for (int i = 0; i < N; i++) {
            double priority = randomPriority();
            returnList.add(priority);
        }
        return returnList;
    }
    
    
    public ArrayHeapMinPQ createRandomAHMPQ2(int N, ArrayList L) {
        ArrayHeapMinPQ returnPQ = new ArrayHeapMinPQ();
        for (int i = 0; i < N; i++) {
            double priority = (double) L.get(i);
            returnPQ.add(i, priority);
        }
        return returnPQ;
    }

    public NaiveMinPQ createRandomNaiveMPQ2(int N, ArrayList L) {
        NaiveMinPQ returnPQ = new NaiveMinPQ();
        for (int i = 0; i < N; i++) {
            double priority = (double) L.get(i);
            returnPQ.add(i, priority);
        }
        return returnPQ;
    }
    
    

    @Test
    public void testGetSmallest() {
        ArrayList priorities = randPriorityList(10000);
        ArrayHeapMinPQ AHMPQ = createRandomAHMPQ2(10000, priorities);
        NaiveMinPQ NMPQ = createRandomNaiveMPQ2(10000, priorities);
        Random r = new Random(12345);
        for (int i = 0; i < 9999; i++) {
            Object expected = NMPQ.getSmallest();
            Object actual = AHMPQ.getSmallest();
            assertEquals(expected, actual);
        }
    }
}

