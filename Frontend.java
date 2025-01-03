// == CS400 Fall 2024 File Header Information ==
// Name: Atif Chowdhury
// Email: achowdhury22@wisc.edu
// Group: P2.3925
// Lecturer: Florian Heimerl
// Notes to Grader: N/A

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * This class implements the FrontendInterface to provide HTML-based prompts and responses for
 * shortest path computations and reachable location queries. It interacts with a backend to obtain
 * data about paths and destinations and formats the data into HTML for display.
 * 
 * The main functionality includes generating HTML prompts for user inputs and displaying results
 * such as the shortest path between locations and reachable destinations within a specified time.
 */
public class Frontend implements FrontendInterface {

  private BackendInterface backend; // stores the backend

  /**
   * Implementing classes should support the constructor below.
   * 
   * @param backend is used for shortest path computations
   */
  public Frontend(BackendInterface backend) {
    this.backend = backend;
  }

  /**
   * Returns an HTML fragment that can be embedded within the body of a larger html page. This HTML
   * output should include: - a text input field with the id="start", for the start location - a
   * text input field with the id="end", for the destination - a button labelled "Find Shortest
   * Path" to request this computation Ensure that these text fields are clearly labelled, so that
   * the user can understand how to use them.
   * 
   * @return an HTML string that contains input controls that the user can make use of to request a
   *         shortest path computation
   */
  @Override
  public String generateShortestPathPromptHTML() {
    return "<label for=\"start\">Start Location: </label>\n"
        + "<input type=\"text\" id=\"start\" placeholder=\"Enter location here...\" />\n"
        + "<label for=\"end\"> Destination: </label>\n"
        + "<input type=\"text\" id=\"end\" placeholder=\"Enter location here...\" />\n"
        + "<input type=\"button\"value=\"Find Shortest Path\" />\n" + "<br/><br/>";
  }

  /**
   * Returns an HTML fragment that can be embedded within the body of a larger html page. This HTML
   * output should include: - a paragraph (p) that describes the path's start and end locations - an
   * ordered list (ol) of locations along that shortest path - a paragraph (p) that includes the
   * total travel time along this path Or if there is no such path, the HTML returned should instead
   * indicate the kind of problem encountered.
   * 
   * @param start is the starting location to find a shortest path from
   * @param end   is the destination that this shortest path should end at
   * @return an HTML string that describes the shortest path between these two locations
   */
  @Override
  public String generateShortestPathResponseHTML(String start, String end) {
    // extracting path from start to end
    List<String> path = backend.findLocationsOnShortestPath(start, end);

    // check if there is a path
    if (path.isEmpty()) {
      return "<p>No shortest path found from " + start + " to " + end + ".</p>";
    }
    // add HTML response
    StringBuilder htmlResponse = new StringBuilder();
    htmlResponse.append("<p>Shortest path from " + start + " to " + end + ":</p>\n");
    htmlResponse.append("<ol>\n");
    htmlResponse.append(generateListHTML(path));
    htmlResponse.append("</ol>\n");

    // Calculate the total travel time for the shortest path
    List<Double> timeList = backend.findTimesOnShortestPath(start, end);
    double totalTime = 0.0;
    for (double time : timeList) {
      totalTime += time;
    }
    htmlResponse.append("<p>Total travel time: " + totalTime + " seconds.</p>");

    return htmlResponse.toString();
  }

  /**
   * Returns an HTML fragment that can be embedded within the body of a larger html page. This HTML
   * output should include: - a text input field with the id="from", for the start locations - a
   * text input field with the id="time", for the max time limit - a button labelled "Reachable From
   * Within" to submit this request Ensure that these text fields are clearly labelled, so that the
   * user can understand how to use them.
   * 
   * @return an HTML string that contains input controls that the user can make use of to request a
   *         ten closest destinations calculation
   */
  @Override
  public String generateReachableFromWithinPromptHTML() {

    return "<label for=\"from\">Start Location: </label>\n"
        + "<input type=\"text\" id=\"from\" placeholder=\"Enter location here...\" />\n"
        + "<label for=\"time\"> Maximum Travel Time (seconds): </label>\n"
        + "<input type=\"text\" id=\"time\" placeholder=\"Enter time here...\" />\n"
        + "<input type=\"button\" value=\"Reachable From Within\" />\n" + "<br/><br/>";
  }

  /**
   * Returns an HTML fragment that can be embedded within the body of a larger html page. This HTML
   * output should include: - a paragraph (p) describing the start location and travel time allowed
   * - an unordered list (ul) of destinations that can be reached within that allowed travel time Or
   * if no such destinations can be found, the HTML returned should instead indicate the kind of
   * problem encountered.
   * 
   * @param start      is the starting location to search from
   * @param travelTime is the maximum number of seconds away from the start that will allow a
   *                   destination to be reported
   * @return an HTML string that describes the closest destinations from the specified start
   *         location.
   */
  @Override
  public String generateReachableFromWithinResponseHTML(String start, double travelTime) {
    List<String> destinations = null;
    try {
      // extracting all destination from start within allowed travel time
      destinations = backend.getReachableFromWithin(start, travelTime);

    } catch (NoSuchElementException e) {
      return "<p>Provided Start Location: " + start + " was not found.</p>";
    }

    // check if there is any destination
    if (destinations.isEmpty()) {
      return "<p>No destinations found from " + start + " within " + travelTime + " seconds.</p>";
    }
    // add HTML response
    StringBuilder htmlResponse = new StringBuilder();
    htmlResponse.append(
        "<p>Destinations reachable from " + start + " within " + travelTime + " seconds:</p>\n");
    htmlResponse.append("<ul>\n");
    htmlResponse.append(generateListHTML(destinations));
    htmlResponse.append("</ul>\n");

    return htmlResponse.toString();
  }

  /**
   * Private helper method that generates a list of locations to list out.
   * 
   * @param list contains the list of the location
   * @return a HTML string with the list of the locations
   */
  private String generateListHTML(List<String> list) {
    StringBuilder htmlResponse = new StringBuilder();
    // add HTML response
    for (String locations : list) {
      htmlResponse.append("<li>" + locations + "</li>\n");
    }

    return htmlResponse.toString();
  }

  // TODO
  public static void main(String[] args) {
    Backend backend = new Backend(new DijkstraGraph<String, Double>());

    try {
      backend.loadGraphData("src/campus.dot");
    } catch (IOException e) {
      System.out.println(404);
    }

    Frontend frontend = new Frontend(backend);



    System.out.println(frontend.generateShortestPathResponseHTML("Union South",
        "Memorial Union"));
    System.out.println(frontend.generateReachableFromWithinResponseHTML("Unsion South", 20));

  }
}
