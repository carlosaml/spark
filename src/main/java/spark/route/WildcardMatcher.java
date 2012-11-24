package spark.route;

import java.util.List;

public class WildCardMatcher {
    private final RouteEntry routeEntry;

    public WildCardMatcher(RouteEntry routeEntry) {
        this.routeEntry = routeEntry;
    }

    boolean matchesWildCards(String path, List<String> thisPathList, List<String> pathList, int thisPathSize, int pathSize) {
        // Number of "path parts" not the same
        // check wild card:
        if (routeEntry.getPath().endsWith("*")) {
            if (pathSize == (thisPathSize - 1) && (path.endsWith("/"))) {
                // Hack for making wildcards work with trailing slash
                pathList.add("");
                pathList.add("");
                pathSize += 2;
            }

            if (thisPathSize < pathSize) {
                for (int i = 0; i < thisPathSize; i++) {
                    String thisPathPart = thisPathList.get(i);
                    String pathPart = pathList.get(i);
                    if (thisPathPart.equals("*") && (i == thisPathSize - 1) && routeEntry.getPath().endsWith("*")) {
                        // wildcard match
                        return true;
                    }
                    if (!thisPathPart.startsWith(":") && !thisPathPart.equals(pathPart)) {
                        return false;
                    }
                }
                // All parts matched
                return true;
            }
            // End check wild card
        }
        return false;
    }
}