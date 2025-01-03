// --== CS400 Fall 2024 File Header Information ==--
// Name: Atif Chowdhury
// Email: achowdhury22@wisc.edu
// Group and Team: P2.3925
// Lecturer: Florian Heimerl
// Notes to Grader: N/A

import java.util.PriorityQueue;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This class extends the BaseGraph data structure with additional methods for computing the total
 * cost and list of node data along the shortest path connecting a provided starting to ending
 * nodes. This class makes use of Dijkstra's shortest path algorithm.
 */
public class DijkstraGraph<NodeType, EdgeType extends Number> extends BaseGraph<NodeType, EdgeType>
    implements GraphADT<NodeType, EdgeType> {

  /**
   * While searching for the shortest path between two nodes, a SearchNode contains data about one
   * specific path between the start node and another node in the graph. The final node in this path
   * is stored in its node field. The total cost of this path is stored in its cost field. And the
   * predecessor SearchNode within this path is referened by the predecessor field (this field is
   * null within the SearchNode containing the starting node in its node field).
   *
   * SearchNodes are Comparable and are sorted by cost so that the lowest cost SearchNode has the
   * highest priority within a java.util.PriorityQueue.
   */
  protected class SearchNode implements Comparable<SearchNode> {
    public Node node;
    public double cost;
    public SearchNode predecessor;

    public SearchNode(Node node, double cost, SearchNode predecessor) {
      this.node = node;
      this.cost = cost;
      this.predecessor = predecessor;
    }

    public int compareTo(SearchNode other) {
      if (cost > other.cost)
        return +1;
      if (cost < other.cost)
        return -1;
      return 0;
    }
  }

  /**
   * Constructor that sets the map that the graph uses.
   */
  public DijkstraGraph() {
    super(new HashtableMap<>());
  }

  /**
   * This helper method creates a network of SearchNodes while computing the shortest path between
   * the provided start and end locations. The SearchNode that is returned by this method is
   * represents the end of the shortest path that is found: it's cost is the cost of that shortest
   * path, and the nodes linked together through predecessor references represent all of the nodes
   * along that shortest path (ordered from end to start).
   *
   * @param start the data item in the starting node for the path
   * @param end   the data item in the destination node for the path
   * @return SearchNode for the final end node within the shortest path
   * @throws NoSuchElementException when no path from start to end is found or when either start or
   *                                end data do not correspond to a graph node
   */
  protected SearchNode computeShortestPath(NodeType start, NodeType end) {
    // check if the START and END node exist in the graph
    if (!containsNode(start) || !containsNode(end)) {
      throw new NoSuchElementException("Either START or END data does not exist in the graph.");
    }

    // getting the Nodes from the graph
    Node startNode = this.nodes.get(start);
    Node endNode = this.nodes.get(end);

    // initializing a priority queue to hold all the nodes with their edges
    PriorityQueue<SearchNode> queue = new PriorityQueue<>();
    // initializing a map placeholder to hold the visited nodes
    HashtableMap<Node, SearchNode> visited = new HashtableMap<>();

    // adding the first node to the priority queue
    queue.add(new SearchNode(startNode, 0.0, null));

    // iterating through all of the elements of the priority queue
    while (!queue.isEmpty()) {
      // retrieving the minimum cost path
      SearchNode currentNode = queue.poll();

      // return the current node if we reached the end
      if (currentNode.node.equals(endNode)) {
        return currentNode;
      }

      // check current node for unvisited
      if (!visited.containsKey(currentNode.node)) {
        // add the current node if it is not visited
        visited.put(currentNode.node, currentNode);

        // insert all the nodes from the current node to the priority queue
        for (Edge nodeEdge : currentNode.node.edgesLeaving) {
          Node neighbor = nodeEdge.successor;
          double newCost = currentNode.cost + nodeEdge.data.doubleValue();

          // adding the edges to the priority queue in (curr, cost, pre) format
          queue.add(new SearchNode(neighbor, newCost, currentNode));
        }
      }
    }
    throw new NoSuchElementException("No path from START to END was found.");
  }

  /**
   * Returns the list of data values from nodes along the shortest path from the node with the
   * provided start value through the node with the provided end value. This list of data values
   * starts with the start value, ends with the end value, and contains intermediary values in the
   * order they are encountered while traversing this shorteset path. This method uses Dijkstra's
   * shortest path algorithm to find this solution.
   *
   * @param start the data item in the starting node for the path
   * @param end   the data item in the destination node for the path
   * @return list of data item from node along this shortest path
   */
  public List<NodeType> shortestPathData(NodeType start, NodeType end) {
    // call to helper method to compute the shortest path
    SearchNode endNode = computeShortestPath(start, end);
    // initializing a list to store all the path
    LinkedList<NodeType> path = new LinkedList<>();

    // iterating from end to the start point and adding all the points of the path
    while (endNode != null) {
      path.addFirst(endNode.node.data);
      endNode = endNode.predecessor;
    }

    return path;
  }

  /**
   * Returns the cost of the path (sum over edge weights) of the shortest path freom the node
   * containing the start data to the node containing the end data. This method uses Dijkstra's
   * shortest path algorithm to find this solution.
   *
   * @param start the data item in the starting node for the path
   * @param end   the data item in the destination node for the path
   * @return the cost of the shortest path between these nodes
   */
  public double shortestPathCost(NodeType start, NodeType end) {
      return computeShortestPath(start, end).cost;
  }

  /**
   * shortestPathTest01: Tests the shortest path and cost between two VALID nodes based on a
   * hand-traced example. This test uses the graph and confirms that the shortest path and cost
   * match the results from a hand-traced computation.
   */
  @Test
  public void shortestPathTest01() {
    boolean exceptionThrown = false; // exception checker

    // creating graph for testing
    DijkstraGraph<String, Integer> graphTester = new DijkstraGraph<String, Integer>();

    // inserting nodes into the graph
    graphTester.insertNode("A");
    graphTester.insertNode("B");
    graphTester.insertNode("C");
    graphTester.insertNode("D");
    graphTester.insertNode("E");
    graphTester.insertNode("F");
    graphTester.insertNode("G");
    graphTester.insertNode("H");

    // inserting edges to the nodes
    graphTester.insertEdge("A", "B", 4);
    graphTester.insertEdge("A", "C", 2);
    graphTester.insertEdge("A", "E", 15);
    graphTester.insertEdge("B", "E", 10);
    graphTester.insertEdge("B", "D", 1);
    graphTester.insertEdge("C", "D", 5);
    graphTester.insertEdge("D", "E", 3);
    graphTester.insertEdge("D", "F", 0);
    graphTester.insertEdge("F", "H", 4);
    graphTester.insertEdge("F", "D", 2);
    graphTester.insertEdge("G", "H", 4);

    // Test 01: Calling the shortestPathData() method on valid nodes should result in the expected
    // path and cost with no exception thrown

    // expected shortes path from A to E (hand-traced in class)
    List<String> expectedPath = Arrays.asList("A", "B", "D", "E");
    double expectedCost = 8.0;

    // check for expected path
    try {
      List<String> actualPath = graphTester.shortestPathData("A", "E");
      Assertions.assertEquals(expectedPath, actualPath,
          "shortestPathTest01() [1.1]: FAILED did not generate expected PATH for VALID input");

      // check for unexpected exception
    } catch (Exception e) {
      exceptionThrown = true; // unexpected exception
    }
    Assertions.assertFalse(exceptionThrown,
        "shortestPathTest01() [1.2]: FAILED shortestPathData() throws unexpected EXCEPTION for VALID input..");

    // check for expected cost
    try {
      double actualCost = graphTester.shortestPathCost("A", "E");
      Assertions.assertEquals(expectedCost, actualCost,
          "shortestPathTest01() [1.3]: FAILED did not return expected COST for VALID input.");

      // check for unexpected exception
    } catch (Exception e) {
      exceptionThrown = true; // unexpected exception
    }
    Assertions.assertFalse(exceptionThrown,
        "shortestPathTest01() [1.4]: FAILED shortestPathCost() throws unexpected EXCEPTION for VALID input.");
  }


  /**
   * shortestPathTest02:Tests the shortest path and cost between two different VALID nodes based on
   * a hand-traced example. This test uses the graph and confirms that the shortest path and cost
   * match the results from a hand-traced computation.This test checks the path and cost from node A
   * to node F.
   */
  @Test
  public void shortestPathTest02() {
    boolean exceptionThrown = false; // exception checker

    // creating graph for testing
    DijkstraGraph<String, Integer> graphTester = new DijkstraGraph<String, Integer>();

    // inserting nodes into the graph
    graphTester.insertNode("A");
    graphTester.insertNode("B");
    graphTester.insertNode("C");
    graphTester.insertNode("D");
    graphTester.insertNode("E");
    graphTester.insertNode("F");
    graphTester.insertNode("G");
    graphTester.insertNode("H");

    // inserting edges to the nodes
    graphTester.insertEdge("A", "B", 4);
    graphTester.insertEdge("A", "C", 2);
    graphTester.insertEdge("A", "E", 15);
    graphTester.insertEdge("B", "E", 10);
    graphTester.insertEdge("B", "D", 1);
    graphTester.insertEdge("C", "D", 5);
    graphTester.insertEdge("D", "E", 3);
    graphTester.insertEdge("D", "F", 0);
    graphTester.insertEdge("F", "H", 4);
    graphTester.insertEdge("F", "D", 2);
    graphTester.insertEdge("G", "H", 4);

    // Test 01: Calling the shortestPathData() method on valid nodes should result in the expected
    // path and cost with no exception thrown

    // expected shortes path from A to E (hand-traced in class)
    List<String> expectedPath = Arrays.asList("A", "B", "D", "F");
    double expectedCost = 5.0;

    // check for expected path
    try {
      List<String> actualPath = graphTester.shortestPathData("A", "F");
      Assertions.assertEquals(expectedPath, actualPath,
          "shortestPathTest02() [1.1]: FAILED did not generate expected PATH for VALID input");

      // check for unexpected exception
    } catch (Exception e) {
      exceptionThrown = true; // unexpected exception
    }
    Assertions.assertFalse(exceptionThrown,
        "shortestPathTest02() [1.2]: FAILED shortestPathData() throws unexpected EXCEPTION for VALID input..");

    // check for expected cost
    try {
      double actualCost = graphTester.shortestPathCost("A", "F");
      Assertions.assertEquals(expectedCost, actualCost,
          "shortestPathTest02() [1.3]: FAILED did not return expected COST for VALID input.");

      // check for unexpected exception
    } catch (Exception e) {
      exceptionThrown = true; // unexpected exception
    }
    Assertions.assertFalse(exceptionThrown,
        "shortestPathTest02() [1.4]: FAILED shortestPathCost() throws unexpected EXCEPTION for VALID input.");
  }

  /**
   * shortestPathTest03: This test checks the behavior when the two nodes are INVALID and when there
   * is no sequence of directed edges connecting the start and end nodes.
   */
  @Test
  public void shortestPathTest03() {
    boolean exceptionThrown = false; // exception checker

    // creating graph for testing
    DijkstraGraph<String, Integer> graphTester = new DijkstraGraph<String, Integer>();

    // inserting nodes into the graph
    graphTester.insertNode("A");
    graphTester.insertNode("B");
    graphTester.insertNode("C");
    graphTester.insertNode("D");
    graphTester.insertNode("E");
    graphTester.insertNode("F");
    graphTester.insertNode("G");
    graphTester.insertNode("H");

    // inserting edges to the nodes
    graphTester.insertEdge("A", "B", 4);
    graphTester.insertEdge("A", "C", 2);
    graphTester.insertEdge("A", "E", 15);
    graphTester.insertEdge("B", "E", 10);
    graphTester.insertEdge("B", "D", 1);
    graphTester.insertEdge("C", "D", 5);
    graphTester.insertEdge("D", "E", 3);
    graphTester.insertEdge("D", "F", 0);
    graphTester.insertEdge("F", "H", 4);
    graphTester.insertEdge("F", "D", 2);
    graphTester.insertEdge("G", "H", 4);

    // Test 01: calling shortestPathData() & shortestPathCost() on inputs that do not exist in the
    // graph should throw a NoSuchElementException

    // calling shortestPathData() with INVALID input
    try {
      graphTester.shortestPathData("I", "Z");
    } catch (NoSuchElementException e) {
      exceptionThrown = true; // expected exception

    } catch (Exception e) {
      exceptionThrown = false; // unexpected exception
    }
    Assertions.assertTrue(exceptionThrown,
        "shortestPathTest03() [1.1]: FAILED shortestPathData() did not throw EXPECTED exception for INVALID input.");

    // reinitializing the exception checker
    exceptionThrown = false;

    // calling shortestPathCost() with INVALID input
    try {
      graphTester.shortestPathCost("I", "Z");
    } catch (NoSuchElementException e) {
      exceptionThrown = true; // expected exception

    } catch (Exception e) {
      exceptionThrown = false; // unexpected exception
    }
    Assertions.assertTrue(exceptionThrown,
        "shortestPathTest03() [1.2]: FAILED shortestPathCost() did not throw EXPECTED exception for INVALID input.");

    // reinitializing the exception checker
    exceptionThrown = false;

    // Test 02: calling shortestPathData() & shortestPathCost() on inputs which do not have a
    // sequence of directed paths in between them should throw a NoSuchElementException

    // calling shortestPathData() with INVALID input
    try {
      graphTester.shortestPathData("A", "G");
    } catch (NoSuchElementException e) {
      exceptionThrown = true; // expected exception

    } catch (Exception e) {
      exceptionThrown = false; // unexpected exception
    }
    Assertions.assertTrue(exceptionThrown,
        "shortestPathTest03() [2.1]: FAILED shortestPathData() did not throw EXPECTED exception for INVALID input.");

    // reinitializing the exception checker
    exceptionThrown = false;

    // calling shortestPathCost() with INVALID input
    try {
      graphTester.shortestPathCost("A", "G");
    } catch (NoSuchElementException e) {
      exceptionThrown = true; // expected exception

    } catch (Exception e) {
      exceptionThrown = false; // unexpected exception
    }
    Assertions.assertTrue(exceptionThrown,
        "shortestPathTest03() [2.2]: FAILED shortestPathCost() did not throw EXPECTED exception for INVALID input.");

    // re-reinitializing the exception checker
    exceptionThrown = false;

    // Test 03: calling shortestPathData() & shortestPathCost() on NULL inputs should throw a
    // NullPointerException

    // calling shortestPathData() with NULL input
    try {
      graphTester.shortestPathData(null, null);
    } catch (NullPointerException e) {
      exceptionThrown = true; // expected exception

    } catch (Exception e) {
      exceptionThrown = false; // unexpected exception
    }
    Assertions.assertTrue(exceptionThrown,
        "shortestPathTest03() [3.1]: FAILED shortestPathData() did not throw EXPECTED exception for NULL input.");

    // reinitializing the exception checker
    exceptionThrown = false;

    // calling shortestPathCost() with NULL input
    try {
      graphTester.shortestPathCost(null, null);
    } catch (NullPointerException e) {
      exceptionThrown = true; // expected exception

    } catch (Exception e) {
      exceptionThrown = false; // unexpected exception
    }
    Assertions.assertTrue(exceptionThrown,
        "shortestPathTest03() [3.2]: FAILED shortestPathCost() did not throw EXPECTED exception for NULL input.");
  }
}
