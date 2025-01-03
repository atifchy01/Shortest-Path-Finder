import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


/**
 * The Backend class is responsible for managing and interacting with the graph data structure for a
 * navigation system. It provides methods to load graph data from a file, retrieve locations,
 * calculate shortest paths, and find reachable locations within a given travel time.
 *
 * This class interacts with a generic GraphADT interface and assumes the graph nodes are of type
 * String and edge weights are of type Double.
 */
public class Backend implements BackendInterface {
  private GraphADT<String, Double> graph;

  /**
   * Constructs a Backend instance with the specified graph.
   *
   * @param graph an instance of GraphADT to be managed by this Backend
   */
  public Backend(GraphADT<String, Double> graph) {
    this.graph = graph;
  }

  /**
   * Loads graph data from a .dot file. Clears existing data in the graph before parsing the file
   * and adding nodes and edges.
   *
   * @param filename the name of the .dot file to load
   * @throws IOException if there is an error reading the file
   */
  @Override
  public void loadGraphData(String filename) throws IOException {
    // Clear existing graph nodes and edges before loading new data.
    List<String> nodes = new ArrayList<>(graph.getAllNodes());
    for (String node : nodes) {
      graph.removeNode(node);
    }

    try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
      String line;
      while ((line = br.readLine()) != null) {
        line = line.trim();

        // Check if this line defines an edge
        if (line.contains("->") && line.contains("[seconds=")) {
          // Extract source, target, and weight
          int sourceStart = line.indexOf("\"") + 1;
          int sourceEnd = line.indexOf("\"", sourceStart);
          String source = line.substring(sourceStart, sourceEnd);

          int targetStart = line.indexOf("\"", sourceEnd + 1) + 1;
          int targetEnd = line.indexOf("\"", targetStart);
          String target = line.substring(targetStart, targetEnd);

          int weightStart = line.indexOf("seconds=") + 8;
          int weightEnd = line.indexOf("]", weightStart);
          Double weight = Double.parseDouble(line.substring(weightStart, weightEnd).trim());

          // Insert nodes and edge
          if (!graph.containsNode(source)) {
            graph.insertNode(source);
          }
          if (!graph.containsNode(target)) {
            graph.insertNode(target);
          }
          graph.insertEdge(source, target, weight);

        } else if (line.startsWith("\"") && line.endsWith("\";")) {
          // This is a standalone node
          String node = line.substring(1, line.length() - 2).trim();
          if (!graph.containsNode(node)) {
            graph.insertNode(node);
          }
        }
      }
    } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
      throw new IOException("Error parsing .dot file: Invalid format or data.", e);
    }
  }

  /**
   * Retrieves a list of all the locations (nodes) currently in the graph.
   *
   * @return a List of Strings representing all nodes in the graph
   */
  @Override
  public List<String> getListOfAllLocations() {
    return graph.getAllNodes();
  }

  /**
   * Finds the shortest path between two locations in the graph. If no path exists, an empty list is
   * returned.
   *
   * @param startLocation the starting location (node) in the graph
   * @param endLocation   the destination location (node) in the graph
   * @return a List of Strings representing the nodes on the shortest path from startLocation to
   *         endLocation, or an empty list if no path exists
   */
  @Override
  public List<String> findLocationsOnShortestPath(String startLocation, String endLocation) {
    try {
      List<String> path = graph.shortestPathData(startLocation, endLocation);
      if (path.isEmpty()) {
        throw new NoSuchElementException("No path found between the locations.");
      }
      return path;
    } catch (NoSuchElementException e) {
      return new ArrayList<>(); // No path? Return an empty list.
    }
  }

  /**
   * Retrieves the edge weights (times) for each step along the shortest path between two locations.
   * If any edge is missing, an empty list is returned.
   *
   * @param startLocation the starting location (node) in the graph
   * @param endLocation   the destination location (node) in the graph
   * @return a List of Doubles representing the edge weights on the shortest path, or an empty list
   *         if any edge is missing or if no path exists
   */
  @Override
  public List<Double> findTimesOnShortestPath(String startLocation, String endLocation) {
    List<Double> times = new ArrayList<>();
    List<String> path = findLocationsOnShortestPath(startLocation, endLocation);

    // We go through each step on the path, fetching the time for each edge.
    for (int i = 0; i < path.size() - 1; i++) {
      try {
        Double time = graph.getEdge(path.get(i), path.get(i + 1));
        times.add(time);
      } catch (NoSuchElementException e) {
        return new ArrayList<>(); // Missing an edge? We’ll just return an empty list.
      }
    }
    return times;
  }

  /**
   * Finds all locations reachable from a starting location within a specified travel time. Throws
   * an exception if the starting location does not exist.
   *
   * @param startLocation the starting location (node) in the graph
   * @param travelTime    the maximum travel time allowed
   * @return a List of Strings representing locations reachable within the specified travel time
   * @throws NoSuchElementException if the startLocation does not exist in the graph
   */
  @Override
  public List<String> getReachableFromWithin(String startLocation, double travelTime)
      throws NoSuchElementException {
    if (!graph.containsNode(startLocation)) {
      throw new NoSuchElementException("Start location does not exist"); // Can't start from
                                                                         // nowhere!
    }

    List<String> reachableLocations = new ArrayList<>();
    List<String> allNodes = graph.getAllNodes();

    // For each node, we check if we can reach it within the allowed travel time
    for (String node : allNodes) {
      try {
        if (!node.equals(startLocation)
            && graph.shortestPathCost(startLocation, node) <= travelTime) {
          reachableLocations.add(node);
        }
      } catch (NoSuchElementException e) {
      }
    }
    return reachableLocations;
  }
}
