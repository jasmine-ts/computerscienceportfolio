package bearmaps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;

public class ArrayHeapMinPQ<T> implements ExtrinsicMinPQ<T> {

    private ArrayList<PriorityNode> minHeap;
    private int size;
    private HashMap<T, Integer> indexHashMap;

    private class PriorityNode {
        private T item;
        private double priority;

        PriorityNode(T item, double priority) {
            this.item = item;
            this.priority = priority;
        }
    }

    public ArrayHeapMinPQ() {
        minHeap = new ArrayList<>();
        minHeap.add(null); //sets 0th index to null (sentinel node basically)
        size = 0;
        indexHashMap = new HashMap<>();
    }

    private int calcParent(int k) {
        return k / 2;
    }

    private int calcLeftChild(int k) {
        return k * 2;
    }

    private int calcRightChild(int k) {
        return k * 2 + 1;
    }

    private void swapMinHeapNodes(PriorityNode p1, int p1Index, PriorityNode p2, int p2Index) {
        minHeap.set(p2Index, p1);
        minHeap.set(p1Index, p2);
    }

    private void swapHashMapIndices(T item1, T item2) {
        int item1origIndex = indexHashMap.get(item1);
        int item2origIndex = indexHashMap.get(item2);
        indexHashMap.replace(item1, item2origIndex);
        indexHashMap.replace(item2, item1origIndex);
    }
    /** Swaps a pNode with its parent Node when given the pNode's index.
     */
    private void swimUp(int pIndex, int parentIndex) {
        PriorityNode pNode = minHeap.get(pIndex);
        PriorityNode pNodeParent = minHeap.get(parentIndex);
        swapMinHeapNodes(pNode, pIndex, pNodeParent, parentIndex);
        swapHashMapIndices(pNode.item, pNodeParent.item);
    }

    private void sinkDown(int pIndex) {
        PriorityNode pNode = minHeap.get(pIndex);
        int leftChildIndex = calcLeftChild(pIndex);
        int rightChildIndex = calcRightChild(pIndex);
        if (leftChildIndex > this.size()) { //0 children case
            return;
        } else if (rightChildIndex > this.size()) { //1 (left) child case
            PriorityNode leftChild = minHeap.get(leftChildIndex);
            if (pNode.priority > leftChild.priority) {
                swapMinHeapNodes(pNode, pIndex, leftChild, leftChildIndex);
                swapHashMapIndices(pNode.item, leftChild.item);
            }
            return;
        } else {
            PriorityNode leftChild = minHeap.get(leftChildIndex);
            PriorityNode rightChild = minHeap.get(rightChildIndex);
            if (pNode.priority > leftChild.priority
                    && leftChild.priority <= rightChild.priority) {
                swapMinHeapNodes(pNode, pIndex, leftChild, leftChildIndex);
                swapHashMapIndices(pNode.item, leftChild.item);
                sinkDown(leftChildIndex);
            } else if (pNode.priority > rightChild.priority
                    && rightChild.priority < leftChild.priority) {
                swapMinHeapNodes(pNode, pIndex, rightChild, rightChildIndex);
                swapHashMapIndices(pNode.item, rightChild.item);
                sinkDown(rightChildIndex);
            }
        }
    }

    /* Adds an item with the given priority value. Throws an
     * IllegalArgumentException if item is already present.
     * You may assume that item is never null. */
    public void add(T item, double priority) {
        if (indexHashMap.containsKey(item)) {
            throw new IllegalArgumentException();
        }
        PriorityNode pNode = new PriorityNode(item, priority);
        minHeap.add(pNode);
        this.size += 1;
        indexHashMap.put(item, size);
        addHelper(pNode);
    }

    private void addHelper(PriorityNode pNode) {
        int pNodeIndex = this.size();
        int parentIndex = calcParent(pNodeIndex);
        if (parentIndex == 0) {
            return;
        }
        PriorityNode parent = minHeap.get(parentIndex);
        while (pNode.priority < parent.priority) {
            swimUp(pNodeIndex, parentIndex);
            pNodeIndex = parentIndex; //updates the pNode's index
            parentIndex = calcParent(parentIndex); //updates the new parentIndex
            if (parentIndex == 0) {
                return;
            }
            parent = minHeap.get(parentIndex); //updates the new parent Node
        }
        return;
    }

    /* Returns true if the PQ contains the given item. */
    /* Must run in 0(log(n)) avg time */
    public boolean contains(T item) {
        return indexHashMap.containsKey(item);
    }

    /* Returns the minimum item. Throws NoSuchElementException if the PQ is empty. */
    /* Must run in 0(log(n)) avg time */
    public T getSmallest() {
        if (this.size() == 0) {
            throw new NoSuchElementException();
        }
        return minHeap.get(1).item;
    }

    /* Removes and returns the minimum item. Throws NoSuchElementException if the PQ is empty. */
    public T removeSmallest() {
        if (this.size() == 0) {
            throw new NoSuchElementException();
        } else if (this.size() == 1) {
            T smallest = minHeap.get(1).item;
            minHeap.remove(1);
            indexHashMap.remove(smallest);
            this.size -= 1;
            return smallest;
        }
        T smallest = minHeap.get(1).item;
        int lastIndex = this.size();
        PriorityNode lastNode = minHeap.get(lastIndex);
        minHeap.set(1, lastNode); //swaps lastNode to the first position
        indexHashMap.replace(lastNode.item, 1); //updates the lastNode index
        minHeap.remove(lastIndex); //removes the bottom position lastNode
        indexHashMap.remove(smallest); //removes the smallest Node index
        this.size -= 1;
        sinkDown(1);
        return smallest;
    }

    /* Returns the number of items in the PQ. */
    /* Must run in 0(log(n)) avg time */
    public int size() {
        return this.size; //CHANGE
    }

    /* Changes the priority of the given item. Throws NoSuchElementException if the item
     * doesn't exist. */
    /* Must run in 0(log(n)) avg time */
    public void changePriority(T item, double priority) {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        int itemIndex = indexHashMap.get(item);
        double origPriority = minHeap.get(itemIndex).priority;
        if (priority == origPriority) {
            return;
        } else {
            PriorityNode itemNode = minHeap.get(itemIndex);
            int lastIndex = this.size;
            PriorityNode lastNode = minHeap.get(lastIndex);
            swapMinHeapNodes(itemNode, itemIndex, lastNode, lastIndex);
            swapHashMapIndices(item, lastNode.item);
            sinkDown(itemIndex);
            minHeap.remove(lastIndex); //removes the original itemNode from the minHeap ArrayList
            this.size -= 1;
            indexHashMap.remove(item); //removes the item-index mapping from the indexHashMap
            this.add(item, priority); //adds the item with new priority
            return;
        }
    }
}
