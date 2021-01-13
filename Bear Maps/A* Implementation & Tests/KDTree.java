package bearmaps;
import java.util.List;


/** @source cs61b project2 walkthrough videos by Josh Hug **/
public class KDTree implements PointSet {
    private Node root;
    private static final boolean HORIZONTAL = false;
    private static final boolean VERTICAL = true;

    private class Node {
        private Point p;
        private boolean orientation;
        private Node leftChild; //also refers to downChild
        private Node rightChild; //also refers to upChild

        private Node(Point point, boolean orientation) {
            p = point;
            this.orientation = orientation;
        }
    }

    public KDTree(List<Point> points) {
        root = null;
        for (Point p : points) {
            root = add(p, root, HORIZONTAL);
        }
    }

    private Node add(Point p, Node n, boolean orientation) {
        if (n == null) {
            return new Node(p, orientation);
        }

        if (p.equals(n.p)) {
            return n;
        }
        int compared = comparePoints(p, n.p, orientation);
        if (compared < 0) {
            n.leftChild = add(p, n.leftChild, !orientation);
        } else if (compared >= 0) {
            n.rightChild = add(p, n.rightChild, !orientation);
        }
        return n;
    }

    private int comparePoints(Point a, Point b, boolean orientation) {
        if (orientation == HORIZONTAL) {
            return Double.compare(a.getX(), b.getX());
        } else {
            return Double.compare(a.getY(), b.getY());
        }
    }

    @Override
    public Point nearest(double x, double y) {
        return nearest(this.root, new Point(x, y), null).p;
    }

    private Node nearest(Node n, Point goal, Node best) {
        Node goodSide = null;
        Node badSide = null;
        if (n == null) {
            return best;
        } else if (best == null) {
            best = n;
        }
        if (n.p.distance(n.p, goal) < best.p.distance(best.p, goal)) {
            best = n;
        }
        if (comparePoints(goal, n.p, n.orientation) < 0) {
            goodSide = n.leftChild;
            badSide = n.rightChild;
        } else {
            goodSide = n.rightChild;
            badSide = n.leftChild;
        }
        best = nearest(goodSide, goal, best);

        if (badSide != null) {
            Point bestBadPoint = null;
            if (badSide.orientation == VERTICAL) {
                bestBadPoint = new Point(n.p.getX(), goal.getY());
            } else {
                bestBadPoint = new Point(goal.getX(), n.p.getY());
            }
            if (bestBadPoint.distance(bestBadPoint, goal) < best.p.distance(best.p, goal)) {
                best = nearest(badSide, goal, best);
            }
        }
        return best;
    }

    public static void main(String[] args) {
        Point A = new Point(2, 3); // constructs a Point with x = 1.1, y = 2.2
        Point Z = new Point(4, 2);
        Point B = new Point(4, 2);
        Point C = new Point(4, 5);
        Point D = new Point(3, 3);
        Point E = new Point(1, 5);
        Point F = new Point(4, 4);


        KDTree kd = new KDTree(List.of(A, Z, B, C, D, E, F));
    }
}
