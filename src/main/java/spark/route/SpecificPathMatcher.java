package spark.route;

import java.util.List;

public class SpecificPathMatcher {
    private final RouteEntry routeEntry;

    public SpecificPathMatcher(RouteEntry routeEntry) {
        this.routeEntry = routeEntry;
    }

    boolean matchesSpecificPaths(List<String> thisPathList, List<String> pathList, int thisPathSize) {
        for (int i = 0; i < thisPathSize; i++) {
            String thisPathPart = thisPathList.get(i);
            String pathPart = pathList.get(i);

            if ((i == thisPathSize - 1)) {
                if (thisPathPart.equals("*") && routeEntry.getPath().endsWith("*")) {
                    // wildcard match
                    return true;
                }
            }

            if (!thisPathPart.startsWith(":") && !thisPathPart.equals(pathPart)) {
                return false;
            }
        }
        // All parts matched
        return true;
    }
}